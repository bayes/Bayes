/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import static utilities.cFFT.*;
import static image.varian.ImageProcess.*;
/**
 *
 * @author marutyan
 */
public class SeGeProcess {
    private SeGeProcess (){};

public static double [] [] []  processSeGeImage(     int nPhaseEncode,
                                                     int nReadOut,
                                                     int phaseEncodePad,
                                                     int readOutPad,
                                                     double ppe,
                                                     double lpe,
                                                     boolean phase,
                                                     double cmplxData [][][] ) {
        double tauRo            =   0f;
        double tauPe            =   0f;
        int totalDataPoints     =   nReadOut *  nPhaseEncode;
        int      start          =   0;
        int      by             =   1;
       
        if (phase){
            tauRo                   =   findMaxRo( start , by, cmplxData);
            tauPe                   =   findMaxPe( start , by, cmplxData);
        }


        double [][][] roFt      =   fft2Dim2(cmplxData,readOutPad,Math.PI,  1);
        double [][][] image     =   MRFFT.ftPE(roFt, phaseEncodePad, ppe, lpe);


        if (phase){
            image                   =   phaseTauRo(image, tauRo);
            image                   =   phaseTauPe(image, tauPe);

            calculateNoiseAndApplyNonLinearPhasing( totalDataPoints,image);

        }
        

      

        return image;

    }

}
   

