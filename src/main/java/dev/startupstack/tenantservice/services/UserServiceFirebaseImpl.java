package dev.startupstack.tenantservice.services;

import static dev.startupstack.tenantservice.entities.json.UserJSONEntity.CLAIM_NAME_ORGANIZATION_ID;
import static dev.startupstack.tenantservice.entities.json.UserJSONEntity.CLAIM_NAME_ROLE;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.entities.json.ResponseEntity;
import dev.startupstack.tenantservice.entities.json.UserJSONEntity;

/**
 * UserServiceFirebaseImpl
 */

@Dependent
public class UserServiceFirebaseImpl implements UserService {
    private static final Logger LOG = Logger.getLogger(UserServiceFirebaseImpl.class);

    // ConfigProperty values are not injected on construction time, don't use them
    // in a constructor
    @ConfigProperty(name = "startupstack.tenantservice.services.userservice.firebase.keyfile")
    public String serviceAccountFile;

    private ObjectMapper mapper = new ObjectMapper();


    @PostConstruct
    void postConstruct() {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            FirebaseApp.initializeApp(options);
        } catch (IOException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response getUserByID(String uid) throws WebApplicationException {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            UserJSONEntity userEntity = new UserJSONEntity();

            userEntity.setUid(user.getUid());
            userEntity.setCustomClaims(user.getCustomClaims());
            userEntity.setProvider(user.getProviderId());

            return Response.ok().entity(this.mapper.writeValueAsString(userEntity)).build();

        } catch (FirebaseAuthException fbae) {
            if (fbae.getErrorCode() == "user-not-found") {
                throw new WebApplicationException(fbae.getMessage(), Status.NOT_FOUND);
            } else {
                LOG.error(fbae.getMessage(), fbae);
                throw new WebApplicationException(fbae.getMessage());
            }
        } catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe).build();
        }
    }

    @Override
    public Response listAllUsers() {
        List<UserJSONEntity> userJSONList = new ArrayList<>();

        try {
            for (Iterator<ExportedUserRecord> iterator = FirebaseAuth.getInstance().listUsers(null).getValues()
                    .iterator(); iterator.hasNext();) {
                ExportedUserRecord user = iterator.next();
                UserJSONEntity userEntity = new UserJSONEntity();

                userEntity.setEmail(user.getEmail());
                userEntity.setUid(user.getUid());
                userEntity.setCustomClaims(user.getCustomClaims());
                userEntity.setProvider(user.getProviderId());

                userJSONList.add(userEntity);
            }
            return Response.ok().entity(this.mapper.writeValueAsString(userJSONList)).build();

        } catch (FirebaseAuthException | JsonProcessingException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response createUser(UserJSONEntity user) {
        CreateRequest request = new CreateRequest();
        request.setEmail(user.getEmail());
        request.setUid(UUID.randomUUID().toString());
        request.setPassword(user.getPassword());

        try {
            UserRecord createdUser = FirebaseAuth.getInstance().createUser(request);
            FirebaseAuth.getInstance().setCustomUserClaims(createdUser.getUid(), user.getCustomClaims());

            UserRecord responseObject = FirebaseAuth.getInstance().getUser(createdUser.getUid());
            return Response.status(Status.CREATED)
                    .entity(this.mapper.writeValueAsString(
                            new ResponseEntity("user created", Status.CREATED.getStatusCode(), responseObject)))
                    .build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response deleteUserByID(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            return Response.status(Status.NO_CONTENT)
                    .entity(this.mapper
                            .writeValueAsString(new ResponseEntity("user deleted", Status.NO_CONTENT.getStatusCode())))
                    .build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response updateUser(UserJSONEntity incomingJSON) {
        UpdateRequest request = new UpdateRequest(incomingJSON.getUid());

        if (incomingJSON.getEmail() != null) {
            request.setEmail(incomingJSON.getEmail());
        }
        if (incomingJSON.getPassword() != null) {
            request.setPassword(incomingJSON.getPassword());
        }
        if (incomingJSON.getRole() != null) {
            try {
                Map<String, Object> currentClaims = FirebaseAuth.getInstance().getUser(incomingJSON.getUid())
                        .getCustomClaims();

                Map<String, Object> modifiedClaims = new HashMap<>();
                modifiedClaims.put(CLAIM_NAME_ORGANIZATION_ID, currentClaims.get(CLAIM_NAME_ORGANIZATION_ID));
                modifiedClaims.put(CLAIM_NAME_ROLE, incomingJSON.getRole());

                request.setCustomClaims(modifiedClaims);
            } catch (FirebaseAuthException exception) {
                LOG.error(exception.getMessage(), exception);
                throw new WebApplicationException(exception.getMessage());
            }
        }
        try {
            UserRecord updatedUser = FirebaseAuth.getInstance().updateUser(request);
            return Response.status(Status.NO_CONTENT)
                    .entity(this.mapper.writeValueAsString(
                            new ResponseEntity("user updated", Status.NO_CONTENT.getStatusCode(), updatedUser)))
                    .build();
        } catch (FirebaseAuthException | JsonProcessingException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @PreDestroy
    void predestroy() {
        FirebaseApp.getInstance().delete();
    }
}