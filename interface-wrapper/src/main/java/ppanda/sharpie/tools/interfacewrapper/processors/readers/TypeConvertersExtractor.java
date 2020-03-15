package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import ppanda.sharpie.tools.interfacewrapper.processors.AnnotationFieldExtractionCapability;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverterMetaModel;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

public class TypeConvertersExtractor extends ProcessingComponent
    implements AnnotationFieldExtractionCapability {

    public TypeConvertersExtractor(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public TypeConverters fetchReturnTypeConverters(Set<AnnotationMirror> annotationMirrors) {
        return annotationMirrors
            .stream()
            .flatMap(mirror -> this.<TypeMirror>extractMultipleValue(mirror, "returnTypeConverters").stream())
            .map(TypeConverterMetaModel::new)
            .collect(Collectors.toCollection(TypeConverters::new));
    }

}
