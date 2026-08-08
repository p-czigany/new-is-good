package dev.pczigany.newisgood.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

/**
 * {@link ValidationSupport} backed by a Jakarta Bean Validation
 * {@link Validator}. Kept small and framework-neutral so callers
 * don't need to know about the underlying provider.
 */
public final class JakartaValidationSupport implements ValidationSupport {

    private final Validator validator;

    public JakartaValidationSupport(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> List<String> validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        return violations.stream()
                .map(this::describe)
                .sorted()
                .toList();
    }

    private <T> String describe(ConstraintViolation<T> violation) {
        String path = violation.getPropertyPath().toString();
        if (path.isEmpty()) {
            return violation.getMessage();
        }
        return path + " " + violation.getMessage();
    }
}
