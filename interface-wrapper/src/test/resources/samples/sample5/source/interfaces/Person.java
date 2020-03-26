package interfaces;

import converters.OptionalFromIntegerConverter;
import converters.OptionalFromStringConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@WrapperModel
public interface Person {
    Optional<String> getNameIfAvailable();

    Optional<Integer> getAgeIfAvailable();

    default boolean hasName() {
        return getNameIfAvailable().isPresent();
    }
}


@WrapperRepository
interface Repository {
    Person findPerson();
}

@WrapperInterface(returnTypeConverters = {
    OptionalFromStringConverter.class,
    OptionalFromIntegerConverter.class
})
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE) @interface WrapperModel {
}

@WrapperInterface(unwrapReturnTypesAnnotatedWith = WrapperModel.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE) @interface WrapperRepository {
}

