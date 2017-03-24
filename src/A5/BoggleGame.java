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

public class BoggleGame implements WordSearchGame{

   private String[][] board;
   private Trie dictionary;
   private boolean loaded = false;

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

   @Override
   public boolean isValidWord(String wordToCheck) {
      return dictionary.contains(wordToCheck);
   }

   @Override
   public boolean isValidPrefix(String prefixToCheck) {
      return dictionary.containsPrefix(prefixToCheck);
   }

   @Override
   public List<Integer> isOnBoard(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      if (!loaded) {
         throw new IllegalStateException();
      }
      Spider spider = new Spider(dictionary.getParentNode(wordToCheck), board, wordToCheck);
      spider.crawl(spider.getSource(), spider.getWeb(), "", new Stack<>());
      Stack<Tuple> locations = spider.getResult();
      List<Integer> result = new ArrayList<>();
      for (Tuple location : locations) {
         result.add(location.getX() * board.length + location.getY());
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
      private Stack<Tuple> result;;
      
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
         this.goal = goal;
         result = new Stack<>();
         this.web = new Stack<>();
         this.web.add(parent);
         Tuple firstLocation = new Tuple(-1, -1);
         for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
               if (board[i][j].equals(parent.getValue())) {
                  firstLocation = new Tuple(i, j);
                  break;
               }
            }
         }
         this.source = firstLocation;
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
         if (result != null && !result.isEmpty()) {
            return stack;
         }
         if (goal != null && word.equals(goal)) {
            if (result == null) {
               result = new Stack<>();
            }
            result.addAll(stack);
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

      boolean isValidMove(int x, int y, int maxX, int maxY) {
         boolean belowMax = x < maxX && y < maxY;
         boolean aboveMin = x >= 0 && y >= 0;
         return belowMax && aboveMin;
      }

      Node<String> getParent(){
         return parent;
      }

      private Stack<Node<String>> getWeb(){
         if (web != null) {
            return web;
         }
         throw new NoSuchElementException();
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

      Stack<Tuple> getResult() {
         return result;
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
         return contains(parent, new ArrayList<>(Arrays.asList(word.split(""))));
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
         return add(parent, new ArrayList<>(Arrays.asList(word.split(""))));
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
