package co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase.AuthenticateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {

    public AuthenticateUserCommand toCommand() {
        return new AuthenticateUserCommand(email, password);
    }
}
