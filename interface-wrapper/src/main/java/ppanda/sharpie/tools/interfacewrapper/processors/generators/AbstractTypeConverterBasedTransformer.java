package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.transformers.Transformer;
import ppanda.sharpie.tools.interfacewrapper.processors.models.AbstractTypeConverterMetaModel;

import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.TRANSIENT;

public abstract class AbstractTypeConverterBasedTransformer<TypeConverterCategory extends AbstractTypeConverterMetaModel> implements Transformer {
    protected final TypeConverterCategory typeConverter;
    protected final String beanNameInImplClass;
    protected final String fieldNameOfUnderlyingIFace;

    public AbstractTypeConverterBasedTransformer(
        TypeConverterCategory typeConverter, String beanNameInImplClass, String fieldNameOfUnderlyingIFace) {
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

    abstract public void implementMethod(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration implClass,
        MethodDeclaration method);

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
}
