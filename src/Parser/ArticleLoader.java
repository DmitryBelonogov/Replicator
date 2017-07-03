package Parser;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ArticleLoader {

    private String url;
    private ArticleLoaderCallback callback;

    public ArticleLoader(String url, ArticleLoaderCallback callback) {
        this.url = url;
        this.callback = callback;

        startLoading();
    }

    private void startLoading() {
        final String[] result = new String[1];
        Request request = new Request.Builder().url(url).build();

        getUnsafeOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result[0] = response.body().string();
                create(result[0]);
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

    private static OkHttpClient getUnsafeOkHttpClient() {
        final X509TrustManager[] trustAllCerts = new X509TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws CertificateException { }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws CertificateException { }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        OkHttpClient.Builder builder = null;
        SSLContext sslContext;
        SSLSocketFactory sslSocketFactory;

        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

            builder = new OkHttpClient()
                    .newBuilder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        assert builder != null;
        return builder.build();
    }

}
