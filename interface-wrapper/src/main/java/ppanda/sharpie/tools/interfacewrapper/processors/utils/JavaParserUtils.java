package ppanda.sharpie.tools.interfacewrapper.processors.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.annotations.Beta;
import java.util.List;
import java.util.stream.Collectors;

public class JavaParserUtils {
    public static CompilationUnit extractPackageAndImportDeclarations(ClassOrInterfaceDeclaration classOrInterface) {
        CompilationUnit compilationUnit = classOrInterface.findCompilationUnit()
            .get()
            .clone();
        List<Node> typeNodes = compilationUnit.getChildNodes()
            .stream()
            .filter(node -> !(node instanceof PackageDeclaration || node instanceof ImportDeclaration))
            .collect(Collectors.toList());
        typeNodes.forEach(Node::removeForced);
        return compilationUnit;
    }

    public static ClassOrInterfaceDeclaration cloneKeepingPackageAndImports(ClassOrInterfaceDeclaration declaration) {
        CompilationUnit compilationUnit = extractPackageAndImportDeclarations(declaration);
        ClassOrInterfaceDeclaration targetClassOrInterface = declaration.clone();
        targetClassOrInterface.setParentNode(compilationUnit);
        compilationUnit.addType(targetClassOrInterface);
        return targetClassOrInterface;
    }

    @Beta
    public static String resolveQualifiedName(Type type) {
        return JavaParserFacade.get(new ReflectionTypeSolver())
            .convertToUsage(type)
            .describe();
    }

    public static String deCapitalize(String string) {
        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}
