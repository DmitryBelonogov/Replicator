package Replicator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Loader {

    private String url;
    private LoaderCallback callback;
    private Options options;

    public Loader(String url, LoaderCallback callback) {
        this.url = url;
        this.callback = callback;

        startLoading();
    }

    public Loader(LoaderCallback callback) {
        this.callback = callback;
    }

    public void load(String html) {
        create(html);
    }

    public void set(String url, LoaderCallback callback) {
        this.url = url;
        this.callback = callback;
        startLoading();
    }

    public void load(String html, Options options) {
        create(html, options);
    }

    public void set(String url, Options options, LoaderCallback callback) {
        this.url = url;
        this.callback = callback;
        this.options = options;
        startLoading();
    }

    private void startLoading() {
        final String[] result = new String[1];
        final Request request = new Request.Builder().url(url).build();

        getUnsafeOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { callback.onFailure(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    if(response.body() != null)
                    result[0] = response.body().string();

                    if(options == null) {
                        create(result[0]);
                    }
                    else {
                        create(result[0], options);
                    }
                }
                else {
                    callback.onFailure();
                }
            }
        });
    }

    private void create(String html) {
        Article article = new Article(html);
        article.url = url;

        callback.onLoaded(article);
    }

    private void create(String html, Options options) {
        Article article = new Article(html, options);
        article.url = url;

        callback.onLoaded(article);
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        final X509TrustManager[] trustAllCerts = new X509TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException { }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException { }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
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
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        assert builder != null;
        return builder.build();
    }

}