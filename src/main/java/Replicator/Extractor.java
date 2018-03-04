package Replicator;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Replicator.Extractors.AuthorExtractor;
import Replicator.Extractors.ContentExtractor;
import Replicator.Extractors.DescriptionExtractor;
import Replicator.Extractors.ImagesExtractor;
import Replicator.Extractors.LanguageExtractor;
import Replicator.Extractors.TagsExtractor;
import Replicator.Extractors.TitleExtractor;

class Extractor {

    private Document document;
    private Element contentElement;
    private String lang;
    private Elements metaTags;
    private Options options;

    Extractor(String htmlContent, Options options) {
        this.options = options;

        document = Jsoup.parse(htmlContent);
        metaTags = document.getElementsByTag("meta");
    }

    String getLang() {
        lang = new LanguageExtractor(document, metaTags).get();
        return lang;
    }

    String getTitle() {
        return new TitleExtractor(document, metaTags).get();
    }

    String getDescription() {
        return new DescriptionExtractor(document, metaTags).get();
    }

    String getAuthor() {
        return new AuthorExtractor(document, metaTags).get();
    }

    List<String> getTags() {
        return new TagsExtractor(document, metaTags).get();
    }

    String getContent() {
        if(contentElement == null) {
            document = new Cleaner(document).clean();
            contentElement = new ContentExtractor(document, metaTags, lang).get();
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
        return new ImagesExtractor(document, metaTags, contentElement, options).get();
    }

}