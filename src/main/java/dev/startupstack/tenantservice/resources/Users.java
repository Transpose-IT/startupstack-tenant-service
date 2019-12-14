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

import dev.startupstack.tenantservice.entities.json.UserJSONEntity;
import dev.startupstack.tenantservice.services.UserService;

@ApplicationScoped
@Path(API_URL_PREFIX + "/users")
public class Users {

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public String allUsers() {
        return userService.listAllUsers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public String createUser( UserJSONEntity user) {
        return userService.createUser(user);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public String getUser(@PathParam("uid") final String uid) {
        return userService.getUserByID(uid);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public String updateUser(@PathParam("uid") final String uid, UserJSONEntity user) {
        user.setUid(uid);
        return userService.updateUser(user);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uid}")
    public String deleteUser(@PathParam("uid") final String uid) {
        return userService.deleteUserByID(uid);
    }
}

