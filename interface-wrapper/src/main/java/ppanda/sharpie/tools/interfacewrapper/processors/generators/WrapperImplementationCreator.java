package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverterMetaModel;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.TRANSIENT;
import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;
import static java.util.stream.Collectors.toMap;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.deCapitalize;

public class WrapperImplementationCreator extends BaseGenerator {

    public WrapperImplementationCreator(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateWrapperInterfaceImplementation(
        ClassOrInterfaceDeclaration anInterface, TypeConverters typeConverters) {

        String underlyingInterfaceQualifiedTypeName = getUnderlyingInterfaceName(anInterface);
        String fieldNameOfUnderlyingIFace = "underlying" + underlyingInterfaceQualifiedTypeName;

        Map<TypeConverterMetaModel, String> converterVsFieldnameInWrapperclass = generateUniqueFieldNames(typeConverters);

        ClassOrInterfaceDeclaration implClass = cloneKeepingPackageAndImports(anInterface)
            .setName(getImpleClassName(anInterface))
            .setInterface(false);

        removeTriggeringAnnotations(implClass);
        removeDefaultAndStaticMethods(implClass);

        implClass.setImplementedTypes(new NodeList<>(
            new ClassOrInterfaceType(anInterface.getNameAsString())
        ));

        converterVsFieldnameInWrapperclass.forEach(
            (converter, fieldNameString) -> addConverterBeansAsField(implClass, converter, fieldNameString));
        addUnderlyingInterfaceAsField(implClass, fieldNameOfUnderlyingIFace, underlyingInterfaceQualifiedTypeName);

        addConstructor(implClass, underlyingInterfaceQualifiedTypeName, fieldNameOfUnderlyingIFace);
        implementMethods(implClass, fieldNameOfUnderlyingIFace, converterVsFieldnameInWrapperclass, typeConverters);
        return implClass;
    }

    private void addConstructor(ClassOrInterfaceDeclaration implClass, String underlyingInterfaceQualifiedTypeName,
        String fieldNameOfUnderlyingIFace) {
        /*
            Pragmatic way of generating following source
            public $implClass.name ($underlyingInterfaceQualifiedTypeName $fieldNameOfUnderlyingIFace)
            {
                this.$fieldNameOfUnderlyingIFace = $fieldNameOfUnderlyingIFace
            }
         */

        BlockStmt blockStmt = new BlockStmt(new NodeList<>(
            new ExpressionStmt(
                new AssignExpr(
                    new FieldAccessExpr(new ThisExpr(), fieldNameOfUnderlyingIFace),
                    new NameExpr(fieldNameOfUnderlyingIFace),
                    ASSIGN
                )
            )));
        implClass.addConstructor(PUBLIC)
            .addParameter(underlyingInterfaceQualifiedTypeName, fieldNameOfUnderlyingIFace)
            .setBody(blockStmt);

    }

    private Map<TypeConverterMetaModel, String> generateUniqueFieldNames(TypeConverters typeConverters) {
        return typeConverters
            .stream()
            .collect(toMap(
                Function.identity(),
                converter -> deCapitalize(converter.getOnlyClassNameAsString())
            ));
    }

    private void implementMethods(ClassOrInterfaceDeclaration implClass, String fieldNameOfUnderlyingIFace,
        Map<TypeConverterMetaModel, String> converterVsFieldnameInWrapperclass, TypeConverters typeConverters) {
        implClass.getMethods()
            .forEach(method -> {
                Optional<TypeConverterMetaModel> foundConverter = typeConverters.getSupportedConverter(method.getType());
                if (foundConverter.isPresent()) {
                    TypeConverterMetaModel typeConverter = foundConverter.get();
                    String fieldNameInWrapperClass = converterVsFieldnameInWrapperclass.get(typeConverter);
                    addWrappingImplmentation(method, fieldNameOfUnderlyingIFace, fieldNameInWrapperClass);
                } else {
                    addDelegatingImplementation(method, fieldNameOfUnderlyingIFace);
                }

            });
    }

    private void addDelegatingImplementation(MethodDeclaration method, String fieldNameOfUnderlyingIFace) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        setBodyAsStatement(method, originalCall);
    }

    private MethodCallExpr delegatingCallExpr(MethodDeclaration method, String fieldNameOfUnderlyingIFace) {
        NodeList<Expression> arguments = method.getParameters().stream().map(NodeWithSimpleName::getNameAsExpression)
            .collect(Collectors.toCollection(NodeList::new));
        return new MethodCallExpr(new NameExpr(fieldNameOfUnderlyingIFace), method.getName(), arguments);
    }

    private void addWrappingImplmentation(MethodDeclaration method, String fieldNameOfUnderlyingIFace,
        String fieldNameInWrapperClass) {
        MethodCallExpr originalCall = delegatingCallExpr(method, fieldNameOfUnderlyingIFace);
        MethodCallExpr conversionCall = new MethodCallExpr(
            new FieldAccessExpr(new ThisExpr(), fieldNameInWrapperClass), "convertFrom", new NodeList<>(originalCall));
        setBodyAsStatement(method, conversionCall);
    }

    private void setBodyAsStatement(MethodDeclaration method, MethodCallExpr callExpr) {
        method.setModifiers(PUBLIC);
        if (method.getType().isVoidType()) {
            method.setBody(new BlockStmt(new NodeList<>(new ExpressionStmt(callExpr))));
            return;
        }
        ReturnStmt returnStmt = new ReturnStmt(callExpr);
        method.setBody(new BlockStmt(new NodeList<>(returnStmt)));
    }

    private void addUnderlyingInterfaceAsField(ClassOrInterfaceDeclaration implClass,
        String fieldNameOfUnderlyingIFace, String underlyingInterfaceQualifiedTypeName) {
        implClass.addField(
            underlyingInterfaceQualifiedTypeName,
            fieldNameOfUnderlyingIFace,
            FINAL, TRANSIENT
        );
    }

    private void addConverterBeansAsField(ClassOrInterfaceDeclaration implClass, TypeConverterMetaModel converter,
        String fieldNameString) {
        implClass.addFieldWithInitializer(
            converter.getQualifiedClassName().toString(),
            fieldNameString,
            new ObjectCreationExpr(null,
                new ClassOrInterfaceType(converter.getQualifiedClassName().toString()),
                new NodeList<>()),
            FINAL, TRANSIENT
        );
    }
}
