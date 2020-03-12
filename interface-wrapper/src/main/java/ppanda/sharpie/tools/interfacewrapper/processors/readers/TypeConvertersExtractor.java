package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;
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

    public TypeConverters fetchReturnTypeConverters(Element element) {
        return this.
            <TypeMirror>extractMultipleValue(
                element, WrapperInterface.class, "returnTypeConverters")
            .stream()
            .map(TypeConverterMetaModel::new)
            .collect(Collectors.toCollection(TypeConverters::new));
    }

}
