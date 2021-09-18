package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.CitationCount;
import com.elsevier.kd.graph.model.Concept;
import com.elsevier.kd.graph.service.AuthorNormalizationService;
import com.elsevier.kd.graph.service.ExpertFinderService;
import com.elsevier.kd.graph.service.IndexMetricService;
import com.elsevier.kd.graph.service.qualifiers.KnowledgeDiscovery;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import java.util.*;

@ApplicationScoped
public class ExpertFinderServiceImpl implements ExpertFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertFinderServiceImpl.class);

    // Need to qualify injected driver because CDI thinks there is another available driver bean (default)
    @Inject
    @KnowledgeDiscovery
    private Driver driver;

    @Inject
    private AuthorNormalizationService authorNormalizationService;

    @Inject
    private IndexMetricService indexMetricService;

    @PostConstruct
    public void init() {
    }

    @Override
    public JsonArray findExperts(Set<String> iris, int k, double decay) {
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
            Concept concept = new Concept(conceptIri);
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
            LOGGER.info("Extracting Author Details in: {}", concept.getPrefLabels());
            // We only need authors of works belonging to a concept with NON-ZERO references. Zero reference works have no h-index. and therefore shouldn't be recommended.
            List<Author> authors = new ArrayList<>();
            try (Session session = driver.session()) {
                // TODO: REVISIT THIS - use Identifier nodes instead of ID fields.
                Result result = session.run(
                        "MATCH (c:Concept { uri: $iri }) MATCH (p:Person)-[o:authorOf]->(w:Work)-[:hasSubject]->(c)<-[:hasSubject]-(ref:Work)-[:references]->(w) RETURN p.ID AS authorId, size((w)<-[:authorOf]-()) AS coauthors, o.ordinal AS ordinal, COLLECT(ref) AS reference",
                        Values.parameters("iri", conceptIri));
                result.stream().forEach(record -> {
                    Value authorValue = record.get("authorId");
                    Value ordinalValue = record.get("ordinal");

                    Author author = new Author();
                    if (!ordinalValue.isNull()) {
                        author.setOrdinal(ordinalValue.asInt());
                    }
                    author.setId(authorValue.asString());
                    // Get reference information, parse out ID and publishedDate into a citation count object.
                    Value referenceValues = record.get("reference");
                    Value coauthorsValue = record.get("coauthors");
                    List<CitationCount> citationCounts = referenceValues.asList(value -> new CitationCount(value.get("ID").asString(), value.get("PublishedDate").asString(), decay, coauthorsValue.asInt()));
                    author.setCitations(citationCounts);
                    authors.add(author);
                });
            }
            concept.setAuthors(authors);
            concepts.add(concept);
        }
        // TODO: Not sure this this is good enough, pretty inefficient but fine for a demo with small data
        List<Map<String, Double>> scores = new ArrayList<>();
        for (Concept concept : concepts) {
            Map<String, Double> conceptScores = new HashMap<>();
            List<Author> authors = concept.getAuthors();
            for (Author author : authors) {
                // Normalize citation count contributions w.r.t a normalization service
                authorNormalizationService.normalize(author);
                // Calculate the hIndex for each author in the concept. Add it to the conceptHIndex
                double authorHindex = indexMetricService.calculate(author.getCitations());
                author.setHindex(authorHindex);
            }
            double totalHindex = authors.stream().mapToDouble(Author::getHindex).sum();
            double averageHindex = totalHindex / authors.size();
            // Reset each h-index with the normalized value.
            authors.forEach(author -> {
                double currentHindex = author.getHindex();
                author.setHindex(currentHindex / averageHindex);
            });
            // Necessary if an author has multiple publications in the same concept space. TODO: This is handled in the query, so not necessary
            conceptScores = generateConceptScores(authors);
            scores.add(conceptScores);
        }
        // TODO: Flatten scores map, merge common key scores, and sort by largest values. Take top k.
        // TODO: Write array result into a JSON Array. Might want to consider returning a JSON object with prefLabels, and other metadata besides just author rankings?
        return null;
    }

    private Map<String, Double> generateConceptScores(List<Author> authors) {
        Map<String, Double> resultsMap = new HashMap<>();
        for (Author author : authors) {
            resultsMap.putIfAbsent(author.getId(), author.getHindex());
            resultsMap.computeIfPresent(author.getId(), (id, prev) -> prev + author.getHindex());
        }
        return resultsMap;
    }
}
