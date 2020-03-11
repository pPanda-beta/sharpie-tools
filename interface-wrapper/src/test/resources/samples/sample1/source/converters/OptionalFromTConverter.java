package converters;

import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

public class OptionalFromTConverter<E> implements TypeConverter<Optional<E>, E> {

    @Override public Optional<E> convertFrom(E original) {
        return Optional.ofNullable(original);
    }
}
