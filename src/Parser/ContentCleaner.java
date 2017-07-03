package Parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class ContentCleaner extends Cleaner {

    ContentCleaner(Document doc) {
        document = doc;
    }

    Document clean() {
        cleanBodyAttrs();
        cleanArticleTags();
        cleanBadTags();
        unwrapTextTags();
        removeEmptyTags();
        removeElements();
        cleanParaSpans();
        divToPara();

        return document;
    }

    private void cleanBodyAttrs() {
        Elements elements = document.getElementsByTag("body");

        if(elements.size() > 0) {
            elements.get(0).removeAttr("class");
            elements.get(0).removeAttr("id");
        }
    }

    private void cleanArticleTags() {
        Elements elements = document.getElementsByTag("article");

        for(Element element: elements) {
            element.removeAttr("id");
            element.removeAttr("name");
            element.removeAttr("class");
        }

        elements.remove();
    }

    private void unwrapTextTags() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("em"));
        elements.addAll(document.getElementsByTag("span"));

        for(Element element: elements) {
            element.unwrap();
        }
    }

    private void cleanBadTags() {
        Elements elements;

        for(String regex: elementsToRemove) {
            elements = document.select("*[id*=" + regex + "]");
            elements.remove();
            elements = document.select("*[class*=" + regex + "]");
            elements.remove();
        }
    }

    private void removeEmptyTags() {
        Elements elements = document.select("*");

        for(Element element: elements) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
    }

    private void cleanParaSpans() {
        Elements elements = document.select("p span");

        for(Element element : elements) {
            element.unwrap();
        }
    }

    private void divToPara() {
        Elements elements = document.select("div");

        document.clearAttributes();

        for(Element element: elements) {
            Element childElement = new Element("div");
            Elements childs = document.getElementsByTag("p");

            if(childs.size() > 1) {
                for(Element child: childs) {
                    childElement.appendElement("p").html(child.html());
                    child.unwrap();
                }

                element.html(childElement.html());
            }
            else {
                element.wrap("<p></p>");
            }
        }
    }

}