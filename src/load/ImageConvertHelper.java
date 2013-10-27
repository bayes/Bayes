/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.PackageManager;
import bayes.DirectoryManager;
import fid.Procpar;
import image.varian.VarianFidImage;
import java.io.*;
import utilities.*;
import image.*;
import java.util.*;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
/** *
 * @author apple
 */
public class ImageConvertHelper {


    public static boolean writeImgFiles(String sourceFile, List<float[][]> allimages,ImageDescriptor imgDescriptor){
        File imgDir             =   DirectoryManager.getImageDir() ;

       // make sure image directory file exists
       if (imgDir.exists() == false){imgDir.mkdirs();}


        File imgDst             =   new File(imgDir,  DirectoryManager.addImgExtention(sourceFile));
        File ifhDst             =   DirectoryManager.getIfhFileForImage(imgDst);



        // write image file
          try {
            ImageIO.writeBinaryFile(allimages, imgDescriptor, imgDst);
            ImageIO.storeToDisk(  imgDescriptor, ifhDst );
        } catch (FileNotFoundException ex) {
            String message = String.format("File not found exception.\n" +
                                            "%s\n"+
                                            "Exit.\n", ex.getMessage());
            DisplayText.popupMessage(message);
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            String message = String.format("IOException is thrown.\n" +
                                            "%s\n"+
                                            "Exit.\n", ex.getMessage());
            DisplayText.popupMessage(message);
            ex.printStackTrace();
            return false ;
        }


        return true;
    }

    /* populates array elements prior to sclices
     *      input                          output
     * 1dim     =   elements        1dim     =   elements
     * 2dim     =   slices          2dim     =   slices
     * 3dim     =   readout         3dim     =   readout
     * 4dim     =   phaseencode     4dim     =   phaseencode
    */
    public static  List<float[][]>populateESRP(  float [] values, ImageDescriptor imgDescriptor) {
        List<float[][]> allImages  =    new Vector<float[][]>();
        int nElm                   =   imgDescriptor.getNumberOfElements();
        int nSlc                   =   imgDescriptor.getNumberOfSlices();
        int nReadout               =   imgDescriptor.getNumberOfReadouts();
        int nPhaseEncode           =   imgDescriptor.getNumberOfPhaseEncodes();

        for (int curElem = 0; curElem < nElm; curElem++) {
            for (int curSlice = 0; curSlice < nSlc; curSlice++) {
                float [][] curImage     =   new   float [nPhaseEncode ][nReadout];

                for (int curReadout = 0; curReadout < nReadout ; curReadout++) {
                    for (int curPhaseencode = 0; curPhaseencode < nPhaseEncode ; curPhaseencode++) {

                        int ind  = curElem * nSlc * nReadout*nPhaseEncode
                                    + curSlice*nReadout*nPhaseEncode
                                        + curReadout*nPhaseEncode
                                            + curPhaseencode;
                        curImage[curPhaseencode] [curReadout] =  values[ind];
                    }
                }
                 allImages.add(curImage);
            }
        }
        return  allImages;
    }



    /* populates array  slices prior to elements
     *       input                          output
     * 1dim     =   slices          1dim     =   elements
     * 2dim     =   elements        2dim     =   slices
     * 3dim     =   readout         3dim     =   readout
     * 4dim     =   phaseencode     4dim     =   phaseencode
    */
     public  static List<float[][]>populateSERP(  float [] values,  ImageDescriptor imgDescriptor) {
        List<float[][]> allImages  =    new Vector<float[][]>();
        int nElm                   =   imgDescriptor.getNumberOfElements();
        int nSlc                   =   imgDescriptor.getNumberOfSlices();
        int nReadout               =   imgDescriptor.getNumberOfReadouts();
        int nPhaseEncode           =   imgDescriptor.getNumberOfPhaseEncodes();


       /* Element lop is first because images in List<float[][]> allImages must
        * be entered in element order.
        * Howerver, when each float[][] image is populated - it is presumed
        * that pixels in ihe input float [] array are sorted as slice first.
        * Previously i have a bug here.
        * Fixed April 09/2010 by Karen Marutyan
        */

       for (int curElem = 0; curElem < nElm; curElem++) {
           for (int curSlice = 0; curSlice < nSlc; curSlice++) {
                float [][] curImage     =   new   float [nPhaseEncode ][nReadout];

                for (int curReadout = 0; curReadout < nReadout ; curReadout++) {
                    for (int curPhaseencode = 0; curPhaseencode < nPhaseEncode ; curPhaseencode++) {

                        int ind  = curSlice * nElm * nReadout*nPhaseEncode
                                    +curElem*nReadout*nPhaseEncode
                                        + curReadout*nPhaseEncode
                                            + curPhaseencode;
                        curImage[curPhaseencode] [curReadout] =  values[ind];
                    }
                }
                 allImages.add(curImage);
            }
        }
        return  allImages;
    }








    /* reshuflle list of array
     * with ESRP structure to SEPR structure
     *       input                          output
     * 1dim     =   elements       1dim     =   slices
     * 2dim     =   slices         2dim     =   elements
     * 3dim     =   readout        3dim     =   readout
     * 4dim     =   phaseencode    4dim     =   phaseencode
    */
     public  static List<float[][]>ESRP2SEPR(   List<float[][]>ESPR,  ImageDescriptor imgDescriptor) {
        List<float[][]>SEPR        =    new Vector<float[][]>();
        int nElm                   =   imgDescriptor.getNumberOfElements();
        int nSlc                   =   imgDescriptor.getNumberOfSlices();


       for (int curSlice = 0; curSlice < nSlc; curSlice++) {
            for (int curElem = 0; curElem < nElm; curElem++) {
                int index    = curElem * nSlc + curSlice;
                SEPR.add(ESPR.get(index));
            }
        }
        return  SEPR;
    }


/* reshuflle list of array
     * with ESRP structure to SEPR structure
     *       input                          output
     * 1dim     =   slices         1dim     =   elements
     * 2dim     =   elements       2dim     =   slices
     * 3dim     =   readout        3dim     =   readout
     * 4dim     =   phaseencode    4dim     =   phaseencode
    */
     public  static List<float[][]>SERP2ESPR(   List<float[][]>SEPR,  ImageDescriptor imgDescriptor) {
        List<float[][]>ESPR        =    new Vector<float[][]>();
        int nElm                   =   imgDescriptor.getNumberOfElements();
        int nSlc                   =   imgDescriptor.getNumberOfSlices();

        for (int curElem = 0; curElem < nElm; curElem++) {
             for (int curSlice = 0; curSlice < nSlc; curSlice++) {
                int index    =curSlice *nElm + curElem;
                ESPR.add(SEPR.get(index));
            }
        }
        return  ESPR;
    }



     /* populates single image
     *  readout is assumed to be contigious in memory
     */
    public static float[][]populateSingleImageRP(  float [] values, ImageDescriptor imgDescriptor) {
        int nReadout               =   imgDescriptor.getNumberOfReadouts();
        int nPhaseEncode           =   imgDescriptor.getNumberOfPhaseEncodes();
        float [][] curImage        =   new   float [nPhaseEncode ][nReadout];

                for (int curReadout = 0; curReadout < nReadout ; curReadout++) {
                    for (int curPhaseencode = 0; curPhaseencode < nPhaseEncode ; curPhaseencode++) {

                        int ind  = curReadout*nPhaseEncode +  curPhaseencode;

                        curImage[curPhaseencode] [curReadout] =  values[ind];
                    }
                }
        return  curImage;
    }



    public static boolean convertVarianFidTo4dfpBinary(VarianFidImage fidImage,String filePath) throws FileNotFoundException, IOException{
        
        

         File realFile                  =       DirectoryManager.getRealImageFile();
         File imagFile                  =       DirectoryManager.getImagImageFile();
         File absFile                   =       DirectoryManager.getAbsImageFile();

         File realIhfFile               =       DirectoryManager.getRealImageDesciptorFile ();
         File imagIhfFile               =       DirectoryManager.getImagImageDesciptorFile ();
         File absIhfFile                =       DirectoryManager.getAbsImageDesciptorFile () ;

         List <Integer> fieldMapMask    =       fidImage.getProcpar().getFieldMap();
         int numberOfSlices             =       fidImage.getNumberOfSlices();
         int numberOfElements           =       fidImage.getNumberOfElements();
         int numberOfTotalElements      =       fidImage.getProcpar().getNumberOfElements();
         int size                       =       numberOfSlices*numberOfElements;
         int count                      =       0;

         ImageDescriptor   id           =     fidImage.toImageDescriptor();
         if (PackageManager.getCurrentApplication() != null){
             id.setConversionProg( PackageManager.getCurrentApplication().getProgramName());
         }
         id.setSourceFileName(filePath);



         FileOutputStream reStream     =   new FileOutputStream(realFile);
         FileChannel      reChannel    =   reStream.getChannel();

         FileOutputStream imStream     =   new FileOutputStream(imagFile);
         FileChannel      imChannel    =   imStream.getChannel();

         FileOutputStream absStream    =   new FileOutputStream(absFile);
         FileChannel      absChannel   =   absStream.getChannel();



        for (int  curElement = 0; curElement < numberOfTotalElements ;  curElement++) {

            int fieldMap            =   fieldMapMask.get(curElement);
            boolean isMap           =   Procpar.isMap(  fieldMap);
            if ( isMap ) {  continue; }



            for (int curSlice = 0; curSlice  < numberOfSlices ; curSlice ++) {
                 System.out.println("Processing slice "+ curSlice  + " and element "+curElement);
                ArrayList< float[][] > curImag = fidImage.getImages(curSlice, curElement);

                    count       =   curElement*numberOfSlices  + curSlice;
                    VarianBinaryConverter.setInfo( "Processing image "+ count+ " of "+ size);


                    float[][]  reImg    =   curImag.get(0);
                    float[][]  imImg    =   curImag.get(1);
                    float[][]  absImg   =   curImag.get(2);

                    ImageIO.writeBinaryFile(reImg,  id, reChannel );
                    ImageIO.writeBinaryFile(imImg,  id, imChannel);
                    ImageIO.writeBinaryFile(absImg, id, absChannel);
               }
        }



         ImageIO.storeToDisk(id, realIhfFile);
         ImageIO.storeToDisk(id, imagIhfFile);
         ImageIO.storeToDisk(id, absIhfFile);


         // write absicssa file
         LoadAndViewData.writeAbscissaFile(fidImage.getProcpar());


         reChannel.close();
         reStream.close();

         imChannel.close();
         imStream.close();

         absChannel.close();
         absStream.close();
     return true;
    }






      public static boolean convertVarianFidTo4dfpBinaryMultiThread(VarianFidImage fidImage,String filePath) {

        Callable<Boolean> callable  =   new VarianBinaryLoadThread(fidImage, filePath);
        FutureTask <Boolean> task   =   new FutureTask(callable);
        task.run();



        boolean result;
        try {
            result = task.get();
        } catch (Exception ex) {
            return false;
        }
        return result;

    }
}
