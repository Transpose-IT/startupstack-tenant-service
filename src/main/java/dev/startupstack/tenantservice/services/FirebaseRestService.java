package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.LoginModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.models.firebase.TokenResponse;

/**
 * FirebaseRestService
 */
@Dependent
public class FirebaseRestService {


    private static final Logger LOG = Logger.getLogger(SessionServiceFirebaseImpl.class);

    @ConfigProperty(name = "startupstack.tenantservice.firebase.webapikey")
    String webApiKey;

    private Client client;

    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/";

    FirebaseRestService() {
        this.client = ClientBuilder.newBuilder().build();
    }

    LoginResponse login(LoginModel entity) {
        Response response = this.doPost(Entity.json(entity), "/accounts:signInWithPassword");
        LOG.debug(response.readEntity(String.class));
        return response.readEntity(LoginResponse.class);
    }

    TokenResponse exchangeToken(Form form) {
        Response response = this.doPost(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), "/token");
        return response.readEntity(TokenResponse.class);
    }

    private Response doPost(Entity<?> preparedEntity, String url) throws WebApplicationException {
        try {
            WebTarget target = client.target(BASE_URL + url + "?key=" + webApiKey);
            Response response = target.request().buildPost(preparedEntity).invoke();
            response.bufferEntity();
            return response;
        } catch( IllegalStateException | ProcessingException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }
}