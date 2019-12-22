package dev.startupstack.tenantservice;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.WebResponseModel;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);
    @Override
    public Response toResponse(Exception exception) {
        int code = 500;
        if (exception instanceof WebApplicationException) {
            code = ((WebApplicationException) exception).getResponse().getStatus();
        }
        LOG.error(exception.getMessage(), exception);
        return Response.status(code).entity(new WebResponseModel(exception.getMessage(), code)).build();
    }
}