/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load.gui;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author apple
 */
public class CustomFileChooser  extends JFileChooser{
    private static String   fileExtension       = "";
    private static String   dirExtension        = "";

    public static CustomFileChooser getInstance (String fileextenstion, String dirextension) {
        fileExtension   =  fileextenstion;
        dirExtension    =  dirextension;

        return  new CustomFileChooser();
    }

     
    private CustomFileChooser () {
       super();
       setFileFilter(new  CustomFileFilter());
       setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
       setMultiSelectionEnabled(false);
       setEnabled(false);

        FormListener formListener = new FormListener();
        this.addPropertyChangeListener(formListener);
    }

     private void fcPropertyChange (java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)){
            String val           =   evt.getNewValue().toString();
            if (val  .endsWith(  dirExtension)){
                approveSelection();
                setCurrentDirectory(getCurrentDirectory().getParentFile());
            }
        }
    }


    private class FormListener implements  java.beans.PropertyChangeListener {
        FormListener() {}


        public void propertyChange(java.beans.PropertyChangeEvent evt) {
           CustomFileChooser.this.fcPropertyChange(evt);
        }


        //java.awt.event.ActionListener,
        public void actionPerformed (java.awt.event.ActionEvent evt) {
        String action           = evt.getActionCommand ();

        if (action.equals (JFileChooser.APPROVE_SELECTION )){
           // File file = getSelectedFile();
            //loadImage (file);
           //  DirectoryManager.startDir     =   getImageFileChooser().getCurrentDirectory();
            //close ();

        } else if (action.equals (JFileChooser.CANCEL_SELECTION )){
         //  close ();
        }


    }// </editor-fold>
     }
    public static class   CustomFileFilter extends javax.swing.filechooser.FileFilter {


     public boolean accept(File f) {
        if (f.isDirectory()) { return true;  }
           

        boolean bl = f.getName().endsWith(  fileExtension);

        if (bl) {
                return true;
        } else {
                return false;
        }
     }
        //The description of this filter
        public String getDescription() {
        return "fid folder";
    }
    }
}
