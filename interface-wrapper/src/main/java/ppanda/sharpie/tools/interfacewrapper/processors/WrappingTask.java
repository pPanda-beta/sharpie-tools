package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.UnderlyingInterfaceCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.WrapperFactoryCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;
import ppanda.sharpie.tools.interfacewrapper.processors.readers.ClassOrInterfaceExtractor;
import ppanda.sharpie.tools.interfacewrapper.processors.readers.TypeConvertersExtractor;
import ppanda.sharpie.tools.interfacewrapper.processors.writers.SourceWriter;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.extractPackageAndImportDeclarations;

public class WrappingTask extends ProcessingComponent {
    private final ClassOrInterfaceExtractor classOrInterfaceExtractor;
    private final SourceWriter sourceWriter;
    private final UnderlyingInterfaceCreator underlyingInterfaceCreator;
    private final WrapperFactoryCreator wrapperFactoryCreator;
    private final TypeConvertersExtractor typeConvertersExtractor;

    public WrappingTask(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
        classOrInterfaceExtractor = new ClassOrInterfaceExtractor(processingEnv, roundEnv);
        sourceWriter = new SourceWriter(processingEnv, roundEnv);
        underlyingInterfaceCreator = new UnderlyingInterfaceCreator(processingEnv, roundEnv);
        wrapperFactoryCreator = new WrapperFactoryCreator(processingEnv, roundEnv);
        typeConvertersExtractor = new TypeConvertersExtractor(processingEnv, roundEnv);
    }

    public void perform(Element element) {
        try {
            Symbol.ClassSymbol sourceInterface = (Symbol.ClassSymbol) element;
            ClassOrInterfaceDeclaration classOrInterface = classOrInterfaceExtractor.extract(sourceInterface);
            CompilationUnit packageAndImports = extractPackageAndImportDeclarations(classOrInterface);
            TypeConverters typeConverters = typeConvertersExtractor.fetchReturnTypeConverters(element);

            ClassOrInterfaceDeclaration factoryClass = wrapperFactoryCreator.generateWrapperFactory(classOrInterface, typeConverters);
            sourceWriter.write(packageAndImports, factoryClass);

            ClassOrInterfaceDeclaration underlyingInterface = underlyingInterfaceCreator.generateUnderlyingInterface(
                classOrInterface, typeConverters);
            sourceWriter.write(packageAndImports, underlyingInterface);
        } catch (Exception e) {
            logError("An error occurred ", e);
        }
    }

}
