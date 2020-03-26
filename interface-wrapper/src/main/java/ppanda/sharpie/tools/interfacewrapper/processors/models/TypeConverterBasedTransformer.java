package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.TRANSIENT;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.delegatingCallExpr;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.setBodyAsStatement;

public class TypeConverterBasedTransformer implements Transformer {
    private final TypeConverterMetaModel typeConverter;
    private final String beanNameInImplClass;
    private final String fieldNameOfUnderlyingIFace;

    public TypeConverterBasedTransformer(
        TypeConverterMetaModel typeConverter, String beanNameInImplClass, String fieldNameOfUnderlyingIFace) {
        this.typeConverter = typeConverter;
        this.beanNameInImplClass = beanNameInImplClass;
        this.fieldNameOfUnderlyingIFace = fieldNameOfUnderlyingIFace;
    }

    @Override public boolean isApplicable(ClassOrInterfaceDeclaration sourceInterface, MethodDeclaration method) {
        MethodDeclaration methodInSourceInterface = findMethodInSourceInterface(sourceInterface, method);
        return typeConverter.supportsDeclaredType(methodInSourceInterface.getType());
    }

    @Override public void integrateInImplClass(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration implClass) {
        addBeanAsField(implClass);
    }

    @Override
    public void implementMethod(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass,
        MethodDeclaration method) {
        addWrappingImplementation(method, fieldNameOfUnderlyingIFace, beanNameInImplClass);
    }

    @Override
    public void changeReturnType(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration underlyingInterface,
        MethodDeclaration method) {
        MethodDeclaration methodInSourceInterface = findMethodInSourceInterface(sourceInterface, method);
        Type declaredType = methodInSourceInterface.getType();
        method.setType(typeConverter.getOriginalType(declaredType));
    }

    private void addBeanAsField(ClassOrInterfaceDeclaration implClass) {
        implClass.addFieldWithInitializer(
            typeConverter.getQualifiedClassName().toString(),
            beanNameInImplClass,
            new ObjectCreationExpr(null,
                new ClassOrInterfaceType(typeConverter.getQualifiedClassName().toString()),
                new NodeList<>()),
            FINAL, TRANSIENT
        );
    }

    private void addWrappingImplementation(MethodDeclaration method, String fieldNameOfUnderlyingIFace,
        String fieldNameInWrapperClass) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        MethodCallExpr conversionCall = new MethodCallExpr(
            new FieldAccessExpr(new ThisExpr(), fieldNameInWrapperClass), "convertFrom", new NodeList<>(originalCall));
        setBodyAsStatement(method, conversionCall);
    }
}
