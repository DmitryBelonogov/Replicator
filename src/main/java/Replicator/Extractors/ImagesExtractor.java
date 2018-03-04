package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import Replicator.Loader;
import Replicator.Options;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class ImagesExtractor extends BaseExtractor {

    private String leadImage = "";
    private Element contentElement;
    private Options options;

    public ImagesExtractor(Document document, Elements metaTags, Element contentElement, Options options) {
        super(document, metaTags);

        this.contentElement = contentElement;
        this.options = options;
    }

    public String get() {
        if(checkMeta()) return leadImage;
        if(checkContent()) return leadImage;

        return leadImage;
    }

    private boolean checkContent() {
        Elements imagesElements = contentElement.select("img[src]");

        if(options.useNetwork) {
            for(Element image: imagesElements) {
                Response response;
                Request request = new Request.Builder()
                        .url(image.attr("src")).head()
                        .build();

                try {
                    response = Loader.getHttpClient().newCall(request).execute();
                } catch (IOException e) {
                    continue;
                }

                Headers responseHeaders = response.headers();

                for(int i = 0; i < responseHeaders.size(); i++) {
                    if(responseHeaders.name(i).equals("Content-Length")) {
                        if(Integer.valueOf(responseHeaders.value(i)) > 80000) {
                            leadImage = image.attr("src");
                            return true;
                        }
                    }
                }
            }
        }
        else {
            leadImage = imagesElements.get(0).attr("src");

            if(leadImage.length() > 0) {
                return true;
            }
        }

        return false;
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