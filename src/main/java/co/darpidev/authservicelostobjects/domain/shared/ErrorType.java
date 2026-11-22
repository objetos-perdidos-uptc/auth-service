package co.darpidev.authservicelostobjects.domain.shared;

/** Categoría del error de negocio. El adaptador de entrada la traduce a un código de estado concreto. */
public enum ErrorType {
    NONE,
    VALIDATION,
    NOT_FOUND,
    CONFLICT,
    UNAUTHORIZED
}
