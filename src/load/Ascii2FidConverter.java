/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;

import bayes.DirectoryManager;
import load.gui.Ascii2FidDialog;
import utilities.*;
import fid.*;
import java.io.*;
/**
 *
 * @author apple
 */
public class Ascii2FidConverter {

    public Ascii2FidConverter (){
    }
    private FidDescriptor  descriptor               =   new FidDescriptor();
    private Procpar        procpar                  =   new Procpar();
    private int numberOfLinesInFile                 =   0;
    private boolean sclicesFirst                    =   false;
    private String [][]  abscissaValues             =   null;


    public   boolean readAndWrite(File sourceFile) {
        float [][][]    fidValues   =    null;
        int numberOfLines           =    0;
        try {
            numberOfLines = IO.getNumberOfLines(sourceFile);
        } catch (IOException ex) {
            String error        =   String.format(
                                    "Failed to parse file\n" +
                                     "%s\n" +
                                     "Exit.", sourceFile.getPath());
            DisplayText.popupMessage(error);
            return false;
        }
        setNumberOfLinesInFile(numberOfLines);
        Ascii2FidDialog.showDialog(this);
        if( Ascii2FidDialog.isDialogCanceled() == true){return false;}

        // read ascii fid file
        fidValues            = readFile( sourceFile);

        // make sure that files was read correctly
        if (fidValues == null) {return false;}


        // write binary fid, procpar and ffh file
        // in the curreExp/fid directory
        boolean isWrite      = writeFidFiles(sourceFile,fidValues);
       

       return   isWrite;
    }


    /*
     *  Ascii file is assumed to have white-space separated
     *  two column of float numbers.
     *
     * Output is 3-dimensional array of float bumbers
     * 1-dimension: length = 2 : 0 for real channel, 1 for imaginary chanel
     * 2-dimension: length = number Of Traces
     * 3-dimension: lenght = number Of Complex Points per Trace
     *
     *
     */

    public  float[][][] readFile(File sourceFile){
       Procpar aprocpar             =   getProcpar();
       int numberOfTraces           =   aprocpar.getArrayDim();
       int numberOfPoinsPerTrace    =   aprocpar.getNp()/2;
       int numberOFlinesInTheFile   =   getNumberOfLinesInFile();
       float [][][] out             =   new float[2][numberOfTraces][numberOfPoinsPerTrace];

       String line                  =   null;
       String regex                 =   "\\s+";
       String message               =   null;
       int curTrace                 =   0;
       int curPoint                 =   0;
       try{
        FileInputStream fstream     = new FileInputStream(sourceFile);
        BufferedReader br        = new BufferedReader(new InputStreamReader(fstream), 1000000);



           for (int curLine = 0; curLine < numberOFlinesInTheFile ; curLine++) {
              line                          =   br.readLine();
              String [] array               =   line.trim().split(regex);

              curTrace                      =   (int)Math.floor( curLine / numberOfPoinsPerTrace );
              curPoint                      =   curLine% numberOfPoinsPerTrace ;
              out[0][ curTrace][curPoint]   =   Float.parseFloat(array[0]);
              out[1][ curTrace][curPoint]   =   Float.parseFloat(array[1]);


            }


            //Close the input stream
            br.close();
            fstream.close();
       }catch(FileNotFoundException exp){
             exp.printStackTrace();
             message =  String.format("File %s\n"+
                                    "is not found\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath());

           DisplayText.popupMessage(message);
           return null;


        }
        catch( NumberFormatException exp){
             exp.printStackTrace();
             message =  String.format("NumberFormatException is thrown \n"+
                                    "when trying to read file\n"+
                                    "%s\n"+
                                    "Error message is:\n"+
                                    "%s\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath(), exp.getMessage());

           DisplayText.popupMessage(message);
           return null;


        }
        catch(ArrayIndexOutOfBoundsException exp){
             exp.printStackTrace();
             message =  String.format("ArrayIndexOutOfBoundsException is thrown \n"+
                                    "when trying to read file\n"+
                                    "%s\n"+
                                    "Error message is:\n"+
                                    "%s\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath(), exp.getMessage());

           DisplayText.popupMessage(message);
           return null;


        }
         catch(IllegalArgumentException exp){
             exp.printStackTrace();
             message =  String.format("IllegalArgumentException is thrown \n"+
                                    "when trying to read file\n"+
                                    "%s\n"+
                                    "Error message is:\n"+
                                    "%s\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath(), exp.getMessage());

           DisplayText.popupMessage(message);
           return null;


        }
        catch(IOException exp){
             exp.printStackTrace();
             message =  String.format("IOException is thrown \n"+
                                    "when trying to read file\n"+
                                    "%s\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath());

           DisplayText.popupMessage(message);
           return null;


        }

        return out;
  }


    public boolean writeFidFiles(File sourceFile, float[][][] data){
         File fidDir             =   DirectoryManager.getFidDir() ;

       // make sure image directory file exists
       if (fidDir .exists() == false){fidDir.mkdirs();}


        File fidDst                         =   DirectoryManager.getFidFile();
        File ffhDst                         =   DirectoryManager.getFidDesciptorFile ();
        File procparFile                    =   DirectoryManager.getProcparFile();
       

        // write fid file
        FidWriter fidWriter                 =   new FidWriter();
        fidWriter.getFileHeader ().nblocks  =   getProcpar ().getArrayDim();
        fidWriter.getFileHeader ().ntraces  =   1;
        fidWriter.getFileHeader ().np       =   getProcpar ().getNp();

        float [][] real                     =   data[0];
        float [][] imag                     =   data[1];

        try{
            fidWriter.writeFid( fidDst, real, imag);
        } catch(IOException exp){
            exp.printStackTrace();
            DisplayText.popupMessage("Failed to write fid binary file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);
            return false;
        }



        // write procpar file
        getProcpar().setFileSource(sourceFile.getAbsolutePath());

        ProcparFileWriterForFid  writer =   new    ProcparFileWriterForFid  ();
        boolean isDone              =   writer.writeProcparFile(getProcpar(), procparFile );


        if (isDone == false){
            DisplayText.popupMessage("Failed to write procpar file.");

            // delete previously written files
            IO.emptyDirectory(fidDir);

            return false;
        }


        // write Fid File Header (FFH) (Fid Descriptor) file
        isDone  = FidIO.createAndSaveFfhFile(getProcpar(), ffhDst);
        if (isDone == false){
            DisplayText.popupMessage("Failed to write ffh file.");

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
   
    public FidDescriptor getDescriptor () {
        return descriptor;
    }
    public void setDescriptor ( FidDescriptor descriptor ) {
        this.descriptor = descriptor;
    }

    public Procpar getProcpar () {
        return procpar;
    }
    public void setProcpar ( Procpar procpar ) {
        this.procpar = procpar;
    }

    public int getNumberOfLinesInFile () {
        return numberOfLinesInFile;
    }
    public void setNumberOfLinesInFile ( int numberOfLinesInFile ) {
        this.numberOfLinesInFile = numberOfLinesInFile;
    }

    public boolean isSclicesFirst () {
        return sclicesFirst;
    }
    public void setSclicesFirst ( boolean sclicesFirst ) {
        this.sclicesFirst = sclicesFirst;
    }

    public String[][] getAbscissaValues () {
        return abscissaValues;
    }
    public void setAbscissaValues ( String[][] abscissaValues ) {
        this.abscissaValues = abscissaValues;
    }
}
