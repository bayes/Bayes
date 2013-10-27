/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
public interface FrequencyDrawable {
    public int getJt ();
    public int getJs ();
    public int getJp ();
    
    public double getPrimaryFrequency ();
    public double getJSecondaryCouplinFrequency ();
    public double getJTertiraryCouplinFrequency  (); 
    
   public UNITS getUnits ();
  
}
