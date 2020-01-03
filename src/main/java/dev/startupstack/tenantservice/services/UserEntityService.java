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
 * The UserEntityService does the actual work around Entities, which ends up in its
 * own local database. The local database is primarily used for storing refresh
 * tokens tenant information, as well as serve as a persistent cache on user
 * information. Like this the external IDP does not always need to be contacted
 * to get certain information.
 */
@ApplicationScoped
public class UserEntityService {

    private static final Logger LOG = Logger.getLogger(UserEntityService.class);

    @Inject
    EntityManager entityManager;

    /**
     * Gets a stored refresh token in the database, which is set when a user logs
     * in. Returns null if no refresh token exists.
     * 
     * @param id The user ID
     * @return String A string representation of the refresh token
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
     * Updates a refreshtoken in the database, which will overwrite the existing
     * refreshtoken or write a new one.
     * 
     * @param id            A user ID
     * @param refresh_token The new refresh token to be written
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
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
     * Creates a new user in the database. This is expected to be called in
     * conjuction of storing a user in an IDP. The entries here exist mostly as a
     * persistent cache, as well as storing a refresh token associated with the user
     * when the user has logged in.
     * 
     * @param userModel A {@link CreateUserModel} object
     * @param id        The user ID
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    @Transactional
    public void createUser(CreateUserModel userModel, String id) throws WebApplicationException {
        LOG.debugf("[%s] Creating new DB user ...", id);
        try {
            TenantEntity tenant = entityManager.find(TenantEntity.class, userModel.getTenantID());
            entityManager.persist(new UserEntity(id, userModel.getEmail(), tenant, userModel.getRole()));
            LOG.debugf("[%s] Creating new DB user: OK", id);
        } catch (IllegalArgumentException iae) {
            LOG.errorf("[%s] Creating new DB user: FAILED - %s ", id, iae.getMessage());
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    /**
     * Deletes the user from the database. Does not delete it from the IDP.
     * 
     * @param id The user ID to delete
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
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
     * Updates a user in the DB. Simply calls entityManager.merge on the given
     * Entity
     * 
     * @param userEntity a valid UserEntity which has already been modified with the
     *                   new values.
     */
    @Transactional
    void updateUser(UserEntity userEntity) {
        entityManager.merge(userEntity);
    }

    /**
     * Determines if a user already exists. Takes an email address which is expected
     * to be unique.
     * 
     * @param email A string email address
     * @return boolean Returns true if email exists, false if it does not exists.
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    boolean existingUser(String email) throws WebApplicationException {
        Query query = entityManager.createNativeQuery("SELECT * FROM users WHERE email = :email", UserEntity.class);
        query.setParameter("email", email);

        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException nae) {
            return false;
        } catch (NonUniqueResultException nure) {
            throw new WebApplicationException(nure.getMessage(), nure);
        }
    }
}