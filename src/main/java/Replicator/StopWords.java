package Replicator;

import java.util.*;

public class StopWords {

    public Map<String, String[]> stopWords;

    private static StopWords ourInstance = new StopWords();

    public static StopWords getInstance() {
        return ourInstance;
    }

    private StopWords() {
        stopWords = StopWordsList.getMap();
    }

    public synchronized int getStopWordsCount(String lang, String text) {
        String[] stopWords = this.stopWords.get(lang);
        String[] words = getWords(text);

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
            int i = Arrays.binarySearch(stopWords, word);

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