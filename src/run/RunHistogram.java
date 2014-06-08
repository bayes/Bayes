/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;
import bayes.DoCGI;
import bayes.ParameterPrior;
import bayes.WriteBayesParams;
import bayes.JobDirections;
import bayes.BayesManager;
import bayes.DirectoryManager;
import utilities.*;
import javax.swing.*;
import java.io.File;
import interfacebeans.*;
import java.util.List;
import java.util.Vector;
import bayes.Enums.*;
import run.Run.RUN_STATUS;
/**
 *
 * @author apple
 */
public class RunHistogram  implements Runnable{

   Thread runThread                 =   null;     
   String jobID                     =   null  ;
   RUN_STATUS status                =   RUN_STATUS.NOT_RUN  ;
   String histogramName             =   null;

   ProgressMonitor pbar             =   null;
   boolean canceled                 =   false;
   int curProgress                  =   99;

   JProgress progresmonitor;

   public  RunHistogram(String histName){
        histogramName  = histName;
   }
             
   public static void       execute(String histName){
      if (isReadyToRun() == false)  {return;}
      
      RunHistogram runHistogram = new RunHistogram(histName);
      runHistogram.runThread    = new Thread(runHistogram);
      runHistogram.runThread.start();
   }
   public void              run(){
     
       submitAndGetModel();
       reset();
   }
   public  void             reset(){
        runThread            =   null;     
        jobID                =   null  ;
        status               =   RUN_STATUS.NOT_RUN  ;
    }
   public static boolean    isReadyToRun(){
        
        // make sure data has been loaded
        File mcmcFile        =   DirectoryManager.getMcmcSamplesFile();
        File plotList        =   DirectoryManager.getBayesPlotFile();
        File dir             =   DirectoryManager.getBayesOtherAnalysisDir();
        
        // make sure all data files exist
        if (!mcmcFile.exists()) {
            DisplayText.popupErrorMessage( "MCMC sample file " + mcmcFile.getPath() + "doesn't exist.");
            return false;
        }

        // make sure all data files exist
        if (!plotList.exists()) {
            DisplayText.popupErrorMessage( "MCMC sample file " + plotList.getPath() + "doesn't exist.");
            return false;
        }
        
        // write the parameter file
       boolean bl  =  WriteBayesParams.writeParamsFile(new HistogramModel(), dir);
       if (bl == false){
            DisplayText.popupErrorMessage( "Failed to write Bayes.params file.");
            return false;
       }
        
      
       //  write "job.directions" file
        bl  =  JobDirections.writeFromProperties("BayesSubmit", BayesManager.BAYES_PARAMS,HistogramModel.modelName);
        if(bl == false){
            DisplayText.popupErrorMessage(  "Failed to write job.directions file.");
            return false;
         }
    
    
        return true;
   }  
   public static void       tarModelJob(){
        File    bayesRoot       =   DirectoryManager.getBayesDir();
        File    tarFile         =   Run.getTarFile();
        
        File    plotListFile    =   DirectoryManager.getBayesPlotFile();  
        File    mcmcSampleFile  =   DirectoryManager.getMcmcSamplesFile(); 
        File    paramsFile      =   DirectoryManager.getBayesParamsFile();

        Tar.tar(tarFile, bayesRoot, plotListFile,  mcmcSampleFile,  paramsFile);
    }
   public void              doSubmitRun(){
        String url              =   BayesManager.getURL();
        String user             =   BayesManager.getUser();
        File    ziptarFile      =   Run.getZipTarFile();
        String password         =   JServerPasswordDialog.getServerPassword();;

        int    usrOption            =   JServerPasswordDialog.getInstance().getOption();
        if(   usrOption == JServerPasswordDialog.CANCEL ) {return ;}

        jobID                   =   DoCGI.submitJob(url, ziptarFile, user, password);
        validateJobIDandSetSubmissionStatus();
    }
   public void              validateJobIDandSetSubmissionStatus(){
         try {
            int id = Integer.valueOf(jobID);
            if (id != 0)
            {
                Server server = JServer.getInstance().getServer();
                if (server.getQueue().equalsIgnoreCase("None")){
                     status = RUN_STATUS.ACTIVE;
                }
                else
                {
                    status  = RUN_STATUS.SUBMITTED;
                }
            }
            else 
            {
                status =  RUN_STATUS.ERROR;
            }
           
         }catch (NumberFormatException exp){
                status =  RUN_STATUS.ERROR;
                return;
        }
        return ;  
    }
    
   public void              showResults() {
        
         
        if (status == RUN_STATUS.ERROR) {
            Run.removeJobFromServer(this.jobID);
            Run.removeUsedFiles();
            this.jobID = null;
            return;
        }
        if (status == RUN_STATUS.RUN)
        {
            getJob();
            
            File plf                     =  DirectoryManager.getBayesPlotFile();
            ResultsViewer plots          =  ResultsViewer.getInstance();
            
            
            // update current plot
            plots.updateAfterHistogramRun(plf, histogramName);
           
            // set notification message
            plots.setMessage(ResultsViewer.END_HISTOGRAM);
             
        } 
    
   
    }
   public void              getJob() {
            Run.getJobFromServer(this.jobID);
            Run.removeJobFromServer(this.jobID);
            Run.removeUsedFiles();
            jobID       = null; 
    }
    
    public boolean isWorkTheadRunning(){
        return runThread != null && runThread.isAlive();
    }

    public void             submitAndGetModel(){
        tarModelJob();
        Run.doZipTar();

      //  JProgress.StartTread progStart   = null;

        try {
        
       
            doSubmitRun();

            String title = "Generating histogram for "+histogramName;
            String message = "Submittimg analysis to server";
         //   progStart = JProgress.startProgress(title , message);

            while (this.jobID != null ){

                        Thread.sleep(5 * 1000);
                        status = Run.getJobStatusOnServer(this.jobID, this.status.getName());

                      //  String updateStatus = "Histogram generation status = "+status.getName();
                      //  JProgress.updateProgress(progStart.pr, updateStatus);
                        showResults();
             }

       } 
        catch (InterruptedException ex) {
            ex.printStackTrace();
       }
        finally{
         // if (progStart != null){JProgress.closeProgress(progStart.pr);}
        }

  }
    public  RUN_STATUS      getStatus(){return status;}
    public  void            setStatus(RUN_STATUS astatus){
        status = astatus;


        switch(status){
            case    NOT_RUN     :   setJobID(null);
                                    break;

            case    ERROR       :   setJobID(null);
                                    break;
        }
        return;
    }
    public String           getJobID(){return jobID;}
    public void             setJobID(String id){jobID = id;}
      
    public static class HistogramModel implements   applications.model.Model{
        public static final String modelName = "BayesHistograms";
       // public static final String modelName = "DensityEstimationBinnedGiven";
        public void              setPackageParameters(java.io.ObjectInputStream serializationFile){}
        public void              savePackageParameters(java.io.ObjectOutputStream serializationFile){}
        public boolean           isReadyToRun() { return true; }
        public String            getProgramName() { return modelName ;}
        public String            getExtendedProgramName() { return "Histogram Generating Thread" ;}
        public int               getNumberOfAbscissa() {  return 1;}
        public int               getNumberOfDataColumns() {return 1; }
        public int               getTotalNumberOfColumns(){return 1;}
        public int               getNumberOfPriors() {return 0;}
        public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) {
            ResultsViewer instance = ResultsViewer.getInstance();
            
            StringBuilder sb = new StringBuilder();
  
             //next line
            sb.append(IO.pad("Package Parameters", -PADLEN, PADCHAR ));
            sb.append(" = ");
            sb.append(4);
            sb.append("\n");
             
             
              //next line
            sb.append(IO.pad("Column Number", -PADLEN, PADCHAR ));
            sb.append(" = ");
            sb.append(instance.getCurrentHistogramNumber ());
            sb.append("\n");
             
             
             //next line
            sb.append(IO.pad("Mcmc Samples File Name", -PADLEN, PADCHAR ));
            sb.append(" = ");
            sb.append(BayesManager.MCMC_SAMPLE_FILE_NAME);
            sb.append("\n");

             //next line
            sb.append(IO.pad("Plot List File Name", -PADLEN, PADCHAR ));
            sb.append(" = ");
            sb.append(BayesManager.BAYES_PLOT_FILE_NAME);
            sb.append("\n");

               //next line
            sb.append(IO.pad("Output Histogram File Name", -PADLEN, PADCHAR ));
            sb.append(" = ");
            sb.append( instance.getFileNameForPlotting());
            sb.append("\n");
             
            return sb;
        }
        public List<ParameterPrior> getPriors() { return new Vector<ParameterPrior>() ;}
        public String            getConstructorArg(){return null;}
        public boolean           isOutliers(){return false;}
        public String            getInstructions(){return null;}
        public void              reset(){}
        public void              setActive(boolean enabled){}
        public void              setDefaults(){};
        public void              clearPreviousRun(){};
        public void              destroy(){};
        
    }



    class Progress implements Runnable{

        public void run() {

              // waith a little bit
              try {Thread.sleep(50); }
              catch (InterruptedException ex) { ex.printStackTrace(); return;}

            System.out.println(getStatus());
            System.out.println(runThread .isAlive());
             // start progress monitor
             while(isWorkTheadRunning() ){
                    try {
                    // waith a little bit
                    Thread.sleep(60);
                    SwingUtilities.invokeLater(new Update());

                    } catch (InterruptedException ex) {
                     ex.printStackTrace();
                    }
                    finally{
                       SwingUtilities.invokeLater(new Update());
                   }

               }

        }

    }
  
    class Update implements Runnable {
        int max  = 100;
        int inc  =   -1;
       


        public void update(){
            System.out.println("update");
            if (pbar == null){
                        pbar = new ProgressMonitor(null, "Generating histogram for " +histogramName,
                        "Start task",   0,  max );
                        pbar.setMillisToDecideToPopup(10);
                        pbar.setMillisToPopup(10);

            }
            
          
            curProgress  =  curProgress + inc;
            
            if (curProgress == max){ curProgress = 1 ;}
            System.out.println(curProgress);
            pbar.setProgress(curProgress);
            pbar.setNote( getStatus().getName());
        }
        public void close(){
          
            if (pbar != null){
                pbar.setNote( "");
                pbar.setProgress( pbar.getMaximum());
                pbar.close();
            }


        }


        public void run() {
        if (pbar != null && pbar.isCanceled()) {
            canceled = pbar.isCanceled();
            close();
           return;
        }
        else if (isWorkTheadRunning()){
            update();
        }
        else {
            close();
        }
        }
  }
}
