/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import utilities.Server;

/**
 *
 * @author apple
 */
public class Properties2Preferences {
    public final static String split                            = ",";
    public final static String results_directories              = "results.directories";
    public final static String results_directory                = "results.directory";
    public final static String server_list                      = "server.list";
    public final static String server_name                      = "server.name";
    public final static String submit_job                       = ".submit_job";
    public final static String MCMC_reps                        = "MCMC.reps";
    public final static String MCMC_sims                        = "MCMC.sims";
    public final static String MCMC_steps                       = "MCMC.steps";


    public static final void convertProperties2Preferences(){
         Properties  properties = loadPropertiesFromDisk();

         File propFile          =   getUserPropertiesFile();
         if (propFile.exists() == false){
             System.out.println(propFile.getPath());
             return;
         }

         try{
             // read servers
             List<Server> servers  =  getAllServersFromProperties(properties);

             // read directories
             List<String> dirs      = getAllDirectoriesFromProperties (properties);

             // read current working directory
             String cwd             = getCurrentWorkingDirFromProperties (properties);

             // read current server
             String server          = getCurrentServerFromProperties (properties);

             // read MCMC sims, reps,
             int   sims             =  getSimsFromProperties(properties);
             int   reps             =  getRepsFromProperties(properties);
             int   steps            =  getStepsFromProperties(properties);

             if (servers.isEmpty() == false){
                     ApplicationPreferences.addServers(servers);
             }
             if (server != null){
                     ApplicationPreferences.setAsCurrentServer(server);
             }
             if (dirs.isEmpty() == false){
                    ApplicationPreferences.addToWorkDirList( dirs );
             }
             if (cwd != null){
                     ApplicationPreferences.setCurrentWorkDir(cwd);
             }
             ApplicationPreferences.setMcmcSims(sims);
             ApplicationPreferences.setMcmcReps(reps);
             ApplicationPreferences.setMcmcSteps(steps);
            

             ApplicationPreferences.persist();
         }
         catch (Exception e){e.printStackTrace();}
         finally{
            if (propFile.exists()){
                propFile.delete();
            }
         }


    }


    public static final File getUserPropertiesFile(){
          File userDir          =   new File ( System.getProperty("user.home"));
          File projectDir       =   new File(userDir, "Bayes"); 
          File resourceDir      =   new File( projectDir, "resources");

          File file             =   new File (resourceDir,"user.properties");

          return file;
    }
    private static Properties loadPropertiesFromDisk(){
        Properties props            = new Properties();
        try {

            File file      =    getUserPropertiesFile();
            if (file.exists() ){
                InputStream in     =   new FileInputStream( file);
                props .load(in);
                in.close();
            }

       }
        catch (IOException ex) { ex.printStackTrace();}
        finally{
            return props;
        }
    }



    // reading from properties

    /*
     * Read servers
     */
    public static Server readServer(String server, Properties pr){
        String str;
        String key;
        int val;
        Server newServer        =  new Server(server);


        key                     =   newServer.getAccounKey();
        str                     =   pr.getProperty(key, newServer.getAccount());
        newServer.setAccount(str);

        key                     =   newServer.getEmailKey();
        str                     =   pr.getProperty(key, newServer.getEmail());
        if (str != null && str.length()>0) { newServer.setEmail(str);}



        key                     =   newServer.getQueueKey();
        str                     =   pr.getProperty(key, newServer.getQueue());
        newServer.setQueue(str);


        key                     =   newServer.getPortKey();
        str                     =   pr.getProperty(key, ""+newServer.getPort());
        val                     =   Integer.valueOf(str);
        newServer.setPort(val);

        key                     =   newServer.getCPUMaxKey();
        str                     =   pr.getProperty(key, ""+newServer.getMaxCpu());
        val                     =   Integer.valueOf(str);
        newServer.setMaxCpu(val);


        key                     =   newServer.getCPUKey();;
        str                     =   pr.getProperty(key, ""+newServer.getCpu());
        val                     =   Integer.valueOf(str);
        newServer.setCpu(val);

        key                     =   newServer.getPasswordKey();
        str                     =   pr.getProperty(key, ""+newServer.isPassword());
        newServer.setIsPassword( new Boolean(str));

        key                     =   newServer.getUserKey();
        str                     =   pr.getProperty(key, newServer.getUser());
        newServer.setUser(str);

        key                     =   newServer.getFortanKey();
        str                     =   pr.getProperty(key, ""+newServer.isPassword());
        newServer.setFortanCompiler( new Boolean(str));

        key                     =   newServer.getCKey();
        str                     =   pr.getProperty(key, ""+newServer.isPassword());
        newServer.setcCompiler( new Boolean(str));



        return newServer;

        }
    public static Collection <String> properties2list( Properties prop, String key ){
         List<String> list = new Vector<String>();

         try{
            String str_list  =  (String)prop.getProperty (key);
            if (str_list != null)  {
                 String [] array =  str_list.split (split);
                  for (String string : array) {
                      list.add( string.trim());
                  }
            }

         }
         catch (Exception e){
            e.printStackTrace();
         }
         finally{
             return list;
         }


    }
    public static Vector<String> getAllServerNamesFromProperties(Properties prop){
         Vector <String> server_names    = (Vector <String>)
                                     properties2list(prop, BayesManager.server_list);
        return server_names;
    }
    public static Vector<Server> getAllServersFromProperties( Properties  props){
         Vector <String> server_names    = getAllServerNamesFromProperties( props);

         Vector <Server> servers  = new  Vector <Server>();
            for (String name : server_names) {
            Server srv = readServer(name,  props);
            servers.add(srv);

         }
        return servers;
    }

    /*
     * Read Directories
     */
   public static List<String> getAllDirectoriesFromProperties (Properties  props){
        Vector <String> dirs = new Vector<String>();

        try{
            dirs = ( Vector <String>) properties2list (props, results_directories);
        }
        catch (Exception e){e.printStackTrace();}
        finally{
             return dirs;
        }



    }

   /*
    * Read  Current Working Directory
    */
    public static String getCurrentWorkingDirFromProperties (Properties  props){
        String cwd = null;
        try{
             cwd  =  (String) props.getProperty(results_directory, null);
        }catch(Exception e){e.printStackTrace();}
        finally {return cwd;}

    }

    /*
    * Read  Current Server
    */
   
    public static String getCurrentServerFromProperties(Properties  props){
        String server = null;
        try{
            server  =  (String) props.getProperty((server_name), null);
        }catch(Exception e){e.printStackTrace();}
        finally {return server;}

}

    public static int     getSimsFromProperties(Properties  props){
        String valStr   =   props.get(MCMC_sims).toString();
        int  val        =   -1;
        try{
         val        =    Integer.valueOf(valStr);
        }

        catch(NumberFormatException exp){
            exp.printStackTrace();
            val = 50;
        }

        return val;

    }
    public static int     getRepsFromProperties(Properties  props){
        String valStr   =   props.get(MCMC_reps).toString();
        int  val        =   -1;
        try{
         val        =    Integer.valueOf(valStr);
        }

        catch(NumberFormatException exp){
            exp.printStackTrace();
             val = 50;
        }

        return val;

    }
    public static int     getStepsFromProperties(Properties  props){
        String valStr   =   props.get(MCMC_steps).toString();
        int  val        =   -1;
        try{
         val            =    Integer.valueOf(valStr);
        }

        catch(NumberFormatException exp){
            exp.printStackTrace();
             val = 50;
        }

        return val;
    }
}
