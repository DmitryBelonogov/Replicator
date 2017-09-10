package Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Extractor {

    private Document document;
    private Element contentElement;
    private String lang;

    Extractor(String htmlContent) {
        document = Jsoup.parse(htmlContent);
    }

    String getLang() {
        List<String> langElements = new ArrayList<String>();

        langElements.add(getElementAttr("html", "lang"));
        langElements.add(getElementAttr("html", "xml:lang"));

        Collections.sort(langElements, new StringLengthComp());

        if(langElements.size() == 0 ||
                langElements.get(langElements.size() - 1).length() < 2) {
            lang = detectLang(document.getElementsByTag("body").text());
        }
        else  {
            lang = langElements.get(langElements.size() - 1).substring(0, 2).toLowerCase();
        }

        return lang;
    }

    private String detectLang(String text) {
        String lang = "";
        int stopwordsCount = 0;

        for(String wordsLang : Stopwords.getInstance().stopwords.keySet()) {
            int count = Stopwords.getInstance().getStopwordsCount(wordsLang, text);

            if(count > stopwordsCount) {
                stopwordsCount = count;
                lang = wordsLang;
            }
        }

        return lang;
    }

    String getTitle() {
        List<String> titleElements = new ArrayList<String>();

        titleElements.add(document.getElementsByTag("title").text());

        if(titleElements.get(0).length() > 0) {
            return splitTitle(titleElements.get(titleElements.size() - 1));
        }

        titleElements.addAll(document.getElementsByTag("h1").eachText());
        titleElements.add(getElementAttr("title", "content"));
        titleElements.add(getMetaProperty("og:title"));
        titleElements.add(getMetaName("og:title"));

        Collections.sort(titleElements, new StringLengthComp());

        return splitTitle(titleElements.get(titleElements.size() - 1));
    }

    private String splitTitle(String title) {
        String splitter = "";

        if(title.contains("|")) {
            splitter = String.valueOf(" \\| ");
        }
        else if(title.contains("_")) {
            splitter = " _ ";
        }
        else if(title.contains("/")) {
            splitter = " / ";
        }
        else if(title.contains("»")) {
            splitter = " » ";
        }
        else if(title.contains("–")) {
            splitter = " – ";
        }
        else if(title.contains(" - ")) {
            splitter = " - ";
        }

        return !splitter.equals("") ? title.split(splitter)[0] : title;
    }

    String getDescription() {
        ArrayList<String> descElements = new ArrayList<String>();

        descElements.add(getMetaName("twitter:description"));
        descElements.add(getMetaName("description"));
        descElements.add(getMetaProperty("og:description"));

        Collections.sort(descElements, new StringLengthComp());

        return descElements.get(descElements.size() - 1);
    }

    String getContent() {
        if(contentElement == null) {
            document = new Cleaner(document).clean();
            contentElement = calculateBestElement();
        }

        return contentElement.html();
    }

    String getContentText() {
        if(contentElement == null) {
            getContent();
        }

        return contentElement.text();
    }

    String getLeadImage() {
        String image;

        image = getMetaProperty("og:image");
        if(image.length() > 0) {
            return image;
        }

        image = getMetaName("og:image");
        if(image.length() > 0) {
            return image;
        }

        if(contentElement == null) {
            getContent();
        }

        Element imageElement = contentElement.getElementsByTag("img").first();
        if(imageElement != null) {
            return imageElement.attr("src");
        }

        return "";
    }

    String getKeywords() {
        StringBuilder keywords = new StringBuilder();
        ArrayList<String> keywordsList = new ArrayList<String>();

        keywordsList.addAll(getMetaNames("keywords"));
        keywordsList.addAll(getMetaProperties("article:tag"));

        if(keywordsList.size() == 0) {
            //return generateKeywords();
            return "";
        }

        for(String key: keywordsList) {
            keywords.append(key).append(", ");
        }

        return keywords.toString().substring(0, keywords.length() - 2);
    }

    private Element calculateBestElement() {
        Elements elementsText = new Elements();
        Elements elementsToCheck = getElementsToCheck();

        int size = elementsToCheck.size();
        for(int i = 0; i < size; i++) {
            if(Stopwords.getInstance().getStopwordsCount(lang, elementsToCheck.get(i).text()) > 2) {
                if (!isHighlinkDensity(elementsToCheck.get(i))) {
                    elementsText.add(elementsToCheck.get(i));
                }
            }
        }

        int topScore = 0;
        Element topElement = new Element("p");

        size = elementsText.size();
        for(int i = 0; i < size; i++) {
            int score = Stopwords.getInstance().getStopwordsCount(lang, elementsText.get(i).text());

            if(score > topScore) {
                topScore = score;
                topElement = elementsText.get(i);
            }
        }

        return topElement;
    }

    private boolean isHighlinkDensity(Element element) {
        Elements linkElements = element.select("a");

        if(linkElements.size() == 0) {
            return false;
        }

        int wordsCount = Stopwords.getInstance().getWordsCount(element.text());
        int linkWordsCount = 0;

        for(Element link: linkElements) {
            linkWordsCount += Stopwords.getInstance().getWordsCount(link.text());
        }

        return (float) linkWordsCount / (float) wordsCount * (float) linkElements.size() >= 1;
    }

    private Elements getElementsToCheck() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("p"));
        elements.addAll(document.getElementsByTag("pre"));
        elements.addAll(document.getElementsByTag("td"));
        elements.addAll(document.getElementsByTag("section"));

        return elements;
    }

    private String getElementAttr(String element, String attr) {
        Elements elements = document.getElementsByTag(element);

        return elements.size() > 0 ? elements.get(0).attr(attr) : "";
    }

    private String getMetaName(String metaTag) {
        Elements elements = document.select("meta[name=" + metaTag + "]");

        return elements.size() > 0 ?
                elements.get(0).attr("content") : "";
    }

    private ArrayList<String> getMetaNames(String metaTag) {
        ArrayList<String> values = new ArrayList<String>();

        Elements elements = document.select("meta[name=" + metaTag + "]");

        for(Element element: elements) {
            values.add(element.attr("content"));
        }

        return values;
    }

    private ArrayList<String> getMetaProperties(String metaTag) {
        ArrayList<String> values = new ArrayList<String>();

        Elements elements = document.select("meta[property=" + metaTag + "]");

        for(Element element: elements) {
            values.add(element.attr("content"));
        }

        return values;
    }

    private String getMetaProperty(String metaTag) {
        Elements elements = document.select("meta[property=" + metaTag + "]");

        return elements.size() > 0 ?
                elements.get(0).attr("content") : "";
    }

    public static class StringLengthComp implements Comparator<String> {

        public int compare(String o1, String o2) {
            return Integer.compare(o1.length(), o2.length());
        }

    }

}