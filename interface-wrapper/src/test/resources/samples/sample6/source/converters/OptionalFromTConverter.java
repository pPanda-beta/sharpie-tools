package converters;

import java.util.Optional;
import java.util.concurrent.Callable;
import ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter;

public class OptionalFromTConverter<E> implements LazyTypeConverter<Optional<E>, E> {

    @Override public Optional<E> convertFrom(Callable<E> original) {
        try {
            return Optional.ofNullable(original.call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
