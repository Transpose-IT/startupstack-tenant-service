package dev.startupstack.tenantservice.resources;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import dev.startupstack.tenantservice.models.CreateTenantModel;
import dev.startupstack.tenantservice.models.ModelValidator;
import dev.startupstack.tenantservice.services.TenantService;

/**
 * TenantResource
 */
@ApplicationScoped
@Path(API_URL_PREFIX + "/tenant")
public class TenantResource {

    @Inject
    TenantService tenantService;

    @Inject
    ModelValidator validator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response allUsers() {
        return tenantService.listTenants();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createTenant(CreateTenantModel tenant) {
        validator.validate(tenant);
        return tenantService.createTenant(tenant);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUser(@NotBlank @PathParam("id") final String id) {
        return tenantService.getTenant(id);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response deleteUser(@NotBlank @PathParam("id") final String id) {
        return tenantService.deleteTenant(id);
    }
    
}