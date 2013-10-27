/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load.gui;
import javax.swing.*;
import java.io.*;
/**
 *
 * @author apple
 */
public class FidFileChooser  extends JFileChooser{
    // private static FidFileChooser instance           = null;
    private final static String   fidExtension       = ".fid";

    public static FidFileChooser getInstance () {
       /*
        if ( instance == null ) {
            instance = new FidFileChooser();
        }
        return instance;
        */
        return  new FidFileChooser();
    }
    private FidFileChooser() {
       super();
       setFileFilter(new FidFileFilter());
       setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
       setMultiSelectionEnabled(false);
       setEnabled(false);

        FormListener formListener = new FormListener();
        this.addPropertyChangeListener(formListener);
    }

     private void fcPropertyChange (java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)){
            if (evt.getNewValue().toString().endsWith(fidExtension)){
                approveSelection();
                setCurrentDirectory(getCurrentDirectory().getParentFile());
            }
        }
    }


    private class FormListener implements  java.beans.PropertyChangeListener {
        FormListener() {}


        public void propertyChange(java.beans.PropertyChangeEvent evt) {
           FidFileChooser.this.fcPropertyChange(evt);
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
     public static class   FidFileFilter extends javax.swing.filechooser.FileFilter {
     
     
         public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        boolean bl = f.getName().endsWith(fidExtension);
       
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
