package ppanda.sharpie.tools.annotationutils;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import static java.util.Collections.emptyList;

public class AnnotationTree {
    private final TypeElement typeElement;
    private final Set<AnnotationTree> children;
    private final Set<ProcessableElement> processableElements;

    public AnnotationTree(TypeElement typeElement,
        Set<AnnotationTree> children,
        Set<ProcessableElement> processableElements) {
        this.typeElement = typeElement;
        this.children = children;
        this.processableElements = processableElements;
    }

    public List<GroupedProcessableElement> findAllMergedProcessableElements() {
        return GroupedProcessableElement.from(findAllProcessableElements());
    }

    public List<ProcessableElement> findAllProcessableElements() {
        Stream<ProcessableElement> elementsReachableViaChildren = children.stream()
            .map(AnnotationTree::findAllProcessableElements)
            .flatMap(Collection::stream);
        return Stream.concat(processableElements.stream(), elementsReachableViaChildren)
            .collect(Collectors.toList());
    }

    public static AnnotationTree of(Class<?> root,
        RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        TypeElement rootElement = processingEnv.getElementUtils()
            .getTypeElement(root.getCanonicalName());
        return depthFirstVisit(rootElement, roundEnv);
    }

    private static AnnotationTree depthFirstVisit(TypeElement annotation, RoundEnvironment roundEnv) {
        return depthFirstVisit(annotation, new HashSet<>(), emptyList(), roundEnv);
    }

    private static AnnotationTree depthFirstVisit(TypeElement annotation,
        Set<Element> visited,
        List<AnnotationMirror> annotationsSoFar, RoundEnvironment roundEnv) {

        if (visited.contains(annotation)) {
            return null;
        }
        visited.add(annotation);

        Set<ProcessableElement> processableElements = collectProcessableElements(annotation, annotationsSoFar, roundEnv);

        Set<AnnotationTree> children = findChildAnnotations(annotation, roundEnv)
            .stream()
            .map(child -> depthFirstVisit(child, visited,
                withAnnotationMirrorsOnElement(annotationsSoFar, child, annotation),
                roundEnv))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return new AnnotationTree(annotation, children, processableElements);
    }

    private static List<TypeElement> findChildAnnotations(TypeElement annotation, RoundEnvironment roundEnv) {
        return roundEnv
            .getElementsAnnotatedWith(annotation)
            .stream()
            .filter(child -> child instanceof TypeElement)
            .map(child -> (TypeElement) child)
            .filter(child -> child.getKind() == ElementKind.ANNOTATION_TYPE)
            .collect(Collectors.toList());
    }

    private static Set<ProcessableElement> collectProcessableElements(TypeElement annotation,
        List<AnnotationMirror> annotationsSoFar,
        RoundEnvironment roundEnv) {
        return roundEnv
            .getElementsAnnotatedWith(annotation)
            .stream()
            .map(element -> new ProcessableElement(element,
                withAnnotationMirrorsOnElement(annotationsSoFar, element, annotation)))
            .collect(Collectors.toSet());
    }

    private static List<AnnotationMirror> withAnnotationMirrorsOnElement(
        List<AnnotationMirror> annotationsSoFar, Element element, TypeElement annotation) {
        AnnotationMirror annotationMirror = Iterables.find(
            element.getAnnotationMirrors(), mirror -> isOfSameType(annotation, mirror));
        return Stream.concat(annotationsSoFar.stream(), Stream.of(annotationMirror))
            .collect(Collectors.toList());
    }

    private static boolean isOfSameType(TypeElement annotation, AnnotationMirror mirror) {
        return mirror.getAnnotationType()
            .asElement()
            .getSimpleName()
            .equals(annotation.getSimpleName());
    }

    @Override public String toString() {
        return "AnnotationTree{" +
            "typeElement=" + typeElement +
            ", children=" + children +
            ", processableElements=" + processableElements +
            '}';
    }
}
