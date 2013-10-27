/*
 * Array.java
 *
 * Created on September 26, 2007, 12:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utilities;
import java.util.Arrays;
/**
 *
 * @author apple
 */
public class Array {
    
    /** Creates a new instance of Array */
    public Array () {
    }
    
    public static float findClosestValue(float [] array, float val){
       float [] sort = new float [array.length];
       System.arraycopy(array, 0, sort, 0, array.length);  
       Arrays.sort(sort);


       int pos = java.util.Arrays.binarySearch (sort, val);
       if (pos <= 0) pos = Math.abs (pos);
       
       // test neighbour point
       float res = sort[pos];
       
       if (( res - val) > (sort[pos+1] - val) ){
            res = sort[pos+1];
       }
     
        if (( res - val) > (sort[pos-1] - val) ){
            res = sort[pos-1];
       }
       
       return res;
    }
    
     public static int findClosestIndex(float [] array, float val){
       float  diff = Float.MAX_VALUE ;
       float  sigma;
       int pos  = -1;
       
       for (int i = 0; i < array.length; i++) {
          sigma = array [i] -val;
          if (Math.abs (diff) > Math.abs (sigma) ){
            diff = sigma;
            pos = i ;
          }
       
       }
       return pos;
    }
     
     public static void main (String[] args) {
         float [] ff = new float []{1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
         System.out.println(findClosestIndex(ff, 2.51f));
     }
}
