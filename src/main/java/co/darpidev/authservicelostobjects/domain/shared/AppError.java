package co.darpidev.authservicelostobjects.domain.shared;

/** Error estructurado (no un simple String): código estable, mensaje legible y tipo. */
public record AppError(String code, String message, ErrorType type) {

    public static final AppError NONE = new AppError("", "", ErrorType.NONE);
}
