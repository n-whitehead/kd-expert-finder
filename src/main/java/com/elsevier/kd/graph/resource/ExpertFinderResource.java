package com.elsevier.kd.graph.resource;

import com.elsevier.kd.graph.service.ExpertFinderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.Set;

@Path("/")
public class ExpertFinderResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertFinderResource.class);

    @Inject
    private ExpertFinderService service;

    @Path("/expert")
    @GET
    @Produces("application/json")
    public String findExperts(@NotNull @QueryParam("iri") Set<String> iris,
                              @DefaultValue("10") @QueryParam("k") Integer k,
                              @DefaultValue("0") @Min(value = 0) @Max(value = 1) @QueryParam("decay") Double decay) {
        service.findExperts(iris, k, decay);
        return null;
    }
}
