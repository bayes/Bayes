/*
 * Static.java
 *
 * Created on September 6, 2007, 4:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bayes;
import java.io.*;
import java.beans.PropertyChangeSupport;
import utilities.Server;




public class  BayesManager implements Serializable, ApplicationConstants{
    private BayesManager(){};
    private static final Object guiProps                = new Object();
    public static final PropertyChangeSupport pcs       = new PropertyChangeSupport(guiProps);
    public final static String APPLICATION_VERSION      = "4.22";
    public final static String APPLICATION_TITLE        = "Bayes Data-Analysis Toolkit "+APPLICATION_VERSION ;

    
    
    public static void shutDownApplication(){
        System.out.println("Shutting down...");
        bayes.Serialize. serializeCurrenExperiment();
        bayes.JPreferences.persist();
       
    }
    
    //########### property change events   ################//
  
    
            
 
    public static void fireJobStartEvent(){
        pcs.firePropertyChange (BayesManager.JRUN_JOB_START, null,false);
    }
    public static void fireJobEndEvent(boolean showResults){
        pcs.firePropertyChange (BayesManager.JRUN_JOB_END, null, showResults);
    }
    public static void fireJobCanceledEvent(){
        pcs.firePropertyChange (BayesManager.JRUN_JOB_CANCELED, null,"Job was cancelled");
    }
  
    public static void fireBayesModelHasRunEvent(){
        pcs.firePropertyChange (BayesManager.JRUN_MODEL_IS_RUN, null,JRUN_MODEL_IS_RUN);
    }
    public static void fireCompileErrorEvent(String errorMessage){
        pcs.firePropertyChange (COMPILE_ERRROR, null, errorMessage);
    }

    
    public static void fireFidIsLoadedByUser(){
        pcs.firePropertyChange (BayesManager.FID_LOADED_BY_USER, false,true);
    }
    public static void fireFIdIsLoadedByJava(){
        pcs.firePropertyChange (BayesManager.FID_LOADED_BY_JAVA, false,true);
    }
    public static void fireFidUnloaded(){
        pcs.firePropertyChange (BayesManager.FID_UNLOADED, false,true);
    }
    public static void fireFidUnitsChanged(Enums.UNITS oldunits,Enums.UNITS newunits){
        pcs.firePropertyChange (FID_UNITS_ARE_CHANGED, oldunits, newunits);
    }
    public static void fireResonanceIsMarked(Object resonance){
        pcs.firePropertyChange(RESONANCE_MARKED ,null,resonance);
    }
    public static void fireFidReferenceChange(Object resonance){
        pcs.firePropertyChange(RESONANCE_MARKED ,null,resonance);
    }
    
 


    //########### end  ################//
  
    


    public static Server  getServer(){
        return ApplicationPreferences.getCurrentServer();
    }
    public static String  getServerName(){return getServer().getName();}
    public static String  getURL(){
        return getServer().getHttpURL();
    }
    public static String  getAccount(){
         return getServer().getAccount();
    }
    public static String  getUser(){
        String user         =   System.getProperty("user.name");
        try{
              user          =   getServer().getUser();
        }
        catch(Exception e){
                e.printStackTrace();
        }
        finally{
            return user;
        }
         
    }
    



    










}