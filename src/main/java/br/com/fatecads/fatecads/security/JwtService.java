package br.com.fatecads.fatecads.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
    private final JwtProperties properties;
    private Key signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must have at least 32 characters.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generatePasswordResetToken(Usuario usuario) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + properties.getExpirationMinutes() * 60_000L);

        return Jwts.builder()
                .setIssuer(properties.getIssuer())
                .setSubject(String.valueOf(usuario.getIdUsuario()))
                .claim("email", usuario.getEmailUsuario())
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .requireIssuer(properties.getIssuer())
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }
}
