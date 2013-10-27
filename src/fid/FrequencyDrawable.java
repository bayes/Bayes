/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import bayes.Enums.UNITS;
/**
 *
 * @author apple
 */
public interface FrequencyDrawable {
 
    
    public double getPrimaryFrequency ();
    
    public double getPrimaryCouplingFrequency ();
    public double getSecondaryCouplingFrequency ();
    public double getTertiaryCouplingFrequency  ();
    
    public int getPrimaryDegeneracy ();
    public int getSecondaryDegeneracy ();
    public int getTertiaryDegeneracy ();
   
    public UNITS            getFrequencyUnits()  ;  
    public UNITS            getCouplingConstantUnits()  ;  
}
