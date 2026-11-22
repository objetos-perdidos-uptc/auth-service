package co.darpidev.authservicelostobjects.domain.shared;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Patron Result (Railway Oriented Programming): una operacion que puede fallar por reglas
 * de negocio devuelve exito (con valor) o fallo (con {@link AppError}) sin lanzar excepciones.
 * Las excepciones quedan reservadas para fallos de infraestructura.
 */
public final class Result<T> {

    private final T value;
    private final AppError error;

    private Result(T value, AppError error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, AppError.NONE);
    }

    /** Exito sin valor de retorno. */
    public static Result<Void> success() {
        return new Result<>(null, AppError.NONE);
    }

    public static <T> Result<T> failure(AppError error) {
        Objects.requireNonNull(error, "error");
        if (error == AppError.NONE || error.type() == ErrorType.NONE) {
            throw new IllegalArgumentException("Un fallo requiere un error distinto de NONE");
        }
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error.type() == ErrorType.NONE;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("No se puede leer el valor de un Result fallido: " + error.code());
        }
        return value;
    }

    /** Devuelve el valor o {@code defaultValue} si hubo fallo. */
    public T getOrElse(T defaultValue) {
        return isSuccess() ? value : defaultValue;
    }

    /** Version perezosa de {@link #getOrElse}. */
    public T getOrElseGet(Supplier<? extends T> supplier) {
        return isSuccess() ? value : supplier.get();
    }

    public AppError getError() {
        return error;
    }

    /** Transforma el valor si hubo exito; propaga el error si no. */
    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        return isSuccess() ? Result.success(mapper.apply(value)) : Result.failure(error);
    }

    /** Encadena otra operacion que tambien puede fallar (flatMap). */
    public <R> Result<R> bind(Function<? super T, Result<R>> next) {
        return isSuccess() ? next.apply(value) : Result.failure(error);
    }

    /** Reemplaza el error si hubo fallo (por ejemplo, para no filtrar detalles al exterior). */
    public Result<T> mapError(Function<AppError, AppError> mapper) {
        return isFailure() ? Result.failure(mapper.apply(error)) : this;
    }

    public <R> R match(Function<? super T, ? extends R> onSuccess,
                       Function<AppError, ? extends R> onFailure) {
        return isSuccess() ? onSuccess.apply(value) : onFailure.apply(error);
    }

    /** Ejecuta la accion solo si hubo exito. Devuelve {@code this} para encadenar. */
    public Result<T> ifSuccess(Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(value);
        }
        return this;
    }

    /** Ejecuta la accion solo si hubo fallo. Devuelve {@code this} para encadenar. */
    public Result<T> ifFailure(Consumer<AppError> action) {
        if (isFailure()) {
            action.accept(error);
        }
        return this;
    }

    /** Alias semantico de {@link #ifSuccess}. */
    public Result<T> peek(Consumer<? super T> action) {
        return ifSuccess(action);
    }

    public Result<Void> asUnit() {
        return map(ignored -> null);
    }
}