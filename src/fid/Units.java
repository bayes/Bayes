/*
 * Units.java
 *
 * Created on September 26, 2007, 3:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fid;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
public class Units {
   
    private Units ( ) {}
    
    
    public static float ppm2hertz(float  specFreq , float value){
         float result   = value *  specFreq  ;
         return result;
    }
    public static float ppm2hertz(Procpar procpar , float value){
         float sfeq     =  procpar.getSpectroscoperRefFrequency ();
         float result   =  ppm2hertz( sfeq , value) ;
         return result;
    }
    
    public static float hertz2ppm(float  specFreq , float value){
        float result     = value / specFreq  ;
        return result;
    }
    public static float hertz2ppm(Procpar procpar , float value){
        float sfeq       =  procpar.getSpectroscoperRefFrequency ();
        float result     = hertz2ppm(sfeq, value)  ;
        return result;
    }
   
    
    public static double ppm2hertz(Procpar procpar , double value){
         double sfeq     =  procpar.getSpectroscoperRefFrequency ();
         double result   = value * sfeq  ;
        
         return result;
    }
    public static double ppm2hertz(float  specFreq , double value){
         double result   = value *  specFreq  ;
         return result;
    }
    
    public static double hertz2ppm(Procpar procpar , double value){
        double sfeq       =  procpar.getSpectroscoperRefFrequency ();
        double result     =  value / sfeq  ;
      return result;
    }
    public static double hertz2ppm(float  specFreq , double value){
        double result     = value / specFreq  ;
        return result;
    } 
   
    public static float [] ppm2hertz( Procpar procpar, float [] values){
         float [] results = new float [values.length];
         for (int i = 0; i < results.length; i++) {
             results [i] = ppm2hertz(procpar, values [i]);
         }
      return results;
    }
   
     
         
    /************************************************************
     * The goal is to convert resonance variables units to desired untis.
     * This will be useful when writng Bayes.params file for example
     * @param val           : value of resonance variable to convert
     * @param procpar       : reference to procpar that will be used to units conversion
     * @param originalUnits: current units of resonance variables
     * @param desiredUnits  : finale units of converted values
     * @return              " converted value
     **************************************************************/
    public static double convertUnits(         double val, 
                                                Procpar procpar,
                                                UNITS originalUnits, 
                                                UNITS desiredUnits){
        
        double convertVal = val ;
        if (originalUnits ==  desiredUnits) {return convertVal;}
        
        switch(originalUnits){
        
            case PPM    : convertVal = Units.ppm2hertz(procpar, val);
                        break;
            
            case HERTZ  : convertVal = Units.hertz2ppm(procpar, val);
                        break;
            default     : convertVal = val;
                
        }
        
        return convertVal;
    }
    /************************************************************
     * The goal is to convert resonance variables units to desired untis.
     * This will be useful when writng Bayes.params file for example
     * @param val           : value of resonance variable to convert
     * @param specFrew      : spec frequency that will be used to units conversion
     * @param originalUnits : current units of resonance variables
     * @param desiredUnits  : finale units of converted values
     * @return              " converted value
     **************************************************************/
    public static float convertUnits(           float val, 
                                                float  specFreq,
                                                UNITS originalUnits, 
                                                UNITS desiredUnits){
        
        float convertVal = val ;
        if (originalUnits ==  desiredUnits) {return convertVal;}
        
        switch(originalUnits){
        
            case PPM    : convertVal = Units.ppm2hertz(specFreq, val);
                        break;
            
            case HERTZ  : convertVal = Units.hertz2ppm(specFreq, val);
                        break;
            default     : convertVal = val;
                
        }
        
        return convertVal;
    }
    
    public static double convertUnits(          double val, 
                                                float  specFreq,
                                                UNITS originalUnits, 
                                                UNITS desiredUnits){
        
        double convertVal = val ;
        if (originalUnits ==  desiredUnits) {return convertVal;}
        
        switch(originalUnits){
        
            case PPM    : convertVal = Units.ppm2hertz(specFreq, val);
                        break;
            
            case HERTZ  : convertVal = Units.hertz2ppm(specFreq, val);
                        break;
            default     : convertVal = val;
                
        }
        
        return convertVal;
    }
    /************************************************************
     * Convenience method to convert frequency-like variables
     * from FidViwer units to "hertz".
     * @param value : value that will be converted to "hertz" units
     * @return      : converted value in "hertz".
     ***********************************************************/
    public static  double convertFromFidViewrUnitsToHertz(double value){
        FidViewer fv                =   FidViewer.getInstance();
        Procpar   procpar           =   fv.getProcpar();
        UNITS fidViwerUnits         =   fv.getUnits();
        
        double valueInHertz = Units.convertUnits(value, procpar, fidViwerUnits, UNITS.HERTZ);
        return valueInHertz;
    }
    
    
    /************************************************************
     * Convenience method to convert value in the given units to
     * the value selected in the FidViewer
     * @param val
     * @param curUnit
     * @return
     *************************************************************/
    public static double convertToFidViewerUnits(double val, UNITS curUnit){
        FidViewer fv                = FidViewer.getInstance();
        Procpar procpar             = fv.getProcpar();
        UNITS fidUnits              = fv.getUnits();
        
        if (fidUnits == curUnit) {return val;}
        double convertVal  = convertUnits(val, procpar, curUnit,fidUnits );
        return convertVal;
    }

    /************************************************************
     * Convenience method to convert value in the given units to
     * the value selected in the FidViewer
     * @param val
     * @param curUnit
     * @return
     *************************************************************/
    public static double convertToFidVieweableUnits(double val, UNITS curUnit, FidViewable fv){
        Procpar procpar             = fv.getProcpar();
        UNITS fidUnits              = fv.getUnits();

        if (fidUnits == curUnit) {return val;}
        double convertVal  = convertUnits(val, procpar, curUnit,fidUnits );
        return convertVal;
    }
}
