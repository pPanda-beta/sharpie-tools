package interfaces;

import converters.OptionalFromIntegerConverter;
import converters.OptionalFromStringConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@WrapperInterface(returnTypeConverters = {
    OptionalFromStringConverter.class,
    OptionalFromIntegerConverter.class
})
public interface Person {
    Optional<String> getNameIfAvailable();

    Optional<Integer> getAgeIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}
