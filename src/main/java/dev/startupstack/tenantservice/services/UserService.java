package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;

import dev.startupstack.tenantservice.entities.json.UserJSONEntity;



/**
 * UserService
 */

 
@Dependent
public interface UserService {

    public String getUserByID(String uid);

    public String deleteUserByID(String uid);

    public String updateUser(UserJSONEntity user);

    public String createUser(UserJSONEntity user);

    public String listAllUsers();
    
}