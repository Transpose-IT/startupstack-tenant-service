package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.entities.json.UserJSONEntity;



/**
 * UserService
 */

 
@Dependent
public interface UserService {

    public Response getUserByID(String uid) throws WebApplicationException;

    public Response deleteUserByID(String uid);

    public Response updateUser(UserJSONEntity user);

    public Response createUser(UserJSONEntity user);

    public Response listAllUsers();
    
}