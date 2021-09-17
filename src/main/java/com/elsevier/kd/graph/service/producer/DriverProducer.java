package com.elsevier.kd.graph.service.producer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class DriverProducer {

    @Inject
    @ConfigProperty(name = "neo4j.uri")
    private String uri;

    @Inject
    @ConfigProperty(name = "neo4j.db")
    private String database;

    @Inject
    @ConfigProperty(name = "neo4j.username")
    private String username;

    @Inject
    @ConfigProperty(name = "neo4j.password")
    private String password;

    private Driver driver;

    @PostConstruct
    public void init() {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @PreDestroy
    public void destroy() {
        this.driver.close();
        this.driver = null;
    }

    @Produces
    @KDDriver
    public Driver driver() {
        return this.driver;
    }
}
