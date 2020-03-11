package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.MethodDeclaration;
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

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.resolveQualifiedName;

abstract class BaseGenerator extends ProcessingComponent
    implements AnnotationFieldExtractionCapability, NameGenerationCapability, CleanupCapability {
    protected BaseGenerator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    //TODO: It should utilize import declarations present in compilationUnit
    protected String getQualifiedReturnType(MethodDeclaration method) {
        return resolveQualifiedName(method.getType());
    }

    protected TypeConverters findTypeConverters(Element element) {
        return this.
            <TypeMirror>extractMultipleValue(
                element, WrapperInterface.class, "returnTypeConverters")
            .stream()
            .map(TypeConverterMetaModel::new)
            .collect(Collectors.toCollection(TypeConverters::new));
    }
}
