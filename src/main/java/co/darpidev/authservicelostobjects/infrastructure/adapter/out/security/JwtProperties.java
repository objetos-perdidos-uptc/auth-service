package co.darpidev.authservicelostobjects.infrastructure.adapter.out.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuración tipada de jwt.secret y jwt.expiration (milisegundos). */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expiration) {
}
