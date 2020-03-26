package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import ppanda.sharpie.tools.interfacewrapper.processors.ProcessingCapability;

//TODO: Pseudo multiple-inheritance
public interface NameGenerationCapability extends ProcessingCapability {
    default String getNameOfFactoryClass(ClassOrInterfaceDeclaration anInterface) {
        return getNameOfFactoryClass(anInterface.getNameAsString());
    }

    default String getNameOfFactoryClass(String sourceIFaceName) {
        return sourceIFaceName + "Factory";
    }

    default String getImpleClassName(ClassOrInterfaceDeclaration sourceIFace) {
        return sourceIFace.getName() + "Impl";
    }

    default String getUnderlyingInterfaceName(ClassOrInterfaceDeclaration sourceIFace) {
        return getUnderlyingInterfaceName(sourceIFace.getNameAsString());
    }

    default String getUnderlyingInterfaceName(String sourceIFaceName) {
        return sourceIFaceName + "Underlying";
    }

    default String getFieldNameOfUnderlyingIFace(ClassOrInterfaceDeclaration sourceIFace) {
        return "underlying" + getUnderlyingInterfaceName(sourceIFace);
    }
}
