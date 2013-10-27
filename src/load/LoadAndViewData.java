/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package load;
import bayes.PackageManager;
import bayes.BayesManager;
import bayes.DirectoryManager;
import utilities.*;
import java.io.*;
import javax.swing.*;
import java.util.List;

import interfacebeans.AllViewers;
import fid.FidViewer;
import fid.FileFilters;
import fid.Procpar;
import static bayes.Enums.*;
import fid.FidModelViewer;
import image.*;
import applications.model.*;
import applications.bayesAnalyze.BayesAnalyze;
import image.siemens.SiemensTo4fdpConverter;
import java.util.Scanner;
import image.siemens.SiemensToVarianConverter;
import image.varian.VarianTo4fdpConverter;

public class LoadAndViewData {
    public static File loadAbscissaFileDir             =   null;
    public static File loadAbscissaFromProcparDir      =   null;


    private LoadAndViewData() {}


    public static void loadFid(File dir){
          boolean canload = FidViewer.getInstance().isLoading();
          if(canload == false){return;}

          if (dir.isDirectory() == false){
               DisplayText.popupMessage( "You must load DIRECTORY that contains fid file.");
                return;
           }

           boolean isValidDir  =  DirectoryManager.isValidFidDir(dir);

            if (isValidDir == false){
                 DisplayText.popupMessage(
                 "You must select directory that contains\n"+
                 "fid, procpar and text files");
                return;
            }

            File procparFile =  DirectoryManager.getProcparFile(dir);
            boolean isImage  =  Procpar.isImage(procparFile);

           if (isImage == true){
                 DisplayText.popupMessage(
                 "Selected fid is an image.\n"+
                 "Spectroscopy fid is required.\n"+
                 "Exit load.");
                return;
            }

        /* 
         * Delete all files in the fid firectory and unload data
         *
         */

        FidViewer.getInstance().unloadData();

        LoadAndViewData. copyFidDir(dir );       // copy files to exp/fid dir

        // Make sure the old FFH file is deleted
        File ffhFile            =   DirectoryManager.getFidDesciptorFile();
        if (ffhFile.exists()){ffhFile.delete();}

        FidViewer fv            =   FidViewer.getInstance();
        boolean loadByUser      =   true;
        File fidDir             =   DirectoryManager.getFidDir ();
        fv.loadData     (   fidDir,loadByUser );
        AllViewers.showFidViewer();
        DirectoryManager.startDir            = dir.getParentFile();
    }
    public static void loadStandardBinaryImage (File [] files){
        File imgDir             =   DirectoryManager.getImageDir() ;
        ImageDescriptor id      =   null;

       // make sure image directory file exists
       if (imgDir.exists() == false){imgDir.mkdirs();}



        File    ifhFileSrc              =   null;
        File    imgFileDst              =   null;
        File    ifhFileDst              =   null;
      
       for (File src : files) {
               if (src.isDirectory() == true ) {
                        DisplayText.popupMessage(  "You selected a directory.\n" +
                                                    "Must load file that ends with .img");
                                                    return;
                }
                ifhFileSrc              =   DirectoryManager.getIfhFileForImage(src);
                imgFileDst              =   new File(imgDir, src.getName());
                ifhFileDst              =   new File(imgDir, ifhFileSrc.getName());


             try {
                 if (ifhFileSrc.exists() == false){
                    String msg = String.format("IFH file %s is not found.", ifhFileSrc.getPath());
                    throw new FileNotFoundException (msg );
                 }

                id                      =   ImageIO.loadFromDisk(ifhFileSrc);
                boolean isBigEndian     =   id.isBigEndian();

                if (isBigEndian ){
                      IO.copyFile(src, imgFileDst);
                      IO.win2unixFileCopy(ifhFileSrc, ifhFileDst );

                }
                else{
                    ImageIO.convetLittleToBigEndian(src, imgFileDst, id, ifhFileDst);
                }


            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return;

            }catch (IOException ex) {
                ex.printStackTrace();
                return;
            }

        }

         // if abscissa file is present
                File firstImgFile       =   files[0];
                File srcDir             =   firstImgFile.getParentFile();
                File abscissaSrc        =   new File(srcDir,  BayesManager.ABSCISSA_FILE_NAME);
                File abscissaDst        =   DirectoryManager.getAbscissaFile();

                // Check if upper(Abscissa) or lower case(abscissa) files exist.
                if (abscissaSrc.exists()  == false){
                     abscissaSrc        =   new File(srcDir,  BayesManager.aBSCISSA_FILE_NAME);
                }

                if (abscissaSrc.exists()){
                            IO.win2unixFileCopy(abscissaSrc, abscissaDst);
                }

               // ImageViewer.getInstance().resetImageSettings();
                //ImageViewer.getInstance().loadDefaultFile();

                ImageViewer.getInstance().loadAndSetImageFile(imgFileDst);
                AllViewers.showImageViewer();
    }

    public static void loadAsciiImage (File  src){
        Ascii2ImageConverter converter        =     Ascii2ImageConverter.getInstance();
        boolean isSuccess                     =     converter.readAndWrite(src);
        if (isSuccess == false) {return;}

        if ( converter.isLoadAbscissa()){
              loadAbscissa(converter.getAbscissaFile());
        }

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        AllViewers.showImageViewer();
    }
    public static void loadMulitColumnsAsciiImage (File  src){
        Ascii2ImageConverter converter        =     Ascii2ImageConverter.getInstance();
        boolean isSuccess                     =     converter.readAndWriteMultipleColumns(src);

        if (isSuccess == false) {return;}

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        //ImageViewer.getInstance().loadAndSetImageFile(src);
        AllViewers.showImageViewer();
    }
    public static void loadImaImage (File [] src){

        boolean isSuccess =      SiemensTo4fdpConverter.writeImagesFromMultiIma(src);
        if (isSuccess == false) {return;}

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        AllViewers.showImageViewer();
    }
    public static void loadFdfImage (File src){

        boolean isSuccess =      VarianTo4fdpConverter.convertImagesFromFdf(src);
        if (isSuccess == false) {return;}

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        AllViewers.showImageViewer();
    }



    public static void loadAsciiFid(File  src){
        doClenupPriorToFidLoad();

        Ascii2FidConverter converter        =  new Ascii2FidConverter();
        //Ascii2ImageConverter converter        =   Ascii2ImageConverter.getInstance();
        boolean isSuccess = converter.readAndWrite(src);

        if (isSuccess == false) {return;}


        FidViewer.getInstance().loadData(DirectoryManager.getFidDir(), true);
        AllViewers.showFidViewer();
    }
    public static void loadAsciiImageFid(File  src){
        File imgFidDir                           =   DirectoryManager.getImageFidDir() ;
        if (imgFidDir.exists() == false){imgFidDir.mkdirs();}

        // make sure image directory file exists
        File imgDir             =   DirectoryManager.getImageDir() ;
        if (imgDir.exists() == false){imgDir.mkdirs();}

        Ascii2FidImageConverter converter        =  new Ascii2FidImageConverter();
        boolean isSuccess = converter.readAndWrite(src, imgFidDir );

        if (isSuccess == false) {return;}

        VarianBinaryConverter varian2imageConverter = new VarianBinaryConverter();
        varian2imageConverter.loadImage(imgFidDir );
        AllViewers.showImageViewer();
    }
    public static void loadDicomImage(File  src){
        Dicom2ImgConverter converter        =  new Dicom2ImgConverter();
        boolean isSuccess                   =   converter.readAndWrite(src);

        if (isSuccess == false) {return;}

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        AllViewers.showImageViewer();
    }
    public static void loadDicomImages(File  srcDir){
        Dicom2ImgConverter converter        =  new Dicom2ImgConverter();
        File [] files                       =  srcDir.listFiles(
                                                new utilities.BayesFileFilters. DYCOMFileFilter()) ;

        if(files == null || files.length==0) {
            DisplayText.popupMessage("No dicom files are found\n" +
                                     "in the selected directory.\n" +
                                     "Exit.");
            return;

        }

     
        boolean isSuccess                   =   converter.readAndWrite(files );

        if (isSuccess == false) {return;}

        ImageViewer.getInstance().resetImageSettings();
        ImageViewer.getInstance().loadDefaultFile();
        AllViewers.showImageViewer();
    }
    public static void loadSiemensRDAFid(File  src){
        doClenupPriorToFidLoad();

        boolean isSuccess =     SiemensToVarianConverter.writeFidFilesFromRda(src);
        if (isSuccess == false) {return;}


        FidViewer.getInstance().loadData(DirectoryManager.getFidDir(), true);
        AllViewers.showFidViewer();
    }
    public static void loadSiemensRAWFid(File  src){
        doClenupPriorToFidLoad();

        boolean isSuccess =     SiemensToVarianConverter. writeFidFilesFromRaw(src);
        if (isSuccess == false) {return;}


        FidViewer.getInstance().loadData(DirectoryManager.getFidDir(), true);
        AllViewers.showFidViewer();
    }


  
    public static boolean   loadAbscissa(File src)  {
        if (src == null || src.exists() == false){
            return false;
        }

        File dst                    =   DirectoryManager.getAbscissaFile ();
        boolean isCopied            =   IO.win2unixFileCopy(src, dst);

        if (isCopied){
             AllViewers.showAbscissa();
        }
        else{
            return false;
        }

        return true;
    }
    public static boolean loadAbscissaFromProcpar()  {
        JFileChooser fc             = new JFileChooser (loadAbscissaFromProcparDir);
        fc.setMultiSelectionEnabled (false);
        fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Specify Procpar File");
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter( new BayesFileFilters.ProcparFileChooserFilter());

        int returnVal = fc.showOpenDialog(null);

        File procparFile = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                procparFile = fc.getSelectedFile();
                loadAbscissaFromProcparDir  = procparFile.getParentFile();

         } else { return false;  }


        Procpar procpar =   new Procpar(procparFile);
/*
        if (procpar.isImage() == false){
            String message     =   String.format(
                            "Procpar %s  \n" +
                            "is not an image procpar file.\n" +
                            "Exit prior assignments...",
                            procparFile.getPath());
            DisplayText.popupErrorMessage(message);

        }

*/
        boolean isDone = writeAbscissaFile (procpar);

        if (isDone){  AllViewers.showAbscissa();}
        else {
            String message  = String.format("Failed to load abscissa file from procpar\n" +
                                            "%s file. ",procparFile.getPath());
            DisplayText.popupErrorMessage(message);}
       


        return true;
    }

    public static boolean writeAbscissaFile( Procpar procpar, File file ) {
       boolean done                       =   false;
       try{
         String abscissa                    =    procpar.getXDAbsciss();
         if (abscissa  == null) { return done;}

         List <Integer> fieldMapMask        =    procpar.getFieldMap();

         Scanner scanner                    =   new Scanner(abscissa);
         StringBuffer sb                    =   new StringBuffer();

         int count                          =   0;
         while(scanner.hasNextLine()){
            String line = scanner.nextLine();


            // make sure count is incremented as soon as it is used!
            int fieldMap            =   fieldMapMask.get(count);
            count +=1;

            boolean isMap           =   Procpar.isMap(  fieldMap);

            if ( isMap )        {  continue; }
            if (fieldMap  < 1)  {continue;}


            sb.append(line);
            sb.append("\n");

           

         }
           done = utilities.IO.writeFileFromString(sb.toString(), file);
    }catch (Exception exp){ exp.printStackTrace();
    }finally{ return done;}


 }
    public static boolean writeAbscissaFile( Procpar procpar ) {
         File abscissafile              =    DirectoryManager.getAbscissaFile();
         return writeAbscissaFile(procpar,  abscissafile);
      }




    public static void loadBrukerFid(File fidfile){
        Bruker2VarianFidConverter.readAndWrite(fidfile);
        FidViewer.getInstance(). loadDefaultFid();
     }


    public static void      copyFidDir(File src) {
        
        // assign destination directories
        File fidDir             = DirectoryManager.getFidDir();
        File modelDir           = DirectoryManager.getFidModelDir();
        File bayesAnalizedir    = DirectoryManager.getBayesAnalyzeDir();
        
        // make sure destination directories exist
        DirectoryManager.doAnalaysisDir ();
        
        // completely clean fid,fidModel  and bayes other analysis directories
        //prior to copying
        IO.emptyDirectory(fidDir);
        IO.deleteDirectory(modelDir);
        IO.emptyDirectory(bayesAnalizedir);

        FidModelViewer.getInstance().unloadData();
        
        // partially clean ascii dir prior to clean
        
        FileFilter filter   = new FileFilters.FilesToBeCopyToFidDir();
        File []   files     =  src.listFiles(filter);
        for (File file :files) {

            String filename =    file.getName();
            File dist       = new File(fidDir, filename);

            if (filename.equalsIgnoreCase(BayesManager.PROCPAR_FILE_NAME)){
                fid.Procpar.overwriteFileSource(file, dist);
            }
            else{
                IO.copyFile(file, dist);
            }
        }



        if ( isCopyBayesAnalyzeFiles() == true){
            filter          =   new FileFilters.FileNameStartsWithBayes();
            files           =   src.listFiles(filter);
            
            for (File file :files)
            {
                File dist   =   new File(bayesAnalizedir, file.getName());
                IO.copyDirectory(file, dist);

            }
        }
       
        
    }
    public static boolean  isCopyBayesAnalyzeFiles(){
        Model model  = PackageManager.getCurrentApplication();
        boolean copyBayesAnalyze = true;
        if (model != null && model instanceof FidModel) {
              if((model instanceof BayesAnalyze) == false){
                   copyBayesAnalyze = false;
               }
        }
        return copyBayesAnalyze;

    }
    public static void      doClenupPriorToFidLoad(){
         // assign destination directories
        File fidDir             = DirectoryManager.getFidDir();
        File modelDir           = DirectoryManager.getFidModelDir();
        File bayesAnalizedir    = DirectoryManager.getBayesAnalyzeDir();

        // make sure destination directories exist
        DirectoryManager.doAnalaysisDir ();

        // completely clean fid,fidModel  and bayes other analysis directories
        //prior to copying
        IO.emptyDirectory(fidDir);
        IO.deleteDirectory(modelDir);
        IO.emptyDirectory(bayesAnalizedir);
        FidModelViewer.getInstance().unloadData();
    }
 

    // deletes all files except data files that are protected by dataFilter
    public static void clearBayesOtherAnalysisDirectory(boolean doUseFilter) {
        File dir            = DirectoryManager.getBayesOtherAnalysisDir();
        if (dir.exists() == false) {return ;}
        
        File[] children;
        if (doUseFilter){ // keep data files
             List <File> dataFiles =  DirectoryManager.getAsciiDataAndIFHFileListOnDisk();
             children = dir.listFiles( new  utilities.BayesFileFilters.AsciiFileFilter (dataFiles ));
        }
        
        else {  // don't keep data files
            children = dir.listFiles();
        }
        
        if (children != null) {
              for (File file : children) { 
                  if (file.isDirectory()){
                        IO.deleteDirectory(file);
                  }
                  else{
                    file.delete();
                  }
              }
        }
      
        
    }
    public static void clearImageDirExceptForLoadedImagesAndAbscissaFIle()  {
        File dir                    =   DirectoryManager.getImageDir();
        if (dir.exists() == false) {return ;}
       
        List <File> filesToKeep   =   DirectoryManager.getListOfLoadedImageFiles();
        File abscissaFile         =   DirectoryManager.getAbscissaFile();
        filesToKeep.add(abscissaFile );
        
        File [] files               =   dir.listFiles();

        for (File file : files) {
            if (filesToKeep.contains(file) == false){
                if (file.isDirectory()){
                    IO.deleteFielsAndDirsInDirectory(file);
                }
                else {
                    file.delete();
                }
            }
        }

        ImageViewer.getInstance().updateImageList();
    }
    public static void cleanMetaboliteFiles(File dir,  METABOLITE_FILE_TYPE type){
         FileFilter filter ;
         if (type == METABOLITE_FILE_TYPE.IS0){
            filter =   new BayesFileFilters.ISOFileFilter();
         }
         else {
             filter =   new BayesFileFilters.ResFileFilter();
         }
         File [] fileList   =   dir.listFiles(filter);
               
        for (File file : fileList) {
            file.delete();
        }
     }
   
    
  
   





}
