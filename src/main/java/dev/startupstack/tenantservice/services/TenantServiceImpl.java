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

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.entities.TenantEntity;
import dev.startupstack.tenantservice.models.CreateTenantModel;

/**
 * TenantServiceFirebaseImpl
 */
@Dependent
public class TenantServiceImpl implements TenantService {

    private static final Logger LOG = Logger.getLogger(TenantServiceImpl.class);

    @Inject
    EntityManager entityManager;

    @Override
    public Response getTenant(String id) {
        LOG.infof("[%s] Getting tenant ...", id);
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, id);
            if (tenant != null) {
                LOG.infof("[%s] Getting tenant: OK", id);
                return Response.ok().entity(tenant).build();
            } else {
                LOG.infof("[%s] Getting tenant: FAILED - tenant not found", id);
                return Response.status(Status.FORBIDDEN).entity(tenant).build();
            }
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Getting tenant: FAILED - %s", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    @Transactional
    public Response deleteTenant(String id) {
        LOG.infof("[%s] Deleting tenant ...", id);
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, id);
            if (tenant != null) {
                entityManager.remove(tenant);
                LOG.infof("[%s] Deleting tenant: OK", id);
                return Response.ok().entity(tenant).build();
            } else {
                LOG.warnf("[%s] Deleting tenant: FAILED - tenant not found", id);
                return Response.status(Status.FORBIDDEN).entity(tenant).build();
            }
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Getting tenant: FAILED - %s", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    @Transactional
    public Response createTenant(CreateTenantModel tenantModel) {
        LOG.info("Creating tenant ...");
        try {
            TenantEntity tenant = new TenantEntity();
            tenant.setId(UUID.randomUUID().toString());
            tenant.setName(tenantModel.getName());
            entityManager.persist(tenant);
            LOG.infof("Creating tenant: OK - %s", tenant.getId());
            return Response.ok().entity(tenant).build();
        } catch (IllegalArgumentException iae) {
            LOG.errorf("Creating tenant: FAILED - %s", iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Override
    public Response listTenants() {
        LOG.info("Getting all tenant ...");
        Query result = entityManager.createNativeQuery("SELECT * FROM tenants", TenantEntity.class);

        @SuppressWarnings("unchecked")
        List<TenantEntity> tenantEntities = result.getResultList();
        LOG.info("Getting all tenants: OK");
        return Response.ok().entity(tenantEntities).build();
    }
}