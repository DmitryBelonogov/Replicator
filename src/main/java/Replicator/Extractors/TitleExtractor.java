package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TitleExtractor extends BaseExtractor {

    private String titleTag = "";
    private String titleMeta = "";

    public TitleExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public String get() {
        checkTitle();

        if(titleTag.length() > 14) {
            return getTitle(titleTag);
        }

        checkMeta();

        return getTitle(titleMeta);
    }

    private void checkTitle() {
        Element title = document.getElementsByTag("title").first();

        if(title != null && title.hasText()) {
            titleTag = title.text();
        }
    }

    private void checkMeta() {
        String title;

        if((title = getMetaAttr("name","title", "content")).length() > 0) {
            titleMeta = title;
        }

        if((title = getMetaAttr("property", "og:title", "content")).length() > 0) {
            titleMeta = title;
        }

        if((title = getMetaAttr("name", "og:title", "content")).length() > 0) {
            titleMeta = title;
        }
    }

    private String getTitle(String title) {
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

        if(splitter.length() > 0) {
            return title.split(splitter)[0];
        }
        else {
            return title;
        }
    }
}