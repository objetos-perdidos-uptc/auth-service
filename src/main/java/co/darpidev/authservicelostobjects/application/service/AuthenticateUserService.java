package co.darpidev.authservicelostobjects.application.service;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase;
import co.darpidev.authservicelostobjects.application.port.out.PasswordHasherPort;
import co.darpidev.authservicelostobjects.application.port.out.TokenGeneratorPort;
import co.darpidev.authservicelostobjects.application.port.out.UserRepositoryPort;
import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.shared.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserService.class);

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenGeneratorPort tokenGenerator;

    public AuthenticateUserService(UserRepositoryPort userRepository,
                                   PasswordHasherPort passwordHasher,
                                   TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Result<AuthenticationResult> authenticate(AuthenticateUserCommand command) {
        return Email.create(command.email())
                .mapError(ignored -> UserErrors.invalidCredentials())
                .bind(this::findUser)
                .bind(user -> verifyCredentials(user, command.rawPassword()))
                .map(this::toAuthenticationResult)
                .peek(result -> log.info("Login exitoso userId={} email={}",
                        result.userId(), result.email()));
    }

    private Result<User> findUser(Email email) {
        return userRepository.findByEmail(email)
                .map(Result::success)
                .orElseGet(() -> {
                    log.info("Login fallido: email desconocido email={}", email.value());
                    return Result.failure(UserErrors.invalidCredentials());
                });
    }

    private Result<User> verifyCredentials(User user, String rawPassword) {
        if (passwordHasher.matches(rawPassword, user.getPasswordHash()) && user.canAuthenticate()) {
            return Result.success(user);
        }
        log.warn("Login fallido: credenciales invalidas o usuario no activo userId={}", user.getId());
        return Result.failure(UserErrors.invalidCredentials());
    }

    private AuthenticationResult toAuthenticationResult(User user) {
        return new AuthenticationResult(
                tokenGenerator.generate(user),
                user.getId(),
                user.getEmail().value(),
                user.getRole().value());
    }
}