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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static ppanda.sharpie.tools.interfacewrapper.TestUtils.parseClassOrIfaceDeclarationFromSource;

@RunWith(MockitoJUnitRunner.class)
public class WrapperFactoryCreatorTest {
    @Mock(answer = RETURNS_DEEP_STUBS) ProcessingEnvironment processingEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) RoundEnvironment roundEnvironment;
    @Mock(answer = RETURNS_DEEP_STUBS) ClassOrInterfaceDeclaration sourceIFace;
    @Mock(answer = RETURNS_DEEP_STUBS) WrapperImplementationCreator wrapperImplementationCreator;
    @Mock(answer = RETURNS_DEEP_STUBS) GroupedProcessableElement processableElement;

    @InjectMocks
    WrapperFactoryCreator wrapperFactoryCreator;

    @Test
    public void shouldPutImplClassInAFactory() {
        when(sourceIFace.getNameAsString()).thenReturn("Foo");
        Transformers transformers = new Transformers(Collections.emptyList(), "underlyingFooUnderlying");

        when(wrapperImplementationCreator.generateWrapperInterfaceImplementation(sourceIFace, transformers, processableElement))
            .thenReturn(parseClassOrIfaceDeclarationFromSource("" +
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
                    "}                                                                   ",
                "FooImpl", true));

        ClassOrInterfaceDeclaration resultClass = wrapperFactoryCreator
            .generateWrapperFactory(sourceIFace, transformers, processableElement);

        assertThat(resultClass.findCompilationUnit().get()).isEqualTo(StaticJavaParser.parse("" +
            "package abc;                                                                      " +
            "                                                                                  " +
            "import java.lang.*;                                                               " +
            "                                                                                  " +
            "public class FooFactory {                                                         " +
            "                                                                                  " +
            "    private static class FooImpl implements Foo {                                 " +
            "                                                                                  " +
            "        public void bar() {                                                       " +
            "            underlyingFooUnderlying.bar();                                        " +
            "        }                                                                         " +
            "                                                                                  " +
            "        final transient FooUnderlying underlyingFooUnderlying;                    " +
            "                                                                                  " +
            "        public FooImpl(FooUnderlying underlyingFooUnderlying) {                   " +
            "            this.underlyingFooUnderlying = underlyingFooUnderlying;               " +
            "        }                                                                         " +
            "    }                                                                             " +
            "                                                                                  " +
            "    public static Foo wrapUnderlying(FooUnderlying underlyingFooUnderlying) {     " +
            "        return new FooImpl(underlyingFooUnderlying);                              " +
            "    }                                                                             " +
            "}                                                                                 "
        ));
    }
}
