package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static com.github.javaparser.ast.Modifier.Keyword.PRIVATE;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;
import static com.github.javaparser.ast.Modifier.publicModifier;

public class WrapperFactoryCreator extends BaseGenerator {

    private final WrapperImplementationCreator wrapperImplementationCreator;

    public WrapperFactoryCreator(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this(processingEnv, roundEnv, new WrapperImplementationCreator(processingEnv, roundEnv));
    }

    WrapperFactoryCreator(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv,
        WrapperImplementationCreator wrapperImplementationCreator) {
        super(processingEnv, roundEnv);
        this.wrapperImplementationCreator = wrapperImplementationCreator;
    }

    public ClassOrInterfaceDeclaration generateWrapperFactory(
        ClassOrInterfaceDeclaration anInterface, TypeConverters typeConverters,
        Set<String> triggeringAnnotationNames) {

        ClassOrInterfaceDeclaration implClass = wrapperImplementationCreator
            .generateWrapperInterfaceImplementation(anInterface, typeConverters, triggeringAnnotationNames);

        ClassOrInterfaceDeclaration factoryClass = new ClassOrInterfaceDeclaration(
            new NodeList<>(publicModifier()),
            false,
            getNameOfFactoryClass(anInterface));

        implClass.getParentNode().get()
            .replace(implClass, factoryClass);
        implClass.setModifiers(PRIVATE, STATIC);

        factoryClass.addMember(implClass);
        factoryClass.addMember(buildFactoryMethod(anInterface, implClass));
        return factoryClass;
    }

    private MethodDeclaration buildFactoryMethod(ClassOrInterfaceDeclaration anInterface,
        ClassOrInterfaceDeclaration implClass) {
        String underlyingInterfaceQualifiedTypeName = getUnderlyingInterfaceName(anInterface);
        String fieldNameOfUnderlyingIFace = getFieldNameOfUnderlyingIFace(anInterface);

        return new MethodDeclaration()
            .setModifiers(PUBLIC, STATIC)
            .setType(anInterface.getNameAsString())
            .setName("wrapUnderlying")
            .addParameter(underlyingInterfaceQualifiedTypeName, fieldNameOfUnderlyingIFace)
            .setBody(new BlockStmt(new NodeList<>(new ReturnStmt(
                new ObjectCreationExpr(null,
                    new ClassOrInterfaceType(implClass.getNameAsString()),
                    new NodeList<>(new NameExpr(fieldNameOfUnderlyingIFace))
                )
            ))));
    }
}
