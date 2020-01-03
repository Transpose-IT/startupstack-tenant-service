package dev.startupstack.tenantservice.shared;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;

/**
 * TestConstants
 */
public class TestConstants {

    public static final String testTenantID = "tenantid123";
    public static final String testEmail = "testuser@fake.net";
    public static final String testPassword = "very-insecure-password";
    public static final String testDefaultRole = "admin";

    public static final String testLoginEmail = "loginuser@fake.net";

    public static final String loginUrl = API_URL_PREFIX + "/" + "session/login";
    public static final String userUrl = API_URL_PREFIX + "/" + "user";
}