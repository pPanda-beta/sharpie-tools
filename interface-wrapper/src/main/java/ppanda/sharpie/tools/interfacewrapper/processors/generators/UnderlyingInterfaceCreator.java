package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Collectors;
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

        //TODO: Since cloneKeepingPackageAndImports(...) will remove default classes in the cu of implClass,
        // we need to build this map from source interface
        Map<MethodDeclaration, String> methodVsUnderlyingReturnTypes = findReturnTypesIfReplaceable(anInterface, typeConverters);

        replaceReturnTypes(underlyingInterface, methodVsUnderlyingReturnTypes);
        return underlyingInterface;
    }

    private Map<MethodDeclaration, String> findReturnTypesIfReplaceable(ClassOrInterfaceDeclaration anInterface,
        TypeConverters typeConverters) {
        return anInterface
            .getMethods()
            .stream()
            .filter(method -> !(method.isStatic() || method.isDefault()))
            .map(method -> Maps.immutableEntry(method, typeConverters.getSupportedConverter(method.getType())))
            .filter(entry -> entry.getValue().isPresent())
            .collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().get()
                    .getOriginalType(entry.getKey().getType())));
    }

    private void addCapturedAnnotations(ClassOrInterfaceDeclaration underlyingInterface,
        Element element) {
        getCapturedAnnotations(element.getAnnotationMirrors(), AnnotationCaptor.class)
            .forEach(underlyingInterface::addAnnotation);
    }

    private void replaceReturnTypes(ClassOrInterfaceDeclaration underlyingInterface,
        Map<MethodDeclaration, String> methodVsUnderlyingReturnTypes) {

        underlyingInterface
            .getMethods()
            .forEach(method -> {
                if (methodVsUnderlyingReturnTypes.containsKey(method)) {
                    method.setType(methodVsUnderlyingReturnTypes.get(method));
                }
            });
    }
}
