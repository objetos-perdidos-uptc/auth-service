package co.darpidev.authservicelostobjects.infrastructure.adapter.out.security;

import co.darpidev.authservicelostobjects.application.port.out.TokenGeneratorPort;
import co.darpidev.authservicelostobjects.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Adaptador de salida que emite JWT firmados con HS256.
 * Los claims (subject, userId, role, campusId, universityId) son los mismos que
 * consume el api-gateway, por lo que el contrato entre servicios no cambia.
 *
 * El secreto debe tener al menos 32 bytes (256 bits) para HS256; si no, JJWT
 * lanza WeakKeyException en tiempo de firma, lo cual es tardio y confuso.
 * Se valida al construir el bean para fallar rapido al arrancar.
 */
@Component
class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationMillis;

    JwtTokenGeneratorAdapter(JwtProperties properties) {
        String secret = properties.secret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos " + MIN_SECRET_BYTES + " bytes (256 bits) para HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = properties.expiration();
    }

    @Override
    public String generate(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(user.getEmail().value())
                .claim("userId", user.getId())
                .claim("role", user.getRole().value())
                .claim("campusId", user.getCampusId())
                .claim("universityId", user.getUniversityId())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }
}