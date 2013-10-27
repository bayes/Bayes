/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.siemens;

import fid.FidIO;
import fid.FidWriter;
import fid.Procpar;
import fid.ProcparFileWriterForFid;
import java.io.File;
import java.io.IOException;
import bayes.DirectoryManager;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class SiemensToVarianConverter {
    public static boolean writeFidFilesFromRda( File  source){
         Rda rh         =  new  Rda();
         rh.read(source);
         if(rh.isLoaded() == false){
             String path            =   source.getPath();
             String er              =   rh.getErrorMessage();

             String message = String.format(
                                    "Failed to read rda file\n" +
                                    "%s .\n" +
                                    "%s ."
                                    ,path, er);
             DisplayText.popupErrorMessage(message);
             return false;}
         else{
            return writeFidFilesFromRda(rh);
         }
    }
    public static boolean writeFidFilesFromRda( Rda rda){
        File fidDir             =   DirectoryManager.getFidDir() ;

       // make sure image directory file exists
        if (fidDir .exists() == false){fidDir.mkdirs();}


        File fidDst                         =   DirectoryManager.getFidFile();
        File ffhDst                         =   DirectoryManager.getFidDesciptorFile ();
        File procparFile                    =   DirectoryManager.getProcparFile();

        // write fid file


        boolean isDone =     writeFidFromRda(fidDst, rda);
        if (isDone  == false){
            IO.emptyDirectory(fidDir); // delete previously written files
            return false;
        }



        // write procpar file

        ProcparFileWriterForFid  writer     =   new    ProcparFileWriterForFid  ();
        Procpar                  procpar    =   rda.getProcpar();
        isDone                              =   writer.writeProcparFile( procpar, procparFile );


        if (isDone == false){
            String error                    =   "Failed to write procpar file.";
            DisplayText.popupErrorMessage( error );

            // delete previously written files
            IO.emptyDirectory(fidDir);
            return false;
        }


        // write Fid File Header (FFH) (Fid Descriptor) file
        isDone  = FidIO.createAndSaveFfhFile( procpar, ffhDst);
        if (isDone == false){
            String error                    =   "Failed to write ffh file.";
            DisplayText.popupErrorMessage( error );

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }

        // write  text file
        File textFile           =   DirectoryManager.writeTextFile(fidDst );
        if (textFile == null || textFile.exists() == false){
            DisplayText.popupMessage("Failed to write text file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }
        return true;
    }
    public static boolean writeFidFromRda( File dist, Rda rda){
        try {
            FidWriter fwriter                   =   new FidWriter();
            float[][] real                      =   rda.getkSpaceReal();
            float[][] imag                      =   rda.getkSpaceImag();
            fwriter.getFileHeader ().nblocks    =   rda.getNumberOfTraces();
            fwriter.getFileHeader ().ntraces    =   1;
            fwriter.getFileHeader ().np         =   rda.getNumberOfTotaldataPoints();
            fwriter.writeFid(dist, real, imag);
        } catch (IOException ex) {
            DisplayText.popupErrorMessage("Failed to write fid binary file.");
            ex.printStackTrace();
            dist.delete();
        }
        finally{
            return dist.exists();
        }
    }
  
   

     public static boolean writeFidFilesFromRaw( File  source){
         Raw r                          =  new  Raw();
         r.read(source);
         if(r.isLoaded() == false){
             String path            =   source.getPath();
             String er              =   r.getErrorMessage();

             String message = String.format(
                                    "Failed to read rda file\n" +
                                    "%s .\n" +
                                    "%s ."
                                    ,path, er);
             DisplayText.popupErrorMessage(message);
             return false;}
         else{
            return writeFidFilesFromRda(r);
         }
    }
     public static boolean writeFidFilesFromRda( Raw raw){
        File fidDir             =   DirectoryManager.getFidDir() ;

       // make sure image directory file exists
        if (fidDir .exists() == false){fidDir.mkdirs();}


        File fidDst                         =   DirectoryManager.getFidFile();
        File ffhDst                         =   DirectoryManager.getFidDesciptorFile ();
        File procparFile                    =   DirectoryManager.getProcparFile();

        // write fid file
        boolean isDone =      writeFidFromRaw(fidDst, raw);
        if (isDone  == false){
            IO.emptyDirectory(fidDir); // delete previously written files
            return false;
        }



        // write procpar file

        ProcparFileWriterForFid  writer     =   new    ProcparFileWriterForFid  ();
        Procpar                  procpar    =   raw.getProcpar();
        isDone                              =   writer.writeProcparFile( procpar, procparFile );


        if (isDone == false){
            String error                    =   "Failed to write procpar file.";
            DisplayText.popupErrorMessage( error );

            // delete previously written files
            IO.emptyDirectory(fidDir);
            return false;
        }


        // write Fid File Header (FFH) (Fid Descriptor) file
        isDone  = FidIO.createAndSaveFfhFile( procpar, ffhDst);
        if (isDone == false){
            String error                    =   "Failed to write ffh file.";
            DisplayText.popupErrorMessage( error );

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }

        // write  text file
        File textFile           =   DirectoryManager.writeTextFile(fidDst );
        if (textFile == null || textFile.exists() == false){
            DisplayText.popupMessage("Failed to write text file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }

        return true;
    }
     public static boolean writeFidFromRaw( File dist, Raw raw){
        try {
            FidWriter fwriter                   =   new FidWriter();
            float[][] real                      =   raw.getkSpaceRealAsFloat();
            float[][] imag                      =   raw.getkSpaceImagAsFloat();
            fwriter.getFileHeader ().nblocks    =   Raw.NUMBER_OF_TRACES;
            fwriter.getFileHeader ().ntraces    =   Raw.NUMBER_OF_TRACES;
            fwriter.getFileHeader ().np         =   2*Raw.NUMBER_COMPLEX_POINTS;
            fwriter.writeFid(dist, real, imag);
        } catch (IOException ex) {
            DisplayText.popupErrorMessage("Failed to write fid binary file.");
            ex.printStackTrace();
            dist.delete();
        }
        finally{
            return dist.exists();
        }
    }

    

     public static void main(String args[]){
        File src = new File("/Users/apple/Desktop/NK29Al1.rda");

        Rda rh =  new  Rda();
        rh.read( src);
        writeFidFilesFromRda(rh);
    }
}
