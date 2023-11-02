package com.example.test_jwt.security;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/api/v1/users/**";
    public static final String SECRET = "SecretKeyToGenJWT";
    public static final String TOKEN_PREFIX = "Bearer "; // space is necessary after Bearer
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 3_600_000; //
}
