package dev.startupstack.tenantservice.services;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.firebase.auth.FirebaseToken;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.UserModel;
import dev.startupstack.tenantservice.models.WebResponseModel;
import dev.startupstack.tenantservice.models.firebase.TokenResponse;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;

/**
 * TokenServiceFirebaseImpl
 */
@Dependent
public class TokenServiceFirebaseImpl implements TokenService {

    private static final Logger LOG = Logger.getLogger(TokenServiceFirebaseImpl.class);

    @Inject
    FirebaseSDKService firebaseSDKService;

    @Inject
    FirebaseRestService firebaseRestService;

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
            throw new WebApplicationException(exception.getMessage(), exception);
        }
    }

    @Override
    public Response validateToken(String accessToken) throws WebApplicationException {
        Optional<String> result = firebaseSDKService.verifyToken(accessToken);

        if (result.isPresent()) {
            int statusCode = Status.BAD_REQUEST.getStatusCode();
            return Response.status(statusCode).entity(new WebResponseModel(result.get(), statusCode)).build();
        } else {
            return Response.ok().build();    
        }
    }

    @Override
    public Response revokeTokens(String uid) {
        firebaseSDKService.revokeTokens(uid);
        return Response.ok().build();
    }

    @Override
    public UserModel getDecryptedToken(String accessToken) {
        FirebaseToken decryptedToken = firebaseSDKService.getDecryptedToken(accessToken);
        UserModel user = new UserModel();
        user.setEmail(decryptedToken.getEmail());
        user.setUid(decryptedToken.getUid());
        user.setCustomClaims(decryptedToken.getClaims());
        return user;
    }
    
}