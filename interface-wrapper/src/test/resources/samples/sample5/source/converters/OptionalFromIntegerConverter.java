package converters;

import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

public class OptionalFromIntegerConverter implements TypeConverter<Optional<Integer>, Integer> {

    @Override public Optional<Integer> convertFrom(Integer original) {
        return Optional.ofNullable(original);
    }
}
