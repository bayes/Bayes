/*
 * cFFT.java
 *
 * Created on October 25, 2007, 9:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utilities;
import java.util.Arrays;
import static java.lang.Math.*;
/**
 *
 * @author apple
 */
public class cFFT {
    
    /** Creates a new instance of cFFT */
    public cFFT () {
    }
    
    /**************************************
     *  Replaces data [0...2*nh -1} by its discrete Fourier transform,
     * if isign is input as 1;
     * or replaces data [0...2*nh -1] by nn times its inverse
     * dscrete Fourier transform, if isign is input a ?1.
     * data is acomplex array of length nn or, equivalently, 
     * a real array of length 2*nn. 
     * nn MUST be an integer power of 2[this is not checkedfor!]. 
    */
    public static final double [] fft(double [] data, int isign){
        int      nh             = data.length/2;
        int      n, j, i, iSTEP, m, mMAX;
        double   aREAL , aIMAG, theta, wpREAL , wpIMAG, wREAL,  wIMAG,  wTEMP;
       
        n                       =   2*nh;
        j                       =   1;
        
       
        for (i = 1; i <= n  ; i+=2){
           if(j > i){
               
                   aREAL        = data[j-1];
                   aIMAG        = data[j];
                   data[j-1]    = data[i-1];
                   data[j]      = data[i];
                   data[i-1]    = aREAL;
                   data[i]      = aIMAG;
            }
        
            m = n/2;
            while ( m >= 2  &&  j > m ) {
                   j = j - m;
                   m = m/2;
            }

            j = j + m;
        }

    
        mMAX = 2 ;
        while (n  > mMAX ){
            iSTEP   = 2*mMAX;
            theta   = 2*PI / (isign * mMAX);
            wpREAL  = -2.0 * pow(sin(0.5*theta),2);
            wpIMAG  = sin(theta);
            wREAL   = 1.0;
            wIMAG   = 0.0;
            for (m = 1; m <=  mMAX; m+= 2){
                for(i = m; i <= n; i+= iSTEP){
                     j=i + mMAX;
                    
                     aREAL      =   wREAL *data[j-1]    -   wIMAG * data[j];
                     aIMAG      =   wREAL * data[j]     +   wIMAG * data[j-1];
                     data[j-1]  =   data[i-1]           -   aREAL;
                     data[j]    =   data[i]             -   aIMAG;
                     data[i-1]  =   data[i-1]           +   aREAL;
                     data[i]    =   data[i]             +   aIMAG;
                }
               wTEMP            =   wREAL;
               wREAL            =   wREAL * wpREAL     -    wIMAG * wpIMAG + wREAL;
               wIMAG            =   wIMAG * wpREAL     +    wTEMP * wpIMAG + wIMAG;

            }
            mMAX = iSTEP;
            
        }
       return data; 
    }


   
    public static final float  [] fft(float [] data, int isign){
        int     nh = data.length/2;
        int      n, j, i, iSTEP, m, mMAX;
        float   aREAL , aIMAG, theta, wpREAL , wpIMAG, wREAL,  wIMAG,  wTEMP;
       
        n   =   2*nh;
        j   =   1;
        
        // ask larry
        for (i = 1; i <= n  ; i+=2){
           if(j > i){
                   aREAL        = data[j-1];
                   aIMAG        = data[j];
                   data[j-1]    = data[i-1];
                   data[j]      = data[i];
                   data[i-1]    = aREAL;
                   data[i]      = aIMAG;
            }
        
            m = n/2;
            while ( m >= 2  &&  j > m ) {
                   j = j - m;
                   m = m/2;
            }

            j = j + m;
        }
       
        mMAX = 2 ;
        
        while (n  > mMAX ){
            iSTEP   =  2*mMAX;
            theta   =  2*(float)PI / (isign * mMAX);
            wpREAL  =  -2 * (float)pow(sin(0.5f*theta),2);
            wpIMAG  =  (float) sin(theta);
            wREAL   =  1.0f;
            wIMAG   =  0.0f;
            for (m = 1; m <=  mMAX; m+= 2){
                for(i = m; i <= n; i+= iSTEP){
                     j=i + mMAX;
                     aREAL      =   wREAL *data[j-1]   - wIMAG * data[j]; 
                     aIMAG      =   wREAL * data[j]    + wIMAG * data[j-1];
                     data[j-1]  =   data[i-1]  - aREAL;
                     data[j]    =   data[i]- aIMAG;
                     data[i-1]  =   data[i-1]  + aREAL;
                     data[i]    =   data[i]+ aIMAG;
                }
               wTEMP  =wREAL;
               wREAL  =wREAL * wpREAL     - wIMAG * wpIMAG + wREAL;
               wIMAG  =wIMAG * wpREAL     + wTEMP * wpIMAG + wIMAG;
            }
            mMAX = iSTEP;
            
        }
       return data; 
    }


     public static final double  [][] doFFT( int cno, double [][] theFFT, int fftSize, double shift , int isign){
       //This routine does the actual fft of the data and then
       //stores it in the appropirate place
       // data [2][] => data [0][] = real dimenion, data [1][] = imag dimension


       //This routines input is in thefft so zero pad the end of this vector
       if(cno < fftSize){
           for (int i = cno; i < fftSize; i++) {
               theFFT[0][i]  = 0;
               theFFT[1][i]  = 0;

           }
       }


      // Shift the frequencies so they run from -pi to pi
       double cosa      =   cos(shift);
       double sina      =   sin(shift);
       double theCos    =   1.0;
       double theSin    =   0.0;


       for (int curFreq = 0; curFreq < fftSize ; curFreq++) {
          double wtemp       =  theCos*theFFT[0][curFreq] - theSin*theFFT[1][curFreq];
          theFFT[1][curFreq] =  theCos*theFFT[1][curFreq] + theSin*theFFT[0][curFreq];
          theFFT[0][curFreq] =  wtemp;
          double aa          =  cosa * theCos - sina * theSin;
          double bb          =  cosa * theSin + sina * theCos;
          theCos             =  aa;
          theSin             =  bb;

       }

    
       double [] data       =   new double [2 * fftSize];
       for (int i = 0; i < fftSize; i++) {
              data[2*i   ] =   theFFT[0][i] ;
              data[2*i+1]  =   theFFT[1][i];

       }

       data                 =   fft(data, isign);

       for (int i = 0; i < fftSize; i++) {
            theFFT[0][i] = data [2*i];
            theFFT[1][i] = data [2*i+ 1];

       }

       return theFFT;
    }
     
     
       public static final float  [][] doFFT(float [][] theFFT, double shift , int isign){
            int fftSize      =   theFFT[0].length;
            if(shift != 0){
                // Shift the frequencies so they run from -pi to pi
                double cosa      =   cos(shift);
                double sina      =   sin(shift);
                double theCos    =   1.0;
                double theSin    =   0.0;



                for (int curFreq = 0; curFreq < fftSize ; curFreq++) {
                  double wtemp       =  theCos*theFFT[0][curFreq] - theSin*theFFT[1][curFreq];
                  theFFT[1][curFreq] = (float)( theCos*theFFT[1][curFreq] + theSin*theFFT[0][curFreq]);
                  theFFT[0][curFreq] = (float) wtemp;
                  double aa          =  cosa * theCos - sina * theSin;
                  double bb          =  cosa * theSin + sina * theCos;
                  theCos             =  aa;
                  theSin             =  bb;

                }

            }
            


           float [] data       =   new float [2 * fftSize];
           for (int i = 0; i < fftSize; i++) {
                  data[2*i   ] =   theFFT[0][i] ;
                  data[2*i+1]  =   theFFT[1][i];

           }
           data                 =   fft(data, isign);

           for (int i = 0; i < fftSize; i++) {
                theFFT[0][i] = data [2*i];
                theFFT[1][i] = data [2*i+ 1];

           }

       return theFFT;
    }


      public static final double [] slowFT ( double [] data, int isign){
        /* Data assumed to be a one-dimensional array of
         interleaved real and imaginary numbers
         */
        int      fftsize        =   data.length/2;
        double   dw             =   2*PI /fftsize;
        int     cno             =   fftsize;
        double [] out           =   new double [2 *fftsize];

        for (int curFreq = 0; curFreq < fftsize; curFreq++) {

            double Freq         =   isign*(0.0 + dw*(curFreq - 1.0));

            double cos_freq     = 1.0;
            double cos_delta    = Math.cos(Freq);
            double sin_freq     = 0.0;
            double sin_delta    = Math.sin(Freq);

            double TotalR       = 0.0;
            double TotalI       = 0.0;


            for (int curTime = 0; curTime < cno ; curTime++) {
               int reIndx       =   curTime*2;
               int imIndx       =   curTime*2 + 1;

               TotalR           =   TotalR + cos_freq* data[reIndx ] - sin_freq* data[imIndx ];
               TotalI           =   TotalI + sin_freq* data[reIndx ] + cos_freq* data[imIndx ];

               double hold_cos  =   cos_freq  * cos_delta - sin_freq * sin_delta;
               double hold_sin  =   cos_freq  * sin_delta + sin_freq * cos_delta;
               cos_freq         =   hold_cos;
               sin_freq         =   hold_sin;
               

               

            }

            out [curFreq *2 ]    =    TotalR;
            out [curFreq *2+1 ]  =    TotalI;

        }

       
       return out;
    }



   
    public static final double [] normalize(double [] data, double norm){
        double [] results = new double [data.length];   
        for (int i = 0; i <results.length; i++) {results[i]= data[i]/norm;}
        return results;
    } 
    public static final double [] getEven(double [] complex){
        double [] even = new double [complex.length/2];
        for (int i = 0; i < even.length; i++) {
            even[i] = complex[2*i];
        }
        return even;
    }
    public static final double [] getOdd (double [] complex){
        double [] even = new double [complex.length/2];
        for (int i = 0; i < even.length; i++) {
            even[i] = complex[2*i+1];
        }
        return even;
    }
    public static final double [] getAmplitude (double [] complex){
        double [] real  = getEven(complex);
        double [] imag  = getOdd(complex);
        double [] ampl  = new double [real.length];
        
        for (int i = 0; i <ampl.length; i++) {
            ampl[i] = hypot(real [i], imag[i]);
        }
        return  ampl;
    }
    public static final double [] real2cmplx (double []real){
        double [] complex  = new double [2*real.length];
        for (int i = 0; i <real.length; i++) {
          complex[2*i] = real[i];
        }
        return complex;
    }
    public static final double [] imag2cmplx (double []imag){
        double [] complex  = new double [2*imag.length];
        for (int i = 0; i < imag.length; i++) {
          complex[2*i+1] = imag[i];
        }
        return complex;
    }
    public static final double [] doPadding(double [] data){
       int n = (int )ceil (log(data.length)/log(2));
       
       int  length = (int) pow (2, n); 
       
       if (data.length == length){  return data;}
       else {
            double []   results = new double [length];
            System.arraycopy (data, 0, results, 0, data.length);
           // java.util.Arrays.fill (results, data.length,results.length - 1,0.0f);
       return results;
       }
    }
    public static final double [] doPadding(double [] data, int n){
       if (data.length >= n){  return data;}
       else {
            double []   results = new double [n];
            System.arraycopy (data, 0, results, 0, data.length);
       return results;
       }
    }
   
    
    // DATA REPRESENTED AS A TWO DIMENSIONAL ARRAY OF DOUBLES
    public static final double [][] fft(double [][] data, int isign){
        int n                   =   data[0].length;
        double [] data1D        =   new double [2*n];
        double [] fft;
        double [] [] fft_data   =   new double [2][n];
        
        for (int i = 0; i < n ; i++) {
            data1D[2*i + 0 ] = data[0][i];
            data1D[2*i + 1 ] = data[1][i];
        }
        fft = fft(data1D, isign);
        
  
        
        for (int i = 0; i < n; i++) {
           fft_data[0][i] = fft[2*i + 0];
           fft_data[1][i] = fft[2*i + 1];
        }
        
        
       return fft_data;
    } 
    public static final double [][] normalize(double [][] data, double norm){
        for ( int i = 0; i < data.length; i++){
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] /=  norm;
            }
        }
        return data;
    } 
    public static final double [][] doPadding(double [][] data){
        int old_length = data[0].length;
        int n = (int )ceil (log(old_length)/log(2));
        int  length = (int) pow (2, n); 
       
        if (old_length == length){  return data;}
        else {
            double [][]   results = new double [data.length][length];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, data[i].length);
            }
       return results;
       }
    }
    public static final double [][] doPadding(double [][] data, int n){
       if (data[0].length >= n){  return data;}
       else {
            double [][]   results = new double [data.length][n];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, data[i].length);
            }
       return results;
       }
    }
    public static final double [][] shift(double [] [] data, double radians){
       double cosa      = cos(radians);
       double sina      = sin(radians);
       double thecos    = 1.0;
       double thesin    = 0.0;
       
       double  aa, bb;
       for (int i = 0; i < data[0].length; i++) {
          double re        = data[0][i];
          double im        = data[1][i];
          data[0][i]        = thecos*re - thesin*im;
          data[1][i]        = thecos*im + thesin*re;
          
          aa               = cosa * thecos - sina* thesin;
          bb               = cosa * thesin + sina* thecos;
          thecos           = aa;
          thesin           = bb;
       }
       return data;
    }
    public static final double []   get_mag(double [] [] fft){
        double [] magnitude = new double [fft[0].length] ;
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = hypot(  fft[0][i]  , fft[1][i]  );
        }
       return magnitude;
    }
    public static final double [][][] FFT2D (double data [][][], int outDim1,  int outDim2, int isign){
    // first dimension  in data     =    2 (0 - for real, 1 - for imag)
    // second dimension in data     =   - number if rows
    // third  dimension in data     =   - number of columns

       data                          =   resize (data, outDim1, outDim2);
       double [][] row               =   new double [2][outDim2];
       double [][] column            =   new double [2][outDim1];
       double FFT_in_1D [] [] []     =   new double [2][outDim1][outDim2];
       double FFT_in_2D [] [] []     =   new double [2][outDim1][outDim2];




      for (int i = 0; i <outDim1; i++) {
           System.arraycopy (data[0][i], 0, row[0], 0, outDim2  ); //real
           System.arraycopy (data[1][i], 0, row[1], 0, outDim2  ); //imag

           row =   cFFT.shift    (row, PI );
           row =   cFFT.fft      (row, isign  );

           System.arraycopy (row[0] , 0, FFT_in_1D[0][i], 0, outDim2);
           System.arraycopy (row[1] , 0, FFT_in_1D[1][i], 0, outDim2);
      }

        
      // populate rows

       for (int i = 0; i < outDim2; i++) {
            for (int j = 0; j < outDim1; j++) {
                column[0][j] = FFT_in_1D[0][j][i];
                column[1][j] = FFT_in_1D[1][j][i];
           }

           column =   cFFT.shift    (column, PI );
           column =   cFFT.fft      (column, isign );


          for (int j = 0; j <outDim1; j++) {
                FFT_in_2D[0][j][i] =   column[0][j];
                FFT_in_2D[1][j][i] =   column[1][j];
           }
       }
       return FFT_in_2D;
    }
    public static final double [][][] resize (double data [][][], int outDim1,  int outDim2){
    // first dimension  in data     =    2 (0 - for real, 1 - for imag)
    // second dimension in data     =   - number if rows
    // third  dimension in data     =   - number of columns
       int inDim1                   =  data[0].length;
       int inDim2                   =  data[0][0].length;
       double out [] [] []          =   new double [2][outDim1][outDim2];
       int minDim1                  =  Math.min(inDim1, outDim1);
       int minDim2                  =  Math.min(inDim2, outDim2);



      for (int i = 0; i <minDim1; i++) {
                for (int j = 0; j <minDim2; j++) {
                   out[0][i][j]         =   data[0][i][j];
                   out[1][i][j]         =   data[1][i][j];
               }
      }
       
      for (int i = minDim1; i < outDim1; i++) {
                for (int j = minDim2; j <  outDim2; j++) {
                   out[0][i][j]         =   0;
                   out[1][i][j]         =   0;
               }
      }

       return out;
    }

    public static final double [][][] fft2Dim1 (double data [][][], int padDim1, double shift, int isign){
    // first dimension  in data     =    2 (0 - for real, 1 - for imag)
    // second dimension in data     =   - number if rows
    // third  dimension in data     =   - number of columns
       int inDim1                   =  data[0].length;
       int inDim2                   =  data[0][0].length;
       int outDim1                  =  (padDim1 < 1) ? inDim1 : padDim1   ;

       double [][] work              =   new double [2][outDim1];;
       double out [] [] []           =   new double [2][outDim1][inDim2];


       for (int i = 0; i < inDim2; i++) {
            for (int j = 0; j <inDim1; j++) {
                work[0][j] = data[0][j][i];
                work[1][j] = data[1][j][i];
           }

           Arrays.fill(work[0],inDim1, outDim1, 0f);             // real
           Arrays.fill(work[1],inDim1, outDim1, 0f);              //imag

           work =   cFFT.shift    (work, shift );
           work =   cFFT.fft      (work, isign );


          for (int j = 0; j <outDim1; j++) {
                out[0][j][i] =   work[0][j];
                out[1][j][i] =   work[1][j];
           }
       }
       return out;
    }
    public static final double [][][] fft2Dim2 (double data [][][], int padDim2, double shift,  int isign){
    // first dimension  in data     =    2 (0 - for real, 1 - for imag)
    // second dimension in data     =   - number if rows
    // third  dimension in data     =   - number of columns
       int inDim1                   =  data[0].length;
       int inDim2                   =  data[0][0].length;
       int outDim2                  =  (padDim2 <1 ) ? inDim2  : padDim2    ;

       double [][] work              =   new double [2][outDim2];;
       double out [] [] []           =   new double [2][inDim1][outDim2];


        for (int i = 0; i < inDim1; i++) {
           System.arraycopy (data[0][i], 0,  work[0], 0, inDim2 ); //real
           System.arraycopy (data[1][i], 0,  work[1], 0, inDim2 ); //imag

           // zero pad
           Arrays.fill(work[0],inDim2, outDim2, 0f);             // real
           Arrays.fill(work[1],inDim2, outDim2, 0f);             // imag

           work                     =   cFFT.shift    (work, shift );
           work                     =   cFFT.fft      (work, isign  );

           System.arraycopy (work[0] , 0, out[0][i], 0, outDim2);
           System.arraycopy (work [1], 0, out[1][i], 0, outDim2);
           
       }
       return out;
    }

// DATA REPRESENTED AS A TWO DIMENSIONAL ARRAY OF FLOATS
    public static final float []    doPadding(float [] data){
       int n = (int )ceil (log(data.length)/log(2));
       
       int  length = (int) pow (2, n); 
       
       if (data.length == length){  return data;}
       else {
            float []   results = new float [length];
            System.arraycopy (data, 0, results, 0, data.length);
           // java.util.Arrays.fill (results, data.length,results.length - 1,0.0f);
       return results;
       }
    }
    public static final float []    doPadding(float [] data, int n){
       if (data.length >= n){  return data;}
       else {
            float []   results = new float [n];
            System.arraycopy (data, 0, results, 0, data.length);
       return results;
       }
    }
    public static final float [][]  doPadding(float  [][] data){
        int old_length = data[0].length;
        int n = (int )ceil (log(old_length)/log(2));
        int  length = (int) pow (2, n); 
        if (old_length == length){  return data;}
        else {
            float [][]   results = new float [data.length][length];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, data[i].length);
            }
       return results;
       }
    }
    public static final float [][]  doPadding(float [][] data, int n){
       if (data[0].length >= n){  return data;}
       else {
            float [][]   results = new float [data.length][n];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, data[i].length);
            }
       return results;
       }
    }
    public static final float [][]  doTruncate(float [][] data, int n){
       if (data[0].length <= n){  return data;}
       else {
            float [][]   results = new float [data.length][n];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, n);
            }
       return results;
       }

    }
    public static final float [][]  copy(float [][] data){
       int dataLength           =   data[0].length;
       
       float [][]   results = new float [data.length][dataLength ];
            for (int i = 0; i < data.length; i++) {
              System.arraycopy ( data[i], 0,  results[i], 0, dataLength   );
            }
       return results;

    }
    public static final float [][]  truncateOrPad(float [][] data, int n){
       int dataLength           =   data[0].length;
       if (dataLength  == n)        {   return copy(data);}
       else {
            if (n < dataLength)     {   return  doTruncate(data, n);}
            else                    {   return  doPadding (data, n);}
       }
      }
    public static final float [][] fft(float [][] data, int isign){
        int n           = data[0].length;
        float [] data1D = new float [2*n];
        float [] fft;
        float [] [] fft_data = new float [2][n];
        
        for (int i = 0; i < n ; i++) {
            data1D[2*i + 0 ] = data[0][i];
            data1D[2*i + 1 ] = data[1][i];
        }
        fft = fft(data1D, isign);

        
        for (int i = 0; i < n; i++) {
           fft_data[0][i] = fft[2*i + 0];
           fft_data[1][i] = fft[2*i + 1];
        }
        
        
       return fft_data;
    }
    public static final float [][] fft(int size, float [][] data, int paddedSize ,int isign){
        int n           = data[0].length;
        float [] data1D = new float [2*n];
        float [] fft;
        float [] [] fft_data = new float [2][n];

        for (int i = 0; i < n ; i++) {
            data1D[2*i + 0 ] = data[0][i];
            data1D[2*i + 1 ] = data[1][i];
        }
        fft = fft(data1D, isign);



        for (int i = 0; i < n; i++) {
           fft_data[0][i] = fft[2*i + 0];
           fft_data[1][i] = fft[2*i + 1];
        }


       return fft_data;
    }

    public static final float [][] normalize(float [][] data, float norm){
        for ( int i = 0; i < data.length; i++){
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] /=  norm;
            }
        }
        return data;
    } 
    
    public static final float [][] shift(float [] [] data, float radians){
       float cosa      = (float)cos(radians);
       float sina      = (float)sin(radians);
       float thecos    = 1.0f;
       float thesin    = 0.0f;
       
       float  aa, bb;
       for (int i = 0; i < data[0].length; i++) {
          float re          = data[0][i];
          float im          = data[1][i];
          data[0][i]        = thecos*re - thesin*im;
          data[1][i]        = thecos*im + thesin*re;

        //  data[0][i]        = thecos*data[0][i] - thesin*data[1][i];
         // data[1][i]        = thecos*data[1][i] + data[0][i];


          aa               = cosa * thecos - sina* thesin;
          bb               = cosa * thesin + sina* thecos;
          thecos           = aa;
          thesin           = bb;
       }
       return data;
    }
    public static final float []   get_mag(float [] [] fft){
        float [] magnitude = new float [fft[0].length] ;
        for (int i = 0; i < magnitude.length; i++) {
            magnitude[i] = (float)hypot(  fft[0][i]  , fft[1][i]  );
        }
       return magnitude;
    }





 

    public static void main(String [] args ){
        int n               =  16;
        double [] data      = new double [n];

        for (int i = 0; i <n; i+= 2) {
           data [i] = i;
           

        }
        for (int i = 0; i < data.length; i++) {
              String message= String.format("%2.1f", data[i] );
              System.out.print(" "+  message);

        }
        System.out.println("");
        double []dcopy        =   data.clone();

        
        double sft []     = cFFT.slowFT(dcopy,1);
        double fft []     = fft(data, 1);
        

        for (int i = 0; i < n; i++) {
           String message= String.format("%2.1f  %2.1f",fft[i] , sft[i]);

            System.out.println(message);
        }
    }
 
    
     public static  double [][] applyPhase( double [][]fft,
                                            double tau,
                                            double phase ){


      int n                              =   fft[0].length;
      double dwRo                        =   2 * Math.PI/n;

      double cosFreq                     =   -Math.cos(Math.PI * tau  + phase);
      double cosDelta                    =   Math.cos(-dwRo * tau );

      double sinFreq                     =   Math.sin(Math.PI  * tau   +  phase) ;
      double sinDelta                    =   Math.sin(-dwRo *tau );


      for (int curPoint = 0; curPoint < n; curPoint++) {
           double re                    =   fft[0][curPoint] ;
           double im                    =   fft[1][curPoint] ;
           double trw                   =   (cosFreq * re   - sinFreq * im);
           double tiw                   =   (sinFreq * re    + cosFreq * im);
           fft[0][curPoint]             =   trw;
           fft[1][curPoint]             =   tiw;

     
           double ph                    =   2 * Math.PI*tau*curPoint/n;
           double cos                   =   Math.cos(ph);
           double sin                   =   Math.sin(ph);

           //fft[0][curPoint]             =   cos * re    - sin * im;
          //fft[1][curPoint]             =   sin * re    + cos * im;

        // System.out.println("");
        // System.out.println("larry "+ curPoint + " "+ cosFreq +  " " + sinFreq);
        // System.out.println("Karen "+ curPoint + " "+ cos           + " " + sin);
        System.out.println(cosFreq + " "+ cos);


           double cosHold               =   cosFreq * cosDelta - sinFreq * sinDelta;
           double sinHold               =   cosFreq * sinDelta + sinFreq * cosDelta;
           cosFreq                      =   cosHold;
           sinFreq                      =   sinHold;

     }


   return fft;
}
     
     
  
}
