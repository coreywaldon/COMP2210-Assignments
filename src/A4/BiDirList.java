package A4;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BiDirList<T> implements DoubleEndedList<T> {

   private Node<T> first;
   private Node<T> last;

   private int size;

   public BiDirList() {
      first = null;
      last = null;
      size = 0;
   }

   @Override
   public void addFirst(T element) {
      if (element == null) {
         throw new NoSuchElementException();
      }

      Node<T> newNode = new Node<T>(element);

      if (size() != 0) {
         first.setLeft(newNode);
         newNode.setRight(first);
         first = newNode;
         size++;
      } else {
         first = newNode;
         last = newNode;
         size++;
      }
   }

   @Override
   public void addLast(T element) {
      if (element == null) {
         throw new NoSuchElementException();
      }

      Node<T> newNode = new Node<T>(element);

      if (!isEmpty()) {
         last.setRight(newNode);
         newNode.setLeft(last);
         last = newNode;
         size++;
      } else {
         first = newNode;
         last = newNode;
         size++;
      }
   }

   @Override
   public T removeFirst() {
      if (isEmpty()) {
         return null;
      }

      T value = first.getValue();

      if (first.hasRight()) {
         first = first.getRight();
         first.setLeft(null);
      } else {
         first = null;
         last = null;
      }

      size--;

      return value;
   }

   @Override
   public T removeLast() {
      if (isEmpty()) {
         return null;
      }

      T value = last.getValue();

      if (last.hasLeft()) {
         last = last.getLeft();
         last.setRight(null);
      } else {
         first = null;
         last = null;
      }

      size--;

      return value;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return size == 0;
   }

   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         Node<T> index = first;

         @Override
         public boolean hasNext() {
            return index != null && index.hasRight();
         }

         @Override
         public T next() {
            if (hasNext()) {
               Node<T> returnVal = index;
               index = index.getRight();
               return returnVal.getValue();
            }
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   class Node<T> {
      private T value;
      private Node<T> left = null;
      private Node<T> right = null;


      public Node(T value) {
         this.value = value;
      }

      public T getValue() {
         return value;
      }

      public void setValue(T value) {
         this.value = value;
      }

      public Node<T> getLeft(){
         return left;
      }

      public Node<T> getRight(){
         return right;
      }

      public void setRight(Node<T> right) {
         this.right = right;
      }

      public void setLeft(Node<T> left) {
         this.left = left;
      }

      public boolean hasRight() {
         return this.right != null;
      }

      public boolean hasLeft() {
         return this.left != null;
      }
   }

}
