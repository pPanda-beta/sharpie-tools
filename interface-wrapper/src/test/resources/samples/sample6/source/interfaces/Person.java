package interfaces;

import converters.OptionalFromTConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@WrapperInterface(lazyReturnTypeConverters = OptionalFromTConverter.class)
public interface Person {
    Optional<String> getNameIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}
