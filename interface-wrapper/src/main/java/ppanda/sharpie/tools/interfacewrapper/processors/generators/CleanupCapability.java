package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingCapability;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

//TODO: Pseudo multiple-inheritance
public interface CleanupCapability extends ProcessingCapability {
    default void removeDefaultAndStaticMethods(ClassOrInterfaceDeclaration underlyingInterface) {
        List<MethodDeclaration> defaultOrStaticMethods = underlyingInterface.getMethods()
            .stream()
            .filter(method -> method.isStatic() || method.isDefault())
            .collect(toList());
        defaultOrStaticMethods.forEach(MethodDeclaration::removeForced);
    }

    default void removeTriggeringAnnotations(ClassOrInterfaceDeclaration targetIFaceOrClass,
        Collection<String> triggeringAnnotationNames) {
        triggeringAnnotationNames
            .stream()
            .map(PackageIterator::fromQualifiedClassname)
            .flatMap(Streams::stream)
            .map(targetIFaceOrClass::getAnnotationByName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(targetIFaceOrClass::remove);
    }
}

//TODO: This is a hack since the targetIFaceOrClass.getAnnotationByName(...).get().resolve() is throwing exceptions
/*
Breaks "ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface" into following strings

"ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface",
"sharpie.tools.interfacewrapper.annotations.WrapperInterface",
"tools.interfacewrapper.annotations.WrapperInterface",
"interfacewrapper.annotations.WrapperInterface",
"annotations.WrapperInterface",
"WrapperInterface",

 */
class PackageIterator implements Iterator<String> {
    private final LinkedList<String> packageParts;

    public PackageIterator(List<String> packageParts) {
        this.packageParts = new LinkedList<>(packageParts);
    }

    @Override public boolean hasNext() {
        return !packageParts.isEmpty();
    }

    @Override public String next() {
        String parts = String.join(".", packageParts);
        packageParts.pop();
        return parts;
    }

    public static PackageIterator fromQualifiedClassname(String qualifiedClassname) {
        return new PackageIterator(asList(qualifiedClassname.split("\\.")));
    }
}
