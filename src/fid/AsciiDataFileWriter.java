/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import bayes.DirectoryManager;
import fid.BayesParamsReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class AsciiDataFileWriter {
   public static String frmt             = "%.7E";
   public static   String pad            =  " ";
   public static   int padlen            =  14 ;

    public static String getContent(                String [][] abscissa,
                                                     List<Double> data1,
                                                     List<Double> data2,
                                                     boolean isMultiplePeaks,
                                                     boolean isMultipleAbs){
        String content = "";
        if   (isMultiplePeaks   == true && isMultipleAbs  == true){
            content           =  AsciiDataFileWriter.writeMultiAbscissaData(abscissa, data1, data2);
        }
        else if (isMultiplePeaks == true && isMultipleAbs == false){
            content           =   AsciiDataFileWriter.writeSingleAbscissaData(abscissa, data1, data2);
        }
        else if (isMultiplePeaks == false && isMultipleAbs == true){
            content           =   AsciiDataFileWriter.writeMultiAbscissaData(abscissa, data1);
        }
        else if (isMultiplePeaks == false && isMultipleAbs == false ){
            content           =   AsciiDataFileWriter.writeSingleAbscissaData(abscissa, data1);
        }
        return content;
    }





   public static String writeMultiAbscissaData(     String [][] abscissa,
                                                     List<Double> data1,
                                                     List<Double> data2){
        StringBuilder sb        = new StringBuilder();

        for (int curRow = 0; curRow < data1.size(); curRow++) {
            String abs          =   "";
            String d1           =   IO.pad( String.format(frmt,  data1.get(curRow)), padlen, pad);
            String d2           =   IO.pad( String.format(frmt,  data2.get(curRow)), padlen, pad);
            String row          =   IO.pad( curRow  ,6, " ");

            for (int curAbs = 0; curAbs < abscissa.length; curAbs++) {
               abs +=  pad ;
               abs += IO.pad( abscissa[curAbs][curRow], padlen, pad);

            }

            sb.append(row +pad  + d1+ pad + d2 + pad + abs);
            sb.append("\n");
        }


        return sb.toString();
    }

   public static String writeMultiAbscissaData(     String [][] abscissa,
                                                    List<Double> data1){

        StringBuilder sb        = new StringBuilder();

        for (int curRow = 0; curRow < data1.size(); curRow++) {
            String abs          =   "";
            String d1           =   IO.pad( String.format(frmt,  data1.get(curRow)), padlen, pad);
            String row          =   IO.pad( curRow , 6, " ");

            for (int curAbs = 0; curAbs < abscissa.length; curAbs++) {
               abs +=  pad ;
               abs += IO.pad( abscissa[curAbs][curRow], padlen, pad);

            }

            sb.append(row + pad + d1 + pad  + abs);
            sb.append("\n");
        }


        return sb.toString();
    }
   public static String writeSingleAbscissaData(    String [][] abscissa,
                                                     List<Double> data1,
                                                     List<Double> data2){
        StringBuilder sb        = new StringBuilder();

        for (int curRow = 0; curRow < data1.size(); curRow++) {
            String abs          =   IO.pad( abscissa[0][curRow], padlen, pad);
            String d1           =   IO.pad( String.format(frmt,  data1.get(curRow)), padlen, pad);
            String d2           =   IO.pad( String.format(frmt,  data2.get(curRow)), padlen, pad);


             sb.append( abs + pad + d1 +pad +  d2);
             sb.append("\n");
        }


        return sb.toString();
    }

   public static String writeSingleAbscissaData(    String [][] abscissa,List<Double> data1) {
        StringBuilder sb        =  new StringBuilder();

        for (int curRow = 0; curRow < data1.size(); curRow++) {
            String abs          =   IO.pad( abscissa[0][curRow], padlen, pad);
            String d1           =   IO.pad( String.format(frmt,  data1.get(curRow)), padlen, pad);

            sb.append( abs+ pad + d1 );
            sb.append("\n");
        }


        return sb.toString();
    }



  public static String writeNonJointData(    String [][] abscissa){
     int absSize                    =   abscissa.length;
      if (absSize > 1){
        return writeNonJointMultipleAbscissaData(abscissa);
      }
      else{
         return writeNonJointSingleAbscissaData(abscissa);
      }

  }
  public static String writeNonJointSingleAbscissaData(    String [][] abscissa) {
     
      

      StringBuilder sb        =  new StringBuilder();
      try{
        int absissaSize                   =   abscissa[0].length;
        for (int curTrace = 0; curTrace  <  absissaSize  ; curTrace ++) {
          File curModelFile               =   DirectoryManager.getIndexedModelFile( curTrace +1 ) ;
          if (curModelFile.exists() == false){
                String error        = String.format("File %s \n" +
                                                "Is not found. Abort load... ",
                                                  curModelFile.getPath()
                                                );
            DisplayText.popupErrorMessage(error);
            return null;
          }
          
          
          BayesParamsReader bpr             =   new BayesParamsReader(curModelFile);
          double ampl                       =   bpr.getResonances().get(0).getAmplitudes().get(0);
          String abs                        =   IO.pad( abscissa[0][curTrace], padlen, pad);
          String d1                         =   IO.pad( String.format(frmt, ampl ), padlen, pad);

          sb.append( abs+ pad + d1 );
          sb.append("\n");
        }
      
      }
      catch ( Exception e){
          e.printStackTrace();
          if (e != null && e.getMessage().length() > 0){

              String error        = String.format("Error while attempting to extract\n" +
                                                 "resonance amplitues from bayes analyze files.\n" +
                                                 "Error message: %s\n"+
                                                 "Abort load... ",
                                                  e.getMessage()
                                                );
            DisplayText.popupErrorMessage(error);
          }
      }



        return sb.toString();
    }
  public static String writeNonJointMultipleAbscissaData(    String [][] abscissa) {



      StringBuilder sb        =  new StringBuilder();
      try{
        int absissaSize                   =   abscissa[0].length;
        for (int curTrace = 0; curTrace  <  absissaSize  ; curTrace ++) {
          File curModelFile               =   DirectoryManager.getIndexedModelFile( curTrace +1 ) ;
          if (curModelFile.exists() == false){
                String error        = String.format("File %s \n" +
                                                "Is not found. Abort load... ",
                                                  curModelFile.getPath()
                                                );
            DisplayText.popupErrorMessage(error);
            return null;
          }


          BayesParamsReader bpr             =   new BayesParamsReader(curModelFile);
          double ampl                       =   bpr.getResonances().get(0).getAmplitudes().get(0);
          String abs                        =   IO.pad( abscissa[0][curTrace], padlen, pad);
          String d1                         =   IO.pad( String.format(frmt, ampl ), padlen, pad);
          String row                        =   IO.pad( curTrace , 6, " ");

           for (int curAbs = 0; curAbs < abscissa.length; curAbs++) {
               abs +=  pad ;
               abs += IO.pad( abscissa[curAbs][curTrace], padlen, pad);

            }

            sb.append(row + pad + d1 + pad  + abs);
            sb.append("\n");


        }

      }
      catch ( Exception e){
          e.printStackTrace();
          if (e != null && e.getMessage().length() > 0){

              String error        = String.format("Error while attempting to extract\n" +
                                                 "resonance amplitues from bayes analyze files.\n" +
                                                 "Error message: %s\n"+
                                                 "Abort load... ",
                                                  e.getMessage()
                                                );
            DisplayText.popupErrorMessage(error);
          }
      }



        return sb.toString();
    }



  public static int getNumberOfColumns(  int nAbscissa, int nDataColumn ){

       int n  =  nAbscissa +  nDataColumn;
       if (nAbscissa  > 1) {n += 1;}

       return n;

    }

  }
