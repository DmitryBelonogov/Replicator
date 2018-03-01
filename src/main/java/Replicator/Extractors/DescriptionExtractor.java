package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DescriptionExtractor extends BaseExtractor {

    public DescriptionExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public String get() {
        String desc;

        if((desc = getMetaAttr("name","description", "content")).length() > 0) {
            return desc;
        }

        if((desc = getMetaAttr("property", "og:description", "content")).length() > 0) {
            return desc;
        }

        if((desc = getMetaAttr("name", "twitter:description", "content")).length() > 0) {
            return desc;
        }

        return "";
    }
}