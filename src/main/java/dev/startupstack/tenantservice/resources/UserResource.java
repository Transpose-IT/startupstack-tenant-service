package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
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

import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.utils.ModelValidator;
import dev.startupstack.tenantservice.models.UpdateUserModel;
import dev.startupstack.tenantservice.services.UserService;

/**
 * REST entrypoint (resource) for user management
 */
@ApplicationScoped
@Path(API_URL_PREFIX + "/user")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    ModelValidator validator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response allUsers() {
        return userService.listUsers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createUser(CreateUserModel user) {
        validator.validate(user);
        return userService.createUser(user);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUser(@NotBlank @PathParam("id") final String id) {
        return userService.getUser(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateUser(@NotBlank @PathParam("id") final String id, UpdateUserModel user) {
        user.setid(id);
        validator.validate(user);
        return userService.updateUser(user);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response deleteUser(@NotBlank @PathParam("id") final String id) {
        return userService.deleteUser(id);
    }
}

