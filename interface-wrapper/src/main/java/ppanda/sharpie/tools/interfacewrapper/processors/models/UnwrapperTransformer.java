package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.delegatingCallExpr;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.setBodyAsStatement;

public class UnwrapperTransformer implements Transformer {
    private final String qualifiedNameOfWrapperIFace;
    private final String qualifiedNameOfUnderlyingIFace;
    private final String qualifiedNameOfFactoryClass;
    private final String fieldNameOfUnderlyingIFace;

    public UnwrapperTransformer(String qualifiedNameOfWrapperIFace, String qualifiedNameOfUnderlyingIFace,
        String qualifiedNameOfFactoryClass, String fieldNameOfUnderlyingIFace) {
        this.qualifiedNameOfWrapperIFace = qualifiedNameOfWrapperIFace;
        this.qualifiedNameOfUnderlyingIFace = qualifiedNameOfUnderlyingIFace;
        this.qualifiedNameOfFactoryClass = qualifiedNameOfFactoryClass;
        this.fieldNameOfUnderlyingIFace = fieldNameOfUnderlyingIFace;
    }

    @Override public boolean isApplicable(ClassOrInterfaceDeclaration sourceInterface, MethodDeclaration method) {
        return findMethodInSourceInterface(sourceInterface, method)
            .getType()
            .resolve()
            .asReferenceType()
            .getQualifiedName()
            .equals(qualifiedNameOfWrapperIFace);
    }

    @Override
    public void implementMethod(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass,
        MethodDeclaration method) {
        addWrappingImplementation(method);
    }

    private void addWrappingImplementation(MethodDeclaration method) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        MethodCallExpr conversionCall = new MethodCallExpr(
            new NameExpr(qualifiedNameOfFactoryClass), "wrapUnderlying", new NodeList<>(originalCall));
        setBodyAsStatement(method, conversionCall);
    }

    @Override public void changeReturnType(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration underlyingInterface, MethodDeclaration method) {
        method.setType(qualifiedNameOfUnderlyingIFace);
    }
}
