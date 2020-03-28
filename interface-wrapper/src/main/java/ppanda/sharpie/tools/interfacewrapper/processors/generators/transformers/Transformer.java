package ppanda.sharpie.tools.interfacewrapper.processors.generators.transformers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface Transformer {

    boolean isApplicable(ClassOrInterfaceDeclaration sourceInterface, MethodDeclaration method);

    void implementMethod(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass,
        MethodDeclaration method);

    void changeReturnType(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration underlyingInterface,
        MethodDeclaration method);

    default void integrateInImplClass(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration implClass) {
    }

    default void integrateInUnderlyingInterface(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration implClass) {
    }

    //TODO: Since cloneKeepingPackageAndImports(...) will remove default classes in the cu of implClass,
    // we need to build this map from source interface
    default MethodDeclaration findMethodInSourceInterface(ClassOrInterfaceDeclaration sourceInterface,
        MethodDeclaration method) {
        return sourceInterface.getMethods()
            .stream()
            .filter(method::equals) // TODO: signature based equality check should be there
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("" + method + " is not found in source interface " + sourceInterface));
    }

}
