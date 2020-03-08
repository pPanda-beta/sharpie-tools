package ppanda.sharpie.tools.interfacewrapper.processors.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;

public class SourceWriter extends ProcessingComponent {
    public SourceWriter(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public void write(CompilationUnit packageAndImports,
        ClassOrInterfaceDeclaration targetClassOrInterface) throws IOException {
        CompilationUnit compilationUnit = packageAndImports.clone();
        targetClassOrInterface.setParentNode(compilationUnit);
        compilationUnit.addType(targetClassOrInterface.asTypeDeclaration());
        String qualifiedName = targetClassOrInterface.getFullyQualifiedName().get();
        write(qualifiedName, compilationUnit);
    }

    public void write(String nameOfFile, CompilationUnit compilationUnit) throws IOException {
        try (Writer writer = filer()
            .createSourceFile(nameOfFile)
            .openWriter()) {
            writer.write(compilationUnit.toString());
        }
    }

}
