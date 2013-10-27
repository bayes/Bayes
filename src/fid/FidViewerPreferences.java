/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import java.awt.Color;
import java.util.prefs.Preferences;

/**
 *
 * @author apple
 */
public class FidViewerPreferences {
    private static Preferences prefs                                    =   Preferences.userNodeForPackage( FidViewerPreferences.class);
        public static Color       CURSOR_COLOR                              = Color.RED;
        public static Color       TRACE_1_COLOR                             = Color.GREEN;
        public static Color       TRACE_2_COLOR                             = Color.WHITE;
    public  static String crsValueFormat                                = "% 1.7f";

    private static final String FID_CURSOR_PRESISION_KEY                =   "FID_CURSOR_PRESISION";
    private static final int FID_CURSOfR_PRESISION_DEF                  =   7;

    private static final String FID_CURSOR_COLOR_KEY                    =   "FID_CURSOR_COLOR_KEY";
    private static final int FID_CURSOR_COLOR_DEF                       =   CURSOR_COLOR .getRGB(); // RED

    private static final String FID_TRACE_1_COLOR_KEY                    =   "FID_TRACE_1_COLOR_KEY";
    private static final int FID_TRACE_1_COLOR_DEF                       =   TRACE_1_COLOR.getRGB();

    private static final String FID_TRACE_2_COLOR_KEY                    =   "FID_TRACE_2_COLOR_KEY";
    private static final int FID_TRACE_2_COLOR_DEF                       =   TRACE_2_COLOR.getRGB();
    

    static{
        refreshCursorColor();
        refreshTraceColors();
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
    public static void persist(){
       try{
           prefs.flush();
       }
       catch (Exception e){
        e.printStackTrace();
       }
    }

    public static void refreshCursorValueFormat(){
            int val                     =  FID_CURSOfR_PRESISION_DEF;
            try{
                val                     =    getFidCursorPresision();
            }
            finally{
                String out             = "% 1."+ val+"f";
                crsValueFormat         =    out;
            }

    }
    public static void refreshCursorColor(){
            try{
                int val                     =  getFidCursorColor();
                CURSOR_COLOR                = new Color(val);
            }
            catch (Exception e){e.printStackTrace();}


    }
    public static void refreshTraceColors(){
            try{
                TRACE_1_COLOR                   =   new Color(getFidTraceColor1());
                TRACE_2_COLOR                   =   new Color(getFidTraceColor2());
            }
            catch (Exception e){e.printStackTrace();}


    }

    public static int getFidCursorColor(){
      return  prefs.getInt(FID_CURSOR_COLOR_KEY  , FID_CURSOR_COLOR_DEF   );
    }
    public static void setFidCursorColor(int val){
      prefs.putInt(FID_CURSOR_COLOR_KEY , val);
    }

    public static int getFidTraceColor1(){
      return  prefs.getInt(FID_TRACE_1_COLOR_KEY, FID_TRACE_1_COLOR_DEF   );
    }
    public static void setFidTraceColor1(int val){
      prefs.putInt(FID_TRACE_1_COLOR_KEY , val);
    }


    public static int getFidTraceColor2(){
      return  prefs.getInt(FID_TRACE_2_COLOR_KEY, FID_TRACE_2_COLOR_DEF   );
    }
    public static void setFidTraceColor2(int val){
      prefs.putInt(FID_TRACE_2_COLOR_KEY , val);
    }

    public static int getFidCursorPresision(){
      return  prefs.getInt(FID_CURSOR_PRESISION_KEY , FID_CURSOfR_PRESISION_DEF  );
    }
    public static void setFidCursorPresision(int val){
      prefs.putInt(FID_CURSOR_PRESISION_KEY , val);
    }

    public static void main (String [] args){
        System.out.println(Color.GREEN.getRGB());
    }
}
