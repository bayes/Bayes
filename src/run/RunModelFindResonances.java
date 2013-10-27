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
import bayes.Enums.*;
import applications.model.*;
import fid.FidModelViewer;
import java.util.logging.Level;
import java.util.logging.Logger;
import fid.FidViewer;
import fid.BayesAnalyzeParamsWriter;
import run.Run.RUN_STATUS;
/**
 *
 * @author apple
 */
public class RunModelFindResonances  {
   private static String modelJobID                             =   null  ;
   private static RUN_STATUS buildModelJobStatus               =   RUN_STATUS.NOT_RUN  ;
   public static void      tarModelJob(){
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    tarFile         =   Run.getTarFile();
        File    fidDir          =   DirectoryManager.getFidDir();
        File    bayesOtherDir   =   DirectoryManager.getBayesOtherAnalysisDir();

       Tar.tar(tarFile, bayesRoot,  bayesOtherDir, fidDir);
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
    public static void    submitAndGetModel(Model model){
      try {
        tarModelJob();
        Run.doZipTar();
        doSubmitRun();

        while (modelJobID != null ){
               System.out.println("modelJobID "+modelJobID);
                    Thread.sleep(1 * 1000);
                    buildModelJobStatus  = Run.getJobStatusOnServer(modelJobID, buildModelJobStatus.getName());
                    showResultsForBayesModel(model);
         }
       }
        catch (InterruptedException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(JRun.class.getName()).log(Level.SEVERE, null, ex);
                    JRun.setStatus(RUN_STATUS.ERROR);
        }

  }

    public static void      doSubmitRun(){
        String url                  =   BayesManager.getURL();
        String user                 =   BayesManager.getUser();
        File    bayesRoot           =   DirectoryManager.getBayesDir();
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





    public static void    submitAndGetModel(){
         submitAndGetModel(PackageManager.getCurrentApplication());
    }
    public static  boolean isModelReadyToRun (int modelNumber, File modelFile) {
        // make sure data is loaded
        boolean isDataLoaded = FidViewer. isValidFidData( );
        if ( isDataLoaded == false ) {
           DisplayText.popupDialog("You must load data before you run the program");
           return false;
        }




        boolean bl = modelFile.exists();

        if ( bl == false ) {
            DisplayText.popupDialog(" File " + modelFile.getAbsolutePath() + "\n" + " doesn't exist");
            return false;
        }



        BayesAnalyzeParamsWriter.updateModelFidInFile(  modelFile,  modelNumber);
        BayesAnalyzeParamsWriter.overwriteModelFileHeader(modelFile);

        // make sure directory for model fid exists  parameter file
        File modelFidDir = DirectoryManager.getFidModelDir();

        if ( modelFidDir.exists() ) {
            utilities.IO.deleteDirectory(modelFidDir);
        }
        modelFidDir.mkdir();

        bl = modelFidDir.exists();
        if ( bl == false ) {

           DisplayText.popupDialog("Fail to create " + modelFidDir.getAbsolutePath());
            return false;
        }



        //  write "job.directions" file
        File bayesAnalyzeDir = DirectoryManager.getBayesAnalyzeDir();
        bl = JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT,
                                               modelFile.getName(),
                                               BayesManager.MODEL_PROGRAM_NAME);
        if ( bl == false ) {
             DisplayText.popupDialog("Failed to write job.directions file.");
             return false;
        }
        return true;
    }
}
