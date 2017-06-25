package Parser;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ArticleLoader {

    private String url;
    private ArticleLoaderCallback callback;

    public ArticleLoader(String url, ArticleLoaderCallback callback) {
        this.url = url;
        this.callback = callback;

        startLoading();
    }

    private void startLoading() {
        StringBuilder result = new StringBuilder();

        try {
            URL curl = new URL(url);
            Scanner scanner = new Scanner(curl.openStream());

            while (scanner.hasNextLine())
                result.append(scanner.nextLine());

            create(result.toString());
        }
        catch(IOException ignored) { }
    }

    private void create(String html) {
        long startTime = System.currentTimeMillis();

        Article article = new Article(html);
        article.url = url;

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");

        callback.onLoaded(article);
    }

}
