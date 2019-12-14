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

    public String listUsers();

    public String createUser(UserJSONEntity user);
    
}