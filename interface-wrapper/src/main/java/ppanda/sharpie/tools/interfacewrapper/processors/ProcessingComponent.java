package ppanda.sharpie.tools.interfacewrapper.processors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public abstract class ProcessingComponent implements ProcessingCapability, LoggingCapability {
    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;

    protected ProcessingComponent(
        ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
    }

    @Override public ProcessingEnvironment processingEnv() {
        return processingEnv;
    }

    @Override public RoundEnvironment roundEnv() {
        return roundEnv;
    }

}

