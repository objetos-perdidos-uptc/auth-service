package co.darpidev.authservicelostobjects.application.port.out;

/** Puerto de salida: estrategia de hashing de contraseñas (BCrypt, Argon2, etc.). */
public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
