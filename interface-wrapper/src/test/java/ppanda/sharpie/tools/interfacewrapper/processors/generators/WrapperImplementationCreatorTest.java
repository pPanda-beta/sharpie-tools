package ppanda.sharpie.tools.interfacewrapper.processors.generators;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.Collections;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ppanda.sharpie.tools.annotationutils.GroupedProcessableElement;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static ppanda.sharpie.tools.interfacewrapper.TestUtils.parseClassOrIfaceDeclarationFromSource;

@RunWith(MockitoJUnitRunner.class)
public class WrapperImplementationCreatorTest {
    @Mock(answer = RETURNS_DEEP_STUBS) ProcessingEnvironment processingEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) RoundEnvironment roundEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) GroupedProcessableElement processableElement;

    @InjectMocks
    private WrapperImplementationCreator wrapperImplCreator;

    @Test
    public void shouldGenerateImplClass() {
        when(processableElement.getTriggeringAnnotationNames()).thenReturn(singleton(
            "ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface"));
        Transformers transformers = new Transformers(Collections.emptyList(), "underlyingFooUnderlying");

        ClassOrInterfaceDeclaration fooIFace = parseClassOrIfaceDeclarationFromSource("" +
                "package abc; " +
                "import java.lang.*; " +
                "import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface; " +
                "                    " +
                "@WrapperInterface " +
                "interface Foo { " +
                "    void bar(); " +
                "} ",
            "Foo", false);
        ClassOrInterfaceDeclaration resultClass = wrapperImplCreator
            .generateWrapperInterfaceImplementation(fooIFace, transformers, processableElement);

        assertThat(resultClass.findCompilationUnit().get()).isEqualTo(StaticJavaParser.parse("" +
            "package abc;                                                                " +
            "                                                                            " +
            "import java.lang.*;                                                         " +
            "import ppanda.sharpie.tools.interfacewrapper.annotations.WrapperInterface;  " +
            "                                                                            " +
            "class FooImpl implements Foo {                                              " +
            "                                                                            " +
            "    public void bar() {                                                     " +
            "        underlyingFooUnderlying.bar();                                      " +
            "    }                                                                       " +
            "                                                                            " +
            "    final transient FooUnderlying underlyingFooUnderlying;                  " +
            "                                                                            " +
            "    public FooImpl(FooUnderlying underlyingFooUnderlying) {                 " +
            "        this.underlyingFooUnderlying = underlyingFooUnderlying;             " +
            "    }                                                                       " +
            "}                                                                           "
        ));
    }

}
