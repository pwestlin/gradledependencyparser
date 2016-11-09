package nu.westlin;

import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<Element> {

    static final SimpleDateFormat formatter = new SimpleDateFormat("MMM, YYYY");

    @Override public int compare(Element o1, Element o2) {
        return 0;
    }
}
