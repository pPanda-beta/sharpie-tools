package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Resource;
import ppanda.sharpie.tools.interfacewrapper.annotations.AnnotationCaptor;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
@AnnotationCaptor
public @interface CustomAnnotationCaptor {
    Resource resource();
}
