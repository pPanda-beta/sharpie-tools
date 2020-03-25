package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;
import ppanda.sharpie.tools.interfacewrapper.annotations.AnnotationCaptor;
import ppanda.sharpie.tools.interfacewrapper.processors.AnnotationExtractionCapability;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;

public class UnderlyingInterfaceCreator extends BaseGenerator implements AnnotationExtractionCapability {

    public UnderlyingInterfaceCreator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateUnderlyingInterface(ClassOrInterfaceDeclaration anInterface,
        Transformers transformers, GroupedProcessableElement processableElement) {
        ClassOrInterfaceDeclaration underlyingInterface = cloneKeepingPackageAndImports(anInterface)
            .setName(getUnderlyingInterfaceName(anInterface));

        removeTriggeringAnnotations(underlyingInterface, processableElement.getTriggeringAnnotationNames());
        addCapturedAnnotations(underlyingInterface, processableElement.getElement());
        removeDefaultAndStaticMethods(underlyingInterface);

        transformers.changeReturnTypes(anInterface, underlyingInterface);
        return underlyingInterface;
    }

    private void addCapturedAnnotations(ClassOrInterfaceDeclaration underlyingInterface,
        Element element) {
        getCapturedAnnotations(element.getAnnotationMirrors(), AnnotationCaptor.class)
            .forEach(underlyingInterface::addAnnotation);
    }
}
