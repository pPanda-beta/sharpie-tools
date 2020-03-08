package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.UnderlyingInterfaceCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.WrapperImplementationCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.readers.ClassOrInterfaceExtractor;
import ppanda.sharpie.tools.interfacewrapper.processors.writers.SourceWriter;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.extractPackageAndImportDeclarations;

public class WrappingTask extends ProcessingComponent {
    private final ClassOrInterfaceExtractor classOrInterfaceExtractor;
    private final SourceWriter sourceWriter;
    private final UnderlyingInterfaceCreator underlyingInterfaceCreator;
    private final WrapperImplementationCreator wrapperImplementationCreator;

    public WrappingTask(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
        classOrInterfaceExtractor = new ClassOrInterfaceExtractor(processingEnv, roundEnv);
        sourceWriter = new SourceWriter(processingEnv, roundEnv);
        underlyingInterfaceCreator = new UnderlyingInterfaceCreator(processingEnv, roundEnv);
        wrapperImplementationCreator = new WrapperImplementationCreator(processingEnv, roundEnv);
    }

    public void perform(Element element) {
        try {
            Symbol.ClassSymbol sourceInterface = (Symbol.ClassSymbol) element;
            ClassOrInterfaceDeclaration classOrInterface = classOrInterfaceExtractor.extract(sourceInterface);
            CompilationUnit packageAndImports = extractPackageAndImportDeclarations(classOrInterface);

            ClassOrInterfaceDeclaration implClass = wrapperImplementationCreator.generateWrapperInterfaceImplementation(element, classOrInterface);
            sourceWriter.write(packageAndImports, implClass);

            ClassOrInterfaceDeclaration underlyingInterface = underlyingInterfaceCreator.generateUnderlyingInterface(
                element, classOrInterface);
            sourceWriter.write(packageAndImports, underlyingInterface);
        } catch (Exception e) {
            logError("An error occurred ", e);
        }
    }

}
