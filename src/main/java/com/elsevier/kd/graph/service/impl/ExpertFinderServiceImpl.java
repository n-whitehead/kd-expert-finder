package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.Concept;
import com.elsevier.kd.graph.model.Work;
import com.elsevier.kd.graph.service.CitationCountNormalizationService;
import com.elsevier.kd.graph.service.ExpertFinderService;
import com.elsevier.kd.graph.service.producer.KDDriver;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ExpertFinderServiceImpl implements ExpertFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertFinderServiceImpl.class);

    @Inject
    @KDDriver
    private Driver driver;

    @Inject
    private CitationCountNormalizationService normalizationService;

    @PostConstruct
    public void init() {
    }

    @Override
    public JsonArray findExperts(Set<String> iris, int k, double decay) {
        LOGGER.info("Filtering Concept List: {}", iris);
        Set<String> filteredConcepts = new HashSet<>();
        if (iris.size() == 1) {
            filteredConcepts.addAll(iris);
        } else {
            // Only look for strictly narrowest concepts from the query
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
        try (Session session = driver.session()) {
            // For each subject find the list of works associated with that subject
            for (String conceptIri : filteredConcepts) {
                // Get concept prefLabels
                Result labelResult = session.run(
                        "MATCH (c:Concept { uri: $iri })-[:prefLabel]->(label) RETURN label.literalForm AS prefLabel",
                        Values.parameters("iri", conceptIri)
                );
                List<String> prefLabels = labelResult.next().get("prefLabel").asList(Value::asString);
                // TODO: Change ignoring identifier node for now, using ID field for work and person
                // Get works that have subject
                Result subjectResult = session.run(
                        "MATCH (c:Concept { uri: $iri })<-[:hasSubject]-(w:Work) RETURN w AS works",
                        Values.parameters("iri", conceptIri));
                List<Work> works = new ArrayList<>();
                subjectResult.stream().forEach(record -> {
                    Value workValue = record.get("works");
                    Work work = new Work(
                            workValue.get("ID").asString(),
                            workValue.get("PublishedDate").asString()
                    );
                    works.add(work);
                });
                for (Work work : works) {
                    // Get references of works that point to the same concept
                    Result refResult = session.run(
                            "MATCH (:Work { ID: $id })<-[:references]-(ref:Work)-[:hasSubject]->(:Concept { uri: $iri }) RETURN ref AS reference",
                            Values.parameters("id", work.getId(), "iri", conceptIri));
                    List<Work> references = new ArrayList<>();
                    refResult.stream().forEach(record -> {
                        Value workValue = record.get("reference");
                        Work reference = new Work(
                                workValue.get("ID").asString(),
                                workValue.get("PublishedDate").asString()
                        );
                        references.add(reference);
                    });
                    work.setReferences(references);
                    // Get authors of works and their ordinal that point to the same concept
                    Result authorResult = session.run(
                            "MATCH (a:Person)-[o:authorOf]->(:Work { ID: $id }) RETURN a AS author, o.ordinal AS ordinal",
                            Values.parameters("id", work.getId()));
                    List<Author> authors = new ArrayList<>();
                    authorResult.stream().forEach(record -> {
                        Value ordinalValue = record.get("ordinal");
                        Value authorValue = record.get("author");
                        Author author = new Author();
                        if (!ordinalValue.isNull()) {
                            author.setOrdinal(ordinalValue.asInt());
                        }
                        author.setId(authorValue.get("ID").asString());
                        authors.add(author);
                    });
                    work.setAuthors(authors);
                }
                Concept concept = new Concept();
                concept.setIri(conceptIri);
                concept.setPrefLabels(prefLabels);
                concept.setSubjectOf(works);
                concepts.add(concept);
            }
        }
        for (Concept concept : concepts) {
            List<Work> works = concept.getSubjectOf();
            for (Work work : works) {
                
                normalizationService.normalize(work, decay);
            }

        }
        return null;
    }
}
