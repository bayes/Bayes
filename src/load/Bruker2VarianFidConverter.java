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
        File abscissFile                    =   DirectoryManager.getAbscissaFile();
        File textFile                       =   DirectoryManager.getTextFile();

        // create procpar
        BrukerDataInfo  paramsReader         =   reader.getParamsReader();
        Procpar procpar                     =   BrukerReader2Procpar( paramsReader);


        // write fid file
        FidWriter fidWriter                 =   new FidWriter();
        fidWriter.getFileHeader ().nblocks  =    paramsReader.getNumberOfTraces();
        fidWriter.getFileHeader ().ntraces  =   1;
        fidWriter.getFileHeader ().np       =   paramsReader.getNp();

        try{
            int shift = paramsReader.calculateTimeShift();
            float[][] shiftedReal = shift(reader.getFid_real(),shift );
            float[][] shiftedImag = shift(reader.getFid_imag(),shift );
            
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
     
     public static float [][]  shift(float [][] data, int shiftValue){
         int dim1 = data.length;
         int dim2 = data[0].length;
         float [][]  shiftedData = new float [dim1][dim2];
         for (int i = 0; i < shiftedData.length; i++) {
             System.arraycopy(data [i], shiftValue,shiftedData[i],0,data [i].length - shiftValue);
         }
                 
                 
         return shiftedData;
     }
     public static Procpar BrukerReader2Procpar(  BrukerDataInfo breader ){
        Procpar procpar = new  Procpar();

        int np              =   breader.getNp();
        double sw_hertz     =   breader.getSweepWidthHERTZ();
        double sw_ppm       =   breader.getSweepWidthPPM();
        float at            =   (float)(np/2/sw_hertz);
       // int gyro            =   60;
         //at            =   (float)(np/(2*sw_ppm*gyro));
        float sfrq          =   (float)breader.getSpectrometerFrequency();
        procpar.updateProcpar(at, sfrq, np);
        procpar.setLb(1f);
        procpar.setArraydim(breader.getNumberOfRepetions());


        return procpar;
    }

}
