package A5;

import java.util.Random;
import java.util.SortedSet;

/**
 * ExampleGameClient.java
 * A sample client for the assignment handout.
 *
 * @author      Dean Hendrix (dh@auburn.edu)
 * @version     2017-03-23
 *
 */
public class ExampleGameClient {

   /** Drives execution. */
   public static void main(String[] args) {
      WordSearchGame game = WordSearchGameFactory.createGame();
      game.loadLexicon("wordfiles/words_medium.txt");
      String alphabet = "QWERTYUIOPASDFGHJKLZXCVBNM";
//      String[] board = new String[500*500];
//      Random random = new Random();
//      for (int i = 0; i < board.length; i++) {
//         board[i] = alphabet.charAt(random.nextInt(26)) + "";
//      }
      game.setBoard(new String[]{"CAT","X","FISH","XXXX",});
      System.out.println(game.getBoard());

      long start = System.currentTimeMillis();
      SortedSet<String> words = game.getAllValidWords(7);
      System.out.println(System.currentTimeMillis()-start + " ms");
      System.out.println(words);
      System.out.println(words.size() + " words");
      System.out.println(game.isOnBoard("CATFISH"));

      System.out.println(game.isValidWord("AA"));
   }
}

/*

E E C A
A L E P
H N B O
Q T T Y

RUNTIME OUTPUT:

LENT is on the board at the following positions: [5, 6, 9, 13]
POPE is not on the board: []
All words of length 6 or more:
[ALEPOT, BENTHAL, PELEAN, TOECAP]

 */
