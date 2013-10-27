/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.File;

/**
 *
 * @author apple
 */
public class OSConversions {
     public final static String slash             = "/";
     public final static String backslash         = "\\";
  
     public enum DIRECTIONS {FROM_WINDOWS, TO_WINDOWS};
     
     public static String modifyForWindows(String filepath, DIRECTIONS directions ){
          if ( isWindows() == false){ return filepath;}
          
          switch (directions){
              case FROM_WINDOWS   :     filepath     =   filepath.replaceAll("\\\\+","/");
                                        filepath     =   filepath.replaceAll("\\+","/");
                                        break;
              case TO_WINDOWS :         filepath     =   filepath.replaceAll("/","\\\\");
                                        break;                  
          }
          
          return filepath;   
    }
     public static File modifyForWindows(File file, DIRECTIONS directions ){
          if ( isWindows() == false){ return file;}
          
          String filepath = modifyForWindows(file.getPath(),directions );
          File   result   = new File(filepath);
          
          
          return result;   
    }
     
     public static boolean isWindows(){
       String os = (String) System.getProperties ().getProperty("os.name");
       return os.contains("Windows");
    }
    public static void main (String []args){
  
      System.out.println(slash);
  }
}
