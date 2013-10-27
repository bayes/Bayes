/*
 * DoCGI.java
 *
 * Created on June 26, 2007, 11:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package bayes;
import java.io.*;
import java.net.*;
import interfacebeans.JServerPasswordDialog;
import java.util.*;
import run.Run;
import utilities.DisplayText;
import utilities.OSConversions;

public class DoCGI  {
    

 public static final String dummyExtraCaracter   =   ".";
 enum  REQUEST {POST, GET, PUT};
 public static final int  ERROR401              =   401;
 public static final String err401Message       =   "Failed authentification\n" +
                                                    "Make sure that user and passowrd " +
                                                    "information is correct.";
 public static final  String LOAD_SCRIPT        =   "cgi-bin/Bayes_get_system_load";
 public static final  String GET_STATUS_SCRIPT  =   "cgi-bin/Bayes_get_status";
 public static final  String GET_RESULT_SCRIPT  =   "cgi-bin/Bayes_get_results";
 public static final  String SUBMIT_JOB_SCRIPT  =   "cgi-bin/Bayes_submit_job";
 public static final  String DELETE_JOB_SCRIPT  =   "cgi-bin/Bayes_delete_job";
 public static final  String GET_FILES_SCRIPT   =   "cgi-bin/Bayes_dir_list";

 public static void      popupErrorMessageAndResetPassword(URL url){
           JServerPasswordDialog.getInstance().reset();
           StringBuilder message    = new StringBuilder();
           String server            =   null;
           if (url != null){
             server            =   url.getHost()+":"+url.getPort();
             message.append("Network error is encountered while trying to communicate");
             message.append("\n");
             message.append("with server "+ server + ".");
             message.append("\n\n");
           }
           else {
            message.append("Network error is encountered.");
            message.append("\n");
           }

            
            message.append("Please make sure that:");
            message.append("\n");
            message.append("\n");
            message.append("    You are connected to internet.");
            message.append("\n");
            message.append("\n");
            message.append("    Server can be accessed from your machine.");
            message.append("\n");
            message.append("    (setting VPN maybe required).");
            message.append("\n");
            message.append("\n");
            message.append("    Server name, user name and password (when required) are correct.");
            message.append("\n");


             DisplayText.popupErrorMessage(message);
    }
 public static void      popupAuthentificationErorrAndResetPassword(){
            JServerPasswordDialog.getInstance().reset();
            DisplayText.popupMessage(err401Message );
    }
 public static void      main(String[] args)  {
       
       String url;

       // Obtain the InetAddress of the computer on which this program is running


            //url = "http://bmrw200.wustl.edu:8080/";
            //  getLoad(url, "apple", "karen76");
            //   getLoad(url, "apple", "karen76");
            // Authenticator.setDefault(null);
           //   getConnectionForPUT(url, "apple", "karen76");
            //  System.out.println(getLoad(url, "apple", "karen76"));
            //  System.out.println(getLoad(url, "larry", "recipes33"));
            // Authenticator.setDefault(new MyAuthenticator("larry","recipies33" ));
            // url ="http://bmrw206.wustl.edu:8080/Bayes/BayesAsciiModels/";
            //getFileListing();
            //  String request              =   "bayes/" +dummyExtraCaracter;
            //  deleteCompletedJob(url, request ,"apple", "karen76");
            //   getFileListing(url,"BayesAsciiModels"  ,"apple", "karen76");
            //  getFileListing(url,"Bayes.Predefined.Spec"  ,"apple", "karen76");
             try {
             String username  = "marutyan";
             String password  = "karen76";

           // username  = "larry"; password  = "recepies:33";

            //String remoteFile   =  "http://bmrw204.wustl.edu:8080/Bayes/installation.inf";
           // File toFile  = new File ("/Users/apple/AAAAAAAAAAAA");
           // boolean isCreated  = toFile.createNewFile();



            url = "http://bmrw204.wustl.edu:8080/";
         //  File file     = new File ( "/Users/apple/Bayes/job.results.tar");
         // file         =   new File( "/Users/apple/hugeTar");
          //file = new File ("/Users/apple/Bayes/System.err.txt");
               // checkConnectionTest( url, username, password) ;
            // submitJobTest(url, file, username, password);
             // submitJob(url, file, username, password);
             checkConnection( url, username, password) ;
             checkConnection( url, "XXXXXXXXXXXXXXXXXX", password) ;

        }
     
        catch (Exception ex) {
             ex.printStackTrace();
        }

    }

 public static int getConnectionTimeOut(){
    return ApplicationPreferences.getCGIConnectionTimeOutInSec()*1000;
 }
 public static int getReadTimeOut(){
    return ApplicationPreferences.getCGIReadTimeOutInSec()*1000;
 }


  public static String           submitJob (String urlStr, File file,
                                String username, String password) {

         Authenticator.setDefault(new MyAuthenticator(username, password));
         OutputStream outStream             =  null;
         InputStream inServerStream         =  null;
         InputStream fileinStream           =  null;
         BufferedReader in                  =  null;
         String jobID                       =  null;
         String         urlString           =  urlStr +   SUBMIT_JOB_SCRIPT;
         URL  url                           =  null;
         int chunkSize                      =  1024*1024* ApplicationPreferences.getSubmitJobBufferSizeMB();

         System.out.println("Submit job to "+urlString  );
         try{
             url                            =   new URL(urlString);
             HttpURLConnection connection   = (HttpURLConnection) url.openConnection();


             if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }
             else {
                System.out.println("Response code is "+connection.getResponseCode());
             }
              connection.disconnect();

            connection   = (HttpURLConnection) url.openConnection();
            
            int filesize                 =   (int)file.length();
            if(filesize   < chunkSize ){
                chunkSize              =    filesize;
            }
            

             connection.setRequestMethod("PUT");
             connection.setFixedLengthStreamingMode( filesize );
             connection.setReadTimeout(getReadTimeOut());
             connection.setConnectTimeout(getConnectionTimeOut());
             connection.setDoOutput(true);
             connection.setDoInput(true);

             outStream              = connection.getOutputStream();
             fileinStream           = new FileInputStream (file);

             if(fileinStream  != null)
             {
                    long startTime          =   System.nanoTime();
                    byte tmp[]              =   new byte[chunkSize ];
                    long written            =   0;

                    for(int i = 0; (i =  fileinStream .read(tmp)) >= 0;)
                    {
                       outStream.write(tmp, 0, i);
                       written    += i;

                     double percent = 1.0*written/filesize*100;
                     String progressMessage = String.format("Uploading job to server - " +
                                                             "%4.2f %s percent ( %d  of %d bytes)", percent,"%", written, filesize);
                    Run.setProgressMessage(progressMessage);
                    }
                    long endTime         = System.nanoTime();
                    double duration      = (endTime - startTime)*1E-9;
                    System.out.println("Time to submit "+  duration);
            }
            

             in             = new BufferedReader( new InputStreamReader(connection.getInputStream()));

             String line    = in.readLine();

             System.out.println("Output of submit script (Job ID)"+ line);
             jobID = line;

             if (jobID == null){ return null;}


             if(jobID.equals("0")){
                DisplayText.popupErrorMessage("The submit failed, analysis not run");
                return jobID;
             }


     }
     catch(SocketTimeoutException e){
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);
     }
     catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);

     } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
     } catch (Exception e) {
               e.printStackTrace();
               popupErrorMessageAndResetPassword(url);

     }finally {
            try {
                if (in != null) { in.close();}
                if (outStream != null) { outStream.close();}
                if (inServerStream != null){inServerStream .close();}
                if (fileinStream != null){fileinStream.close();}
            }  catch (IOException ex) {}
     }
     return jobID ;
 }

 public static List<String>   getFileListing(String urlString, String dir ,String username,
                                                  String password){
         Authenticator.setDefault(new MyAuthenticator(username, password));
         String         line                    =   null;
         urlString                              =   urlString + GET_FILES_SCRIPT;
         BufferedReader in                      =   null;
         URL            url                     =   null;
         HttpURLConnection connection           =   null;
         String request                         =   dir;
         PrintWriter out                        =   null;
         List<String>   flenames                =   new  ArrayList<String>()  ;
         System.out.println("Get file listing from "+urlString  );

         if ( OSConversions.isWindows() == false){

           request                              =   request   + DoCGI.dummyExtraCaracter;
         }



         try {
             url                                =   new URL(urlString);
             connection                         =   (HttpURLConnection) url.openConnection();
             connection.setConnectTimeout(getConnectionTimeOut());
            // connection.setRequestMethod("POST");

             connection.setDoOutput(true);
             connection.setReadTimeout(getReadTimeOut());

             out                        =   new PrintWriter(connection.getOutputStream());

              out.println ("");
              out.println ("");
              out.println ("");
              out.println(request);

              System.out.println("REQUEST "+ request);
              out.flush ();

              if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }

              in = new BufferedReader(new InputStreamReader(connection.getInputStream()));


               while ((line = in.readLine()) != null) {

                 //System.out.println(line);
                 flenames.add(line);
               }

      }catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);
                return  null;

     } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
                return  null;
     } catch (Exception e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);
                return null;

     }
     finally {
            try {
                if (in != null) { in.close();}
                if (out!= null) {out.close();}
            } catch (IOException ex) {}

     }
     return flenames;
 }


 public static  StringBuilder getLoad(String urlStr,  String username, String password) {
     StringBuilder  sb                      =   new StringBuilder();
     String         line                    =   null;
     String         urlString               =   urlStr + LOAD_SCRIPT;
     BufferedReader in                      =   null;
     URL            url                     =   null;
     HttpURLConnection connection           =   null;

     try {
           // Authenticator authenticator = new MyAuthenticator(username, password);
            Authenticator.setDefault( new MyAuthenticator(username, password));
            url                 =        new URL(urlString);
           System.out.println("Get system load from "+urlString  );


            connection                  = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(getConnectionTimeOut());
            connection.setReadTimeout(getReadTimeOut());


          int status = connection.getResponseCode();
    
            System.out.println("response code " + status);
            if (status == ERROR401 ){
                throw new FailedAuthentificationException();

            }
            in                              =   new BufferedReader(new InputStreamReader(
                                                connection.getInputStream()));
            
            while ((line = in.readLine()) != null) {

                sb.append(line + BayesManager.EOL);

            }
        
     
     } catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);

     } catch (FailedAuthentificationException e) {
              sb.append(err401Message);
              JServerPasswordDialog.getInstance().reset();
              e.printStackTrace();
     } catch (Exception e) {
               e.printStackTrace();
               popupErrorMessageAndResetPassword(url);

     }finally {
            try {
                if (in != null) { in.close();}
            }   catch (IOException ex) {}

     }
     return  sb;
  }    

 public static String           getStatus(String urlStr, String request,
                                String username, String password, String defaultOut){
        Authenticator.setDefault(new MyAuthenticator(username, password));
        String line                     =   null;
        PrintWriter out                 =   null;
        BufferedReader in               =   null;
        HttpURLConnection connection    =   null;
        String status                   =   defaultOut;
        String         urlString        =   urlStr + GET_STATUS_SCRIPT;
        URL  url                        =   null;


        if ( OSConversions.isWindows() == false){
           request  = request + DoCGI.dummyExtraCaracter;
        }



        System.out.println("Get status from "+ urlString + " request "+ request);
        try {  
             url                        =   new URL(urlString);
             connection                 =   (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("POST");
             connection.setDoOutput(true);
             connection.setConnectTimeout(getConnectionTimeOut());
             connection.setReadTimeout(getReadTimeOut());

          


             out                        =   new PrintWriter(connection.getOutputStream());
             
              out.println ("");
              out.println ("");
              out.println ("");
              out.println(request);
              out.flush ();


              if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }

              in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                        
     
               while ((line = in.readLine()) != null) {
                 status = line;
                 System.out.println("In doCGI status get_status return  = "+ status);
                }
 
      }catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword( url );

     } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
     } catch (Exception e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword( url );

     }
     finally {
            try {
                if (in != null) { in.close();}
                if (out!= null) { out.close();}
            } catch (IOException ex) {}
             return status;
     }
  }
 public static boolean          getResults(String urlStr, String fileIn, File dist,
                                   String username, String password){
        boolean     result              =    false;
        Authenticator.setDefault(new MyAuthenticator(username, password));
        PrintWriter out                 =   null;
        InputStream in                  =   null;
        FileOutputStream o              =   null;
        HttpURLConnection connection    =   null;
        String         urlString        =   urlStr +  GET_RESULT_SCRIPT;
        URL  url                        =   null;

        if ( OSConversions.isWindows() == false){
            fileIn  = fileIn + DoCGI.dummyExtraCaracter;
        }

        try {
             url                        =   new URL(urlString);
             connection                 =   (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("POST");
             connection.setDoOutput(true);
             connection.setConnectTimeout(getConnectionTimeOut());
             connection.setReadTimeout(getReadTimeOut());

             out                        = new PrintWriter(connection.getOutputStream());
 
              out.println("");
              out.println ("");
              out.println ("");
              out.println(fileIn);
              out.flush();

              if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }

             
              in                        =   connection.getInputStream();
              o                         =   new  FileOutputStream(dist);
              byte tmp[]                =   new byte[33554432];
              long written              =   0;
              for(int i = 0; (i = in.read(tmp)) >= 0;)
              {
                    o.write(tmp, 0, i);
                    written +=i;

                    String progressMessage = String.format(
                              "%s bytes are downloaded so far", written);
                    Run.setProgressMessage(progressMessage);
              }

              result                    =    true;

        }catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);

        } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
        } catch (Exception e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);

        }
        finally {
            try {
                if (in != null) { in.close();}
                if (out!= null) {out.close();}
                if (o  != null) {o.close();}
            } catch (IOException ex) {}
             return  result;
     }
     
 }

 public static String           deleteCompletedJob(String urlStr, String jobId, String account,
                                String username, String password){

        Authenticator.setDefault(new MyAuthenticator(username, password));
        String status                           =   "";
        String         urlString                =   urlStr +    DELETE_JOB_SCRIPT ;
        BufferedReader in                       =   null;
        PrintWriter out                         =   null;
        URL            url                      =   null;
        HttpURLConnection connection            =   null;
        String line                             =   null;
        String request                          = account + "/" + jobId;
        if ( OSConversions.isWindows() == false){
            request  = request + DoCGI.dummyExtraCaracter;
        }


        System.out.println("Delete completed job from "+urlString  );
        System.out.println("Server Request: "+ request);


        try {
             url                                =   new URL(urlString);
             connection                         =   (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("POST");
             connection.setDoOutput(true);
             connection.setConnectTimeout(getConnectionTimeOut());
             connection.setReadTimeout(getReadTimeOut());

              out                               = new PrintWriter(connection.getOutputStream());

              out.println ("");
              out.println ("");
              out.println ("");
              out.println(request);
              out.flush();




              if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
              }

              System.out.println("");
              System.out.println("Cleaning jobs on server");
              System.out.println("request = "+ request);

              in = new BufferedReader(new InputStreamReader( connection.getInputStream()));
              while ((line = in.readLine()) != null) { System.out.println("In remove job in = "+line);}



     } catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword( url );

     } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
     } catch (Exception e) {
               e.printStackTrace();
               popupErrorMessageAndResetPassword( url );

     }finally {
            try {
                if (in  != null) { in.close();}
                if (out != null) { out.close();}
            }  catch (IOException ex) {}
     }
     return status;
  }

 public static boolean           checkConnection(String urlStr, String username, String password) {
                               

        System.out.println("Checking connection to server "+ urlStr);
        Authenticator.setDefault(new MyAuthenticator(username , password));


         URL  url                               =   null;
         PrintWriter out                        =   null;
         HttpURLConnection connection           =   null;
         String         urlString               =   urlStr +  LOAD_SCRIPT;
      

         try{
            
             url                        =   new URL(urlString);
             connection                 =   (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("POST");
             connection.setDoOutput(true);
             connection.setConnectTimeout(getConnectionTimeOut());
             connection.setReadTimeout(getReadTimeOut());


             if (connection.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }
             else {
               // System.out.println("Response code is "+connection.getResponseCode());
             }

                  

     }

     catch ( java.net.UnknownHostException e){
                e.printStackTrace();
                popupErrorMessageAndResetPassword( url);
                return false;
     }

     catch ( java.net.ConnectException e){
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);
                return false;
     }
     catch (IOException e) {
                e.printStackTrace();
                popupErrorMessageAndResetPassword(url);
                return false;

     } catch (FailedAuthentificationException e) {
                popupAuthentificationErorrAndResetPassword();
                e.printStackTrace();
                return false;
     } catch (Exception e) {
               e.printStackTrace();
               popupErrorMessageAndResetPassword(url);
               return false;

     }finally {
                if (connection != null){
                   connection.disconnect();
                }
                 Authenticator.setDefault(null);

     }
     

     System.out.println("Connection is granted.");
     return true ;
 }
 public static boolean           connectToURL(String urlStr)
 throws UnknownHostException, ConnectException, ConnectException, MalformedURLException, IOException{
      boolean success                           =   false;

             URL  url                           =   new URL(urlStr);

             System.out.println("connecting to url " + url);
             HttpURLConnection connection       = (HttpURLConnection) url.openConnection();
             connection.setConnectTimeout(500);
             connection.setReadTimeout(500);
             connection.setDoOutput(true);
             connection.setDoInput(true);

             int response = connection.getResponseCode();
             if (response == 200){success  = true;}

             System.out.println("ConnectToURL " +urlStr+" "+ response);
                 

     return success  ;


 }


 

 public static boolean downloadFile (  String urlstr, File localFile, String username, String password)throws SocketTimeoutException, FileNotFoundException{
       Authenticator.setDefault(new MyAuthenticator(username, password));

      boolean isDownloaded          =   false;
      URL  url                      =   null;
      OutputStream out              =   null;
      HttpURLConnection conn        =   null;
      InputStream  in               =   null;
      int chunkSize                 =  1024*1024* ApplicationPreferences.getDownloadJobBufferSizeMB();

System.out.println( "Downloading file = "+ urlstr + " "+localFile.getPath());
      try {
            url                     =   new URL( urlstr);
            out                     =   new BufferedOutputStream(new FileOutputStream(localFile));
            conn                    =   (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(getConnectionTimeOut());
            conn.setReadTimeout(getReadTimeOut());
            conn.setDoOutput(true);
            conn.setDoInput(true);

            System.out.println("Response code is "+conn.getResponseCode());
            if (conn.getResponseCode() == ERROR401 ){
                throw new FailedAuthentificationException();
             }
             else {
             }

            int  contentLength      =   conn.getContentLength();
            System.out.println( "Downloading content length = "+ contentLength);


            in                      =   conn.getInputStream();




            byte[] buffer           =   new byte[chunkSize];

            int numRead;
            long numWritten         = 0;
            while ((numRead         = in.read(buffer)) != -1) {

                    out.write(buffer, 0, numRead);
                    numWritten += numRead;
            }
            isDownloaded = true;
            }
            catch (SocketTimeoutException exception) {
                 popupErrorMessageAndResetPassword(url);
                 throw new SocketTimeoutException( exception.getMessage());
            }
            catch (FileNotFoundException exception) {
                 throw new FileNotFoundException( exception.getMessage());
	    } catch (Exception exception) {
                isDownloaded = false;
                exception.printStackTrace();

                popupErrorMessageAndResetPassword(url);

            } finally {
                try {
                    if (in != null) {in.close(); }
                    if (out != null) {out.close();}
                } catch (IOException ioe) {}
            }


       return isDownloaded ;
     }

}

 class MyAuthenticator extends Authenticator {
        String username;
        String password;
    
        MyAuthenticator(String username, String password){
             this.username = username;
             this.password = password;
        }
        // This method is called when a password-protected URL is accessed
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
           
             
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
  class FailedAuthentificationException extends Exception {

}