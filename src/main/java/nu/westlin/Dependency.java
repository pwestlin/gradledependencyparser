package nu.westlin;

public class Dependency {

    public final String group;
    public final String name;
    public final String version;

    public Dependency(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    @Override public String toString() {
        return "Dependency{" +
            "group='" + group + '\'' +
            ", name='" + name + '\'' +
            ", version='" + version + '\'' +
            '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Dependency that = (Dependency) o;

        if (!group.equals(that.group))
            return false;
        if (!name.equals(that.name))
            return false;
        return version.equals(that.version);
    }

    @Override public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
