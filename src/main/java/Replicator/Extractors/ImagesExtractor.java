package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ImagesExtractor extends BaseExtractor {

    private String leadImage = "";

    public ImagesExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public String get() {
        if(checkMeta()) return leadImage;

        return leadImage;
    }

    private boolean checkMeta() {
        String image;

        if((image = getMetaAttr("name","description", "content")).length() > 0) {
            leadImage = image;
            return true;
        }

        if((image = getMetaAttr("property", "og:description", "content")).length() > 0) {
            leadImage = image;
            return true;
        }

        if((image = getMetaAttr("name", "twitter:description", "content")).length() > 0) {
            leadImage = image;
            return true;
        }

        return false;
    }
}