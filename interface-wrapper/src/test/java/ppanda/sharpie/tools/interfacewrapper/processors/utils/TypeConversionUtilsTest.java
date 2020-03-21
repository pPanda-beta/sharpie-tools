package ppanda.sharpie.tools.interfacewrapper.processors.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static ppanda.sharpie.tools.interfacewrapper.processors.utils.TypeConversionUtils.isConvertible;

public class TypeConversionUtilsTest {

    private CompilationUnit imports;

    @BeforeClass
    public static void beforeAll() {
        StaticJavaParser.getConfiguration()
            .setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));

    }

    @Before
    public void setUp() throws Exception {
        imports = StaticJavaParser.parse("" +
            "import java.util.*;" +
            "import java.util.function.*;" +
            "");
    }

    @Test
    public void shouldVerifyForBasicClass() {
        assertThat(isConvertible(parseType("java.lang.String"), parseType("String"))).isTrue();
    }

    @Test
    public void shouldVerifyForSimple1stOrderGenericClass() {
        assertThat(isConvertible(parseType("Optional<String>"),
            parseType("java.util.Optional<java.lang.String>"))).isTrue();

        assertThat(isConvertible(parseType("Optional<String>"),
            parseType("Optional<Integer>"))).isFalse();
    }

    @Test
    public void shouldVerifyForGenericClassWithMultipleTypeArgs() {
        assertThat(isConvertible(parseType("Function<String, Integer>"),
            parseType("Function<String, Float>"))).isFalse();
    }

    @Test
    public void shouldVerifyForGenericClassWithTypeArgs() {
        ClassOrInterfaceDeclaration clazzXT = StaticJavaParser.parse("" +
            "import java.util.*;" +
            "" +
            "class X<T extends Number>{ " +
            "    Optional<T> tOpt;" +
            "}" +
            "")
            .getType(0)
            .asClassOrInterfaceDeclaration();

        Type optionalNumberLike = clazzXT.getMember(0)
            .asFieldDeclaration()
            .getVariable(0)
            .getType();

        assertThat(isConvertible(parseType("Optional<Integer>"),
            optionalNumberLike))
            .describedAs("Optional<T> can capture Optional<Integer> where T extends Number")
            .isTrue();

        assertThat(isConvertible(parseType("Optional<Character>"),
            optionalNumberLike))
            .describedAs("Optional<T> can not capture Optional<Character> where T extends Number")
            .isFalse();
    }

    @Test
    public void shouldVerifyForGenericClassWithUnboundedTypeArgs() {
        ClassOrInterfaceDeclaration clazzXT = StaticJavaParser.parse("" +
            "import java.util.*;" +
            "" +
            "class X<T> { " +
            "    Optional<T> tOpt;" +
            "}" +
            "")
            .getType(0)
            .asClassOrInterfaceDeclaration();

        Type optionalOfAnyType = clazzXT.getMember(0)
            .asFieldDeclaration()
            .getVariable(0)
            .getType();

        assertThat(isConvertible(parseType("Optional<Integer>"),
            optionalOfAnyType))
            .isTrue();
    }

    @Test
    public void shouldVerifyForFieldsWithWildcardTypeArgs() {
        Type numberLikeOptional = parseType("Optional<? extends Number>");

        assertThat(isConvertible(parseType("Optional<Integer>"),
            numberLikeOptional))
            .describedAs("Optional<? extends Number> can capture Optional<Integer>")
            .isTrue();

        assertThat(isConvertible(parseType("Optional<Character>"),
            numberLikeOptional))
            .describedAs("Optional<? extends Number> can not capture Optional<Character>")
            .isFalse();
    }

    @Test
    public void shouldVerifyForFieldsWithUnboundedWildcardTypeArgs() {
        assertThat(isConvertible(parseType("Optional<Integer>"),
            parseType("Optional<?>")))
            .isTrue();
    }

    @Test
    public void shouldVerifyForSubClassAndSuper() {
        assertThat(isConvertible(parseType("Integer"), parseType("Object"))).isTrue();
    }

    private Type parseType(String stringifiedType) {
        Type type = StaticJavaParser.parseType(stringifiedType);
        type.setParentNode(imports);
        return type;
    }

}
