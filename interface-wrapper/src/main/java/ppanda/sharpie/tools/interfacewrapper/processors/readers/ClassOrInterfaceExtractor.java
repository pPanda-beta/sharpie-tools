package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;

public class ClassOrInterfaceExtractor extends ProcessingComponent {

    public ClassOrInterfaceExtractor(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    public ClassOrInterfaceDeclaration extract(Symbol.ClassSymbol element) throws IOException {
//        return findTypeByPrintingElement(element);
        return findTypeByReadingSource(element);
    }

    //TODO: Does not read method bodies
    private ClassOrInterfaceDeclaration findTypeByPrintingElement(Element element) {
        String s = getSourceByElementPrinting(element);
        CompilationUnit compilationUnit = StaticJavaParser.parse(s);
        return findTypeOfElement(compilationUnit, (Symbol.ClassSymbol) element);
    }

    private String getSourceByElementPrinting(Element element) {
        StringWriter writer = new StringWriter();
        processingEnv().getElementUtils().printElements(writer, element);
        return writer.getBuffer().toString();
    }

    //TODO: Does not read in qualified names for type declarations
    private ClassOrInterfaceDeclaration findTypeByReadingSource(Element element) throws IOException {
        InputStream file = getSource(element);
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);
        return findTypeOfElement(compilationUnit, (Symbol.ClassSymbol) element);
    }

    private InputStream getSource(Element element) throws IOException {
        if (element instanceof Symbol.ClassSymbol) {
            return ((Symbol.ClassSymbol) element).sourcefile.openInputStream();
        }
        return null;
    }

    private ClassOrInterfaceDeclaration findTypeOfElement(CompilationUnit compilationUnit, Symbol.ClassSymbol element) {
        return compilationUnit
            .getTypes()
            .stream()
            .filter(t -> t.getFullyQualifiedName().orElse(t.getNameAsString())
                .equals(element.getQualifiedName().toString()))
            .findAny()
            .map(t -> (ClassOrInterfaceDeclaration) t)
            .orElseThrow(() -> new RuntimeException("No class found for " + element + " inside " + compilationUnit));
    }

}
