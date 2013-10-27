/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.io.Serializable;

public class Server implements  Cloneable, Serializable{
    public static String REMOTE_ROOT                    = "Bayes";
    public static String SERVER_BAYES_MODEL             = "BayesAsciiModels";
    public static String SERVER_SPEC_DIR                = "Bayes.Predefined.Spec";
    public static String SERVER_TEST_DATA               = "Bayes.test.data.tar.gz";
    public static String SERVER_MANUAL                  = "BayesManual.pdf";
    public static String NOEMAIL                        = "None";
    public static String DEFAULT_QUEUE                  = "None";

    public static  Server getDummyServer(){
       return new Server("none");
   }

    private int port                =   8080;
    private String account          =  "bayes";
    private String email            =   NOEMAIL;
    private String user             =   System.getProperty("user.name");
    private int max_cpu             =   1;
    private int cpu                 =   1;
    private String queue            =   DEFAULT_QUEUE ;
    //private String queue            =   "dque_smp";
    private String name             =   "www.xxx.yyy";
    private boolean isPassword      =   true;
    private boolean cCompiler       =   true;
    private boolean fortanCompiler  =   true;
    public boolean isEditable       =   false;


    public String getAccounKey(){return this.getName() +".account"; }
    public String getEmailKey(){return this.getName() +".email"; }
    public String getQueueKey(){return this.getName() +".queue"; }
    public String getPortKey(){return this.getName() +".port"; }
    public String getCPUMaxKey(){return this.getName() +".cpu_max"; }
    public String getCPUKey(){return this.getName() +".cpu"; }
    public String getPasswordKey(){return this.getName() +".isPassword"; }
    public String getUserKey(){return this.getName() +".user"; }
    public String getFortanKey(){return this.getName() +".isFortanCompiler"; }
    public String getCKey(){return this.getName() +".isCCompiler"; }



   
    public Server (String aName) { this.name = aName;}

    
    @Override
    public Server clone(){
        try {
           return (Server) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("clone in Server has thrown error");
            
            return null;
                 
        }
     
       
       
    }



    /* do not modify
     * this wirl scree up EditServer dialog and possiibly other GUI elements
     */
    @Override
    public String toString(){
        return name;
     }


    /* do not modify. Crucial to run JSditServers.isDuplicte() method
     */
    @Override
    public boolean equals(Object aThat) {
        //check for self-comparison
        if ( this == aThat ) return true;
        if ( aThat == null || !(aThat instanceof Server) ) return false;

        Server srv = (Server)aThat;

        boolean eq =    srv.getName().equalsIgnoreCase(this.getName()) &&
                        srv.getPort() == this.getPort();

        return eq;
  }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash+ + this.port;
        return hash;
    }


    public String getHttpURL(){
         return getHttpURL( getPort());
    }
    public String getHttpURL(int aport){
         String url      = "http://"+ getName() +":" + aport+"/";
         return url;
    }

    public String getScriptURL(String script){
         String url      = getHttpURL() + "/"+ script;
         return url;
    }


    public  String  getSystemURL()  {
        String sysDir               =   getHttpURL() + REMOTE_ROOT + "/";
        return sysDir;
    }

    public  String getSystemModelURL() {
        String sysModelDir          =   getSystemURL() +SERVER_BAYES_MODEL;
        return  sysModelDir;
    }

    public  String getPredefinedSpecURL()  {
        String specDir              =   getSystemURL()  + SERVER_SPEC_DIR;
        return specDir ;
    }
    public  String getTestDataURL()  {
        String specDir              =   getSystemURL()  + SERVER_TEST_DATA;
        return specDir ;
    }
    public  String getManualURL()  {
        String specDir              =   getSystemURL()  + SERVER_MANUAL;
        return specDir ;
    }


   public  String getRelativePath()  {
        String specDir              =   getAccount()+ "/"+ REMOTE_ROOT+"/";
        return specDir ;
    }
   public  String  getRelativePathToSpecDir()  {
        String path       =  getRelativePath()+  SERVER_SPEC_DIR;
        return path;
    }
   public  String  getRelativePathToModelDir()  {
        String path       =  getRelativePath()+  SERVER_BAYES_MODEL;
        return path;
    }

    public int      getPort () {
        return port;
    }
    public String   getAccount () {
        return account;
    }
    public String   getEmail () {
        return email;
    }
    public int      getMaxCpu () {
        return max_cpu;
    }
    public int      getCpu () {
        return cpu;
    }
    public String   getQueue () {
        return queue;
    }
    public String   getName () {
        return name;
    }
    public boolean  isPassword () {
        return isPassword;
    }
    public String   getUser () {
        return user;
    }
    public boolean  iscCompiler() {
        return cCompiler;
    }
    public boolean  isFortanCompiler() {
        return fortanCompiler;
    }
    public String   getShortName () {
        String fullname       =   getName();
        String shname       =   fullname.toString();
        if (fullname!= null && fullname.isEmpty() == false){
            int ind = fullname.indexOf(".");
            if (ind > 0){
                shname      =   fullname.substring(0, ind);
            }
        
        }
        return shname;
    }

    public void setPort ( int port ) {
        this.port = port;
    }
    public void setAccount ( String account ) {
        this.account = account;
    }
    public void setEmail ( String email ) {
        this.email = email;
    }
    public void setMaxCpu ( int max_cpu ) {
        this.max_cpu = max_cpu;
    }
    public void setCpu ( int cpu ) {
        this.cpu = cpu;
    }
    public void setQueue ( String queue ) {
        this.queue = queue;
    }
    public void setName ( String name ) {
        this.name = name;
    }
    public void setIsPassword ( boolean isPassword ) {
        this.isPassword = isPassword;
    }
    public void setUser ( String user ) {
        this.user = user;
    }
    public void setcCompiler(boolean cCompiler) {
        this.cCompiler = cCompiler;
    }
    public void setFortanCompiler(boolean fortanCompiler) {
        this.fortanCompiler = fortanCompiler;
    }
  
}
