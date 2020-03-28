package ppanda.sharpie.tools.interfacewrapper.processors.generators.transformers;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.AbstractTypeConverterBasedTransformer;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverterMetaModel;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.delegatingCallExpr;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.MethodUtils.setBodyAsStatement;

public class TypeConverterBasedTransformer extends AbstractTypeConverterBasedTransformer<TypeConverterMetaModel> {

    public TypeConverterBasedTransformer(
        TypeConverterMetaModel typeConverter, String beanNameInImplClass, String fieldNameOfUnderlyingIFace) {
        super(typeConverter, beanNameInImplClass, fieldNameOfUnderlyingIFace);
    }

    @Override
    public void implementMethod(ClassOrInterfaceDeclaration sourceInterface, ClassOrInterfaceDeclaration implClass,
        MethodDeclaration method) {
        addWrappingImplementation(method, fieldNameOfUnderlyingIFace, beanNameInImplClass);
    }

    private void addWrappingImplementation(MethodDeclaration method, String fieldNameOfUnderlyingIFace,
        String fieldNameInWrapperClass) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        MethodCallExpr conversionCall = new MethodCallExpr(
            new FieldAccessExpr(new ThisExpr(), fieldNameInWrapperClass), "convertFrom", new NodeList<>(originalCall));
        setBodyAsStatement(method, conversionCall);
    }
}
