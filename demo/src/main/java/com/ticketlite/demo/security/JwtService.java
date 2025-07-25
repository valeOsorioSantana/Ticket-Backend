package com.ticketlite.demo.security;

import com.ticketlite.demo.model.UsersEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generar token con roles
    public String generateToken(UsersEntity user, String name, String email) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", name);
        claims.put("email", email);
        claims.put("Rol", user.getRole());
        claims.put("id", user.getId());

        //console.log(localStorage.getItem("token"));

        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        Set<String> nameSet = name == null || name.isEmpty()
                ? Set.of()
                : Set.of(name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    // Método para generar token desde UserDetails si quieres usarlo
    /*public String generateToken(UserDetails user) {
        Set<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return generateToken(user.getUsername(), roles);
    }*/
    // Obtener username (email)
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    // Extraer roles del token
    public Set<String> extractRoles(String token) {
        Claims claims = (Claims) extractClaims(token).get("roles");
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) &&
                !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}