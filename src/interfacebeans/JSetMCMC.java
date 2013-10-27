/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JSetMCMC.java
 *
 * Created on Mar 4, 2009, 9:31:37 AM
 */

package interfacebeans;
import bayes.ApplicationPreferences;
import java.util.*;
/**
 *
 * @author apple
 */
public class JSetMCMC extends javax.swing.JDialog {
     private static JSetMCMC  instance                     = null;
    /** Creates new form JSetMCMC */
    private JSetMCMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }


    public static JSetMCMC getInstance() {
        if (instance == null) {
            instance = new  JSetMCMC(null, true);

        }

        return instance;
    }

    public static void showDialog(){
        JSetMCMC  mcmcInstance =  JSetMCMC.getInstance();
        getInstance( ).setLocation(java.awt.MouseInfo.getPointerInfo().getLocation());
        mcmcInstance.setVisible(true);

    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMCMC = new interfacebeans.JMCMCparams(true);
        closeOkButtonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MCMC"); // NOI18N
        setMaximumSize(new java.awt.Dimension(400, 300));
        setMinimumSize(new java.awt.Dimension(300, 250));
        addWindowListener(formListener);

        jMCMC.setBorder(javax.swing.BorderFactory.createTitledBorder("Markov Chain Monte Carlo settings"));
        jMCMC.setMaximumSize(new java.awt.Dimension(200, 300));
        jMCMC.setMinimumSize(new java.awt.Dimension(300, 200));
        jMCMC.setName("jMCMC"); // NOI18N
        jMCMC.setPreferredSize(new java.awt.Dimension(140, 120));
        getContentPane().add(jMCMC, java.awt.BorderLayout.CENTER);

        closeOkButtonPanel.setName("closeOkButtonPanel"); // NOI18N

        okButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        okButton.setText("OK"); // NOI18N
        okButton.setToolTipText("<html><html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nUpdate values for MCMC parameters.\n\n</font></p><html>\n\n"); // NOI18N
        okButton.setMaximumSize(new java.awt.Dimension(200, 100));
        okButton.setName("okButton"); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(150, 100));
        okButton.addActionListener(formListener);

        closeButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        closeButton.setText("Cancel"); // NOI18N
        closeButton.setToolTipText("<html><html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nClose window without updating<br>\nvalues for MCMC parameters.\n\n</font></p><html>\n\n"); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout closeOkButtonPanelLayout = new org.jdesktop.layout.GroupLayout(closeOkButtonPanel);
        closeOkButtonPanel.setLayout(closeOkButtonPanelLayout);
        closeOkButtonPanelLayout.setHorizontalGroup(
            closeOkButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(closeOkButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(closeButton)
                .add(18, 18, 18)
                .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(98, Short.MAX_VALUE))
        );

        closeOkButtonPanelLayout.linkSize(new java.awt.Component[] {closeButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        closeOkButtonPanelLayout.setVerticalGroup(
            closeOkButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, closeOkButtonPanelLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .add(closeOkButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(closeOkButtonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.WindowListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == okButton) {
                JSetMCMC.this.okButtonActionPerformed(evt);
            }
            else if (evt.getSource() == closeButton) {
                JSetMCMC.this.closeButtonActionPerformed(evt);
            }
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == JSetMCMC.this) {
                JSetMCMC.this.formWindowClosing(evt);
            }
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

    private void okButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        save ();
        JSetMCMC.getInstance (). setVisible (false);
}//GEN-LAST:event_okButtonActionPerformed
    private void closeButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        cancel ();
        JSetMCMC.getInstance (). setVisible (false);
}//GEN-LAST:event_closeButtonActionPerformed
    private void formWindowClosing (java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        JSetMCMC.getInstance (). setVisible (false);
    }//GEN-LAST:event_formWindowClosing



    private void save(){

          setSims(getJMCMC ().getSims());
          setReps(getJMCMC ().getReps());
          setSteps(getJMCMC ().getSteps());

          ApplicationPreferences.setMcmcSims(getSims());
          ApplicationPreferences.setMcmcReps(getReps());
          ApplicationPreferences.setMcmcSteps(getSteps());


    }
    private void cancel(){

         getJMCMC ().setSims(sims);
         getJMCMC ().setReps(reps);
         getJMCMC ().setSteps(steps);

    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JSetMCMC dialog = new JSetMCMC(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel closeOkButtonPanel;
    private interfacebeans.JMCMCparams jMCMC;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    public interfacebeans.JMCMCparams getJMCMC () {
        return jMCMC;
    }
    public javax.swing.JButton      getCloseButton () {
        return closeButton;
    }
    public javax.swing.JPanel       getCloseOkButtonPanel () {
        return closeOkButtonPanel;
    }
    public javax.swing.JButton      getOkButton () {
        return okButton;
    }

    protected int sims              = 30;
    protected int reps              = 30;
    protected int steps             = 30;


    // *************** GETTERS AND SETTERS **********************//

    public int getSteps () {
        return steps;
    }
    public void setSteps ( int steps ) {
        this.steps = steps;
    }

    public int getReps () {
        return reps;
    }
    public void setReps ( int reps ) {
        this.reps = reps;
    }


    public int getSims () {
        return sims;
    }
    public void setSims ( int sims ) {
        this.sims = sims;
    }

}
