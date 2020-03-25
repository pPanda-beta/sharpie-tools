package ppanda.sharpie.tools.interfacewrapper.processors.models;

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
}
