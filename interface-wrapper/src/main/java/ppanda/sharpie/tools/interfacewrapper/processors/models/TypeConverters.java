package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.ast.type.Type;
import java.util.ArrayList;
import java.util.Optional;

public class TypeConverters extends ArrayList<TypeConverterMetaModel> {
    public Optional<String> getOriginalType(Type declaredType) {
        return getSupportedConverter(declaredType)
            .map(model -> model.getOriginalType(declaredType));
    }

    public Optional<TypeConverterMetaModel> getSupportedConverter(Type declaredType) {
        return super.stream()
            .filter(typeConverter -> typeConverter.supportsDeclaredType(declaredType))
            .findFirst();
    }
}
