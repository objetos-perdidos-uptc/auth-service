package co.darpidev.authservicelostobjects.infrastructure.adapter.out.persistence;

import co.darpidev.authservicelostobjects.application.port.out.UserRepositoryPort;
import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.shared.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto del dominio usando Spring Data JPA.
 * Solo traduce a error de negocio las violaciones de la constraint de email;
 * cualquier otra violacion de integridad se propaga como excepcion para no
 * enmascarar problemas reales (NOT NULL, CHECK, FK, etc.).
 */
@Component
class UserPersistenceAdapter implements UserRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(UserPersistenceAdapter.class);

    private final SpringDataUserRepository repository;

    UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public Result<User> save(User user) {
        try {
            UserJpaEntity saved = repository.save(UserPersistenceMapper.toEntity(user));
            return Result.success(UserPersistenceMapper.toDomain(saved));
        } catch (DataIntegrityViolationException ex) {
            if (isEmailUniquenessViolation(ex)) {
                log.warn("Email duplicado al guardar usuario email={}", user.getEmail().value());
                return Result.failure(UserErrors.emailAlreadyRegistered());
            }
            throw ex;
        }
    }

    private static boolean isEmailUniquenessViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause.getMessage();
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("email") && (lower.contains("unique") || lower.contains("duplicate"));
    }
}