package ppanda.sharpie.tools.javaparser.typesolvers;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.file.ZipFileIndexArchive;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.tools.JavaFileObject;

import static java.util.stream.Collectors.toMap;

abstract class BaseJavacSymbolsTypeSolver implements TypeSolver {
    protected final Map<String, Symbol.ClassSymbol> qualifiedNameVsSymbols;
    private TypeSolver parent;
    private final Map<String, SymbolReference<ResolvedReferenceTypeDeclaration>> foundTypes = new HashMap<>();

    public BaseJavacSymbolsTypeSolver(Collection<Symbol.ClassSymbol> symbols) {
        qualifiedNameVsSymbols = symbols.stream()
            .collect(toMap(symbol -> symbol.getQualifiedName().toString(),
                Function.identity(), (x, y) -> x));
    }

    abstract SymbolReference<ResolvedReferenceTypeDeclaration> desperatelyTryToSolve(String name) throws Exception;

    @Override public SymbolReference<ResolvedReferenceTypeDeclaration> tryToSolveType(String name) {
        return foundTypes.computeIfAbsent(name, ignore -> {
            try {
                SymbolReference<ResolvedReferenceTypeDeclaration> symbolReference = desperatelyTryToSolve(name);
                return symbolReference;
            } catch (Exception e) {
                return SymbolReference.unsolved(ResolvedReferenceTypeDeclaration.class);
            }
        });
    }

    @Override public TypeSolver getParent() {
        return parent;
    }

    @Override public void setParent(TypeSolver parent) {
        this.parent = parent;
    }

    protected static void resetJavaFileState(JavaFileObject file) throws NoSuchFieldException, IllegalAccessException {
        if (file instanceof ZipFileIndexArchive.ZipFileIndexFileObject) {
            Field inputStreamField = ZipFileIndexArchive.ZipFileIndexFileObject.class.getDeclaredField("inputStream");
            inputStreamField.setAccessible(true);
            inputStreamField.set(file, null);
        }
    }
}
