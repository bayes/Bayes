/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author apple
 */
public class PanelUtility {

    public static void setEnabled(Container pane, boolean enable){
        if (pane.getComponentCount() == 0){
             pane.setEnabled(enable);
        }
         else{
             Component [] components = pane.getComponents();
                for (Component component : components) {
                        if(component instanceof Container){
                            setEnabled((Container) component, enable);
                        }

         }
         
        }
    }
    public static void setVisible(Container pane, boolean visible){
        if (pane.getComponentCount() == 0){
             pane.setVisible(visible);
        }
         else{
             Component [] components = pane.getComponents();
                for (Component component : components) {
                        if(component instanceof Container){
                            setVisible((Container) component, visible);
                        }

         }

        }

        pane.setVisible(visible);
    }

}
