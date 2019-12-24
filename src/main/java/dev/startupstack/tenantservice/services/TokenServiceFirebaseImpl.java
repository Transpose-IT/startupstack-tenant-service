package dev.startupstack.tenantservice.services;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.UserModel;
import dev.startupstack.tenantservice.models.WebResponseModel;
import dev.startupstack.tenantservice.models.firebase.TokenResponse;
import dev.startupstack.tenantservice.services.external.FirebaseRestService;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;

/**
 * TokenServiceFirebaseImpl
 */
@Dependent
public class TokenServiceFirebaseImpl implements TokenService {

    private static final Logger LOG = Logger.getLogger(TokenServiceFirebaseImpl.class);
    private static final String FIREBASE_ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL";

    @Inject
    FirebaseSDKService firebaseSDKService;

    @Inject
    FirebaseRestService firebaseRestService;

    @Inject
    EntityService entityService;

    @Override
    public Response exchangeToken(Form form) throws WebApplicationException {
        LOG.info("Exchanging Token ...");
        try {
            TokenResponse restResponse = firebaseRestService.exchangeToken(form);
            if (restResponse.getError().isEmpty()) {
                LOG.info("Exchanging Token: OK");
                return Response.ok().entity(restResponse).build();
            } else {
                int code = (int) restResponse.getError().get("code");
                LOG.warnf("Exchanging Token: FAILED - %s", restResponse.getError().get("message").toString());
                return Response.status(code).entity(restResponse).build();
            }
        } catch (ProcessingException | IllegalStateException exception) {
            LOG.errorf("Exchanging Token: FAILED - %s", exception.getMessage());
            throw new WebApplicationException(exception.getMessage(), exception);
        }
    }

    @Override
    public Response validateToken(String accessToken, String id) throws WebApplicationException {
        LOG.infof("[%s] Validating token ...", id);
        Optional<FirebaseAuthException> result = firebaseSDKService.verifyToken(accessToken);

        if (result.isPresent()) {
            FirebaseAuthException firebaseResult = result.get();
            if (firebaseResult.getErrorCode() == FIREBASE_ERROR_INVALID_CREDENTIAL) {
                LOG.infof("[%s] Validating Token: PENDING - Token expired, attempting to refresh it", id);
                String refresh_token = entityService.getRefreshToken(id);

                if (refresh_token == null) {
                    LOG.warnf("[%s] Validating Token: FAILED - no refresh token present in DB", id);
                    return Response.status(Status.BAD_REQUEST.getStatusCode())
                            .entity(new WebResponseModel("NO_REFRESHTOKEN", Status.BAD_REQUEST.getStatusCode()))
                            .build();
                }

                Form form = new Form().param("grant_type", "refresh_token").param("refresh_token", refresh_token);
                TokenResponse refreshedAccessToken = this.exchangeToken(form).readEntity(TokenResponse.class);

                if (refreshedAccessToken.getError().isEmpty()) {
                    LOG.infof("[%s] Validating Token: OK", id);
                    return Response.ok().entity(refreshedAccessToken).build();
                } else {
                    LOG.warnf("[%s] Validating Token: FAILED - %s", id, refreshedAccessToken.getError().get("message"));
                    return Response.status(Status.FORBIDDEN.getStatusCode()).entity(refreshedAccessToken).build();
                }
            } else {
                int statusCode = Status.BAD_REQUEST.getStatusCode();
                LOG.warnf("[%s] Validating Token: FAILED - %s", id, result.get().getMessage());
                return Response.status(statusCode).entity(new WebResponseModel(result.get().getMessage(), statusCode))
                        .build();
            }
        } else {
            LOG.infof("[%s] Validating Token: OK", id);
            return Response.noContent().build();
        }
    }

    @Override
    public Response revokeTokens(String id) {
        firebaseSDKService.revokeTokens(id);
        return Response.ok().build();
    }

    @Override
    public UserModel getDecryptedToken(String accessToken) {
        FirebaseToken decryptedToken = firebaseSDKService.getDecryptedToken(accessToken);
        UserModel user = new UserModel();
        user.setEmail(decryptedToken.getEmail());
        user.setid(decryptedToken.getUid());
        user.setCustomClaims(decryptedToken.getClaims());
        return user;
    }

}