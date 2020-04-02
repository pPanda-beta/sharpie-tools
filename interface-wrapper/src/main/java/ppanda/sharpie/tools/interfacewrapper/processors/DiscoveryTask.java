package ppanda.sharpie.tools.interfacewrapper.processors;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.StandardLocation;
import ppanda.sharpie.tools.annotationutils.AnnotationTree;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;
import ppanda.sharpie.tools.annotationutils.LookupMechanism;
import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;
import ppanda.sharpie.tools.interfacewrapper.processors.generators.NameGenerationCapability;

import static java.util.stream.Collectors.toSet;
import static ppanda.sharpie.tools.annotationutils.LookupMechanism.allClassesBasedLookUp;

public class DiscoveryTask extends ProcessingComponent implements NameGenerationCapability {
    private final LookupMechanism allClassesBasedLookUp;

    public DiscoveryTask(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv,
        LookupMechanism allClassesBasedLookUp) {
        super(processingEnv, roundEnv);
        this.allClassesBasedLookUp = allClassesBasedLookUp;
    }

    static class Builder {
        private final ProcessingEnvironment processingEnv;
        private LookupMechanism allClassesBasedLookUp;

        public Builder(ProcessingEnvironment processingEnv) {
            this.processingEnv = processingEnv;
            allClassesBasedLookUp = constructClassLookUpDictionary();
        }

        public DiscoveryTask buildFor(RoundEnvironment roundEnvironment) {
            return new DiscoveryTask(processingEnv, roundEnvironment, allClassesBasedLookUp);
        }

        private LookupMechanism constructClassLookUpDictionary() {
            Symtab symtab = Symtab.instance(((JavacProcessingEnvironment) processingEnv).getContext());
            Collection<Symbol.ClassSymbol> classes = new ArrayList<>(symtab
                .classes
                .values());

            return allClassesBasedLookUp(classes);
        }
    }

    public Collection<GroupedProcessableElement> findProcessableElements() {
        return AnnotationTree.of(WrapperInterface.class, processingEnv(), allClassesBasedLookUp)
            .findAllMergedProcessableElements()
            .stream()
            .filter(processableElement -> isAnInterface(processableElement.getElement()))
            .filter(processableElement -> hasNotBeenProcessedYet(processableElement.getElement()))
            .collect(toSet());
    }

    private boolean hasNotBeenProcessedYet(Element element) {
        String name = getUnderlyingInterfaceName(element.getSimpleName().toString());
        String pkg = processingEnv().getElementUtils().getPackageOf(element).getQualifiedName().toString();
        return !javaFileExists(pkg, name);
    }

    private boolean javaFileExists(String pkg, String classOrInterfaceName) {
        try {
            //TODO: Hacky way to identify if the file already exists or not
            processingEnv().getFiler()
                .getResource(StandardLocation.SOURCE_OUTPUT, pkg, classOrInterfaceName + ".java")
                .delete();//TODO: In case getResource() creates the file, we need to delete it
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isAnInterface(Element element) {
        return element
            .getKind().equals(ElementKind.INTERFACE);
    }
}
