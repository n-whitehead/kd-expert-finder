package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.*;
import com.elsevier.kd.graph.service.ExpertFinderService;
import com.elsevier.kd.graph.service.IndexMetricService;
import com.elsevier.kd.graph.service.NormalizationService;
import com.elsevier.kd.graph.service.qualifiers.KnowledgeDiscovery;
import com.elsevier.kd.graph.service.qualifiers.OrdinalNormalization;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;

@ApplicationScoped
public class ExpertFinderServiceImpl implements ExpertFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertFinderServiceImpl.class);

    @Inject
    SessionConfig sessionConfig;

    // quarkus-neo4j artifact creates a Driver bean, so rather than using a different library we qualify ours.
    @Inject
    @KnowledgeDiscovery
    Driver driver;

    @Inject
    @OrdinalNormalization
    NormalizationService normalizationService;

    @Inject
    IndexMetricService indexMetricService;

    @PostConstruct
    public void init() {
    }

    @Override
    public JsonObject findExperts(Set<String> iris, int k, double decay) {
        LOGGER.info("Filtering Concepts: {}", iris);
        Set<String> filteredConcepts = new HashSet<>();
        if (iris.size() == 1) {
            filteredConcepts.addAll(iris);
        } else {
            // Only look for strictly the narrowest concepts in the set of iris.
            for (String iri : iris) {
                try (Session session = driver.session(sessionConfig)) {
                    Result result = session.run(
                            "UNWIND $iris AS other MATCH p=(c:Concept {uri: $iri})-[:narrower*1..]->(o:Concept {uri: other}) RETURN count(nodes(p)) AS count",
                            Values.parameters("iris", iris, "iri", iri));
                    if (result.next().get("count").asInt() == 0) {
                        filteredConcepts.add(iri);
                    }
                }
            }
            LOGGER.info("Filtered Concepts: {}", filteredConcepts);
        }
        // For set of concepts, find the normalized h-index of every author in that subject.
        Set<Concept> concepts = new HashSet<>();
        for (String conceptIri : filteredConcepts) {
            Concept concept = new Concept(conceptIri);
            // Get prefLabels for the concepts
            List<String> prefLabels = new ArrayList<>();
            try (Session session = driver.session(sessionConfig)) {
                Result result = session.run(
                        "MATCH (c:Concept { uri: $iri })-[:prefLabel]->(lbl) RETURN lbl.literalForm AS literalForm",
                        Values.parameters("iri", conceptIri)
                );
                result.stream().forEach(record -> {
                    concept.addLabels(record.get("literalForm").asList(Value::asString));
                });
            }
            LOGGER.info("Extracting Citation Details in: {}", concept.getPrefLabels());
            // We only need authors of works belonging to a concept with NON-ZERO references. Zero reference works have no h-index. and therefore shouldn't be recommended.
            try (Session session = driver.session(sessionConfig)) {
                // TODO: Use Identifier nodes instead of ID fields.
                Result result = session.run(
                        "MATCH (p:Person)-[o:authorOf]->(w:Work)-[:hasSubject]->(c:Concept { uri: $iri })<-[:hasSubject]-(ref:Work)-[:references]->(w) WHERE id(w)<>id(ref) RETURN p.ID as author, w AS work, o.ordinal AS ordinal, size((w)<-[:authorOf]-()) AS count, collect(DISTINCT ref) AS references",
                        Values.parameters("iri", conceptIri));
                result.stream().forEach(record -> {
                    String authorId = record.get("author").asString();

                    Work work = new Work(record.get("work").get("ID").asString(), record.get("work").get("PublishedDate").asString());
                    List<Work> references = record.get("references").asList(value -> new Work(value.get("ID").asString(), value.get("PublishedDate").asString()));
                    work.setReferences(references);

                    Score score = new Score();
                    score.setWork(work);
                    score.setAuthorsCount(record.get("count").asInt());
                    Value ordinalValue = record.get("ordinal");
                    if (!ordinalValue.isNull()) {
                        score.setOrdinal(ordinalValue.asInt());
                    }
                    score.setDecay(decay);
                    score.calculateCitationCount();
                    normalizationService.normalize(score);

                    concept.addScore(authorId, score);
                });
            }
            concepts.add(concept);
        }
        List<AuthorScore> authorScores = new ArrayList<>();
        for (Concept concept : concepts) {
            LOGGER.info("Calculating index scores for: {}", concept.getPrefLabels());
            // For each value in the map, calculate h-index
            List<AuthorScore> conceptAuthorScores = new ArrayList<>();
            for (String authorId : concept.getAuthorScores().keySet()) {
                List<Score> values = concept.getAuthorScores().get(authorId);
                int hindex = indexMetricService.calculate(values);
                AuthorScore authorScore = new AuthorScore(authorId, hindex);
                Set<ConceptDetail> conceptSet = new HashSet<>();
                conceptSet.add(new ConceptDetail(concept, hindex));
                authorScore.addConcepts(conceptSet);
                conceptAuthorScores.add(authorScore);
            }
            // Calculate average h-index of the concept
            double averageHindex = conceptAuthorScores.stream().mapToDouble(AuthorScore::getHindex).average().orElse(1.0);
            // Divide each concept score by the average hindex of the concept
            conceptAuthorScores.forEach(authorScore -> {
                double currentIndex = authorScore.getHindex();
                double normal = currentIndex / averageHindex;
                authorScore.setHindex(normal);
                authorScore.getConcepts().forEach(conceptDetail -> conceptDetail.setHindexContribution(normal));
            });
            authorScores.addAll(conceptAuthorScores);
        }
        // Merge author scores by first grouping on ID, then merging old and new objects into a new object.
        Map<String, AuthorScore> scoresMap = new HashMap<>();
        authorScores.forEach(authorScore -> {
            scoresMap.merge(
                    authorScore.getId(),
                    new AuthorScore(authorScore.getId(), authorScore.getHindex(), authorScore.getConcepts()),
                    (o, n) -> {
                        o.setHindex(o.getHindex() + n.getHindex());
                        o.addConcepts(n.getConcepts());
                        return o;
                    });
        });
        // Reverse sort author scores by h-index
        List<AuthorScore> sortedScores = new ArrayList<>(scoresMap.values());
        sortedScores.sort(Comparator.comparing(AuthorScore::getHindex).reversed());
        // Add top-K into a JSON array
        int count = 0;
        JsonArrayBuilder resultArrayBuilder = Json.createArrayBuilder();
        while ((count < k) && (count < sortedScores.size())) {
            AuthorScore score = sortedScores.get(count);
            JsonArrayBuilder conceptBuilder = Json.createArrayBuilder();
            for (ConceptDetail concept : score.getConcepts()) {
                JsonObjectBuilder conceptObj = Json.createObjectBuilder();
                conceptObj.add("contribution", concept.getHindexContribution());
                conceptObj.add("concept", concept.getConcept().getIri());
                JsonArrayBuilder labelBuilder = Json.createArrayBuilder();
                for (String prefLabel : concept.getConcept().getPrefLabels()) {
                    labelBuilder.add(prefLabel);
                }
                conceptObj.add("prefLabels", labelBuilder.build());
                conceptBuilder.add(conceptObj);
            }
            JsonObject scoreObject = Json.createObjectBuilder()
                    .add("id", score.getId())
                    .add("score", score.getHindex())
                    .add("concepts", conceptBuilder.build()).build();
            resultArrayBuilder.add(scoreObject);
            count++;
        }
        JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
        // Get unique set of concepts
        JsonArrayBuilder uniqueConceptsBuilder = Json.createArrayBuilder();
        for (Concept concept : concepts) {
            JsonObjectBuilder usedConceptObjBuilder = Json.createObjectBuilder();
            usedConceptObjBuilder.add("iri", concept.getIri());
            JsonArrayBuilder prefLabelsArrayBuilder = Json.createArrayBuilder();
            for (String prefLabel : concept.getPrefLabels()) {
                prefLabelsArrayBuilder.add(prefLabel);
            }
            usedConceptObjBuilder.add("prefLabels", prefLabelsArrayBuilder.build());
            uniqueConceptsBuilder.add(usedConceptObjBuilder.build());
        }
        resultBuilder.add("returned", count).add("usedConcepts", uniqueConceptsBuilder.build()).add("results", resultArrayBuilder.build());
        return resultBuilder.build();
    }
}
