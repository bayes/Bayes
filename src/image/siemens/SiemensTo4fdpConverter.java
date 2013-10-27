/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.siemens;

import image.ImageDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import load.ImageConvertHelper;
import utilities.DisplayText;

/**
 *
 * @author apple
 */
public class SiemensTo4fdpConverter {
     public static boolean writeImagesFromIma( File src){
         if (src.isDirectory()){
             return writeImagesFromMultiIma(src);
         }
         else {
             return writeImagesFromSingleIma(src);
         }
    }
     public static boolean writeImagesFromMultiIma( File src){
         File [] sources                =   src.listFiles( new ImaFileFilter  ());
         return  writeImagesFromMultiIma(sources  );
    }
     public static boolean writeImagesFromMultiIma(  File [] sources    ){
         List <Ima> validImages          =   new ArrayList<Ima>();
         List <File> nonvalidImages      =   new ArrayList<File>();
         for (File source : sources) {

                if (isValidImaFile(source ) == false){
                    nonvalidImages.add(source);
                    continue;
                }
                Ima ima                          =  new  Ima();
                ima.read(source);
                if(ima.isLoaded() == true)  { validImages.add    (ima);}
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
            return writeImagesFromIma( validImages );
         }
    }
     public static boolean writeImagesFromSingleIma( File  source){
         Ima ima                          =  new  Ima();
         ima.read(source);
         if(ima.isLoaded() == false){
             String path            =   source.getPath();
             String er              =   ima.getErrorMessage();

             String message = String.format(
                                    "Failed to read ima image\n" +
                                    "%s .\n" +
                                    "%s ."
                                    ,path, er);
             DisplayText.popupErrorMessage(message);
             return false;}
         else{
            return writeImagesFromIma(ima);
         }
    }

     public static boolean writeImagesFromIma(Ima ima){
        ImageDescriptor  descriptor     =   ima2ImageDescriptor(ima);
        List<float[][]>  images         =   new ArrayList<float[][]>();
        images  .add(ima.getPixels());
        String srcName                  =   ima.getSourseFile().getName();

        boolean isWrite                 =  ImageConvertHelper.writeImgFiles(
                                                    srcName ,images , descriptor);
        return isWrite;
    }
     public static boolean writeImagesFromIma(List <Ima> validImages ){
        Ima etalon                      =    validImages.get(0);
        ImageDescriptor  descriptor     =   ima2ImageDescriptor(etalon );

        List<float[][]>  images         =   new ArrayList<float[][]>();
        for ( Ima ima :  validImages) {
                  images  .add(ima.getPixels());
        }


        descriptor.setNumberOfElements(validImages.size());
        String src                      =  etalon .getSourseFile().getName();
        boolean isWrite                 =   ImageConvertHelper.writeImgFiles(
                                                    src ,images , descriptor);
        return isWrite;
    }
     public static  ImageDescriptor  ima2ImageDescriptor(Ima ima){
         ImageDescriptor  descriptor     =   new ImageDescriptor();

         descriptor.setSourceFileName(ima.getSourseFile().getAbsolutePath());
         descriptor.setBigEndian(true);
         descriptor.setBytesPerPoint(4); // pixels will be outputted as floats
         descriptor.setNumberOfColumns(ima.getImgSize());
         descriptor.setNumberOfRows(ima.getImgSize());
         descriptor.setNumberOfElements(1);
         descriptor.setNumberOfSlices(1);
         descriptor.setScaling1(1);
         descriptor.setScaling2(1);
         descriptor.setScaling3(1);


         return  descriptor;
     }
     public static boolean isValidImaFile (File file){
        if (file.isFile() || file.getName().endsWith("."+Ima.FILE_EXT)){
            return true;
        }
        else {
            return false;
        }

     }
     public static  class ImaFileFilter    implements FileFilter {

        public boolean accept(File file) {
            if (file.getName().endsWith("."+Ima.FILE_EXT)   == true) { return true;}
            return false;
        }
    };

}
