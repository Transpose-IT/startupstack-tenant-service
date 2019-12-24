package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.services.TokenService;


/**
 * TokenResource
 */
@ApplicationScoped
@Path(API_URL_PREFIX + "/token")
public class TokenResource {

    @Inject
    TokenService tokenService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response exchangeToken(@FormParam("refresh_token") String refreshToken) {
        return tokenService.exchangeToken(new Form().param("grant_type", "refresh_token").param("refresh_token", refreshToken));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validate")
    public Response validateToken(@FormParam("access_token") String accessToken, @FormParam("id") String id) {
        return tokenService.validateToken(accessToken, id);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response revokeTokens(@PathParam("id") final String id) {
        return tokenService.revokeTokens(id);
    }
}