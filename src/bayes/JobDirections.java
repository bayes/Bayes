/*
 * JobDirections.java
 *
 * Created on September 7, 2007, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bayes;
import applications.model.Model;
import java.io.*;
import utilities.Server;

public class JobDirections {
    public final static String BAYES_SUBMIT                 = "BayesSubmit";
    public final static String BAYES_SUBMIT_COMPILE         = "BayesSubmit_Compile";
    public final static String BAYES_SUBMIT_IMAGE           = "BayesSubmit_Image";
    public final static String BAYES_SUBMIT_ENTER_ASCII     = "BayesSubmit_EnterAscii";
    private JobDirections () {}
    public static boolean  writeFromProperties(String script){
       return  writeFromProperties(script, BayesManager.BAYES_PARAMS);
    }
    public static boolean  writeFromProperties(String script, String bayesParams ){
         Model model    = PackageManager.getCurrentApplication();
         String program = model.getProgramName();
            
        return  writeFromProperties(script, bayesParams,  program);
    }
    public static boolean  writeFromProperties(String script, String bayesParams, String packageName  ){
        File workDir            =   DirectoryManager.getBayesOtherAnalysisDir();
        return writeFromProperties(script,bayesParams,  packageName,  workDir);
    }
    public static boolean  writeFromProperties(String script, String bayesParams, String program, File  dir){
        BufferedWriter out          = null;
        FileWriter fr               = null;
        
        File     jobdir             = new File(  DirectoryManager.getBayesDir(), "job.directions");
        Server  server              = ApplicationPreferences.getCurrentServer();
        String   directionsfile     = jobdir.getPath();
                
        
        try {     
            File file               = new File (directionsfile);
            fr                      = new FileWriter(file);
            out                     = new BufferedWriter(fr);
            
            // line 1 - account
            out.write(server.getAccount());
            out.write("\n");
            
            // line 2 - exp
            out.write( ApplicationPreferences.getCurrentWorkDir());
            out.write("\n");
            
            // line 3 - BayesOtherAnalysis
            out.write(dir.getName());
            out.write("\n");
            
            // line 4  - packageNAme
            out.write(program);
            out.write("\n");
            
            // line 5  - cpu
            int ncpu = server.getCpu();
            String cpu      =   ncpu + "";
            out.write(cpu);
            out.write("\n");
            
            // line 6  - queue
            out.write(server.getQueue());
            out.write("\n");
            
            // line 7  - script
            out.write(script);
            out.write("\n");
            
            // line 8  - user
            out.write(server.getUser());
            out.write("\n");
            
            // line 9 - email
            String email = server.getEmail();
            if (email == null || email.length() < 4 ){
                email = Server.NOEMAIL;
            }
            out.write(email);
            out.write("\n");
            
            //line 10 - BayesParams file name
            out.write(bayesParams);
            out.write("\n");
            out.close(); 
         } catch (IOException ex) {
            ex.printStackTrace();
           
            return false;
            
        } 
        
        finally{
                try{
                    out.close();
                    fr.close();
                }catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
        } 
            
        return true;
    }

     public static boolean  writeFromProperties(String script, File dir){
       Model model    = PackageManager.getCurrentApplication();
       String program = "BayesEnterAscii";
       if (model != null){
            program = model.getProgramName();
       }


       return  writeFromProperties(script, BayesManager.BAYES_PARAMS, program, dir);
    }

}   
