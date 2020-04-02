package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public class ClassOrInterfaceExtractor extends AbstractSourceReader {

    public ClassOrInterfaceExtractor(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration extract(Symbol.ClassSymbol element) throws IOException {
//        return findTypeByPrintingElement(element);
        return findTypeByReadingSource(element);
    }
}
