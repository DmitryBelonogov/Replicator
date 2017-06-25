package Parser;

public class Article {

    public String url;
    public String lang;
    public String title;
    public String description;
    public String keywords;
    public String content;
    public String contentText;
    public String leadImage;

    public Article(String html) {
        ContentExtractor extractor = new ContentExtractor(html);

        lang = extractor.getLang();
        title = extractor.getTitle();
        description = extractor.getDescription();
        keywords = extractor.getKeywords();
        content = extractor.getContent();
        contentText = extractor.getContentText();
        leadImage = extractor.getLeadImage();
    }

}
