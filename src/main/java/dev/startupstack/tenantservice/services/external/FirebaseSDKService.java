package dev.startupstack.tenantservice.services.external;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * This class wraps the Firebase Admin SDK into calls that we'd need in this
 * service. Note that the SDK does not provide login capabiltiies, we need the
 * REST service for that.
 */
@Dependent
public class FirebaseSDKService {

    private static final Logger LOG = Logger.getLogger(FirebaseSDKService.class);

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    /**
     * Validates an active token issued by Firebase using the Firebase SDK. This
     * does NOT validate if a token has been revoked as this will require a
     * roundtrip to Firebase. That means a revoked token will pass the validation
     * until it expires. If a FirebaseAuthException is thrown, it is captured in the
     * Optional which can then be handled by the caller.
     * 
     * @param accessToken An accesstoken issued by Firebase
     * @return Optional<FirebaseAuthException> An optional exception, will be filled
     *         if an error is found, and empty if verification is successful.
     * @throws WebApplicationException A standard exception that is expected to be
     *                                 caught and wrapped in a JSON response
     */
    public Optional<FirebaseAuthException> verifyToken(String accessToken) throws WebApplicationException {
        Optional<FirebaseAuthException> result = Optional.empty();
        try {
            LOG.debug("Verifying token ...");
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(accessToken);
            LOG.debugf("[%s] Token OK", token.getUid());
            return result;
        } catch (FirebaseAuthException fae) {
            result = Optional.of(fae);
            LOG.warn("Token validation failed: " + fae.getErrorCode());
            return result;
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    /**
     * Returns a decrypted Firebase token which can be used by the caller to
     * validate the contents of the token, e.g. if the user has a certain role or
     * which tenant a user is a part of.
     * 
     * @param accessToken A valid Firebase-issued accesstoken
     * @return FirebaseToken A decrypted Firebase Token
     * @throws WebApplicationException A standard exception that is expected to be
     *                                 caught and wrapped in a JSON response
     */
    public FirebaseToken getDecryptedToken(String accessToken) throws WebApplicationException {
        try {
            LOG.debug("Validating and parsing token ...");
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(accessToken, true);
            LOG.debugf("[%s] Parsing OK, returning token", token.getUid());
            return token;
        } catch (FirebaseAuthException fae) {
            LOG.warn("Unable to get token: " + fae.getMessage());
            return null;
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    /**
     * Revokes all active tokens for a given user, effectively logging them out.
     * 
     * @param id A user ID which is known by Firebase
     * @throws WebApplicationException A standard exception that is expected to be
     *                                 caught and wrapped in a JSON response
     */
    public void revokeTokens(String id) throws WebApplicationException {
        try {
            LOG.debugf("[%s] Revoking tokens ...", id);
            FirebaseAuth.getInstance().revokeRefreshTokens(id);
            LOG.debugf("[%s] Revoke tokens OK", id);
        } catch (IllegalArgumentException | FirebaseAuthException exception) {
            throw new WebApplicationException(exception.getMessage(), exception);
        }
    }

    @PostConstruct
    void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                LOG.debug("Initializing FirebaseApp ...");
                FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

                FirebaseApp.initializeApp(options);
                LOG.debug("FirebaseApp initialized");
            } catch (IOException | NullPointerException exception) {
                throw new WebApplicationException(exception.getMessage(), exception);
            }
        }
    }

    @PreDestroy
    void predestroy() {
        if (FirebaseApp.getApps().isEmpty() == false) {
            FirebaseApp.getInstance().delete();
        }
    }

}