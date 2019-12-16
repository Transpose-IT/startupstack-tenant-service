package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.dto.DTOValidator;
import dev.startupstack.tenantservice.dto.json.CreateUserDTO;
import dev.startupstack.tenantservice.dto.json.UpdateUserDTO;
import dev.startupstack.tenantservice.services.UserService;

@ApplicationScoped
@Path(API_URL_PREFIX + "/users")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    DTOValidator validator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response allUsers() {
        return userService.listAllUsers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createUser(CreateUserDTO user) {
        validator.validate(user);
        return userService.createUser(user);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public Response getUser(@PathParam("uid") final String uid) {
        return userService.getUserByID(uid);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public Response updateUser(@PathParam("uid") final String uid, UpdateUserDTO user) {
        user.setUid(uid);
        validator.validate(user);
        return userService.updateUser(user);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public Response deleteUser(@PathParam("uid") final String uid) {
        return userService.deleteUserByID(uid);
    }
}

