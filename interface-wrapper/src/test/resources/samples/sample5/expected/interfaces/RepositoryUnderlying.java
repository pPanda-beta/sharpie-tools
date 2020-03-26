package interfaces;

import converters.OptionalFromIntegerConverter;
import converters.OptionalFromStringConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;


interface RepositoryUnderlying {

    interfaces.PersonUnderlying findPerson();
}
