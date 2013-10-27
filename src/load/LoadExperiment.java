/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.ApplicationPreferences;
import bayes.DirectoryManager;
import java.awt.Cursor;
import utilities.*;
import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

/**
 *
 * @author apple
 */
public class LoadExperiment  {
    // for single experiment load
    public File sourceExp                       = null;
    public File destinExp                       = null;
    public static String DESTINATION_DIR_NAME   = null;
    
    // for multiples experiments load
    public Collection<File> srcWorkDirs         =   new TreeSet<File>();
    public File dstRoot                         =   null;
    
     public void loadSingleExperiment(){
          try{

           Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

         
           boolean loaded = loadDir (sourceExp, destinExp);

           if (loaded){
            String message = String.format("Working directory %s is copied to\n" +
                                            "%s",sourceExp.getPath(),destinExp.getName());
            DisplayText.popupMessage(message);
         }
         
       }finally{
        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
      }

    }
     public void loadMultipleExperiments(){
         StringBuilder sb = new StringBuilder();
          try{
            
           Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
           for (File dir : srcWorkDirs) {
                 boolean loaded      =   false;
                 String curExpName  =   dir.getName();
                 
                  if (dir.exists()){
                    sourceExp          =   dir; 
                    destinExp          =   new File (dstRoot, curExpName);
                    loaded             = loadDir (sourceExp, destinExp);
                 }
                 
                 if (loaded){
                     sb.append("WorkDir "+ curExpName + " was loaded.\n\n");
                 }
                 else{
                     sb.append("WorkDir "+ curExpName + " was not loaded.\n");
                 }
           }
          

           if (sb.toString().isEmpty() == false){
            DisplayText.popupMessage(sb);
         }
         
       }finally{
        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
      }

    }
     public boolean checkFilesValidity(){
         boolean valid      =   false;
         try{
            if (sourceExp  == null ){
            String message = String.format(
                    "No save working directory was specified. Abort load...");
            DisplayText.popupErrorMessage(message);
            return false;
           }
           if (sourceExp .exists() == false){
                String message = String.format("Specified working directory \n" +
                                               "%s\n" +
                                               "doesn't exist. Abort load...", sourceExp.getPath());
                DisplayText.popupErrorMessage(message);
                return false;
            }
             System.out.println("Destinattion name \""+ destinExp.getName()+"\"");
           boolean isValidDestinationName  = IO.isValidFileName( destinExp.getName());
           if (isValidDestinationName == false){
               String message = String.format("%s is not valid working directory name.\n"
                                                + "Exit experiment load", destinExp.getName());
               DisplayText.popupErrorMessage(message);
               return false;
           }
             valid           =  true;
         }
     
         catch (Exception e){   e.printStackTrace();}
         finally{
             return valid;
         }
     
     }
     public boolean loadDir(File src, File dst ){
         boolean success            =   false;
         try{
             
             System.out.println("");
             System.out.println("^^^^^^^^^^^^^^^^^^");
             System.out.println("Processing directory "+ src.getPath());
           if (checkFilesValidity() == false){
               throw new IllegalArgumentException();
           }
            System.out.println("It has valid file name");
           if ( destinExp.exists() == false){
                 destinExp.mkdirs();
           }
            System.out.println("All necessary directories has been created");
            if (sourceExp.equals(destinExp) == false){
               IO.deleteDirectory(destinExp);
            System.out.println("Destination directory has been deleted");             
               IO.copyDirectory( sourceExp,destinExp);
             System.out.println("Directory has been copied");                   
           }

             String destName            =   destinExp.getName();
             boolean isRegistered       =   recordChange(destName);
             if ( isRegistered  ){
                  System.out.println("Change has been registered.");  
             }
             else{
                  System.out.println("Failed to register.");  
                  throw new IllegalStateException();
             }
            
           
             
             success                =   true;
         }catch (Exception e){e.printStackTrace();}
         
         finally{
             System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");
             return success;
         }

    }
     public boolean recordChange(String destName ){
         boolean out        =   true;
         try{
             boolean isAlreadyRegistered = ApplicationPreferences.isWorkDir(destName);
             
             if ( isAlreadyRegistered == false){
                  out            =     ApplicationPreferences.addToWorkDirList(destName );
             }
            
         }
         catch (Exception e){e.printStackTrace();}
         finally {return out;}
         
        
          
    }
}
