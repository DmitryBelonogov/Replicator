package Parser;

public interface LoaderCallback {

    void onLoaded(Article article);
    void onFailure();

}