package dev.startupstack.tenantservice.services.external;

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


    private static final Logger LOG = Logger.getLogger(FirebaseRestService.class);

    @ConfigProperty(name = "startupstack.tenantservice.firebase.webapikey")
    String webApiKey;

    private Client client;

    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/";

    FirebaseRestService() {
        this.client = ClientBuilder.newBuilder().build();
    }

    public LoginResponse login(LoginModel entity) {
        LOG.debug("Sending login request ...");
        Response response = this.doPost(Entity.json(entity), "/accounts:signInWithPassword");
        LOG.debug(response.readEntity(String.class));
        return response.readEntity(LoginResponse.class);
    }

    public TokenResponse exchangeToken(Form form) {
        LOG.debug("Exchanging token for a fresh one ...");
        Response response = this.doPost(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), "/token");
        LOG.debug("Token exchange succeeded, returning response");
        return response.readEntity(TokenResponse.class);
    }

    private Response doPost(Entity<?> preparedEntity, String url) throws WebApplicationException {
        LOG.debugf("Sending POST to %s", BASE_URL + url + "?key=XXXXXXX");
        try {
            WebTarget target = client.target(BASE_URL + url + "?key=" + webApiKey);
            Response response = target.request().buildPost(preparedEntity).invoke();
            response.bufferEntity();
            return response;
        } catch( IllegalStateException | ProcessingException exception) {
            LOG.errorf("HTTP POST failed! Caused by: %s", exception.getCause());
            throw new WebApplicationException(exception.getMessage());
        }
    }
}