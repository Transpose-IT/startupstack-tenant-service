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
 * The TokenServiceFirebaseImpl implements the {@link TokenService} for
 * Firebase. Firebase calls tokens Firebase ID tokens but they are effectively
 * just OAuth2 tokens.
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
    UserEntityService entityService;

    /**
     * Exchanges an existing accessToken for a fresh one. Expects a JAX-RS Form
     * object as the Firebase REST API expects a POST in the FORM_URLENCODED format.
     * 
     * @param form A JAX-RS {@ link Form} containing the access token.
     * @return Response a JAX-RS {@link Response} object which has the
     *         {@link TokenResponse} in JSON format
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
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

    /**
     * This validates a given access Token with Firebase to see if its valid. If no
     * exception is returned, the validation succeeded and the token is valid. If 
     * Firebase returns anything other than a {@link FIREBASE_ERROR_INVALID_CREDENTIAL} 
     * error, we return a 400 Bad Request.
     * 
     * @param accessToken A string of the access token
     * @param id          a user ID
     * @return Response a JAX-RS {@link Response} object which has the
     *         {@link TokenResponse} in JSON format
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    @Override
    public Response validateToken(String accessToken, String id) throws WebApplicationException {
        LOG.infof("[%s] Validating token ...", id);
        Optional<FirebaseAuthException> result = firebaseSDKService.verifyToken(accessToken);

        if (result.isPresent()) {
            LOG.info(result);
            FirebaseAuthException firebaseResult = result.get();
            if (firebaseResult.getErrorCode() == FIREBASE_ERROR_INVALID_CREDENTIAL) {
                LOG.infof("[%s] Validating Token: FAILED - Token is invalid or expired", id);

                int statusCode = Status.FORBIDDEN.getStatusCode();
                return Response.status(statusCode).entity(new WebResponseModel(result.get().getMessage(), statusCode))
                        .build();
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

    /**
     * This passes through the call to the Firebase SDK to revoke all tokens of a given user
     * 
     * @param id A user id
     * @return Response A standard JAX-RS {@link Response} object that returns a 204 No Content
     */
    @Override
    public Response revokeTokens(String id) {
        firebaseSDKService.revokeTokens(id);
        return Response.noContent().build();
    }

    /**
     * Takes an encrypted token and decrypts it, so services can consume its contents. Does a roundtrip to the firebase backend to determine if the token has expired, so has additional latency. If the response is null it
     * 
     * @param accessToken A string representation of an access token
     * @return UserModel A user in the {@link UserModel} form, can also return null if the token cannot be decrypted.
     */
    @Override
    public UserModel getDecryptedToken(String accessToken) {
        FirebaseToken decryptedToken = firebaseSDKService.getDecryptedToken(accessToken);
        if (decryptedToken != null) {
            return new UserModel(decryptedToken.getUid(), decryptedToken.getEmail(), decryptedToken.getClaims());
        }
        return null;
    }
}