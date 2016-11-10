package nu.westlin.gradledependencyparser;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParserTest {

    final Parser parser = new Parser();

    @Test
    public void createDependency_doubleQuotesWithoutGroup() {
        String line = "compile \"org.apache.httpcomponents:httpclient:4.5.2\"";
        Optional<Dependency> dependency = parser.createDependency(line);

        assertThat(dependency, is(Optional.of(new Dependency("org.apache.httpcomponents", "httpclient", "4.5.2"))));
    }

    @Test
    public void createDependency_singleQuotesWithoutGroup() {
        String line = "compile 'org.apache.httpcomponents:httpclient:4.5.2'";
        Optional<Dependency> dependency = parser.createDependency(line);

        assertThat(dependency, is(Optional.of(new Dependency("org.apache.httpcomponents", "httpclient", "4.5.2"))));
    }

    @Test
    public void createDependency_singleQuotesWitGroup() {
        String line = "compile group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.5.2'";
        Optional<Dependency> dependency = parser.createDependency(line);

        assertThat(dependency, is(Optional.of(new Dependency("org.apache.httpcomponents", "httpclient", "4.5.2"))));
    }

    @Test
    public void createDependency_dobleQuotesWitGroup() {
        String line = "compile group: \"org.apache.httpcomponents\", name: \"httpclient\", version: \"4.5.2\"";
        Optional<Dependency> dependency = parser.createDependency(line);

        assertThat(dependency, is(Optional.of(new Dependency("org.apache.httpcomponents", "httpclient", "4.5.2"))));
    }

}