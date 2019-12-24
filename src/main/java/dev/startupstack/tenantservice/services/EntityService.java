package dev.startupstack.tenantservice.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.entities.TenantEntity;
import dev.startupstack.tenantservice.entities.UserEntity;
import dev.startupstack.tenantservice.models.CreateUserModel;

/**
 * 
 */
@ApplicationScoped
class EntityService {

    private static final Logger LOG = Logger.getLogger(EntityService.class);

    @Inject
    EntityManager entityManager;

    
    /** 
     * @param id
     * @return String
     */
    String getRefreshToken(String id) {
        LOG.debugf("[%s] Getting refresh token ...", id);
        try {

            UserEntity user = entityManager.find(UserEntity.class, id);
            if (user == null) {
                return null;
            }
            LOG.debugf("[%s] Getting refresh token: OK", id);
            return user.getRefresh_token();
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Getting refresh token: FAILED - %s ", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    
    /** 
     * @param id
     * @param refresh_token
     * @throws WebApplicationException
     */
    @Transactional
    void updateRefreshToken(String id, String refresh_token) throws WebApplicationException {
        LOG.debugf("[%s] Updating refresh token ...", id);
        try {
            UserEntity user = entityManager.find(UserEntity.class, id);
            if (user == null) {
                throw new WebApplicationException("userid " + id + " not found!");
            }
            user.setRefresh_token(refresh_token);
            entityManager.merge(user);
            LOG.debugf("[%s] Updating refresh token: OK", id);
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Updating refresh token: FAILED - %s ", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    
    /** 
     * @param userModel
     * @param id
     * @throws WebApplicationException
     */
    @Transactional
    void createUser(CreateUserModel userModel, String id) throws WebApplicationException {
        LOG.debugf("[%s] Creating new DB user ...", id);
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, userModel.getTenantID());
            UserEntity newUser = new UserEntity();
            newUser.setEmail(userModel.getEmail());
            newUser.setRole(userModel.getRole());
            newUser.setTenant(tenant);
            newUser.setid(id);
            entityManager.persist(newUser);
            LOG.debugf("[%s] Creating new DB user: OK", id);
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Creating new DB user: FAILED - %s ", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    
    /** 
     * @param id
     * @throws WebApplicationException
     */
    @Transactional
    void deleteUser(String id) throws WebApplicationException {
        LOG.debugf("[%s] Deleting DB user ...", id);
        try {
            UserEntity user = entityManager.find(UserEntity.class, id);
            entityManager.remove(user);
            LOG.debugf("[%s] Deleting DB user: OK", id);
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Deleting DB user: FAILED - %s ", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    
    /** 
     * Updates a user in the DB. Calls entityManager.merge on the given Entity
     * 
     * @param userEntity a valid UserEntity
     */
    @Transactional
    void updateUser(UserEntity userEntity) {
        entityManager.merge(userEntity);
    }

    
    /** 
     * @param email
     * @return boolean
     */
    boolean existingUser(String email) {
        Query query = entityManager.createNativeQuery("SELECT * FROM users WHERE email = :email" , UserEntity.class);
        query.setParameter("email", email);

        try {
           query.getSingleResult();
           return true;
        } catch(NoResultException nae) {
            return false;
        } catch(NonUniqueResultException nure) {
            throw new WebApplicationException(nure.getMessage(), nure);
        }
    }
}