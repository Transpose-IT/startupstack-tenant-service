package dev.startupstack.tenantservice.shared;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static dev.startupstack.tenantservice.shared.TestConstants.testTenantID;
import static dev.startupstack.tenantservice.shared.TestConstants.testEmail;
import static dev.startupstack.tenantservice.shared.TestConstants.testPassword;
import static dev.startupstack.tenantservice.shared.TestConstants.testDefaultRole;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.LoginModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ResponseBody;

/**
 * TestUtils
 */
public class TestUtils {

    private static final Logger LOG = Logger.getLogger(TestUtils.class);
    private static final String userUrl = API_URL_PREFIX + "/" + "user";
    private static final String loginUrl = API_URL_PREFIX + "/" + "session/login";

    public static void createTempUser(String email)  {
        CreateUserModel user = new CreateUserModel(testTenantID, testDefaultRole, email, testPassword);
        given().body(user).contentType(ContentType.JSON).when().post(userUrl);
    }

    public static LoginResponse createTempUserAndLogin(String email) {
        CreateUserModel user = new CreateUserModel(testTenantID, testDefaultRole, email, testPassword);
        given().body(user).contentType(ContentType.JSON).when().post(userUrl);

        LoginModel loginModel = new LoginModel();
        loginModel.setEmail(email);
        loginModel.setPassword(testPassword);

        ResponseBody<?> response = given().body(loginModel).contentType(ContentType.JSON)
            .when().post(loginUrl)
            .then().extract().response().getBody();
        return response.as(LoginResponse.class, ObjectMapperType.JACKSON_2);
    }


    public static void deleteUser(String email) throws Exception {
        try {
            UserRecord firebaseUser = FirebaseAuth.getInstance().getUserByEmail(email);
            FirebaseAuth.getInstance().deleteUser(firebaseUser.getUid());    
        } catch (FirebaseAuthException fae) {
            if (fae.getErrorCode() == "user-not-found") {
                LOG.infof("User '%s' not found, ignoring", email);
            } else {
                throw new Exception("Error during user deletion", fae);
            }
        }
    }

    public static String getUserIDByEmail(String email) {
        ArrayList<LinkedHashMap<String, String>> users = when().get(userUrl)
        .then().extract().response().body().jsonPath().get("responseObject");
    
        for (Iterator<LinkedHashMap<String, String>> iterator = users.iterator(); iterator.hasNext();) {
            LinkedHashMap<String, String> user = iterator.next();
            if (user.get("email").equals(email)) {
               return user.get("id");
            }
        }
        fail("Couldn't find id of created user");
        return null;
    }
    
}