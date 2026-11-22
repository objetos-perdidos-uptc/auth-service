package co.darpidev.authservicelostobjects.infrastructure.config;

import co.darpidev.authservicelostobjects.application.port.in.AuthenticateUserUseCase;
import co.darpidev.authservicelostobjects.application.port.in.RegisterUserUseCase;
import co.darpidev.authservicelostobjects.application.port.out.PasswordHasherPort;
import co.darpidev.authservicelostobjects.application.port.out.TokenGeneratorPort;
import co.darpidev.authservicelostobjects.application.port.out.UserRepositoryPort;
import co.darpidev.authservicelostobjects.application.service.AuthenticateUserService;
import co.darpidev.authservicelostobjects.application.service.RegisterUserService;
import co.darpidev.authservicelostobjects.infrastructure.adapter.out.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Composition root: único lugar donde se ensamblan los casos de uso con sus puertos.
 * Así la capa de aplicación permanece libre de anotaciones de Spring.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepository,
                                                   PasswordHasherPort passwordHasher,
                                                   Clock clock) {
        return new RegisterUserService(userRepository, passwordHasher, clock);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepositoryPort userRepository,
                                                           PasswordHasherPort passwordHasher,
                                                           TokenGeneratorPort tokenGenerator) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenGenerator);
    }
}
