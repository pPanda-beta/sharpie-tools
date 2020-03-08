package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.List;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingCapability;

import static java.util.stream.Collectors.toList;

//TODO: Pseudo multiple-inheritance
public interface CleanupCapability extends ProcessingCapability {
    default void removeDefaultAndStaticMethods(ClassOrInterfaceDeclaration underlyingInterface) {
        List<MethodDeclaration> defaultOrStaticMethods = underlyingInterface.getMethods()
            .stream()
            .filter(method -> method.isStatic() || method.isDefault())
            .collect(toList());
        defaultOrStaticMethods.forEach(MethodDeclaration::removeForced);
    }

    default void removeTriggeringAnnotations(ClassOrInterfaceDeclaration targetIFaceOrClass) {
        targetIFaceOrClass.getAnnotationByClass(WrapperInterface.class)
            .map(targetIFaceOrClass::remove)
            .orElseThrow(() -> new RuntimeException("WrapperInterface annotation not found on  " + targetIFaceOrClass));
    }
}
