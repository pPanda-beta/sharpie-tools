package interfaces;

import annotations.OptionalStringWrapper;
import converters.DoubleFromFloatConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@OptionalStringWrapper
@WrapperInterface(returnTypeConverters = DoubleFromFloatConverter.class)
public interface Person {
    Optional<String> getNameIfAvailable();

    Double getAge();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}
