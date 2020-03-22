package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.common.collect.Streams;
import com.google.common.graph.Traverser;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

import static java.util.stream.Collectors.toList;

//TODO: Pseudo multiple-inheritance - inject behavior in classes
public interface AnnotationExtractionCapability extends ProcessingCapability {
    Traverser<AnnotationMirror> ANNOTATION_PARENT_GRAPH_TRAVERSER = Traverser
        .forGraph(mirror -> mirror.getAnnotationType().asElement().getAnnotationMirrors());

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

    default Set<AnnotationExpr> getCapturedAnnotations(List<? extends AnnotationMirror> startingAnnotationMirrors,
        Class<? extends Annotation> captorMarkerAnnotation) {
        Iterable<AnnotationMirror> allAnnotationMirrors = findAllAncestors(startingAnnotationMirrors);

        List<AnnotationMirror> captors = Streams.stream(allAnnotationMirrors)
            .filter(mirror -> isAnnotatedWith(mirror, captorMarkerAnnotation))
            .collect(Collectors.toList());

        return captors.stream()
            .map(mirror -> processingEnv().getElementUtils().getElementValuesWithDefaults(mirror).values())
            .flatMap(Collection::stream)
            .map(value -> StaticJavaParser.parseAnnotation(value.toString()))
            .collect(Collectors.toSet());
    }

    //TODO: This can be extracted to a single class
    default boolean isAnnotatedWith(AnnotationMirror mirror, Class<? extends Annotation> annotationType) {
        return Objects.nonNull(mirror.getAnnotationType()
            .asElement()
            .getAnnotation(annotationType));
    }

    //TODO: This can be extracted to a single class
    default Iterable<AnnotationMirror> findAllAncestors(List<? extends AnnotationMirror> annotationMirrors) {
        return ANNOTATION_PARENT_GRAPH_TRAVERSER.depthFirstPreOrder(annotationMirrors);
    }

}
