package interfaces;

import converters.OptionalFromTConverter;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

public class PersonFactory {

    private static class PersonImpl implements Person {

        public Optional<String> getNameIfAvailable() {
            return this.optionalFromTConverter.convertFrom(() -> underlyingPersonUnderlying.getNameIfAvailable());
        }

        final transient converters.OptionalFromTConverter optionalFromTConverter = new converters.OptionalFromTConverter();

        final transient PersonUnderlying underlyingPersonUnderlying;

        public PersonImpl(PersonUnderlying underlyingPersonUnderlying) {
            this.underlyingPersonUnderlying = underlyingPersonUnderlying;
        }
    }

    public static Person wrapUnderlying(PersonUnderlying underlyingPersonUnderlying) {
        return new PersonImpl(underlyingPersonUnderlying);
    }
}
