package Parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Cleaner {

    String[] elementsToRemove = ("comment header footer menu banner auth").split(" ");

    Document document;

    private Elements elements;

    Cleaner() {
        elements = new Elements();
    }

    void removeElements() {
        elements.addAll(document.getElementsByTag("script"));
        elements.addAll(document.getElementsByTag("style"));
        elements.addAll(document.getElementsByTag("form"));
        elements.addAll(document.getElementsByTag("header"));
        elements.addAll(document.getElementsByTag("footer"));

        for(Element element: elements) element.remove();
    }

}