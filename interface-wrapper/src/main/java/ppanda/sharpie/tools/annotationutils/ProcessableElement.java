package ppanda.sharpie.tools.annotationutils;

import com.google.common.collect.Iterables;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class ProcessableElement {
    private final Element element;
    private final List<AnnotationMirror> path;

    public ProcessableElement(Element element, List<AnnotationMirror> path) {
        this.element = element;
        this.path = path;
    }

    public AnnotationMirror getTriggeringAnnotation() {
        return Iterables.getLast(path);
    }

    public AnnotationMirror getSubstitutedAnnotation() {
        return Iterables.getFirst(path, null);
    }

    public Element getElement() {
        return element;
    }

    public List<AnnotationMirror> getPathFromOriginalToMeta() {
        return path;
    }

    @Override public String toString() {
        return "ProcessableElement{" +
            "element=" + element +
            ", path=" + path +
            '}';
    }
}
