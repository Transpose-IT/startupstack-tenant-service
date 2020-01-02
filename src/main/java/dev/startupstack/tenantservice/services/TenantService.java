package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.tenantservice.models.CreateTenantModel;

/**
 * The TenantService controls everything in regards to Tenants. A user must be
 * part of a tenant, and a tenant will get its own namespace within
 * StartupStack. There is only minimal information stored with regards to
 * Tenants.
 */

@Dependent
public interface TenantService {

    public Response getTenant(String id);

    public Response deleteTenant(String id);

    public Response createTenant(CreateTenantModel tenant);

    public Response listTenants();

}