/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;
import applications.model.Model;
import java.util.*;
import java.io.*;
import applications.bayesAnalyze.BayesAnalyze;
import interfacebeans.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JOptionPane;
import utilities.IO;
import utilities.Server;

public class DirectoryManager  implements ApplicationConstants  {

    private static final NumberFormat asciiFileNameformatter    =   new DecimalFormat("000");

  

    // make sure  that DirectoryManager can not be instantinated
    private DirectoryManager(){};
    public  final static String  spr                   = "/";
    public static File   startDir  ;
    public static final    String MODEL_COMPILE_DIR            =   "model.compile";

    public static void deliteDirectory (String dirStr){
        boolean isCurrenWorkDir     =   ApplicationPreferences.isCurrentWorkDir(dirStr);
         if ( isCurrenWorkDir ) {
            JOptionPane.showMessageDialog(null,
                    "Can not remove current working directory.",
                    "Message",  JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isWorkDir = ApplicationPreferences.isWorkDir(dirStr);
        if (isWorkDir){
            ApplicationPreferences.removeFromWorkDirList(dirStr);
        }

        File dir  =  getExperimentDir (dirStr);
        IO.deleteDirectory(dir);
    }
    public static void shutDownDirectory(){
                BayesManager.shutDownApplication();
    } 
    public static void leaveExperiment(){
            /****** Prepare direcotry to leave previous
       *            directory and clear data imnformation;*/
       
        // get reference to directory we are switching from
        File oldDir =  getExperimentDir();
       
        // serialize directory (oldDir) data information
        Serialize.serializeExperiment(oldDir);

    } 
    public static void move2Experiment(String exp){
        if (exp != null){
            ApplicationPreferences.setCurrentWorkDir(exp);
        }
        
       // if the directory  doesn't exist   - create one. 
        doAnalaysisDir ();
        
        
    } 
   
    
    public static void doAnalaysisDir (){
        
           // Bayes Other Analysis
            File destination =   getBayesOtherAnalysisDir () ;
            if (!destination.exists ()){ 
                destination.mkdirs ();
            }
            else if (destination.isDirectory() == false){
                destination.delete();
                destination.mkdirs ();
            }
            
            // BayesAnalyze
             destination =   getBayesAnalyzeDir ();
            if (!destination.exists ()){ 
                destination.mkdirs ();
            }
            else if (destination.isDirectory() == false){
                destination.delete();
                destination.mkdirs ();
            }
             
           
            // IMAGE FID DIR
            destination = getImageFidDir ();
            if (!destination.exists ()){  
                destination.mkdirs ();
            }
            else if (destination.isDirectory() == false){
                destination.delete();
                destination.mkdirs ();
            }
            
           //  IMAGE  DIR
            File imgDir             =   DirectoryManager.getImageDir() ;
            if (imgDir.exists() == false){imgDir.mkdirs();}
        }
    public static void cleanCurrentExp() {
         File bayesOtherDir = DirectoryManager.getBayesOtherAnalysisDir();
         getSerializationFile ().delete();
         IO.emptyDirectory( bayesOtherDir);
    }
    public static void cleanFidModelAndBayesAnalyzeDirs(){
       File bayesAnalDir    =   DirectoryManager.getBayesAnalyzeDir();
       File fidModelDir     =   DirectoryManager.getFidModelDir();

       IO.emptyDirectory(fidModelDir);
       IO.emptyDirectory (bayesAnalDir);

    }

    /*******************************************************************/

    public static File   getBayesDir(){
        File homedir =        ApplicationPreferences.getBayesHomeDir();
        return homedir ;
        
    }
    public static File getUserDirectory(){
          File userDir  =   new File ( System.getProperty("user.name"));
          return  userDir;
    }
    public static String getRelativePath(File reference, File file){
      String referencePath      =   reference.getPath() + File.separator;
      String filepath           =   file.getPath() ;    
      
      return filepath.replace( referencePath,"" );
    }

    


    public static String getExperimentDirName() { 
       return   ApplicationPreferences.getCurrentWorkDir();
    }

    public static File   getResourceDir(){
        File dir        =   getBayesDir();
        File resDir     =   new File (dir,RESOURCE_DIR);
        return resDir ;

    }
    public static File   getUserPropertiesFile(){
        File dir            =  getResourceDir();
        File file           =   new File (dir,user_resource);
        return file ;

    }
    public static File   getUserPropertiesBackupFile(){
        File dir            =  getResourceDir();
        File file           =   new File (dir,backup_resource);
        return file ;

    }


    public static File   getExperimentDir ()  { 
        return getExperimentDir(getExperimentDirName());
    }
    public static File   getExperimentDir (String name)  {
        File expDir   = new File( getBayesDir(), name);
        return expDir;
    }
    public static boolean   isCurrentExperimentDir(File adir){
        File curWorkDir     =     getExperimentDir  ();
        return curWorkDir.equals(adir);
    }
    public static boolean   isCurrentExperimentDir(String name){
        return  getExperimentDir().getName().equals(name);
    }
   

    public static boolean   isSerializationFilePresent(File adir){
        boolean valid       =   false;
        File ser            =   getSerializationFile (adir);
        if (ser.exists()) {
            valid = true;
        }
        return valid;
    }


 

    public static File   getFidDir   ()         { return getFidDir(getExperimentDir());}
    public static File   getFidDir   (File dir) { return new File (dir ,  FID_DIR_NAME);}
    public static File   getImageDir ()     {
        return getImageDir (getExperimentDir());}
    public static File   getImageDir (File workDir)     {
        return new File (workDir ,  IMAGE_DIR_NAME);}
    public static File   getASCIIDir ()     { return new File( getExperimentDir () ,  getAnalysisDirName());}
    public static File   getBayesOtherAnalysisDir ()     { 
        File workDir        =   getExperimentDir ();
        return  getBayesOtherAnalysisDir (workDir);}
    public static File   getBayesOtherAnalysisDir (File workDir)     {

        return new File( workDir ,  ASCII_DIR_NAME);}
    public static File   getFidModelDir ()  { return new  File (getExperimentDir(),   MODEL_FID_DIR);}
    public static File   getBayesAnalyzeDir ()  { return new  File (getExperimentDir(),  BAYES_ANALYZE );}
    public static File   getImageFidDir ()     { return new File (getExperimentDir() ,  IMAGE_FID_DIR);}
    public static File   getModelCompileDir ()     { return new File (getExperimentDir() ,  MODEL_COMPILE_DIR  );}


    public static String  getAnalysisDirName(){
        Model model = PackageManager.getCurrentApplication();
        
        if (model == null){
             return   BAYES_ANALYZE;
        }
        else if (model.getClass() == BayesAnalyze.class){
            return   BAYES_ANALYZE;
        }
        else{
             return   ASCII_DIR_NAME;
        }
    }
    public static File   getPWD(){
        String str  = System.getProperties ().getProperty("user.dir");
        File file   = new File(str);
        return file;
    }
    public static File   getUserHomeDirectory(){
        String str = System.getProperties ().getProperty("user.home");
        File file = new File(str);
        return file;
    }


    // SYSTEM Models and Predefines Specs directories.
    public static String getSystemModelURL() {
        Server server               =   JServer.getInstance().getServer();
        return  server.getSystemModelURL();
    }
    public static String getSystemPredefinedSpecURL()  {
        Server server               =   JServer.getInstance().getServer();
        return  server.getPredefinedSpecURL() ;
    }
   
    public static File   getUserPredefinedSpecDir (){
        File userPredefinedSpecDir         = new File( getBayesDir(), CLIENT_SPEC_DIR);
        return userPredefinedSpecDir ;
    }
    public static File   getUserModelDir() {
        File userModelDir         = new File( getBayesDir(), CLIENT_BAYES_MODEL);
        return userModelDir ;
    }

    public static File   getClientTestDatalDir() {
        File userModelDir         = new File( getBayesDir(), SAMPLE_DATA_DIR_NAME  );
        return userModelDir ;
    }
    public static File   getBayesManualFile() {
        File userModelDir         = new File( getBayesDir(), BAYES_MANUAL_FILE_NAME );
        return userModelDir ;
    }

    public static File   getSerializationFile ()  {
        File dir        = DirectoryManager.getExperimentDir();
        return getSerializationFile(dir);
    }
    public static File   getSerializationFile (File dir)  {
        String name     =    getSerializationFileName ();
        File file       = new File(dir,name );
        return file;
    }
    public static String   getSerializationFileName ()  {
        return Serialize.DIR_INF0;
    }

    public static File   getBayesParamsFile ()  {
        File dir        = DirectoryManager.getASCIIDir ();
        File file       = new File(dir,BAYES_PARAMS );
        return file;
    }
    public static File   getModelProbabilityFile ()  { 
        File dir        = DirectoryManager.getBayesOtherAnalysisDir();
        File file       = new File(dir, PROBAILITY_FILE_NAME );
        return file;
    }
    public static File   getSystemOutFile ()  {
        File dir        =  getBayesDir();
        File file       =  new File(dir, LOG_OUT_FILE_NAME);
        return file;
    }
    public static File   getSystemErrorFile ()  {
        File dir        =  getBayesDir();
        File file       =  new File(dir, LOG_ERR_FILE_NAME);
        return file;
    }

   
    // fid files
    public static File   getFidFile (File dir)  {
        File  file          = new File(dir, FID_FILE_NAME);
        return file;
    }
    public static File   getFidFile ()  { 
        File dir            = DirectoryManager.getFidDir ();
        return getFidFile(dir);
    }
    public static File   getProcparFile ()  { 
        File dir            = DirectoryManager.getFidDir ();
        return getProcparFile(dir);
    }
    public static File   getProcparFile ( File dir )  {
        File  file          = new File(dir,  PROCPAR_FILE_NAME);
        return file;
    }
    public static File   getFidDesciptorFile () {
        File dir        = DirectoryManager.getFidDir();
        File file       = new File(dir,  FFH);
        return file;
    }
    public static File   getFidDesciptorFile (File dir) {
        File file       = new File(dir,  FFH);
        return file;
    }
    public static File   getFidModelDesciptorFile () {
        File dir        = DirectoryManager.getFidModelDir();
        File file       = new File(dir,  FFH);
        return file;
    }

    public static File   getTextFile ()  {
        File dir            = DirectoryManager.getFidDir ();
        return getTextFile(dir);
    }
    public static File   getTextFile ( File dir )  {
        File  file          = new File(dir,  FID_TEXTFILE_NAME );
        return file;
    }
    public static File   writeTextFile(File fidFile ){
         if (fidFile    == null || fidFile.exists() == false){return null;}
         File   textFile        =   getTextFile(fidFile.getParentFile());

         if (textFile   != null && textFile.exists() == true){return textFile;}

         String fidFilename     =   fidFile.getName();
         IO.writeFileFromString( fidFilename,  textFile );

         return textFile;
    }
  

    
    // files in directory designed for BayesAnalyze files

    public static File   getIndexedModelFile (int index) {
        String baseName     =  MODEL_FILE_NAME;
        String format       =  INDEX_FORMAT;
        File dir            = DirectoryManager.getBayesAnalyzeDir();
        
        String indexString  = String.format(format, index);
        String filename     = baseName + "." + indexString;
        File modelFile      = new File(dir, filename);
        return modelFile;
    }
    public static File   getBayesAnalyzeParamsFile ()  { 
        File dir        = DirectoryManager.getBayesAnalyzeDir ();
        File file       = new File(dir, bAYES_PARAMS );
        return file;
    }
    public static File   getRegionsFile ()  {
        File dir        = DirectoryManager.getBayesAnalyzeDir ();
        File file       = new File(dir, REGIONS_FILE_NAME);
        return file;
    }
    
    // noise file file and directory
    
    public static File   getBayesNoiseFile ( File dir ) {
        File file = new File(dir,  BAYES_NOISE_FILE_NAME);
        return file;
    }
    public static File   getBayesNoiseFile () {
        File dir = DirectoryManager.getASCIIDir();
        File file = new File(dir,  BAYES_NOISE_FILE_NAME);
        return file;
    }
    public static File   getBayesAcceptFile ()  { 
        File dir        = DirectoryManager.getASCIIDir();
        File file       = new File(dir,BAYES_ACCEPT_FILE);
        return file;
    }
    public static File   getConsoleLogFile ()  {
        File dir        = DirectoryManager.getASCIIDir();
        File file       = new File(dir,CONSOLE_LOG_FILE );
        return file;
    }

   
  
    
    public static File   getAbscissaFile () {
        File asciiDir       =   DirectoryManager.getImageDir();
        File abscissaFile   =   new File(asciiDir, ABSCISSA_FILE_NAME) ;
        return abscissaFile;
    }
    //
    public static File    getModelFile   (int ind)  {
        String baseName     =  MODEL_FILE_NAME;
        String format       =  INDEX_FORMAT;
        File dir            = DirectoryManager.getBayesAnalyzeDir();
       
        String index        = String.format(format, ind);
        String filename     = baseName + "." + index;
        File modelFile      = new File(dir, filename);
        return modelFile;
    }
     
    public static File getBayesPlotFile () {
        File dir        = DirectoryManager.getBayesOtherAnalysisDir();
        File file       = new File(dir,  BAYES_PLOT_FILE_NAME);
        return file;
    }
    public static File getMcmcSamplesFile () {
        File dir        = DirectoryManager.getBayesOtherAnalysisDir();
        File file       = new File(dir,  MCMC_SAMPLE_FILE_NAME);
        return file;
    }  
    
    // image files
    public static File getRealImageFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  RE_IMAGE_FILE_NAME);
        return file;
    }
    public static File getImagImageFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  IM_IMAGE_FILE_NAME);
        return file;
    }
    public static File getAbsImageFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  ABS_IMAGE_FILE_NAME);
        return file;
    }
    public static File getRealImageDesciptorFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  RE_IFH_FILE_NAME);
        return file;
    }
    public static File getImagImageDesciptorFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  IM_IFH_FILE_NAME);
        return file;
    }
    public static File getAbsImageDesciptorFile () {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir,  ABS_IFH_FILE_NAME);
        return file;
    }    
    public static List <File> getListOfLoadedImageFiles (){
                 
                 ArrayList <File> loadedImg = new ArrayList <File>();
                 File realFile              =   DirectoryManager.getRealImageFile();
                 File imagFile              =   DirectoryManager.getImagImageFile();
                 File absFile               =   DirectoryManager.getAbsImageFile();
                 File realIhfFile           =   DirectoryManager.getRealImageDesciptorFile (); 
                 File imagIhfFile           =   DirectoryManager.getImagImageDesciptorFile (); 
                 File absIhfFile            =   DirectoryManager.getAbsImageDesciptorFile () ;
                 
                 loadedImg.add(absFile);
                 loadedImg.add(realFile );
                 loadedImg.add(imagFile);
                 loadedImg.add(absIhfFile); 
                 loadedImg.add(realIhfFile);
                 loadedImg.add(imagIhfFile);
                 
                 return loadedImg;
    }

    public static File   getImageFidFile ()  {
        File dir            = DirectoryManager.getImageFidDir ();
        File  file          = new File(dir,  FID_FILE_NAME);
        return file;
    }
    public static File   getImageProcparFile ()  {
        File dir            =   DirectoryManager.getImageFidDir();
        File  file          =   getProcparFile(dir);
        return file;
    }
    public static File getImageFileByName (String fileName) {
        File dir        = DirectoryManager.getImageDir();
        File file       = new File(dir, fileName);
        return file;
    }



    public static boolean isValidFidDir(File dir){
            if (dir.isDirectory() == false) {return false;}
            
            File[] files        = dir.listFiles();
            boolean isFid       = false;
            boolean isProcpar   = false;
            for (File file : files) {
                if ( file.getName().equals( FID_FILE_NAME) )      {isFid = true;}
                if ( file.getName().equals( PROCPAR_FILE_NAME) )  { isProcpar = true; }
            }
            boolean isValidDir = isFid && isProcpar;
            return isValidDir;
    }
    public  static File getIfhFileForImage(File imageFile){
        String imgEnd       =   "."+  IMG;
        String ifhEnd       =   "."+  IFH;
        String imgName      =   imageFile.getName();
        String ifhName      =   imgName.replace(imgEnd, ifhEnd);
        File  dir           =   imageFile.getParentFile();
        File ihfFile        =   new File (dir,ifhName  ) ;
        return  ihfFile;
    }
    public  static String addImgExtention(File imageFile){
        return  addImgExtention( imageFile.getName());
    }
    public  static String addImgExtention(String imageFileName){
        String imgEnd       =   "."+  IMG;
        String imgName      =   imageFileName+imgEnd;
        return  imgName;
    }




    public static List <String>getListOfImageSources(File dir){
        List <String> srcs = new ArrayList<String>();

        return srcs;

    }




   /* ascii data management*/
   public static  File []         getAsciiDataFiles(){
        File dir       =   DirectoryManager.getBayesOtherAnalysisDir();
        return getAsciiDataFiles(dir);
   }
   public static  File []         getAsciiDataFiles(File dir){
        File [] files       =    dir.listFiles(new  ASCIIFileFilter ());
        return files;
    }
   public  static List <File>     getAsciiDataFileListOnDisk(){
        File dir        =   DirectoryManager.getBayesOtherAnalysisDir();
        return  getAsciiDataFileListOnDisk(dir);
    }
   public  static List <File>     getAsciiDataFileListOnDisk(File dir){
        File [] files           =  getAsciiDataFiles(dir);
        List <File> list        =  new ArrayList<File>();

        if (files != null){
               list        =   Arrays.asList(files);
        }

        return list;
    }
   public  static List <File>     getAsciiDataAndIFHFileListOnDisk(){
        File dir        =   DirectoryManager.getBayesOtherAnalysisDir();
        return getAsciiDataAndIFHFileListOnDisk (dir);
    }
   public  static List <File>     getAsciiDataAndIFHFileListOnDisk(File dir){
        List <File> list        =  getAsciiDataFileListOnDisk(dir);
        ArrayList<File> files   =   new  ArrayList<File>();
        files.addAll(list);

        for (File file : list) {
            File afh           =    getAfhFileForData(file);
            files.add( afh); }
        return files;
    }

   public  static List <File>     getValidAfhFileListOnDisk(File dir){
        List <File> list        =   getAsciiDataFileListOnDisk(dir);
        ArrayList<File> files   =   new  ArrayList<File>();

        for (File file : list) {
            File afh           =   getAfhFileForData(file);
            if (afh.exists()){ files.add( afh);}
         }
        return files;
    }
   public static File             getAfhFileForData (File asciiFile) {
        if (asciiFile  ==  null) {return null;}
        String name     =   asciiFile.getName();
        String img      =   "."+ DAT;
        String ifh      =   "."+ AFH;
        name            =   name.replace(img,ifh );
        File dir        =   asciiFile.getParentFile();
        File file       =   new File(dir, name);
        return file;
    }
   public static File             getDatFileForIfh (File asciiFile) {
        if (asciiFile  ==  null) {return null;}
        String name     =   asciiFile.getName();
        String img      =   "."+ DAT;
        String ifh      =   "."+ AFH;
        name            =   name.replace(ifh,img );
        File dir        =   asciiFile.getParentFile();
        File file       =   new File(dir, name);
        return file;
    }
   public static File             getNewAsciiFile(File dir) {
        List <File>  datFiles   =      DirectoryManager.getAsciiDataFileListOnDisk();
        int ind                 =      getAvailableDataIndex(datFiles);
        String name             =      getAciiFileNameFormatter().format(ind)+"."+ DirectoryManager.DAT ;
        File dist               =      new File(dir,name );
        return dist;
    }
   public static File             getNewAsciiFile() {
        File dir                =      getBayesOtherAnalysisDir ();
        return  getNewAsciiFile(dir);
    }

   public static int              getAvailableDataIndex( List <File>  files) {
        if (files.isEmpty() == true) {return 1;}

        Collections.sort(files);
        int ind                 =    files.size() - 1;
        File file               =    files.get(ind);
        String name             =    file.getName();
        int    dotInd           =    name.indexOf(".");
        name                    =    name.substring(0, dotInd);
        int index               =    1+Integer.parseInt(name);
        return index;
    }
   public static NumberFormat     getAciiFileNameFormatter () {
        return asciiFileNameformatter;
    }
   static class ASCIIFileFilter implements FileFilter{

        public boolean accept(File imageFile) {
                boolean dontAccept  = false;
                boolean accept      = true;
                if (imageFile.isDirectory()){  return dontAccept;}

                String end          =   "."+ DAT;
                boolean bl          =   imageFile.getName().endsWith( end ) ;
                if (bl == false) {return dontAccept;}

                return accept;
            }
    }

   /* ascii data management stops */





   /* image data management*/

    public  static File []      getImageFileList(){
        File dir            =   DirectoryManager.getImageDir();
        return getImageFileList(dir);
    }
    public  static File []      getImageFileList(File imageDir){
        File [] files       =   imageDir.listFiles(new ImageFileFilter());
        return files;
    }
    public static   File        replaceImgByIFH (File imgFile) {
        if (imgFile  ==  null) {return null;}
        String name =   imgFile.getName();
        String img  =   "."+BayesManager.IMG;
        String ifh  =   "."+BayesManager.IFH;
        name        =   name.replace(img,ifh );
        File dir    =    imgFile.getParentFile();
        File file   =   new File(dir, name);
        return file;
    }
    public  static List <File>  getImageFileListOnDisk(File dir){
        List <File> list        =  new ArrayList<File>();
        try{
            File [] files               =  getImageFileList(dir);
            if (files != null){list     =   Arrays.asList(files);}
        }
        catch (Exception e){e.printStackTrace();}
        finally{return list;}
    }
    public  static List <File>  getIfhFileListOnDisk(File dir){
        ArrayList<File> files   =   new  ArrayList<File>();

        try{
            List <File> list        =   getImageFileListOnDisk(dir);

            for (File file : list) {
                File ifh           =  replaceImgByIFH(file);
                if (ifh.exists()){ files.add( ifh);}
            }
        }
        catch (Exception e){e.printStackTrace();}
        finally{return files;}
        
    }
    public  static List <File>  getImgAndIfhFileListOnDisk(File dir){
        List <File> imglist        =   getImageFileListOnDisk(dir);
        List <File> ifhlist        =   getIfhFileListOnDisk(dir);
        List <File> files          =   new ArrayList();

        files.addAll(imglist);
        files.addAll(ifhlist);

        return files;
    }
    public  static List <File>  getImgAndIfhFileListOnDisk(){
        File imageDir           =   DirectoryManager.getImageDir();

        return getImgAndIfhFileListOnDisk (imageDir);
    }

    public static class ImageFileFilter implements FileFilter{
        public boolean accept(File imageFile) {
                boolean dontAccept  = false;
                boolean accept      = true;
                if (imageFile.isDirectory()){  return dontAccept;}

                String end          =   "."+ BayesManager.IMG;
                boolean bl          =   imageFile.getName().endsWith( end ) ;
                if (bl == false) {return dontAccept;}

                File ihfFile        =   replaceImgByIFH(imageFile);
                if (ihfFile.exists() == false) {return dontAccept;}

                return accept;
            }
    }

   /* image data management stops */




      public static File             getBayesModelProbabilityFile() {
        File dir                =      getBayesOtherAnalysisDir ();
        return  getBayesModelProbabilityFile(dir);
    }
      public static File             getBayesModelProbabilityFile(File dir) {
        File file = new File(dir,  PROBAILITY_FILE_NAME );
        return file;
    }
}
