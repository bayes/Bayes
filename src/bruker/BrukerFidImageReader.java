/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

import bayes.DirectoryManager;
import bruker.BrukerConstants.DATA_FORMAT;
import image.ImageDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import load.ImageConvertHelper;
import utilities.cFFT;

/**
 *
 * @author apple
 */
public class BrukerFidImageReader {
    BrukerDataInfoOLD datainfo                 =  new  BrukerDataInfoOLD();
    private List  <double[] [][]> kspace     =  new ArrayList <double[] [][]>();
    private List  <float [][]>  real        =  new ArrayList <float   [][]>();
    private List  <float [][]>  imag        =  new ArrayList <float [][]>();
    private List  <float  [][]>  ampl       =  new ArrayList <float   [][]>();

    public BrukerFidImage      readKSpaceFid(File dir){
        // by default kImage.isConstructed = false;
        BrukerFidImage fidImage = new BrukerFidImage();

        // initialize BrukerDataInfoOLD and Fid files
        BrukerDataInfoOLD bdi      =   new BrukerDataInfoOLD();
        bdi.readFilesInDir(dir);

        this.datainfo           =   bdi;

        File fidFile         = DirectoryManager.getFidFile(dir);

        // Are all files in place?
        if ( bdi.isLoaded() == false ) { return  fidImage;}


        // Is image?
        if (bdi.isImage() == false ) {return fidImage;}

        //
        try{
            FileInputStream     fin         =   new FileInputStream(fidFile);
            FileChannel channel             =   fin.getChannel();
            readBrukerFidImage( channel);

            fidImage.setDataInfo(bdi);
          //  fidImage.updateImageDimensions(); // must not be called prior to setting procpar
            fidImage.setIsConstructed(true);

            channel.close();
            fin.close();
      } catch(IOException exp){
            exp.printStackTrace();
            return fidImage;
      }
        return fidImage;
    }


      public void    readBrukerFidImage( FileChannel channel ) throws IOException{

        int nSlices                 = datainfo.getNumbersOfSlices();
        int nElements               = datainfo.getNumberOfElements();
        int nPhaseEncodes           = datainfo.getNumberOfPhaseEncodes();
        int nPointsPerTrace         = datainfo.getPointsInReadout();
        int []imageOrder            = datainfo.getAcquisitionObjectOrder();
        
        int phaseFactor             = datainfo.getPhaseFactor();
        ByteOrder byteorder         = datainfo.getByteOrder();
        DATA_FORMAT dformat         = datainfo.getDataFormat();

          for (int i = 0; i < imageOrder.length; i++) {
              kspace.add( new double [2][nPhaseEncodes ][nPointsPerTrace]);

          }

        for (int curPhaseEncode = 0; curPhaseEncode <  nPhaseEncodes;  curPhaseEncode += phaseFactor ) {
            for (int curSlice = 0; curSlice < nSlices; curSlice++) {
               for (int curElement = 0; curElement < nElements; curElement ++) {
                   for (int curPhaseFactor = 0; curPhaseFactor < phaseFactor ; curPhaseFactor++) {
                        double [][]  complextrace  = readData(channel,nPointsPerTrace, byteorder, dformat  );

                        int imagePositionIndex          = curSlice * nElements + curElement;


                        int imagePosition               = imageOrder [imagePositionIndex];
                        int phaseEncode                 = curPhaseEncode + curPhaseFactor;

                        double [][][]  curImage             = kspace.get(imagePosition);
                         kspace.set(imagePosition, curImage);
                        curImage [0][phaseEncode]    =  complextrace[0];
                        curImage [1][phaseEncode]    =  complextrace[1];
                            
                    }
                }
            }
        }


    }

      public void fft(){

        int nPhaseEncodes           = datainfo.getNumberOfPhaseEncodes();
        int nPointsPerTrace         = datainfo.getPointsInReadout();


          for (int curImg = 0; curImg < kspace .size(); curImg++) {
                double  [][][] cmplxkspace   =   kspace.get(curImg);
                double [][][] roFt          =    cFFT.fft2Dim2(cmplxkspace,nPointsPerTrace,Math.PI,  1);
                double [][][] image         =    cFFT.fft2Dim1 (roFt, nPhaseEncodes , Math.PI,  1);
                
                
                
                float [][] r             =   new float [nPhaseEncodes][nPointsPerTrace];
                float [][] i             =   new float [nPhaseEncodes][nPointsPerTrace];
                float [][] a             =   new float [nPhaseEncodes][nPointsPerTrace];
                
                    for (int k = 0; k < image[0].length; k++) {
                        for (int l = 0; l <image[0][0].length; l++) {
                            double re = image[0][k][l];
                            double im = image[1][k][l];
                            double am = Math.sqrt(re*re + im*im);
                            
                            r [k][l] = (float)re;
                            i [k][l] = (float)im;
                            a [k] [l] =(float) am;
                            
                            
                        }
                        
;
                  
              }
                
                real.add(r);
                imag.add(i);
                ampl.add(a);
          }

     
      }



     public void write(){
        ImageDescriptor id          =   datainfo.toImageDescriptor();
        ImageConvertHelper.writeImgFiles("real",real, id);
        ImageConvertHelper.writeImgFiles("imag",imag, id);
        ImageConvertHelper.writeImgFiles("ampl",ampl, id);
     }
     public static double [][] readData(
             FileChannel channel,
             int nComplexPointsInTrace,
             ByteOrder biteorder,
             DATA_FORMAT dformat

        ) throws IOException{

        double [][] data                 =   new double [2][nComplexPointsInTrace];
        int length                      =  2* nComplexPointsInTrace * dformat.getNumberOfBytes();
        ByteBuffer buffer               =  ByteBuffer.allocateDirect( length);
        buffer.order(biteorder);
        channel.read(buffer);
        buffer.rewind();


        {
            for (int i = 0; i < nComplexPointsInTrace; i++) {
                    float re        = 0;
                    float im        = 0;

                    switch (dformat){
                        case   GO_16_BIT_SGN_INT :
                                re        = buffer.getShort();
                                im        = buffer.getShort();
                                break;
                        case   GO_32BIT_SGN_INT :
                                re        = buffer.getInt();
                                im        = buffer.getInt();
                                break;
                        case   GO_32_BIT_FLOAT :
                                re        = buffer.getFloat();
                                im        = buffer.getFloat();
                                break;

                    }
                    
                    data[0][i]      = re;
                    data[1][i]      = im;

            }
     

      return data;
    }
}


  public static void main(String [] args){

    File file =  new File("/Users/apple/BayesSys/Bruker/pulseprogram");


    BrukerFidImageReader reader  =  new BrukerFidImageReader();
    reader.readKSpaceFid(file);
    reader.fft();
     reader .write();

}

}
