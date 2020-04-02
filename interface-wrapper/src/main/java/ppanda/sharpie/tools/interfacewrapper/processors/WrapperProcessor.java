package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("*") //TODO: Meta annotations should be cached on a temp file to prevent running it for all rounds
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class WrapperProcessor extends AbstractProcessor {

    private DiscoveryTask.Builder discoveryTaskBuilder;

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv) {

        WrappingTask wrappingTask = new WrappingTask(processingEnv, roundEnv);
        FinalizingTask finalizingTask = new FinalizingTask(processingEnv, roundEnv);
        DiscoveryTask discoveryTask = discoveryTaskBuilder.buildFor(roundEnv);

        discoveryTask
            .findProcessableElements()
            .stream()
            .map(wrappingTask::perform)
            .forEach(finalizingTask::perform);

        //When we are consuming @SupportedAnnotationTypes("*"), give all chances to other annotation processors, hence return false
        return false;
    }

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        discoveryTaskBuilder = new DiscoveryTask.Builder(processingEnv);

        CombinedTypeSolver solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());

        JavacProcessingEnvironment env = (JavacProcessingEnvironment) processingEnv;
        solver.add(new ClassLoaderTypeSolver(env.getProcessorClassLoader()));

        StaticJavaParser.getConfiguration()
            .setSymbolResolver(new JavaSymbolSolver(solver));
    }
}
