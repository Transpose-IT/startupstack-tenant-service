package dev.startupstack.tenantservice.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import com.google.firebase.auth.FirebaseToken;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.firebase.LoginModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.models.firebase.TokenResponse;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;

import static dev.startupstack.tenantservice.Constants.CLAIM_NAME_ORGANIZATION_ID;
/**
 * SessionServiceFirebaseImpl
 */
@Dependent
public class SessionServiceFirebaseImpl implements SessionService {

    private static final Logger LOG = Logger.getLogger(SessionServiceFirebaseImpl.class);

    @PostConstruct
    void postConstruct() {
        FirebaseSDKService.initialize();
    }
    
    @Inject
    FirebaseRestService firebaseRestService;

    @Override
    public Response login(LoginModel login) throws WebApplicationException {
        try {
            LoginResponse restResponse = firebaseRestService.login(login);
            if (restResponse.getError().isEmpty()) {
                return Response.ok().entity(restResponse).build();
            } else {
                int code = (int) restResponse.getError().get("code");
                return Response.status(code).entity(restResponse).build();
            }

        } catch (ProcessingException | IllegalStateException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response exchangeToken(Form form) throws WebApplicationException {
        try {
            TokenResponse restResponse = firebaseRestService.exchangeToken(form);
            if (restResponse.getError().isEmpty()) {
                return Response.ok().entity(restResponse).build();
            } else {
                int code = (int) restResponse.getError().get("code");
                return Response.status(code).entity(restResponse).build();
            }
        } catch (ProcessingException | IllegalStateException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage());
        }
    }

    @Override
    public Response validateToken(String token) throws WebApplicationException {
        FirebaseToken firebaseToken = FirebaseSDKService.verifyToken(token);
        
        String organization = firebaseToken.getClaims().get(CLAIM_NAME_ORGANIZATION_ID).toString();
        System.out.println(organization);
        return Response.ok().build();
    }

    
}