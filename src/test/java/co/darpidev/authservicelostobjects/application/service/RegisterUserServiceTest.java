package co.darpidev.authservicelostobjects.application.service;

import co.darpidev.authservicelostobjects.application.port.in.RegisterUserUseCase.RegisterUserCommand;
import co.darpidev.authservicelostobjects.application.service.TestDoubles.FakePasswordHasher;
import co.darpidev.authservicelostobjects.application.service.TestDoubles.InMemoryUserRepository;
import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.Role;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.model.UserStatus;
import co.darpidev.authservicelostobjects.domain.shared.ErrorType;
import co.darpidev.authservicelostobjects.domain.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterUserServiceTest {

    private InMemoryUserRepository repository;
    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        Clock fixedClock = Clock.fixed(Instant.parse("2026-11-17T12:00:00Z"), ZoneOffset.UTC);
        service = new RegisterUserService(repository, new FakePasswordHasher(), fixedClock);
    }

    @Test
    void registersUserWithHashedPasswordAndDefaults() {
        Result<Void> result = service.register(
                new RegisterUserCommand("Ana", "ana@uptc.edu.co", "secret123", 3L, 7L));

        assertThat(result.isSuccess()).isTrue();
        User saved = repository.findByEmail(Email.create("ana@uptc.edu.co").getValue()).orElseThrow();
        assertThat(saved.getPasswordHash()).isEqualTo("hashed:secret123");
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
        assertThat(saved.getRegistrationDate()).isEqualTo(LocalDate.of(2026, 11, 17));
        assertThat(saved.getCampusId()).isEqualTo(3L);
        assertThat(saved.getUniversityId()).isEqualTo(7L);
    }

    @Test
    void failsWithConflictWhenEmailIsAlreadyRegistered() {
        service.register(new RegisterUserCommand("Ana", "ana@uptc.edu.co", "secret123", 1L, 1L));

        Result<Void> result = service.register(
                new RegisterUserCommand("Otra", "ana@uptc.edu.co", "secret456", 1L, 1L));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(UserErrors.emailAlreadyRegistered());
        assertThat(result.getError().type()).isEqualTo(ErrorType.CONFLICT);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void failsWithValidationErrorWhenEmailIsInvalid() {
        Result<Void> result = service.register(
                new RegisterUserCommand("Ana", "no-es-un-email", "secret123", 1L, 1L));

        assertThat(result.getError()).isEqualTo(UserErrors.invalidEmail());
        assertThat(repository.count()).isZero();
    }

    @Test
    void failsWithValidationErrorWhenNameIsBlank() {
        Result<Void> result = service.register(
                new RegisterUserCommand("  ", "ana@uptc.edu.co", "secret123", 1L, 1L));

        assertThat(result.getError()).isEqualTo(UserErrors.nameRequired());
        assertThat(repository.count()).isZero();
    }
}
