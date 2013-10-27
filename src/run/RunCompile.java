/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import bayes.DoCGI;
import bayes.ApplicationPreferences;
import bayes.JobDirections;
import bayes.BayesManager;
import bayes.DirectoryManager;
import utilities.*;
import java.io.File;
import interfacebeans.*;
import bayes.Enums.*;
import run.Run.RUN_STATUS;
/**
 *
 * @author apple
 */
public class RunCompile {
   private  String buildJobID                             =   null  ;
   private  RUN_STATUS buildJobStatus                     =   RUN_STATUS.NOT_RUN  ;
   private  File CompileDir                               =   DirectoryManager. getModelCompileDir ();
   EnterAsciiModel model                                  =   null;
   private boolean  canceled                              =   false;
   private boolean  timeouted                             =   false;
   public RunCompile(EnterAsciiModel amodel){
       model  = amodel;

   }
   public    void    submitCompileFortran(   ){
       model.setTimedoutComplile(false);
       try{
          File compileDirectory = CompileDir ;
          IO.emptyDirectory(compileDirectory);
          model.writeModelFile(compileDirectory);
          JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT_COMPILE, compileDirectory );
          tarJob();
          Run.doZipTar();
          doSubmitRun();
          if(isCanceled()){return;}

         int timeouttime            = ApplicationPreferences.getBuildModelTimeOutMils();
         int checkInterval          = 250;
         int nTries                 = timeouttime/checkInterval;
         int curTry                 = 0;
         
         timeouted                  = false; 
        
         while (buildJobID != null && timeouted == false){
                     System.out.println("Fortan/C build job ID "+ buildJobID);
                     Thread.sleep(checkInterval);
                     setBuildJobStatus(Run.getJobStatusOnServer(buildJobID, getBuildJobStatus().getName()));
                     System.out.println("Compile Status "+getBuildJobStatus());
                     updateWithStatus();

                     curTry +=1;
                     if ( curTry >nTries ){
                         timeouted    = true;
                         System.out.println("Model build request has timed out ");
                         System.out.println("Timeout is set to "+  timeouttime+ "µs");
                     }
           }

       }
        catch (InterruptedException ex) {
                    ex.printStackTrace();
                    JRun.setStatus(RUN_STATUS.ERROR);
        }
        finally{
              model.setTimedoutComplile( timeouted );
              EnterAsciiModel.updateModelBuilt(model);
        }

  }

   public  void     completeSuccessfulRun() {
            boolean isGetResults = Run.getJobFromServer(buildJobID );
            if (!isGetResults){ return; }

            Run.removeJobFromServer(buildJobID);
            Run.removeUsedFiles();

            buildJobID              =   null;
            setBuildJobStatus(RUN_STATUS.NOT_RUN);


    }
   public  void     doSubmitRun(){
        String url                  =   BayesManager.getURL();
        String user                 =   BayesManager.getUser();
        File    ziptarFile          =   Run.getZipTarFile();
        String password             =   JServerPasswordDialog.getServerPassword();

        int    usrOption            =   JServerPasswordDialog.getInstance().getOption();
        if(   usrOption == JServerPasswordDialog.CANCEL ) {
              setBuildJobStatus(RUN_STATUS.NOT_RUN);
              this.setCanceled(true);
        }
        else {
             setBuildJobStatus(RUN_STATUS.SUBMITTED);

              buildJobID              =   DoCGI.submitJob(url, ziptarFile, user, password);
            // checks genearted jobID and sets status
            validateJobIDandSetSubmissionStatus();
        }

    }
   public  void     validateJobIDandSetSubmissionStatus(){

            boolean isValidID  = Run.isValidJobID(buildJobID);
            if (isValidID == false) {setStatusToErrorRun();}


            Server server = JServer.getInstance().getServer();
            if (server.getQueue().equalsIgnoreCase("None")){
                  setBuildJobStatus(RUN_STATUS.ACTIVE);
            }
            else{
                  setBuildJobStatus(RUN_STATUS.SUBMITTED);
            }


    }
   public  void     updateWithStatus(){
        switch(getBuildJobStatus() ){
            case ACTIVE     : /* do nothing */ break;
            case SUBMITTED  : /* do nothing */ break;
            case RUN        :  completeSuccessfulRun() ; break;
            default         :  setStatusToErrorRun();break;

        }

        return;
    }


   public   void    tarJob(){
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    tarFile         =   Run.getTarFile();

        Tar.tar(tarFile, bayesRoot, CompileDir);
        System.out.println("Bayes compile tarring directory "+ CompileDir.getAbsolutePath());

    }

    public    void setStatusToErrorRun(){
            setBuildJobStatus(RUN_STATUS.ERROR);
            buildJobID         =   null;
      }


    public RUN_STATUS   getBuildJobStatus() {
        return buildJobStatus;
    }
    public void         setBuildJobStatus(RUN_STATUS buildJobStatus) {
        this.buildJobStatus = buildJobStatus;
    }

    public boolean      isCanceled() {
        return canceled;
    }
    public void         setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean      isTimeouted() {
        return timeouted;
    }
    public void         setTimeouted(boolean timeouted) {
        this.timeouted = timeouted;
    }
}
