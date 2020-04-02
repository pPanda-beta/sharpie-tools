package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.parametrization.ResolvedTypeParametersMap;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.TypeConversionUtils.isConvertible;

public abstract class AbstractTypeConverterMetaModel {
    private final TypeMirror typeMirror;
    private ResolvedReferenceType implementationIFace;

    public AbstractTypeConverterMetaModel(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
        Symbol.TypeSymbol symbol = ((com.sun.tools.javac.code.Type) typeMirror).asElement();
        ResolvedClassDeclaration converterClass =
            tryToResolveAlreadyCompiledClass(symbol)
                .orElseGet(() -> readFromSourceCode(symbol));

        implementationIFace = converterClass
            .getAllInterfaces()
            .stream()
            .filter(this::isImplementationIFace)
            .findFirst()
            .get();
    }

    abstract protected boolean isImplementationIFace(ResolvedReferenceType iFace);

    public String getOnlyClassNameAsString() {
        return ((com.sun.tools.javac.code.Type) typeMirror)
            .tsym
            .name.toString();
    }

    public Name getQualifiedClassName() {
        return ((com.sun.tools.javac.code.Type) typeMirror)
            .asElement()
            .getQualifiedName();
    }

    public String getOriginalType(Type requestedDeclaredType) {
        ResolvedType resolvedRequestedType = requestedDeclaredType.resolve();
        return Substitutions.infer(declaredType(), resolvedRequestedType)
            .applyOn(originalType())
            .describe();
    }

    public boolean supportsDeclaredType(Type requestedDeclaredType) {
        ResolvedType resolvedDeclaredType = declaredType();
        ResolvedType resolvedRequestedType = requestedDeclaredType.resolve();
        return isConvertible(resolvedRequestedType.asReferenceType(), resolvedDeclaredType.asReferenceType());
    }

    abstract protected ResolvedType declaredType();

    abstract protected ResolvedType originalType();

    protected ResolvedTypeParametersMap typeArguments() {
        return implementationIFace.typeParametersMap();
    }

    //TODO: Dirty util, should move to reader/extractor
    private static Optional<ResolvedClassDeclaration> tryToResolveAlreadyCompiledClass(
        Symbol.TypeSymbol symbol) {
        String qualifiedNameOfConverterClass = symbol.getQualifiedName().toString();

        TypeSolver typeSolver = getGlobalTypeSolver();
        SymbolReference<ResolvedReferenceTypeDeclaration> result = typeSolver.tryToSolveType(qualifiedNameOfConverterClass);
        return result.isSolved() ?
            Optional.of(result.getCorrespondingDeclaration().asClass())
            : Optional.empty();
    }

    //TODO: Dirty util, should move to reader/extractor
    private static ResolvedClassDeclaration readFromSourceCode(Symbol.TypeSymbol symbol) {
        try {
            JavaFileObject sourcefile = symbol.enclClass().sourcefile;
            return StaticJavaParser.parse(sourcefile.openInputStream())
                .getClassByName(symbol.getSimpleName().toString())
                .orElseThrow(() -> new RuntimeException("class " + symbol.getQualifiedName() + " not found inside " + sourcefile))
                .resolve()
                .asClass();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Dirty util and static injection
    private static TypeSolver getGlobalTypeSolver() {
        try {
            JavaSymbolSolver solver1 = (JavaSymbolSolver) StaticJavaParser.getConfiguration()
                .getSymbolResolver().get();

            Field field = JavaSymbolSolver.class.getDeclaredField("typeSolver");
            field.setAccessible(true);
            return (TypeSolver) field.get(solver1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractTypeConverterMetaModel model = (AbstractTypeConverterMetaModel) o;

        return typeMirror.equals(model.typeMirror);
    }

    @Override public int hashCode() {
        return typeMirror.hashCode();
    }
}
