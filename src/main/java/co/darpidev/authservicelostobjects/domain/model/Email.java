package co.darpidev.authservicelostobjects.domain.model;

import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.util.Locale;
import java.util.Objects;

/**
 * Value Object inmutable. Solo se construye mediante {@link #create}, que devuelve un Result
 * en lugar de lanzar excepciones.
 *
 * El email se normaliza a minusculas y se recorta antes de validar. La normalizacion
 * evita que {@code Ana@x.com} y {@code ana@x.com} convivan como usuarios distintos en una
 * columna UNIQUE case-sensitive (PostgreSQL por defecto). Si en el futuro hay emails
 * almacenados con mayusculas, la migracion debe normalizarlos antes.
 */
public final class Email {

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Result<Email> create(String raw) {
        if (raw == null) {
            return Result.failure(UserErrors.invalidEmail());
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || !normalized.contains("@")) {
            return Result.failure(UserErrors.invalidEmail());
        }
        return Result.success(new Email(normalized));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Email other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}