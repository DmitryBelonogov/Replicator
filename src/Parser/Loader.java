package Parser;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Loader {

    private String url;
    private LoaderCallback callback;

    public Loader(String url, LoaderCallback callback) {
        this.url = url;
        this.callback = callback;

        startLoading();
    }

    public Loader() {
    }

    public void set(String url, LoaderCallback callback) {
        this.url = url;
        this.callback = callback;
        startLoading();
    }

    private void startLoading() {
        final long startTime = System.currentTimeMillis();
        final String[] result = new String[1];
        final Request request = new Request.Builder().url(url).build();

        getUnsafeOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { callback.onFailure(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    result[0] = response.body().string();
                    long endTime = System.currentTimeMillis();
                    System.out.println("Total loading time: " + (endTime-startTime) + "ms");
                    create(result[0]);
                }
                else {
                    callback.onFailure();
                }
            }
        });
    }

    private void create(String html) {
        long startTime = System.currentTimeMillis();
        Article article = new Article(html);
        article.url = url;
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");

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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (KeyManagementException e){
            e.printStackTrace();
        }

        assert builder != null;
        return builder.build();
    }

}