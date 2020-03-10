package interfaces;

import converters.OptionalFromStringConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@WrapperInterface(returnTypeConverters = OptionalFromStringConverter.class)
public interface Person {
    Optional<String> getNameIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}
