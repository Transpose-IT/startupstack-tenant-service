package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.dto.json.CreateUserDTO;
import dev.startupstack.tenantservice.dto.json.UpdateUserDTO;

/**
 * UserService
 */

@Dependent
public interface UserService {

    public Response getUserByID(String uid);

    public Response deleteUserByID(String uid);

    public Response updateUser(UpdateUserDTO user);

    public Response createUser(CreateUserDTO user);

    public Response listAllUsers();

}