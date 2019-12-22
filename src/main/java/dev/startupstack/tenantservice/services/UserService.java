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

    public Response getUserByID(String uid);

    public Response deleteUserByID(String uid);

    public Response updateUser(UpdateUserModel user);

    public Response createUser(CreateUserModel user);

    public Response listAllUsers();

}