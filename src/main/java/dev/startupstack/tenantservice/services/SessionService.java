package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.LoginModel;

/**
 * SessionService
 */
@Dependent
public interface SessionService {

    public Response login(LoginModel login);

    public Response logout(String accessToken, String uid);
     
}