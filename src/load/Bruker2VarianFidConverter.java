/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.DirectoryManager;
import bruker.BrukerDataInfo;
import bruker.BrukerFidReader;
import utilities.*;
import fid.*;
import java.io.*;
/**
 *
 * @author apple
 */
public class Bruker2VarianFidConverter {

   public static  boolean readAndWrite(File sourceFile) {
        BrukerFidReader reader = new  BrukerFidReader(sourceFile);
        if (reader.isLoaded()  == false){  return false; }
          
        // write binary fid, procpar and ffh file
        // in the curreExp/fid directory
        boolean isWrite      =  writeFidFiles(sourceFile, reader );


        return   isWrite ;
    }


     public static boolean writeFidFiles(File sourceFile,  BrukerFidReader reader){
         File fidDir             =   DirectoryManager.getFidDir() ;

       // make sure image directory file exists
       if (fidDir .exists() == false){fidDir.mkdirs();}


        File fidDst                         =   DirectoryManager.getFidFile();
        File ffhDst                         =   DirectoryManager.getFidDesciptorFile ();
        File procparFile                    =   DirectoryManager.getProcparFile();
        File textFile                       =   DirectoryManager.getTextFile();

        // create procpar
        BrukerDataInfo  paramsReader         =   reader.getParamsReader();
        Procpar procpar                     =   BrukerReader2Procpar( paramsReader);


        // write fid file
        FidWriter fidWriter                 =   new FidWriter();
        fidWriter.getFileHeader ().nblocks  =    paramsReader.getNumberOfTraces();
        fidWriter.getFileHeader ().ntraces  =   1;
        fidWriter.getFileHeader ().np       =   2*paramsReader.calculateTruncatedDimension();

        try{
            float[][] real = reader.getFid_real() ;
            float[][] imag = reader.getFid_imag() ;
            float[][][] postProcessFid= applyBrukerFilter(real,imag,paramsReader)  ; 
            float[][] shiftedReal = truncate(real,paramsReader );
            float[][] shiftedImag = truncate(imag,paramsReader );
//            
//            fidWriter.writeFid( fidDst, shiftedReal, shiftedImag);
            fidWriter.writeFid( fidDst, shiftedReal, shiftedImag);
        } catch(IOException exp){
            exp.printStackTrace();
            DisplayText.popupMessage("Failed to write fid binary file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);
            return false;
        }



        // write procpar file
        procpar.setFileSource(sourceFile.getAbsolutePath());

        ProcparFileWriterForFid  writer =   new    ProcparFileWriterForFid  ();
        boolean isDone                  =   writer.writeProcparFile(procpar, procparFile );


        if (isDone == false){
            DisplayText.popupMessage("Failed to write procpar file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }


        // write Fid File Header (FFH) (Fid Descriptor) file
        isDone  = FidIO.createAndSaveFfhFile(procpar, ffhDst);
        if (isDone == false){
            DisplayText.popupMessage("Failed to write ffh file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }

         // write Fid File Header (FFH) (Fid Descriptor) file
         String str = String.format("Created from text file %s",  sourceFile.getAbsolutePath());
         isDone     = IO.writeFileFromString(str, textFile );
        if (isDone == false){
            DisplayText.popupMessage("Failed to text file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }

        return true;
    }
     
     public static float [][]  truncate(float [][] data,BrukerDataInfo breader){
         int dim1 = data.length;
         int dim2 = data[0].length;
         int truncDim = breader.calculateTruncatedDimension();
         float [][]  shiftedData = new float [dim1][truncDim];
         for (int i = 0; i < shiftedData.length; i++) {
             System.arraycopy(data [i], 0,shiftedData[i],0,truncDim);
         }
                 
                 
         return shiftedData;
     }
     public static float [][][]  applyBrukerFilter (float [][] real, float [][] imag, BrukerDataInfo breader ){
         int np                 =   breader.getNp();
         double grpdly          =   breader.getGroupDelay();
         float shift            =   (float)(Math.PI + 4*Math.PI*(grpdly/np));
         int dim1               =   real.length;
         int dim2               =   real[0].length;
         float shiftedData  [][][]= new float [2][dim1][dim2];
         
         for (int i = 0; i < dim1; i++) {
            float [][] curFFT = new float[2][dim2];
            curFFT[0]  = real[i];
            curFFT[1]  = imag[i];
            curFFT     = cFFT.doFFT(curFFT, 0, -1);
            curFFT     = cFFT.shift(curFFT, shift);
            curFFT     = cFFT.doFFT(curFFT, -Math.PI,1);
            shiftedData[0][i] = curFFT[0];
            shiftedData[1][i] = curFFT[1];
     }
                 
                 
         return shiftedData;
     }
     public static Procpar BrukerReader2Procpar(  BrukerDataInfo breader ){
        Procpar procpar = new  Procpar();

        int np              =   breader.getNp();
        double sw_hertz     =   breader.getSweepWidthHERTZ();
        double sw_ppm       =   breader.getSweepWidthPPM();
        float at            =   (float)(np/sw_hertz);
       // int gyro            =   60;
         //at            =   (float)(np/(2*sw_ppm*gyro));
        float sfrq          =   (float)breader.getSpectrometerFrequency();
        int npAfterTruncation = 2*breader.calculateTruncatedDimension();
        procpar.updateProcpar(at, sfrq, npAfterTruncation);
        procpar.setLb(1f);
        procpar.setArraydim(breader.getNumberOfRepetions());


        return procpar;
    }

}
