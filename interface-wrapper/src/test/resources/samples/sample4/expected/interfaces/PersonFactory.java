package interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import javax.annotation.Resource;
import ppanda.sharpie.tools.interfacewrapper.annotations.AnnotationCaptor;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

public class PersonFactory {

    private static class PersonImpl implements Person {

        public Optional<PersonName> getNameIfAvailable() {
            return this.optionalFromTConverter.convertFrom(underlyingPersonUnderlying.getNameIfAvailable());
        }

        final transient interfaces.OptionalFromTConverter optionalFromTConverter = new interfaces.OptionalFromTConverter();

        final transient PersonUnderlying underlyingPersonUnderlying;

        public PersonImpl(PersonUnderlying underlyingPersonUnderlying) {
            this.underlyingPersonUnderlying = underlyingPersonUnderlying;
        }
    }

    public static Person wrapUnderlying(PersonUnderlying underlyingPersonUnderlying) {
        return new PersonImpl(underlyingPersonUnderlying);
    }
}
