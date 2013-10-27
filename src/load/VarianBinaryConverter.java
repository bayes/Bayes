/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.PackageManager;
import bayes.BayesManager;
import bayes.DirectoryManager;
import load.gui.JVariaImageFileChooser;
import java.io.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import image.varian.VarianFidImage;
import image.varian.VairanImageReader;
import utilities.*;
import utilities.InfiniteProgressPanel;
import fid.Procpar;
import interfacebeans.AllViewers;
import image.ImageViewer;
/**
 *
 * @author apple
 */
public class VarianBinaryConverter {
     public static boolean MULTITHREAD                   =   true;

     private javax.swing.Timer timer                     =   null;
     private LoadVarianImage loadImg                     =   null;
     private boolean isPhaseEncodeSetByUser              =   false;
     private boolean isredOutLengthSetByUser             =   false;
     private int     phaseEncodeLengthSetByUser          =   -1;
     private int     redouLengthSetByUser                =   -1;
     private static String INFO                          =   "";
     private File sourceDirectory                        =    null;
     private boolean           phaseImages               =   true;



     public static void chooseAndLoadVarianImage(){
         VarianBinaryConverter converter  = new VarianBinaryConverter();
         JVariaImageFileChooser.showDialog(converter);
         boolean canceled = JVariaImageFileChooser.getInstance().isCanceled();
         if (canceled){return;}

         File srcDir = converter.getSourceDirectory();
         converter.loadImage(srcDir);

     }


     public void loadImage (File file){
       // if fid is not an image - do nothing
       if ( isValidImage (file) == false){ return;}


       // Record current state to ProgreeMonitor
        setInfo("Start Loading Image");

       // non-swing loading procedures are going here
       setLoadVarianImage(new LoadVarianImage(file));
       getLoadVarianImage().execute();


       // CallBack implemtens ActrionListener and is
       // responsible for updating Progress Monitor as
       // well updates ImageViewer when image load is complete
         setTimer(new Timer(10, new CallBack()));
         getTimer().start();




    }

     public  boolean isValidImage (File src){


           if ( src.isDirectory() == false ) {
                DisplayText.popupMessage(   "You must load DIRECTORY that\n" +
                                             "contains fid file.");
                return false;
            }

            boolean isValidDir = DirectoryManager.isValidFidDir(src);
            if ( isValidDir == false ) {
                DisplayText.popupMessage(
                                 "You must select directory that contains \n" +
                                 "files named fid and procpar");
                return false;
            }

            File procparFile    =   DirectoryManager.getProcparFile(src);
            boolean isImage     =   Procpar.isImage(procparFile) ;

            if ( isImage  == false ) {
                DisplayText.popupMessage(
                                 "Selected fid is not an image");
                return false;
            }

            return true;
    }
     public  void copyAndLoadVarianBinaryImage (File src) throws FileNotFoundException, IOException{

          boolean isValid  = isValidImage(src);
          if (isValid == false) {return;}

          setInfo("Copying fid files to working directory");

          // copy files to exp/image.fid dir
          copyFidImageDir(src );

         // load varian binary image 
         convertTo4fdpbinary(src);

    }

     private  void convertTo4fdpbinary (File src) throws FileNotFoundException, IOException{


            VarianFidImage fidImage              =      VairanImageReader.readKSpaceFid(src);


            // set padded FFT length for images
            if (isPhaseEncodeSetByUser() == true){
                int phaseEncodePaddedLengthUser     =  getPhaseEncodeLengthSetByUser();
                 fidImage.setOutputPhaseEncodeLength(phaseEncodePaddedLengthUser);

            }
            if (isRedOutLengthSetByUser() == true){
                int readoutPaddedLengthUser         =  getRedouLengthSetByUser();
                fidImage.setOutputReadOutLength(readoutPaddedLengthUser  );

            }

            boolean isPhaseImages                     =   this.isPhaseImages();
            fidImage.setPhaseImages(isPhaseImages);

            //long t1 = System.nanoTime();
             System.out.println("Loading fid image");
             System.out.println("Use multiple threads = "+MULTITHREAD);
              if (MULTITHREAD){
                  // ImageConvertHelper.convertVarianFidTo4dfpBinaryMultiThread(fidImage,src.getAbsolutePath());
                   ImageConvertHelper.convertVarianFidTo4dfpBinaryMultiThread(fidImage,src.getAbsolutePath());
              }
              else {
                   ImageConvertHelper.convertVarianFidTo4dfpBinary(fidImage,src.getAbsolutePath());
              }

            
           
            //long t2 = System.nanoTime();
           // double time = (t2 - t1)*1e-9;
           // System.out.println("Time to FFT and save all images to disk  = " + time +" "+ "seconds.");

            fidImage           = null;

    }

     public static String   getInfo () {
        return INFO;
    }
     public static void     setInfo ( String aINFO ) {
        INFO = aINFO;
    }
     public static void     copyFidImageDir(File src) {

        // assign destination directories
        File imgFidDir             = DirectoryManager.getImageFidDir();

        // if source directory  = destination directory  -> do  nothing
        if(imgFidDir.equals(src)){ return;  }

        // make sure image directory file exists
         File imgDir             =   DirectoryManager.getImageDir() ;
         if (imgDir.exists() == false){imgDir.mkdirs();}

        // completely clean fid image directory  and bayes other analysis directories
        //prior to copying
        IO.emptyDirectory(imgFidDir  );


        FileFilter filter   = new fid.FileFilters.FilesToBeCopyToFidDir();
        File []   files     =  src.listFiles(filter);
        for (File file :files) {

            String filename =    file.getName();
            File dist       =     new File(imgFidDir , filename);


            if (filename.equalsIgnoreCase(BayesManager.PROCPAR_FILE_NAME)){
                fid.Procpar.overwriteFileSource(file, dist);
            }
            else{
                IO.copyFile(file, dist);

            }
        }




    }

    public int getRedouLengthSetByUser () {
        return redouLengthSetByUser;
    }
    public int getPhaseEncodeLengthSetByUser () {
        return phaseEncodeLengthSetByUser;
    }
    public boolean isRedOutLengthSetByUser () {
        return isredOutLengthSetByUser;
    }
    public boolean isPhaseEncodeSetByUser () {
        return isPhaseEncodeSetByUser;
    }

    public void setRedouLengthSetByUser ( int redouLengthSetByUser ) {
        this.redouLengthSetByUser = redouLengthSetByUser;
    }
    public void setPhaseEncodeLengthSetByUser ( int phaseEncodeLengthSetByUser ) {
        this.phaseEncodeLengthSetByUser = phaseEncodeLengthSetByUser;
    }
    public void setIsRedOutLengthSetByUser ( boolean isredOutLengthSetByUser ) {
        this.isredOutLengthSetByUser = isredOutLengthSetByUser;
    }
    public void setIsPhaseEncodeSetByUser ( boolean isPhaseEncodeSetByUser ) {
        this.isPhaseEncodeSetByUser = isPhaseEncodeSetByUser;
    }


    public javax.swing.Timer getTimer () {
        return timer;
    }
    public LoadVarianImage getLoadVarianImage () {
        return loadImg;
    }

    public void setTimer ( javax.swing.Timer timer ) {
        this.timer = timer;
    }
    public void setLoadVarianImage ( LoadVarianImage loadImg ) {
        this.loadImg = loadImg;
    }

    public File getSourceDirectory () {
        return sourceDirectory;
    }
    public void setSourceDirectory ( File sourceDirectory ) {
        this.sourceDirectory = sourceDirectory;
    }

    public boolean isPhaseImages() {
        return phaseImages;
    }
    public void setPhaseImages(boolean phaseImages) {
        this.phaseImages = phaseImages;
    }


     class CallBack implements ActionListener{
        boolean isStarted                       =   false;
        InfiniteProgressPanel progresspane      =   new InfiniteProgressPanel();
        private Component glassPane             =   null;
        private Component packagePane           =   (Component)PackageManager.getCurrentApplication();
        private JFrame frame                    =   (JFrame)SwingUtilities.windowForComponent(  packagePane);
        public void actionPerformed(ActionEvent ev){

            if(isStarted == false){

                    progresspane            = new InfiniteProgressPanel();
                    glassPane               = frame.getGlassPane();
                    frame.setGlassPane(progresspane );
                    frame.validate();
                    progresspane.start();
                    isStarted               = true;
               }


              progresspane.setText(getInfo());

            if (getLoadVarianImage().isRunning() == false) {
                getTimer().stop();
                frame.setGlassPane(glassPane);
                frame.validate();
                progresspane.stop();

                
                File realFile       =   DirectoryManager.getRealImageFile();
                //ImageViewer.getInstance().resetImageSettings();
                ImageViewer.getInstance(). loadAndSetImageFile(realFile);
                AllViewers.showImageViewer();

            }
          }
    }

     public class LoadVarianImage  implements Runnable{
        Thread runThread                =   null;
        File src                        =   null;

    public  LoadVarianImage(File asrc){
        src                     =   asrc;
   }

    public  void       reset(){
        runThread                   =   null;
        src                         =   null;
    }
    public  void       execute(){
      this.runThread    =   new Thread(this);
      this.runThread.start();
   }

    public void  run(){

       try {

             copyAndLoadVarianBinaryImage (src);

        } catch (FileNotFoundException ex) {
              ex.printStackTrace();
               VarianBinaryConverter.setInfo("Error while loading file.\n" +
                         "Error message: " + ex.getMessage());
               return;

        } catch (IOException ex) {
            ex.printStackTrace();
             VarianBinaryConverter.setInfo("Error while loading file.\n" +
                         "Error message: " + ex.getMessage());
             return;

        } catch (Exception ex){
            ex.printStackTrace();
             VarianBinaryConverter.setInfo("Error while loading file.\n" +
                         "Error message: " + ex.getMessage());
             return;
        }
        finally{
            reset();
        }
   }

    public boolean isRunning(){
        if (runThread  == null) {return false;}
        else {
            return runThread.isAlive();
        }
    }



}
}
