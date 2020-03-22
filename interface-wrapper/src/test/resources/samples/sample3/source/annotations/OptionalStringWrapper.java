package annotations;

import converters.OptionalFromStringConverter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Resource;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.CLASS)
@Target(TYPE)
@WrapperInterface(returnTypeConverters = OptionalFromStringConverter.class)
@CustomAnnotationCaptor(resource = @Resource(name = "UnderlyingInterfaceAsResource"))
public @interface OptionalStringWrapper {
}
