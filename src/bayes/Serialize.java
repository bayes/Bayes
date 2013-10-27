/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;
import run.JRun;
import applications.model.Model;
import java.io.*;
import fid.*;
import fid.FidViewer;
import image.ImageViewer;
import interfacebeans.*;
import run.Run.RUN_STATUS;
import utilities.ModelDescriptor;
/**
 *
 * @author apple
 */
public class Serialize {
    public  final static String  DIR_INF0              = "dir.info";
    protected static boolean fidStaticParametersDeserialized = false;

 
    public static void    serializeCurrenExperiment() {
          File dir = DirectoryManager.getExperimentDir();
          serializeExperiment(dir);
    }
    public static void    serializeExperiment(File dir) {
        Model model                 = PackageManager.getCurrentApplication();
        if (model == null) { return;}

        FileOutputStream   fileout  = null;
        ObjectOutputStream objOut   = null;
        File f                      = new  File(dir,DIR_INF0);
        String  value;

        if(!dir.exists()){return;}

        try{
           fileout                  = new  FileOutputStream (f);
           objOut                   = new  ObjectOutputStream(fileout);
       
           value        =   model.getClass().getName();
           objOut.writeObject(value); //1

           value        =   model.getConstructorArg(); 
           objOut.writeObject(value);

           value        =   model.getProgramName();
           objOut.writeObject(value);


           String jobID = JRun.getJobID();
           objOut.writeObject(jobID);

           RUN_STATUS status = JRun.getStatus();
           objOut.writeObject(status);

           String curServer = JServer.getInstance().getServer().getName();
           objOut.writeObject(curServer);


           FidViewer.getInstance().serialize(objOut);
           FidModelViewer.getInstance().serialize(objOut);
           ImageViewer.getInstance().serialize(objOut);
           model.savePackageParameters(objOut);


        } catch (Exception iox){
            iox.printStackTrace();
            System.out.println("Serialization failed...");
            DirectoryManager.cleanCurrentExp();


        }finally{
             try{
                objOut.close();
                fileout.close();
             } catch (IOException iox){
                iox.printStackTrace();
            }
         }
    }

    public static boolean deserializeCurrenExperiment() {
        File expDir         =    DirectoryManager.getExperimentDir();
        if (!expDir.exists()) {  return false;   }

        File serFile        =    DirectoryManager.getSerializationFile();
        return deserializeExperiment( serFile);
    }
    public static boolean deserializeExperiment(File serFile) {
         JRun.setNotRun();

         if (serFile.exists() == false){
              DirectoryManager.cleanCurrentExp();
              return false;
         }

         Model model  =  PackageManager.getCurrentApplication();
         if (model == null) {
                return false;
         }
         String curClassName = model.getClass().getName();

         FileInputStream   fin   =   null;
         ObjectInputStream objIn =   null;
         try{

            fin   = new  FileInputStream (serFile);
            objIn = new  ObjectInputStream(fin);

            String oldClassName = (String)objIn.readObject();

            if(!oldClassName.equals(curClassName)){
                DirectoryManager.cleanCurrentExp();
                return false;
            }

            String constructorArg           = (String)objIn.readObject();
            String programName              = (String)objIn.readObject();



            String jobID      = (String)objIn.readObject();
            JRun.setJobID(jobID);

            RUN_STATUS jobStatus  = (RUN_STATUS)objIn.readObject();
            if(jobStatus == null){throw new NullPointerException();}
            JRun.setStatus(jobStatus);

            String server  = (String)objIn.readObject();
            boolean serverFoundAndSet  = ApplicationPreferences.setAsCurrentServer(server);
            if (serverFoundAndSet == false){
                JRun.setNotRun();
            }
           
            FidViewer fv = FidViewer.getInstance();
            fv.deserialize(objIn);
            FidModelViewer fmv = FidModelViewer.getInstance();
            fmv.deserialize(objIn);
            ImageViewer iv = ImageViewer.getInstance();
            iv.deserialize(objIn);
            


            model.setPackageParameters(objIn);
            

         }catch(Exception exp){
                 exp.printStackTrace();
                 DirectoryManager.getSerializationFile().delete();
                 System.err.println("Deserialization failed");
                 return false;
         } finally{
             try{
                objIn.close();
                fin.close();
             } catch ( IOException iox){
                iox.printStackTrace();
                return false;
            }
         }

         return true;
    }



    public static ModelDescriptor   getDeserializedModelDescriptor() {
         File dir = DirectoryManager.getExperimentDir();
         return getDeserializedModelDescriptor(dir);
    }
    public static ModelDescriptor   getDeserializedModelDescriptor(File dir) {

        ModelDescriptor   modelDescriptor   = new   ModelDescriptor();

        File f                  = new File(dir, DIR_INF0);
        boolean isSerFileExist  = f.exists();
         if (isSerFileExist == false) {
             DirectoryManager.cleanCurrentExp();
             return null;
         }

         FileInputStream   fin   =   null;
         ObjectInputStream objIn =   null;
         try{

            fin   = new  FileInputStream (f);
            objIn = new  ObjectInputStream(fin);

           String  className    = (String)objIn.readObject();
           String  constArg     = (String)objIn.readObject();
           String  programName  = (String)objIn.readObject();
           Class modelClass     = Class.forName(className  );

           modelDescriptor.setModelClass(modelClass);
           modelDescriptor.setConstrArg(constArg);
           modelDescriptor.setModelTitle( programName);

           return modelDescriptor;

         }catch(Exception exp){
              exp.printStackTrace();
              System.err.println("Failed to deserialize moderl descriptor");
              return null;
         }
         finally{
             try{
                objIn.close();
                fin.close();
             } catch ( IOException iox){}
         }
    }  




   

}
