package Replicator.Extractors;

import Replicator.StopWords;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class ContentExtractor extends BaseExtractor {

    private String lang;

    public ContentExtractor(Document document, Elements metaTags, String lang) {
        super(document, metaTags);
        this.lang = lang;
    }

    public Element get() {
        return getBest();
    }

    private Element getBest() {
        Map<Element, Integer> candidates = getCandidates();

        candidates = getScore(candidates);

        return bestScore(candidates);
    }

    private Element bestScore(Map<Element, Integer> candidates) {
        int topScore = 0;
        Element topElement = new Element("p");

        for(Map.Entry<Element, Integer> candidate: candidates.entrySet()) {
            if(candidate.getKey().hasAttr("Score")) {
                String scoreAttr = candidate.getKey().attr("Score");

                if(scoreAttr.length() > 0) {
                    int score = Integer.parseInt(scoreAttr);

                    if(score >= topScore) {
                        topScore = score;
                        topElement = candidate.getKey();
                    }
                }
            }
        }

        return topElement;
    }

    private Map<Element, Integer> getScore(Map<Element, Integer> candidates) {
        for(Map.Entry<Element, Integer> candidate: candidates.entrySet()) {
            int score = StopWords.getInstance()
                    .getStopWordsCount(lang, candidate.getKey().text());

            if(score > 2 && checkLinks(candidate.getKey())) {
                Elements children = candidate.getKey().children();

                for(Element child: children) {
                    if(child.hasText()) {
                        child.attr("Score", Integer.toString(getScore(child)));
                    }
                }
            }
        }

        return candidates;
    }

    private int getScore(Element child) {
        return StopWords.getInstance().getStopWordsCount(lang, child.text());
    }

    private boolean checkLinks(Element element) {
        Elements linkElements = element.select("a");

        if(linkElements.size() == 0) {
            return false;
        }

        int wordsCount = StopWords.getWordsCount(element.text());
        int linkWordsCount = 0;

        for(Element link: linkElements) {
            linkWordsCount += StopWords.getWordsCount(link.text());
        }

        return (float) linkWordsCount / (float) wordsCount * (float) linkElements.size() >= 1;
    }

    private Map<Element, Integer> getCandidates() {
        Map<Element, Integer> candidates = new HashMap<>();
        Elements elements = getElementsToCheck();

        for(Element element: elements) {
            if(element.hasText()) {
                candidates.put(element, 0);
            }
        }

        return candidates;
    }

    private Elements getElementsToCheck() {
        Elements elements = new Elements();

        elements.addAll(document.getElementsByTag("p"));
        elements.addAll(document.getElementsByTag("td"));
        elements.addAll(document.getElementsByTag("div"));
        elements.addAll(document.getElementsByTag("section"));

        return elements;
    }
}