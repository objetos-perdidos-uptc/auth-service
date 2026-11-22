package co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto;

import co.darpidev.authservicelostobjects.application.port.in.RegisterUserUseCase.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        Long campusId,
        Long universityId) {

    /** Traduce el DTO HTTP al Command de la capa de aplicación. */
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
                name,
                email,
                password,
                campusId == null ? 0L : campusId,
                universityId == null ? 0L : universityId);
    }
}
