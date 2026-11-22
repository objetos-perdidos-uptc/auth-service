package co.darpidev.authservicelostobjects.application.service;

import co.darpidev.authservicelostobjects.application.port.in.RegisterUserUseCase;
import co.darpidev.authservicelostobjects.application.port.out.PasswordHasherPort;
import co.darpidev.authservicelostobjects.application.port.out.UserRepositoryPort;
import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.time.Clock;
import java.time.LocalDate;

/** Caso de uso sin anotaciones de Spring: se instancia en el composition root. */
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final Clock clock;

    public RegisterUserService(UserRepositoryPort userRepository,
                               PasswordHasherPort passwordHasher,
                               Clock clock) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    @Override
    public Result<Void> register(RegisterUserCommand command) {
        return Email.create(command.email())
                .bind(this::ensureEmailIsAvailable)
                .bind(email -> createUser(email, command))
                .bind(userRepository::save)
                .asUnit();
    }

    private Result<Email> ensureEmailIsAvailable(Email email) {
        if (userRepository.existsByEmail(email)) {
            return Result.failure(UserErrors.emailAlreadyRegistered());
        }
        return Result.success(email);
    }

    private Result<User> createUser(Email email, RegisterUserCommand command) {
        return User.register(
                command.name(),
                email,
                passwordHasher.hash(command.rawPassword()),
                command.campusId(),
                command.universityId(),
                LocalDate.now(clock));
    }
}
