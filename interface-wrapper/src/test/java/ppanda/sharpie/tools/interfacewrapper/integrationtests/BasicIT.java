package ppanda.sharpie.tools.interfacewrapper.integrationtests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaFileObject;
import org.junit.Test;
import ppanda.sharpie.tools.interfacewrapper.processors.WrapperProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class BasicIT {

    @Test
    public void simpleInterfaceWithGenericTypeConverter() {
        Compilation compilation = compile("samples/sample1/source",
            "samples/sample1/source/interfaces/Person.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonUnderlying",
            "samples/sample1/expected/interfaces/PersonUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonFactory",
            "samples/sample1/expected/interfaces/PersonFactory.java");
    }

    @Test
    public void interfaceWithMultipleTypeConverters() {
        Compilation compilation = compile("samples/sample2/source",
            "samples/sample2/source/interfaces/Person.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonUnderlying",
            "samples/sample2/expected/interfaces/PersonUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonFactory",
            "samples/sample2/expected/interfaces/PersonFactory.java");
    }

    @Test
    public void interfaceWithMetaAnnotation() {
        Compilation compilation = compile("samples/sample3/source",
            "samples/sample3/source/annotations/OptionalStringWrapper.java",
            "samples/sample3/source/interfaces/Person.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonUnderlying",
            "samples/sample3/expected/interfaces/PersonUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonFactory",
            "samples/sample3/expected/interfaces/PersonFactory.java");
    }

    @Test
    public void interfaceWithCustomReturnType() {
        Compilation compilation = compile("samples/sample4/source",
            "samples/sample4/source/interfaces/Person.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonUnderlying",
            "samples/sample4/expected/interfaces/PersonUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonFactory",
            "samples/sample4/expected/interfaces/PersonFactory.java");
    }

    @Test
    public void interfaceWithReturnTypeUnWrapper() {
        Compilation compilation = compile("samples/sample5/source",
            "samples/sample5/source/interfaces/Person.java"
        );

        verifyGeneratedFileContents(compilation, "interfaces.PersonUnderlying",
            "samples/sample5/expected/interfaces/PersonUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.PersonFactory",
            "samples/sample5/expected/interfaces/PersonFactory.java");

        verifyGeneratedFileContents(compilation, "interfaces.RepositoryUnderlying",
            "samples/sample5/expected/interfaces/RepositoryUnderlying.java");

        verifyGeneratedFileContents(compilation, "interfaces.RepositoryFactory",
            "samples/sample5/expected/interfaces/RepositoryFactory.java");
    }

    private void verifyGeneratedFileContents(Compilation compilation, String generatedSourceQualifiedName,
        String expectedGeneratedFile) {
        JavaFileObject expectedSrcOfUnderlyingIFace = JavaFileObjects.forResource(expectedGeneratedFile);
        assertThat(compilation)
            .generatedSourceFile(generatedSourceQualifiedName)
            .hasSourceEquivalentTo(expectedSrcOfUnderlyingIFace);
    }

    private Compilation compile(String sourceClasspath, String... sourcePaths) {
        JavaFileObject[] javaFileObjects = Stream.of(sourcePaths)
            .map(JavaFileObjects::forResource)
            .toArray(JavaFileObject[]::new);

        return javac()
            .withClasspath(systemClasspathsAnd(sourceClasspath))
            .withProcessors(new WrapperProcessor())
            .compile(javaFileObjects);
    }

    private List<File> systemClasspathsAnd(String extraClasspath) {
        ImmutableList<File> systemClasspaths = javac()
            .withClasspathFrom(this.getClass().getClassLoader())
            .classPath()
            .get();
        File extraFolder = new File(Resources.getResource(extraClasspath).getPath());
        return Streams.concat(systemClasspaths.stream(),
            Stream.of(extraFolder))
            .collect(Collectors.toList());
    }
}
