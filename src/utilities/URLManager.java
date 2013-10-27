/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import bayes.DoCGI;
import bayes.InstallationInfo;
import java.util.List;
import interfacebeans.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author apple
 */
public class URLManager {
    public static final String BAYES_SOURCE_SERVER         = "bayes.wustl.edu";
    public static final String INSTALLATION_KIT_URL_DIR    = "ServerSoftware";
    public static final String INSTALLATION_KIT_KEY        =  "LinuxPc64";
    public static final String INSTALLATION_FILE           =  "/Bayes/installation.inf";
    public static final String DISTRIBUTION_LISTING_FILENAME = "DistributionKitListing";
   


    // MASTER SERVER
    public static String getMasterServerURL(){
         String url          =   "http://"+ BAYES_SOURCE_SERVER+ "/";
         return url;
    }
    public static String getMasterServerInstallKitURL(){
         String url          =   getMasterServerURL()+INSTALLATION_KIT_URL_DIR+ "/" ;
         return url;
    }
    public static String getInstallationKitURL(String installationKitName){
         String src          =   getMasterServerInstallKitURL()+installationKitName ;
         return src ;
    }
    public static String getInstallationKitListingURL(){
         return getMasterServerInstallKitURL() + DISTRIBUTION_LISTING_FILENAME;
    }
    public static List<String> getInstallationList() throws IOException {
         List<String> files     =    new ArrayList<String>();
         String url             =    getInstallationKitListingURL();
         File tmp               =    new File ("tmp");

         try{
             boolean isDone     =     DoCGI.downloadFile(url, tmp, null, null);
             Scanner scanner    =    new Scanner(tmp);
             while(scanner.hasNextLine()){
               files.add(scanner.nextLine().trim());
             }
         }

         finally { tmp.delete();}



         return files ;
    }
    public static String getSoftwareVersionForMasterServer(){

        String sfVersion        =   null;
        try{
        
            List<String> files      =   getInstallationList();
            String tmp              =   null;
            for (String string : files) {
                if (string.contains(INSTALLATION_KIT_KEY)){
                    tmp = string;
                    break;
                 }
            }
                
                
           if (tmp == null) {return null;}

            String spr                  =   ".";
            int start                   =   tmp.indexOf(spr );
            int floatPoint              =   tmp.indexOf(spr, start+1 );
            int end                     =   tmp.indexOf(spr, floatPoint+1);


            if (start < 0 || end <= start){sfVersion  = null;}
            else{sfVersion         = tmp.substring(start+1, end);}
        
        }
        catch (Exception exp) {
            exp.printStackTrace();
           
        }
        finally {
            return sfVersion;
        }


    }







    public static String getSoftwareVersionForServer(Server server){
        InstallationInfo ii             =    getInstallationInfoForServer (server);
        
        if (ii.isLoaded()){  return ii.getInstallationVersion();}
        else {return null;}

    }
    public static InstallationInfo getInstallationInfoForServer(Server server){
        File file                       =   null;
        InstallationInfo ii             =   new  InstallationInfo();

        try{
            file                        =   downloadInstallationFile(server);
            if (file == null || file.exists() == false) {return ii;}
            else{    ii =   InstallationInfo.loadFromFile(file); }


        }
        catch(Exception exp){exp.printStackTrace();}
        finally{
            if (file!= null && file.exists() == true) { file.delete();;}
           
        }


         return  ii;

    }

    


    public static String getInstallationInfoAsString (Server server)throws Exception {
        File file                       =   null;
        String str                      =   null;

        try{
            file                        =   downloadInstallationFile(server);
            if (file == null || file.exists() == false) {return null;}
            else{   str =  IO.readFileToString(file); }


        }
        finally{
            if (file!= null || file.exists() == true) { file.delete();;}

        }


         return  str;

    }
    
    public static void displayServerInfo(Server server){
       displayServerInfo(server,null);

    }
    public static void displayServerInfo(Server server, String additionalInfo){
       if (server  == null) {return;}
       StringBuilder sb = new StringBuilder();


       try{
          String info = URLManager.getInstallationInfoAsString(server);

          if (info == null){
            throw new Exception ("");
          }
          else{
                 sb.append(info);
                 if (additionalInfo != null){
                    sb.append(additionalInfo);
                 }
                 
                 Viewer.display(sb.toString(), server.getName());
          }



       }
       catch(Exception exp){
           exp.printStackTrace();
           String expMsg = exp.getMessage();
           String error  = (expMsg == null)? "":expMsg+".";
           String message = String.format("Failed to retrieve installation\n" +
                                          "information for server\n" +
                                          "%s.\n" +
                                          "%s", server.getName(),error) ;
          DisplayText.popupErrorMessage(message);
          return;}
        finally{

        }



    }
    public static File downloadInstallationFile(Server server)throws Exception{
        String url                      =   server.getHttpURL();
        File file                       =   new File("bayesServerInstalInfo.tmp");
        String remoteFile               =   url  +  INSTALLATION_FILE;


        DoCGI.downloadFile(remoteFile , file, null, null);

        return  file;

    }
    public static String checkForServerSofrwareUpdates(List <Server> servers){
        String masterServerVersion  =  getSoftwareVersionForMasterServer();
        String out                  =  "";
        java.text.DateFormat df     = new java.text.SimpleDateFormat("HH:mm:ss MM/dd/yyyy/");
        String date                 =  df.format(new java.util.Date()).toString();
        
        // Putup general header
        out                         = out + "Time:Date = ";
        out                         = out + date;
        out                         = out +"\n";
        
        // if master server is down, can not be reached or misconfigured
        if (masterServerVersion == null){
                out        = out +"\n";
                out        = out + "Can not check for software update";
                out        = out +"\n";
                out        = out + "Software developer server is down,unreachable or misconfigured.";
                out        = out +"\n";
        
        }
        else{
            out        = out + "The latest software version is "+   masterServerVersion + ".";
            out        = out +"\n";   
            out        = out +"\n"; 
          
                       
            for (Server server : servers) {
                String tmp = checkForServerSofrwareUpdates(server,masterServerVersion);

                out        = out + server.toString();
                out        = out +"\n";
                out        = out + tmp;
                out        = out +"\n";
            }
        }

       
        return out;

    }
    public static String checkForServerSofrwareUpdates(Server server,  String masterServerVersion ){
        String message                  =  null;
        String currentServerVersion     =  getSoftwareVersionForServer(server);


        if ( currentServerVersion == null) {
                String servername       =   server.getName();
                 message                =   String.format(  
                                            "Can not check software update information "
                                            + "for server %s.",
                                            servername) ;
                message                 = message +"\n";
                message                 = message + "Server is either down, unreachable or misconfigured";
                message                 = message +"\n";
             
            return message ;
        }
        else if (masterServerVersion.equals(currentServerVersion)){
              message   = String.format( "Software is up to date\n" ,server) ;
              return(message);
        }
        else {
                message   = String.format(  "A new software, version %s, is available for installation.\n",
                                            masterServerVersion) ;
               return(message);
        }




    }


    public static void main (String args []){

        System.out.println(getSoftwareVersionForMasterServer());
    }
}
