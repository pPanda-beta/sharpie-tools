package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;

public class UnderlyingInterfaceCreator extends BaseGenerator {

    public UnderlyingInterfaceCreator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateUnderlyingInterface(ClassOrInterfaceDeclaration anInterface,
        TypeConverters typeConverters) {
        ClassOrInterfaceDeclaration underlyingInterface = cloneKeepingPackageAndImports(anInterface)
            .setName(getUnderlyingInterfaceName(anInterface));

        removeTriggeringAnnotations(underlyingInterface);
        removeDefaultAndStaticMethods(underlyingInterface);

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
