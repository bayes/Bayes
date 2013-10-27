/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import bayes.PackageManager;
import bayes.DirectoryManager;
import applications.model.Model;
import ascii.AsciiDescriptor;
import ascii.AsciiIO;
import image.ImageDescriptor;
import interfacebeans.JServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import run.JRun;
import run.Run.RUN_STATUS;
import utilities.IO;
import utilities.Server;

/**
 *
 * @author apple
 */
public class DirectoryInfo  {

    public static final  String pad     =   " ";
    public static final  String rp      =   "=   ";
    public static final  int padlen     =   20;

    public static String getNoInfoMessage(File dir){
        String tmp = "No previous analysis is stored\n" +
                      "in the " + dir.getAbsolutePath()+
                      "\n\n";

        return tmp;
    }

   public static String getWorkingDirInfo(String dirName) {
        File parentdir         =    DirectoryManager.getBayesDir();
        File dir               =    new File(parentdir,dirName);
        File serFile           =    DirectoryManager.getSerializationFile(dir);
        File fidDir            =    DirectoryManager.getFidDir(dir );
        File asciiDir          =    DirectoryManager.getBayesOtherAnalysisDir(dir);
        File imageDir          =    DirectoryManager.getImageDir(dir);
        File workDir           =    DirectoryManager.getExperimentDir () ;
        StringBuilder sb       =    new StringBuilder();

        Model model            =    PackageManager.getCurrentApplication();
        boolean isCurDir       =    dir.equals(workDir);



       sb.append(IO.pad("This WorkDir", padlen,pad));
       sb.append(rp + dir.getAbsolutePath() + "\n");
       sb.append(IO.pad("Current WorkDir", padlen,pad));
       sb.append(rp +  workDir.getAbsolutePath() + "\n");


        if      (isCurDir && model!= null)  { sb.append(getModelInfo(model)); }
        else if (serFile.exists() == false) { sb.append(getNoInfoMessage(dir));}
        else                                { sb.append(getSerilizedInfo(serFile));}
        
        sb.append("\n");

           String    asciiInfo  =        listAsciiDataSources( asciiDir );
           sb.append("ASCII data"); sb.append("\n");
           sb.append( asciiInfo)  ; sb.append("\n");
          
         

           sb.append("Spectral Fid"); sb.append("\n");
           sb.append( listSpectalFidSources(fidDir));sb.append("\n");

           String    imageInfo  =        listImageDataSources( imageDir );
           sb.append("Image data"); sb.append("\n");
           sb.append( imageInfo)  ;  sb.append("\n");
          
       
         return sb.toString();
    }
       public static String        listImageDataSources(File dir){
        List <File> ifhs              =  DirectoryManager.getIfhFileListOnDisk(dir);
        StringBuilder sb              =   new StringBuilder();

        for (File file : ifhs) {
           try{
               ImageDescriptor id =   image.ImageIO.loadFromDisk(file);
               String source      =   id.getSourceFileName();
               String name        =   id.getDataFileName();
                 if (source!= null){
                 if (source!= null){
                    sb.append("Image   " + name);
                    sb.append("\n");
                 }
                    sb.append("Source  " + source);
                    sb.append("\n");

                 }

           }
           catch(Exception exp){exp.printStackTrace()  ;continue;}

       }
       if (sb.length()>1){return sb.toString();}
       else {return "No image data information is available";}
   }
       public static String        listAsciiDataSources(File dir){
        List <File> afhs              =    DirectoryManager.getValidAfhFileListOnDisk(dir);
        StringBuilder sb              =   new StringBuilder();
        for (File file : afhs) {
           try{
                 AsciiDescriptor ad =   AsciiIO.loadFromDisk(file);
                 String source      =   ad.getSourceFileName();
                 String type        =   ad.getDataSource();
                 String name        =   DirectoryManager.getDatFileForIfh (file).getName();
                 if (source!= null){

                    sb.append("Name    " + name);
                    sb.append("\n");

                    sb.append("Type    " + type);
                    sb.append("\n");

                    sb.append("Source  " + source);
                    sb.append("\n");
                 }

           }
           catch(Exception exp){exp.printStackTrace()  ;continue;}

       }

       if (sb.length()>1){return sb.toString();}
       else {return "No ascii data information is available\n";}
   }
       public static StringBuilder listSpectalFidSources(File dir)  {
         File ffhFile           =    DirectoryManager. getFidDesciptorFile(dir);
         StringBuilder sb       =    new StringBuilder();

         if (ffhFile.exists()) {
            fid.FidDescriptor ffh = fid.FidIO.loadFromDisk(ffhFile);

            if (ffh.getSourceFileName()!=null){
                sb.append("Source   " +ffh.getSourceFileName()); sb.append("\n");
            }
            else{
                sb.append("Spectral fid information is not available "); sb.append("n");
            }
         }
         else {
             sb.append("No spectral fid is loaded"); sb.append("\n");
         }

         return sb; 
       }
       
       public static StringBuilder getSerilizedInfo(File serFile){
         FileInputStream   fin      =   null;
         ObjectInputStream objIn    =   null;
         StringBuilder sb           =    new StringBuilder();

         try{

            fin   = new  FileInputStream (serFile);
            objIn = new  ObjectInputStream(fin);

            String oldClassName         = (String)objIn.readObject();
            String constructorArg       = (String)objIn.readObject();
            String programName          = (String)objIn.readObject();
            String jobID                = (String)objIn.readObject();
            String jobStatus            = ((RUN_STATUS)objIn.readObject()).getName();
            String server               = (String)objIn.readObject();

            sb.append(IO.pad("Package", padlen,pad));
            sb.append(rp + Class.forName(oldClassName).getSimpleName());
            sb.append("\n");
            if (constructorArg!= null){
                sb.append(IO.pad("Package arguments", padlen,pad));
                sb.append(rp + constructorArg);
                sb.append("\n");
            }


            sb.append(IO.pad("Server", padlen,pad));
            sb.append(rp + server);
            sb.append("\n");
            sb.append(IO.pad("Job Status", padlen,pad));
            sb.append(rp + jobStatus);
            sb.append("\n");
            if (jobID!=null){
                 sb.append(IO.pad("Job ID", padlen,pad));
                 sb.append(rp + jobID);
                 sb.append("\n");
            }

          }catch(Exception exp){
                 exp.printStackTrace();
                 System.err.println("Deserialization failed");
                 sb.append( getNoInfoMessage(serFile.getParentFile()));
                 return sb;
         } finally{
             try{
                objIn.close();
                fin.close();
                 return sb;
             } catch ( IOException iox){
                iox.printStackTrace();
                return sb;
            }
         }
}
       public static StringBuilder getModelInfo( Model model  ){
         
            StringBuilder sb           =    new StringBuilder();

            String className         =   model.getClass().getSimpleName();
            Server srv               =   JServer.getInstance().getServer();
            String server            =   srv.getName();
            int port                 =   srv.getPort();

            sb.append(IO.pad("Package", padlen,pad));
            sb.append(rp + className );
            sb.append("\n");
            sb.append(IO.pad("Server", padlen,pad));
            sb.append(rp + server);
            sb.append("\n");
            sb.append(IO.pad("Server port", padlen,pad));
            sb.append(rp + port);
            sb.append("\n");
            sb.append(IO.pad("Job Status", padlen,pad));
            sb.append(rp + JRun.getStatus().getName());
            sb.append("\n");
            if (JRun.getJobID()!=null){
                 sb.append(IO.pad("Job ID", padlen,pad));
                 sb.append(rp + JRun.getJobID());
                 sb.append("\n");
            }

             return sb;
       }


}
