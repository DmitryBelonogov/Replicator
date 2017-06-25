package Parser;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Extractor {

    String lang;
    Document document;
    Element contentElement;

    Extractor() { }

    void clean() {
        ContentCleaner cleaner = new ContentCleaner(document);
        document = cleaner.clean();
    }

    String detectLang() {
        lang = "ru";
        return lang;
    }

    String generateKeywords() {
        return "";
    }

    Element calculateBestElement() {
        Elements elementsText = new Elements();
        Elements elementsToCheck = getElementsToCheck();

        for(Element elementToCheck: elementsToCheck) {
            elementsToCheck.attr("score", String.valueOf(isHighlinkDensity(elementToCheck)));
            if(Stopwords.getInstance().getStopwordsCount(lang, elementToCheck.text()) > 4 &&
                    !isHighlinkDensity(elementToCheck)) {
                elementsText.add(elementToCheck);
            }
        }

        int topScore = 0;
        Element topElement = new Element("p");

        for(Element elementText: elementsText) {
            int score = Stopwords.getInstance().getStopwordsCount(lang, elementText.text());

            if(score > topScore) {
                topScore = score;
                topElement = elementText;
            }
        }

        return topElement;
    }

    private boolean isHighlinkDensity(Element element) {
        Elements linkElements = element.select("a");

        if(linkElements.size() == 0) {
            return false;
        }

        int wordsCount = Stopwords.getInstance().getWordsCount(element.ownText());
        int linkWordsCount = 0;

        for(Element link: linkElements) {
            linkWordsCount += Stopwords.getInstance().getWordsCount(link.ownText());
        }

        return (float) linkWordsCount / (float) wordsCount * (float) linkElements.size() >= 1;
    }

    private Elements getElementsToCheck() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("p"));
        elements.addAll(document.getElementsByTag("pre"));
        elements.addAll(document.getElementsByTag("td"));
        elements.addAll(document.getElementsByTag("div"));

        return elements;
    }

    String getElementAttr(String element, String attr) {
        Elements elements = document.getElementsByTag("html");

        return elements.get(0).attr(attr) != null ? elements.get(0).attr(attr) : "";
    }

    String getMetaName(String metaTag) {
        Elements elements = document.select("meta[name=" + metaTag + "]");

        return elements.size() > 0 ?
                elements.get(0).attr("content") : "";
    }

    ArrayList<String> getMetaNames(String metaTag) {
        ArrayList<String> values = new ArrayList<>();

        Elements elements = document.select("meta[name=" + metaTag + "]");

        for(Element element: elements) {
            values.add(element.attr("content"));
        }

        return values;
    }

    public ArrayList<String> getMetaProperties(String metaTag) {
        ArrayList<String> values = new ArrayList<>();

        Elements elements = document.select("meta[property=" + metaTag + "]");

        for(Element element: elements) {
            values.add(element.attr("content"));
        }

        return values;
    }

    public String getMetaProperty(String metaTag) {
        Elements elements = document.select("meta[property=" + metaTag + "]");

        return elements.size() > 0 ?
                elements.get(0).attr("content") : "";
    }
}
