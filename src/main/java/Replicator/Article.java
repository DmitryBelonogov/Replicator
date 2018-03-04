package Replicator;

import java.util.ArrayList;
import java.util.List;

public class Article {

    public String url;
    public String lang;
    public String title;
    public String description;
    public String author;
    public String leadImage;
    public String content = "";
    public String contentText = "";

    public List<String> tags = new ArrayList<>();

    public Article(String html) {
        get(html, new Options());
    }
    public Article(String html, Options options) {
        get(html, options);
    }

    private void get(String html, Options options) {
        Extractor extractor = new Extractor(html, options);

        lang = extractor.getLang();

        title = options.loadTitle ? extractor.getTitle() : "";
        description = options.loadDescription ? extractor.getDescription() : "";
        author = options.loadAuthor ? extractor.getAuthor() : "";

        if(options.loadTags) tags = extractor.getTags();
        if(options.loadContent) {
            content = extractor.getContent();
            contentText = extractor.getContentText();
        }
        
        leadImage = options.loadImages ? extractor.getLeadImage() : "";
    }

}