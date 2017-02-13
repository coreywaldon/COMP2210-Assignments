import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Selector {
   private Selector() { }

   public static <T> T min(Collection<T> coll, Comparator<T> comp) {
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      if (coll.size() == 0) {
         throw new NoSuchElementException();
      }
      return coll.stream()
         .min(comp)
         .get();
   }

   public static <T> T max(Collection<T> coll, Comparator<T> comp) {
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      if (coll.size() == 0) {
         throw new NoSuchElementException();
      }
      return coll.stream()
         .max(comp)
         .get();
   }

   public static <T> T kmin(Collection<T> coll, int k, Comparator<T> comp) {
      List<T> sortedList;
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      sortedList = sortDistinct(coll, comp);
      if (coll.size() == 0 || k > sortedList.size() || k < 1) {
         throw new NoSuchElementException();
      }
      return sortedList.get(k - 1);
   }

   public static <T> T kmax(Collection<T> coll, int k, Comparator<T> comp) {
      List<T> sortedList;
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      sortedList = sortDistinct(coll, comp);
      if (coll.size() == 0 || k > sortedList.size() || k < 1) {
         throw new NoSuchElementException();
      }
      return sortedList.get(sortedList.size() - k);
   }

   public static <T> Collection<T> range(Collection<T> coll, T low, T high,
                                         Comparator<T> comp) {
      Collection<T> output = new ArrayList<>();
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      if (coll.size() == 0) {
         throw new NoSuchElementException();
      }
      coll.stream()
         .filter(t -> inRange(t, low, high, comp))
         .forEach(output::add);
      if (output.size() == 0) {
         throw new NoSuchElementException();
      }
      return output;
   }

   public static <T> T ceiling(Collection<T> coll, T key, Comparator<T> comp) {
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      Collection<T> qualifyingList = new ArrayList<>();
      qualifyingList.addAll(range(coll, key, max(coll, comp), comp));
      return min(qualifyingList,  comp);
   }
 
   public static <T> T floor(Collection<T> coll, T key, Comparator<T> comp) {
      if (coll == null || comp == null) {
         throw new IllegalArgumentException();
      }
      Collection<T> qualifyingList = new ArrayList<>();
      qualifyingList.addAll(range(coll, min(coll, comp), key, comp));
      return max(qualifyingList,  comp);
   }

   private static <T> ArrayList<T> sortDistinct(Collection<T> list, Comparator<T> comp) {
      ArrayList<T> sortedList = new ArrayList<>();
      list.stream()
         .distinct()
         .sorted(comp)
         .forEach(sortedList::add);
      return sortedList;
   }

   private static <T> boolean inRange(T val, T low, T high, Comparator<T> comp) {
      boolean aboveLower = comp.compare(val, low) >= 1 || comp.compare(val, low) == 0;
      boolean belowUpper = comp.compare(high, val) >= 1 || comp.compare(high, val) == 0;
      return (aboveLower && belowUpper);
   }
}