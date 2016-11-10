package nu.westlin;

import com.google.common.collect.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class GradleParser {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GradleParser() {
        // Do nothing
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java " + GradleParser.class.getName() + " filename");
            System.exit(1);
        }

        new GradleParser().parse(args[0]);
    }

    private void parse(String filename) throws IOException {
        //String filename = "/home/petves/gdsfastighet/habanero/build.gradle";
        List<String> lines = Files.readAllLines(Paths.get(filename));
        List<Dependency> dependencies = Lists.newArrayList();

        boolean dependencyMode = false;
        for (String line : lines) {
            if (dependencyMode) {
                if (line.startsWith("}")) {
                    break;
                } else {
                    if (isOk(line)) {
                        Optional<Dependency> dependency = createDependency(line.trim());
                        dependency.ifPresent(dependencies::add);
                        if (!dependency.isPresent()) {
                            logger.debug("Skipping line: " + line);
                        }
                    } else {
                        logger.debug("Skipping line: " + line);
                    }
                }
            } else if (line.startsWith("dependencies {")) {
                dependencyMode = true;
            }
        }

        dependencies.forEach(this::checkForNewerVersion);
    }

    private boolean isOk(String line) {
        return !line.isEmpty() && !line.contains("se.lantmateriet");
    }

    private void checkForNewerVersion(Dependency dependency) {
        logger.debug("Kollar efter ny version för " + dependency);

        try {
            // http://mvnrepository.com/artifact/javax.inject/javax.inject
            String url = "http://mvnrepository.com/artifact/" + dependency.group + "/" + dependency.name;
            Document doc = Jsoup.connect(url).get();
            Element table = doc.select("table").last();
            Optional<Element> firstCentralRow = getFirstCentralRow(table);
            if (firstCentralRow.isPresent()) {
                Elements cols = firstCentralRow.get().select("td");
                // TODO petves: Use "cols.size() - x" instead
                int versionIdx = cols.size() == 4 ? 0 : 1;
                String newestVersion = cols.get(versionIdx).text();
                if (!dependency.version.equals(newestVersion)) {
                    logger.info("Ny version finns: " + newestVersion);
                }
                logger.info("Current version: " + dependency.version + ", Newest version: " + newestVersion + "\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<Element> getFirstCentralRow(Element table) {
        // Mar, 2007
        Elements rows = table.select("tr");
        Optional<Element> centralRow = Optional.empty();

        //rows.stream().sorted(DATE_COMPARATOR).collect(Collectors.toList());
        for (Element row : rows) {
            Elements cols = row.select("td");
            // TODO petves: Use "cols.size() - x" instead
            int repoIdx = cols.size() == 4 ? 1 : 2;
            if (!cols.isEmpty() && cols.get(repoIdx).text().equals("Central")) {
                centralRow = Optional.of(row);
                break;
            }
        }

        return centralRow;
    }

    protected Optional<Dependency> createDependency(String line) {
        Optional<Dependency> dependency;

        if (line.contains(" group:")) {
            // compile group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.5.2'
            String[] strings = line.split(":");
            String group = strings[1].split(",")[0].trim().replaceAll("\"", "").replaceAll("'", "");
            String name = strings[2].split(",")[0].trim().replaceAll("\"", "").replaceAll("'", "");
            String version = strings[3].trim().replaceAll("\"", "").replaceAll("'", "");
            dependency = Optional.of(new Dependency(group, name, version));
        } else {
            // compile "javax.inject:javax.inject:1"
            String[] strings = line.split(" ");
            String str = strings[1].replaceAll("\"", "");
            str = str.replaceAll("'", "");
            String[] split = str.split(":");
            dependency = split.length == 3 ? Optional.of(new Dependency(split[0], split[1], split[2])) : Optional.empty();
        }

        return dependency;
    }
}
