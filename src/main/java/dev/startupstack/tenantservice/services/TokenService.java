package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.UserModel;

/**
 * TokenService
 */
@Dependent
public interface TokenService {

    public Response exchangeToken(Form tokenModel);

    public Response validateToken(String accessToken, String id);

    public Response revokeTokens(String id);

    UserModel getDecryptedToken(String accessToken);
}