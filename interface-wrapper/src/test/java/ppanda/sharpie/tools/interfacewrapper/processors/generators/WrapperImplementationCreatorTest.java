package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ppanda.sharpie.tools.interfacewrapper.processors.models.TypeConverters;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static ppanda.sharpie.tools.interfacewrapper.TestUtils.parseClassOrIfaceDeclarationFromSource;

@RunWith(MockitoJUnitRunner.class)
public class WrapperImplementationCreatorTest {
    @Mock(answer = RETURNS_DEEP_STUBS) ProcessingEnvironment processingEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) RoundEnvironment roundEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) Element element;
    private WrapperImplementationCreator wrapperImplCreator;

    @Before
    public void setUp() throws Exception {
        wrapperImplCreator = spy(new WrapperImplementationCreator(processingEnvironment, roundEnvironment));
    }

    @Test
    public void shouldGenerateImplClass() {
        doReturn(new TypeConverters())
            .when(wrapperImplCreator).findTypeConverters(element);

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
            .generateWrapperInterfaceImplementation(element, fooIFace);

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
