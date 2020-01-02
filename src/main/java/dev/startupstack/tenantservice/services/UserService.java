package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.UpdateUserModel;

/**
 * The UserService handles everything regarding users. Based on the
 * implementation it can also recruit the {@link UserEntityService} to store
 * data in the database as a persistent cache, in addition to storing it in the
 * IDP.
 */

@Dependent
public interface UserService {

    public Response getUser(String id);

    public Response deleteUser(String id);

    public Response updateUser(UpdateUserModel user);

    public Response createUser(CreateUserModel user);

    public Response listUsers();

}