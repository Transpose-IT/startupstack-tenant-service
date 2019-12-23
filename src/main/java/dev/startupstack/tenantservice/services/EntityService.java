package dev.startupstack.tenantservice.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import dev.startupstack.tenantservice.entities.TenantEntity;
import dev.startupstack.tenantservice.entities.UserEntity;
import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.UpdateUserModel;

/**
 * EntityService
 */
@ApplicationScoped
class EntityService {

    @Inject
    EntityManager entityManager;

    String getRefreshToken(String id) {
        try {
            UserEntity user = entityManager.find(UserEntity.class, id);
            if (user == null) {
                return null;
            }
            return user.getRefresh_token();
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Transactional
    void updateRefreshToken(String id, String refresh_token) throws WebApplicationException {
        try {
            UserEntity user = entityManager.find(UserEntity.class, id);
            if (user == null) {
                throw new WebApplicationException("userid " + id + " not found!");
            }
            user.setRefresh_token(refresh_token);
            entityManager.merge(user);

        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Transactional
    void createUser(CreateUserModel userModel, String id) throws WebApplicationException {
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, userModel.getTenantID());
            UserEntity newUser = new UserEntity();
            newUser.setEmail(userModel.getEmail());
            newUser.setRole(userModel.getRole());
            newUser.setTenant(tenant);
            newUser.setid(id);
            entityManager.persist(newUser);
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Transactional
    void deleteUser(String id) throws WebApplicationException {
        try {
            UserEntity user = entityManager.find(UserEntity.class, id);
            entityManager.remove(user);
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    @Transactional
    void updateUser(UserEntity userEntity) {
        entityManager.merge(userEntity);
    }
}