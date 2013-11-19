/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import fid.FidViewerPreferences;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.ToolTipManager;
import utilities.PrefObj;
import utilities.Screenshot;
import utilities.Server;

/**
 *
 * @author apple
 */
public class ApplicationPreferences {
    private static final String LOOK_AND_FEEL_NAME_KEY                  = "Look and Feel Name";
    public static final String LOOK_AND_FEEL_NAME_NIMBUS                = "Nimbus";

    private static final String BAYES_HOME_DIR_KEY                      =   "BAYES_HOME_DIR_KEY";
    private static final String PROJECT_NAME_DEF                        = "Bayes";
    private static       File PROJECT_DIR                               =  null;
    
    private static final String CONECTION_TIMEOUT_MS_KEY                   =   "CONECTION_TIMEOUT_KEY";
    private static final int CONECTION_TIMEOUT_SEC_DEF                      =   1;

    private static final String READ_TIMEOUT_SEC_KEY                         =   "COONECTION_TIMEOUT_KEY";
    private static final int READ_TIMEOUT_SEC_DEF                            =   20;

    private static final String SUBMIT_JOB_BUFFER_SIZE_MB_KEY               =   "SUBMIT_JOB_BUFFER_SIZE_MB";
    private static final int  SUBMIT_JOB_BUFFER_SIZE_MB_DEF                =   32;

    private static final String DOWNLOAD_JOB_BUFFER_SIZE_MB_KEY            =   "DOWNLOAD_JOB_BUFFER_SIZE_MB_KEY";
    private static final int  DOWNLOAD_JOB_BUFFER_SIZE_MB_DEF                =   16;

    private static final String BUILD_MODEL_TIMEOUT_MILS_KEY              =   "BUILD_MODEL_TIMEOUT_KEY";
    private static final int BUILD_MODEL_TIMEOUT_MILS_DEF                 =   500;



    private static final String ENABLE_TOOLTIPS_KEY                      =   "ENABLE_TOOLTIPS_KEY";
    private static final boolean ENABLE_TOOLTIPS_DEF                     =   true;

    private static final String  TOOLTIP_UPTIME_SEC_KEY                  =   "TOOLTIP_UPTIME_KEY";
    private static final int TOOLTIP_UPTIME_SEC_DEF                      =  20;


    private static final String  TOOLTIP_FONT_SIZE_KEY                  =   "TOOLTIP_FONT_SIZE_KEY";
    private static final int TOOLTIP_FONT_SIZE_DEF                      =  14;

    private static final String  REDIRECT_JAVA_STREAMS_KEY              =   "REDIRECT_JAVA_STREAMS_KEY ";
    private static final boolean REDIRECT_JAVA_STREAMS_DEF              =  false;

    private static final String  DISPLAY_HOSTNAME_KEY                       =   "DISPLAY_HOST_NAME_KEY ";
    private static final boolean DISPLAY_HOSTNAME_DEF                       =   false;
    
    private static final String  MONITOR_SUBMMITED_JOBS_LIVE_KEY            =   "REDIRECT_JAVA_STREAMS_KEY ";
    private static final boolean MONITOR_SUBMMITED_JOBS_LIVE_DEF            =  false;

    private static final String CLEAN_JOB_ON_SERVER_KEY                     =   "CLEAN_JOB_ON_SERVER_KEY";
    private static final boolean CLEAN_JOB_ON_SERVER_DEF                    =  true;


    private static final String  MCMC_SIMS_KEY                              = "MCMC.sims";
    private static final int     MCMC_SIMS_DEF                              =  50;

    private static final String  MCMC_REPS_KEY                              = "MCMC.reps";
    private static final int     MCMC_REPS_DEF                              =  50;

    private static final String  MCMC_STEPS_KEY                             = "MCMC.steps";
    private static final int     MCMC_STEPS_DEF                              =  50;
    
    private static final String   CURRENT_WORK_DIR_KEY                       =  "Current Working directory";
    private static final String   CURRENT_WORK_DIR_DEFAULT                   =  "exp1";

    private static final String   WORK_DIR_LIST_KEY                          =  "List of working directories";
    private static  List<String> WORK_DIR_LIST                              =   new ArrayList<String> ();

    private static final String  SERVER_LIST_KEY                            =  "List of servers";
    private static  List<Server> SERVER_LIST                                =   new ArrayList<Server> ();

    public  static  Server       DEFAULT_SERVER                             =    Server.getDummyServer();

    private static final String  CURRENT_SERVER_KEY                             =  "Currently set server";
    private static Server  CURRENT_SERVER                                       =  DEFAULT_SERVER;

    private static final String   SCREENSHOT_FORMAT_KEY                     = "DEFAULT_SCREENSHOT_FORMAT_KEY ";
    private static final String   SCREENSHOT_FORMAT_DEF                     =  "png";
    
    private static final String   FILE_CHOOSER_DIR_KEY                       =  "GENERIC FILE_CHOOSER_DIR_KEY";
    private static final String   FILE_CHOOSER_DIR_DEF                       =   null;

    /*
     * NON-PERSITENT PROPERTIES
     */
    private static final boolean DELETE_COMPLETED_JOB_FROM_SERVER_DEF        =  true;
    private static boolean DELETE_COMPLETED_JOB_FROM_SERVER                  =  DELETE_COMPLETED_JOB_FROM_SERVER_DEF ;
    
    private static final boolean DELETE_SUBMITTED_JOB_FROM_CLIENT_DEF        =  true;
    private static boolean DELETE_SUBMITTED_JOB_FROM_CLIENT                  =  DELETE_SUBMITTED_JOB_FROM_CLIENT_DEF;
    
    private static Preferences prefs                                        =   Preferences.userNodeForPackage(ApplicationPreferences.class);

    public static  void resetPreferences(){
        try {
            if (prefs != null) {
                prefs.removeNode();
            }
           
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
             resetNonPersistentProperties();
        }
            catch (Exception e){
            e.printStackTrace();
        }
        finally{
        }
    }
    
    public static void resetNonPersistentProperties(){
        setDeleteCompletedJobFromServer(DELETE_COMPLETED_JOB_FROM_SERVER_DEF );
        setDeleteSubmittedJobFromClient(DELETE_SUBMITTED_JOB_FROM_CLIENT_DEF );
    }


  
    public static File getBayesHomeDir(){
         if (PROJECT_DIR == null){
            String defpath      =   getDefaultBayesHomeDir().getPath();
            String path         =   prefs.get(BAYES_HOME_DIR_KEY, defpath  ).toString();
            PROJECT_DIR        = new File( path );
         }

         return  PROJECT_DIR;
    }
    public static File getDefaultBayesHomeDir(){
            String home        =   System.getProperty("user.home");
            File file          =   new File (home,PROJECT_NAME_DEF  );
         return  file;
    }
    public static void setBayesHomeDir(File file){
         if (file != null && file.exists() && file.isDirectory()){
             PROJECT_DIR           =   file ;
             prefs.put(BAYES_HOME_DIR_KEY,PROJECT_DIR.getAbsolutePath()   );
         }
         else{
             PROJECT_DIR           =  null; ;
             prefs.put(BAYES_HOME_DIR_KEY,  null );
         }
    }

   public static String getAbsolutePath(){
        return  prefs.absolutePath();
    }
    public static void persist(){
       try{
           saveWorkDirs();
           prefs.flush();
       }
       catch (Exception e){
        e.printStackTrace();
       }
    }

    /**********convenience methods starts ********/
    public static void refreshAllPreferences(){
         refreshEnableTooltips();
         refreshTooltipTime();
         refreshScreenShotFormat();
         refreshFidPreferences();
    }
  
    public static void  refreshEnableTooltips(){
        ToolTipManager.sharedInstance().setEnabled(isTooltipsEnabled());
    }
    public static void  refreshTooltipTime(){
        int val                = getTooltipUptimeInSeconds();
        ToolTipManager.sharedInstance().setDismissDelay(1000 * val);
    }
    public static void  refreshTooltipFont(){
        java.awt.Font  tooltipFont    =   new java.awt.Font("Dialog", 0,getTooltipFontSize() );
        javax.swing.UIManager.put( "ToolTip.font", new javax.swing.plaf.FontUIResource(tooltipFont) );
    }
     public static void  refreshScreenShotFormat(){
        String format   =     ApplicationPreferences.getScreenShotFormat();
        Screenshot.setDefaultType(format);
    }
    public static void  refreshFidPreferences(){
         FidViewerPreferences.refreshCursorValueFormat();
    }
 




    public static String getLookAndFeel(){
      return  prefs.get(LOOK_AND_FEEL_NAME_KEY  , LOOK_AND_FEEL_NAME_NIMBUS ).toString();
    }
    public static void setLookAndFeel (String val){
       prefs.put(LOOK_AND_FEEL_NAME_KEY, val  );
    }

    
    
    public static int getCGIConnectionTimeOutInSec(){
      return  prefs.getInt( CONECTION_TIMEOUT_MS_KEY, CONECTION_TIMEOUT_SEC_DEF  );
    }
    public static void setCGIConnectionTimeOutInSec(int val){
      prefs.putInt( CONECTION_TIMEOUT_MS_KEY , val);
    }
    public static int getCGIReadTimeOutInSec(){
      return  prefs.getInt(READ_TIMEOUT_SEC_KEY,READ_TIMEOUT_SEC_DEF  );
    }
    public static void setCGIReadTimeOutInSec(int val){
      prefs.putInt( READ_TIMEOUT_SEC_KEY , val);
    }

    public static int getSubmitJobBufferSizeMB(){
      return  prefs.getInt( SUBMIT_JOB_BUFFER_SIZE_MB_KEY , SUBMIT_JOB_BUFFER_SIZE_MB_DEF );
    }
    public static void setSubmitJobBufferSizeMB(int val){
      prefs.putInt(SUBMIT_JOB_BUFFER_SIZE_MB_KEY , val);
    }

    public static int getDownloadJobBufferSizeMB(){
      return  prefs.getInt( DOWNLOAD_JOB_BUFFER_SIZE_MB_KEY , DOWNLOAD_JOB_BUFFER_SIZE_MB_DEF );
    }
    public static void setDownloadJobBufferSizeMB(int val){
      prefs.putInt(DOWNLOAD_JOB_BUFFER_SIZE_MB_KEY , val);
    }

    public static String getScreenShotFormat(){
      return  prefs.get( SCREENSHOT_FORMAT_KEY , SCREENSHOT_FORMAT_DEF );
    }
    public static void setScreenShotFormat(String val){
      prefs.put( SCREENSHOT_FORMAT_KEY , val);
    }
    
    
    public static int getBuildModelTimeOutMils(){
      return  prefs.getInt(BUILD_MODEL_TIMEOUT_MILS_KEY , BUILD_MODEL_TIMEOUT_MILS_DEF  );
    }
    public static void setBuildModelTimeOutMils(int val){
      prefs.putInt(BUILD_MODEL_TIMEOUT_MILS_KEY , val);
    }

    public static boolean isTooltipsEnabled(){
      return  prefs.getBoolean(ENABLE_TOOLTIPS_KEY ,ENABLE_TOOLTIPS_DEF  );
    }
    public static void setTooltipsEnabled(Boolean val){
      prefs.putBoolean(ENABLE_TOOLTIPS_KEY , val);
    }


    public static int getTooltipUptimeInSeconds(){
      return  prefs.getInt(TOOLTIP_UPTIME_SEC_KEY ,TOOLTIP_UPTIME_SEC_DEF  );
    }
    public static void setTooltipUptimeInSeconds(int val){
      prefs.putInt(TOOLTIP_UPTIME_SEC_KEY , val);
    }

    public static int getTooltipFontSize(){
      return  prefs.getInt( TOOLTIP_FONT_SIZE_KEY, TOOLTIP_FONT_SIZE_DEF  );
    }
    public static void setTooltipFontSize(int val){
      prefs.putInt( TOOLTIP_FONT_SIZE_KEY, val);
    }


    public static boolean isRedirectJavaStream(){
      return  prefs.getBoolean( REDIRECT_JAVA_STREAMS_KEY   , REDIRECT_JAVA_STREAMS_DEF  );
    }
    public static void setRedirectJavaStream(boolean val){
      prefs.putBoolean( REDIRECT_JAVA_STREAMS_KEY , val);
    }

    public static boolean isMonitorSubmittedJobsLive(){
      return  prefs.getBoolean(  MONITOR_SUBMMITED_JOBS_LIVE_KEY   ,  MONITOR_SUBMMITED_JOBS_LIVE_DEF  );
    }
    public static void setMonitorSubmittedJobsLive(boolean val){
      prefs.putBoolean( MONITOR_SUBMMITED_JOBS_LIVE_KEY , val);
    }
        
    public static boolean isDisplayHostanme(){
      return  prefs.getBoolean(  DISPLAY_HOSTNAME_KEY   ,  DISPLAY_HOSTNAME_DEF  );
    }
    public static void setDisplayHostname(boolean val){
      prefs.putBoolean(  DISPLAY_HOSTNAME_KEY , val);
    }
    
    /*
     public static boolean isCleanJobOnServer(){
      return  prefs.getBoolean( CLEAN_JOB_ON_SERVER_KEY ,  CLEAN_JOB_ON_SERVER_DEF   );
    }
    public static void setCleanJobOnServer(boolean val){
      prefs.putBoolean( CLEAN_JOB_ON_SERVER_KEY , val);
    }
     * 
     */


    public static int getMcmcSims(){
      return  prefs.getInt(MCMC_SIMS_KEY ,MCMC_SIMS_DEF  );
    }
    public static void setMcmcSims(int val){
      prefs.putInt(MCMC_SIMS_KEY , val);
    }

    public static int getMcmcReps(){
      return  prefs.getInt(MCMC_REPS_KEY ,MCMC_REPS_DEF  );
    }
    public static void setMcmcReps(int val){
      prefs.putInt(MCMC_REPS_KEY , val);
    }

    public static int getMcmcSteps(){
      return  prefs.getInt(MCMC_STEPS_KEY ,MCMC_STEPS_DEF  );
    }
    public static void setMcmcSteps(int val){
      prefs.putInt(MCMC_STEPS_KEY , val);
    }
  

    public static List<String> getWorkDirs(){
     if ( WORK_DIR_LIST.isEmpty()){
        try{
            WORK_DIR_LIST =  (List <String>) PrefObj.getObject(prefs, WORK_DIR_LIST_KEY);
        }
        catch(Exception e){e.printStackTrace();}
        finally{
          if (WORK_DIR_LIST.isEmpty()){
                WORK_DIR_LIST.add(getCurrentWorkDir());
          }
         
         }

     }
        return  WORK_DIR_LIST;
      
    }
    public static void saveWorkDirs(){

      try{
        PrefObj.putObject(prefs,  WORK_DIR_LIST_KEY, WORK_DIR_LIST);
        prefs.flush();
      }
      catch(Exception e){e.printStackTrace();}
      finally{
      }

    }
    public static boolean addToWorkDirList(String dir){
        boolean out             =   false;
        List<String> dirs       =  getWorkDirs();

        if (isConflictingWorkDirName(dir) == false){
            dirs.add(dir);
            out = true;
        }
        
        saveWorkDirs();
        return out;
    }
    public static boolean addToWorkDirList( List <String> newdirs){
        boolean out             =   false;
        List<String> dirs       =  getWorkDirs();

        for (String dir : newdirs) {
           if (isConflictingWorkDirName(dir) == false){
                dirs.add(dir);
                out = true;
            }
        }


        saveWorkDirs();
        return out;
    }
    public static void removeFromWorkDirList(String dir){
        List<String> dirs =  getWorkDirs();
        dirs.remove(dir);
        saveWorkDirs();
    }
    public static boolean isWorkDir(String dir){
        List<String> dirs =  getWorkDirs();
        return dirs.contains(dir);
    }
    public static boolean isConflictingWorkDirName(String adir){
        boolean out             =   false;
        try{
            List<String> dirs   =  getWorkDirs();
            for (String dir : dirs) {
                if (dir.equalsIgnoreCase(adir)){
                    out  = true;
                    break;
                }
            }
        }
        finally {
            return out;
        }

    }

    public static boolean isCurrentWorkDir(String dir){
        return getCurrentWorkDir().equals(dir);
    }
    public static String getCurrentWorkDir(){
         return  prefs.get(CURRENT_WORK_DIR_KEY ,CURRENT_WORK_DIR_DEFAULT );
    }
    public static void resetCurrentWorkDir(){
        prefs.put(CURRENT_WORK_DIR_KEY ,CURRENT_WORK_DIR_DEFAULT );
    }
    public static void setCurrentWorkDir(String dir){
        prefs.put(CURRENT_WORK_DIR_KEY  , dir);

    }

 
    public static void setDefaultServer(  String [] args ){
        boolean isServer            =       args != null && args.length>1;
        int numberOfArguments       =      args.length;
        if (isServer){


             try{
                 // set name
                if (numberOfArguments  >0) {
                    String name         =    args[0];
                    DEFAULT_SERVER.setName(name);
                 }


                 // set isPassword
                if (numberOfArguments  >1) {
                    Boolean isPassword  =   new Boolean(args[1]);
                    DEFAULT_SERVER.setIsPassword(isPassword);
                }


                // set maxCpu/cpu
                if (numberOfArguments  >2) {
                    int cpu             =   Integer.parseInt(args[2]);
                    DEFAULT_SERVER.setMaxCpu(cpu);
                    DEFAULT_SERVER.setCpu(cpu);
                }


                //set account
                if (numberOfArguments  >3) {
                    String account      =   args[3];
                    DEFAULT_SERVER.setAccount(account);
                }


                // set port
                if (numberOfArguments  >4) {
                    int port             =   Integer.parseInt(args[4]);
                    DEFAULT_SERVER.setPort( port);
                }


                // set Fortran
                 if (numberOfArguments  >5) {
                    Boolean isFCompiler  =   new Boolean(args[5]);
                    DEFAULT_SERVER.setFortanCompiler(isFCompiler );
                }



                // set C compiler
                if (numberOfArguments  >6) {
                     Boolean isCCompiler  =   new Boolean(args[6]);
                     DEFAULT_SERVER.setcCompiler(isCCompiler);
                }


                // set queue
                if (numberOfArguments  >7) {
                     String queue        =   args[7];
                     DEFAULT_SERVER.setQueue(queue);
                }

            }
            catch (Exception exp){exp.printStackTrace(); }



        }

    }
    public static List<Server> getServers(){
     if ( SERVER_LIST.isEmpty()){
        try{
            List <Server> srvs =  (List <Server>) PrefObj.getObject(prefs, SERVER_LIST_KEY);
            if (srvs  != null){
                 SERVER_LIST.addAll(srvs);
            }

        }
        catch(Exception e){e.printStackTrace();}

        Server curServer        =   getCurrentServer();
        if (!SERVER_LIST.contains(curServer)){
            SERVER_LIST.add(curServer);
        }
     }
        
     
        return  SERVER_LIST;

    }
    public static boolean isConflictingServer(Server server){
        boolean out                 =   false;
        List<Server> servers        =   getServers();
        try{
            String url              =   server.getHttpURL();
            for (Server curserver : servers) {
                String curURL = curserver.getHttpURL();
                if (url.equalsIgnoreCase(curURL)){
                    out  = true;
                    break;
                }
            }
        }
        finally {
            return out;
        }

    }
    public static void saveServers(){

      try{
        SERVER_LIST = removeDummyServer(SERVER_LIST);
        
        PrefObj.putObject(prefs,  SERVER_LIST_KEY, SERVER_LIST);
        prefs.flush();
      }
      catch(Exception e){e.printStackTrace();}

    }
    public static void saveAsServers(List<Server> servers){
        SERVER_LIST.clear();
        SERVER_LIST.addAll(servers);
        saveServers();
    }
    public static void addServers(List<Server> servers){
        for (Server server : servers) {
            addServer(server);
        }
         saveServers();
    }
    public static boolean addServer(Server server){
        boolean out             =   false;
        SERVER_LIST             =   getServers();

       if (isConflictingServer(server) == false){
            SERVER_LIST .add(server);
            out = true;
        }


        saveServers();
        return out;
    }
    
    public static List<Server> removeDummyServer(List<Server> servers){
        Server dummyServer         =   Server.getDummyServer();
        if (servers.size() >1 &&  servers.contains(dummyServer)){
             servers.remove( dummyServer);
        }
        
        return servers;

    }


    public static Server getCurrentServer(){
        try{
            Server server  =  (Server) PrefObj.getObject(prefs,CURRENT_SERVER_KEY  );
            if (server != null){
                CURRENT_SERVER =  server;
            }
        }
        catch(Exception e){e.printStackTrace();}
        finally{
            return CURRENT_SERVER;
         }


    }
    public static boolean setAsCurrentServer(String  serverName){
        boolean serverSet             =   false;
         List<Server> servers   = ApplicationPreferences.getServers();
         for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(serverName)){
                CURRENT_SERVER =  server;
                saveCurrentServer();

                serverSet = true;
                break;
            }
        }

         return serverSet;
    }
    public static void saveAsCurrentServer(Server server){
        CURRENT_SERVER =  server;
        saveCurrentServer();

    }
    public static void saveCurrentServer(){
      try{
         if (CURRENT_SERVER != null){
            PrefObj.putObject(prefs,  CURRENT_SERVER_KEY , CURRENT_SERVER);
         }
        
      }
      catch(Exception e){e.printStackTrace();}

    }
    public static void removeServer(Server server){
        SERVER_LIST.remove(server);
        saveServers();
    }


    public static String getFileChooserDir(){
         return  prefs.get( FILE_CHOOSER_DIR_KEY , FILE_CHOOSER_DIR_DEF );
    }
    public static void setFileChooserDir(String dir){
        prefs.put(FILE_CHOOSER_DIR_KEY  , dir);

    }

    
    /*
     * Non Persisten setters and getters start
     * 
     */
    public static boolean isDeleteCompletedJobFromServer() {
        return DELETE_COMPLETED_JOB_FROM_SERVER;
    }
    public static void setDeleteCompletedJobFromServer(boolean delete) {
        DELETE_COMPLETED_JOB_FROM_SERVER = delete;
    }
    public static boolean isDeleteSubmittedJobFromCLient() {
        return  DELETE_SUBMITTED_JOB_FROM_CLIENT;
    }
    public static void setDeleteSubmittedJobFromClient(boolean delete) {
         DELETE_SUBMITTED_JOB_FROM_CLIENT = delete;
    }

     /*
     * Non Persisten setters and getters stop
     * 
     */
}
