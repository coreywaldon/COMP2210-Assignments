package A1;

import java.util.Arrays;

/**
 * Defines a library of selection methods
 * on arrays of ints.
 *
 * @author   Corey Waldon (caw0086@auburn.edu)
 * @author   Dean Hendrix (dh@auburn.edu)
 * @version  2017-01-19
 *
 */
public final class Selector {

   /**
    * Can't instantiate this class.
    *
    * D O   N O T   C H A N G E   T H I S   C O N S T R U C T O R
    *
    */
   private Selector() { }


   /**
    * Selects the minimum value from the array a. This method
    * throws IllegalArgumentException if a is null or has zero
    * length. The array a is not changed by this method.
    */
   public static int min(int[] a) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int minimum = Integer.MAX_VALUE;
      for (int b : a) {
         minimum = Math.min(b, minimum);
      }
      return minimum;
   }


   /**
    * Selects the maximum value from the array a. This method
    * throws IllegalArgumentException if a is null or has zero
    * length. The array a is not changed by this method.
    */
   public static int max(int[] a) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int maximum = Integer.MIN_VALUE;
      for (int b : a) {
         maximum = Math.max(b, maximum);
      }
      return maximum;
   }


   /**
    * Selects the kth minimum value from the array a. This method
    * throws IllegalArgumentException if a is null, has zero length,
    * or if there is no kth minimum value. Note that there is no kth
    * minimum value if k < 1, k > a.length, or if k is larger than
    * the number of distinct values in the array. The array a is not
    * changed by this method.
    */
   public static int kmin(int[] a, int k) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int[] uniqueArr = getUnique(a);
      if (k < 1 || k > uniqueArr.length) {
         throw new IllegalArgumentException();
      }
      Arrays.sort(uniqueArr);
      return uniqueArr[k - 1];
   }


   /**
    * Selects the kth maximum value from the array a. This method
    * throws IllegalArgumentException if a is null, has zero length,
    * or if there is no kth maximum value. Note that there is no kth
    * maximum value if k < 1, k > a.length, or if k is larger than
    * the number of distinct values in the array. The array a is not
    * changed by this method.
    */
   public static int kmax(int[] a, int k) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int[] uniqueArr = getUnique(a);
      if (k < 1 || k > uniqueArr.length) {
         throw new IllegalArgumentException();
      }
      Arrays.sort(uniqueArr);
      return uniqueArr[uniqueArr.length - k];
   }


   /**
    * Returns an array containing all the values in a in the
    * range [low..high]; that is, all the values that are greater
    * than or equal to low and less than or equal to high,
    * including duplicate values. The length of the returned array
    * is the same as the number of values in the range [low..high].
    * If there are no qualifying values, this method returns a
    * zero-length array. Note that low and high do not have
    * to be actual values in a. This method throws an
    * IllegalArgumentException if a is null or has zero length.
    * The array a is not changed by this method.
    */
   public static int[] range(int[] a, int low, int high) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      if (low > max(a) || high < min(a)) {
         return new int[0];
      }
      String results = "";
      int n = 0;
      for (int b : a) {
         if (low <= b && b <= high) {
            results += b + " ";
            n++;
         }
      }
      int[] resultsArr = new int[n];
      int c = 0;
      for (String s : results.split(" ")) {
         resultsArr[c++] = Integer.parseInt(s);
      }
      return resultsArr;
   }


   /**
    * Returns the smallest value in a that is greater than or equal to
    * the given key. This method throws an IllegalArgumentException if
    * a is null or has zero length, or if there is no qualifying
    * value. Note that key does not have to be an actual value in a.
    * The array a is not changed by this method.
    */
   public static int ceiling(int[] a, int key) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int[] qualifyArr = range(a, key, max(a));
      if (qualifyArr.length == 0) {
         throw new IllegalArgumentException();
      }
      return min(qualifyArr);
   }


   /**
    * Returns the largest value in a that is less than or equal to
    * the given key. This method throws an IllegalArgumentException if
    * a is null or has zero length, or if there is no qualifying
    * value. Note that key does not have to be an actual value in a.
    * The array a is not changed by this method.
    */
   public static int floor(int[] a, int key) {
      if (a == null || a.length == 0) {
         throw new IllegalArgumentException();
      }
      int[] qualifyArr = range(a, min(a), key);
      if (qualifyArr.length == 0) {
         throw new IllegalArgumentException();
      }
      return max(qualifyArr);
   }

   private static int[] getUnique(int[] a) {
      int[] set = new int[2000000];
      String results = "";
      for (int b : a) {
         if (b < 0 && set[set.length + b] == 0) {
            results += b + " ";
            set[set.length + b] = 1;
         } else if (b > 0 && set[set.length - b] == 0) {
            results += b + " ";
            set[set.length - b] = 1;
         }
      }
      String[] resultsArr = results.split(" ");
      int[] uniqueArr = new int[resultsArr.length];
      int c = 0;
      for (String s : resultsArr) {
         uniqueArr[c++] = Integer.parseInt(s);
      }
      return uniqueArr;
   }
}
