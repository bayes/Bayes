/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.util.*;
/**
 *
 * @author apple
 */
public class IndexSort {
    
    
    public static int [] sort( double[] data){
        SortedObject [] array       =   new  SortedObject [data.length];
        int []          indecies    =   new int[ data.length];
        
        for (int i = 0; i < array.length; i++) {
            array[i]= new SortedObject(data[i], i);
        }
        
        Arrays.sort(array, new ObjectComparator());
        
        for (int i = 0; i < array.length; i++) {
           indecies [i] = array[i].ind;
            
        }
        return indecies;
    }
    
    
}
 class SortedObject {
     SortedObject(double akey, int index){
         key    =   akey;
         ind    =   index;
     
     }
     public double key;
     public int    ind;
 
 }
 class ObjectComparator implements Comparator{
   public  int	compare(Object o1, Object o2) {
        SortedObject so1 = (SortedObject)o1;
        SortedObject so2 = (SortedObject)o2;
        
       if (so1.key > so2.key)  {return +1;} 
       else if (so1.key == so2.key){return 0;}
       else {return -1;}
    }
       
    
 }