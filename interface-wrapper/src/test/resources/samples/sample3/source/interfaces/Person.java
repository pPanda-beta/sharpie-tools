package interfaces;

import annotations.OptionalStringWrapper;
import java.util.Optional;

@OptionalStringWrapper
public interface Person {
    Optional<String> getNameIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}
