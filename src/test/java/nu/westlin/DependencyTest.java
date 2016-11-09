package nu.westlin;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class DependencyTest {

    @Test
    public void equals_isEqual() {
        Dependency dependency1 = new Dependency("group", "name", "version");
        Dependency dependency2 = new Dependency("group", "name", "version");

        assertThat(dependency1, is(dependency2));
    }

    @Test
    public void equals_groupDiffers() {
        Dependency dependency1 = new Dependency("group", "name", "version");
        Dependency dependency2 = new Dependency("group2", "name", "version");

        assertThat(dependency1, is(not(dependency2)));
    }

    @Test
    public void equals_nameDiffers() {
        Dependency dependency1 = new Dependency("group", "name", "version");
        Dependency dependency2 = new Dependency("group", "name2", "version");

        assertThat(dependency1, is(not(dependency2)));
    }

    @Test
    public void equals_versionDiffers() {
        Dependency dependency1 = new Dependency("group", "name", "version");
        Dependency dependency2 = new Dependency("group", "name", "version2");

        assertThat(dependency1, is(not(dependency2)));
    }
}