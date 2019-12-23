package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.UpdateUserModel;

/**
 * UserService
 */

@Dependent
public interface UserService {

    public Response getUser(String id);

    public Response deleteUser(String id);

    public Response updateUser(UpdateUserModel user);

    public Response createUser(CreateUserModel user);

    public Response listUsers();

}