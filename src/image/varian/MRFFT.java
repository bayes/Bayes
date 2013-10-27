/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import utilities.cFFT;
import static java.lang.Math.*;
/**
 *
 * @author apple
 */
public class MRFFT {
     public static  double [][][] ftPE(     double [][][] data,
                                            int ftPeSize,
                                            double ppe,
                                            double lpe
                                   ){

     // first dimension  in data     =    2 (0 - for real, 1 - for imag)
    // second dimension in data     =   - number of rows
    // third  dimension in data     =   - number of columns
       int peSize                   =  data[0].length;
       ftPeSize                     =  max(ftPeSize, peSize );

       double dd                     = 2*PI/ftPeSize;
       double shift, theSign;
       int  iShift                    = 0;

       if(ppe != 0 && lpe != 0){
         shift              = -2.0*PI*ppe/lpe;
         theSign            = shift/abs(shift);
         iShift             = (int)(abs(shift) / dd);

       }
       else {
         shift              = 0;
         iShift             = 0;
         theSign            = 1;
       }

       if(iShift < 0){ iShift = 0 ;}
       if(iShift > ftPeSize) {
           iShift = ftPeSize - 2;}

       double shiftLow  = theSign*iShift*dd;
       double shiftHigh = theSign*(iShift+1)*dd;


      if(abs(shift-shiftLow) > abs(shift-shiftHigh)){
        shift = shiftHigh;
      }
      else {
         shift = shiftLow;
      }

        // System.out.println("shift "+ shift);
       //   System.out.println("ishift "+ iShift);
      double [][][] out = cFFT.fft2Dim1 (data, ftPeSize ,  shift+PI, 1);
      return out;

 }
}
