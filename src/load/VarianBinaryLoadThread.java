/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import fid.Procpar;
import image.varian.VarianFidImage;
import image.ImageDescriptor;
import image.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import bayes.DirectoryManager;
import bayes.PackageManager;

/**
 *
 * @author apple
 */
public class VarianBinaryLoadThread implements Callable{
        FileChannel      reChannel;
        FileChannel      imChannel;
        FileChannel      absChannel;
        private VarianFidImage fidImage;
        String filePath;
        int size;
        int nSkippedElements    =   0;
        ImageDescriptor   id;
        final ExecutorService pool ;

        VarianBinaryLoadThread (VarianFidImage afidImage, String afilePath){
            fidImage = afidImage;
            filePath = afilePath;
            int np                         =   Runtime.getRuntime().availableProcessors();
            pool                           =   Executors.newFixedThreadPool(np);

            System.out.println("Number of threads "+np);
        }



        public Boolean call() throws IOException, FileNotFoundException{
         boolean isDone                 =       false;
         File realFile                  =       DirectoryManager.getRealImageFile();
         File imagFile                  =       DirectoryManager.getImagImageFile();
         File absFile                   =       DirectoryManager.getAbsImageFile();

         File realIhfFile               =       DirectoryManager.getRealImageDesciptorFile ();
         File imagIhfFile               =       DirectoryManager.getImagImageDesciptorFile ();
         File absIhfFile                =       DirectoryManager.getAbsImageDesciptorFile () ;

         List <Integer> fieldMapMask    =       getFidImage().getProcpar().getFieldMap();
         int numberOfSlices             =       getFidImage().getNumberOfSlices();
         int numberOfElements           =       getFidImage().getNumberOfElements();
         int numberOfTotalElements      =       getFidImage().getProcpar().getNumberOfElements();
         size                           =       numberOfSlices*numberOfElements;

         id                             =        getFidImage().toImageDescriptor();
         if (PackageManager.getCurrentApplication() != null){
             id.setConversionProg( PackageManager.getCurrentApplication().getProgramName());
         }
         
         id.setSourceFileName(filePath);
         id.setxLabel("Phase Encode (cm)");
         id.setyLabel("Readout (cm)");

         FileOutputStream reStream      =   new FileOutputStream(realFile);
         reChannel                      =   reStream.getChannel();
         FileOutputStream imStream      =   new FileOutputStream(imagFile);
         imChannel                      =   imStream.getChannel();

         FileOutputStream absStream     =   new FileOutputStream(absFile);
         absChannel                     =   absStream.getChannel();

       

        

        for (int  curElement = 0; curElement < numberOfTotalElements ;  curElement++) {

            int fieldMap            =   fieldMapMask.get(curElement);
            boolean isMap           =   Procpar.isMap(  fieldMap);
            if ( isMap )                { nSkippedElements +=1; continue; }


            for (int curSlice = 0; curSlice  < numberOfSlices ; curSlice ++) {
                Fid2Img fid2Img         =   new Fid2Img();
                fid2Img.curElement      =   curElement;
                fid2Img.curSlice        =   curSlice;
                fid2Img.numberOfSlices  =  numberOfSlices;

                try{
                    pool.submit(fid2Img);

                 }
                 catch(Exception exp){
                     exp.printStackTrace();
                     pool.shutdownNow();

                 }
               }
        }

        pool.shutdown();

        try {
            pool.awaitTermination(60 * 60 * 24, TimeUnit.SECONDS);

         System.out.println("Thread pool is shutdown  = "+ pool.isTerminated());

         ImageIO.storeToDisk(id, realIhfFile);
         ImageIO.storeToDisk(id, imagIhfFile);
         ImageIO.storeToDisk(id, absIhfFile);


         // write absicssa file
         LoadAndViewData.writeAbscissaFile(getFidImage().getProcpar());
      

         isDone                         = true;


        } catch (InterruptedException ex) {
              ex.printStackTrace();
              pool.isTerminated();
        }
        catch (Exception ex) {
              ex.printStackTrace();
              pool.isTerminated();
        }


        try{
                reChannel.close();
                reStream.close();
                imChannel.close();
                imStream.close();
                absChannel.close();
                absStream.close();
        }catch(Exception exp){ exp.printStackTrace();}
      
       
        return isDone;
    }

        public VarianFidImage getFidImage() {
        return fidImage;
    }
        public void setFidImage(VarianFidImage fidImage) {
        this.fidImage = fidImage;
    }



    class Fid2Img implements Callable {
        int curSlice;
        int curElement;
        int numberOfSlices;

        public Integer call() throws IOException {
                    ArrayList< float[][] >curImag       =   getFidImage().getImages(curSlice, curElement);
                    int count                           =   (curElement - nSkippedElements)*numberOfSlices  + curSlice ;

                    int countIndx1                      =   count+1;
                    VarianBinaryConverter.setInfo( "Processing image "+  countIndx1+ " of "+ size);

                    // Keep in mind that ImageIO.writeBinaryFile is synchronized


                        float[][]  reImg    =   curImag.get(0);
                        float[][]  imImg    =   curImag.get(1);
                        float[][]  absImg   =   curImag.get(2);

                        ImageIO.writeBinaryFile(count,reImg,  id, reChannel );
                        ImageIO.writeBinaryFile(count,imImg,  id, imChannel);
                        ImageIO.writeBinaryFile(count,absImg, id, absChannel);




                        return count;


    }



    }
}
