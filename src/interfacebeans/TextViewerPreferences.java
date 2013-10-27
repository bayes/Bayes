/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;

/**
 *
 * @author apple
 */
public class TextViewerPreferences {
    private static Preferences prefs                                    =   Preferences.userNodeForPackage(TextViewerPreferences.class);
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

    public static Color       FOREGROUND_COLOR                      =   Color.WHITE;
    public static Color       BACKGROUND_COLOR                      =   new Color (0, 51, 51);

    private static final String FOREGROUND_COLOR_KEY                    =   "TEXT_VIEWER_FOREGROUND_COLOR_KEY";
    private static final int FOREGROUND_COLOR_DEF                       =   FOREGROUND_COLOR.getRGB();

    private static final String BACKGROUND_COLOR_KEY                =   "TEXT_VIEWER_BACKGROUND_COLOR_KEY";
    private static final int    BACKGROUND_COLOR_DEF                =   BACKGROUND_COLOR.getRGB();

    private static final String  TEXT_FONT_SIZE_KEY                 =   "TEXT_VIEWER_TEXT_FONT_SIZE_KEY";
    private static final int   TEXT_FONT_SIZE_DEF                   =   22;

    private static final String  TEXT_FONT_NAME_KEY                 =   "TEXT_VIEWER_TEXT_FONT_NAME_KEY";
    private static final String  TEXT_FONT_NAME_DEF                 =   "Monospaced";

    private static final String  TEXT_FONT_BOLD_KEY                 =   "TEXT_VIEWER_TEXT_FONT_BOLD_KEY";
    private static final boolean TEXT_FONT_BOLD_DEF                 =  true;

    static{
        refreshColors();

    }


    public static Font   getTextFont(){
        String name                 = getTextFontName();
        int size                    = getTextFontSize();
        int type                    = 0;
        if ( isTextFontBold()) {type = 1;}
        Font  font    =   new java.awt.Font(name, type , size  );
        return font;
    }
    public static Font   getDefaultFont(){
        String name                 = TEXT_FONT_NAME_DEF;
        int size                    = TEXT_FONT_SIZE_DEF;
        int type                    = 0;
        if ( TEXT_FONT_BOLD_DEF) {type = 1;}
        Font  font    =   new java.awt.Font(name, type , size  );
        return font;
    }
    public static Font  resetToDefaultFont(){
        setTextFontName(TEXT_FONT_NAME_DEF);
        setTextFontSize(TEXT_FONT_SIZE_DEF);
        setTextBold(TEXT_FONT_BOLD_DEF);

        return getTextFont();
    }
    public static void refreshColors(){
            try{
                FOREGROUND_COLOR                   =   new Color(getForegroundColor());
                BACKGROUND_COLOR                   =   new Color(getBackgroundColor());
            }
            catch (Exception e){e.printStackTrace();}


    }


    public static int getTextFontSize(){
      return  prefs.getInt(  TEXT_FONT_SIZE_KEY, TEXT_FONT_SIZE_DEF  );
    }
    public static void setTextFontSize(int val){
      prefs.putInt( TEXT_FONT_SIZE_KEY, val);
    }

    public static String getTextFontName(){
      return  prefs.get(  TEXT_FONT_NAME_KEY, TEXT_FONT_NAME_DEF  );
    }
    public static void setTextFontName(String val){
      prefs.put( TEXT_FONT_NAME_KEY, val);
    }

    public static boolean isTextFontBold(){
      return  prefs.getBoolean(TEXT_FONT_BOLD_KEY, TEXT_FONT_BOLD_DEF   );
    }
    public static void setTextBold(boolean val){
      prefs.putBoolean(TEXT_FONT_BOLD_KEY, val);
    }


    public static int getForegroundColor(){
      return  prefs.getInt(FOREGROUND_COLOR_KEY, FOREGROUND_COLOR_DEF   );
    }
    public static void setForegroundColor(int val){
      prefs.putInt(FOREGROUND_COLOR_KEY , val);
    }


    public static int getBackgroundColor(){
      return  prefs.getInt(BACKGROUND_COLOR_KEY, BACKGROUND_COLOR_DEF   );
    }
    public static void setBackgroundColor(int val){
      prefs.putInt(BACKGROUND_COLOR_KEY , val);
    }

}
