/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import utilities.IO;

/**
 *
 * @author apple
 */
public class FileNameInputVerifier extends InputVerifier {

    @Override
     public boolean verify(JComponent input) {
        boolean verify = false;

        if (input instanceof JTextField) {
               JTextField c  = (JTextField)input;
               String text = c.getText();

               if (IO.isValidFileName(text)){return true;}
               else {
                    c.setText("");
                    return false;
               }

        }
        return verify;
    }

}
