/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import utilities.MathFunctions;
/**
 *
 * @author apple
 */
public class BrukerProcess {
     public static double   findMaxTau( double [][] data){
      // data in the form of data [2][data]
      // first dimension  - reals [0][data]
      // second dimension - imag  [1][data]
      int n                        =   data[0].length;
      double [] work               =   new   double [n] ;

      for (int curPoint = 0; curPoint < n ; curPoint++) {
          double re             =   data[0][curPoint];
          double im             =   data[1][curPoint];
          work[curPoint]        =   re*re + im*im;
      }


    //sliding box average applied 5 times
     for ( int curCount = 0;   curCount < 5; curCount++) {
         for (int curPoint = 1; curPoint < n - 1; curPoint++) {

             double left            =    work [curPoint-1];
             double center          =    work [curPoint];
             double right           =    work [curPoint+1];
             double sum             =    left + 2*center + right;
             work[curPoint]     =    sum/4;
         }
     }



       double   max             =   0.0;
       int      iMax            =   0;

       for (int curPoint = 0; curPoint < n; curPoint++) {
           double curPwr = work[curPoint];
           if(curPwr > max){
                max     =   work[curPoint];
                iMax    =   curPoint;
            }
        }

       double tauRo = iMax + 1;

       return tauRo;

     }

     

    public static double findTauRo( double [][] data,double tauRo){
                                     
      // data in the form of data [2][data]
      // first dimension  - reals [0][data]
      // second dimension - imag  [1][data]
       double dValue            =   2;
       double aDelta            =   10;
       int maxIter              =   13;

       double [][] fft          =   utilities.cFFT.fft(data, -  1);
       double power             =   calcTotalPower( data);

       double tauRoCenter       =   tauRo;
       double probCenter        =   aLogpSmoothedData( tauRoCenter,aDelta,fft, power );


       double tauRoLow          =   tauRoCenter - dValue;
       double probLow           =   aLogpSmoothedData( tauRoLow,aDelta,fft,power );


       double tauRoHigh         =   tauRoCenter + dValue;
       double probHigh          =   aLogpSmoothedData( tauRoHigh,aDelta,fft,power );

       for (int curIter = 0; curIter < maxIter ; curIter++) {


          double tauRo0         =   0.5*(tauRoLow + tauRoCenter);
          double prob           =   aLogpSmoothedData( tauRo0,aDelta,fft,power );
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
          prob                  =   aLogpSmoothedData( tauRo0,aDelta,fft,power );


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
     //   System.out.println(probCenter );
       tauRo = tauRoCenter   ;

    return tauRo;
 }
    public static double aLogpSmoothedData(   double tauRo,
                                              double aDelta,
                                              double [][] data,
                                              double power){

      // data in the form of data [2][data]
      // first dimension  - reals [0][data]
      // second dimension - imag  [1][data]
        int n                       =   data[0].length;

        double prob                 =   0;
        double sinFreq              =   0;
        double cosHold              =   0;
        double cosFreq              =   0;
        double sinHold              =   0;
        double sinDelta             =   0;
        double cosDelta             =   0;

        double [] lDiag             =   new double [n];
        double [] uDiag             =   new double [n];
        double [] diag              =   new double [n];
        double [][] t               =   new double [n][2];
        double [][]rhs              =   new double [n][2];
        double [][] a               =   new double [n][2];

        double dWx                  = 2*Math.PI / n;

        for (int curPoint = 0; curPoint < n; curPoint++) {
            lDiag[curPoint]        = -1*aDelta*aDelta;
            diag [curPoint]        =  2*aDelta*aDelta + n;
            uDiag[curPoint]        = -1*aDelta*aDelta;
         }



            cosFreq         =   Math.cos(  Math.PI  * tauRo);
            cosDelta        =   Math.cos(-dWx       * tauRo);

            sinFreq         =   Math.sin( Math.PI   * tauRo);
            sinDelta        =   Math.sin(-dWx       * tauRo);

            for (int curPoint = 0; curPoint < n; curPoint++) {
                 double re              =    data[0][curPoint];
                 double im              =    data[1][curPoint];
                 t[curPoint][0]         =    cosFreq * re - sinFreq * im;
                 t[curPoint][1]         =    sinFreq * re + cosFreq * im;

                 rhs[curPoint][0]       =   t[curPoint][0];
                 rhs[curPoint][1]       =   t[curPoint][1];

                 cosHold                =   cosFreq * cosDelta - sinFreq * sinDelta;
                 sinHold                =   cosFreq * sinDelta + sinFreq * cosDelta;
                 cosFreq                =   cosHold;
                 sinFreq                =   sinHold ;

  
            }

            double aDet             =    MathFunctions.tridag(lDiag,diag,uDiag,rhs,2,a,n);

            double r                =   0;
            double i                =   0;
            double p                =   0;

             for (int curPoint = 0; curPoint < n; curPoint++) {
                 double xj  =   a[curPoint][0] * t[curPoint][0];
                 double yj  =   a[curPoint][0] * t[curPoint][1] + a[curPoint][1]*t[curPoint][0];
                 double zj  =   a[curPoint][1] * t[curPoint][1];

                 p          =   p + 0.5*(xj + zj);
                 r          =   r + xj - zj;
                 i          =   i + yj;
             }

             double h2      =   p + 0.5*Math.sqrt( r*r + i*i );
             prob           =   prob - 0.5*aDet+ n * Math.log( aDelta / (1.00000001 - h2/power));
                                
             

       
       return prob;
    }
     
    public static double  calcTotalPower( double data [][] ) {
      // data in the form of data [2][data]
      // first dimension  - reals [0][data]
      // second dimension - imag  [1][data]
          int n                           =    data[0].length;
          double power                    =    0;


         for (int curPoint = 0; curPoint < n ;  curPoint++) {
              double re             =  data[0][curPoint];
              double im             =  data[1][curPoint];
              power                 =  power + re*re + im*im;
          }

          return power ;
      }

    public static double calcPhase(double [][] data ){
          int n                             =   data[0].length;
          double i                          =   0;
          double r                          =   0;

            for (int curPoint = 0;curPoint  <  n ; curPoint ++) {

                double tRW                  =   data[0][curPoint ];
                double tIW                  =   data[1][curPoint] ;

                r                           =  r + tRW*tRW - tIW*tIW;
                i                           =  i + 2*tRW*tIW ;
            }


        double phase                 = Math.PI + Math.atan2(-i,r)/2;
        return  phase;

    }

    public static  double [][] applyPhaseAndTau(      double [][] data,
                                                        double phase,
                                                        double tau
                                                       ){

      int n                            =   data[0].length;
    

      double dwRo                       =   2 * Math.PI/n;
      for (int curPe = 0; curPe < n; curPe++) {



             double cosFreq                     =   Math.cos(+Math.PI *tau  + phase);
             double cosDelta                    =   Math.cos(-dwRo * tau);

             double sinFreq                     =   Math.sin(+Math.PI  * tau  + phase);
             double sinDelta                    =   Math.sin(-dwRo * tau);


             for (int curPoint = 0;  curPoint < n;  curPoint++) {
                   double re                    =   data[0][ curPoint] ;
                   double im                    =   data[1][ curPoint] ;
                   double trw                   =   cosFreq * re - sinFreq * im;
                   double tiw                   =   sinFreq * re + cosFreq * im;
                   data[0][curPoint]            =   trw;
                   data[1][curPoint]            =   tiw;

                   double cosHold               =   cosFreq * cosDelta - sinFreq * sinDelta;
                   double sinHold               =   cosFreq * sinDelta + sinFreq * cosDelta;
                   cosFreq                      =   cosHold;
                   sinFreq                      =   sinHold;
              }



        }



      return data;
      }


   public static double calculatePhase(double [][] fft ){

           // data in the form of data [2][phaseEncode][readout]
          int n                             =  fft [0].length;


          double i                          =   0;
          double r                          =   0;

            for (int curPoint = 0;curPoint <  n ; curPoint++) {

             double tRW                  =  fft[0][curPoint];
             double tIW                  =  fft[1][curPoint];

             r                           =  r + tRW*tRW - tIW*tIW;
             i                           =  i + 2*tRW*tIW ;
            }




        double phase                    = Math.PI + Math.atan2(-i,r)/2;

        return phase ;
 }
   public static  double [][] applyPhase(   double [][]fft,
                                            double tau,
                                            double phase ){
                                                      

      int n                              =   fft[0].length;
      /*
       double dwRo                        =   2 * Math.PI/n;
        
      double cosFreq                     =   Math.cos(+Math.PI * tau  + phase);
      double cosDelta                    =   Math.cos(-dwRo * tau );

      double sinFreq                     =   Math.sin(+Math.PI  * tau   +  phase) ;
      double sinDelta                    =   Math.sin(-dwRo *tau );


      for (int curPoint = 0; curPoint < n; curPoint++) {
          /*
           double re                    =   fft[0][curPoint] ;
           double im                    =   fft[1][curPoint] ;
           double trw                   =   (cosFreq * re    - sinFreq * im);
           double tiw                   =   (sinFreq * re    + cosFreq * im);
           fft[0][curPoint]             =   trw;
           fft[1][curPoint]             =   tiw;

           double cosHold               =   cosFreq * cosDelta - sinFreq * sinDelta;
           double sinHold               =   cosFreq * sinDelta + sinFreq * cosDelta;
           cosFreq                      =   cosHold;
           sinFreq                      =   sinHold;

     }
   */
      for (int curPoint = 0; curPoint < n; curPoint++) {

           double ph                    =   2 * Math.PI*tau*curPoint/n;
           double cos                   =   Math.cos(ph);
           double sin                   =   Math.sin(ph);
           double re                    =   fft[0][curPoint] ;
           double im                    =   fft[1][curPoint] ;

           fft[0][curPoint]             =   cos * re    - sin * im;
           fft[1][curPoint]             =   sin * re    + cos * im;

     }


   return fft;
}

}
