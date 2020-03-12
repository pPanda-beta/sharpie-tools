package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.AnnotationFieldExtractionCapability;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;

abstract class BaseGenerator extends ProcessingComponent
    implements AnnotationFieldExtractionCapability, NameGenerationCapability, CleanupCapability {
    protected BaseGenerator(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }
}
