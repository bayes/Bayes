/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 *
 * @author apple
 */
public class RangeIntegerInputVerifier extends InputVerifier{
   Range range;
   public RangeIntegerInputVerifier(int min, int max){
       range = new Range(min, max);
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
                      int val = Integer.parseInt(text);
                    
                      
                      if(val < range.getMin()){
                          val = range.getMin();
                      }
                      else if (val > range.getMax()){
                          val  = range.getMax();
                      }
                      ftf .setValue( Math.abs(val));
                      verify =  true;
                  } catch (Exception e) {
                       verify =  false;
                  }
              }
        }
        return verify;
    }
}

class Range{
    Range(int amin, int amax){
        min = Math.min(amin, amax);
        max = Math.max(amin, amax);
    
    }
    private int max;
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
