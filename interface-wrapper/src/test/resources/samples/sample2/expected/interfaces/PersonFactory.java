package interfaces;

import converters.OptionalFromIntegerConverter;
import converters.OptionalFromStringConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

public class PersonFactory {

    private static class PersonImpl implements Person {

        public Optional<String> getNameIfAvailable() {
            return this.optionalFromStringConverter.convertFrom(underlyingPersonUnderlying.getNameIfAvailable());
        }

        public Optional<Integer> getAgeIfAvailable() {
            return this.optionalFromIntegerConverter.convertFrom(underlyingPersonUnderlying.getAgeIfAvailable());
        }

        final transient converters.OptionalFromIntegerConverter optionalFromIntegerConverter = new converters.OptionalFromIntegerConverter();

        final transient converters.OptionalFromStringConverter optionalFromStringConverter = new converters.OptionalFromStringConverter();

        final transient PersonUnderlying underlyingPersonUnderlying;

        public PersonImpl(PersonUnderlying underlyingPersonUnderlying) {
            this.underlyingPersonUnderlying = underlyingPersonUnderlying;
        }
    }

    public static Person wrapUnderlying(PersonUnderlying underlyingPersonUnderlying) {
        return new PersonImpl(underlyingPersonUnderlying);
    }
}
