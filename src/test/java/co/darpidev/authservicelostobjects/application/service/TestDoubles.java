package co.darpidev.authservicelostobjects.application.service;

import co.darpidev.authservicelostobjects.application.port.out.PasswordHasherPort;
import co.darpidev.authservicelostobjects.application.port.out.TokenGeneratorPort;
import co.darpidev.authservicelostobjects.application.port.out.UserRepositoryPort;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Fakes en memoria de los puertos de salida: permiten probar los casos de uso sin infraestructura. */
final class TestDoubles {

    private TestDoubles() {
    }

    static class InMemoryUserRepository implements UserRepositoryPort {
        private final Map<String, User> users = new HashMap<>();
        private final AtomicLong sequence = new AtomicLong(1);

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public Result<User> save(User user) {
            User persisted = User.reconstitute(
                    sequence.getAndIncrement(), user.getName(), user.getEmail(),
                    user.getPasswordHash(), user.getStatus(), user.getRegistrationDate(),
                    user.getCampusId(), user.getUniversityId(), user.getRole());
            users.put(persisted.getEmail().value(), persisted);
            return Result.success(persisted);
        }

        int count() {
            return users.size();
        }
    }

    static class FakePasswordHasher implements PasswordHasherPort {
        @Override
        public String hash(String rawPassword) {
            return "hashed:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return hash(rawPassword).equals(hashedPassword);
        }
    }

    static class FakeTokenGenerator implements TokenGeneratorPort {
        @Override
        public String generate(User user) {
            return "token-for-" + user.getEmail().value();
        }
    }
}
