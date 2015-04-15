/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import bayes.DirectoryManager;
import image.ImageViewer;
import bruker.Bruker2DSeqFileFilter;
import bruker.SeqReader;
import bruker.SeqStackReader;
import image.commonImage.commoneImageFileFilter;
import image.commonImage.commonImageHandler;
import image.raw.BinaryReader;
import image.raw.RawImageDialog;
import interfacebeans.AllViewers;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import utilities.BayesFileFilters;
import utilities.DisplayText;

/**
 *
 * @author apple
 */
public class DataLoader {
    public static JFileChooser dataFileChooser                           =    new JFileChooser();
    public static JFileChooser fidFileChooser                            =   load.gui.FidFileChooser.getInstance();

    public static int FILECHOOSER_INTI_WIDTH                             =  700;
    public static int FILECHOOSER_INTI_HEIGHT                            =  450;
    
   public final static Cursor busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
   public final static Cursor defaultCursor = Cursor.getDefaultCursor();
    static{
         dataFileChooser.setCurrentDirectory(DirectoryManager.startDir);
         dataFileChooser .setPreferredSize(new Dimension (FILECHOOSER_INTI_WIDTH ,  FILECHOOSER_INTI_HEIGHT ));
         fidFileChooser.setPreferredSize(new Dimension (FILECHOOSER_INTI_WIDTH ,  FILECHOOSER_INTI_HEIGHT ));
    }

    public static JFileChooser getAsciiDataFileChooser(){
       dataFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
       dataFileChooser.setMultiSelectionEnabled(true);
       dataFileChooser.setAcceptAllFileFilterUsed(true);
       dataFileChooser.setDialogTitle("Load Ascii File");
       return dataFileChooser ;

    }
    public static JFileChooser getImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(true);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        dataFileChooser.setFileFilter( new utilities.BayesFileFilters. ImageFileChooserFilter ());
        dataFileChooser.setDialogTitle("Load .img Image");
        return dataFileChooser ;

    }
    public static JFileChooser getNiftiImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(true);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        dataFileChooser.setFileFilter( new utilities.BayesFileFilters. NiftiFileChooserFilter ());
        dataFileChooser.setDialogTitle("Load .nii Nifti Image");
        return dataFileChooser ;

    }
    public static JFileChooser getRawBinaryFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Raw Binary Image");

        return dataFileChooser ;

    }
    public static JFileChooser getCommonImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setFileFilter(new commoneImageFileFilter());
        dataFileChooser.setDialogTitle("Load Raw Binary Image");

        return dataFileChooser ;

    }
    public static JFileChooser getBrukerd2SeqFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setFileFilter(new Bruker2DSeqFileFilter());
        dataFileChooser.setDialogTitle("Load Bruker 2dseq Image");
        return dataFileChooser ;

    }
    public static JFileChooser getBrukerd2SeqStackFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(true);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.DIRECTORIES_ONLY);
        dataFileChooser.setFileFilter(new Bruker2DSeqFileFilter());
        dataFileChooser.setDialogTitle("Load Bruker 2dseq Image Stack");
        return dataFileChooser ;

    }
    public static JFileChooser getSingleColAsciiImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Single-Column Text File Of 4dfp image");
        return dataFileChooser ;
    }
    public static JFileChooser getMultiColAsciiImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Multi-Column Text File Of 4dfp image");
        return dataFileChooser ;
    }
    public static JFileChooser getAbscissaFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setFileFilter(dataFileChooser.getAcceptAllFileFilter());
        dataFileChooser.setDialogTitle("Load Abscissa File");

        return dataFileChooser ;
    }
    public static JFileChooser getSiemensRdaFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setFileFilter( new BayesFileFilters.SiemensRDAFileFilter ());
        dataFileChooser.setDialogTitle("Load Siemens RDA File");

        return dataFileChooser ;
    }
    public static JFileChooser getSiemensRawFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setFileFilter( new BayesFileFilters.SiemensRAWFileFilter());
        dataFileChooser.setDialogTitle("Load Siemens RAW File");

        return dataFileChooser ;
    }
    public static JFileChooser getTextFidFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Text File Of Spectroscopy Fid");
        return dataFileChooser ;
    }
    public static JFileChooser getTextFidImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Text File Of Image Fid");
        return dataFileChooser ;
    }
    public static JFileChooser getImaImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(true);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setFileFilter(new BayesFileFilters.SiemensImaFileFilter());
        dataFileChooser.setDialogTitle("Load Siemens IMA image");
        return dataFileChooser ;

    }
    public static JFileChooser getFdfImageFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        dataFileChooser.setFileFilter( new BayesFileFilters.VarainFdfFileFilter());
        dataFileChooser.setDialogTitle("Load Varian FDF image");
        return dataFileChooser ;
    }
    public static JFileChooser getDicomFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
         dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load DICOM Image(s)");
        return dataFileChooser ;

    }
    
      public static JFileChooser getBrukerFileChooser(){
        dataFileChooser.setMultiSelectionEnabled(false);
        dataFileChooser.setFileSelectionMode (javax.swing.JFileChooser.FILES_ONLY);
        dataFileChooser.setAcceptAllFileFilterUsed(true);
        dataFileChooser.setDialogTitle("Load Bruker FID)");
        return dataFileChooser ;
    }

    public static void  loadAscii(){
        JFileChooser fc         =   getAsciiDataFileChooser();
        int returnVal           =  fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir =  fc.getCurrentDirectory();

                File []  files     =   fc.getSelectedFiles ();
                ascii.ASCIIDataViewer.loadFiles(files);
                AllViewers.showAsciiViewer();
        }
        else {
            return;
        }


    }
    public static void  loadSpectralFid(){
        fidFileChooser .setCurrentDirectory( dataFileChooser.getCurrentDirectory());
        int returnVal = fidFileChooser  .showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads\
                File curDir                 =   fidFileChooser .getCurrentDirectory();
                DirectoryManager.startDir = curDir ;
                dataFileChooser.setCurrentDirectory(curDir );

                File file           =   fidFileChooser.getSelectedFile ();
                LoadAndViewData.loadFid(file);
        }
        else {
            return;
        }
    }
    public static void  loadImgBinary(){
        JFileChooser fc         =    getImageFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File [] files               = fc.getSelectedFiles();
                LoadAndViewData.loadStandardBinaryImage(files);
        }
    }
    public static void  loadNifti(){
        JFileChooser fc         =    getNiftiImageFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                //File [] files               = fc.getSelectedFiles();
                File file               = fc.getSelectedFile();
                LoadAndViewData.loadNiftiImage(file);
        }
    }
    public static void  loadGeneralBinary(){
        JFileChooser fc         =    getRawBinaryFileChooser();

        int returnVal           =   fc.showOpenDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        // remember for future loads
        DirectoryManager.startDir       = fc.getCurrentDirectory();

        File file                       = fc.getSelectedFile();
        BinaryReader binReader          = new   BinaryReader();
        binReader.setSourseFile(file);

         RawImageDialog dialog          =    RawImageDialog.getInstance();
         dialog.setFileSize(file.length());
         dialog.setConverter(binReader);
         dialog.setVisible(true);

         if (dialog.isLoad() == false){
             return;
         }
         binReader.read(file);
         if ( binReader.isLoaded() == false){
                String errror = String.format(
                        "Error encountered while trying to read binary file\n"
                        + "%s\n"
                        + "Error  description is: %s\n."
                        + "Exiting... "
                        ,
                        file.getPath(),
                         binReader.getErrorMessage());
                    DisplayText.popupErrorMessage(errror);
                return;
          }


             boolean isWrite                 =     binReader.writeImages();

             if (isWrite  == true) {
                ImageViewer.getInstance().resetImageSettings();
                ImageViewer.getInstance().loadDefaultFile();
                AllViewers.showImageViewer();

            }

    }
     public static void  loadCommonImage(){
        JFileChooser fc         =    getCommonImageFileChooser();

        int returnVal           =   fc.showOpenDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        // remember for future loads
        DirectoryManager.startDir       = fc.getCurrentDirectory();

        File file                       = fc.getSelectedFile();
        commonImageHandler jpgHandler          =   new commonImageHandler();

        boolean isWrite                 =     jpgHandler.handleHpegFile(file);

             if (isWrite  == true) {
                ImageViewer.getInstance().resetImageSettings();
                ImageViewer.getInstance().loadDefaultFile();
                AllViewers.showImageViewer();

            }

    }
    public static void  loadBruker2dseq(){
        JFileChooser fc         =    getBrukerd2SeqFileChooser();
        int returnVal           =   fc.showOpenDialog(null);

        if (returnVal!= JFileChooser.APPROVE_OPTION) {
            return;
        }
        // remember for future loads
        DirectoryManager.startDir       = fc.getCurrentDirectory();

        File file                       =  fc.getSelectedFile();
        BinaryReader reader             =  SeqReader.creatBinaryReader(file);;
        if (reader == null){
             return;
        }

        reader.setSourseFile(file);
        reader.read(file);
        if (reader.isLoaded() == false){
              String errror = String.format(
                        "Error encountered while trying to read binary file\n"
                        + "%s\n"
                        + "Error  description is: %s\n"
                        + "Exiting... "
                        ,
                        file.getPath(),
                        reader.getErrorMessage());
                DisplayText.popupErrorMessage(errror);
             return;
        }
         boolean isWrite                 =     reader.writeImages();


        if (isWrite  == true) {
            ImageViewer.getInstance().resetImageSettings();
            ImageViewer.getInstance().loadDefaultFile();
            AllViewers.showImageViewer();

        }


       }
    public static void  loadBruker2dseqStack(Component cursorComp){
        JFileChooser fc                 =    getBrukerd2SeqStackFileChooser();
        int returnVal                   =    fc.showOpenDialog(null);

        if (returnVal!= JFileChooser.APPROVE_OPTION) {
            return;
        }
        // remember for future loads
         File rootDir                   =   fc.getCurrentDirectory();
         DirectoryManager.startDir      =   rootDir ;

       
        File [] dirs                    =   fc.getSelectedFiles();
       // File file                       =   null;
        List<File> files                =   null;
        SeqStackReader stackreader      =   null;
        boolean isWrite                 =   false;
        files                           =   Arrays.asList(dirs);
        stackreader                     =   new  SeqStackReader(rootDir ,files);
       /*
        if (dirs.length == 1){
             file                       =   fc.getSelectedFile();
             stackreader                =   new  SeqStackReader(file);
        }
        else{
            files                       =   Arrays.asList(dirs);
            stackreader                 =   new  SeqStackReader(rootDir ,files);
        }
        * 
        */
        try{
         cursorComp.setCursor(busyCursor );
        BinaryReader reader             =   stackreader.read();
        isWrite                 =   reader.writeImages();
        }
        finally{
               cursorComp.setCursor(defaultCursor );
        }
       

        if (isWrite  == true) {
            ImageViewer.getInstance().resetImageSettings();
            ImageViewer.getInstance().loadDefaultFile();
            AllViewers.showImageViewer();
        }

       }
    public static void  loadSingleCoumnAsciiImage(){
        JFileChooser fc                 =    getSingleColAsciiImageFileChooser();
        int returnVal                   =   fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir = fc.getCurrentDirectory();

                File file           =fc.getSelectedFile();
                LoadAndViewData. loadAsciiImage(file);

       } else {
                return;
        }
        
    }
    public static void  loadMultiCoumnAsciiImage(){
        JFileChooser fc                 =    getMultiColAsciiImageFileChooser();
        int returnVal                   =   fc.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir = fc.getCurrentDirectory();

                File file           =   fc.getSelectedFile();
                LoadAndViewData. loadMulitColumnsAsciiImage(file);

       } else {
                return;
       }
        
    }

    public static void  loadSiemenesRdaFid(){
        JFileChooser fc         =    getSiemensRdaFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                LoadAndViewData.loadSiemensRDAFid(file);
        }
    }
    public static void  loadSiemenesRawFid(){
        JFileChooser fc         =    getSiemensRawFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                LoadAndViewData.loadSiemensRAWFid(file);
        }
    }
   public static void  loadBrukerFid(){
        JFileChooser fc = getBrukerFileChooser();
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

              // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();
                
                File file = fc.getSelectedFile();
                LoadAndViewData.loadBrukerFid(file);

        }
    }
    public static void  loadTextFid(){
        JFileChooser fc         =    getTextFidFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                LoadAndViewData. loadAsciiFid(file);
        }
    }
    public static void  loadTextFidImage(){
        JFileChooser fc         =     getTextFidImageFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                LoadAndViewData.  loadAsciiImageFid(file);
        }
    }
    public static void  loadImaImage(){
        JFileChooser fc         =     getTextFidImageFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                 File [] files           = fc.getSelectedFiles();
                 LoadAndViewData.loadImaImage(files);
        }
    }
    public static void  loadFdfImage(){
        JFileChooser fc         =     getTextFidImageFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                LoadAndViewData.loadFdfImage(file );
        }
    }
    public static void  loadDicom(){
        JFileChooser fc         =     getDicomFileChooser();
        int returnVal           =    fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir   = fc.getCurrentDirectory();

                File file           = fc.getSelectedFile();
                if (file.isDirectory()){LoadAndViewData.  loadDicomImages(file);}
                else{ LoadAndViewData.  loadDicomImage(file); }

       }

    }

    public static void  loadAbscissa()  {
        try{
            JFileChooser fc             =    getAbscissaFileChooser();

            int returnVal               =   fc .showOpenDialog (null);
            if (returnVal               ==   JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir = fc .getCurrentDirectory();

                File abscissafile       =    fc.getSelectedFile ();
                LoadAndViewData.loadAbscissa(abscissafile);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
      

    }
    public static File  assignAbscissaFile(File startdir)  {
          JFileChooser fc             =    getAbscissaFileChooser();
         File abscissafile            =   null;
         try{

            int returnVal               =   fc.showOpenDialog (null);
            if (returnVal               ==   JFileChooser.APPROVE_OPTION) {
                // remember for future loads
                DirectoryManager.startDir = fc.getCurrentDirectory();

                abscissafile       =    fc.getSelectedFile ();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            return abscissafile;
        }
         
    }
}
