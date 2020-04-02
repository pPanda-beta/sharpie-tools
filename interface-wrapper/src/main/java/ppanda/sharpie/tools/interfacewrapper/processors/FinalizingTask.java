package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.ast.CompilationUnit;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.writers.SourceWriter;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.extractPackageAndImportDeclarations;

public class FinalizingTask extends ProcessingComponent {
    private final SourceWriter sourceWriter;

    public FinalizingTask(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
        sourceWriter = new SourceWriter(processingEnv, roundEnv);
    }

    public void perform(ProcessingContext context) {
        try {
            CompilationUnit packageAndImports = extractPackageAndImportDeclarations(context.getSourceInterface());

            sourceWriter.write(packageAndImports, context.getUnderlyingInterface());
            sourceWriter.write(packageAndImports, context.getFactoryClass());
        } catch (Exception e) {
//            logError("An error occurred ", e);
        }
    }

}
