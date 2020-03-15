package ppanda.sharpie.tools.interfacewrapper.processors;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

import static java.util.stream.Collectors.toList;

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
            .map(annotationMirror -> this.<T>extractMultipleValue(annotationMirror, fieldName))
            .get();
    }

    default <T> List<T> extractMultipleValue(AnnotationMirror mirror, String fieldName) {
        AnnotationValue field = AnnotationMirrors.getAnnotationValue(mirror, fieldName);
        List<AnnotationValue> values = (List<AnnotationValue>) field.getValue();

        return values.stream()
            .map(value -> (T) value.getValue())
            .collect(toList());
    }
}
