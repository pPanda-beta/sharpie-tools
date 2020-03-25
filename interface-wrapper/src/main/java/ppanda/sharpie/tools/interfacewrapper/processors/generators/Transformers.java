package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.util.List;
import ppanda.sharpie.tools.interfacewrapper.processors.models.Transformer;

import static java.util.stream.Collectors.toList;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.delegatingCallExpr;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.setBodyAsStatement;

public class Transformers {

    private final List<Transformer> transformers;
    private final String fieldNameOfUnderlyingIFace;

    public Transformers(
        List<Transformer> transformers, String fieldNameOfUnderlyingIFace) {
        this.transformers = transformers;
        this.fieldNameOfUnderlyingIFace = fieldNameOfUnderlyingIFace;
    }

    public void setupImplClass(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass) {
        transformers.forEach(transformer -> transformer.integrateInImplClass(sourceInterface, implClass));
    }

    public void implementMethods(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass) {
        implClass
            .getMethods()
            .forEach(method -> {
                    List<Transformer> applicableTransformers = getApplicableTransformers(sourceInterface, method);
                    if (applicableTransformers.isEmpty()) {
                        implementDelegatingMethod(method);
                        return;
                    }
                    applicableTransformers.forEach(transformer -> transformer.implementMethod(sourceInterface, implClass, method));
                }
            );

    }

    public void changeReturnTypes(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration underlyingInterface) {
        underlyingInterface
            .getMethods()
            .forEach(method -> getApplicableTransformers(sourceInterface, method)
                .forEach(transformer -> transformer.changeReturnType(sourceInterface, underlyingInterface, method))
            );
    }

    private List<Transformer> getApplicableTransformers(ClassOrInterfaceDeclaration sourceInterface,
        MethodDeclaration method) {
        return transformers.stream()
            .filter(transformer -> transformer.isApplicable(sourceInterface, method))
            .collect(toList());
    }

    private void implementDelegatingMethod(MethodDeclaration method) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        setBodyAsStatement(method, originalCall);
    }
}
