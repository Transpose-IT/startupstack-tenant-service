package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.startupstack.tenantservice.dto.DTOValidator;
import dev.startupstack.tenantservice.dto.json.LoginDTO;
import dev.startupstack.tenantservice.models.firebase.LoginModel;
import dev.startupstack.tenantservice.models.firebase.TokenModel;
import dev.startupstack.tenantservice.services.SessionService;


@ApplicationScoped
@Path(API_URL_PREFIX + "/session")
public class SessionResource {



    @Inject
    SessionService sessionService;

    @Inject
    DTOValidator validator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(LoginModel login) {
        //validator.validate(login);
        return sessionService.login(login);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/token")
    public Response validateToken(@FormParam("token") String token) {
        return sessionService.exchangeToken(new Form().param("grant_type", "refresh_token").param("refresh_token", token));
    }

    // @POST
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path("/logout")
    // public Response logout(CreateUserDTO user) {
    //     validator.validate(user);
    //     return userService.createUser(user);
    // }

    // @POST
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path("/validate/{token}")
    // public Response getUser(@PathParam("uid") final String uid) {
    //     return userService.getUserByID(uid);
    // }

    // @PUT
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path("/{uid}")
    // public Response updateUser(@PathParam("uid") final String uid, UpdateUserDTO user) {
    //     user.setUid(uid);
    //     validator.validate(user);
    //     return userService.updateUser(user);
    // }

    // @DELETE
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path("/{uid}")
    // public Response deleteUser(@PathParam("uid") final String uid) {
    //     return userService.deleteUserByID(uid);
    // }
}

