package com.elsevier.kd.graph.resource;

import com.elsevier.kd.graph.model.ErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Response toResponse(Exception ex) {
        LOGGER.info("An error is being handled.", ex);
        int statusCode = 0;
        String message = ex.getLocalizedMessage();
        if (ex instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) ex;
            Response r = wae.getResponse();
            statusCode = r.getStatusInfo().getStatusCode();
        } else {
            if (statusCode == 0) {
                statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            }
        }
        ErrorModel error = new ErrorModel(statusCode, message);
        Response.ResponseBuilder responseBuilder = Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(error);
        return responseBuilder.build();
    }
}