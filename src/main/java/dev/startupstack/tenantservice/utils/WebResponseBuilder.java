package dev.startupstack.tenantservice.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.startupstack.tenantservice.models.WebResponseModel;

/**
 * A helper class that can take regular strings and maps it to a valid
 * {@link WebResponseModel} so it can be returned as JSON.
 */
public class WebResponseBuilder {
    private static ObjectMapper mapper = new ObjectMapper();

    public static Response build(String message, int code) {
        try {
            return Response.status(code).entity(mapper.writeValueAsString(new WebResponseModel(message, code))).build();
        } catch (JsonProcessingException jpe) {
            throw new WebApplicationException(jpe.getMessage(), jpe);
        }
    }

    public static Response build(String message, int code, Object object) {
        try {
            return Response.status(code).entity(mapper.writeValueAsString(new WebResponseModel(message, code, object)))
                    .build();
        } catch (JsonProcessingException jpe) {
            throw new WebApplicationException(jpe.getMessage(), jpe);
        }
    }

}