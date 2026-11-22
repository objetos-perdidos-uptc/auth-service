package co.darpidev.authservicelostobjects.application.port.in;

import co.darpidev.authservicelostobjects.domain.shared.Result;

/** Puerto de entrada: autenticar a un usuario y emitir un token de acceso. */
public interface AuthenticateUserUseCase {

    Result<AuthenticationResult> authenticate(AuthenticateUserCommand command);

    record AuthenticateUserCommand(String email, String rawPassword) {
    }

    record AuthenticationResult(String token, long userId, String email, String role) {
    }
}
