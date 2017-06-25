package Parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class ContentCleaner extends Cleaner {

    ContentCleaner(Document doc) {
        document = doc;
    }

    public Document clean() {
        cleanBodyClasses();
        cleanArticleTags();
        removeDropCaps();
        cleanBadTags();
        unwrapTextTags();
        removeEmptyTags();
        removeElements();
        removeNodesRegex();
        cleanParaSpans();
        divToPara();

        return document;
    }

    private void cleanBodyClasses() {
        Elements elements = document.getElementsByTag("body");

        if(elements.size() > 0) {
            elements.get(0).removeAttr("class");
        }
    }

    private void cleanArticleTags() {
        Elements elements = document.getElementsByTag("article");

        for(Element element: elements) {
            element.removeAttr("id");
            element.removeAttr("name");
            element.removeAttr("class");
        }


        elements = document.getElementsByTag("header");
        elements.addAll(document.getElementsByTag("footer"));
        elements.addAll(document.getElementsByTag("form"));

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

    private void removeDropCaps() {
        Elements elements = document.select("span[class~=dropcap], span[class~=drop_cap]");

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

    private void removeNodesRegex() {
        Elements elements = document.getElementsMatchingOwnText("//*[re:test(@%s, id, 'i')]");
        System.out.println("regex " + elements.size());

        for(Element element: elements) {
            element.remove();
        }

        elements = document.getElementsMatchingOwnText("//*[re:test(@%s, class, 'i')]");

        for(Element element: elements) {
            element.remove();
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
            Elements childs = document.getElementsByTag("p");

            for(Element child: childs) {
                child.unwrap();
            }

            element.wrap("<p></p>");
        }
    }

}
