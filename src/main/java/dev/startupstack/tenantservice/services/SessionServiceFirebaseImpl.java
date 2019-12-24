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
 * SessionServiceFirebaseImpl
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
    EntityService entityService;

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