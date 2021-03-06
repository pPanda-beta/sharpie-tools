package interfaces;

import annotations.OptionalStringWrapper;
import java.util.Optional;

public class PersonFactory {

    private static class PersonImpl implements Person {

        public Optional<String> getNameIfAvailable() {
            return this.optionalFromStringConverter.convertFrom(underlyingPersonUnderlying.getNameIfAvailable());
        }

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
