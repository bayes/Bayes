/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import java.awt.Font;
import java.util.prefs.Preferences;
import utilities.PrefObj;

/**
 *
 * @author apple
 */
public class WidgetPreferences {
    public static Font DEFAULT_WIDGET_FONT                          = new java.awt.Font("Lucida Grande", 1, 18);
    public static Font  WIDGET_FONT                                 = DEFAULT_WIDGET_FONT ;
    public static String  WIDGET_FONT_KEY                           = "WIDGET_FONT_KEY"; 
    private static Preferences prefs                                =   Preferences.userNodeForPackage(WidgetPreferences.class);
    public static void persist(){
       try{
           prefs.flush();
       }
       catch (Exception e){
        e.printStackTrace();
       }
    }
    public static String getAbsolutePath(){
        return  prefs.absolutePath();
    }
    public static  void resetPreferences(){
        try {
            if (prefs != null) {
                prefs.removeNode();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
        }
    }
   


    static{
        try{
            Object fontObj = PrefObj.getObject(prefs,  WIDGET_FONT_KEY );
            if(fontObj!=null && fontObj instanceof Font){
                WIDGET_FONT = (Font)fontObj;
            }
        } 
        catch(Exception e){
        }
      
    }


    public static Font   getFont(){
        return WIDGET_FONT;
    }
    public static Font   getDefaultFont(){
        return DEFAULT_WIDGET_FONT;
    }
  



   


}
