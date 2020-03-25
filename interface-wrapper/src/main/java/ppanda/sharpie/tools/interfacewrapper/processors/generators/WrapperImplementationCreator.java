package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;

import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.TRANSIENT;
import static com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.cloneKeepingPackageAndImports;

public class WrapperImplementationCreator extends BaseGenerator {

    public WrapperImplementationCreator(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration generateWrapperInterfaceImplementation(
        ClassOrInterfaceDeclaration anInterface, Transformers transformers,
        GroupedProcessableElement processableElement) {

        String underlyingInterfaceQualifiedTypeName = getUnderlyingInterfaceName(anInterface);
        String fieldNameOfUnderlyingIFace = "underlying" + underlyingInterfaceQualifiedTypeName;

        ClassOrInterfaceDeclaration implClass = cloneKeepingPackageAndImports(anInterface)
            .setName(getImpleClassName(anInterface))
            .setInterface(false);

        removeTriggeringAnnotations(implClass, processableElement.getTriggeringAnnotationNames());
        removeDefaultAndStaticMethods(implClass);

        implClass.setImplementedTypes(new NodeList<>(
            new ClassOrInterfaceType(anInterface.getNameAsString())
        ));

        transformers.setupImplClass(anInterface, implClass);
        addUnderlyingInterfaceAsField(implClass, fieldNameOfUnderlyingIFace, underlyingInterfaceQualifiedTypeName);

        addConstructor(implClass, underlyingInterfaceQualifiedTypeName, fieldNameOfUnderlyingIFace);
        transformers.implementMethods(anInterface, implClass);
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

    private void addUnderlyingInterfaceAsField(ClassOrInterfaceDeclaration implClass,
        String fieldNameOfUnderlyingIFace, String underlyingInterfaceQualifiedTypeName) {
        implClass.addField(
            underlyingInterfaceQualifiedTypeName,
            fieldNameOfUnderlyingIFace,
            FINAL, TRANSIENT
        );
    }
}
