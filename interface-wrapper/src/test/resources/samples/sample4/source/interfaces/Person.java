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

@OptionalStringWrapper
public interface Person {
    Optional<PersonName> getNameIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}

class PersonName {
    private final String firstName;
    private final String lastName;

    public PersonName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

class OptionalFromTConverter<T> implements TypeConverter<Optional<T>, T> {

    @Override public Optional<T> convertFrom(T original) {
        return Optional.ofNullable(original);
    }
}

@Retention(RetentionPolicy.CLASS)
@Target(TYPE)
@WrapperInterface(returnTypeConverters = OptionalFromTConverter.class)
@CustomAnnotationCaptor(resource = @Resource(name = "UnderlyingInterfaceAsResource"))
@interface OptionalStringWrapper {
}

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
@AnnotationCaptor
@interface CustomAnnotationCaptor {
    Resource resource();
}


