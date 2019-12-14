package dev.startupstack.tenantservice.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.startupstack.tenantservice.entities.json.ResponseEntity;
import dev.startupstack.tenantservice.entities.json.UserJSONEntity;

/**
 * UserServiceFirebaseImpl
 */
@Dependent
public class UserServiceFirebaseImpl implements UserService {

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
        } catch (FileNotFoundException fnfe) {
            System.out.println("Unable to find service account file: " + fnfe);
        } catch (IOException ioe) {
            System.out.println("Got IOException: " + ioe);
        }
    }

    @Override
    public String getUserByID(String uid) {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            UserJSONEntity userEntity = new UserJSONEntity();

            userEntity.setUid(user.getUid());
            userEntity.setCustomClaims(user.getCustomClaims());

            return this.mapper.writeValueAsString(userEntity);

        } catch (FirebaseAuthException fbae) {
            fbae.printStackTrace();
            return fbae.toString();
        } catch (JsonProcessingException jpe) {
            return jpe.toString();
        }
    }

    @Override
    public String listUsers() {
        List<UserJSONEntity> userJSONList = new ArrayList<>();

        try {
            for (Iterator<ExportedUserRecord> iterator = FirebaseAuth.getInstance().listUsers(null).getValues().iterator(); iterator.hasNext();) {
                ExportedUserRecord user = iterator.next();
                UserJSONEntity userEntity = new UserJSONEntity();

                userEntity.setEmail(user.getEmail());
                userEntity.setUid(user.getUid());
                userEntity.setCustomClaims(user.getCustomClaims());

                userJSONList.add(userEntity);
            }
            return this.mapper.writeValueAsString(userJSONList);

        } catch (FirebaseAuthException fbae) {
            // TODO: Better error handling
            return fbae.toString();
        } catch (JsonProcessingException jpe) {
            return jpe.toString();
        }
    }

    @Override
    @JsonIgnoreProperties(value = { "uid" })
    public String createUser(UserJSONEntity user) {
        CreateRequest request = new CreateRequest();
        request.setEmail(user.getEmail());
        request.setUid(UUID.randomUUID().toString());
        request.setPassword(user.getPassword());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("org", "testorg");
        claims.put("role", "admin");

        try {
            UserRecord createdUser = FirebaseAuth.getInstance().createUser(request);
            FirebaseAuth.getInstance().setCustomUserClaims(createdUser.getUid(), claims);

            return this.mapper.writeValueAsString(new ResponseEntity("user created", 200, createdUser));
        } catch (FirebaseAuthException fbae) {
            fbae.printStackTrace();
            return fbae.toString();
        } catch (JsonProcessingException jpe) {
            return jpe.toString();
        }
    }

    @Override
    public String deleteUserByID(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            return this.mapper.writeValueAsString(new ResponseEntity("user deleted", 200, null));
        } catch (FirebaseAuthException fbae) {
            fbae.printStackTrace();
            return fbae.toString();
        } catch (JsonProcessingException jpe) {
            return jpe.toString();
        } 
        
    }

    @PreDestroy
    void predestroy() {
        FirebaseApp.getInstance().delete();
    }

}