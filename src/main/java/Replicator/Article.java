package Replicator;

import java.util.List;

public class Article {

    public String url;
    public String lang;
    public String title;
    public String description;
    public String content;
    public String author;
    public String contentText;
    public String leadImage;

    public List<String> tags;

    public Article(String html) {
        Extractor extractor = new Extractor(html);

        lang = extractor.getLang();
        tags = extractor.getTags();
        title = extractor.getTitle();
        description = extractor.getDescription();
        author = extractor.getAuthor();
        content = extractor.getContent();
        contentText = extractor.getContentText();
        leadImage = extractor.getLeadImage();
    }

}