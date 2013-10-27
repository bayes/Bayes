/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import interfacebeans.JServer;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import bayes.DirectoryManager;
import utilities.DisplayText;
import utilities.Download;
import utilities.FileCompress;
import utilities.Server;
import utilities.Tar;

/**
 *
 * @author apple
 */
public class DownloadSampleFiles  implements Callable <Boolean>{

    private Server server;
    public DownloadSampleFiles(Server aserver){ server = aserver;}
        
    public  Boolean call (){
      String url                    =   getServer().getTestDataURL();
      File dir                      =   DirectoryManager.getBayesDir();
      File gz                       =   new File(dir,"test.tar.gz");
      File tar                      =   new File(dir,"test.tar");
        
       try {
            
            Download download       =    new Download(url, gz);
            Future<Boolean> future  =   Executors.newSingleThreadExecutor().submit( download);
           // Thread.

            //DoCGI.downloadFile(url, gz, null, null);
            boolean isDownloaded = future.get();


            if (isDownloaded) {
                 FileCompress.unGzip(gz, tar);
                 Tar.untar(tar);


                File dst      =  DirectoryManager. getClientTestDatalDir() ;
               // DisplayText.popupMessage(   "Test data was downloaded to \n"+ dst.getPath() + " directory.");
                                   


                String str   = "<html><p style=\"margin: 6px;\"><font size=\"5\">" +
                        "Test data was downloaded to<br>"+
                  "<font color=\"blue\" size = \"+1\"><bold>"+
                         dst.getPath() +
                  "</font></bold><br>" +

                        " directory."+
                        "</font></p><html>";
                  DisplayText.popupMessage(str);
                return true;

            }
            else if (download.canceled == true){ 
                return false;
            }
            else{throw new Exception();}
           

       }

       catch(Exception ex){
             String error = String.format("Failed to retrieve test data repository.",getServer().getTestDataURL());
             DisplayText.popupErrorMessage( error);
            return false;
       }
       finally{
            if (tar.exists()){tar.delete();}
            if (gz.exists()){gz.delete();}

       }


    }




    public static void main(String [] args){
      Server server = JServer.getInstance().getServer();
       DownloadSampleFiles.execute(server);
    }




    public Server getServer() {
        return server;
    }
    public void setServer(Server server) {
        this.server = server;
    }


     public static void  execute(Server server){
        DownloadSampleFiles downloadTrhead  =   new DownloadSampleFiles(server);
        Executors.newSingleThreadExecutor().submit(downloadTrhead);



    }
}
