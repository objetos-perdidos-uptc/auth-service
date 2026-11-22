package co.darpidev.authservicelostobjects.domain.shared;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultTest {

    private static final AppError BOOM = new AppError("Test.Boom", "boom", ErrorType.VALIDATION);

    @Test
    void successCarriesValueAndNoError() {
        Result<Integer> result = Result.success(42);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getValue()).isEqualTo(42);
        assertThat(result.getError()).isEqualTo(AppError.NONE);
    }

    @Test
    void failureExposesErrorAndRefusesValueAccess() {
        Result<Integer> result = Result.failure(BOOM);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(BOOM);
        assertThatThrownBy(result::getValue).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void failureCannotBeBuiltWithNoneError() {
        assertThatThrownBy(() -> Result.failure(AppError.NONE)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bindShortCircuitsOnFailure() {
        AtomicInteger calls = new AtomicInteger();

        Result<Integer> result = Result.<Integer>failure(BOOM).bind(v -> {
            calls.incrementAndGet();
            return Result.success(v + 1);
        });

        assertThat(result.getError()).isEqualTo(BOOM);
        assertThat(calls).hasValue(0);
    }

    @Test
    void mapAndBindChainOnSuccess() {
        Result<String> result = Result.success(2)
                .map(v -> v * 10)
                .bind(v -> Result.success("n=" + v));

        assertThat(result.getValue()).isEqualTo("n=20");
    }

    @Test
    void mapErrorReplacesOnlyFailures() {
        AppError other = new AppError("Test.Other", "other", ErrorType.UNAUTHORIZED);

        assertThat(Result.<Integer>failure(BOOM).mapError(e -> other).getError()).isEqualTo(other);
        assertThat(Result.success(1).mapError(e -> other).isSuccess()).isTrue();
    }

    @Test
    void matchFoldsBothBranches() {
        assertThat(Result.success(1).match(v -> "ok", e -> "ko")).isEqualTo("ok");
        assertThat(Result.<Integer>failure(BOOM).match(v -> "ok", e -> e.code())).isEqualTo("Test.Boom");
    }
}
