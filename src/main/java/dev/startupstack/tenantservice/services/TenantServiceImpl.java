package dev.startupstack.tenantservice.services;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import dev.startupstack.tenantservice.entities.TenantEntity;
import dev.startupstack.tenantservice.models.CreateTenantModel;

/**
 * TenantServiceFirebaseImpl
 */
@Dependent
public class TenantServiceImpl implements TenantService {

    @Inject
    EntityManager entityManager;

    @Override
    public Response getTenant(String id) {
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, id);
            if (tenant != null) {
                return Response.ok().entity(tenant).build();
            } else {
                return Response.status(Status.NOT_FOUND.getStatusCode()).entity(tenant).build();
            }
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    public Response deleteTenant(String id) {
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, id);
            if (tenant != null) {
                entityManager.remove(tenant);
                return Response.ok().entity(tenant).build();
            } else {
                return Response.status(Status.NOT_FOUND.getStatusCode()).entity(tenant).build();
            }
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    @Transactional
    public Response createTenant(CreateTenantModel tenantModel) {
        try {
            TenantEntity tenant = new TenantEntity();
            tenant.setId(UUID.randomUUID().toString());
            tenant.setName(tenantModel.getName());
            entityManager.persist(tenant);
            return Response.ok().entity(tenant).build();
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    public Response listTenants() {
        Query result = entityManager.createNativeQuery("SELECT * FROM tenants", TenantEntity.class);

        @SuppressWarnings("unchecked")
        List<TenantEntity> tenantEntities = result.getResultList();

        return Response.ok().entity(tenantEntities).build();
    }
}