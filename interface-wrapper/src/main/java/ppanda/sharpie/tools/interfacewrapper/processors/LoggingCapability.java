package ppanda.sharpie.tools.interfacewrapper.processors;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static java.util.stream.Collectors.joining;

//TODO: Pseudo multiple-inheritance
public interface LoggingCapability extends ProcessingCapability {
    default Messager messager() {
        return processingEnv().getMessager();
    }

    default void logError(String msg, Throwable t) {
        String stackTrace = Arrays.stream(t.getStackTrace())
            .map(Objects::toString)
            .collect(joining("\n"));

        messager().printMessage(Diagnostic.Kind.ERROR, msg + " " + t + " \n " + stackTrace);
    }
}
