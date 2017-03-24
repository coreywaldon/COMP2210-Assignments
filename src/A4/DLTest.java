package A4;


import org.junit.Before;
import org.junit.Test;

/**
 * Created by caw0086 on 3/5/17.
 */
public class DLTest {
   @Before public void setup(){

   }

   @Test
   public void testOrder(){
      DoubleEndedList<Integer> g = new BiDirList<>();
      g.addFirst(0);
      g.addLast(1);
      g.addLast(2);
      int f = 0, s = 0, l = 0, c = 0;
      for(Integer i : g) {
         switch(c++){
            case 0:
               f = i;
               System.out.println(f);
               break;
            case 1:
               s = i;
               break;
            case 2:
               l = i;
               break;
         }
      }
      assert  f == 0;
      assert s == 1;
      assert l == 1;

   }
}
