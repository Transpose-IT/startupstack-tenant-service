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
 * The REST service of Firebase handles everything the Firebase SDK does not
 * provide, most notibly the login capability. Normally this is used with the
 * Firebase Client SDK which calls the REST API.
 */
@Dependent
public class FirebaseRestService {

    private static final Logger LOG = Logger.getLogger(FirebaseRestService.class);

    @ConfigProperty(name = "startupstack.tenantservice.firebase.webapikey")
    String webApiKey;

    private Client client;

    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/";

    /**
     * Default constructor initializes a REST client
     * 
     * @return
     */
    FirebaseRestService() {
        this.client = ClientBuilder.newBuilder().build();
    }

    /**
     * Takes a {@link LoginModel} which calls the Firebase REST API to login a user.
     * It returns a {@link LoginResponse} object, which may be populated with an
     * error or a valid response given by Firebase.
     * 
     * @param entity A LoginModel that contains the username and password
     * @return LoginResponse A LoginResponse that contains the valid response of
     *         Firebase or contains the error on why the login failed.
     */
    public LoginResponse login(LoginModel entity) {
        LOG.debug("Sending login request ...");
        Response response = this.doPost(Entity.json(entity), "/accounts:signInWithPassword");
        LOG.debug(response.readEntity(String.class));
        return response.readEntity(LoginResponse.class);
    }

    /**
     * Exchanges a token for a new one. Takes a Form as the Firebase REST service
     * requires a POST in a standard FORM_URLENCODED format. Returns a
     * {@link TokenResponse}
     * 
     * @param form The Form object that contains the token.
     * @return TokenResponse The response that contains the new token or an Error
     *         object
     */
    public TokenResponse exchangeToken(Form form) {
        LOG.debug("Exchanging token for a fresh one ...");
        Response response = this.doPost(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), "/token");
        LOG.debug("Token exchange succeeded, returning response");
        return response.readEntity(TokenResponse.class);
    }

    /**
     * Perform the actual HTTP POST to the REST API. Takes a partial url and
     * an @{link Entity} with the right mediaType that this REST endpoint expects.
     * Returns a JAX-RS response object
     * 
     * @param preparedEntity An Entity ecoded with the right MediaType
     * @param url            A partial url unique to this call, the base URL is
     *                       already part of the POST
     * @return Response A JAX-RS Response object
     * @throws WebApplicationException A standard exception which is expected to be
     *                                 caught and sent back to the client as a JSON
     *                                 object.
     */
    private Response doPost(Entity<?> preparedEntity, String url) throws WebApplicationException {
        LOG.debugf("Sending POST to %s", BASE_URL + url + "?key=XXXXXXX");
        try {
            WebTarget target = client.target(BASE_URL + url + "?key=" + webApiKey);
            Response response = target.request().buildPost(preparedEntity).invoke();
            response.bufferEntity();
            return response;
        } catch (IllegalStateException | ProcessingException exception) {
            LOG.errorf("HTTP POST failed! Caused by: %s", exception.getCause());
            throw new WebApplicationException(exception.getMessage());
        }
    }
}