/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import static java.lang.Math.*;
/**
 *
 * @author apple
 */
public class MathFunctions {
    private MathFunctions(){}

     public static double myMod(double phase){
        double myMod        = phase;
        while (myMod > PI){ myMod = myMod - 2*PI;}
        while (myMod < -PI){myMod = myMod + 2* PI;}
        return myMod;

       }
     public static double aGamma(double x){

       double [] taylor     =   new double []{    76.18009173,
                                                -86.50532033,
                                                24.01409822,
                                                -1.231739516,
                                                0.120858003E-2,
                                                -0.536382E-5};

       double z             =   2.50662827465;

       if(x == 0){return 0;}


       double y             =   x - 1;
       double work          =   y + 5.5;
       work                 =   (y + 0.5) * log(work) - work;
       double expand        =   1.0;


       for (int i = 0; i < 6; i++) {
          y                 =   y + 1;
          expand            =   expand + taylor[i] / y;
       }

       double agamma        =   work + log(z * expand);




       return agamma;

       }

     public static double logGamma(double x) {
      double tmp = (x - 0.5) * log(x + 4.5) - (x + 4.5);
      double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                       + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                       +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
      return tmp + log(ser * sqrt(2 * PI));
   }
     public static double gamma(double x) { return exp(logGamma(x)); }


     public static double aErfCC(double x){

      double z                  =   abs(x);
      double t                  =   1/(1 + 0.5 * z);

      double val                =   log(t) - z*z - 1.26551223
                                    + t * (1.00002368
                                    + t * (.37409196
                                    + t * (.09678418
                                    + t * (-.18628806
                                    + t * (.27886807
                                    + t * (-1.13520398
                                    + t * (1.48851587
                                    + t * (-.82215223
                                    + t * 0.17087277))))))));

      if(x > 0){
        val                     =  diffLog(log(2),val);
      }

      return val;
}

     public static double diffLog(double a, double b){

       double diffLog   =   Double.NaN;
       if( a == b){
         diffLog = -1E+50;
       }

       else if (a > b){
            diffLog = a + log( 1.0 - exp( b - a ) );
       }


       else{
            diffLog = b + log( exp( a - b ) - 1.0 );
       }
       return diffLog ;
  }

     public static final int getPaddedLength( int curLength){
        int n           = (int )ceil (log(curLength)/log(2));
        int  length     = (int) pow (2, n);

        return length;
      }

     public static double  tridag(  double [] lDiag,
                                    double[] diag,
                                    double [] udiag,
                                    double [][] rhs,
                                    int nRhs,
                                    double [][] solution,
                                    int n){



      double bet                    =   diag[0];
      double [] work                =   new double[n];
      double aDet                   =   log(bet);

      for (int curRHS = 0; curRHS < nRhs; curRHS++) {
           solution [0][curRHS] =   rhs [0][curRHS]/bet;


      }


      for (int curRow = 1; curRow < n ; curRow++) {
            work[curRow]        = udiag[curRow -1] / bet;
            bet                 = diag[curRow] - lDiag[curRow] * work[curRow];
            aDet                = aDet + log(bet);

            for (int curRHS = 0; curRHS < nRhs; curRHS++) {
                solution [curRow][curRHS] =   ( rhs [curRow][curRHS] - lDiag[curRow]
                                              * solution[curRow -1][curRHS]) / bet ;

            }
      }


      for (int curRow = n - 2; curRow >= 0 ; curRow--) {
            for (int curRHS = 0; curRHS < nRhs; curRHS++) {
                solution [curRow][curRHS] =   solution [curRow][curRHS]
                                              - work [curRow+1]*solution [curRow + 1][curRHS] ;

            }
      }

      return aDet;
    }


     public static double findMax(double [] data){
        double max      =   findMaxValAndLoc(data)[0];
        return max;
     }
     public static int findMaxLoc(double [] data){
        int index       =   (int)findMaxValAndLoc(data)[1];
        return index;
     }
     public static double [] findMaxValAndLoc(double [] data){
        double max      =   -Double.MAX_VALUE;
        int    index    =   -1;

         for (int i = 0; i < data.length; i++) {
             double d = data[i];
             if (max < d) {
                 max    = d;
                 index  = i;
             }

         }
        return new double []{max,index};
     }

     public static double findMin(double [] data){
        double min      = Double.MAX_VALUE;
        int    index    =   -1;

         for (int i = 0; i < data.length; i++) {
             double d = data[i];
             if (min > d) {
                 min    = d;
                 index  = i;
             }

         }


        return min;
     }


     public static double [][][] reverse2DimArray (double [][][] in){
         int dim1               =   in.length;
         int dim2               =   in[0].length;
         int dim3               =   in[0][0].length;

         double out [][][]      = new double [dim1 ][dim2][];
         for (int curDim1 = 0; curDim1 < out.length; curDim1++) {
            for (int curDim2     = 0;  curDim2 <dim2;  curDim2++) {
                    double []temp        = reverseArray( in [curDim1][ curDim2]);
                    out [curDim1][curDim2] = temp;
            }

         }

         return out;
     }
     public static double [][][] reverse3DimArray (double [][][] in){
         int dim1               =   in.length;
         int dim2               =   in[0].length;
         int dim3               =   in[0][0].length;

         double out [][][]      = new double [dim1 ][dim2][dim3];
         for (int curDim1 = 0; curDim1 < out.length; curDim1++) {
            for (int curDim2     = 0;  curDim2 <dim2;  curDim2++) {
                for (int curDim3 = 0; curDim3 < dim3; curDim3++) {
                    double val = in[curDim1][curDim2][curDim3];
                    out[curDim1][dim2 - 1 -curDim2][curDim3] = val;
                }
            }

         }

         return out;
     }
     public static double []  reverseArray( double [] array){
        int last = array.length -1;

        for (int left=0,  right = last; left < right; left++, right--) {
            double temp         =   array[left];
            array[left]         =   array[right];
            array[right]        =   temp;
        }
        return array;
    }

   public static void main(String[] args) {
      double x = 9;
      System.out.println(aGamma(x));
      System.out.println("Gamma(" + x + ") = " + gamma(x));
      System.out.println("log Gamma(" + x + ") = " + logGamma(x));
   }
}
