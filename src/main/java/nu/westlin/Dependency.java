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
}
