/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.Set;
import javax.swing.UIManager;

/**
 *
 * @author apple
 */
public class Test {
    public static void main (String [] args){
    Object[] objs = javax.swing.UIManager.
                   getLookAndFeel().getDefaults().keySet().toArray();
       java.util.Arrays.sort( objs );
       for( int i = 0; i < objs.length; i++ )
       {
           System.out.println(objs[i]+
              ", "+javax.swing.UIManager.getDefaults().get(objs[i]));
       }
    }
}
