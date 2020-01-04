package dev.startupstack.tenantservice.integrationtests;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import static dev.startupstack.tenantservice.shared.TestUtils.*;

import java.io.FileInputStream;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.UserModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.services.TokenService;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)
public class TokenResourceFirebaseTest {

    final String baseUrl = API_URL_PREFIX + "/" + "token";

    private static final Logger LOG = Logger.getLogger(TokenResourceFirebaseTest.class);

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    @Inject
    TokenService tokenService;

    private final String sessionEmail = "session@fake.net";

    @BeforeAll
    void initialize() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
        }
        createTempUser(sessionEmail);
    }

    @Test
    void testThatTokenCanBeValidated() throws Exception {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(sessionEmail);
        given()
            .formParam("access_token", loginResponse.getIdToken())
            .formParam("id", loginResponse.getLocalId())
            .basePath(baseUrl)
        .when().post("/validate")
        .then().statusCode(Status.NO_CONTENT.getStatusCode());    
    }
    @Test
    void onValidateTestThatInvalidTokenReturns403Forbidden() throws Exception {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        given()
            .formParam("access_token", "invalid-token")
            .formParam("id", getUserIDByEmail(sessionEmail))
            .basePath(baseUrl)
        .when().post("/validate")
        .then().statusCode(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void testThatRefreshTokenCanBeExchangedForAccessToken() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(sessionEmail);

        given()
            .formParam("grant_type", "refresh_token")
            .formParam("refresh_token", loginResponse.getRefreshToken())
        .when().post(baseUrl)
        .then()
            .statusCode(Status.OK.getStatusCode()).and()
            .body("refresh_token", equalTo(loginResponse.getRefreshToken())).and()
            .body("user_id", equalTo(loginResponse.getLocalId()));
    }

    @Test
    void testThatAllTokensOfUserIDCanBeRevoked() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        String id = getUserIDByEmail(sessionEmail);
        given()
            .basePath(baseUrl)
            .pathParam("id", id)
        .when().delete("/{id}")
        .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Disabled
    void testThatRevokedTokenCannotBeReused() throws Exception {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(sessionEmail);

        tokenService.revokeTokens(loginResponse.getLocalId());
        Assertions.assertNull(tokenService.getDecryptedToken(loginResponse.getIdToken()));

    }
    
    @Test
    void testThatExchangingAnInvalidTokenReturns400BadRequest() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        given()
            .formParam("grant_type", "refresh_token")
            .formParam("refresh_token", "invalid-refresh-token")
        .when().post(baseUrl)
        .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testThatDecryptingTokenReturnsNullIfTokenIsInvalid() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        Assertions.assertNull(tokenService.getDecryptedToken("invalid-token"));
    }

    @Test
    void testThatAccessTokenCanBeDecrypted() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(sessionEmail);
        UserModel token = tokenService.getDecryptedToken(loginResponse.getIdToken());
        Assertions.assertNotNull(token);

        Assertions.assertEquals(token.getEmail(), sessionEmail);
        Assertions.assertEquals(token.getid(), loginResponse.getLocalId());
    }
    
    @AfterAll
    void teardown() throws Exception {
        deleteUser(sessionEmail);
    }   
}