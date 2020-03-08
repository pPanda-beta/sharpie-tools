package ppanda.sharpie.tools.interfacewrapper.processors;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

//TODO: Pseudo multiple-inheritance - inject behavior in classes
public interface AnnotationFieldExtractionCapability extends ProcessingCapability {

    default <T> T extract(Element element, Class<? extends Annotation> annotationClass, String fieldName) {
        return null;
    }

    default <T> List<T> extractMultipleValue(
        Element element,
        Class<? extends Annotation> annotationClass,
        String fieldName) {
        return MoreElements.getAnnotationMirror(element, annotationClass)
            .toJavaUtil()
            .map(annotationMirror -> AnnotationMirrors.getAnnotationValue(annotationMirror, fieldName))
            .map(value -> (List<AnnotationValue>) value.getValue())
            .map(Collection::stream)
            .get()
            .map(value -> (T) value.getValue())
            .collect(Collectors.toList());
    }
}
