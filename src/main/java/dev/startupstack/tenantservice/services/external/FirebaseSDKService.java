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
 * FirebaseSDKService
 */
@Dependent
public class FirebaseSDKService {

    private static final Logger LOG = Logger.getLogger(FirebaseSDKService.class);

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    public Optional<FirebaseAuthException> verifyToken(String accessToken) throws WebApplicationException {
        Optional<FirebaseAuthException> result = Optional.empty();
        try {
            FirebaseAuth.getInstance().verifyIdToken(accessToken);
            return result;
        } catch (FirebaseAuthException fae) {
            result = Optional.of(fae);
            LOG.warn("Token validation failed: " + fae.getMessage());
            return result;
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    public FirebaseToken getDecryptedToken(String accessToken) throws WebApplicationException {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(accessToken, true);
        } catch (FirebaseAuthException fae) {
            LOG.warn("Unable to get token: " + fae.getMessage());
            return null;
        } catch (IllegalArgumentException iae) {
            throw new WebApplicationException(iae.getMessage(), iae);
        }
    }

    
    public void revokeTokens(String id) throws WebApplicationException {
        try {
            LOG.infof("[%s] Revoking tokens ...", id);
            FirebaseAuth.getInstance().revokeRefreshTokens(id);
            LOG.infof("[%s] Revoke tokens OK", id);
        } catch (IllegalArgumentException | FirebaseAuthException exception) {
            throw new WebApplicationException(exception.getMessage(), exception);
        }
    }

    @PostConstruct
    void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
    
                FirebaseApp.initializeApp(options);
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