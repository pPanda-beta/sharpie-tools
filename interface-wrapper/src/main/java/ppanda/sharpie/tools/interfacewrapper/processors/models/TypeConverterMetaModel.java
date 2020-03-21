package ppanda.sharpie.tools.interfacewrapper.processors.models;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.auto.common.MoreTypes;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import ppanda.sharpie.tools.interfacewrapper.converters.TypeConverter;

import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.applyResolutions;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.JavaParserUtils.buildGenericTypeExtractor;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.TypeConversionUtils.isConvertible;

public class TypeConverterMetaModel {
    private final TypeMirror typeMirror;
    private final ClassOrInterfaceDeclaration converterClass;
    private final ClassOrInterfaceType implementationIFace;
    private Map<TypeParameter, Function<Type, ReferenceType>> genericTypeExtractors;

    public TypeConverterMetaModel(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
        Symbol.TypeSymbol symbol = ((com.sun.tools.javac.code.Type) typeMirror).asElement();
        try {
            JavaFileObject sourcefile = symbol.enclClass().sourcefile;
            converterClass = StaticJavaParser.parse(sourcefile.openInputStream())
                .getClassByName(symbol.getSimpleName().toString())
                .orElseThrow(() -> new RuntimeException("class " + symbol.getQualifiedName() + " not found inside " + sourcefile));
            implementationIFace = getImplementation();
            genericTypeExtractors = buildGenericTypeExtractor(converterClass.getTypeParameters(), declaredType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassOrInterfaceType getImplementation() {
        return converterClass
            .getImplementedTypes()
            .stream()
            .filter(implementation -> implementation.getName().toString().equals("TypeConverter"))
            .findFirst()
            .get();
    }

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
        Map<TypeParameter, ReferenceType> genericTypeResolutions = genericTypeExtractors.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().apply(requestedDeclaredType)));

        Type typeWithResolutions = applyResolutions(originalType(), genericTypeResolutions);

        //TODO: Hack to resolve qualified names
        typeWithResolutions.setParentNode(requestedDeclaredType.findCompilationUnit()
            .get()
            .clone());
        return typeWithResolutions.resolve().describe();
    }

    public boolean supportsDeclaredType(Type requestedDeclaredType) {
        Type declaredType = declaredType();

        return isConvertible(requestedDeclaredType, declaredType);
    }

    private Type declaredType() {
        return typeArguments().get(0);
    }

    private Type originalType() {
        return typeArguments().get(1);
    }

    private NodeList<Type> typeArguments() {
        return implementationIFace.getTypeArguments()
            .get();
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
