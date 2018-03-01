package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Replicator.StopWords;

public class LanguageExtractor extends BaseExtractor {

    private String lang;

    public LanguageExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public String get() {
        if(checkHtml()) return getLang();
        if(checkMeta()) return getLang();

        lang = detectLang(document.getElementsByTag("body").text());

        return lang;
    }

    private String getLang() {
        if(lang.length() > 2) {
            return lang.substring(0, 2).toLowerCase();
        }

        return lang;
    }

    private boolean checkHtml() {
        Element html = document.getElementsByTag("html").get(0);

        if(html.hasAttr("lang")) {
            lang = html.attr("lang");
            return true;
        }

        if(html.hasAttr("xml:lang")) {
            lang = html.attr("xml:lang");
            return true;
        }

        return false;
    }

    private boolean checkMeta() {
        String meta;

        if((meta = getMetaAttr("http-equiv", "lang", "content-language")).length() > 0) {
            lang = meta;
            return true;
        }

        if((meta = getMetaAttr("name", "lang", "content")).length() > 0) {
            lang = meta;
            return true;
        }

        if((meta = getMetaAttr("property", "og:locale", "content")).length() > 0) {
            lang = meta;
            return true;
        }

        return false;
    }

    private String detectLang(String text) {
        String lang = "";
        int stopWordsCount = 0;

        for(String wordsLang : StopWords.getInstance().stopWords.keySet()) {
            int count = StopWords.getInstance().getStopWordsCount(wordsLang, text);

            if(count > stopWordsCount) {
                stopWordsCount = count;
                lang = wordsLang;
            }
        }


        return lang;
    }
}