package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TagsExtractor extends BaseExtractor {

    private List<String> candidates = new ArrayList<>();

    public TagsExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public List<String> get() {
        checkMeta();
        checkTags();

        return getTags();
    }

    private List<String> getTags() {
        return candidates;
    }

    private void checkTags() {
        Elements tagsElements = document.select("a[rel*=tag]");

        if(tagsElements.size() > 0) {
            for(Element tagElement: tagsElements) {
                if(tagElement.hasText()) {
                    candidates.add(tagElement.text());
                }
            }

            return;
        }

        if((tagsElements = document.select("a[href*=tag], a[href*=topic], a[href*=keyword]")).size() > 0) {
            if(tagsElements.size() > 0) {
                for(Element tagElement: tagsElements) {
                    if(tagElement.hasText()) {
                        candidates.add(tagElement.text());
                    }
                }
            }
        }
    }

    private void checkMeta() {
        String keywords = getMetaAttr("name","keywords", "content");

        if(keywords.length() > 0) {
            String[] tags = keywords.split(",");

            for(int i = 0, s = 0; i < tags.length; i++, s = 0) {
                for(int j = 0; j < tags[i].length(); j++) {
                    if(tags[i].charAt(j) == ' ') s++;
                }
                if(s < 3) {
                    if(tags[i].charAt(0) == ' ') {
                        tags[i] = tags[i].substring(1);
                    }
                    candidates.add(tags[i]);
                }
            }
        }
    }
}