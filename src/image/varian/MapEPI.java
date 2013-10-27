/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import static java.lang.Math.*;
import static utilities.cFFT.*;
import static image.varian.ImageProcess.*;
/**
 *
 * @author apple
 */
public class MapEPI {



    public static double [] [] []  processMapEpiImage(  int nPhaseEncode,
                                                        int nReadOut,
                                                        int phaseEncodePad,
                                                        int readOutPad,
                                                        double ppe,
                                                        double lpe,
                                                        boolean phase,
                                                        double cmplxData [][][],
                                                        double map [][][] ) {
        int totalDataPoints     =   nReadOut *  nPhaseEncode;
        double [][][] image     =   null;
        double [][] phasesRO    =   null;
        int      start          =   0;
        int      by             =   1;
        double tauPe            =   findMaxPe( start , by, cmplxData);
        
       
        image                   =   fft2Dim2(cmplxData,readOutPad,Math.PI,  1);
        //if (phase){
            phasesRO                =   epiMapCalcPhaseMap( readOutPad,map,  1);
            image                   =   epiMapApplyMap(phasesRO , image);
        //}
        
        image                   =   MRFFT.ftPE(image,phaseEncodePad, ppe, lpe);

        if (phase){
           image                   =   phaseTauPe(image, tauPe);
           calculateNoiseAndApplyNonLinearPhasing( totalDataPoints,image);
        }
        

        return image;

    }


    public static double  [][]      epiMapCalcPhaseMap(int readOutPad,double [][][] map, int sign){
                                                        
       // data in the form of data [2][phaseEncode][readout]
       int nPe                           =   map[0].length;
       int nRo                           =   map[0][0].length;
       double [][] xMapEpiPhase       =     new double[nPe][readOutPad ];
       double [][][] xFT              =     fft2Dim2 (map, readOutPad,Math.PI, sign);

       for (int curRo = 0; curRo < readOutPad; curRo ++) {
              for (int curPe = 0; curPe <  nPe ; curPe++) {
                    double re                   =   xFT[0][curPe][curRo];
                    double im                   =   xFT[1][curPe][curRo];
                    xMapEpiPhase[curPe][curRo]  =   atan2(im,re);
              }
       }

       return xMapEpiPhase;
     }
    public static double  [][][]    epiMapApplyMap( double [][] phases ,double [][][] cmplxData ){
                                                       
       // data in the form of data [2][phaseEncode][readout]
       int nPe                        =   cmplxData[0].length;
       int nRo                        =   cmplxData[0][0].length;
    
      for (int curPe = 0; curPe < nPe ; curPe++) {
           for (int curRo = 0; curRo <nRo; curRo ++) {
                    double re                   =   cmplxData[0][curPe][curRo];
                    double im                   =   cmplxData[1][curPe][curRo];
                    double phase                =   phases[curPe][curRo];

                    //double real                 =   + re*cos(phase) + im*sin(phase);
                    //double imag                 =   + re*sin(phase) - im*cos(phase);
                    
                    double real                 =   + re*cos(phase) + im*sin(phase);
                    double imag                 =   - re*sin(phase) + im*cos(phase);
                    cmplxData[0][curPe][curRo]  =   real;
                    cmplxData[1][curPe][curRo]  =   imag;
              }
       }

       return cmplxData;
     }

}