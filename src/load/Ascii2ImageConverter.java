/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import load.gui.Ascii2ImageDialog;
import java.io.*;
import utilities.*;
import image.*;
import java.util.*;
/**
 *
 * @author apple
 */
public class Ascii2ImageConverter {
    private static  Ascii2ImageConverter instance;
    public static    Ascii2ImageConverter getInstance(){
        if ( instance == null){
             instance = new  Ascii2ImageConverter();
        }
        return instance;
    }
    private Ascii2ImageConverter(){
    }
    private ImageDescriptor  descriptor             =   new ImageDescriptor();
    private boolean slicesFirst                     =   false;
    private int numberOfLinesInFile                 =   0;
    private File abscissaFile                       =   null;
    private File asciiImageFile                     =   null;
    private boolean loadAbscissa                    =   false;

    public   boolean readAndWrite(File sourceFile) {
        String message              =    null;
        List<float[][]> allImages   =    null;
        float [] values             =    null;

        setAsciiImageFile(sourceFile);
        setLoadAbscissa(false);
        setAbscissaFile(null);

        int numberOfLines           =    0;
        try {
            numberOfLines = IO.getNumberOfLines(sourceFile);
            setNumberOfLinesInFile(numberOfLines);
        } catch (IOException ex) {
            DisplayText.popupMessage(String.format("Failed to parse file\n" +
                                                    "%s\n" +
                                                    "Exit.", sourceFile.getPath()));
            return false;
        }
        Ascii2ImageDialog.showDialog(this);
        if(Ascii2ImageDialog.isDialogCanceled() == true){return false;}
        try {
            values          = readFile(sourceFile, numberOfLines);


        } catch (NumberFormatException ex) {
             message = String.format( "Number format exception is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           return false;

        } catch (FileNotFoundException ex) {
            message = String.format(  "FileNotFoundException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           return false;
        } catch (IOException ex) {
             message = String.format( "IOException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           return false;
        }

        // make sure that files was read correctly
        if (values == null) {return false;}



        int nLines      =   numberOfLines;
        int nPoints     =   getDescriptor().getTotalNumberOfPoints();
        if (nLines != nPoints){
         message =  "Number of lines in the text file "+ nLines+"\n"+
                    "is not equal to the number of total pixels "+nPoints +"\n"+
                     "Exiting.\n";

           DisplayText.popupMessage(message);
           return false;
        }

        if (isSclicesFirst()){
             allImages     =   ImageConvertHelper.populateSERP(values,getDescriptor());
        }
        else{
             allImages     =  ImageConvertHelper.populateESRP(values,getDescriptor());
        }

         values        =   null;


        // write  binary .img filr and text ifh file
        // in the curreExp/fid directory
        boolean isWrite      =  ImageConvertHelper.writeImgFiles(sourceFile.getName(),allImages, getDescriptor());

       return  isWrite;
    }
    public  static float[] readFile(File sourceFile, int numberOfLines)
            throws NumberFormatException, FileNotFoundException, IOException{
     String strLine                 =   null;
     float [] values                =   new float [numberOfLines];

     try{
        FileInputStream fstream     =   new FileInputStream(sourceFile);
        BufferedReader br           =   new BufferedReader(new InputStreamReader(fstream), 1000000);
       // br = new BufferedReader(new FileReader(sourceFile));


       for (int curLine = 0; curLine <  numberOfLines ; curLine++) {
          strLine                       =   br.readLine();
          float f                       =   Float.parseFloat(strLine);
           values[curLine]              =   f;


        }

        //Close the input streams
        br.close();
        fstream.close();
    }catch (NumberFormatException exp){ throw exp;}
    catch (FileNotFoundException exp){ throw exp;}
    catch (IOException exp){ throw exp;}

    return values;
  }



    public   boolean readAndWriteMultipleColumns(File sourceFile) {
        String message              =    null;
        List<float[][]> allImages   =    null;
        int numberOfLines           =    0;
        int numberOfColumns         =    0;
        try {
            numberOfLines           =   IO.getNumberOfLines (sourceFile);
            setNumberOfLinesInFile(numberOfLines);

            numberOfColumns         =   IO.getNumberOfColumns(sourceFile);
        } catch (IOException ex) {
            DisplayText.popupMessage(String.format("Failed to parse file\n" +
                                                    "%s\n" +
                                                    "Exit.", sourceFile.getPath()));
            return false;
        }
        getDescriptor().setNumberOfColumns(numberOfColumns);
        getDescriptor().setNumberOfRows(numberOfLines);
        /*
        this.getDescriptor().setNumberOfRows(128);
        this.getDescriptor().setNumberOfSlices(1);
        this.getDescriptor().setNumberOfElements(1);
        */

        Ascii2ImageMultiColumnDialog.showDialog(this);
        if( Ascii2ImageMultiColumnDialog.isDialogCanceled() == true){return false;}
        try {
            allImages          = readMultiColumnFile(sourceFile, (numberOfLines));


        } catch (NumberFormatException ex) {
             message = String.format( "Number format exception is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupErrorMessage(message);
           ex.printStackTrace();
           return false;

        } catch (FileNotFoundException ex) {
            message = String.format(  "FileNotFoundException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupErrorMessage(message);
           ex.printStackTrace();
           return false;
        } catch (IOException ex) {
             message = String.format( "IOException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is\n" +
                                      "%s",sourceFile.getPath(),ex.getMessage() ) ;

           DisplayText.popupErrorMessage(message);
           ex.printStackTrace();
           return false;
        }

       
        if (this.isSclicesFirst()){

             allImages     =   ImageConvertHelper.SERP2ESPR( allImages ,getDescriptor());
        }
      



        // write  binary .img filr and text ifh file
        // in the curreExp/fid directory
        boolean isWrite      =  ImageConvertHelper.writeImgFiles(sourceFile.getName(),allImages, getDescriptor());

       return  isWrite;
    }
    public  List <float [][]> readMultiColumnFile(    File sourceFile, int NumberOfLinesInFile)
            throws NumberFormatException, FileNotFoundException, IOException{

     String strLine                 =   null;
     List <float [][]> allimages    =   new ArrayList<float[][]>();
     int width                      =   this.getDescriptor().getNumberOfColumns();
     int height                     =   this.getDescriptor().getNumberOfRows();
     

     try{
        FileInputStream fstream     =   new FileInputStream(sourceFile);
        BufferedReader br           =   new BufferedReader(new InputStreamReader(fstream), 1000000);



       int curLine = 0;
       while (curLine <  NumberOfLinesInFile){
           float [][] curImage            =   new float [width][height];
           for (int curRow = 0; curRow < height; curRow++) {
                strLine                       =   br.readLine();
                curLine                       +=  1;
                
                strLine                       =   strLine.trim();
                String []strs                 =   strLine.split("\\s+") ;

                if (strs.length != width){throw new NumberFormatException("Errors parsing line "+curLine);}

                for (int curCol = 0; curCol < width; curCol++) {
                 
                  
                    float f                       =   Float.parseFloat(strs [curCol]);
                    curImage [curCol][curRow]     =   f;
               }
           }
           allimages.add(curImage);
        }

        //Close the input streams
        br.close();
        fstream.close();
    }catch (NumberFormatException exp){ throw exp;}
    catch (FileNotFoundException exp){ throw exp;}
    catch (IOException exp){ throw exp;}

    return  allimages ;
  }



    public static void main(String args[]){
       
            File file = new File("/Users/apple/Bayes/Bayes.test.data/Ascii/singlecolImage49pixle.dat");
           Ascii2ImageConverter converter        =     Ascii2ImageConverter.getInstance();
            boolean isSuccess                     =     converter.readAndWrite(file);

            /*
            Ascii2ImageConverter c = new Ascii2ImageConverter(128  ,256,3,45);
            Ascii2ImageDialog.showDialog(c);
            if(Ascii2ImageDialog.isDialogCanceled() == true){return;}
          c.read(file);
             */
            long t1 = System.nanoTime();
          //   readFile(file);
             //readFile(file);
          // String content = IO.readFileToString(file);
           //content.split("\n");
            long t2 = System.nanoTime();
            double time = (t2-t1)*1e-9;
            System.out.println("time = "+time);
    }




    public ImageDescriptor getDescriptor () {
        return descriptor;
    }
    public void setDescriptor ( ImageDescriptor descriptor ) {
        this.descriptor = descriptor;
    }

  

    public boolean isSclicesFirst () {
        return slicesFirst;
    }
    public void setSclicesFirst ( boolean sclicesFirst ) {
        this.slicesFirst = sclicesFirst;
    }

    public  int getNumberOfLinesInFile(){
    return numberOfLinesInFile;
   }
    public  void setNumberOfLinesInFile(int nlines){
     numberOfLinesInFile = nlines;
   }

    public File getAbscissaFile() {
        return abscissaFile;
    }
    public void setAbscissaFile(File abscissaFile) {
        this.abscissaFile = abscissaFile;
    }

    public File getAsciiImageFile() {
        return asciiImageFile;
    }
    public void setAsciiImageFile(File asciiImageFile) {
        this.asciiImageFile = asciiImageFile;
    }

    public boolean isLoadAbscissa() {
        return loadAbscissa;
    }
    public void setLoadAbscissa(boolean loadAbscissa) {
        this.loadAbscissa = loadAbscissa;
    }
}
