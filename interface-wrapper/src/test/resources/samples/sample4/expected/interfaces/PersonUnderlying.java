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


@javax.annotation.Resource(name = "UnderlyingInterfaceAsResource")
public interface PersonUnderlying {

    interfaces.PersonName getNameIfAvailable();
}
