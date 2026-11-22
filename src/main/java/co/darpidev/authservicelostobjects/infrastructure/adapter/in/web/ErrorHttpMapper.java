package co.darpidev.authservicelostobjects.infrastructure.adapter.in.web;

import co.darpidev.authservicelostobjects.domain.shared.AppError;
import co.darpidev.authservicelostobjects.domain.shared.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/** Única pieza que conoce la relación entre tipos de error de negocio y códigos HTTP. */
final class ErrorHttpMapper {

    private ErrorHttpMapper() {
    }

    static HttpStatus toStatus(ErrorType type) {
        return switch (type) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case NONE -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    static ResponseEntity<Object> toResponse(AppError error) {
        return ResponseEntity.status(toStatus(error.type()))
                .body(Map.of("message", error.message(), "code", error.code()));
    }
}
