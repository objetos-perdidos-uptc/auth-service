package co.darpidev.authservicelostobjects.application.port.out;

import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.util.Optional;

/**
 * Puerto de salida (patrón Repository) expresado en lenguaje del dominio, no de JPA.
 * {@code save} devuelve Result porque la unicidad del email es una regla de negocio
 * que también puede violarse por concurrencia.
 */
public interface UserRepositoryPort {

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Result<User> save(User user);
}
