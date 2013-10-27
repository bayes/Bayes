/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

/**
 *
 * @author apple
 */
public class Download implements Callable <Boolean>{
    public final static int readTimeout             =   1800*1000;
    public final static int connectionTimeout       =   5*1000;
    public static final int  ERROR401               =   401;

    private String urlStr                           = null;
    private String errorMessage                     = null;
    private int  contentLength                      = 0;
    private int  contentWritten                     = 0;
    private File destination                        = new File("downloadedByJavaApplicatio");
    public String message                           = "Start download";
    public STATE state                              =  STATE.NEW;
    public boolean canceled                         = false;
    ProgressMonitor pbar                            = null;

    public enum STATE {NEW,  INPROGRESS, COMPLETED, CANCELED};
 

    public Download(String url, File dst){
           urlStr       = url;
           destination  = dst;
    }

    public Boolean call() {
          boolean isDone                =   false;
          URL  url                      =   null;
          OutputStream out              =   null;
          HttpURLConnection conn        =   null;
          InputStream  in               =   null;


      try {
           System.out.println("Downloading file "+url);
            url                     =   new URL(getUrlStr());
            out                     =   new BufferedOutputStream(new FileOutputStream(this.getDestination()));
            conn                    =   (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            contentLength = conn.getContentLength();

            System.out.println("");
            System.out.println("Downloading url "+ getUrlStr());
            System.out.println( "Downloading content length = "+ contentLength);
            System.out.println("Response code is "+ conn.getResponseCode());

           

            in                      =   conn.getInputStream();
            int bufferSize          =   1024;
            byte[] buffer           =   new byte[ bufferSize];

            int numRead;
            setContentWritten(0);

            state  = STATE.INPROGRESS;
            Thread dg = new Thread(new DownloadGUI());
            dg.start();

            while ((numRead         = in.read(buffer)) != -1 ) {

                    if (canceled){
                          state  = STATE.CANCELED;
                          return false;
                    }

                    out.write(buffer, 0, numRead);
                    setContentWritten(getContentWritten() + numRead);
            }
            isDone = true;
            state  = STATE.COMPLETED;

           }catch (Exception exception) {
               state  = STATE.CANCELED;
                exception.printStackTrace();
                return false;

            } finally {
                try {
                    if (in != null) {in.close(); }
                    if (out != null) {out.close();}
                } catch (IOException ioe) {}
            }


            return isDone;
    }




    public String getProgressMessage(){
       double curDownload    =   contentWritten/1024/8.0;
       double total          =   contentLength/1024/8.0 ;
       // long percent         =  Math.round(100.0 *curDownload/total);
       // System.out.println("total = "+total +" curDownload = "+ contentWritten +" percent "+ percent);
        String msg          =  String.format("%.1f of %.1f KB", curDownload, total);
        return  msg;
    }
    public boolean canDoProgressMonitor(){
       if (contentWritten < 1) {return false;}
       else {return true;}

    }


    public String getUrlStr() {
        return urlStr;
    }
    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getContentLength() {
        return contentLength;
    }
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public File getDestination() {
        return destination;
    }
    public void setDestination(File destination) {
        this.destination = destination;
    }

    public int getContentWritten() {
        return contentWritten;
    }
    public void setContentWritten(int contentWritten) {
        this.contentWritten = contentWritten;
    }


    public static void main (String [] args){
       // String url = "http://bayes.wustl.edu:8080/Bayes/Bayes.test.data.tar.gz";
    //    File gz    = new File ("DownloadTest.gz");
     //   Download download       =    new Download(url, gz);
     //   Future<Boolean> future  =   Executors.newSingleThreadExecutor().submit( download);

     Server server = interfacebeans.JServer.getInstance().getServer();
     server         = new Server("bayes.wustl.edu");
     server.setPort(80);

   //  load.DownloadSampleFiles.execute(server);

      int total       =  500;
                int cur         =   300;
                double percent  =   1.00*cur/total*100;
                String msg      =   String.format(
                        "Downloaded %.1f%s of %d bytes", percent,"%" ,total);

                System.out.println(msg);
    }



    class DownloadGUI implements Runnable{
    
        public void run() {

             boolean doProgressMonitor = canDoProgressMonitor() ;
             if  (doProgressMonitor == false){return;}

              // waith a little bit
              try {Thread.sleep(50); }
              catch (InterruptedException ex) { ex.printStackTrace(); return;}


             // start progress monitor
             while(state == STATE.INPROGRESS){
                    try {
                    // waith a little bit
                    Thread.sleep(30);
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

        public void update(){
            if (pbar == null){
                        pbar = new ProgressMonitor(null, "Downloading...",
                        "Initializing . . .",  0, contentLength );
                        pbar.setMillisToDecideToPopup(20);
                        pbar.setMillisToPopup(20);

            }
            pbar.setProgress( getContentWritten());
            pbar.setNote( getProgressMessage());
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
        if (state == STATE.INPROGRESS){update();}
        else {close();}

        }
  }
}
