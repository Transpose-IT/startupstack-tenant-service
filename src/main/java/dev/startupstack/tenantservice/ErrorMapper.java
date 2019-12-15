package dev.startupstack.tenantservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import dev.startupstack.tenantservice.dto.json.WebResponseDTO;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        int code = 500;
        if (exception instanceof WebApplicationException) {
            code = ((WebApplicationException) exception).getResponse().getStatus();
        }

        return Response.status(code).entity(new WebResponseDTO(exception.getMessage(), code)).build();
    }
}