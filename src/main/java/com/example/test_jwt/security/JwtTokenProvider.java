package com.example.test_jwt.security;

import com.example.test_jwt.entity.Users;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.test_jwt.security.SecurityConstants.EXPIRATION_TIME;
import static java.security.KeyRep.Type.SECRET;

@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication) {
        Users users = (Users) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());

        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String userId = Long.toString(users.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(users.getId())));
        claims.put("userName", users.getUsername());
        claims.put("passWord", users.getPassword());
        claims.put("roles", users.getRoles());
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, String.valueOf(SECRET))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(String.valueOf(SECRET)).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    } //Validate the token

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(String.valueOf(SECRET)).parseClaimsJws(token).getBody();
        String id = (String) claims.get("id");

        return Long.parseLong(id);
    } //Get user Id from token

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(String.valueOf(SECRET)).parseClaimsJws(token).getBody();
        String name = (String) claims.get("userName");

        return name;
    } //Get user name from token

}