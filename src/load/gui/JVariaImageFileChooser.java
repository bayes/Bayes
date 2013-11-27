/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JVariaImageFileChooser.java
 *
 * Created on Feb 3, 2009, 5:26:17 PM
 */

package load.gui;
import load.*;
import interfacebeans.*;
import java.io.*;
import javax.swing.*;
import java.awt.MouseInfo;
import bayes.DirectoryManager;

/**
 *
 * @author apple
 */
public class JVariaImageFileChooser extends javax.swing.JDialog {
  
    private VarianBinaryConverter vairanConverter       =   null;
    private  JImageFileChooserAccessory loadControls    =   new  JImageFileChooserAccessory();
    private boolean isCanceled                          =   true;
    public static  JVariaImageFileChooser instance      =   null;

    
    public static JVariaImageFileChooser getInstance(){
        if (instance == null){
            instance =  new  JVariaImageFileChooser(null, true);
        }
        return instance;
    }
    private JVariaImageFileChooser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("Load Fid Image");
        initComponents();

        getImageFileChooser ().setCurrentDirectory(DirectoryManager.startDir);
    }
    public static  JVariaImageFileChooser showDialog(VarianBinaryConverter converter){
         getInstance().setVairanConverter(converter);
         getInstance().getLoadControls().setToDefaults();
         getInstance().setLocation(MouseInfo.getPointerInfo().getLocation());
         getInstance().setCanceled(true);
         getInstance().rescanCurrentDirectory();
         getInstance().setVisible(true);
       
       return instance;
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        image_fc = load.gui.FidFileChooser.getInstance();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(formListener);

        image_fc.setAcceptAllFileFilterUsed(false);
        image_fc.setAccessory(this.getLoadControls());
        image_fc.setCurrentDirectory(DirectoryManager.startDir);
        image_fc.setDialogTitle("Load Image Fid"); // NOI18N
        image_fc.setName("image_fc"); // NOI18N
        image_fc.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, image_fc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(image_fc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.WindowListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == image_fc) {
                JVariaImageFileChooser.this.image_fcActionPerformed(evt);
            }
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == JVariaImageFileChooser.this) {
                JVariaImageFileChooser.this.formWindowClosed(evt);
            }
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }
    }// </editor-fold>//GEN-END:initComponents

    private void image_fcActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_image_fcActionPerformed
        String action = evt.getActionCommand ();
        if (action.equals (JFileChooser.APPROVE_SELECTION )){
            File file                       =   getImageFileChooser ().getSelectedFile();
            if (file == null){return;}

            DirectoryManager.startDir       =   file.getParentFile();
            recalculate();
            setCanceled(false);


            close ();

        } else if (action.equals (JFileChooser.CANCEL_SELECTION )){
            this.setCanceled(true);
            close ();
        }
}//GEN-LAST:event_image_fcActionPerformed
    private void formWindowClosed (java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
       // do nothing
    }//GEN-LAST:event_formWindowClosed

    public void close(){
        this.setVisible(false);
    }


    private void recalculate(){
        int val;
        boolean setByUser;


        // phase encode
        setByUser =  getLoadControls ().isPhaseEncodeSetByUser();
        getVairanConverter().setIsPhaseEncodeSetByUser(setByUser);

        if (setByUser){
            val =  getLoadControls ().getPhaseEncodeLengthSetByUser();
            getVairanConverter().setPhaseEncodeLengthSetByUser(val);
        }


        //readout
        setByUser =  getLoadControls ().isRedOutLengthSetByUser();
        getVairanConverter().setIsRedOutLengthSetByUser(setByUser);

        if (setByUser){
            val =  getLoadControls ().getRedouLengthSetByUser();
            getVairanConverter().setRedouLengthSetByUser(val);
        }

        // phasing
         boolean isPhaseImages   =  getLoadControls ().isPhaseImages();
         getVairanConverter().setPhaseImages(isPhaseImages );

        File srcDir             =   getImageFileChooser ().getSelectedFile();
        getVairanConverter().setSourceDirectory(srcDir);
    }
    private void rescanCurrentDirectory(){
       if (getImageFileChooser ()!= null){
           getImageFileChooser ().rescanCurrentDirectory();
       }
    }



    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JVariaImageFileChooser dialog = new JVariaImageFileChooser(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser image_fc;
    // End of variables declaration//GEN-END:variables
    
  
    public javax.swing.JFileChooser getImageFileChooser () {
        return image_fc;
    }
    public JImageFileChooserAccessory getLoadControls () {
        return loadControls;
    }

    public int getRedouLengthSetByUser () {
        return getLoadControls ().getRedouLengthSetByUser();
    }
    public int getPhaseEncodeLengthSetByUser () {
        return getLoadControls ().getPhaseEncodeLengthSetByUser();
    }

    public boolean isRedOutLengthSetByUser () {
        return getLoadControls ().isRedOutLengthSetByUser();
    }


    public VarianBinaryConverter getVairanConverter () {
        return vairanConverter;
    }
    public void setVairanConverter ( VarianBinaryConverter vairanConverter ) {
        this.vairanConverter = vairanConverter;
    }

    public boolean isCanceled () {
        return isCanceled;
    }
    public void setCanceled ( boolean isCanceled ) {
        this.isCanceled = isCanceled;
    }
    
   

  


}