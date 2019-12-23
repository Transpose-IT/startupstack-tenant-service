package dev.startupstack.tenantservice.services;

import static dev.startupstack.tenantservice.Constants.CLAIM_NAME_ROLE;
import static dev.startupstack.tenantservice.Constants.CLAIM_NAME_TENANT_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
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
import dev.startupstack.tenantservice.models.WebResponseModel;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;

/**
 * UserServiceFirebaseImpl
 */

@Dependent
public class UserServiceFirebaseImpl implements UserService {
    private static final Logger LOG = Logger.getLogger(UserServiceFirebaseImpl.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    FirebaseSDKService firebaseSDKService;

    @Inject
    EntityService entityService;

    @Override
    public Response getUser(String id) {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(id);
            UserModel userModel = new UserModel();

            userModel.setid(user.getUid());
            userModel.setCustomClaims(user.getCustomClaims());
            userModel.setProvider(user.getProviderId());

            return Response.ok().entity(this.mapper.writeValueAsString(userModel)).build();

        } catch (FirebaseAuthException fbae) {
            if (fbae.getErrorCode().equals("user-not-found")) {
                throw new WebApplicationException(fbae.getMessage(), Status.NOT_FOUND);
            } else {
                throw new WebApplicationException(fbae.getMessage());
            }
        } catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe).build();
        }
    }

    @Override
    public Response listUsers() {
        List<UserModel> userJSONList = new ArrayList<>();

        try {
            for (Iterator<ExportedUserRecord> iterator = FirebaseAuth.getInstance().listUsers(null).getValues()
                    .iterator(); iterator.hasNext();) {
                ExportedUserRecord user = iterator.next();
                UserModel userModel = new UserModel();

                userModel.setEmail(user.getEmail());
                userModel.setid(user.getUid());
                userModel.setCustomClaims(user.getCustomClaims());
                userModel.setProvider(user.getProviderId());

                userJSONList.add(userModel);
            }
            return Response.ok().entity(this.mapper.writeValueAsString(userJSONList)).build();

        } catch (FirebaseAuthException | JsonProcessingException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    // TODO: Add check to check for existing email addresses
    public Response createUser(CreateUserModel user) {
        CreateRequest request = new CreateRequest();
        request.setEmail(user.getEmail());
        request.setUid(UUID.randomUUID().toString());
        request.setPassword(user.getPassword());

        try {
            UserRecord createdUser = FirebaseAuth.getInstance().createUser(request);
            FirebaseAuth.getInstance().setCustomUserClaims(createdUser.getUid(), user.getCustomClaims());

            UserRecord responseObject = FirebaseAuth.getInstance().getUser(createdUser.getUid());

            entityService.createUser(user, createdUser.getUid());

            return Response.status(Status.CREATED).entity(this.mapper.writeValueAsString(
                    new WebResponseModel("user created", Status.CREATED.getStatusCode(), responseObject))).build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response deleteUser(String id) {
        try {
            FirebaseAuth.getInstance().deleteUser(id);
            return Response.status(Status.NO_CONTENT)
                    .entity(this.mapper
                            .writeValueAsString(new WebResponseModel("user deleted", Status.NO_CONTENT.getStatusCode())))
                    .build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response updateUser(UpdateUserModel user) {
        UpdateRequest request = new UpdateRequest(user.getid());

        if (user.getEmail() != null) {
            request.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            request.setPassword(user.getPassword());
        }
        if (user.getRole() != null) {
            try {
                Map<String, Object> currentClaims = FirebaseAuth.getInstance().getUser(user.getid())
                        .getCustomClaims();

                Map<String, Object> modifiedClaims = new HashMap<>();
                modifiedClaims.put(CLAIM_NAME_TENANT_ID, currentClaims.get(CLAIM_NAME_TENANT_ID));
                modifiedClaims.put(CLAIM_NAME_ROLE, user.getRole());

                request.setCustomClaims(modifiedClaims);
            } catch (FirebaseAuthException exception) {
                throw new WebApplicationException(exception.getMessage(), exception);
            }
        }
        try {
            UserRecord updatedUser = FirebaseAuth.getInstance().updateUser(request);

            UserEntity userEntity = new UserEntity();
            userEntity.setid(updatedUser.getUid());
            userEntity.setRole(updatedUser.getCustomClaims().get(CLAIM_NAME_ROLE).toString());
            userEntity.setEmail(updatedUser.getEmail());
            
            entityService.updateUser(userEntity);
            
            return Response.status(Status.NO_CONTENT).entity(this.mapper.writeValueAsString(
                    new WebResponseModel("user updated", Status.NO_CONTENT.getStatusCode(), updatedUser))).build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @PreDestroy
    void predestroy() {
        FirebaseApp.getInstance().delete();
    }
}