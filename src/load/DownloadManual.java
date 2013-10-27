/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import bayes.DirectoryManager;
import java.awt.Desktop;
import utilities.DisplayText;
import utilities.Download;

/**
 *
 * @author apple
 */
public class DownloadManual  implements Callable <Boolean>{

    private String url                      =   null;
    private String  downloadDesciptor       =   null;
    public DownloadManual(String  aurl){ url = aurl;}

    public  Boolean call (){
      File dst      =  DirectoryManager.getBayesManualFile();
       
       try {

            Download download       =    new Download(url, dst );
            Future<Boolean> future  =   Executors.newSingleThreadExecutor().submit( download);

            boolean isDownloaded = future.get();


            if (isDownloaded) {
                 
               StringBuilder sb         =    new StringBuilder();
               sb.append("<html><p style=\"margin: 6px;\"><font size=\"5\">");
               sb.append(getDownloadDesciptor() + "was downloaded to <br>");
               sb.append("<font color=\"blue\" size = \"+1\"><bold>");
               sb.append( dst.getPath());
               sb.append( "</font></bold><br>");
               sb.append( " directory.");
               sb.append("</font></p><html>");
              // DisplayText.popupMessage(sb.toString());

               if (dst.exists()){
                Desktop.getDesktop().open(dst);
               }

          
                
                return true;

            }
            else if (download.canceled == true){
                return false;
            }
            else{throw new Exception();}


       }

       catch(Exception ex){
             String error = String.format("Failed to retrieve %s from \n%s.",
                     this.getDownloadDesciptor(),url);
             DisplayText.popupErrorMessage( error);
            return false;
       }
       finally{

       }


    }




    public static void main(String [] args){
      //Server server = JServer.getInstance().getServer();
      //DownloadManual.execute(server);
    }






   public static void  execute(DownloadManual serverDownload){
        Executors.newSingleThreadExecutor().submit(serverDownload);



    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getDownloadDesciptor() {
        return downloadDesciptor;
    }

    public void setDownloadDesciptor(String downloadDesciptor) {
        this.downloadDesciptor = downloadDesciptor;
    }
}
