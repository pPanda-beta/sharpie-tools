package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

import static java.util.stream.Collectors.toMap;
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

        Map<String, String> userDeclaredTypeToOriginalType = buildTypeConversionMap(element);

        replaceReturnTypes(userDeclaredTypeToOriginalType, underlyingInterface);
        return underlyingInterface;
    }

    private void replaceReturnTypes(Map<String, String> userDeclaredTypeToOriginalType,
        ClassOrInterfaceDeclaration underlyingInterface) {
        underlyingInterface
            .getMethods()
            .stream()
            .filter(method -> userDeclaredTypeToOriginalType.containsKey(getQualifiedReturnType(method)))
            .forEach(method -> method.setType(userDeclaredTypeToOriginalType.get(getQualifiedReturnType(method))));
    }

    private Map<String, String> buildTypeConversionMap(Element element) {
        return findTypeConverters(element)
            .stream()
            .collect(toMap(model -> model.getDeclaredType().toString(),
                model -> model.getOriginalType().toString()));
    }

}
