package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.startupstack.tenantservice.models.FirebaseLoginResponseModel;
import dev.startupstack.tenantservice.models.LoginModel;

/**
 * FirebaseRestService
 */
@Dependent
public class FirebaseRestService {

    @ConfigProperty(name = "startupstack.tenantservice.firebase.webapikey")
    public String webApiKey;

    private Client client;

    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/";

    public FirebaseRestService() {
        this.client = ClientBuilder.newBuilder().build();
    }

    public FirebaseLoginResponseModel login(LoginModel entity) {
        try {
            WebTarget target = client.target(BASE_URL + "/accounts:signInWithPassword?key=" + webApiKey);
            Response response = target.request().buildPost(Entity.json(entity)).invoke();
            return response.readEntity(FirebaseLoginResponseModel.class);
        } catch(ResponseProcessingException rpe) {
            rpe.printStackTrace();
            return new FirebaseLoginResponseModel();
        }

    }
    
}