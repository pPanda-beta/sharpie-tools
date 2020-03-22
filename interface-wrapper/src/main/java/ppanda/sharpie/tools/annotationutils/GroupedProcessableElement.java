package ppanda.sharpie.tools.annotationutils;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Symbol;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class GroupedProcessableElement {
    private final Element element;
    private final Set<List<AnnotationMirror>> allPaths;

    public GroupedProcessableElement(Element element,
        Set<List<AnnotationMirror>> allPaths) {
        this.element = element;
        this.allPaths = allPaths;
    }

    public Set<AnnotationMirror> getSubstitutedAnnotationMirrors() {
        return extractFromPath(path -> Iterables.getFirst(path, null));
    }

    public Set<AnnotationMirror> getTriggeringAnnotationMirrors() {
        return extractFromPath(Iterables::getLast);
    }

    public Set<String> getTriggeringAnnotationNames() {
        return getTriggeringAnnotationMirrors()
            .stream()
            .map(mirror -> ((Symbol.ClassSymbol) mirror.getAnnotationType().asElement())
                .fullname.toString())
            .collect(Collectors.toSet());
    }

    private Set<AnnotationMirror> extractFromPath(Function<List<AnnotationMirror>, AnnotationMirror> extractor) {
        return allPaths.stream()
            .map(extractor)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public Element getElement() {
        return element;
    }

    public Set<List<AnnotationMirror>> getAllPathsFromOriginalToMeta() {
        return allPaths;
    }

    public static List<GroupedProcessableElement> from(List<ProcessableElement> processableElements) {
        return processableElements
            .stream()
            .collect(groupingBy(ProcessableElement::getElement,
                mapping(ProcessableElement::getPathFromOriginalToMeta, toSet())))
            .entrySet()
            .stream()
            .map(entry -> new GroupedProcessableElement(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override public String toString() {
        return "GroupedProcessableElement{" +
            "element=" + element +
            ", allPaths=" + allPaths +
            '}';
    }
}
