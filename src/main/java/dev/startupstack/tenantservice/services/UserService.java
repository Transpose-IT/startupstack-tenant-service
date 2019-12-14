package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;



/**
 * UserService
 */

 
@Dependent
public interface UserService {

    public String getUserByID(String uid);

    public String listUsers();

    public String createUser();
    
}