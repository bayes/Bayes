/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JResetSave.java
 *
 * Created on Oct 5, 2010, 1:32:50 PM
 */

package interfacebeans;

import applications.model.Model;
import bayes.PackageManager;
import load.SaveExperimentGUI;

/**
 *
 * @author apple
 */
public class JResetSave extends javax.swing.JPanel {

    /** Creates new form JResetSave */
    public JResetSave() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        resetButton = new javax.swing.JButton();
        saveButoon = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Save/Reset"));
        setLayout(new java.awt.GridBagLayout());

        resetButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        resetButton.setText("Reset");
        resetButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nReset the current experiment:<br>\nThe results from any previous run will be deleted<br>\nand the parametes will be reset to their default values.<br>\n\n</p><html>"); // NOI18N
        resetButton.setName("resetButton"); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(resetButton, gridBagConstraints);

        saveButoon.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        saveButoon.setText("Save");
        saveButoon.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nSave current working directory.<br>\n\n\n</p><html>"); // NOI18N
        saveButoon.setName("saveButoon"); // NOI18N
        saveButoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButoonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(saveButoon, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
}//GEN-LAST:event_resetButtonActionPerformed

    private void saveButoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButoonActionPerformed
        SaveExperimentGUI.showDialog();
}//GEN-LAST:event_saveButoonActionPerformed

    public void reset(){
        Model model = PackageManager.getCurrentApplication();
        if (model != null){model.reset();}
    }
    public void setEnabled (boolean enabled){
        setActive(enabled);
    }
    public void setActive(boolean enabled){
         resetButton.setEnabled(enabled);
         saveButoon.setEnabled(enabled);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButoon;
    // End of variables declaration//GEN-END:variables

}