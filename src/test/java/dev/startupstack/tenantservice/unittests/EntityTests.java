package dev.startupstack.tenantservice.unittests;

import static dev.startupstack.tenantservice.shared.TestConstants.*;
import static dev.startupstack.tenantservice.shared.TestUtils.*;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.services.UserEntityService;
import io.quarkus.test.junit.QuarkusTest;

/**
 * EntityTests
 */
@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
public class EntityTests {

    private static final Logger LOG = Logger.getLogger(EntityTests.class);

    @Inject
    UserEntityService userEntityService;

    @Test
    void testThatRefreshTokenCanBeRetrievedFromDB() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        createTempUser(testEmail);
        LoginResponse response = LoginTempUser(testEmail);

        Assertions.assertEquals(response.getRefreshToken(), userEntityService.getRefreshToken(response.getLocalId()));
    }
    
    @Test
    void testThatGettingARefreshTokenWithAnInvalidUserReturnsNull() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        Assertions.assertNull(userEntityService.getRefreshToken("invalid-user-id"));
    }

    @Test
    void onUpdateOfRefreshTokenTestThatInvalidUserIDReturnsWebApplicationException() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        Assertions.assertThrows(WebApplicationException.class, () -> userEntityService.updateRefreshToken("invalid-id", "invalid-refresh-token"));
    }

    @AfterAll
    void teardown() throws Exception {
        deleteUser(testEmail);
    }

}