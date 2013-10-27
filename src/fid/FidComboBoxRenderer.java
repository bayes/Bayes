/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import java.awt.*;
import javax.swing.*;


   class FidComboBoxRenderer extends JLabel
                           implements ListCellRenderer {

        public FidComboBoxRenderer() {
           setOpaque(true);
         //  setForeground(Color.WHITE);
        //   setBackground(Color.BLACK);
           // setHorizontalAlignment(CENTER);
           //setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
        String entry =  value.toString();
        setText(entry);
            if (isSelected) {
                setOpaque(true);
              //   setForeground(Color.WHITE);
                 //setBackground(Color.BLACK);
                 setOpaque(true);
            } else {
                setOpaque(true);
                //setBackground(Color.BLACK);
               // setForeground(Color.YELLOW);
                setOpaque(true);
            }

        
            return this;
        }

    }