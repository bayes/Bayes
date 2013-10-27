/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import bayes.DirectoryManager;
import load.gui.JRemoteFileChooser;
import utilities.*;
import applications.bayesEnterAsciiModel.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
/**
 *
 * @author apple
 */
public class LoadAsciiModels implements  BayesEneterAsciiConstants {
     private LoadAsciiModels(){}
 
     
     
     // load model for EnterAscii model (just one model)
     public static EnterAsciiModel loadUserModel(File selectedModelFile ){
         
       String bayesModelName        =   selectedModelFile.getName();
     
       File asciiDir                =   DirectoryManager.getBayesOtherAnalysisDir();
       cleanModelFiles(asciiDir);
       
       File expDirModelFile         =   new File(asciiDir,bayesModelName);
       File paramsFile              =   EnterAsciiModel.getParamsFileToRead( selectedModelFile);
       
       
       IO.copyFile(selectedModelFile,expDirModelFile);
       EnterAsciiModel model        = new EnterAsciiModel(selectedModelFile,paramsFile);

       return model;
        
     }
     public static EnterAsciiModel loadRemoteSystemModel(String remoteDir, String modelFilename, boolean alwaysCopyParamsFile)throws SocketTimeoutException, FileNotFoundException{
       File bayesModelDir            = DirectoryManager. getUserModelDir();
       String spr                    = DirectoryManager.spr; // "/"
       if(!bayesModelDir.exists ()){ 
           bayesModelDir.mkdirs();
       }
       EnterAsciiModel  asciiModel   = null;
       
       String paramsFileName         =  EnterAsciiModel.getParamsFileName(modelFilename);
       
       
       String fromModelFile          = remoteDir + spr + modelFilename;
       String fromParamsFile         = remoteDir + spr + paramsFileName;

       File userModelFile            = new File( bayesModelDir, modelFilename);
       File toParamsFile             = new File( bayesModelDir, paramsFileName);

       
       try {
            JRemoteFileChooser.downloadFile( fromModelFile , userModelFile ); 
            if (alwaysCopyParamsFile == false && toParamsFile.exists() == true ){
            // do nothing
            }
            else {
                load.gui.JRemoteFileChooser.downloadFile(fromParamsFile,toParamsFile);
            }

              asciiModel         = loadUserModel(userModelFile);
              asciiModel.setBuilt(true);  // trust system models


     } catch (SocketTimeoutException e){
                 throw new SocketTimeoutException( e.getMessage());
               
       }
       catch (FileNotFoundException exp){
           throw new FileNotFoundException(exp.getMessage());
       }
      
       return asciiModel;    
      }
   


    public static void cleanModelFiles(File dir){
        File [] fileList     = dir.listFiles(new  FortranAndCFileFilter ());

        if(fileList !=  null){
          for (File modelFile : fileList) {
                 modelFile.delete();
          }
        }
     }
    public static void cleanModelFiles(){
        File dir        =   DirectoryManager.getBayesOtherAnalysisDir();
        if (dir.exists() ){cleanModelFiles(dir);}

        dir             =   DirectoryManager.getModelCompileDir();
        if (dir.exists() ){IO.emptyDirectory(dir);}
     }

}
  class FortranAndCFileFilter    implements FileFilter {
        public FortranAndCFileFilter(){
        }
        public boolean accept(File file) {
            if (file.getName().endsWith(".c")   == true) { return true;}
            if (file.getName().endsWith(".f")   == true) { return true;}
            if (file.getName().endsWith(".so")  == true) { return true;}
            if (file.getName().endsWith(".lst") == true) { return true;}
            return false;
        }
    };