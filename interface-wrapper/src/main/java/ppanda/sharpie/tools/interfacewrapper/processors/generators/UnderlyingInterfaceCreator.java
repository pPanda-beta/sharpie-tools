package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;

public class UnderlyingInterfaceCreator extends BaseGenerator {

    public UnderlyingInterfaceCreator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateUnderlyingInterface(Element element,
        ClassOrInterfaceDeclaration anInterface) {
        ClassOrInterfaceDeclaration underlyingInterface = cloneKeepingPackageAndImports(anInterface)
            .setName(getUnderlyingInterfaceName(anInterface));

        removeTriggeringAnnotations(underlyingInterface);
        removeDefaultAndStaticMethods(underlyingInterface);

        TypeConverters typeConverters = findTypeConverters(element);
        replaceReturnTypes(underlyingInterface, typeConverters);
        return underlyingInterface;
    }

    private void replaceReturnTypes(ClassOrInterfaceDeclaration underlyingInterface,
        TypeConverters typeConverters) {
        underlyingInterface
            .getMethods()
            .forEach(method -> typeConverters.getOriginalType(method.getType())
                .ifPresent(originalType -> method.setType(originalType.toString())));
    }
}
