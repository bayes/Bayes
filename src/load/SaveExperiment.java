/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import bayes.Serialize;
import java.io.File;
import javax.swing.SwingWorker;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class SaveExperiment  extends SwingWorker<Boolean, String>{
      private String error                      = "Unknown";
      private boolean successfulSave                     = false;
      public File sourceExp                     = null;
      public File destinExp                     = null;
      @Override
       public Boolean doInBackground() {
           successfulSave            =    false;
           try{

              if (sourceExp  == null ){
                  error = String.format("No working directory is currently loaded.");
                 throw new IllegalArgumentException(error);
              }
              if (sourceExp .exists() == false){
                error = String.format("No valid working directory is currently loaded.");
                throw new IllegalArgumentException(error);
              }

              if ( destinExp  == null ){
                  error = String.format("Destination directory is not specified.");
                 throw new IllegalArgumentException(error);
              }
              if ( destinExp.exists()){
                     boolean dirCleared = IO.deleteDirectory(destinExp);
                     if (dirCleared == false){
                          error = String.format("Failed to cleans %s \n"
                          + "prior to saving.", destinExp.getPath());
                            throw new IllegalArgumentException(error);
                     }
                  
              }
             else  {
                 boolean canMakeDir    =   destinExp.mkdirs();
                    if (canMakeDir == false){
                        error = String.format("Failed to create required directory\n%s.", destinExp.getPath());
                        throw new IllegalArgumentException(error);

                    }
              }

            
               Serialize.serializeExperiment(sourceExp);


              if (destinExp.equals(sourceExp)){
                error   = String.format(
                            "Source and destination directories\n"
                        +   "are the same. Nothing to save.\n",
                             sourceExp.getName(),
                             destinExp.getPath());
                throw new IllegalArgumentException(error);

              }
              else{

                boolean dirCopied = IO.copyDirectory( sourceExp,destinExp);
                if ( dirCopied  == false){
                    error   = String.format(
                            "Failed to copy %s \nto directory %s"
                             ,
                             sourceExp.getName(),
                             destinExp.getPath());
                throw new IllegalArgumentException(error);

              }
            }


                successfulSave    = true;
           }
           catch (Exception e){
               successfulSave    = false;
               if ( e.getMessage() != null){
                error             = e.getMessage();
               }
                e.printStackTrace();

           }
           finally{
             return successfulSave ;
           }

       }

       @Override
       protected void done() {
           String message           =    null;
           if (successfulSave){
                message = String.format(
                        "Working directory %s is saved to\n" +
                         "%s",
                         sourceExp.getName(),
                         destinExp.getPath());

                         DisplayText.popupMessage(message);
          }
          else{
               message = String.format(
                        "Failed to save %s to\n" +
                         "%s\n"+
                         "Following error has occured:\n"
                         + "%s",
                         sourceExp.getName(),
                         destinExp.getPath(),
                         error);
                 DisplayText.popupErrorMessage(message);

          }
                
              
    }
}
