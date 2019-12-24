package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.LoginModel;
import dev.startupstack.tenantservice.models.ModelValidator;
import dev.startupstack.tenantservice.services.SessionService;


@ApplicationScoped
@Path(API_URL_PREFIX + "/session")
public class SessionResource {



    @Inject
    SessionService sessionService;

    @Inject
    ModelValidator validator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(LoginModel login) {
        validator.validate(login);
        return sessionService.login(login);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logout(@NotBlank @FormParam("access_token") String accessToken, @NotBlank @FormParam("id") String id) {
        return sessionService.logout(accessToken, id);
    }
}

