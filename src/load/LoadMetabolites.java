/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.DirectoryManager;
import load.gui.JRemoteFileChooser;
import utilities.*;
import java.io.File;
import bayes.Enums.METABOLITE_FILE_TYPE;
import java.io.FileNotFoundException;
import java.net.*;
/**
 *
 * @author apple
 */
public class LoadMetabolites {
     public static File loadSystemMetaboliteFile(File sysMetabFile){
       File specDir                 = DirectoryManager.getUserPredefinedSpecDir();
       if(!specDir .exists ())      {specDir.mkdirs();}
         
       
       File userMetabFile            = new File(specDir  ,sysMetabFile.getName());

       utilities.IO.copyFile(sysMetabFile , userMetabFile  ); 
       

       return userMetabFile;
      }
     public static File loadRemoteSystemMetaboliteFile(String remoteDir, String isoFilename) throws SocketTimeoutException, FileNotFoundException{
        File specDir                 = DirectoryManager.getUserPredefinedSpecDir();
        String spr                   = DirectoryManager.spr; // "/"
        
        if(!specDir.exists ()){specDir.mkdirs();}
         
       String fromModelFile          = remoteDir + spr + isoFilename;
       File userMetabFile            = new File(specDir  ,isoFilename);
       try {
            JRemoteFileChooser.downloadFile( fromModelFile , userMetabFile );
       }
       catch (SocketTimeoutException exp){throw new SocketTimeoutException(exp.getMessage());}
       catch (FileNotFoundException exp){throw new FileNotFoundException(exp.getMessage());}
      
       

       return userMetabFile;
      }
     
     public static File loadUserMetaboliteFile(File userMatabFile, METABOLITE_FILE_TYPE type){
       File asciiDir                = DirectoryManager.getBayesOtherAnalysisDir();
       LoadAndViewData.cleanMetaboliteFiles(asciiDir, type);
       
       File expMetabFile            = new File(asciiDir,userMatabFile.getName());
       IO.copyFile(userMatabFile, expMetabFile);
       
        
       return expMetabFile;
     }

}
