/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import java.util.ArrayList;
import bayes.Enums.UNITS;
/**
/**
 *
 * @author apple
 */
public class DrawResonances {
    

   
    private DrawResonances(){} // can not be instantianted
    
    public static  double frequencyLocation(int order ,int peakNumber,double centFreq,double JCoupling){
         double freqLoc = centFreq - 0.5*JCoupling*(order + 1 - 2*peakNumber);
         return freqLoc;
    }
    public static  ArrayList <Double>  getMetabolitesFrequenciesInFIDUnits(FrequencyDrawable  res,  FidViewable viewer){
        ArrayList <Double>freqList  =   new  ArrayList <Double>();
        
        int  primaryOrder           =   res.getPrimaryDegeneracy() ;
        int  secondaryOrder         =   res.getSecondaryDegeneracy();
        int  tertiaryOrder          =   res.getTertiaryDegeneracy();

       
        double centerFreq           =  res.getPrimaryFrequency();
        double j1                   =  res.getPrimaryCouplingFrequency(); // primary coupling
        double j2                   =  res.getSecondaryCouplingFrequency(); // secondary coupling
        double j3                   =  res.getTertiaryCouplingFrequency(); // tertiary coupling
        
        UNITS  freqUnits            =  res.getFrequencyUnits();
        UNITS  cplConstUnits        =  res.getCouplingConstantUnits();

        centerFreq                  =  Units.convertToFidVieweableUnits(centerFreq , freqUnits, viewer);
        j1                          =  Units.convertToFidVieweableUnits(j1 ,  cplConstUnits, viewer);
        j2                          =  Units.convertToFidVieweableUnits(j2 ,  cplConstUnits, viewer);
        j3                          =  Units.convertToFidVieweableUnits(j3 ,  cplConstUnits, viewer);

        
        freqList.add(0,centerFreq);
        
         double  prmFreq, scnFreq, terFreq;
         int peakNumber;
        // itterate thtough first order spin degenracy
        for (int i = 0; i < primaryOrder ; i++) {
            peakNumber = i + 1;
            prmFreq = frequencyLocation(primaryOrder ,peakNumber,centerFreq,j1);  
                    
            
             // itterate thtough second order spin degenracy
            for (int j = 0; j < secondaryOrder ; j++) {
                peakNumber = j + 1;    
                scnFreq = frequencyLocation(secondaryOrder ,peakNumber,prmFreq,j2); 
                      
                 // itterate thtough third order spin degenracy
                for (int k = 0; k <  tertiaryOrder; k++) {
                         peakNumber = k + 1;
                         terFreq = frequencyLocation( tertiaryOrder ,peakNumber,scnFreq,j3); 
                         freqList.add(terFreq);
                    }
            }
            
       }
          return freqList;
    }
       
   /************************************************************
    * The goal is to calculate frequencies in order to draw them
    * in the FidViewer. This frequencies will be converted later to
    * java 2D coordinated to draw resonance shape.
    * @param resonance - Resonance for each frequencied must be calculated
    * @return    - ArrayList of frequencies. 0-index corrsonds to center frequency.
    *              All other indecies corresponds to frequencies in the increasoing order 
    */
    public static  ArrayList <Double>  getResonanceFrequenciesInHertz(Resonance resonance, FidViewable viewer){
        ArrayList <Double>freqList  = new  ArrayList <Double>();
        Procpar procpar             =   viewer.getProcpar();
        double  prmCenter           =  ( resonance.getFirstOrder() + 1)/2.0;
        double  scnCenter           =  ( resonance.getSecondOrder() + 1)/2.0;

        int  primaryOrder           =  resonance.getFirstOrder() ;
        int  secondaryOrder         =  resonance.getSecondOrder();
        
        double centerFreq           =  resonance.getFreqFinalVal();
        double j1coupling           =  resonance.getJ1FinalVal();
        double j2coupling           =  resonance.getJ2FinalVal();
        
       
        centerFreq                  = Units.convertUnits(centerFreq , procpar,resonance.getUnits(), 
                                                    UNITS.HERTZ);
        
        
        freqList.add(0,centerFreq);
        
        double  prmFreq, scnFreq;
        int peakNumber; 
         
         // itterate thtough first order spin degenracy
        for (int i = 0; i < resonance.getFirstOrder(); i++) {
            peakNumber = i + 1;
            
            if (new Double( j1coupling).equals(Double.NaN)){
                prmFreq = centerFreq;  
            }
            else{
                 prmFreq = frequencyLocation(primaryOrder ,peakNumber,centerFreq, j1coupling);  
            }
            
             // itterate thtough second order spin degenracy
            for (int j = 0; j <resonance.getSecondOrder(); j++) {
                    peakNumber = j + 1;   
                    if (new Double( j2coupling).equals(Double.NaN)){
                        scnFreq = prmFreq ;
                    }
                    else{
                        scnFreq =  frequencyLocation(secondaryOrder ,peakNumber,prmFreq,j2coupling); ;
                    }
                    freqList.add(scnFreq);
                }
       }
       return freqList;
    }

  
   

    
}
