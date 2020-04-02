package ppanda.sharpie.tools.javaparser.typesolvers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.sun.tools.javac.code.Symbol;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class JavacSourceFileTypeSolver extends BaseJavacSymbolsTypeSolver {

    public JavacSourceFileTypeSolver(Collection<Symbol.ClassSymbol> symbols) {
        super(symbols);
    }

    @Override SymbolReference<ResolvedReferenceTypeDeclaration> desperatelyTryToSolve(
        String qualifiedName) throws Exception {
        Symbol.ClassSymbol symbol = qualifiedNameVsSymbols.get(qualifiedName);
        if (Objects.nonNull(symbol) && Objects.nonNull(symbol.sourcefile)) {
            CompilationUnit compilationUnit = StaticJavaParser.parse(symbol.sourcefile.openInputStream());
            Optional<TypeDeclaration<?>> astTypeDeclaration = Navigator.findType(compilationUnit, qualifiedName);
            if (astTypeDeclaration.isPresent()) {
                return SymbolReference.solved(JavaParserFacade.get(this).getTypeDeclaration(astTypeDeclaration.get()));
            }
        }
        return SymbolReference.unsolved(ResolvedReferenceTypeDeclaration.class);
    }
}
