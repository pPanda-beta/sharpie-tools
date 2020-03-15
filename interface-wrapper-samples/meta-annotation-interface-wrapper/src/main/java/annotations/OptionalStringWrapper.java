package annotations;

import converters.OptionalFromStringConverter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.CLASS)
@Target(TYPE)
@WrapperInterface(returnTypeConverters = OptionalFromStringConverter.class)
public @interface OptionalStringWrapper {
}
