package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.firebase.LoginModel;

/**
 * SessionService
 */
@Dependent
public interface SessionService {

    public Response login(LoginModel login);
 
    public Response exchangeToken(Form tokenModel);

    public Response validateToken(String token);
    
}