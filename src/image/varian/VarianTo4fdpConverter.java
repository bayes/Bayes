/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;

import image.ImageDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import load.ImageConvertHelper;
import utilities.DisplayText;

/**
 *
 * @author apple
 */
public class VarianTo4fdpConverter {
     public static boolean convertImagesFromFdf(  File file  ){
         if (file == null || file.exists() == false){return false;}
         
         if (file.isDirectory()){
                return  writeImagesFromFdf(file.listFiles(new  FdfFileFilter ()), file);
         }
         else{
                return writeImagesFromFdf( file  );
         }
    }
     public static  ImageDescriptor  fdf2ImageDescriptor(Fdf fdf){
         ImageDescriptor  descriptor     =   new ImageDescriptor();

         descriptor.setSourceFileName(fdf.getSourseFile().getAbsolutePath());
         descriptor.setBigEndian(true);
         descriptor.setBytesPerPoint(fdf.getBytesPerPoint()); // pixels will be outputted as floats
         descriptor.setNumberOfColumns(fdf.getPhaseencodeSize());
         descriptor.setNumberOfRows(fdf.getReadoutSize());
        
         descriptor.setScaling1(1);
         descriptor.setScaling2(1);
         descriptor.setScaling3(1);


         return  descriptor;
     }

     /*************** For A Single FDF FILE USE THESE ROUTINES*******************/
     public static boolean writeImagesFromFdf( File  source){
         Fdf fdf                          =  new  Fdf();
         fdf.read(source);
         if(fdf.isLoaded() == false){
             String path            =   source.getPath();
             String er              =   fdf.getErrorMessage();

             String message = String.format(
                                    "Failed to read fdf image\n" +
                                    "%s .\n" +
                                    "%s ."
                                    ,path, er);
             DisplayText.popupErrorMessage(message);
             return false;}
         else{
            return writeImagesFromFdf(fdf);
         }
    }
     public static boolean writeImagesFromFdf(Fdf fdf){
        ImageDescriptor  descriptor     =   fdf2ImageDescriptor(fdf);
        List<float[][]>  images         =   new ArrayList<float[][]>();
        images  .add(fdf.getPixels());
        String srcName                  =   fdf.getSourseFile().getName();

        boolean isWrite                 =  ImageConvertHelper.writeImgFiles(
                                                    srcName ,images , descriptor);
        return isWrite;
    }


   /*************** ForMULTIPLE FDF FILES USE THESE ROUTINES*******************/
     public static boolean writeImagesFromFdf(  File [] sources  , File sourseDir  ){
         List <Fdf> validImages          =   new ArrayList<Fdf>();
         List <File> nonvalidImages      =   new ArrayList<File>();


         // sort files lexicographically
         Arrays.sort(sources);
         for (File source : sources) {
                Fdf fdf                          =  new  Fdf();
                fdf.read(source);
                if(fdf.isLoaded() == true)  { validImages.add    (fdf);}
                else                        { nonvalidImages.add (source);}

         }

         if (nonvalidImages.isEmpty() == false){
             String message = "Failed to load following images:\n";
             for (File f: nonvalidImages) {
                String path            =   f.getPath();
                message                =    message + path + "\n";
            }
             DisplayText.popupErrorMessage(message);
         }
         if (validImages.isEmpty() == true){
            return false;
         }

         else{
            return writeImagesFromFdf( validImages ,  sourseDir);
         }
    }
     public static boolean writeImagesFromFdf(List <Fdf> fdfs , File sourceDir){
        Fdf etalon                      =       fdfs .get(0);
        int nSlices                     =       etalon.getSliceSize();
        int nElems                      =       etalon.getElementSize();
        int nImages                     =       nSlices*nElems;

        if (nImages   !=  fdfs .size()){
            String error = String.format(   "Number of valid fdf images (%s) must\n" +
                                            "be equal to number of slises (%s)\n" +
                                            "times number of elements(%s).\n" +
                                            "Error. Abort load...",
                                            fdfs .size(), nSlices  , nElems );
            DisplayText.popupErrorMessage(error);
            return false;
        }


        ImageDescriptor  descriptor     =   fdf2ImageDescriptor(etalon );

        List<float[][]>  images         =   new ArrayList<float[][]>();
        for (int curElem = 0; curElem < nElems; curElem++) {
            for (int curSlice = 0;  curSlice < nSlices;  curSlice++) {
              int index     = curElem + curSlice*nElems;
               images  .add(fdfs.get(index).getPixels());
             //  System.out.println(index + " "+fdfs.get(index).getSourseFile().getName());
        }

        }
     

        descriptor.setNumberOfElements(nElems);
        descriptor.setNumberOfSlices(nSlices );
        descriptor.setSourceFileName(sourceDir.getPath());

        boolean isWrite                 =   ImageConvertHelper.writeImgFiles(
                                                    sourceDir.getName() ,images , descriptor);
        return isWrite;
    }


     public static  class FdfFileFilter    implements FileFilter {
        public boolean accept(File file) {
            if (file.isFile() && file.getName().endsWith(Fdf.FILE_EXTENSION)   == true) { return true;}
            return false;
        }
    };

}
