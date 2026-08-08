package dev.pczigany.newisgood.api;

import java.util.List;

/**
 * Framework-neutral request validation gateway. Implementations
 * inspect the bean and return a list of human-readable violation
 * messages; empty list means "valid".
 */
public interface ValidationSupport {

    <T> List<String> validate(T bean);
}
