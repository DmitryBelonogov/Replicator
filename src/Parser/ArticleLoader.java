package Parser;

import okhttp3.*;

import java.io.IOException;

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

        new OkHttpClient()
                .newCall(new Request.Builder().url(url).build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.body() != null) {
                        result.append(response.body().string());
                        create(result.toString());
                    }
                }
        });
    }

    public Article fromHtml(String html) {
        return new Article(html);
    }

    private void create(String html) {
        Article article = new Article(html);
        article.url = url;

        callback.onLoaded(article);
    }

}
