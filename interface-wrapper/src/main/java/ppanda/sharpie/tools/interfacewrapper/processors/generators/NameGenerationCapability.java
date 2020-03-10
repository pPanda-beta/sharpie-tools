package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingCapability;

//TODO: Pseudo multiple-inheritance
public interface NameGenerationCapability extends ProcessingCapability {
    default String getNameOfFactoryClass(ClassOrInterfaceDeclaration anInterface) {
        return anInterface.getNameAsString() + "Factory";
    }

    default String getImpleClassName(ClassOrInterfaceDeclaration sourceIFace) {
        return sourceIFace.getName() + "Impl";
    }

    default String getUnderlyingInterfaceName(ClassOrInterfaceDeclaration sourceIFace) {
        return sourceIFace.getNameAsString() + "Underlying";
    }

    default String getFieldNameOfUnderlyingIFace(ClassOrInterfaceDeclaration sourceIFace) {
        return "underlying" + getUnderlyingInterfaceName(sourceIFace);
    }
}
