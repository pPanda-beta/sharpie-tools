package ppanda.sharpie.tools.interfacewrapper.annotations;

import ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WrapperInterface {
    Class<? extends TypeConverter>[] returnTypeConverters() default {};

    Class<? extends LazyTypeConverter>[] lazyReturnTypeConverters() default {};

    Class<? extends Annotation>[] unwrapReturnTypesAnnotatedWith() default {};
}
