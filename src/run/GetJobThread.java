/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

/**
 *
 * @author apple
 */
public class GetJobThread implements Runnable {
    Thread runThread                        =   null;
    boolean successfulyRetrieved            =   false;
    String jobId                            =   null;

    GetJobThread (String id){
        jobId  = id;
    }

    public void         run(){
       try{
                   successfulyRetrieved = Run.getJobFromServer(jobId);
       }
       finally{
            reset();
        }
   }
    public  void        reset(){
        runThread                   =   null;
    }
    public  void        execute(){
      this.runThread    =   new Thread(this);
      this.runThread.setPriority(Thread.NORM_PRIORITY);
      this.runThread.start();
   }
    public boolean      isRunning(){
        if (runThread  == null) {return false;}
        else {
            return runThread.isAlive();
        }
    }
}
