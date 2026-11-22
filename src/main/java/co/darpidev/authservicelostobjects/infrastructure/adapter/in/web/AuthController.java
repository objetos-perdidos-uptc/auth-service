package co.darpidev.authservicelostobjects.infrastructure.adapter.in.web;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase;
import co.darpidev.authservicelostobjects.application.port.in.RegisterUserUseCase;
import co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto.AuthResponse;
import co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto.LoginRequest;
import co.darpidev.authservicelostobjects.infrastructure.adapter.in.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Adaptador de entrada HTTP: traduce HTTP <-> casos de uso y Result <-> ResponseEntity. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final AuthenticateUserUseCase authenticateUser;

    public AuthController(RegisterUserUseCase registerUser,
                          AuthenticateUserUseCase authenticateUser) {
        this.registerUser = registerUser;
        this.authenticateUser = authenticateUser;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        return registerUser.register(request.toCommand())
                .<ResponseEntity<Object>>match(
                        ignored -> ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente")),
                        ErrorHttpMapper::toResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        return authenticateUser.authenticate(request.toCommand())
                .<ResponseEntity<Object>>match(
                        result -> ResponseEntity.ok(AuthResponse.from(result)),
                        ErrorHttpMapper::toResponse);
    }
}
