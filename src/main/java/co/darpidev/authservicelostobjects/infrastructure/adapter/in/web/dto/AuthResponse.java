package co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase.AuthenticationResult;

/** Mismo contrato JSON que antes: token, tokenType, userId, email, role. */
public record AuthResponse(String token,
                           String tokenType,
                           Long userId,
                           String email,
                           String role) {

    private static final String BEARER = "Bearer";

    public static AuthResponse from(AuthenticationResult result) {
        return new AuthResponse(result.token(), BEARER, result.userId(), result.email(), result.role());
    }
}
