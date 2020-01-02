package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.UserModel;
import dev.startupstack.tenantservice.models.LoginModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.services.external.FirebaseRestService;
import dev.startupstack.tenantservice.services.external.FirebaseSDKService;

/**
 * The SessionServiceFirebaseImpl implements the {@link SessionService} for
 * Google Firebase. It uses the Firebase REST API rather than the SDK since the
 * SDK does not support login functionality.
 */
@Dependent
public class SessionServiceFirebaseImpl implements SessionService {

    private static final Logger LOG = Logger.getLogger(SessionServiceFirebaseImpl.class);

    @Inject
    FirebaseSDKService firebaseSDKService;

    @Inject
    FirebaseRestService firebaseRestService;

    @Inject
    TokenService tokenService;

    @Inject
    UserEntityService entityService;

    /**
     * Logs in the user as according to the {@link LoginModel}. It will check if an
     * error is returned by Firebase in its JSON response, and will report this back
     * to the client. If login succeeds, the refreshToken will be written to the
     * database by the {@link UserEntityService}
     * 
     * @param login A valid {@link LoginModel}
     * @return Response A JAX-RS {@link Response} object in the Firebase
     *         {@link LoginResponse} format.
     * @throws WebApplicationException A standard exception that will be caught by
     *                                 the ErrorMapper to be returned as JSON.
     */
    @Override
    public Response login(LoginModel login) throws WebApplicationException {
        LOG.info("Logging in user ...");
        try {
            LoginResponse restResponse = firebaseRestService.login(login);

            if (restResponse.getError().isEmpty()) {
                entityService.updateRefreshToken(restResponse.getLocalId(), restResponse.getRefreshToken());
                LOG.infof("[%s] Logging in user: OK", restResponse.getLocalId());
                return Response.ok().entity(restResponse).build();
            } else {
                int code = (int) restResponse.getError().get("code");
                return Response.status(code).entity(restResponse).build();
            }

        } catch (ProcessingException | IllegalStateException exception) {
            LOG.errorf("Logging in user: FAILED - %s ", exception.getMessage());
            throw new WebApplicationException(exception.getMessage());
        }
    }

    /**
     * Logs out a user, which uses the {@link TokenService} to do the actual work.
     * With a given access token it checks if it can be decrypted. If Firebase
     * cannot do this it means the user has already been logged out since the access
     * token is no longer valid. It will also check if user ID in the token matches
     * with the user its trying to log out. If this is the case a 403 Forbidden is
     * returned, otherwise a 204 No Content.
     * 
     * @param accessToken A string representation of the access token
     * @param id          A user ID
     * @return Response A JAX-RS {@link Response} object reprensenting either a 204
     *         or a 403.
     */
    @Override
    public Response logout(String accessToken, String id) {
        LOG.infof("[%s] Logging out user ...", id);
        UserModel userInfo = tokenService.getDecryptedToken(accessToken);
        if (userInfo == null) {
            LOG.infof("[%s] Logging out user: OK - Already logged out", id);
            return Response.noContent().build();
        } else if (userInfo.getid().equals(id)) {
            tokenService.revokeTokens(id);
            LOG.infof("[%s] Logging out user: OK", id);
            return Response.noContent().build();
        } else {
            LOG.warnf("[%s] Logging out user: FAILED - userid in access token did not match provided userid", id);
            return Response.status(Status.FORBIDDEN).build();
        }
    }
}