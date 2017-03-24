package A4;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandyList<T> implements RandomizedList<T> {

   private T[] list;
   private int size;

   public RandyList(){
      list = (T[]) new Object[1];
      size = 0;
   }

   @Override
   public int size() {
      return size;
   }

   @Override
   public boolean isEmpty() {
      return size() != 0;
   }

   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private int index = 0;
         private boolean initialized = false;
         private int[] bag = new int[10000];

         @Override
         public boolean hasNext() {
            return index < list.length;
         }

         @Override
         public T next() {
            if (!hasNext()) {
               throw new NoSuchElementException();
            }
            if (!initialized) {
               Random random = new Random();
               Iterator<Integer> iterator = random.ints(0, list.length).distinct().iterator();
               for (int i = 0; i < list.length; i++) {
                  bag[i] = iterator.next();
               }
            }
            initialized = true;
            return list[bag[index++]];
         }

         @Override
         public void remove(){
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public void add(T element) {
      if (element == null) {
         throw new NoSuchElementException();
      }

      if (size() < list.length) {
         list[size++] = element;
      } else {
         while(size() >= list.length){
            stretch();
         }
         list[size++] = element;
      }
   }

   private void stretch(){
      T[] stretchedList = (T[]) new Object[list.length + 1];
      System.arraycopy(list, 0, stretchedList, 0, list.length);
   }

   @Override
   public T remove() {
      if(!isEmpty()) {
         int indexToRemove = new Random().nextInt(size());
         T deletedElement = list[indexToRemove];
         list[indexToRemove] = list[size--];
         return deletedElement;
      }
      return null;
   }

   @Override
   public T sample() {
      if(!isEmpty()) {
         return list[new Random().nextInt(size)];
      }
      return null;
   }
}
