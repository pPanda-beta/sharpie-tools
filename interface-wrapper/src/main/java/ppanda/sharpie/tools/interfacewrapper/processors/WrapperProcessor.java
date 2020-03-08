package ppanda.sharpie.tools.interfacewrapper.processors;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;

@SupportedAnnotationTypes("ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class WrapperProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv) {

        WrappingTask wrappingTask = new WrappingTask(processingEnv, roundEnv);
        roundEnv.getElementsAnnotatedWith(WrapperInterface.class)
            .forEach(wrappingTask::perform);
        return true;
    }
}
