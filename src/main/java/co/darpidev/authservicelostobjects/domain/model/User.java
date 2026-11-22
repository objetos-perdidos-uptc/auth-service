package co.darpidev.authservicelostobjects.domain.model;

import co.darpidev.authservicelostobjects.domain.error.UserErrors;
import co.darpidev.authservicelostobjects.domain.shared.Result;

import java.time.LocalDate;

/**
 * Entidad de dominio pura: sin anotaciones de JPA, Spring ni Lombok. Es inmutable y solo se crea
 * mediante Factory Methods: {@link #register} (alta nueva; valida y devuelve un Result) y
 * {@link #reconstitute} (rehidratación de datos ya confiables desde persistencia).
 */
public final class User {

    private final Long id;
    private final String name;
    private final Email email;
    private final String passwordHash;
    private final UserStatus status;
    private final LocalDate registrationDate;
    private final long campusId;
    private final long universityId;
    private final Role role;

    private User(Long id, String name, Email email, String passwordHash, UserStatus status,
                 LocalDate registrationDate, long campusId, long universityId, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.registrationDate = registrationDate;
        this.campusId = campusId;
        this.universityId = universityId;
        this.role = role;
    }

    /** Factory Method: alta de un usuario nuevo con los valores por defecto del negocio. */
    public static Result<User> register(String name, Email email, String passwordHash,
                                        long campusId, long universityId, LocalDate today) {
        if (name == null || name.isBlank()) {
            return Result.failure(UserErrors.nameRequired());
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            return Result.failure(UserErrors.passwordHashRequired());
        }
        return Result.success(new User(null, name, email, passwordHash, UserStatus.ACTIVE,
                today, campusId, universityId, Role.STUDENT));
    }

    /** Factory Method: reconstruye un usuario ya existente (uso de los adaptadores de persistencia). */
    public static User reconstitute(Long id, String name, Email email, String passwordHash,
                                    UserStatus status, LocalDate registrationDate,
                                    long campusId, long universityId, Role role) {
        return new User(id, name, email, passwordHash, status,
                registrationDate, campusId, universityId, role);
    }

    /** Regla de negocio: solo los usuarios activos pueden iniciar sesión. */
    public boolean canAuthenticate() {
        return status == UserStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserStatus getStatus() { return status; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public long getCampusId() { return campusId; }
    public long getUniversityId() { return universityId; }
    public Role getRole() { return role; }
}
