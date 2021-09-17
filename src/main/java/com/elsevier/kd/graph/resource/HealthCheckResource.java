package com.elsevier.kd.graph.resource;

import com.elsevier.kd.graph.model.HealthCheckModel;
import io.quarkus.runtime.configuration.ProfileManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author MOQUINR
 * @author WHITEHEADN
 */
@Path("/_health")
public class HealthCheckResource {

    @ConfigProperty(name = "service.name", defaultValue = "KD Expert Finder Service")
    String serviceName;

    @ConfigProperty(name = "service.version", defaultValue = "0.0.0")
    String buildVersion;

    @ConfigProperty(name = "build.hash", defaultValue = "unknown")
    String buildHash;

    private final String serviceStart = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    private HealthCheckModel model;

    @PostConstruct
    public void init() {
        String environment = ProfileManager.getActiveProfile();
        this.model = new HealthCheckModel(serviceName, environment, buildVersion, buildHash, serviceStart);
    }

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthCheckModel checkHealth() {
        return model;
    }
}