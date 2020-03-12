package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static ppanda.sharpie.tools.interfacewrapper.TestUtils.parseClassOrIfaceDeclarationFromSource;

@RunWith(MockitoJUnitRunner.class)
public class WrapperImplementationCreatorTest {
    @Mock(answer = RETURNS_DEEP_STUBS) ProcessingEnvironment processingEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) RoundEnvironment roundEnvironment;

    @InjectMocks
    private WrapperImplementationCreator wrapperImplCreator;

    @Test
    public void shouldGenerateImplClass() {
        ClassOrInterfaceDeclaration fooIFace = parseClassOrIfaceDeclarationFromSource("" +
                "package abc; " +
                "import java.lang.*; " +
                "                    " +
                "@WrapperInterface " +
                "interface Foo { " +
                "    void bar(); " +
                "} ",
            "Foo", false);
        ClassOrInterfaceDeclaration resultClass = wrapperImplCreator
            .generateWrapperInterfaceImplementation(fooIFace, new TypeConverters());

        assertThat(resultClass.findCompilationUnit().get()).isEqualTo(StaticJavaParser.parse("" +
            "package abc;                                                        " +
            "                                                                    " +
            "import java.lang.*;                                                 " +
            "                                                                    " +
            "class FooImpl implements Foo {                                      " +
            "                                                                    " +
            "    public void bar() {                                             " +
            "        underlyingFooUnderlying.bar();                              " +
            "    }                                                               " +
            "                                                                    " +
            "    final transient FooUnderlying underlyingFooUnderlying;          " +
            "                                                                    " +
            "    public FooImpl(FooUnderlying underlyingFooUnderlying) {         " +
            "        this.underlyingFooUnderlying = underlyingFooUnderlying;     " +
            "    }                                                               " +
            "}                                                                   "
        ));
    }

}
