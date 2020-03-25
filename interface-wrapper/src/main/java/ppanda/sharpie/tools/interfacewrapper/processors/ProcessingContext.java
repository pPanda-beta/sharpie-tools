package ppanda.sharpie.tools.interfacewrapper.processors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

class ProcessingContext {
    private final ClassOrInterfaceDeclaration sourceInterface;
    private final ClassOrInterfaceDeclaration factoryClass;
    private final ClassOrInterfaceDeclaration underlyingInterface;

    public ProcessingContext(ClassOrInterfaceDeclaration sourceInterface,
        ClassOrInterfaceDeclaration factoryClass,
        ClassOrInterfaceDeclaration underlyingInterface) {
        this.sourceInterface = sourceInterface;
        this.factoryClass = factoryClass;
        this.underlyingInterface = underlyingInterface;
    }

    public ClassOrInterfaceDeclaration getSourceInterface() {
        return sourceInterface;
    }

    public ClassOrInterfaceDeclaration getFactoryClass() {
        return factoryClass;
    }

    public ClassOrInterfaceDeclaration getUnderlyingInterface() {
        return underlyingInterface;
    }
}
