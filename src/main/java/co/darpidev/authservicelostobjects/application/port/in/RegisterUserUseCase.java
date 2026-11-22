package co.darpidev.authservicelostobjects.application.port.in;

import co.darpidev.authservicelostobjects.domain.shared.Result;

/** Puerto de entrada: registrar un usuario nuevo. */
public interface RegisterUserUseCase {

    Result<Void> register(RegisterUserCommand command);

    /** Command: encapsula la intención y sus datos, sin detalles de transporte (HTTP, JSON). */
    record RegisterUserCommand(String name,
                               String email,
                               String rawPassword,
                               long campusId,
                               long universityId) {
    }
}
