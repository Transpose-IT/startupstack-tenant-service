package dev.startupstack.tenantservice.dto;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import dev.startupstack.tenantservice.dto.json.WebResponseDTO;

@Dependent
public class DTOValidator {

    @Inject
    Validator validator;

    public <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (violations.isEmpty() == false) {
            LinkedList<String> messages = new LinkedList<>();
            for (Iterator<ConstraintViolation<T>> iterator = violations.iterator(); iterator.hasNext();) {
                ConstraintViolation<T> constraint = iterator.next();
                messages.add(constraint.getMessage());
            }
            int statusCode = Status.BAD_REQUEST.getStatusCode();
            Response response = Response.status(statusCode).entity(new WebResponseDTO(messages, statusCode)).build();
            throw new WebApplicationException(response);
        }
    }
}