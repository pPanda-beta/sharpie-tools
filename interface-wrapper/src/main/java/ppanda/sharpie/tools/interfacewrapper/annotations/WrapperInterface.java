package ppanda.sharpie.tools.interfacewrapper.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WrapperInterface {
    Class<? extends TypeConverter>[] returnTypeConverters() default {};

    Class<? extends LazyTypeConverter>[] lazyReturnTypeConverters() default {};

    Class<? extends Annotation>[] unwrapReturnTypesAnnotatedWith() default {};
}
