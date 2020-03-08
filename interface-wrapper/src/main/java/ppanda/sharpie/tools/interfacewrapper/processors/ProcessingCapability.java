package ppanda.sharpie.tools.interfacewrapper.processors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

//TODO: Pseudo multiple-inheritance
public interface ProcessingCapability {
    ProcessingEnvironment processingEnv();

    RoundEnvironment roundEnv();

    default Filer filer() {
        return processingEnv().getFiler();
    }
}
