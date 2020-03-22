package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;
import ppanda.sharpie.tools.interfacewrapper.annotations.AnnotationCaptor;
import ppanda.sharpie.tools.interfacewrapper.processors.AnnotationExtractionCapability;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;

public class UnderlyingInterfaceCreator extends BaseGenerator implements AnnotationExtractionCapability {

    public UnderlyingInterfaceCreator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateUnderlyingInterface(ClassOrInterfaceDeclaration anInterface,
        TypeConverters typeConverters, GroupedProcessableElement processableElement) {
        ClassOrInterfaceDeclaration underlyingInterface = cloneKeepingPackageAndImports(anInterface)
            .setName(getUnderlyingInterfaceName(anInterface));

        removeTriggeringAnnotations(underlyingInterface, processableElement.getTriggeringAnnotationNames());
        addCapturedAnnotations(underlyingInterface, processableElement.getElement());
        removeDefaultAndStaticMethods(underlyingInterface);

        replaceReturnTypes(underlyingInterface, typeConverters);
        return underlyingInterface;
    }

    private void addCapturedAnnotations(ClassOrInterfaceDeclaration underlyingInterface,
        Element element) {
        getCapturedAnnotations(element.getAnnotationMirrors(), AnnotationCaptor.class)
            .forEach(underlyingInterface::addAnnotation);
    }

    private void replaceReturnTypes(ClassOrInterfaceDeclaration underlyingInterface,
        TypeConverters typeConverters) {
        underlyingInterface
            .getMethods()
            .forEach(method -> typeConverters.getOriginalType(method.getType())
                .ifPresent(originalType -> method.setType(originalType.toString())));
    }
}
