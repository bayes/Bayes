/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import javax.swing.JComponent;
import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
/**
 *
 * @author apple
 */
public class PositiveFloatInputVerifier extends InputVerifier {

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