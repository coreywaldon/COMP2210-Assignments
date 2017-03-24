package A5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Created by caw0086 on 3/23/17.
 */
public class BoggleGame implements WordSearchGame{

   private String[][] board;
   private Trie dictionary;
   private boolean loaded = false;

   /**
    * Loads the lexicon into a data structure for later use.
    *
    * @param fileName A string containing the name of the file to be opened.
    * @throws IllegalArgumentException if fileName is null
    * @throws IllegalArgumentException if fileName cannot be opened.
    */
   @Override
   public void loadLexicon(String fileName) {
      if (fileName == null) {
         throw new IllegalArgumentException();
      }
      try {
         URL path = BoggleGame.class.getResource(fileName);
         File f = new File(path.getFile());
         BufferedReader reader = new BufferedReader(new FileReader(f));
         String input;
         while((input = reader.readLine()) != null){
            if (dictionary == null) {
               dictionary = new Trie();
            }
            dictionary.add(input.split(" ")[0]);
         }
      } catch (IOException e) {
         throw new IllegalArgumentException();
      }
      loaded = true;
   }

   /**
    * Stores the incoming array of Strings in a data structure that will make
    * it convenient to find words.
    *
    * @param letterArray This array of length N^2 stores the contents of the
    *     game board in row-major order. Thus, index 0 stores the contents of board
    *     position (0,0) and index length-1 stores the contents of board position
    *     (N-1,N-1). Note that the board must be square and that the strings inside
    *     may be longer than one character.
    * @throws IllegalArgumentException if letterArray is null, or is  not
    *     square.
    */
   @Override
   public void setBoard(String[] letterArray) {
      if(letterArray == null){
         throw new IllegalArgumentException();
      }
      double n = Math.sqrt(letterArray.length);
      if (n != Math.floor(n)){
         throw new IllegalArgumentException();
      }
      int N = (int) Math.floor(n);
      board = new String[N][N];
      int c = 0;
      for (int i = 0; i < N; i++) {
         for (int j = 0; j < N; j++) {
            board[i][j] = letterArray[c++];
         }
      }
   }

   /**
    * Creates a String representation of the board, suitable for printing to
    *   standard out. Note that this method can always be called since
    *   implementing classes should have a default board.
    */
   @Override
   public String getBoard() {
      String output = "";
      int imax = board.length + 2;
      int jmax = board.length * 2 + 3;
      for (int i = 0, k = -1; i < imax; i++) {
         for (int j = 0, l = -1; j < jmax; j++) {
            if (i == 0 || i == imax - 1) {
               if (j == 0 || j == jmax - 1) {
                  if (i > 0 && i != imax - 1) {
                     output += "|";
                  } else {
                     output += " ";
                  }
               }
               if (j > 0 && j < jmax - 1) {
                  output += "-";
               }
            } else if (i > 0 && (j == 0 || j == jmax - 1)) {
               output += "|";
            } else if ((j + 1) % 2 == 0) {
               output += " ";
               l += (l + 1 != board.length) ? 1 : 2 - board.length;
               if (l == 0) {
                  k++;
               }
            } else {
               output += board[k][l];
            }
         }
         output += "\n";
      }
      return output;
   }

   /**
    * Retrieves all valid words on the game board, according to the stated game
    * rules.
    *
    * @param minimumWordLength The minimum allowed length (i.e., number of
    *     characters) for any word found on the board.
    * @return java.util.SortedSet which contains all the words of minimum length
    *     found on the game board and in the lexicon.
    * @throws IllegalArgumentException if minimumWordLength < 1
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   @Override
   public SortedSet<String> getAllValidWords(int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
      if (!loaded) {
         throw new IllegalStateException();
      }
      SortedSet<String> allWords = new TreeSet<>();
      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            Spider spider = new Spider(dictionary.getParentNode(board[i][j]), board, new Tuple(i, j), minimumWordLength);
            Stack<Node<String>> nodeStack = new Stack<>();
            nodeStack.push(spider.getParent());
            spider.crawl(spider.getSource(), nodeStack, "", new Stack<>());
            allWords.addAll(spider.getWords());
         }
      }
      return allWords;
   }

   /**
    * Computes the cummulative score for the scorable words in the given set.
    * To be scorable, a word must (1) have at least the minimum number of characters,
    * (2) be in the lexicon, and (3) be on the board. Each scorable word is
    * awarded one point for the minimum number of characters, and one point for
    * each character beyond the minimum number.
    *
    * @param words The set of words that are to be scored.
    * @param minimumWordLength The minimum number of characters required per word
    * @return the cummulative score of all scorable words in the set
    * @throws IllegalArgumentException if minimumWordLength < 1
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   @Override
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
      if (!loaded) {
         throw new IllegalStateException();
      }
      int total = 0;
      for (String word : words) {
         if (word.length() > minimumWordLength) {
            total += word.length() - minimumWordLength;
         }
      }
      return total;
   }

   /**
    * Determines if the given word is in the lexicon.
    *
    * @param wordToCheck The word to validate
    * @return true if wordToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   @Override
   public boolean isValidWord(String wordToCheck) {
      return dictionary.contains(wordToCheck);
   }

   /**
    * Determines if there is at least one word in the lexicon with the
    * given prefix.
    *
    * @param prefixToCheck The prefix to validate
    * @return true if prefixToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if prefixToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   @Override
   public boolean isValidPrefix(String prefixToCheck) {
      return dictionary.containsPrefix(prefixToCheck);
   }

   /**
    * Determines if the given word is in on the game board. If so, it returns
    * the path that makes up the word.
    * @param wordToCheck The word to validate
    * @return java.util.List containing java.lang.Integer objects with  the path
    *     that makes up the word on the game board. If word is not on the game
    *     board, return an empty list. Positions on the board are numbered from zero
    *     top to bottom, left to right (i.e., in row-major order). Thus, on an NxN
    *     board, the upper left position is numbered 0 and the lower right position
    *     is numbered N^2 - 1.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   @Override
   public List<Integer> isOnBoard(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      if (!loaded) {
         throw new IllegalStateException();
      }
      Spider spider = new Spider(dictionary.getParentNode(wordToCheck), board, wordToCheck);
      Vector<Stack<Tuple>> locations = spider.crawlAll(spider.getSource(), spider.getWeb(), "", new Stack<>());
      List<Integer> result = new ArrayList<>();
      for (Stack<Tuple> stack : locations) {
         for (Tuple location : stack) {
            result.add(location.getX() * board.length + location.getY());
         }
      }
      return result;
   }


   private class Spider {
      private String goal;
      private String[][] board;
      private final Node<String> parent;
      private final Tuple source;
      private final int minimumLength;
      private SortedSet<String> words;
      private Vector<Spider> brood;
      private Stack<Node<String>> web;
      private Vector<Stack<Tuple>> results;
      
      private Spider(Node<String> parent, String[][] board, Vector<Tuple> locations, int index) {
         this.parent = parent;
         words = new TreeSet<>();
         this.board = board;
         this.minimumLength = 0;
         this.source = locations.get(index);        
      }
      
      Spider(Node<String> parent, String[][] board, String goal) {
         this.parent = parent;
         words = new TreeSet<>();
         this.board = board;
         this.minimumLength = 0;
         Vector<Tuple> locations = getLocations(parent);
         if (locations.size() > 1) {
            source = locations.get(0);
            brood = new Vector<>();
            for (int i = 0; i < locations.size(); i++) {
               brood.add(new Spider(parent, board, locations, i));
            }
         } else if (locations.size() == 1) {
            source = locations.get(0);
         } else {
            source = new Tuple(-1, -1);
         }
         web = new Stack<>();
         web.add(parent);
         this.goal = goal;
         results = new Vector<>();
      }

      Spider(Node<String> parent, String[][] board, Tuple source, int minimumLength) {
         this.parent = parent;
         words = new TreeSet<>();
         this.board = board;
         this.source = source;
         this.minimumLength = minimumLength;
      }

      Stack<Tuple> crawl(Tuple position, Stack<Node<String>> nodeStack, String word, Stack<Tuple> stack) {
         int[][] pathMatrix = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
         int x = position.getX();
         int y = position.getY();
         if (x < 0 || y < 0) {
            throw new IllegalArgumentException();
         }
         word += board[x][y];
         stack.push(new Tuple(x, y));
         if (goal != null && word.equals(goal)) {
            Stack<Tuple> result = new Stack<>();
            result.addAll(stack);
            results.add(result);
         }
         addWord(nodeStack.peek(), word);
         for (int[] path : pathMatrix) {
            while(stack.size() > word.length()){
               stack.pop();
            }
            while(nodeStack.size() > word.length()){
               nodeStack.pop();
            }
            int dx = path[0];
            int dy = path[1];
            if (isValidMove(dx + x, dy + y, board.length, board.length) &&
                  nodeStack.peek().getChild(board[dx + x][dy + y]) != null &&
                  !stack.contains(new Tuple(dx + x, dy + y))) {
               nodeStack.push(nodeStack.peek().getChild(board[dx + x][dy + y]));
               crawl(new Tuple(x + dx, y + dy), nodeStack, word, stack);
            }
         }
         return stack;
      }

      Vector<Stack<Tuple>> crawlAll(Tuple position, Stack<Node<String>> nodeStack, String word, Stack<Tuple> stack){
         Vector<Stack<Tuple>> foundPaths = new Vector<>();
         crawl(position, nodeStack, word, stack);
         if (results != null && results.size() != 0) {
            foundPaths.addAll(results);
         }
         if (brood != null) {
            for (Spider spider : brood) {
               spider.crawl(position, nodeStack, word, stack);
               if (spider.getResults() != null && spider.getResults().size() != 0){
                  foundPaths.addAll(spider.getResults());
               }
            }
         }
         return foundPaths;
      }

      boolean isValidMove(int x, int y, int maxX, int maxY) {
         boolean belowMax = x < maxX && y < maxY;
         boolean aboveMin = x >= 0 && y >= 0;
         return belowMax && aboveMin;
      }

      Node<String> getParent(){
         return parent;
      }

      Tuple getSource(){
         return source;
      }

      private void addWord(Node<String> node, String word){
         if (node.isEdge() && word.length() >= minimumLength) {
            words.add(word);
         }
      }

      private SortedSet<String> getWords(){
         return words;
      }
      
      private Vector<Tuple> getLocations(Node<String> parent) {
         Vector<Tuple> locations = new Vector<>();
         if (parent == null) {
            return locations;
         }
         for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
               if (board[i][j].equals(parent.getValue())){
                  locations.add(new Tuple(i, j));
               }
            }
         }
         return locations;
      }

      private Stack<Node<String>> getWeb(){
         if (web != null) {
            return web;
         }
         throw new NoSuchElementException();
      }

      Vector<Stack<Tuple>> getResults() {
         return results;
      }
   }

   private class Tuple {
      private int x;
      private int y;

      Tuple(int x, int y) {
         this.x = x;
         this.y = y;
      }

      int getX(){
         return x;
      }

      int getY(){
         return y;
      }

      @Override
      public boolean equals(Object other) {
         if (other == null || !(other instanceof Tuple)) {
            return false;
         }
         return (this.x == ((Tuple) other).getX() && this.y == ((Tuple) other).getY());
      }
   }

   private class Trie {
      private Vector<Node<String>> network;
      private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

      public Trie(){
         network = new Vector<>();
         for (String s : alphabet.split("")) {
            network.add(new Node<>(s));
         }
      }
      
      boolean containsPrefix(String prefix) {
         prefix = prefix.toUpperCase();
         Node<String> parent = getParentNode(prefix);
         return containsPrefix(parent, new ArrayList<String>(Arrays.asList(prefix.split(""))));
      }

      private boolean containsPrefix(Node<String> parent, ArrayList<String> remainingLetters) {
         if (parent == null) {
            return false;
         }
         remainingLetters.remove(0);
         if (remainingLetters.size() == 0) {
            return true;
         }
         return containsPrefix(parent.getChild(remainingLetters.get(0)), remainingLetters);
      }

      boolean contains(String word) {
         word = word.toUpperCase();
         Node<String> parent = getParentNode(word);
         return contains(parent, new ArrayList<String>(Arrays.asList(word.split(""))));
      }
      
      private boolean contains(Node<String> parent, ArrayList<String> remainingLetters) {
         if (parent == null) {
            return false;
         }
         remainingLetters.remove(0);
         if (remainingLetters.size() == 0) {
            return parent.isEdge();
         }
         return contains(parent.getChild(remainingLetters.get(0)), remainingLetters);
      }

      boolean add(String word) {
         word = word.toUpperCase();
         Node<String> parent = getParentNode(word);
         return add(parent, new ArrayList<String>(Arrays.asList(word.split(""))));
      }

      private boolean add(Node<String> parent, ArrayList<String> remainingLetters) {
         remainingLetters.remove(0);
         if (remainingLetters.size() == 0) {
            parent.setAsEdge();
            return true;
         }
         return add(parent.addChild(remainingLetters.get(0)), remainingLetters);
      }

      Node<String> getParentNode(String word) {
         if (word == null) {
            throw new IllegalArgumentException();
         }
         return network.get(alphabet.indexOf(word.charAt(0)+""));
      }
   }

   private class Node<T> {
      private T value;
      private Node<T> parent;
      private HashMap<T, Node<T>> children;
      private boolean isEdge;

      Node(T value) {
         this.value = value;
         this.children = new HashMap<>();
         isEdge = false;
      }

      Node(T value, Node<T> parent) {
         this.value = value;
         this.parent = parent;
         this.children = new HashMap<>();
         isEdge = false;
      }

      Node<T> addChild(T value){
         Node<T> child = new Node<>(value, this);
         children.putIfAbsent(value, child);
         return children.get(value);
      }

      HashMap<T, Node<T>> getChildren(){
         return children;
      }

      Node<T> getChild(T value) {
         return children.get(value);
      }

      Node<T> setAsEdge(){
         isEdge = true;
         return this;
      }

      boolean isEdge(){
         return isEdge;
      }

      @SuppressWarnings("unchecked")
      @Override
      public boolean equals(Object object) {
         if (object == null) {
            return false;
         }
         if (!(object instanceof Node)){
            return false;
         }
         Node<T> other = (Node<T>) object;
         return value == other.value && parent == other.parent;
      }

      public T getValue() {
         return value;
      }
   }
}
