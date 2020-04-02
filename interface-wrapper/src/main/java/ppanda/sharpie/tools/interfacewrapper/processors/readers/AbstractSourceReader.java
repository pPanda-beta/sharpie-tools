package ppanda.sharpie.tools.interfacewrapper.processors.readers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.PrintingProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingComponent;

public abstract class AbstractSourceReader extends ProcessingComponent {
    protected AbstractSourceReader(ProcessingEnvironment processingEnv,
        RoundEnvironment roundEnv) {
        super(processingEnv, roundEnv);
    }

    protected ClassOrInterfaceDeclaration readAnyHow(Element element) {
        try {
            return findTypeByReadingSource(element);
        } catch (Exception e) {
            return findTypeByPrintingElement(element);
        }
    }

    //TODO: Does not read original method bodies, replaced by PrinterWithMarkerForMethodBodies.EMPTY_METHOD_BODY
    protected ClassOrInterfaceDeclaration findTypeByPrintingElement(Element element) {
        String s = getSourceByElementPrinting(element);
        CompilationUnit compilationUnit = StaticJavaParser.parse(s);
        return findTypeOfElement(compilationUnit, (Symbol.ClassSymbol) element);
    }

    protected String getSourceByElementPrinting(Element element) {
        StringWriter writer = new StringWriter();
        //TODO: Worst hack in entire code base to make source parsable, this adds empty method bodies without thinking
        new PrinterWithMarkerForMethodBodies(writer, processingEnv().getElementUtils()).visit(element).flush();
        String sourceWithoutMethodBodies = writer.getBuffer().toString();

        return sourceWithoutMethodBodies.replaceAll(
            ";\\n" + PrinterWithMarkerForMethodBodies.STUPID_UNIQUE_MARKER,
            PrinterWithMarkerForMethodBodies.EMPTY_METHOD_BODY);
    }

    //TODO: Does not read in qualified names for type declarations
    protected ClassOrInterfaceDeclaration findTypeByReadingSource(Element element) throws IOException {
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

    private static class PrinterWithMarkerForMethodBodies extends PrintingProcessor.PrintingElementVisitor {

        public static final String EMPTY_METHOD_BODY = " {  } ";
        public static final String STUPID_UNIQUE_MARKER = "TAA_DOOM_FISSSS_b1beb89bf12df689";
        private final Writer writer;

        public PrinterWithMarkerForMethodBodies(Writer writer, Elements elements) {
            super(writer, elements);
            this.writer = writer;
        }

        @Override
        public PrintingProcessor.PrintingElementVisitor visitExecutable(ExecutableElement element, Boolean aBoolean) {
            PrintingProcessor.PrintingElementVisitor visitor = super.visitExecutable(element, aBoolean);
            try {
                writer.write(STUPID_UNIQUE_MARKER);
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return visitor;
        }
    }
}
