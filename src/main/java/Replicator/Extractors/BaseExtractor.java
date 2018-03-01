package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class BaseExtractor {

    Document document;
    private Elements metaTags;

    BaseExtractor(Document document, Elements metaTags) {
        this.document = document;
        this.metaTags = metaTags;
    }

    String getMetaAttr(String attr, String name, String value) {
        for (Element metaTag : metaTags) {
            if (metaTag.hasAttr(attr)) {
                if(metaTag.attr(attr).equals(name)) {
                    return metaTag.attr(value);
                }
            }
        }

        return "";
    }
}