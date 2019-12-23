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
        try {
            LoginResponse restResponse = firebaseRestService.login(login);

            if (restResponse.getError().isEmpty()) {
                entityService.updateRefreshToken(restResponse.getLocalId(), restResponse.getRefreshToken());
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
    public Response logout(String accessToken, String id) {
        UserModel userInfo = tokenService.getDecryptedToken(accessToken);
        if (userInfo.getid().equals(id)) {
            tokenService.revokeTokens(id);
            return Response.noContent().build();
        } else {
            return Response.status(Status.BAD_REQUEST.getStatusCode()).build();
        }
    }
}