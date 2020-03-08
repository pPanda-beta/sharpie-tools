package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.google.auto.common.MoreTypes;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Name;
import java.util.List;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

public class TypeConverterMetaModel {
    private final TypeMirror typeMirror;

    public TypeConverterMetaModel(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    public String getOnlyClassNameAsString() {
        return ((Type) typeMirror)
            .tsym
            .name.toString();
    }

    public Name getQualifiedClassName() {
        return ((Type) typeMirror)
            .asElement()
            .getQualifiedName();
    }

    public TypeMirror getDeclaredType() {
        return getTypeArguments().get(0);
    }

    public TypeMirror getOriginalType() {
        return getTypeArguments().get(1);
    }

    private List<? extends TypeMirror> getTypeArguments() {
        return MoreTypes.asTypeElement(typeMirror).getInterfaces().stream()
            .filter(iFace -> MoreTypes.isTypeOf(TypeConverter.class, iFace))
            .findAny()
            .map(MoreTypes::asDeclared)
            .map(DeclaredType::getTypeArguments)
            .orElse(null);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TypeConverterMetaModel model = (TypeConverterMetaModel) o;

        return typeMirror.equals(model.typeMirror);
    }

    @Override public int hashCode() {
        return typeMirror.hashCode();
    }
}
