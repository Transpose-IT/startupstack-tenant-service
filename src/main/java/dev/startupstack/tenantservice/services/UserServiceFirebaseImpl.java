package dev.startupstack.tenantservice.services;

import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_ROLE;
import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_TENANT_ID;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.entities.UserEntity;
import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.UpdateUserModel;
import dev.startupstack.tenantservice.models.UserModel;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;
import dev.startupstack.tenantservice.utils.WebResponseBuilder;

/**
 * The UserServiceFirebaseImpl implements the {@link UserService} in Firebase.
 */

@Dependent
public class UserServiceFirebaseImpl implements UserService {
    private static final Logger LOG = Logger.getLogger(UserServiceFirebaseImpl.class);

    @Inject
    FirebaseSDKService firebaseSDKService;

    @Inject
    UserEntityService entityService;

    @Inject
    TenantService tenantService;

    /**
     * Gets a user from Firebase with the given ID.
     * 
     * @param id A user ID
     * @return Response A Response constructed with {@link WebResponseBuilder}
     */
    // TODO: Add support for querying the DB instead of firebase
    @Override
    public Response getUser(String id) {
        try {
            LOG.infof("[%s] Getting user info from Firebase ...", id);
            UserRecord user = FirebaseAuth.getInstance().getUser(id);
            UserModel userModel = new UserModel(user.getUid(), user.getCustomClaims(), user.getProviderId(), user.getEmail());
            LOG.infof("[%s] Getting user info: OK", id);
            return WebResponseBuilder.build(null, Status.OK.getStatusCode(), userModel);

        } catch (FirebaseAuthException fbae) {
            if (fbae.getErrorCode().equals("user-not-found")) {
                LOG.warnf("[%s] Getting user info: FAILED - user not found", id);
                return WebResponseBuilder.build("user not found", Status.NOT_FOUND.getStatusCode());
            } else {
                throw new WebApplicationException(fbae.getMessage());
            }
        }
    }

    /**
     * Returns a list of all users in Firebase
     * 
     * @return Response A Response constructed with {@link WebResponseBuilder}
     */
    // TODO: Add support for querying the DB instead of firebase
    @Override
    public Response listUsers() {
        List<UserModel> userJSONList = new ArrayList<>();

        try {
            LOG.info("Listing all users ...");
            for (Iterator<ExportedUserRecord> iterator = FirebaseAuth.getInstance().listUsers(null).getValues()
                    .iterator(); iterator.hasNext();) {
                ExportedUserRecord user = iterator.next();
                userJSONList.add(
                        new UserModel(user.getUid(), user.getEmail(), user.getCustomClaims(), user.getProviderId()));
            }
            LOG.info("Listing all users: OK ");
            return WebResponseBuilder.build(null, Status.OK.getStatusCode(), userJSONList);
        } catch (FirebaseAuthException exception) {
            LOG.errorf("Listing all users: FAILED - %s", exception.getMessage());
            throw new WebApplicationException(exception.getMessage());
        }
    }

    /**
     * Creates a new user based on the {@link CreateUserModel}. Will create this
     * both in Firebase and the EntityService. If one of the two fails, we will bail
     * out. If database creation fails but Firebase succeeds, the user gets removed
     * from Firebase again.
     * 
     * @param user a valid {@link CreateUserModel}
     * @return Response A Response constructed with {@link WebResponseBuilder}
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    // TODO: Add constraints for role names
    @Override
    public Response createUser(CreateUserModel user) throws WebApplicationException {
        LOG.info("Creating user ...");
        if (entityService.existingUser(user.getEmail())) {
            LOG.info("user creation: FAILED - User already exists");
            return WebResponseBuilder.build("user already exists", Status.FORBIDDEN.getStatusCode());
        }
        Response checkTenantID = tenantService.getTenant(user.getTenantID());
        if (checkTenantID.getStatus() == Status.FORBIDDEN.getStatusCode()) {
            LOG.warn("user creation: FAILED - tenant not found");
            return WebResponseBuilder.build("tenant_id not found", Status.FORBIDDEN.getStatusCode());
        }

        String id = UUID.randomUUID().toString();
        try {
            CreateRequest request = new CreateRequest();
            request.setEmail(user.getEmail());
            request.setUid(id);
            request.setPassword(user.getPassword());
            UserRecord createdUser = FirebaseAuth.getInstance().createUser(request);
            FirebaseAuth.getInstance().setCustomUserClaims(createdUser.getUid(), user.getCustomClaims());

            UserRecord responseObject = FirebaseAuth.getInstance().getUser(createdUser.getUid());
            entityService.createUser(user, createdUser.getUid());

            LOG.infof("user creation: OK - %s", createdUser.getUid());
            return WebResponseBuilder.build("user created", Status.CREATED.getStatusCode(), responseObject);
        } catch (FirebaseAuthException | IllegalArgumentException exception) {
            if (exception.getMessage() == null) {
                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                LOG.errorf("user creation: FAILED - %s", sw.toString());    
            } else {
                LOG.errorf("user creation: FAILED - %s", exception.getMessage());    
            }
            throw new WebApplicationException(exception.getMessage(), exception);
        } 
    }

    /**
     * Deletes a user on the given ID. Deletes it from both the database and
     * Firebase
     * 
     * @param id A user ID
     * @return Response A Response constructed with {@link WebResponseBuilder}
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    @Override
    public Response deleteUser(String id) throws WebApplicationException {
        LOG.infof("[%s] Deleting user ...", id);
        try {
            FirebaseAuth.getInstance().deleteUser(id);
            entityService.deleteUser(id);
            LOG.infof("[%s] User deletion: OK");
            return WebResponseBuilder.build(null, Status.NO_CONTENT.getStatusCode());
        } catch (FirebaseAuthException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    /**
     * Updates a user based on the {@link UpdateUserModel}. It will check which
     * fields are set and updates the fields on both the database and Firebase
     * respectively.
     * 
     * @param user A {@link UpdateUserModel} model
     * @return Response A Response constructed with {@link WebResponseBuilder}
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    @Override
    public Response updateUser(UpdateUserModel user) throws WebApplicationException {
        LOG.infof("[%s] Updating user ...", user.getid());
        UpdateRequest request = new UpdateRequest(user.getid());

        if (user.getEmail() != null) {
            LOG.debug("changing email address");
            request.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            LOG.debug("changing password");
            request.setPassword(user.getPassword());
        }
        if (user.getRole() != null) {
            LOG.debug("changing role");
            try {
                Map<String, Object> currentClaims = FirebaseAuth.getInstance().getUser(user.getid()).getCustomClaims();

                Map<String, Object> modifiedClaims = new HashMap<>();
                modifiedClaims.put(CLAIM_NAME_TENANT_ID, currentClaims.get(CLAIM_NAME_TENANT_ID));
                modifiedClaims.put(CLAIM_NAME_ROLE, user.getRole());

                request.setCustomClaims(modifiedClaims);
            } catch (FirebaseAuthException exception) {
                LOG.errorf("[%s] Updating user: FAILED - changing role failed: %s", user.getid(),
                        exception.getMessage());
                throw new WebApplicationException(exception.getMessage(), exception);
            }
        }
        try {
            UserRecord updatedUser = FirebaseAuth.getInstance().updateUser(request);
            entityService.updateUser(new UserEntity(updatedUser.getUid(), updatedUser.getEmail(),
                    updatedUser.getCustomClaims().get(CLAIM_NAME_ROLE).toString()));

            LOG.infof("[%s] Updating user: OK", updatedUser.getUid());
            return WebResponseBuilder.build("user updated", Status.ACCEPTED.getStatusCode(), updatedUser);
        } catch (FirebaseAuthException exception) {
            throw new WebApplicationException(exception.getMessage(), exception);
        }
    }
}