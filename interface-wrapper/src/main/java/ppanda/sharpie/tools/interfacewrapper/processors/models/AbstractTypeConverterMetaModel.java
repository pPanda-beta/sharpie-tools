package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.parametrization.ResolvedTypeParametersMap;
import com.google.common.collect.Iterables;
import com.sun.tools.javac.util.Name;
import javax.lang.model.type.TypeMirror;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.TypeConversionUtils.isConvertible;

public abstract class AbstractTypeConverterMetaModel {
    private final TypeMirror typeMirror;
    private ResolvedReferenceType implementationIFace;

    public AbstractTypeConverterMetaModel(TypeMirror typeMirror, ResolvedClassDeclaration converterClass) {
        this.typeMirror = typeMirror;
        implementationIFace = Iterables.find(converterClass.getAllInterfaces(), this::isImplementationIFace);
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
