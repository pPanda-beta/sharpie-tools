package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import javax.lang.model.type.TypeMirror;

public class LazyTypeConverterMetaModel extends AbstractTypeConverterMetaModel {

    public LazyTypeConverterMetaModel(TypeMirror typeMirror) {
        super(typeMirror);
    }

    @Override protected boolean isImplementationIFace(ClassOrInterfaceType implementation) {
        return implementation.getName().toString().equals("LazyTypeConverter");
    }
}
