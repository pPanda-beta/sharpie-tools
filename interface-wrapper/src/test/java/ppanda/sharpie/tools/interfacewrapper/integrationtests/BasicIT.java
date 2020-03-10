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
    public void generateSourcesForSample1() {
        String sourceClasspath = "samples/sample1/source";
        JavaFileObject sourceIFace = JavaFileObjects.forResource("samples/sample1/source/interfaces/Person.java");

        Compilation compilation = javac()
            .withClasspath(systemClasspathsAnd(sourceClasspath))
            .withProcessors(new WrapperProcessor())
            .compile(sourceIFace);

        JavaFileObject expectedSrcOfUnderlyingIFace = JavaFileObjects.forResource("samples/sample1/expected/interfaces/PersonUnderlying.java");
        JavaFileObject expectedSrcOfFactoryClass = JavaFileObjects.forResource("samples/sample1/expected/interfaces/PersonFactory.java");

        assertThat(compilation)
            .generatedSourceFile("interfaces.PersonUnderlying")
            .hasSourceEquivalentTo(expectedSrcOfUnderlyingIFace);
        assertThat(compilation)
            .generatedSourceFile("interfaces.PersonFactory")
            .hasSourceEquivalentTo(expectedSrcOfFactoryClass);
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
