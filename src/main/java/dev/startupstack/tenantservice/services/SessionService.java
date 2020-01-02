package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.LoginModel;

/**
 * SessionService handles all session related actions, such as logging in and logging out a user in a remote IDP
 */
@Dependent
public interface SessionService {

    public Response login(LoginModel login);

    public Response logout(String accessToken, String id);
     
}