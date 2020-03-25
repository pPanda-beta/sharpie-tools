package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.Transformers;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.UnderlyingInterfaceCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.WrapperFactoryCreator;
import ppanda.sharpie.tools.interfacewrapper.processors.readers.ClassOrInterfaceExtractor;
import ppanda.sharpie.tools.interfacewrapper.processors.readers.TransformersExtractor;

public class WrappingTask extends ProcessingComponent {
    private final ClassOrInterfaceExtractor classOrInterfaceExtractor;
    private final UnderlyingInterfaceCreator underlyingInterfaceCreator;
    private final WrapperFactoryCreator wrapperFactoryCreator;
    private final TransformersExtractor transformersExtractor;

    public WrappingTask(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
        classOrInterfaceExtractor = new ClassOrInterfaceExtractor(processingEnv, roundEnv);
        underlyingInterfaceCreator = new UnderlyingInterfaceCreator(processingEnv, roundEnv);
        wrapperFactoryCreator = new WrapperFactoryCreator(processingEnv, roundEnv);
        transformersExtractor = new TransformersExtractor(processingEnv, roundEnv);
    }

    public ProcessingContext perform(GroupedProcessableElement processableElement) {
        try {
            Symbol.ClassSymbol sourceInterface = (Symbol.ClassSymbol) processableElement.getElement();
            ClassOrInterfaceDeclaration classOrInterface = classOrInterfaceExtractor.extract(sourceInterface);
            Transformers transformers = transformersExtractor.fetchTransformers(classOrInterface,
                processableElement.getSubstitutedAnnotationMirrors());
            ClassOrInterfaceDeclaration factoryClass = wrapperFactoryCreator.generateWrapperFactory(
                classOrInterface, transformers, processableElement);

            ClassOrInterfaceDeclaration underlyingInterface = underlyingInterfaceCreator.generateUnderlyingInterface(
                classOrInterface, transformers, processableElement);
            return new ProcessingContext(classOrInterface, factoryClass, underlyingInterface);
        } catch (Exception e) {
            logError("An error occurred ", e);
        }
        return null;
    }

}
