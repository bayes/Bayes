/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import interfacebeans.JServerPasswordDialog;
import java.io.File;
import bayes.BayesManager;
import bayes.DirectoryManager;
import bayes.DoCGI;
import utilities.FileCompress;
import utilities.Tar;
import javax.swing.*;
import applications.model.*;
import bayes.ApplicationPreferences;
import interfacebeans.AllViewers;

/**
 *
 * @author apple
 */
public class Run extends JPanel {

   public static enum RUN_STATUS {
            RUN         (   "Run"),
            NOT_RUN     (   "Not Run"),
            ERROR       (   "Error"),
            SUBMITTED   (   "Submitted"),
            DELETED     (   "Deleted"),
            ACTIVE      (   "Active");

        boolean isRunning(){
            if (this == ACTIVE ){return true;}
            else if(this ==SUBMITTED  ){return true;}
            else {return false;}
        }

        private final String name;
        RUN_STATUS(String aname) {
            this.name       = aname;
        }
        public  String getName()          { return name;};
        static public RUN_STATUS getStatus(String aShortName)
        {

            for (RUN_STATUS status: RUN_STATUS.values()) {
                if ( status.getName() .equalsIgnoreCase(aShortName)){
                    return status;
                }
            }
           return RUN_STATUS.ERROR;
         }
     }

   
    public static final char spr                        =   '/';
    private static final String tarfileName              =   "job.tar";
    private static final String zipfileName              =   "job.tar.gz";
    private static final String ziptarfileName           =   "job.results.tar";
    private static final String directionsfileName       =   "job.directions";
    public static final  int NO_COMPILER_CODE            =     1;
    public static final  int COMPILER_ERROR_CODE         =     2;
    
    private Run() { }

    public static File getZipFile(){
          File bayesRoot          = DirectoryManager.getBayesDir();
          File zipFile            = new File(bayesRoot, zipfileName);
          return  zipFile ;
    }
    public static File getTarFile(){
          File bayesRoot          = DirectoryManager.getBayesDir();
          File zipFile            = new File(bayesRoot, tarfileName);
          return  zipFile ;
    }
    public static File getZipTarFile(){
          File bayesRoot          = DirectoryManager.getBayesDir();
          File zipFile            = new File(bayesRoot, ziptarfileName);
          return  zipFile ;
    }
    public static File getDirectionsFile(){
          File bayesRoot          = DirectoryManager.getBayesDir();
          File zipFile            = new File(bayesRoot, directionsfileName);
          return  zipFile ;
    }





    public static boolean getJobFromServer(String id) {
        File zipFile            =   getZipFile();
        File tarFile            =   getTarFile();
        String user             =   BayesManager.getUser();
        String account          =   BayesManager.getAccount();
        String url              =   BayesManager.getURL();
        String sourceFile       =   account + spr + id + spr + zipfileName;
        String password         =   JServerPasswordDialog.getServerPassword();
        
        int usrOption           = JServerPasswordDialog.getInstance().getOption();
        if (usrOption == JServerPasswordDialog.CANCEL) {
            return false;
        }

        setProgressMessage("Downloading job from server");
        boolean isDone          = DoCGI.getResults(url, sourceFile, zipFile, user, password);
        
        setProgressMessage("Unzipping downloaded files");
        FileCompress.unGzip(zipFile, tarFile);

        setProgressMessage("Untarring downloaded files");
        Tar.untar(tarFile);
        return true;
    }

    public static void      removeJobFromServer(String id) {
        if (ApplicationPreferences.isDeleteCompletedJobFromServer()){
        
            String user             = BayesManager.getUser();
            String account          = BayesManager.getAccount();
            String url              = BayesManager.getURL();
            String password         = JServerPasswordDialog.getServerPassword();

            int usrOption = JServerPasswordDialog.getInstance().getOption();
            if (usrOption == JServerPasswordDialog.CANCEL) {
                return;
            }
            DoCGI.deleteCompletedJob(url,id, account, user, password);
            }
        
        }
   
    public static void      removeUsedFiles(){
        boolean fileExist;
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    zipFile         =   new File(bayesRoot, zipfileName);
        File    tarFile         =   new File(bayesRoot, tarfileName);
        File    ziptarFile      =   new File(bayesRoot, ziptarfileName);
        File    directionsFile  =   new File(bayesRoot, directionsfileName);


         fileExist = ziptarFile.exists();
         if (fileExist){ziptarFile.delete();}

         fileExist  = zipFile.exists();
         if (fileExist){zipFile.delete(); }


         fileExist = tarFile.exists();
         if (fileExist){ tarFile.delete();}

         fileExist = directionsFile.exists();
         if (fileExist){ directionsFile.delete();}

    }
    public static void      doZipTar(){
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    zipFile         =   Run.getZipFile();
        File    tarFile         =   Run.getTarFile();
        File    ziptarFile      =   Run.getZipTarFile();
        File    directionsFile  =   Run.getDirectionsFile();

        // GZIP job.tar file into job.tar.gz.
        long startZip       =  System.nanoTime();
        FileCompress.gzip( tarFile.getPath(), zipFile.getPath());
        long stopZip        =  System.nanoTime();
        double zipTime      =   (stopZip- startZip)*1e-9;
        System.out.println("Time to zip job "+ zipTime );


        long startTar       =  System.nanoTime();
        Tar.tar(ziptarFile, bayesRoot, zipFile, directionsFile);
        long stopTar        =  System.nanoTime();
        double tarTime      =   (stopTar - startTar)*1e-9;
        System.out.println("Time to make job.results.tar file "+ tarTime );
        
    }


    public static boolean   getStatusFileFromServer(String jobid){
        String account              =   BayesManager.getAccount();
        String user                 =   BayesManager.getUser();
        String expName              =   DirectoryManager.getExperimentDirName();
        String url                  =   BayesManager.getURL();
        String password             =   JServerPasswordDialog.getServerPassword();
        String analysDir            =   DirectoryManager.getAnalysisDirName();
        File    dstFile             =   DirectoryManager.getBayesAcceptFile();
        boolean isDone              =   getStatusFileFromServer ( jobid, account, url, user, expName,analysDir, password, dstFile);

        return isDone ;
    }

    public static boolean   getStatusFileFromServer(
                                        String jobid,
                                        String account,
                                        String url,
                                        String user,
                                        String expName,
                                        String analysisDir,
                                        String password,
                                        File dstFile){
        String filename             =   BayesManager.BAYES_ACCEPT_FILE;
        String  sourceFile          =   account
                                    +   spr + jobid
                                    +   spr + expName
                                    +   spr + analysisDir
                                    +   spr + filename;


        boolean isDone = DoCGI.getResults (url, sourceFile,dstFile, user, password );
        return isDone ;
    }


    public static void      showResultsForStatusACTIVE(Model curModel,  String jobid) {
         boolean isGetResults = false;

         isGetResults           =  Run.getStatusFileFromServer(jobid);


         if (!isGetResults) {return;}
         File status_file       = DirectoryManager.getBayesAcceptFile();

          AllViewers.showMessage(status_file);
        
     }

    public static boolean   getConsoleLogFileFileFromServer(String filename, String jobid){
       // if (isJobIDValid() == false) {return false;}

        File    acceptFile          =   DirectoryManager.getConsoleLogFile();
        String account              =   BayesManager.getAccount();
        String user                 =   BayesManager.getUser();
        String expName              =   DirectoryManager.getExperimentDirName();
        String url                  =   BayesManager.getURL();
        String password             =   JServerPasswordDialog.getServerPassword();


        String  sourceFile          =   account
                                    +   spr + jobid
                                    +   spr + expName
                                    +   spr + DirectoryManager.getAnalysisDirName()
                                    +   spr + filename;

        //System.out.println("sourceFile  "+sourceFile );

        boolean isDone = DoCGI.getResults (url, sourceFile,acceptFile, user, password );
        return isDone ;
    }
    public static void      showResultsForStatusERROR(Model curModel,  String jobid) {
         boolean isGetResults               =  Run. getConsoleLogFileFileFromServer(BayesManager.CONSOLE_LOG_FILE, jobid);
         if (!isGetResults) {return;}
         File status_file                   = DirectoryManager.getConsoleLogFile();
         AllViewers.showMessage(status_file);

     }



     public static RUN_STATUS   getJobStatusOnServer(String id, String defStatus){
        String account              =   BayesManager.getAccount();
        String url                  =   BayesManager.getURL();
        String user                 =   BayesManager.getUser();
        String folder               =   DirectoryManager.getExperimentDirName();
        String password             =   JServerPasswordDialog.getServerPassword();

        int    usrOption            =   JServerPasswordDialog.getInstance().getOption();
        if(   usrOption == JServerPasswordDialog.CANCEL ) {return RUN_STATUS.NOT_RUN;}

        String  statusOut           =   getJobStatusOnServer (id, defStatus,account,url, user,folder, password);
        RUN_STATUS newStatus        =   RUN_STATUS.getStatus(statusOut);
        return  newStatus;

    }
     public static String   getJobStatusOnServer(
                                        String id,
                                        String defStatus,
                                        String account,
                                        String url,
                                        String user,
                                        String folder,
                                        String password
                                        ){
        String request              =   account + spr + id + spr + folder;
        String status               =   DoCGI.getStatus(url, request, user, password, defStatus);
       
        return status;

    }
     public static boolean  isValidJobID(String jobid){
          if (jobid == null) {
             String errorMessage = String.format("Server error. Invalid job id generated.\n" +
                                                 "Job id  = "+jobid );
            BayesManager.fireCompileErrorEvent(errorMessage); 
              return false;}




          int id            =   0;

          try{
              id = Integer.valueOf(jobid);
          }catch (NumberFormatException exp){ 
                String errorMessage = String.format("Server error. Invalid job id generated.\n" +
                                                 "Job id  = "+jobid );
            BayesManager.fireCompileErrorEvent(errorMessage); 
              
              return false;

          }


          if (id == 0) {return false;}

          else if (id  == NO_COMPILER_CODE){
            String errorMessage = String.format("Fortran or C compilers are not installed on the server.");
            BayesManager.fireCompileErrorEvent(errorMessage);
            return false;
          }
          else if(id  == COMPILER_ERROR_CODE ){
            String errorMessage = String.format(
                    "Server software failed to compile Enter Ascii Model.\n\n" +
                    "Possible causes are:\n\n" +
                    "  Either Fortran or C compilers  are not installed on the server.\n" +
                    "  Either Fortran or C executables are not installed on the server.\n" +
                    "  The Enter Asccii Model code contains error.");
            BayesManager.fireCompileErrorEvent(errorMessage);
            return false;
          }

          return true;
        }



     public static  synchronized void  setProgressMessage(String message){
        AbstractCallBack.progressMessage = message;
    }
}
