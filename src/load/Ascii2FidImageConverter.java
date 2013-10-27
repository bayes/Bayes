/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.DirectoryManager;
import load.gui.Ascii2FidImageDialog;
import utilities.*;
import fid.*;
import java.io.*;
/**
 *
 * @author apple
 */
public class Ascii2FidImageConverter {
    float [] real;
    float [] imag;
    private Procpar        procpar                  =   new Procpar();
    private int numberOfLinesInFile                 =   0;
    private boolean sclicesFirst                    =   false;
    private String [][]  abscissaValues             =   null;


    public Ascii2FidImageConverter (){
        super();
    }

      public   boolean readAndWrite(File sourceFile) {
       File imgFidDir             =   DirectoryManager.getImageFidDir() ;
       return  readAndWrite(sourceFile,imgFidDir );
    }
      public   boolean readAndWrite(File sourceFile, File distDir) {
        int numberOfLines           =    0;
        try {
            numberOfLines = IO.getNumberOfLines(sourceFile);
        } catch (IOException ex) {
            DisplayText.popupMessage(String.format("Failed to parse file\n" +
                                                    "%s\n" +
                                                    "Exit.", sourceFile.getPath()));
            return false;
        }
        setNumberOfLinesInFile(numberOfLines);
        Ascii2FidImageDialog.showDialog(this);
        if(Ascii2FidImageDialog.isDialogCanceled() == true){return false;}

        // read ascii fid file
       boolean isRead            = readFile( sourceFile);

        // make sure that files was read correctly
        if (isRead  == false) {
            real = null;
            imag = null;
            return false;
        }


        // write binary fid, procpar and ffh file
        // in the curreExp/fid directory
        boolean isWrite      = writeFidFiles(sourceFile,distDir);

        // free up resources
        real = null;
        imag = null;
        
       return   isWrite;
    }

    /*
     *  Ascii file is assumed to have white-space separated
     *  two column of float numbers.
     *
     * Two 1-dimensional array of float numbers
     * are being popultaed "real" and "img".
     *
     */
    public boolean readFile(File sourceFile){
        int nPhaseEncode            =   getProcpar().getNumberOfPhaseEncodePoints();
        int nReadOut                =   getProcpar().getNumberOfReadOutPoints();
        int nSclices                =   getProcpar().getNs();
        int nElements               =   getProcpar().getNumberOfElements();
        int totalPoints             =   nReadOut * nPhaseEncode * nSclices * nElements;
        int numberOFlinesInTheFile  =   getNumberOfLinesInFile();
        real                        =   new float[totalPoints];
        imag                        =   new float[totalPoints];
        String line                 =   null;
        String regex                =   "\\s+";
        String message              =   null;
        
        int count                   =   0;
        
        try
        {
            FileInputStream fstream = new FileInputStream(sourceFile);
            BufferedReader br       = new BufferedReader(new InputStreamReader(fstream), 1000000);



           for (int curLine = 0; curLine < numberOFlinesInTheFile ; curLine++) {
              line                          =   br.readLine();
              String [] array               =   line.trim().split(regex);
              real[count]                   =   Float.parseFloat(array[0]);
              imag[count]                   =   Float.parseFloat(array[1]);

              count++;
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
           return false;


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
           return false;


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
             System.out.println("count "+ count);

           DisplayText.popupMessage(message);
           return false;


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
           return false;


        }
        catch(IOException exp){
             exp.printStackTrace();
             message =  String.format("IOException is thrown \n"+
                                    "when trying to read file\n"+
                                    "%s\n"+
                                    "Exiting.\n"
                                    ,sourceFile.getPath());

           DisplayText.popupMessage(message);
           return false;


        }

        return true;
  }

    public boolean writeFidFiles(File sourceFile){
         File fidDir             =   DirectoryManager.getImageFidDir() ;
         return writeFidFiles(sourceFile,  fidDir);
    }
    public boolean writeFidFiles(File sourceFile, File distDir){
 
       // make sure image directory file exists
       if (distDir .exists() == false){distDir.mkdirs();}


        File fidDst                         =   DirectoryManager.getFidFile(distDir);
        File procparFile                    =   DirectoryManager.getProcparFile(distDir);
        File textFile                       =   DirectoryManager.getTextFile(distDir);

        // write fid file
        FidWriter fidWriter                 =   new FidWriter();
        // number of blocks                 =   number of elements <->arraydim*nv for standard image fid
        fidWriter.getFileHeader ().nblocks  =   getProcpar ().getArrayDim();
        // number of traces per block       =   number of slices
        fidWriter.getFileHeader ().ntraces  =   getProcpar ().getNs();
        // number of points per trace       =   np - > points in readout
        fidWriter.getFileHeader ().np       =   getProcpar ().getNp();
        // set procpar
        fidWriter.setProcpar(this.getProcpar());


        try{
             fidWriter.writeImageFid( fidDst, real, imag, isSclicesFirst());

        } catch(IOException exp){
            exp.printStackTrace();
            DisplayText.popupMessage("Failed to write fid binary file.");

            // delete previously written files
            IO.emptyDirectory( distDir);
            return false;
        }



        // write procpar file
        getProcpar().setFileSource(sourceFile.getAbsolutePath());

        ProcparFileWriterForImages  writer = new    ProcparFileWriterForImages  ();
        boolean isDone =  writer.writeProcparFile(getProcpar(), procparFile );

        if (isDone == false){
            DisplayText.popupMessage("Failed to write procpar file.");

            // delete previously written files
            IO.emptyDirectory( distDir);

            return false;
        }


         // write textfile
         String str = String.format("Created from text file %s",  sourceFile.getAbsolutePath());
         isDone     = IO.writeFileFromString(str, textFile );
        if (isDone == false){
            DisplayText.popupMessage("Failed to text file.");

            // delete previously written files
            IO.emptyDirectory( distDir);

            return false;
        }

        return true;
    }


    public static void main (String []args){
        File src   = new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/Ir_fidImage");
        File dist  = new File("/Users/apple/Bayes/exp1/diff45_SEPR.fid");
        src         =   new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/diiff45SEPR");
        if(dist.exists() == false){dist.mkdirs();}

        Ascii2FidImageConverter  converter        =  new Ascii2FidImageConverter ();
        boolean isSuccess           = converter.readAndWrite(src, dist);

        if (isSuccess == false) {return;}

        //ImageViewer.getInstance().resetImageSettings();
        //ImageViewer.getInstance().loadDefaultFile();
        //interfacebeans.AllViewers.showImageViewer();
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
