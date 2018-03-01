package Replicator.Extractors;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class AuthorExtractor extends BaseExtractor {

    private Map<String, Integer> candidates = new HashMap<>();

    public AuthorExtractor(Document document, Elements metaTags) {
        super(document, metaTags);
    }

    public String get() {
        checkMeta();

        return getAuthor();
    }

    private void checkMeta() {
        String candidate;
        String[] attributes = {"creator", "dc.creator", "author", "dcterms.creator",
                "article:author", "og:article:author"};

        for(String attribute : attributes) {
            if ((candidate = getMetaAttr("name", attribute, "content")).length() > 0) {
                candidates.put(candidate, 0);
            }
            if ((candidate = getMetaAttr("property", attribute, "content")).length() > 0) {
                candidates.put(candidate, 0);
            }
        }
    }

    private String getAuthor() {
        for(String candidate: candidates.keySet()) {
            int score = 0;
            int upper = 0;
            int spaces = 0;

            if(candidate.length() > 10 && candidate.length() < 28) {
                score += 2;
            }
            if(candidate.matches("[0-9]")) {
                score -= 5;
            }

            for(int i = 0; i < candidate.length(); i++) {
                char c = candidate.charAt(i);
                if(c == ' ') spaces++;
                if(Character.isUpperCase(c)) upper++;
            }

            score += spaces < 2 ? 2 : 0;
            score += upper < 3 ? 2 : 0;

            candidates.put(candidate, score);
        }

        Map.Entry<String, Integer> candidate = null;
        for(Map.Entry<String, Integer> entry: candidates.entrySet()) {
            if(candidate == null || entry.getValue().compareTo(candidate.getValue()) > 0) {
                candidate = entry;
            }
        }

        if(candidate != null) {
            return candidate.getKey();
        }

        return "";
    }
}