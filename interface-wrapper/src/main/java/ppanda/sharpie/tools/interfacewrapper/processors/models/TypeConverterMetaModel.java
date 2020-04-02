package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import javax.lang.model.type.TypeMirror;

public class TypeConverterMetaModel extends AbstractTypeConverterMetaModel {

    public TypeConverterMetaModel(TypeMirror typeMirror) {
        super(typeMirror);
    }

    @Override protected boolean isImplementationIFace(ResolvedReferenceType iFace) {
        return iFace.getQualifiedName().equals("ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter");
    }

    @Override protected ResolvedType declaredType() {
        return typeArguments().getValueBySignature("ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter.DeclaredT").get();
    }

    @Override protected ResolvedType originalType() {
        return typeArguments().getValueBySignature("ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter.OriginalT").get();
    }
}
