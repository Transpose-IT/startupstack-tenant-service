package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.CreateTenantModel;

/**
 * UserService
 */

@Dependent
public interface TenantService {

    public Response getTenant(String id);

    public Response deleteTenant(String id);

    public Response createTenant(CreateTenantModel tenant);

    public Response listTenants();

}