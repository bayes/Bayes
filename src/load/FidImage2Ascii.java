/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.BayesManager;
import image.varian.VarianFidImage;
import image.varian.VairanImageReader;
import java.io.*;
import fid.*;
import utilities.*;
/**
 *
 * @author apple
 */
public class FidImage2Ascii {
    public void convertESPR( File imageDir, File asciiFile){
        BufferedWriter out = null;
        try {
            VarianFidImage fidImage   = VairanImageReader.readKSpaceFid(imageDir);
            Procpar procpar     = fidImage.getProcpar();
            int nPhaseEncode    = procpar.getNumberOfPhaseEncodePoints();
            int nReadOut        = procpar.getNumberOfReadOutPoints();
            int nSclices        = procpar.getNs();
            int nElements       = procpar.getNumberOfElements();
            int totalPoints     = nReadOut * nPhaseEncode * nSclices * nElements;
            double[][] values   = new double[2][totalPoints];
            int count           = 0;
            for (int curElem = 0; curElem < nElements; curElem++) {
                for (int curSlice = 0; curSlice < nSclices; curSlice++) {

                    double[][][] curImag = fidImage.getComplexKSpace(curSlice, curElem);

                    for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {
                        for (int curReadOut = 0; curReadOut < nReadOut; curReadOut++) {
                            values[0][count] = curImag[0][curPhaseEncode][curReadOut];
                            values[1][count] = curImag[1][curPhaseEncode][curReadOut];
                            count++;
                        }
                    }
                }
            }
           // System.out.println("totalPoints "+totalPoints);
           // System.out.println("populated points "+ count);

            out = new BufferedWriter(new FileWriter(asciiFile));
            String line;
            for (int i = 0; i < totalPoints; i++) {
                line = values[0][i] + " " + values[1][i];
                out.write(line);
                out.newLine();
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {}
        }

    }
    public void convertSEPR( File imageDir, File asciiFile){
        BufferedWriter out = null;
        try {
            VarianFidImage fidImage   = VairanImageReader.readKSpaceFid(imageDir);
            Procpar procpar     = fidImage.getProcpar();
            int nPhaseEncode    = procpar.getNumberOfPhaseEncodePoints();
            int nReadOut        = procpar.getNumberOfReadOutPoints();
            int nSclices        = procpar.getNs();
            int nElements       = procpar.getNumberOfElements();
            int totalPoints     = nReadOut * nPhaseEncode * nSclices * nElements;
            double[][] values   = new double[2][totalPoints];
            int count           = 0;

            for (int curSlice = 0; curSlice < nSclices; curSlice++) {
                    for (int curElem = 0; curElem < nElements; curElem++) {


                    double[][][] curImag = fidImage.getComplexKSpace(curSlice, curElem);

                    for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {
                        for (int curReadOut = 0; curReadOut < nReadOut; curReadOut++) {
                            values[0][count] = curImag[0][curPhaseEncode][curReadOut];
                            values[1][count] = curImag[1][curPhaseEncode][curReadOut];
                            count++;
                        }
                    }
                }
            }
           // System.out.println("totalPoints "+totalPoints);
           // System.out.println("populated points "+ count);

            out = new BufferedWriter(new FileWriter(asciiFile));
            String line;
            for (int i = 0; i < totalPoints; i++) {
                line = values[0][i] + " " + values[1][i];
                out.write(line);
                out.newLine();
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {}
        }

    }


   public boolean writeAbscissaFile(File dest, String [][] abscissaValues){
        StringBuffer sb = new StringBuffer();
        int numParamters    =   abscissaValues.length;
        int numValues       =   abscissaValues[0].length;
        String space        =   " ";

        for (int i = 0; i < numValues; i++) {
            for (int j = 0; j < numParamters ; j++) {
               sb.append( abscissaValues[j][i]);
               sb.append(space);
            }
            sb.append(BayesManager.EOL);

        }


        boolean isDone = IO.writeFileFromString(sb.toString(), dest);

        return isDone;
    }
   public void writeAbscissaFromProcparFile(File procparFile, File abscissaFile){
    Procpar procpar                 = new Procpar(procparFile);
    String [][] abscissaValues      = procpar.getArrayValues();
    writeAbscissaFile( abscissaFile, abscissaValues) ;

   }
    public static void main (String [] args ){
        File src = new File("/Users/apple/BayesSys/Bayes.test.data/images/ir_image.fid");
        File dst = new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/Ir_fidImage");
        src = new File("/Users/apple/BayesSys/Bayes.test.data/images/diff45_1.fid");
        dst = new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/diiff45SEPR");

        File procparFile    =   new File("/Users/apple/BayesSys/Bayes.test.data/images/diff45_1.fid/procpar");
        File abscissaFile   =   new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/abscissa_diiff45ESPR");

         new FidImage2Ascii().convertSEPR(src, dst);
        // new FidImage2Ascii().writeAbscissaFromProcparFile(procparFile,  abscissaFile );

    }
}
