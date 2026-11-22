package co.darpidev.authservicelostobjects.application.port.out;

import co.darpidev.authservicelostobjects.domain.model.User;

/** Puerto de salida: emisión de tokens de acceso (JWT hoy, otro mecanismo mañana). */
public interface TokenGeneratorPort {

    String generate(User user);
}
