package nu.westlin.gradledependencyparser;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Parser {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM, yyyy", Locale.US);

    public Parser() {
        // Do nothing
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java " + Parser.class.getName() + " filename");
            System.exit(1);
        }

        new Parser().parse(args[0]);
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
        logger.debug("Searching for new version for " + dependency);

        try {
            String url = "http://mvnrepository.com/artifact/" + dependency.group + "/" + dependency.name;
            Document doc = Jsoup.connect(url).get();
            Element table = doc.select("table").last();
            Optional<Element> firstCentralRow = getNewestCentralRow(table);
            if (firstCentralRow.isPresent()) {
                Elements cols = firstCentralRow.get().select("td");
                int versionIdx = cols.size() - 4;
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

    protected Optional<Element> getNewestCentralRow(Element table) {
        // Mar, 2007
        Elements rows = table.select("tr");
        Optional<Element> centralRow = Optional.empty();

        //rows.stream().sorted(DATE_COMPARATOR).collect(Collectors.toList());
        Date lastDate = null;
        for (Element row : rows) {
            Elements cols = row.select("td");
            if (!cols.isEmpty() && cols.get(cols.size() - 3).text().equals("Central")) {
                Date rowDate = null;
                try {
                    rowDate = DATE_FORMATTER.parse(cols.get(cols.size() - 1).text().replaceAll("\\(", "").replaceAll("\\)", ""));
                    if (lastDate == null || lastDate.before(rowDate)) {
                        centralRow = Optional.of(row);
                        lastDate = rowDate;
                    } else {
                        break;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Could not parse date", e);
                }
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
