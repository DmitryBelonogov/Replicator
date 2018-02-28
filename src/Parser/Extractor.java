package Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Extractor {

    private static final Pattern DIGITS = Pattern.compile("[^0-9]");
    private Document document;
    private Element contentElement;
    private String lang;

    private Elements metaTags;

    Extractor(final String htmlContent) {
        document = Jsoup.parse(htmlContent);
    }

    String getLang() {
        List<String> langElements = new ArrayList<>(5);

        langElements.add(getElementAttr("html", "lang"));
        langElements.add(getElementAttr("html", "xml:lang"));
        langElements.add(getMetaAttr("http-equiv", "lang", "content-language"));
        langElements.add(getMetaAttr("name", "lang", "content"));
        langElements.add(getMetaAttr("property", "og:locale", "content"));

        Collections.sort(langElements, new StringLengthComp());

        if((langElements.isEmpty()) ||
                (langElements.get(langElements.size() - 1).length() < 2)) {
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

        titleElements.add(document.getElementsByTag("h1").first().text());
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
        List<String> descElements = new ArrayList<>(4);

        descElements.add(getMetaAttr("name","twitter:description", "content"));
        descElements.add(getMetaAttr("name","og:description", "content"));
        descElements.add(getMetaAttr("name","dc:description", "content"));
        descElements.add(getMetaAttr("name","description", "content"));

        Collections.sort(descElements, new StringLengthComp());

        return descElements.get(descElements.size() - 1);
    }

    String getAuthor() {
        final List<String> authorElements = new ArrayList<>(8);

        authorElements.add(getMetaAttr("name", "creator", "content"));
        authorElements.add(getMetaAttr("name", "dc.creator", "content"));
        authorElements.add(getMetaAttr("name", "DC.creator", "content"));
        authorElements.add(getMetaAttr("name", "DC.Creator", "content"));
        authorElements.add(getMetaAttr("name", "dcterms.creator", "content"));
        authorElements.add(getMetaAttr("name", "author", "content"));
        authorElements.add(getMetaAttr("property", "article:author", "content"));
        authorElements.add(getMetaAttr("property", "og:article:author", "content"));

        int size = authorElements.size();
        for(int i = 0; i < size; i++) {
            if(DIGITS.matcher(authorElements.get(i)).matches()) {
                authorElements.set(i, "");
            }
        }

        Collections.sort(authorElements, new StringLengthComp());

        if(authorElements.get(authorElements.size() - 1).length() > 0) {
            return authorElements.get(authorElements.size() - 1);
        }

        Elements author;
        if((author = document.select("span[class*='author']")).size() > 0 ||
                (author = document.select("p[class*='author']")).size() > 0 ||
                (author = document.select("div[class*='author']")).size() > 0 ||
                (author = document.select("span[class*='byline']")).size() > 0 ||
                (author = document.select("p[class*='byline']")).size() > 0 ||
                (author = document.select("div[class*='byline']")).size() > 0) {
            if(author.get(0).hasText()) {
                if(author.get(0).children().size() > 0) {
                    for(Element authorElement: author.get(0).children()) {
                        if(!authorElement.text().matches("[^0-9]")) {
                            return authorElement.text();
                        }
                    }
                }
                else {
                    return author.get(0).text();
                }
            }
        }

        return "";
    }

    ArrayList<String> getTags() {
        ArrayList<String> tagsList = new ArrayList<>();

        Elements tagsElements = document.select("a[rel*=tag]");

        tagsElements.addAll(document.select("a[href*=tag], a[href*=topic], a[href*=keyword]"));

        for(Element tagElement: tagsElements) {
            if(tagElement.text().length() > 0) {
                tagsList.add(tagElement.text());
            }
        }

        return tagsList;
    }

    String getContent() {
        if(contentElement == null) {
            long startTime = System.currentTimeMillis();
            document = new Cleaner(document).clean();
            long endTime = System.currentTimeMillis();
            System.out.println("Total clean time: " + (endTime-startTime) + "ms\n\n ");
            startTime = System.currentTimeMillis();
            contentElement = calculateBestElement();
            endTime = System.currentTimeMillis();
            System.out.println("Total calculate time: " + (endTime-startTime) + "ms\n\n ");
            System.out.println("-");
            System.out.println("-");
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
        ArrayList<String> keywordsList = new ArrayList<>();

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
        Elements elementsToCheck = getElementsToCheck();

        for(int i = 0; i < elementsToCheck.size(); i++) {
            int score = getScore(elementsToCheck.get(i).text());

            if(score > 2 && !isHighlinkDensity(elementsToCheck.get(i))) {
                Elements childrens = elementsToCheck.get(i).children();

                for(Element children: childrens) {
                    children.attr("Score", Integer.toString(getScore(children.text())));
                }
            }
        }

        Elements els = document.getElementsByAttribute("Score");


        int topScore = 0;
        Element topElement = new Element("p");

        //System.out.println("Size elementsText " + size);
        for(int i = 0; i < els.size(); i++) {
            //int score = Stopwords.getInstance().getStopwordsCount(lang, elementsText.get(i).text());

            //System.out.println(score);
            //System.out.println(els.get(i).text());

            //elementsText.get(i).append(String.valueOf(score));

            int score;
            String scoreAttr = els.get(i).attr("Score");

            if(scoreAttr.length() > 0) {
                score = Integer.parseInt(els.get(i).attr("Score"));
            }
            else {
                score = 0;
            }

            if(score >= topScore) {
                topScore = score;

                topElement = els.get(i);
            }

        }

        return topElement;
    }

    private int getScore(String text) {
        return Stopwords.getInstance().getStopwordsCount(lang, text);
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

        //System.out.print("pp - ");
        //System.out.print((float) linkWordsCount / (float) wordsCount * (float) linkElements.size());
        //System.out.println((float) linkWordsCount / (float) wordsCount * (float) linkElements.size() >= 1);

        return (float) linkWordsCount / (float) wordsCount * (float) linkElements.size() >= 1;
    }

    private Elements getElementsToCheck() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("p"));
        elements.addAll(document.getElementsByTag("td"));
        elements.addAll(document.getElementsByTag("div"));
        elements.addAll(document.getElementsByTag("section"));

        return elements;
    }

    private String getElementAttr(String element, String attr) {
        Elements elements = document.getElementsByTag(element);

        return elements.size() > 0 ? elements.get(0).attr(attr) : "";
    }

    private String getMetaAttr(String attr, String name, String value) {
        if(metaTags == null || metaTags.size() == 0) {
            metaTags = document.getElementsByTag("meta");
        }

        if(metaTags != null) {
            for (Element metaTag : metaTags) {
                if (metaTag.hasAttr(attr)) {
                    if(metaTag.attr(attr).equals(name)) {
                        return metaTag.attr(value);
                    }
                }
            }
        }

        return "";
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