package co.darpidev.authservicelostobjects.application.service;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase.AuthenticateUserCommand;
import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase.AuthenticationResult;
import co.darpidev.authservicelostobjects.application.service.TestDoubles.FakePasswordHasher;
import co.darpidev.authservicelostobjects.application.service.TestDoubles.FakeTokenGenerator;
import co.darpidev.authservicelostobjects.application.service.TestDoubles.InMemoryUserRepository;
import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.Role;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.model.UserStatus;
import co.darpidev.authservicelostobjects.domain.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticateUserServiceTest {

    private AuthenticateUserService service;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        service = new AuthenticateUserService(repository, new FakePasswordHasher(), new FakeTokenGenerator());
        repository.save(userWithStatus("ana@uptc.edu.co", UserStatus.ACTIVE));
        repository.save(userWithStatus("blocked@uptc.edu.co", UserStatus.BLOCKED));
    }

    private User userWithStatus(String email, UserStatus status) {
        return User.reconstitute(null, "Ana", Email.create(email).getValue(), "hashed:secret123", status,
                LocalDate.of(2026, 1, 1), 1L, 1L, Role.STUDENT);
    }

    @Test
    void returnsTokenAndUserDataWhenCredentialsAreValid() {
        Result<AuthenticationResult> result =
                service.authenticate(new AuthenticateUserCommand("ana@uptc.edu.co", "secret123"));

        assertThat(result.isSuccess()).isTrue();
        AuthenticationResult auth = result.getValue();
        assertThat(auth.token()).isEqualTo("token-for-ana@uptc.edu.co");
        assertThat(auth.email()).isEqualTo("ana@uptc.edu.co");
        assertThat(auth.role()).isEqualTo("STUDENT");
        assertThat(auth.userId()).isPositive();
    }

    @Test
    void failsWithWrongPassword() {
        Result<AuthenticationResult> result =
                service.authenticate(new AuthenticateUserCommand("ana@uptc.edu.co", "incorrecta"));

        assertThat(result.getError()).isEqualTo(UserErrors.invalidCredentials());
    }

    @Test
    void failsWithSameErrorForUnknownUser() {
        Result<AuthenticationResult> result =
                service.authenticate(new AuthenticateUserCommand("nadie@uptc.edu.co", "secret123"));

        assertThat(result.getError()).isEqualTo(UserErrors.invalidCredentials());
    }

    @Test
    void doesNotRevealEmailFormatProblemsAtLogin() {
        Result<AuthenticationResult> result =
                service.authenticate(new AuthenticateUserCommand("sin-arroba", "secret123"));

        assertThat(result.getError()).isEqualTo(UserErrors.invalidCredentials());
    }

    @Test
    void rejectsNonActiveUsers() {
        Result<AuthenticationResult> result =
                service.authenticate(new AuthenticateUserCommand("blocked@uptc.edu.co", "secret123"));

        assertThat(result.getError()).isEqualTo(UserErrors.invalidCredentials());
    }
}
