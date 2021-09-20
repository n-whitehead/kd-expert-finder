package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.Concept;
import com.elsevier.kd.graph.model.Work;
import com.elsevier.kd.graph.service.ExpertFinderService;
import com.elsevier.kd.graph.service.IndexMetricService;
import com.elsevier.kd.graph.service.NormalizationService;
import com.elsevier.kd.graph.service.qualifiers.KnowledgeDiscovery;
import org.neo4j.driver.*;
import org.neo4j.driver.types.MapAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpertFinderServiceImpl implements ExpertFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertFinderServiceImpl.class);

    // Need to qualify injected driver because CDI thinks there is another available driver bean (default)
    @Inject
    @KnowledgeDiscovery
    Driver driver;

    @Inject
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
                try (Session session = driver.session()) {
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
        List<Concept> concepts = new ArrayList<>();
        for (String conceptIri : filteredConcepts) {
            Concept concept = new Concept();
            concept.setIri(conceptIri);
            // Get prefLabels for the concepts
            List<String> prefLabels = new ArrayList<>();
            try (Session session = driver.session()) {
                Result result = session.run(
                        "MATCH (c:Concept { uri: $iri })-[:prefLabel]->(label) RETURN label.literalForm AS prefLabel",
                        Values.parameters("iri", conceptIri)
                );
                prefLabels = result.next().get("prefLabel").asList(Value::asString);
            }
            concept.setPrefLabels(prefLabels);
            LOGGER.info("Extracting Citation Details in: {}", concept.getPrefLabels());

            try (Session session = driver.session()) {
                Result result = session.run(
                        "MATCH (c:Concept { uri: $iri })<-[:hasSubject*0..]->(w:Work) RETURN w AS work",
                        Values.parameters("iri", conceptIri));
                List<Work> works = new ArrayList<>();
                int test = result.list().size();
            }


            // We only need authors of works belonging to a concept with NON-ZERO references. Zero reference works have no h-index. and therefore shouldn't be recommended.
            try (Session session = driver.session()) {
                // TODO: Use Identifier nodes instead of ID fields.
                Result result = session.run(
                        "MATCH (c:Concept { uri: $iri }) MATCH (p:Person)-[o:authorOf]->(w:Work)-[:hasSubject]->(c)<-[:hasSubject]-(ref:Work)-[:references]->(w) RETURN w AS work, collect(DISTINCT ref) AS references, collect(DISTINCT {authorId: p.ID, ordinal: o.ordinal}) AS authors",
                        Values.parameters("iri", conceptIri));
                List<Work> works = new ArrayList<>();
                // TODO: Should throw an exception, or continue if result set is empty.
                result.stream().forEach(record -> {
                    Value workValue = record.get("work");
                    Work work = new Work(workValue.get("ID").asString(), workValue.get("PublishedDate").asString());
                    work.setDecay(decay);
                    Value referenceValues = record.get("references");
                    List<Work> references = referenceValues.asList(value -> new Work(value.get("ID").asString(), value.get("PublishedDate").asString()));
                    work.setReferences(references);
                    // calculate the time decay for the citationCount contribution
                    work.calculateTimeDecay();
                    Value authorsValue = record.get("authors");
                    List<Author> authors = authorsValue.asList(MapAccessor::asMap).stream().map(m -> {
                        Author author = new Author();
                        author.setId(String.valueOf(m.get("authorId")));
                        if (m.get("ordinal") != null) {
                            author.setOrdinal((Integer) m.get("ordinal"));
                        }
                        author.addWork(work);
                        return author;
                    }).collect(Collectors.toList());
                    work.setAuthors(authors);
                    works.add(work);
                    concept.setWorks(works);
                });
            }
            concepts.add(concept);
        }
        // For each concept, normalize each work using a normalization strategy, then calculate the hindex for each author in the concept.
        for (Concept concept : concepts) {
            double totalConceptHindex = 0;
            // Problems with using ordinal?
            for (Work work : concept.getWorks()) {
                // Normalize citation contribution w.r.t authors of the work.
                normalizationService.normalize(work);
                // Are there duplicate authors for sets of works?
                for (Author author : work.getAuthors()) {
                    double authorHindex = indexMetricService.calculate(author);
                    totalConceptHindex += authorHindex;
                    author.setHindex(authorHindex);
                }
            }

        }


//        for (Concept concept : concepts) {
//            List<Author> authors = concept.getAuthors();
//            for (Author author : authors) {
//                // Normalize citation count contributions w.r.t authors in a concept
//                normalizationService.normalize(author);
//                // Calculate the hIndex for each author in the concept. Add it to the conceptHIndex
//                double authorHindex = indexMetricService.calculate(author.getCitations());
//                author.setHindex(authorHindex);
//            }
//            double totalHindex = authors.stream().mapToDouble(Author::getHindex).sum();
//            double averageHindex = totalHindex / authors.size();
//            // Normalize h-index score within a concept, this makes it comparable to other concepts.
//            authors.forEach(author -> {
//                double currentHindex = author.getHindex();
//                author.setHindex(currentHindex / averageHindex);
//            });
//    }


        // TODO: Flatten scores map, merge common key scores, and sort by largest values. Take top k.
        // TODO: Write array result into a JSON Array. Might want to consider returning a JSON object with prefLabels, and other metadata besides just author rankings?
        JsonObject obj = Json.createObjectBuilder().build();
        return obj;
    }
}
