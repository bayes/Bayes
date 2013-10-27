/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import static java.lang.Math.*;
import static utilities.MathFunctions.*;
import static utilities.cFFT.*;
import static image.varian.ImageProcess.*;
/**
 *
 * @author apple
 */
public class NonMapEpi {
    private  NonMapEpi(){};
         public static double [] [] []  processNonMapEpiImage(  int nPhaseEncode,
                                                                int nReadOut,
                                                                int phaseEncodePad,
                                                                int readOutPad,
                                                                double ppe,
                                                                double lpe,
                                                                boolean phase,
                                                                double cmplxData [][][] ) {

        double [][][] image     =   null;

        boolean isDoPhasing     =  phase && isDoPhasing ( nPhaseEncode,nReadOut,phaseEncodePad,readOutPad);

        if (isDoPhasing == true){

            image               =   procesWithPhasing(phaseEncodePad,readOutPad,ppe, lpe,cmplxData);

        }
        else {

            image                =   FFT2D(cmplxData, phaseEncodePad ,readOutPad, 1);
        }

        return image;

    }

     public static double [] [] []  procesWithPhasing  (    int peFtSize,
                                                            int roFtSize,
                                                            double ppe,
                                                            double lpe,
                                                            double cmplxData [][][] ) {

        int totalDataPoints     =   roFtSize *  peFtSize;
        double [][][] image     =   null;


        double []  d2nRo        =   calcD2nRo(cmplxData);
        double [][][] roFt      =   fft2Dim2(cmplxData,0,PI,  1);

        double tauRoOdd         =   findMaxRo(0, 1 , cmplxData);
        tauRoOdd                =   findTauRo(0,peFtSize, 2, roFt, d2nRo, tauRoOdd);

        double tauRoEven        =   findMaxRo(1, 2 , cmplxData);
        tauRoEven               =   findTauRo(1,peFtSize, 2, roFt, d2nRo,tauRoEven);

        roFt                    =   epiNonMapPhaseTauRo(roFt,tauRoOdd,tauRoEven);


        double []  d2nPe        =    calcD2nPe(roFt);
        image                   =    MRFFT.ftPE(roFt, peFtSize, ppe, lpe);

        double tauPe            =   findMaxPe(0, 1 , cmplxData);
        tauPe                   =   findTauPe(0, roFtSize, 1, image, d2nPe,tauPe );

        image                   =   phaseTauPe(image, tauPe);
        calculateNoiseAndApplyNonLinearPhasing( totalDataPoints,image);




        return image;

    }



      public static boolean isDoPhasing(  int nPhaseEncode,
                                        int nReadOut,
                                        int phaseEncodePad,
                                        int readOutPad) {


        int nPe                 = getPaddedLength(nPhaseEncode);
        int nRo                 = getPaddedLength(nReadOut);

        if (nPe             != nPhaseEncode ) {return false;}
        if (nRo             != nReadOut     ) {return false;}
        if (phaseEncodePad  > nPhaseEncode  ) {return false;}
        if (readOutPad      > nReadOut      ) {return false;}

        return true;

    }

     public static double []  calcD2nRo( double data [][][] ) {
      // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   data[0].length;
          int nRo                           =   data[0][0].length;

          double []d2nRo                    =   new double [nPe];

          for (int curPe = 0; curPe < nPe; curPe++) {
             d2nRo [curPe]                      =   0;
             
             for (int curRo = 0; curRo < nRo ;  curRo++) {
                  double val            =  d2nRo [curPe];
                  double re             =  data[0][curPe][curRo];
                  double im             =  data[1][curPe][curRo];

                  d2nRo [curPe]         =  val + re*re + im*im;
              }

          }

          return d2nRo ;
      }
     public static double []  calcD2nPe( double data [][][] ) {
      // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   data[0].length;
          int nRo                           =   data[0][0].length;

          double []d2nPe                    =   new double [nRo];

          for (int curRo = 0; curRo < nRo ;  curRo++) {

             d2nPe  [curRo]                      =   0;
                 for (int curPe = 0; curPe < nPe; curPe++) {

                  double val            =  d2nPe [curRo];
                  double re             =  data[0][curPe][curRo];
                  double im             =  data[1][curPe][curRo];

                  d2nPe  [curRo]        =  val + re*re + im*im;
              }
          }


          return d2nPe ;
      }


     public static double epiNonMapCalcOddPhase(double [][][] roFt ){
           // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   roFt[0].length;
          int nRo                           =   roFt[0][0].length;


          double i                          =   0;
          double r                          =   0;

         for (int curPe = 0; curPe < nPe; curPe = curPe + 2) {
            for (int curRo = 0;curRo <  nRo ; curRo++) {

                double tRW                  =  roFt[0][curPe][curRo];
                double tIW                  =  roFt[1][curPe][curRo];

                r                           =  r + tRW*tRW - tIW*tIW;
                i                           =  i + 2*tRW*tIW ;
            }
         }

    
        double oddPhase                 = PI + atan2(-i,r)/2;
        return oddPhase ;

    }

     public static double epiNonMapCalcEvenPhase(double [][][] roFt ){


           // data in the form of data [2][phaseEncode][readout]
          int nPe                           =   roFt[0].length;
          int nRo                           =   roFt[0][0].length;


          double i                          =   0;
          double r                          =   0;

          for (int curPe = 1; curPe < nPe; curPe = curPe + 2) {
            for (int curRo = 0;curRo <  nRo ; curRo++) {

                 double tRW                  =  roFt[0][curPe][curRo];
                 double tIW                  =  roFt[1][curPe][curRo];

                 r                           =  r + tRW*tRW - tIW*tIW;
                 i                           =  i + 2*tRW*tIW ;
            }
        }

       


        double evenPhase                 = PI + atan2(-i,r)/2;

        return evenPhase;
 }


    public static  double [][][] epiNonMapApplyPhases(double [][][]roFt,double oddPhase,double evenPhase) {
     // data in the form of data [2][phaseEncode][readout]
      int nPe                           =   roFt[0].length;
      int nRo                           =   roFt[0][0].length;

        for (int curPe = 0; curPe <nPe; curPe++) {

            int mod                             =  curPe%2;
            if(mod  == 1){

                double cosPhase                 =   cos(evenPhase);
                double sinPhase                 =   sin(evenPhase);

                for (int curRo = 0; curRo < nRo; curRo++) {
                   double re                    = roFt[0][curPe][curRo] ;
                   double im                    = roFt[1][curPe][curRo] ;
                   double trw                   = cosPhase * re - sinPhase * im;
                   double tiw                   = sinPhase * re + cosPhase * im;
                   roFt[0][curPe][curRo]        = trw;
                   roFt[1][curPe][curRo]        = tiw;

                }



            }
            else {

                double cosPhase                 =   cos(oddPhase);
                double sinPhase                 =   sin(oddPhase);
             for (int curRo = 0; curRo < nRo; curRo++) {
                   double re                    = roFt[0][curPe][curRo] ;
                   double im                    = roFt[1][curPe][curRo] ;
                   double trw                   = cosPhase * re - sinPhase * im;
                   double tiw                   = sinPhase * re + cosPhase * im;
                   roFt[0][curPe][curRo]        = trw;
                   roFt[1][curPe][curRo]        = tiw;


             }
         

           }

        }
        return roFt;
}


    public static  double [][][] epiNonMapPhaseTauRo(   double [][][]roFt,
                                                        double tauRoOdd,
                                                        double tauRoEven ){


       // data in the form of data [2][phaseEncode][readout]
      int nPe                           =   roFt[0].length;
      int nRo                           =   roFt[0][0].length;

      double dwRo                       =   2 * PI/nRo;
      for (int curPe = 0; curPe < nPe; curPe++) {

            int mod                             =  curPe%2;
            if(mod  == 1){


             double cosFreq                     =   cos(+PI *tauRoEven + Math.PI);
             double cosDelta                    =   cos(-dwRo * tauRoEven);

             double sinFreq                     =   sin(+PI  * tauRoEven+ Math.PI);
             double sinDelta                    =   sin(-dwRo * tauRoEven);


             for (int curRo = 0; curRo < nRo; curRo++) {
                   double re                    =   roFt[0][curPe][curRo] ;
                   double im                    =   roFt[1][curPe][curRo] ;
                   double trw                   =   cosFreq * re - sinFreq * im;
                   double tiw                   =   sinFreq * re + cosFreq * im;
                   roFt[0][curPe][curRo]        =   trw;
                   roFt[1][curPe][curRo]        =   tiw;

                   double cosHold               =   cosFreq * cosDelta - sinFreq * sinDelta;
                   double sinHold               =   cosFreq * sinDelta + sinFreq * cosDelta;
                   cosFreq                      =   cosHold;
                   sinFreq                      =   sinHold;
              }



            }

            else{


             double cosFreq                     =   cos(+PI * tauRoOdd  );
             double cosDelta                    =   cos(-dwRo * tauRoOdd);

             double sinFreq                     =   sin(+PI  * tauRoOdd  );
             double sinDelta                    =   sin(-dwRo *tauRoOdd );


             for (int curRo = 0; curRo < nRo; curRo++) {
                   double re                    =   roFt[0][curPe][curRo] ;
                   double im                    =   roFt[1][curPe][curRo] ;
                   double trw                   =   cosFreq * re - sinFreq * im;
                   double tiw                   =   sinFreq * re + cosFreq * im;
                   roFt[0][curPe][curRo]        =   trw;
                   roFt[1][curPe][curRo]        =   tiw;

                   double cosHold               =   cosFreq * cosDelta - sinFreq * sinDelta;
                   double sinHold               =   cosFreq * sinDelta + sinFreq * cosDelta;
                   cosFreq                      =   cosHold;
                   sinFreq                      =   sinHold;

              }
           }


      }
      return roFt;
}


   

    public static double findTauRo( int curStart,
                                    int curStop,
                                    int curBy,
                                    double [][][] roFt,
                                    double []d2nPe,
                                    double tauRo){

       double dValue            =   2;
       double aDelta            =   10;
       int maxIter              =   13;

       double tauRoCenter       =   tauRo;
       double probCenter        =   aLogpSmoothedRo(    curStart,curStop,curBy,
                                                        tauRoCenter,aDelta, roFt, d2nPe);

       
       double tauRoLow          =   tauRoCenter - dValue;
       double probLow           =   aLogpSmoothedRo(    curStart,curStop,curBy,
                                                        tauRoLow,aDelta,roFt,d2nPe);

       double tauRoHigh         =   tauRoCenter + dValue;
       double probHigh          =   aLogpSmoothedRo(    curStart,curStop,curBy,
                                                        tauRoHigh,aDelta,roFt,d2nPe);

       for (int curIter = 0; curIter < maxIter ; curIter++) {


          double tauRo0         =   0.5*(tauRoLow + tauRoCenter);
          double prob           =   aLogpSmoothedRo(    curStart,curStop,curBy,
                                                        tauRo0,aDelta,roFt,d2nPe);
          if (prob > probCenter){
             tauRoHigh          =   tauRoCenter;
             probHigh           =   probCenter;
             tauRoCenter        =   tauRo0;
             probCenter         =   prob;
          }
          else{
             tauRoLow           =   tauRo0;
             probLow            =   prob;
          }




          tauRo0                =   0.5*(tauRoHigh + tauRoCenter);
          prob                  =   aLogpSmoothedRo(    curStart,curStop,curBy,
                                                        tauRo0,aDelta,roFt,d2nPe);


          if (prob > probCenter){
             tauRoLow           =   tauRoCenter;
             probLow            =   probCenter;
             tauRoCenter        =   tauRo0;
             probCenter         =   prob;
          }
          else{
             tauRoHigh           =   tauRo0;
             probHigh            =   prob;
          }

       }

       tauRo = tauRoCenter   ;

    return tauRo;
 }

    public static double findTauPe(int curStart,
                                    int curStop,
                                    int curBy,
                                    double [][][] peFt,
                                    double []d2nPe,
                                    double tauPe){

       double dValue            =   2;
       double aDelta            =   10;
       int maxIter              =   13;

       double tauPeCenter       =   tauPe;


       double probCenter        =   aLogpSmoothedPe(    curStart,curStop,curBy,
                                                        tauPeCenter,aDelta, peFt, d2nPe);
     

       double tauPeLow          =   tauPeCenter - dValue;
       double probLow           =   aLogpSmoothedPe(    curStart,curStop,curBy,
                                                        tauPeLow,aDelta,peFt,d2nPe);

       double tauPeHigh         =   tauPeCenter + dValue;
       double probHigh          =   aLogpSmoothedPe(    curStart,curStop,curBy,
                                                        tauPeHigh,aDelta,peFt,d2nPe);

       for (int curIter = 0; curIter < maxIter ; curIter++) {


          double tauPe0         =   0.5*(tauPeLow + tauPeCenter);
          double prob           =   aLogpSmoothedPe(    curStart,curStop,curBy,
                                                        tauPe0,aDelta,peFt,d2nPe);

          if (prob > probCenter){
             tauPeHigh          =   tauPeCenter;
             probHigh           =   probCenter;
             tauPeCenter        =   tauPe0;
             probCenter         =   prob;
          }
          else{
             tauPeLow           =   tauPe0;
             probLow            =   prob;
          }




          tauPe0                =   0.5*(tauPeHigh + tauPeCenter);
          prob                  =   aLogpSmoothedPe(    curStart,curStop,curBy,
                                                        tauPe0,aDelta,peFt,d2nPe);


          if (prob > probCenter){
             tauPeLow           =   tauPeCenter;
             probLow            =   probCenter;
             tauPeCenter        =   tauPe0;
             probCenter         =   prob;
          }
          else{
             tauPeHigh           =   tauPe0;
             probHigh            =   prob;
          }

       }

       tauPe = tauPeCenter   ;

    return tauPe;
 }



     public static double aLogpSmoothedRo(    int curStart,
                                              int curStop,
                                              int curBy,
                                              double tauRo,
                                              double aDelta,
                                              double [][][]roFt,
                                              double []d2nRo){

         // data in the form of data [2][phaseEncode][readout]
        int nPe                        =   roFt[0].length;
        int nRo                        =   roFt[0][0].length;

        double prob                 =   0;
        double sinFreq              =   0;
        double cosHold              =   0;
        double cosFreq              =   0;
        double sinHold              =   0;
        double sinDelta             =   0;
        double cosDelta             =   0;

        double [] lDiag             =   new double [nRo];
        double [] uDiag             =   new double [nRo];
        double [] diag              =   new double [nRo];
        double [][] t               =   new double [nRo][2];
        double [][]rhs              =   new double [nRo][2];
        double [][] a               =   new double [nRo][2];

        double dWx                  = 2*PI / nRo;

        for (int curRo = 0; curRo < nRo; curRo++) {
            lDiag[curRo]        = -1*aDelta*aDelta;
            diag [curRo]        =  2*aDelta*aDelta + nRo;
            uDiag[curRo]        = -1*aDelta*aDelta;
         }



        for (int curPe = curStart; curPe < curStop; curPe  =  curPe + curBy ) {
            cosFreq         =   cos(  PI  * tauRo);
            cosDelta        =   cos(-dWx  * tauRo);

            sinFreq         =   sin( PI   * tauRo);
            sinDelta        =   sin(-dWx  * tauRo);
            
            for (int curRo = 0; curRo < nRo; curRo++) {
                 double re          =    roFt[0][curPe][curRo];
                 double im          =    roFt[1][curPe][curRo];
                 t[curRo][0]        =    cosFreq * re - sinFreq * im;
                 t[curRo][1]        =    sinFreq * re + cosFreq * im;

                 rhs[curRo][0]      =   t[curRo][0];
                 rhs[curRo][1]      =   t[curRo][1];

                 cosHold            =   cosFreq * cosDelta - sinFreq * sinDelta;
                 sinHold            =   cosFreq * sinDelta + sinFreq * cosDelta;
                 cosFreq            =   cosHold;
                 sinFreq            =   sinHold ;
                
                
            }

            double aDet             =   tridag(lDiag,diag,uDiag,rhs,2,a,nRo);

            double r                =   0;
            double i                =   0;
            double p                =   0;

             for (int curRo = 0; curRo < nRo; curRo++) {
                 double xj  =   a[curRo][0] * t[curRo][0];
                 double yj  =   a[curRo][0]*t[curRo][1] + a[curRo][1]*t[curRo][0];
                 double zj  =   a[curRo][1]*t[curRo][1];

                 p          =   p + 0.5*(xj + zj);
                 r          =   r + xj - zj;
                 i          =   i + yj;
             }

             double h2      =   p + 0.5*sqrt( r*r + i*i );
             prob           =   prob - 0.5*aDet
                                + nRo * log( aDelta / (1.00000001 - h2/d2nRo[curPe]));

     }

       return prob;
    }



      public static double aLogpSmoothedPe(   int curStart,
                                              int curStop,
                                              int curBy,
                                              double tauPe,
                                              double aDelta,
                                              double [][][]data,
                                              double []d2nPe){

         // data in the form of data [2][phaseEncode][readout]
        int nPe                        =   data[0].length;
        int nRo                        =   data[0][0].length;

        double prob                 =   0;
        double sinFreq              =   0;
        double cosHold              =   0;
        double cosFreq              =   0;
        double sinHold              =   0;
        double sinDelta             =   0;
        double cosDelta             =   0;

        double [] lDiag             =   new double [nPe];
        double [] uDiag             =   new double [nPe];
        double [] diag              =   new double [nPe];
        double [][] t               =   new double [nPe][2];
        double [][]rhs              =   new double [nPe][2];
        double [][] a               =   new double [nPe][2];

        double dwPe                  = 2*PI / nPe;

        for (int curPe = 0; curPe < nPe; curPe++) {
            lDiag[curPe]        = -1*aDelta*aDelta;
            diag [curPe]        =  2*aDelta*aDelta + nPe;
            uDiag[curPe]        = -1*aDelta*aDelta;
         }



        for (int curRo = curStart; curRo < curStop; curRo = curRo + curBy) {
            cosFreq         =   cos(  PI  * tauPe);
            cosDelta        =   cos(-dwPe  * tauPe);

            sinFreq         =   sin( PI   * tauPe);
            sinDelta        =   sin(-dwPe  * tauPe);

            for (int curPe = 0; curPe < nPe; curPe++) {
                 double re          =    data[0][curPe][curRo];
                 double im          =    data[1][curPe][curRo];
                 t[curPe][0]        =    cosFreq * re - sinFreq * im;
                 t[curPe][1]        =    sinFreq * re + cosFreq * im;

                 rhs[curPe][0]      =   t[curPe][0];
                 rhs[curPe][1]      =   t[curPe][1];

                 cosHold            =   cosFreq * cosDelta - sinFreq * sinDelta;
                 sinHold            =   cosFreq * sinDelta + sinFreq * cosDelta;
                 cosFreq            =   cosHold;
                 sinFreq            =   sinHold ;


            }

            double aDet             =   tridag(lDiag,diag,uDiag,rhs,2,a,nPe);

       
            double r                =   0;
            double i                =   0;
            double p                =   0;

             for (int curPe = 0; curPe < nPe; curPe++) {
                 double xj  =   a[curPe][0] * t[curPe][0];
                 double yj  =   a[curPe][0] * t[curPe][1] + a[curPe][1]*t[curPe][0];
                 double zj  =   a[curPe][1] * t[curPe][1];

                 p          =   p + 0.5*(xj + zj);
                 r          =   r + xj - zj;
                 i          =   i + yj;

            
             }

           

             double h2      =   p + 0.5*sqrt( r*r + i*i );
             prob           =   prob - 0.5*aDet
                                + nRo * log( aDelta / (1.00000001 - h2/d2nPe[curRo]));

     }

       return prob;
    }





 }