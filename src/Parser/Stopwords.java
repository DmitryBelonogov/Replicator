package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

class Stopwords {

    private String[] langs = ("ar da de el en es fi fr he hu id it ko mk nb nl no pt ru sv tr vi zh").split(" ");

    HashMap<String, ArrayList<String>> stopwords;

    private static Stopwords ourInstance = new Stopwords();

    static Stopwords getInstance() {
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

    int getStopwordsCount(String lang, String text) {
        ArrayList<String> stopWords = stopwords.get(lang);
        String[] words = text.replaceAll("\\p{Punct}|\\d", " ").toLowerCase().split("\\s+");
        String[] swords = new String[stopWords.size()];

        swords = stopWords.toArray(swords);

        int count = 0;

        Map<String, Integer> counts = new HashMap<>();

        for(int i = 0; i < words.length; i++) {
            if (counts.containsKey(words[i])) {
                counts.put(words[i], counts.get(words[i]) + 1);
            } else {
                counts.put(words[i], 1);
            }
        }

        for(String word: counts.keySet()) {
            int i = Arrays.binarySearch(swords, word);

            if(i != -1) {
                count += counts.get(word);
            }
        }

        return count;
    }

    int getWordsCount(String text) {
        String[] words = text.replaceAll("\\p{Punct}|\\d", " ").split("\\s+");

        return words.length;
    }

}
