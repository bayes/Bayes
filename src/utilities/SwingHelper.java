/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 *
 * @author apple
 */
public class SwingHelper {
     public static String getTruncatedGuiName( String text ,  JComponent comp ){
           if (comp.isShowing() == false){
             return text;
           }

            int availableWidth          =   comp.getWidth();

            Graphics2D g2               =  (Graphics2D)   comp.getGraphics();

            String cellText             =   text;
            FontMetrics fm              =   g2.getFontMetrics();
            int strWidth                =   fm.getStringBounds(cellText,  g2).getBounds().width;



            if (strWidth  > availableWidth)
            {
                    String dots = "...";
                    int textWidth                   =   fm.getStringBounds(dots,  g2).getBounds().width;
                    int nCharsBefore                =   cellText.length();
                    int nCharsAfter                 =   0;
                    for (;  nCharsAfter < nCharsBefore;  nCharsAfter++)
                    {
                            textWidth               += fm.charWidth(cellText.charAt(nCharsAfter ));
                            if (textWidth > availableWidth){ break;}
                    }

                    String cmpname      =  cellText.substring(0,  nCharsAfter - 1) + dots;
                    return cmpname ;
            }
            else{
                return text;
            }


         }
}
