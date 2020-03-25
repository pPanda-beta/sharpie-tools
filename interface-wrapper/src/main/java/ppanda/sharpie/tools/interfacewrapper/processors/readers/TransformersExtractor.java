package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import ppanda.sharpie.tools.interfacewrapper.processors.AnnotationExtractionCapability;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.NameGenerationCapability;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.Transformers;
import ppanda.sharpie.tools.interfacewrapper.processors.models.Transformer;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverterBasedTransformer;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverterMetaModel;

import static java.util.stream.Collectors.toList;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.deCapitalize;

public class TransformersExtractor extends ProcessingComponent
    implements AnnotationExtractionCapability, NameGenerationCapability {

    public TransformersExtractor(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public Transformers fetchTransformers(ClassOrInterfaceDeclaration sourceInterface,
        Set<AnnotationMirror> annotationMirrors) {
        String fieldNameOfUnderlyingIFace = getFieldNameOfUnderlyingIFace(sourceInterface);

        List<Transformer> transformers = annotationMirrors
            .stream()
            .flatMap(mirror -> this.<TypeMirror>extractMultipleValue(mirror, "returnTypeConverters").stream())
            .map(TypeConverterMetaModel::new)
            .sorted(Comparator.comparing(converter -> converter.getQualifiedClassName().toString())) //TODO: Just for ITs, compile-testing:0.18 breaks if order of fields are different
            .map(converter -> new TypeConverterBasedTransformer(
                converter,
                deCapitalize(converter.getOnlyClassNameAsString()),
                fieldNameOfUnderlyingIFace
            ))
            .collect(toList());
        return new Transformers(transformers, fieldNameOfUnderlyingIFace);
    }

}
