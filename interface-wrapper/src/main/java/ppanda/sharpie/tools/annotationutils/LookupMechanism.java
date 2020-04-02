package ppanda.sharpie.tools.annotationutils;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public interface LookupMechanism {
    Set<? extends Element> getElementsAnnotatedWith(TypeElement annotation);

    static LookupMechanism roundBasedLookUp(RoundEnvironment roundEnv) {
        return roundEnv::getElementsAnnotatedWith;
    }

    //TODO: Super slow and Not much efficient
    static LookupMechanism allClassesBasedLookUp(Collection<? extends Element> classes) {
        Map<Name, Set<Element>> annotationVsElements = classes
            .stream()
            .flatMap(symbol -> {
                    try {
                        return symbol.getAnnotationMirrors()
                            .stream()
                            .map(AnnotationMirror::getAnnotationType)
                            .map(DeclaredType::asElement)
                            .map(element -> (TypeElement) element)
                            .map(annotationType -> new SimpleImmutableEntry<Name, Element>(annotationType.getQualifiedName(), symbol));
                    } catch (Exception e) {
                        return Stream.empty();
                    }
                }
            )
            .collect(groupingBy(
                Entry::getKey, mapping(Entry::getValue, toSet()))
            );

        return annotation -> annotationVsElements.getOrDefault(annotation.getQualifiedName(), emptySet());
    }
}
