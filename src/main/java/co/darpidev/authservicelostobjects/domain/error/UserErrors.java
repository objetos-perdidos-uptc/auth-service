package co.darpidev.authservicelostobjects.domain.error;

import co.darpidev.authservicelostobjects.domain.shared.AppError;
import co.darpidev.authservicelostobjects.domain.shared.ErrorType;

/** Catálogo de errores del dominio de usuarios. */
public final class UserErrors {

    private UserErrors() {
    }

    public static AppError emailAlreadyRegistered() {
        return new AppError("User.EmailAlreadyRegistered", "El email ya está registrado", ErrorType.CONFLICT);
    }

    /** Mismo error para "usuario inexistente" y "contraseña incorrecta" para no revelar qué correos existen. */
    public static AppError invalidCredentials() {
        return new AppError("Auth.InvalidCredentials", "Credenciales inválidas", ErrorType.UNAUTHORIZED);
    }

    public static AppError invalidEmail() {
        return new AppError("User.InvalidEmail", "El email no es válido", ErrorType.VALIDATION);
    }

    public static AppError nameRequired() {
        return new AppError("User.NameRequired", "El nombre no puede estar vacío", ErrorType.VALIDATION);
    }

    public static AppError roleRequired() {
        return new AppError("User.RoleRequired", "El rol no puede estar vacío", ErrorType.VALIDATION);
    }

    public static AppError passwordHashRequired() {
        return new AppError("User.PasswordHashRequired", "El hash de la contraseña es obligatorio", ErrorType.VALIDATION);
    }
}
