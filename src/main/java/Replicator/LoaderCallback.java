package Replicator;

public interface LoaderCallback {

    void onLoaded(Article article);
    void onFailure();

}