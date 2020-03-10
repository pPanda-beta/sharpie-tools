package ppanda.sharpie.tools.interfacewrapper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class TestUtils {

    public static ClassOrInterfaceDeclaration parseClassOrIfaceDeclaration(String code) {
        return (ClassOrInterfaceDeclaration) StaticJavaParser.parseTypeDeclaration(code);
    }

    public static ClassOrInterfaceDeclaration parseClassOrIfaceDeclarationFromSource(String code, String name,
        boolean isClass) {
        if (isClass) {
            return StaticJavaParser.parse(code).getClassByName(name).get();
        }
        return StaticJavaParser.parse(code).getInterfaceByName(name).get();
    }

}
