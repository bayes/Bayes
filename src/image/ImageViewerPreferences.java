/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;

import bayes.ApplicationPreferences;
import java.awt.Font;
import java.util.prefs.Preferences;

/**
 *
 * @author apple
 */
public class ImageViewerPreferences {
    private static Preferences prefs                                    =   Preferences.userNodeForPackage(ApplicationPreferences.class);
    public static void persist(){
       try{
           prefs.flush();
       }
       catch (Exception e){
        e.printStackTrace();
       }
    }

    private static final String CALIBRATION_BAR_LABEL_PRECISION_KEY     =   "CALIBRATION_BAR_LABEL_PRECISION_KEY";
    private static final String IMAGE_PIXEL_VALUE_PRECISION_KEY         =   "IMAGE_PIXEL_VALUE_PRECISION_KEY";

    private static final String  IMAGE_LIST_FONT_SIZE_KEY               =   "IMAGE_LIST_FONT_SIZE_KEY";
    private static final int   IMAGE_LIST_FONT_SIZE_DEF                =   15;

    private static final String  IMAGE_LIST_FONT_NAME_KEY               =   "IMAGE_LIST_FONT_NAME_KEY";
    private static final String  IMAGE_LIST_FONT_NAME_DEF                =   "Lucia Grande";

    private static final String  IMAGE_LIST_FONT_BOLD_KEY        =   "IMAGE_LIST_FONT_BOLD_KEY";
    private static final boolean IMAGE_LIST_FONT_BOLD_DEF        =  false;

    private static final String  RESET_CONSTRAST_ON_NEW_IMAGE_KEY        =   "RESET_CONSTRAST_ON_NEW_IMAGE_KEY";
    private static final boolean RESET_CONSTRAST_ON_NEW_IMAGE_DEF        =  false;


    private static final String  THRESHOLD_IMAGE_PIXEL_EXTRACTION_KEY   =   "THRESHOLD_IMAGE_PIXEL_EXTRACTION_KE";
    private static final boolean THRESHOLD_IMAGE_PIXEL_EXTRACTION_DEF        =  false;


     public static String getAbsolutePath(){
        return  prefs.absolutePath();
    }
     public static  void resetPreferences(){
        try {
            if (prefs != null &&  prefs.nodeExists("")) {

                prefs.removeNode();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
        }
    }


    public static Font   getImageListFont(){
        String name                 = getImageListFontName();
        int size                    = getImageListFontSize();
        int type                    = 0;
        if ( isImageFontBold()) {type = 1;}
        Font  font    =   new java.awt.Font(name, type , size  );
        return font;
    }


    public static int getImageListFontSize(){
      return  prefs.getInt(  IMAGE_LIST_FONT_SIZE_KEY, IMAGE_LIST_FONT_SIZE_DEF  );
    }
    public static void setImageListFontSize(int val){
      prefs.putInt( IMAGE_LIST_FONT_SIZE_KEY, val);
    }

    public static String getImageListFontName(){
      return  prefs.get(  IMAGE_LIST_FONT_NAME_KEY, IMAGE_LIST_FONT_NAME_DEF  );
    }
    public static void setImageListFontName(String val){
      prefs.put( IMAGE_LIST_FONT_NAME_KEY, val);
    }

    public static boolean isImageFontBold(){
      return  prefs.getBoolean(IMAGE_LIST_FONT_BOLD_KEY  , IMAGE_LIST_FONT_BOLD_DEF   );
    }
    public static void setImageFontBold(boolean val){
      prefs.putBoolean(IMAGE_LIST_FONT_BOLD_KEY, val);
    }


    public static int getImageContrastBarLabelPresision(){
      return  prefs.getInt(CALIBRATION_BAR_LABEL_PRECISION_KEY , 4);
    }
    public static void setImageContrastBarLabelPresision(int val){
      prefs.putInt(CALIBRATION_BAR_LABEL_PRECISION_KEY , val);
    }

    public static int getImagePixelValuePresision(){
      return  prefs.getInt(IMAGE_PIXEL_VALUE_PRECISION_KEY , 6);
    }
    public static void setImagePixelValuePresision(int val){
      prefs.putInt(IMAGE_PIXEL_VALUE_PRECISION_KEY , val);
    }


   


    public static boolean isAutoContrastNewImage(){
      return  prefs.getBoolean( RESET_CONSTRAST_ON_NEW_IMAGE_KEY  , RESET_CONSTRAST_ON_NEW_IMAGE_DEF  );
    }
    public static void setAutoContrastNewImage(boolean val){
      prefs.putBoolean(RESET_CONSTRAST_ON_NEW_IMAGE_KEY , val);
    }


    public static boolean isThresholdImagePixelExtraction(){
      return  prefs.getBoolean( THRESHOLD_IMAGE_PIXEL_EXTRACTION_KEY   , THRESHOLD_IMAGE_PIXEL_EXTRACTION_DEF  );
    }
    public static void setThresholdImagePixelExtraction(boolean val){
      prefs.putBoolean( THRESHOLD_IMAGE_PIXEL_EXTRACTION_KEY, val);
    }


}
