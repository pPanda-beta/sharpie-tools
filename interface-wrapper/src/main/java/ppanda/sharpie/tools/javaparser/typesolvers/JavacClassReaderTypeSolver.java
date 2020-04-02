package ppanda.sharpie.tools.javaparser.typesolvers;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistFactory;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.sun.tools.javac.code.Symbol;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import javassist.ClassPool;
import javassist.CtClass;
import javax.tools.JavaFileObject;

public class JavacClassReaderTypeSolver extends BaseJavacSymbolsTypeSolver {
    private ClassPool classPool = new ClassPool(false);

    public JavacClassReaderTypeSolver(Collection<Symbol.ClassSymbol> symbols) {
        super(symbols);
    }

    @Override SymbolReference<ResolvedReferenceTypeDeclaration> desperatelyTryToSolve(
        String qualifiedName) throws Exception {
        Symbol.ClassSymbol symbol = qualifiedNameVsSymbols.get(qualifiedName);
        if (Objects.nonNull(symbol) && Objects.nonNull(symbol.classfile)) {
            JavaFileObject file = symbol.classfile;
            resetJavaFileState(file);
            InputStream byteCodeStream = symbol.classfile
                .openInputStream();
            CtClass ctClass = classPool.makeClass(byteCodeStream);

            SymbolReference<ResolvedReferenceTypeDeclaration> declarationSymbolReference = SymbolReference.solved(
                JavassistFactory.toTypeDeclaration(ctClass, getRoot()));
            resetJavaFileState(file);
            return declarationSymbolReference;
        }
        return SymbolReference.unsolved(ResolvedReferenceTypeDeclaration.class);
    }

}
