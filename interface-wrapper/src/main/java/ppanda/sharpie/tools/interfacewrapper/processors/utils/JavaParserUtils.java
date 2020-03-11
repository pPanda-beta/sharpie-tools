package ppanda.sharpie.tools.interfacewrapper.processors.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

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

    public static Map<TypeParameter, Function<Type, ReferenceType>> buildGenericTypeExtractor(
        List<TypeParameter> typeParameters, Type typeWithGenericTypeArgs
    ) {
        Function<Node, Iterator<Node>> iteratorCreator = Node.DirectChildrenIterator::new;
        return typeParameters.stream()
            .collect(Collectors.toMap(identity(), typeParameter -> {
                int positionOfTypeArg = Iterators.indexOf(iteratorCreator.apply(typeWithGenericTypeArgs),
                    nodeWithSameNameAs(typeParameter));
                return (type) -> (ReferenceType) Iterators.get(iteratorCreator.apply(type), positionOfTypeArg);
            }));
    }

    public static Type applyResolutions(Type type, Map<TypeParameter, ReferenceType> genericTypeResolutions) {
        Type clone = type.clone();
        //TODO: in case the type itself is going to be replaced
        MethodDeclaration holder = new MethodDeclaration();
        holder.setType(clone);

        genericTypeResolutions.forEach((typeParameter, referenceType) -> clone
            .stream()
            .filter(nodeWithSameNameAs(typeParameter))
            .collect(Collectors.toList())
            .forEach(node -> node.replace(referenceType)));
        return holder.getType();
    }

    private static Predicate<Node> nodeWithSameNameAs(TypeParameter typeParameter) {
        return node -> (node instanceof NodeWithSimpleName) &&
            ((NodeWithSimpleName) node).getName().equals(typeParameter.getName());
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
