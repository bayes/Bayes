/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;
import bayes.DoCGI;
import bayes.PackageManager;
import bayes.JobDirections;
import bayes.BayesManager;
import bayes.DirectoryManager;
import utilities.*;
import java.io.File;
import interfacebeans.*;
import run.Run.RUN_STATUS;
import bayes.Enums.*;
import applications.model.*;
import fid.FidModelViewer;
import fid.FidViewer;
import fid.BayesAnalyzeParamsWriter;
/**
 *
 * @author apple
 */
public class RunBayesModel {
   private static String modelJobID                             =   null  ;
   private static RUN_STATUS buildModelJobStatus               =   RUN_STATUS.NOT_RUN  ;
   public static void      tarModelJob(){
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    tarFile         =   Run.getTarFile();
        File    fidDir          =   DirectoryManager.getFidDir();  
       // File    bayesAnlDir     =   DirectoryManager.getBayesAnalyzeDir();

        int modelNumber         =   FidModelViewer.getInstance().getModelNumber();
        // make sure that model params file exists
        int modelIndex          =   FidViewer. getFidModelIndex (  modelNumber  );

        File modelFile          =   DirectoryManager.getModelFile(modelIndex );
        Tar.tar(tarFile, bayesRoot, modelFile, fidDir);
    }
    
    public static void    showResultsForBayesModel(Model curModel) {
         if (buildModelJobStatus  ==  RUN_STATUS.RUN )
        {
            boolean isGetResults = Run.getJobFromServer(modelJobID  );
            if (!isGetResults){ return; }

           
            Run.removeJobFromServer(modelJobID );
            Run.removeUsedFiles();
            modelJobID  = null;

            // load new model
            File dir = DirectoryManager.getFidModelDir();
            FidModelViewer.getInstance().loadFidModel (dir);

            // make sure FidModelViewer is visible
            AllViewers.showFidModelViewer();

            // fire ModelRunEvent
            BayesManager.fireBayesModelHasRunEvent();
        } 
        
        else if (buildModelJobStatus == RUN_STATUS.ACTIVE)
        {
           Run.showResultsForStatusACTIVE(curModel, modelJobID);
        }
   
    }
    public static void    submitAndGetModel(){
         submitAndGetModel(PackageManager.getCurrentApplication());
    }
    public static void    submitAndGetModel(Model model){
      try {
        tarModelJob();
        Run.doZipTar();
        doSubmitRun();
       
        while (modelJobID != null ){
                    Thread.sleep(1 * 1000);
                    buildModelJobStatus  = Run.getJobStatusOnServer(modelJobID,  buildModelJobStatus.getName());
                    showResultsForBayesModel(model);
         }
       } 
        catch (InterruptedException ex) {
                    ex.printStackTrace();
                    JRun.setStatus(RUN_STATUS.ERROR);
        }
          
  }

    public static void      doSubmitRun(){
        String url                  =   BayesManager.getURL();
        String user                 =   BayesManager.getUser();
        File    ziptarFile          =   Run.getZipTarFile();
        String password             =   JServerPasswordDialog.getServerPassword();

        int    usrOption            =   JServerPasswordDialog.getInstance().getOption();
        if(   usrOption == JServerPasswordDialog.CANCEL ) {return ;}

         modelJobID              =   DoCGI.submitJob(url, ziptarFile, user, password);


        // checks genearted jobID and sets status
        validateJobIDandSetSubmissionStatus();
    }
    public static void      validateJobIDandSetSubmissionStatus(){
         try {
            int id = Integer.valueOf(modelJobID);
            if (id != 0)
            {
                Server server = JServer.getInstance().getServer();
                if (server.getQueue().equalsIgnoreCase("None")){
                     buildModelJobStatus = RUN_STATUS.ACTIVE;
                }
                else{
                     buildModelJobStatus = RUN_STATUS.SUBMITTED;
                }
            }
            else
            {
                 buildModelJobStatus = RUN_STATUS.ERROR;
                return ;
            }

         }catch (NumberFormatException exp){
                buildModelJobStatus = RUN_STATUS.ERROR;
                return;
        }
        return ;
    }
   




   
    public static  boolean isModelReadyToRun () {
        // make sure data is loaded
        boolean isDataLoaded = FidViewer. isValidFidData( );
        if ( isDataLoaded == false ) {
           DisplayText.popupErrorMessage("To generate fid model,you must\n" +
                                          "fist load fid data. Exiting...");
           return false;
        }

        int modelFid            =   FidModelViewer.getInstance().getModelNumber();
        // make sure that model params file exists
        int ind                 =   FidViewer. getFidModelIndex ( modelFid  );
        File modelFile          =   DirectoryManager.getModelFile(ind);



        if ( modelFile.exists() == false ) {
            DisplayText.popupErrorMessage("File " + modelFile.getAbsolutePath() + "\n" + " doesn't exist");
            return false;
        }


        BayesAnalyzeParamsWriter.updateModelFidInFile(  modelFile, modelFid );
        BayesAnalyzeParamsWriter.overwriteModelFileHeader(modelFile);

        // make sure directory for model fid exists  parameter file
        File modelFidDir        = DirectoryManager.getFidModelDir();

        if ( modelFidDir.exists() ) {
            utilities.IO.deleteDirectory(modelFidDir);
        }
        modelFidDir.mkdir();

        if ( modelFidDir.exists() == false ) {

           DisplayText.popupErrorMessage("Failed to create " + modelFidDir.getAbsolutePath());
            return false;
        }



        //  write "job.directions" file
        File bayesAnalyzeDir        = DirectoryManager.getBayesAnalyzeDir();
        boolean isJobDirection      = JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT,
                                               modelFile.getName(),
                                               BayesManager.MODEL_PROGRAM_NAME,
                                               bayesAnalyzeDir );
        if ( isJobDirection == false ) {
             DisplayText.popupErrorMessage("Failed to write job.directions file.");
             return false;
        }
        return true;
    }
}
