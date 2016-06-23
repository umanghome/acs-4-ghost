package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        String selected = null;

        // If prefix is empty
        if (prefix.length() == 0) {

            // Pick random word
            selected = words.get(random.nextInt(words.size()));
        }
        // Perform binary search
        else {

            // Set min and max
            int min = 0;
            int max = words.size() - 1;

            // Set midWord
            String midWord = null;

            while (min < max) {
                // Calculate mid
                int mid = (min + max) / 2;

                // Get middle word
                midWord = words.get(mid);

                Log.v("midWord", midWord);

                // Get relation between prefix and midWord
                int result = relationBetweenAAndB(prefix, midWord);

                // prefix < midWord
                if (result == 0) {
                    max = mid - 1;
                }
                // prefix > midWord
                else if (result == 2) {
                    min = mid + 1;
                }
                // prefix == midWord
                else if (result == 1) {
                    selected = midWord;
                    break;
                }

            }

        }

        return selected;

    }

    // whoWentFirst = 0 => computer went first. Find words of even length
    // whoWentFirst = 1 => user went first. Find words of odd length
    @Override
    public String getGoodWordStartingWith(String prefix, int whoWentFirst) {
        String selected = null;

        // If prefix is empty
        if (prefix.length() == 0) {

            // Pick random word
            selected = words.get(random.nextInt(words.size()));
        }
        // Perform binary search
        else {

            // Set min and max
            int min = 0;
            int max = words.size() - 1;

            // Set mid
            int mid = 0;

            // Set midWord
            String midWord = null;

            while (min < max) {
                // Calculate mid
                mid = (min + max) / 2;

                // Get middle word
                midWord = words.get(mid);

                // Get relation between prefix and midWord
                int result = relationBetweenAAndB(prefix, midWord);

                // prefix < midWord
                if (result == 0) {
                    max = mid - 1;
                }
                // prefix > midWord
                else if (result == 2) {
                    min = mid + 1;
                }
                // prefix == midWord
                else if (result == 1) {
                    break;
                }

            }

            // Create wordsToConsider
            ArrayList<String> wordsToConsider = new ArrayList<String>();

            // Copy mid
            int midCopy = mid;

            // Go upwards
            while (true) {

                // Get word
                String w = words.get(mid--);

                // If the word does not contain prefix, we're done
                if (!isPrefix(prefix, w)) {
                    break;
                }

                // If word's length is desirable, consider it
                if (w.length() % 2 == whoWentFirst) {
                    wordsToConsider.add(w);
                }

            }

            // Restore mid
            mid = midCopy;

            // Go downwards
            while (true) {

                // Get word
                String w = words.get(mid++);

                // If the word does not contain prefix, we're done
                if (!isPrefix(prefix, w)) {
                    break;
                }

                // If word's length is desirable, consider it
                if (w.length() % 2 == whoWentFirst) {
                    wordsToConsider.add(w);
                }

            }

            // If there are no words to consider
            if (wordsToConsider.size() == 0) {
                selected = getAnyWordStartingWith(prefix);
            } else {
                // Pick a random word from wordsToConsider
                selected = wordsToConsider.get(random.nextInt(wordsToConsider.size()));
            }

        }

        return selected;
    }

    // 0 => a < b
    // 1 => a == b
    // 2 => a > b
    public int relationBetweenAAndB (String a, String b) {
        // If A's length is greater than B's length, a > b
        if (a.length() >= b.length()) return 2;

        // Loop over each element of A
        for (int i = 0; i < a.length(); i++) {
            char charA = a.charAt(i);
            char charB = b.charAt(i);

            if (charA == charB) {
                continue;
            }
            // A is lesser
            else if (charA < charB) {
                return 0;
            }
            // B is lesser
            else if (charA > charB) {
                return 2;
            }
        }

        return 1;
    }

    // Check if a word contains a prefix
    public boolean isPrefix (String prefix, String word) {
        // Word and prefix are of equal length
        // or prefix is longer
        if (prefix.length() >= word.length()) return false;

        // Check
        for (int i = 0; i < prefix.length(); i++) {
            // Does not match
            if (prefix.charAt(i) != word.charAt(i)) return false;
        }

        // Matches
        return true;
    }

}
