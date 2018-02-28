package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

class Stopwords {

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
            if(langFile == null) {
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(langFile));
            ArrayList<String> words = new ArrayList<>();
            String word;

            while((word = reader.readLine()) != null) {
                words.add(word);
            }

            stopwords.put(lang, words);
        }
    }

    synchronized int getStopwordsCount(String lang, String text) {
        ArrayList<String> stopWords = stopwords.get(lang);
        String[] words = getWords(text);
        String[] swords = new String[stopWords.size()];

        swords = stopWords.toArray(swords);

        int count = 0;

        Map<String, Integer> counts = new HashMap<>();

        for (String word1 : words) {
            if (counts.containsKey(word1)) {
                counts.put(word1, counts.get(word1) + 1);
            } else {
                counts.put(word1, 1);
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

    public static synchronized int getWordsCount(String text) {
        return getWords(text).length;
    }

    private static StringBuilder stringBuilder = new StringBuilder();

    private synchronized static String[] getWords(String text) {
        char c;
        int size = text.length();
        stringBuilder.setLength(0);

        for(int i = 0; i < size; i++) {
            while(i < size && !isPunct(c = text.charAt(i))) {
                stringBuilder.append(c);
                i++;
            }

            while(i < size && isPunct(text.charAt(i))) {
                i++;
            }

            stringBuilder.append(' ');
        }

        return stringBuilder.toString().split(" ");
    }

    private static boolean isPunct(char c) {
        int ascii = (int) c;

        return (ascii >= 33 && ascii <= 47) ||
                (ascii >= 58 && ascii <= 64) ||
                (ascii >= 91 && ascii <= 96) ||
                (ascii >= 123 && ascii <= 126);

    }

}