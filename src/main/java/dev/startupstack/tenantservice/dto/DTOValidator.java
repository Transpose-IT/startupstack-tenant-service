package dev.startupstack.tenantservice.dto;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import dev.startupstack.tenantservice.dto.json.CreateUserDTO;

@Dependent
public class DTOValidator {

    @Inject
    Validator validator;

    // TODO: Make this more DRY
    public void validate(CreateUserDTO dto) {
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(dto);

        if (violations.isEmpty() == false) {
            LinkedList<String> messages = new LinkedList<>();
            for(Iterator<ConstraintViolation<CreateUserDTO>> iterator = violations.iterator(); iterator.hasNext();) {
                ConstraintViolation<?> constraint = iterator.next();
                messages.add(constraint.getMessage());
            }
            throw new WebApplicationException(messages.toString(), Status.BAD_REQUEST.getStatusCode());
        } 
    }
}