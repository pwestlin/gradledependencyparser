package nu.westlin;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GradleParserTest {

    final GradleParser parser = new GradleParser();

    @Test
    public void createDependency_withoutGroup() {
        String line = "compile \"javax.inject:javax.inject:1";
        Optional<Dependency> dependency = parser.createDependency(line);

        assertThat(dependency, is(Optional.of(new Dependency("javax.inject", "javax.inject", "1"))));
    }
}