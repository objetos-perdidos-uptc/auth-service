package co.darpidev.authservicelostobjects.domain.model;

import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.util.Objects;

/**
 * Value Object del rol. Se modela como valor (y no como enum cerrado) porque
 * en la base de datos pueden existir roles asignados manualmente.
 */
public final class Role {

    public static final Role STUDENT = new Role("STUDENT");

    private final String value;

    private Role(String value) {
        this.value = value;
    }

    public static Result<Role> create(String raw) {
        if (raw == null || raw.isBlank()) {
            return Result.failure(UserErrors.roleRequired());
        }
        return Result.success(new Role(raw));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Role other && value.equals(other.value);
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
