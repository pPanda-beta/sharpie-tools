package converters;

import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

public class OptionalFromStringConverter implements TypeConverter<Optional<String>, String> {

    @Override public Optional<String> convertFrom(String original) {
        return Optional.ofNullable(original);
    }
}
