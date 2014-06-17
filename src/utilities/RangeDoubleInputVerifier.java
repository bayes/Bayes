/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.text.DecimalFormat;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

    
/**
 *
 * @author apple
 */
public class RangeDoubleInputVerifier extends InputVerifier{
   DRange range;
   public RangeDoubleInputVerifier(double min, double max){
       range = new DRange(min, max);
   }
    
     @Override
     public boolean verify(JComponent input) {
        boolean verify = false;
        if (input instanceof JFormattedTextField) {
             JFormattedTextField ftf = (JFormattedTextField)input;
             AbstractFormatter formatter = ftf.getFormatter();
             
             if (formatter != null) {
                 String text = ftf.getText();
                 try {
                      double val = Double.parseDouble(text);
                    
                      
                      if(val < range.getMin()){
                          val = range.getMin();
                      }
                      else if (val > range.getMax()){
                          val  = range.getMax();
                      }
                      
                      //DecimalFormat myFormatter = new DecimalFormat("#.#####");
                      //String output = myFormatter.format(val);
                      ftf .setValue( val);
                      verify =  true;
                  } catch (Exception e) {
                       verify =  false;
                  }
              }
        }
        return verify;
    }
}
class DRange{
    DRange(double amin, double amax){
        min = Math.min(amin, amax);
        max = Math.max(amin, amax);
    
    }
    private double max;
    private double min;

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }
}
