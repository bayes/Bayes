/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
 
import image.*;
import fid.*;
import java.util.ArrayList;
import static utilities.MathFunctions.*;
import image.varian.VairanImageReader.CHANEL;
import bayes.Enums.IMAGE_TYPE;
  
public class VarianFidImage {
 private Procpar           procpar;
 private   float []        pixels ;
 private   boolean         isConstructed = false;
 private int               outputReadOutLength;
 private int               outputPhaseEncodeLength;
 private boolean           phaseImages      =   true;

 

    public  double[][][] getComplexKSpace(int curSlice, int curElement){
        int nPhaseEncode        = getProcpar().getNumberOfPhaseEncodePoints();
        int nReadOut            = getProcpar(). getNumberOfReadOutPoints();

        double [][][] data      = new double [2][ nPhaseEncode][nReadOut];
        for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {
            for (int curPointInReadOut = 0; curPointInReadOut < nReadOut; curPointInReadOut++) {

                int posRe = VairanImageReader.getStartIndex(getProcpar(),curSlice,curElement,curPhaseEncode,CHANEL.Real );
                int posIm = VairanImageReader.getStartIndex(getProcpar(),curSlice, curElement,curPhaseEncode,CHANEL.Imag );

                data[0][curPhaseEncode][curPointInReadOut] = pixels[posRe + curPointInReadOut];
                data[1][curPhaseEncode][curPointInReadOut] = pixels[posIm + curPointInReadOut];
            }
        }
           
        return data;
    }
    public ArrayList< float[][] >   getImages(int curSlice, int curElement){
        Procpar proc                =   getProcpar();
        double cmplxData [][][]     =   getComplexKSpace(curSlice,curElement);
        int nPhaseEncode            =   proc.getNumberOfPhaseEncodePoints();
        int nReadOut                =   proc. getNumberOfReadOutPoints();
        int readOutPad              =   getOutputReadOutLength();
        int phaseEncodePad          =   getOutputPhaseEncodeLength();
        double ppe                  =   proc.getPpe();
        double lpe                  =   proc.getLengthPhaseEncodeInCM();
        boolean applyPhasing        =   isPhaseImages();

        double image [] [] []       =   null;


        double real                 =   0;
        double imag                 =   0;
        int width                   =   phaseEncodePad ;
        int height                  =   readOutPad ;
        float amp [][]              =   new float [width][height];
        float im  [][]              =   new float [width][height];
        float re [] []              =   new float [width][height];
        int norm                    =   nPhaseEncode*nReadOut;
        
        ArrayList< float[][] > img  =   new  ArrayList< float[][] >();

      //  System.out.println("Slice = "+curSlice);
      //   System.out.println("Element = "+curElement);

        IMAGE_TYPE type             = getProcpar().getImageType();

     //   System.out.println("Process Image Type "+ type);
        switch(type){


            case SPIN_ECHO :

                image       =  SeGeProcess.processSeGeImage(nPhaseEncode,
                                                            nReadOut,
                                                            phaseEncodePad,
                                                            readOutPad,
                                                            ppe,
                                                            lpe,
                                                            applyPhasing,
                                                            cmplxData);



                break;




             case MAP_EPI:
                 double map [][][]      =   getComplexKSpace(curSlice,0);
                 image                  =   MapEPI.processMapEpiImage(
                                                         nPhaseEncode,
                                                         nReadOut,
                                                         phaseEncodePad,
                                                         readOutPad,
                                                         ppe,
                                                         lpe,
                                                         applyPhasing,
                                                         cmplxData,
                                                         map);
                 break;




            case EPI:

  
                image                   =   NonMapEpi.processNonMapEpiImage( nPhaseEncode,
                                                                            nReadOut,
                                                                            phaseEncodePad,
                                                                            readOutPad,
                                                                            ppe,
                                                                            lpe,
                                                                            applyPhasing,
                                                                            cmplxData
                                                                           );
                break;

        }


        
        for (int i = 0; i < width  ; i++) {
               for (int j = 0; j  < height; j ++) {
                  real              =   image[0][i][j];
                  imag              =   image[1][i][j];
                  re[i][j]          =   (float)(real/norm);
                  im[i][j]          =   (float)(imag/norm);
                  amp[i][j]         =   (float)( Math.hypot (real,  imag )/norm); 
               }
        }
       
        img.add(re);
        img.add(im);
        img.add(amp);
  
        
        return img;
        
    }
    
    


    public ImageDescriptor toImageDescriptor(){
         ImageDescriptor   id           =      new ImageDescriptor ();
         int row                        =      getOutputReadOutLength();
         int col                        =      getOutputPhaseEncodeLength();
         int slc                        =      getNumberOfSlices();
         int elm                        =      getNumberOfElements();
         float scale1                   =      computeScalingFactorPhaseEncodeInMm();
         float scale2                   =      computeScalingFactorReadOutInMm();
         float scale3                   =      computeScalingFactorThicknessInMm();


         id.setNumberOfColumns  (col); //nFtPe -  first dimension in IFH
         id.setNumberOfRows     (row); //nFtRo -  second dimension in IFH
         id.setNumberOfElements (elm);
         id.setNumberOfSlices   (slc);
         id.setScaling1(scale1);
         id.setScaling2(scale2);
         id.setScaling3(scale3);



         return id;

    }
    // getters and setters//
        
    public int getNumberOfSlices(){  return getProcpar().getNs();}
    public int getNumberOfElements(){
        int nTotalElemements    =   getProcpar().getNumberOfElements();
        int nFieldMaps          =   getProcpar().getNumberOfFieldMaps();
        int nElemements         =   nTotalElemements - nFieldMaps;
        return nElemements;}
    public int calculateImageReadOutLength(){
        int length                      =   0;   
        Procpar  pr                     =   getProcpar();
        boolean isPadReadOut            =   getProcpar().isFnActivated();
        if (isPadReadOut   == true){
           length                       =   pr.getNumberOfPaddedReadOutPoints();
        }
        else {
           int curLength                =   pr.getNumberOfReadOutPoints();
           length                       =   getPaddedLength(curLength);
        }
            
        return length;
    }
    public int calculateImagePhaseEncodeLength(){
        int length                      =   0;   
        Procpar  pr                     =   getProcpar();
        boolean isPadPhaseEncode        =   getProcpar().isFn1Activated();
        if (isPadPhaseEncode   == true){
           length                       =   pr.getNumberOfPaddedOfPhaseEncodetPoints();
        }
        else {
           int curLength                =   pr.getNumberOfPhaseEncodePoints();
           length                       =   getPaddedLength(curLength);
        }
            
        return length;
    }
    public void updateImageDimensions (){
        int readoutN    = calculateImageReadOutLength();
        int phsEncdN    = calculateImagePhaseEncodeLength();
        
        setOutputReadOutLength       (readoutN);
        setOutputPhaseEncodeLength  ( phsEncdN);
    }
    
    
        
    // get image parameters
    
    public float  computeScalingFactorReadOutInMm(){
         int npixels            =   getOutputReadOutLength ();
         float sizeInCm         =   getProcpar().getLengthReadOutInCM();
         float mmPrePxl         =   sizeInCm*10/ npixels ; 
         return  mmPrePxl;
     }
    public float  computeScalingFactorPhaseEncodeInMm(){
         int npixels            =   getOutputPhaseEncodeLength ();
         float sizeInCm         =   getProcpar().getLengthPhaseEncodeInCM();
         float mmPrePxl         =   sizeInCm*10/ npixels ; 
         return  mmPrePxl;
     }
    public float  computeScalingFactorThicknessInMm(){
         //int npixels            =   getNumberOfSlices();
         float sizeInmm         =   getProcpar(). getThicknesInMM();
        // float mmPrePxl         =   sizeInmm/ npixels ; 
         return sizeInmm;
     }
   
    public Procpar  getProcpar () {
        return procpar;
    }
    public boolean  isConstructed () {
        return isConstructed;
    }

    public float[]  getPixels () {
        return pixels;
    }
    public void     setPixels ( float[] images ) {
        this.pixels = images;
    }
    public void     setIsConstructed ( boolean isConstructed ) {
        this.isConstructed = isConstructed;
    }
    public void     setProcpar ( Procpar procpar ) {
        this.procpar = procpar;
    }
  
    public int   getOutputPhaseEncodeLength () {
        return outputPhaseEncodeLength;
    }
    public void  setOutputPhaseEncodeLength ( int paddedPhaseEncodeLength ) {
        this.outputPhaseEncodeLength = paddedPhaseEncodeLength;
    }
    public int   getOutputReadOutLength () {
        return outputReadOutLength;
    }
    public void  setOutputReadOutLength ( int paddedReadOutLength ) {
        this.outputReadOutLength = paddedReadOutLength;
    }

    public boolean  isPhaseImages() {
        return phaseImages;
    }
    public void     setPhaseImages(boolean phaseImages) {
        this.phaseImages = phaseImages;
    }



    

}
