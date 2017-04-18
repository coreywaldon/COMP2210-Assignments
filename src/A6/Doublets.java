package A6;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.*;

/**
 * Doublets.java
 * Provides an implementation of the WordLadderGame interface. The lexicon
 * is stored as a TreeSet of Strings.
 *
 * @author Corey Waldon (caw0086@auburn.edu)
 * @author Dean Hendrix (dh@auburn.edu)
 * @version 2017-04-28
 */

public class Doublets implements WordLadderGame {

    TreeSet<String> lexicon;

    public Doublets(InputStream in) {
        try {
            lexicon = new TreeSet<String>();
            Scanner s = new Scanner(new BufferedReader(new InputStreamReader(in)));
            while (s.hasNext()) {
                String str = s.next();
                lexicon.add(str.toLowerCase());
                s.nextLine();
            }
            in.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Error reading from InputStream.");
            System.exit(1);
        }
    }

    @Override
    public int getHammingDistance(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return -1;
        }
        int total = 0;
        String[] word = str1.split("");
        String[] other = str2.split("");
        for (int i = 0; i < str1.length(); i++) {
            if (!word[i].equals(other[i])) {
                total++;
            }
        }
        return total;
    }

    @Override
    public List<String> getMinLadder(String start, String end) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        HashMap<String, String> pred = new HashMap<>();

        visited.add(start);
        queue.offer(start);
        pred.put(start, "end0");
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                String prev = end;
                return getPrev(pred, prev);
            }
            for (String neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    pred.put(neighbor, current);
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    private List<String> getPrev(HashMap<String, String> pred, String end) {
        Stack<String> words = new Stack<>();
        String last = end;
        words.push(last);
        while (!pred.get(last).equals("end0")) {
            last = pred.get(last);
            words.push(last);
        }
        ArrayList<String> list = new ArrayList<>();
        while (!words.isEmpty()) {
            list.add(words.pop());
        }
        return list;
    }

    @Override
    public List<String> getNeighbors(String word) {
        Set<String> neighbors = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            char[] chars = word.toCharArray();
            for (char c = 'a'; c <= 'z'; c++) {
                chars[i] = c;
                String testWord = new String(chars);
                if (isWord(testWord) && !testWord.equals(word)) neighbors.add(testWord);
            }
        }
        return new ArrayList<>(neighbors);
    }

    @Override
    public int getWordCount() {
        return lexicon.size();
    }

    @Override
    public boolean isWord(String str) {
        return lexicon.contains(str);
    }

    @Override
    public boolean isWordLadder(List<String> sequence) {
        String prev = "";
        boolean firstWord = true;
        for (String word : sequence) {
            if (!isWord(word)) {
               return false;
            }
            if (firstWord) {
                firstWord = false;
                prev = word;
            } else {
                if (getHammingDistance(word, prev) == 1) {
                    prev = word;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

}