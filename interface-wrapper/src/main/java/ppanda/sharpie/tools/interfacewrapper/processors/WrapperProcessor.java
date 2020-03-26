package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Options;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import ppanda.sharpie.tools.annotationutils.AnnotationTree;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@SupportedAnnotationTypes("ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class WrapperProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv) {

        WrappingTask wrappingTask = new WrappingTask(processingEnv, roundEnv);
        FinalizingTask finalizingTask = new FinalizingTask(processingEnv, roundEnv);

        AnnotationTree.of(WrapperInterface.class, roundEnv, processingEnv)
            .findAllMergedProcessableElements()
            .stream()
            .filter(processableElement -> isAnInterface(processableElement.getElement()))
            .map(wrappingTask::perform)
            .forEach(finalizingTask::perform);
        return true;
    }

    private boolean isAnInterface(Element element) {
        return element
            .getKind().equals(ElementKind.INTERFACE);
    }

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        CombinedTypeSolver solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());

        JavacProcessingEnvironment env = (JavacProcessingEnvironment) processingEnv;
        String cp = Options.instance(env.getContext()).get("-classpath");
        if (cp != null) {
            for (String path : cp.split(":")) {
                try {
                    if (path.endsWith(".jar")) {
                        solver.add(new JarTypeSolver(path));
                    } else
                        solver.add(new JavaParserTypeSolver(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        StaticJavaParser.getConfiguration()
            .setSymbolResolver(new JavaSymbolSolver(solver));
    }
}
