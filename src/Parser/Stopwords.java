package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Stopwords {

    private String[] langs = ("ar da de el en es fi fr he hu id it ko mk nb nl no pt ru sv tr vi zh").split(" ");

    public HashMap<String, ArrayList<String>> stopwords;

    private static Stopwords ourInstance = new Stopwords();

    public static Stopwords getInstance() {
        return ourInstance;
    }

    private Stopwords() {
        stopwords = new HashMap<>();

        try {
            populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populate() throws IOException {
        for(String lang: langs) {
            InputStream langFile = getClass().getResourceAsStream(lang + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(langFile));
            ArrayList<String> words = new ArrayList<>();
            String word;

            while((word = reader.readLine()) != null) {
                words.add(word);
            }

            stopwords.put(lang, words);
        }
    }

    public ArrayList<String> get(String lang) {
        return stopwords.get(lang);
    }

    public int getStopwordsCount(String lang, String text) {
        int count = 0;
        ArrayList<String> stopWords = stopwords.get(lang);
        String[] words = text.replaceAll("\\p{Punct}|\\d", " ").toLowerCase().split("\\s+");

        for(String word: words) {
            for(String stopWord: stopWords) {
                if(word.length() == stopWord.length()) {
                    if (word.equals(stopWord)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public int getWordsCount(String text) {
        String[] words = text.replaceAll("\\p{Punct}|\\d", " ").split("\\s+");

        return words.length;
    }

}
