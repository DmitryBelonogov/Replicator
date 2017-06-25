package Parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;


public class ContentExtractor extends Extractor {

    public ContentExtractor(String htmlContent) {
        document = Jsoup.parse(htmlContent);
    }

    public String getLang() {
        List<String> langElements = new ArrayList<>();

        langElements.add(getElementAttr("html", "lang"));
        langElements.add(getElementAttr("html", "xml:lang"));
        langElements.sort(Comparator.comparingInt(String::length));

        if(langElements.size() == 0 ||
                langElements.get(langElements.size() - 1).length() < 2) {
            return detectLang();
        }

        lang = langElements.get(langElements.size() - 1).substring(0, 2).toLowerCase();

        System.out.println(lang);
        return lang;
    }

    public String getTitle() {
        List<String> titleElements = new ArrayList<>();

        titleElements.addAll(document.getElementsByTag("h1").eachText());
        titleElements.add(getElementAttr("title", "content"));
        titleElements.add(getMetaProperty("og:title"));
        titleElements.add(getMetaName("og:title"));
        titleElements.sort(Comparator.comparingInt(String::length));

        return titleElements.get(titleElements.size() - 1);
    }

    public String getDescription() {
        ArrayList<String> descElements = new ArrayList<>();

        descElements.add(getMetaName("twitter:description"));
        descElements.add(getMetaName("description"));
        descElements.add(getMetaProperty("og:description"));
        descElements.sort(Comparator.comparingInt(String::length));

        return descElements.get(descElements.size() - 1);
    }

    public String getContent() {
        if(contentElement == null) {
            clean();
            contentElement = calculateBestElement();
        }

        return contentElement.html();
    }

    public String getContentText() {
        if(contentElement == null) {
            getContent();
        }

        return contentElement.text();
    }

    public String getLeadImage() {
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

    public String getKeywords() {
        StringBuilder keywords = new StringBuilder();
        ArrayList<String> keywordsList = new ArrayList<>();

        keywordsList.addAll(getMetaNames("keywords"));
        keywordsList.addAll(getMetaProperties("article:tag"));

        if(keywordsList.size() == 0) {
            return generateKeywords();
        }

        for(String key: keywordsList) {
            keywords.append(key).append(", ");
        }

        return keywords.toString().substring(0, keywords.length() - 2);
    }

}
