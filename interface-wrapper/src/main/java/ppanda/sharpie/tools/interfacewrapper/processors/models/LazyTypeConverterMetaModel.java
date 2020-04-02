package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import javax.lang.model.type.TypeMirror;

public class LazyTypeConverterMetaModel extends AbstractTypeConverterMetaModel {

    public LazyTypeConverterMetaModel(TypeMirror typeMirror, ResolvedClassDeclaration converterClass) {
        super(typeMirror, converterClass);
    }

    @Override protected boolean isImplementationIFace(ResolvedReferenceType iFace) {
        return iFace.getQualifiedName().equals("ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter");
    }

    @Override protected ResolvedType declaredType() {
        return typeArguments().getValueBySignature("ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter.DeclaredT").get();
    }

    @Override protected ResolvedType originalType() {
        return typeArguments().getValueBySignature("ppanda.sharpie.tools.interfacewrapper.converters.LazyTypeConverter.OriginalT").get();
    }
}
