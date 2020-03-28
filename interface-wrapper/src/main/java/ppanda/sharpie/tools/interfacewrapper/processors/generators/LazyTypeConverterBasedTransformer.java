package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import ppanda.sharpie.tools.interfacewrapper.processors.models.LazyTypeConverterMetaModel;
import ppanda.sharpie.tools.interfacewrapper.processors.models.Transformer;

import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.TRANSIENT;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.delegatingNoArgLambdaCallExpr;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.setBodyAsStatement;

public class LazyTypeConverterBasedTransformer implements Transformer {
    private final LazyTypeConverterMetaModel typeConverter;
    private final String beanNameInImplClass;
    private final String fieldNameOfUnderlyingIFace;

    public LazyTypeConverterBasedTransformer(
        LazyTypeConverterMetaModel typeConverter, String beanNameInImplClass, String fieldNameOfUnderlyingIFace) {
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
        Expression originalCall = delegatingNoArgLambdaCallExpr(method, fieldNameOfUnderlyingIFace);
        MethodCallExpr conversionCall = new MethodCallExpr(
            new FieldAccessExpr(new ThisExpr(), fieldNameInWrapperClass), "convertFrom", new NodeList<>(originalCall));
        setBodyAsStatement(method, conversionCall);
    }
}
