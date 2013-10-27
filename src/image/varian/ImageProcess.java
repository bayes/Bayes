/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import static java.lang.Math.*;
import static utilities.MathFunctions.*;
import java.util.Random;
/**
 *
 * @author apple
 */
public class ImageProcess {
    private ImageProcess(){};


     public static double   findMaxRo( int curStart, int curBy,  double [][][] data){
      // data in the form of data [2][phaseEncode][readout]
      int nPe                        =   data[0].length;
      int nRo                        =   data[0][0].length;
      double [][] work               =   new   double [nRo] [nPe];

      for (int curPe =   curStart ; curPe < nPe ; curPe  = curPe + curBy ){
          for (int curRo = 0; curRo <   nRo ;   curRo++) {

              double re             =   data[0][curPe][curRo];
              double im             =   data[1][curPe][curRo];

              work[curRo][curPe]    =   re*re + im*im;
          }


          //sliding box average applied 5 times
          for ( int curCnt = 0; curCnt < 5; curCnt++) {
                 for (int curRo = 1; curRo < nRo-1; curRo++) {
                     double left            =    work [curRo-1][curPe];
                     double center          =    work[curRo][curPe];
                     double right           =    work [curRo+1][curPe];
                     double sum             =    left + 2*center + right;
                     work[curRo][curPe]     =    sum/4;
                 }

           }

      }


       double   max             =   0.0;
       int      iMax            =   0;
       double   [] pwr          =   new double [nRo];

       for (int curRo = 0; curRo < nRo; curRo++) {
            pwr[curRo]          =   0;

            for (int curPe =   curStart; curPe < nPe; curPe  = curPe + curBy) {
                pwr[curRo]      =   pwr[curRo] + work[curRo][curPe];
            }

            if(pwr[curRo] > max){
                max     =   pwr[curRo];
                iMax    =   curRo;
            }

        }

       double tauRo = iMax + 1;

       return tauRo;

     }
     public static double   findMaxPe(int curStart, int curBy, double [][][] data){
      // data in the form of data [2][phaseEncode][readout]
      int nPe                        =   data[0].length;
      int nRo                        =   data[0][0].length;
      double [][] work               =   new   double [nRo] [nPe];

      for (int curRo =   curStart; curRo < nRo ; curRo  = curRo + curBy ){

          for (int curPe = 0;curPe  <   nPe ;   curPe ++) {
              double re             =   data[0][curPe][curRo];
              double im             =   data[1][curPe][curRo];

              work[curRo][curPe]    =   re*re + im*im;
          }


          //sliding box average applied 5 times
          for ( int curCnt = 0; curCnt < 5; curCnt++) {
                 for (int curPe = 1; curPe < nPe-1; curPe++) {
                     double left            =    work [curRo][curPe - 1];
                     double center          =    work [curRo][curPe];
                     double right           =    work [curRo][curPe + 1];
                     double sum             =    left + 2*center + right;
                     work[curRo][curPe]     =    sum/4;
                 }

           }

      }


       double   max             =   0.0;
       int      iMax            =   0;
       double   [] pwr          =   new double [nPe];
       for (int curPe = 0; curPe < nPe; curPe++) {
            pwr[curPe]      =   0;

            for (int curRo = curStart ; curRo < nRo; curRo  = curRo + curBy) {
                pwr[curPe]      =   pwr[curPe] + work[curRo][curPe];
            }

            if(pwr[curPe] > max){
                max     =   pwr[curPe];
                iMax    =   curPe;
            }

        }

       double tauRo = iMax + 1;

       return tauRo;

     }
     public static double [][][] phaseTauRo(double [][][]data, double tauRo){
           // data in the form of data [2][phaseEncode][readout]
            int nPe                        =    data[0].length;
            int nRo                        =    data[0][0].length;
            double dwRo                    =    2*PI/nRo;

          for (int curPe = 0; curPe < nPe; curPe ++) {
            double cosFreq              =   cos(+PI *tauRo);
            double cosDelta             =   cos(-dwRo*tauRo);
            double sinFreq              =   sin(+PI *tauRo);
            double sinDelta             =   sin(-dwRo*tauRo);

            for (int curRo = 0; curRo < nRo; curRo++) {
                double tmp1             =   cosFreq*data[0][curPe][curRo];
                double tmp2             =   sinFreq*data[1][curPe][curRo];
                double tRw              =   tmp1 - tmp2;

                double tmp3             =   sinFreq*data[0][curPe][curRo];
                double tmp4             =   cosFreq*data[1][curPe][curRo];
                double tIw              =   tmp3 + tmp4;

                data[0][curPe][curRo]   =   tRw;
                data[1][curPe][curRo]   =   tIw;



                double cosHold          =   cosFreq  * cosDelta - sinFreq * sinDelta;
                double sinHold          =   cosFreq  * sinDelta + sinFreq * cosDelta;
                cosFreq                 =   cosHold;
                sinFreq                 =   sinHold;
             }
         }

         return data;

       }
     public static double [][][] phaseTauPe( double [][][]data, double tauPe){
           // data in the form of data [2][phaseEncode][readout]
            int nPe                         =   data[0].length;
            int nRo                         =   data[0][0].length;
            double dwPe                     =   2*PI/nPe;

           for (int curRo = 0; curRo < nRo; curRo ++) {
            double cosFreq              =   cos(+PI *tauPe);
            double cosDelta             =   cos(-dwPe*tauPe);

            double sinFreq              =   sin(+PI *tauPe);
            double sinDelta             =   sin(-dwPe*tauPe);

            
            for (int curPe = 0; curPe < nPe; curPe++) {
                double tmp1             =   cosFreq*data[0][curPe][curRo];
                double tmp2             =   sinFreq*data[1][curPe][curRo];
                double tRw              =   tmp1 - tmp2;

                double tmp3             =   sinFreq*data[0][curPe][curRo];
                double tmp4             =   cosFreq*data[1][curPe][curRo];
                double tIw              =   tmp3 + tmp4;

                data[0][curPe][curRo]   =   tRw;
                data[1][curPe][curRo]   =   tIw;



                double cosHold          =   cosFreq  * cosDelta - sinFreq * sinDelta;
                double sinHold          =   cosFreq  * sinDelta + sinFreq * cosDelta;
                cosFreq                 =   cosHold;
                sinFreq                 =   sinHold;

             }
         }

         return data;

       }



     private static NoiseStat  calculateNoiseSdev( int totalDataValues,double [][][] image){

         // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   image[0].length;
          int nRo                           =   image[0][0].length;
          double amp2                       =   0.0;
          double [][] work                  =   new double [2][9];
          double sigmaP                     =   0.0;
          double [][] temp                  =   new double [nPe][nRo];
          SignalDetect signalDetect         =   null;

          for (int curRo = 0; curRo < nRo - 2; curRo += 3) {
              for (int curPe = 0; curPe < nPe - 2; curPe+=3) {

                  int n             =   0;

                  for (int thePe = curPe; thePe <= curPe + 2; thePe++) {
                      for (int theRo = curRo; theRo <= curRo + 2; theRo++) {


                          work[0][n]    =   image[0][thePe][theRo]/totalDataValues;
                          work[1][n]    =   image[1][thePe][theRo]/totalDataValues;



                          amp2          =   work[0][n]*work[0][n] + work[1][n]*work[1][n];

                          n             =   n + 1;
                          if(amp2 > sigmaP) { sigmaP = amp2;}

                      }
                  }
                signalDetect = signalDetect( n,  work);

                for (int thePe = curPe; thePe <= curPe + 2; thePe++) {
                     for (int theRo = curRo; theRo <= curRo + 2; theRo++) {
                            temp[thePe][theRo] = signalDetect.pNoise;
                    }
                }

              }
         }

         double maxProb         =   0;
         for (int curRo = 0; curRo < nRo; curRo ++) {
              for (int curPe = 0; curPe < nPe; curPe++) {
                  double curProb = temp[curPe][curRo];
                  if (maxProb < curProb){
                        maxProb = curProb;
                  }

              }
         }
         int nn                 =   0;
         double  sig            =   0;

         StringBuilder mask      =   new StringBuilder();
          for (int curRo = 0; curRo < nRo; curRo ++) {
              for (int curPe = 0; curPe < nPe; curPe++) {
                  double curProb = temp[curPe][curRo];
                  if ( curProb > maxProb - 0.1){
                         double re  =   image[0][curPe][curRo];
                         double im  =   image[1][curPe][curRo];
                         sig        =   sig + (re*re + im*im )/totalDataValues/totalDataValues;
                         nn         =   nn + 2;
                         
                        mask.append(1+ " ");
                  }
                  else{
                        mask.append(0+ " ");
                  }


              }
              mask.append("\n");
         }


         sigmaP                 =   0.5*sqrt(sigmaP);
         double noiseSd         =   sqrt( sig / nn);

         NoiseStat noiseStat    =   new NoiseStat();
         noiseStat.sigmaP       =   sigmaP;
         noiseStat.noiseSd      =   noiseSd;

         
       //  utilities.IO.writeFileFromString(mask.toString(), new java.io.File ("mask"));
         return noiseStat;
     }
     private static SignalDetect  signalDetect( int n, double [][]data){

         double d1BarR      =   0;
         double d2BarR      =   0;
         double d1BarI      =   0;
         double d2BarI      =   0;
         double dataR       =   0;
         double dataI       =   0;
         double d2          =   0;

         for (int i = 0; i < n; i++) {
              dataR         =   data[0][i];
              dataI         =   data[1][i];

              d1BarR        =   d1BarR + dataR;
              d2BarR        =   d2BarR + dataR*dataR;

              d1BarI        =   d1BarI + dataI;
              d2BarI        =   d2BarI + dataI*dataI;

          }


         d2                 =   d2BarR + d2BarI;

         d1BarR             =   d1BarR / n;
         d2BarR             =   d2BarR / n;

         d1BarI             =   d1BarI / n;
         d2BarI             =   d2BarI / n;

         double pNoise      =   -n*log(2*PI)
                                +aGamma(n)
                                - n*log(0.5 * n * (d2BarR + d2BarI)) ;



         double pSignal     =   -(n - 1)*log(2*PI)
                                - 2*log(5.0)
                                + aGamma(n -1)
                                - (n - 1)*log(0.5 * n * (d2BarR + d2BarI)
                                - 0.5*n*(d1BarR*d1BarR + d1BarI*d1BarI) );

         pSignal            =   pSignal - pNoise;

         pSignal            =   exp(pSignal);
         pNoise             =   1.0;
         double total       =   pSignal + pNoise;
         pSignal            =   pSignal / total;
         pNoise             =   pNoise  / total;

         SignalDetect signalDetect  = new SignalDetect();
         signalDetect.pNoise        =    pNoise;
         signalDetect.d2            =    d2;
         signalDetect.pSignal       =    pSignal;


         return signalDetect;
     }
     public static void  computeNonLinearPhase( int totalDataValues, int phaseEncodePos,int redoutPos, double [][][] data,
                                                double noiseSd, double sigmaP, Random rng ){

        double re           =   data [0][phaseEncodePos][redoutPos];
        double im           =   data [1][phaseEncodePos][redoutPos];
        double phase        =   0;
        double beta         =   noiseSd / sigmaP;
        double amp          =   sqrt(im*im + re*re)/totalDataValues;
        double sig          =   noiseSd * sqrt(1.0 + beta*beta) / amp;
        double that         =   atan2(-im,re);

        double t            =   re*cos(that) - im*sin(that);

        if(t < 0){ that = that + PI; }


        if(amp/noiseSd > 2.5){

                double gaus =   rng.nextGaussian();
                phase       =   that + sig*gaus;
        }
        else {
                phase       = 0   + 2*PI*rng.nextDouble();
        }

        double aReal        =   re*cos(phase) - im*sin(phase);
        double aImag        =  -re*sin(phase) - im*cos(phase);


        data [0][phaseEncodePos][redoutPos] = aReal;
        data [1][phaseEncodePos][redoutPos] = aImag;



     }
     public static void nonLinearPhaseing(int totalDataValues,double [][][] image,double noiseSd, double sigmaP ){
        // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   image[0].length;
          int nRo                           =   image[0][0].length;
            Random rng          =   new Random();
          for (int curRo = 0; curRo < nRo; curRo++) {
              for (int curPe = 0; curPe < nPe; curPe++) {

                 computeNonLinearPhase(totalDataValues, curPe, curRo, image,noiseSd,sigmaP, rng );
              }

          }

      }

     public static void calculateNoiseAndApplyNonLinearPhasing( int totalDataValues,double [][][] image){

        NoiseStat noiseStat =   calculateNoiseSdev(totalDataValues, image);
        double    noiseSd   =   noiseStat.noiseSd;
        double    sigmaP    =   noiseStat.sigmaP;
        nonLinearPhaseing(totalDataValues, image,noiseSd, sigmaP);
     }









}
     class SignalDetect{
        public  SignalDetect(){};
        double pNoise   =   0.0;
        double pSignal  =   0.0;
        double d2       =   0.0;
      }
      class NoiseStat{
        public  NoiseStat(){};
        double noiseSd   =   0.0;
        double sigmaP    =   0.0;
      }