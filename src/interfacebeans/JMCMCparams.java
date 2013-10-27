/*
 * JMCMCparams.java
 *
 * Created on August 16, 2007, 12:40 PM
 */

package interfacebeans;
import bayes.ApplicationPreferences;
import javax.swing.*;

public class JMCMCparams extends javax.swing.JPanel {
                          
    
    public JMCMCparams () {
         initComponents ();
    }
    public JMCMCparams (boolean isUserProperty) {
            updateValues();
            initComponents ();
           
    }
 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        simsField = new JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        repsField =  new JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        stepsField = new JFormattedTextField();

        FormListener formListener = new FormListener();

        setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Simulations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel1, gridBagConstraints);

        simsField.setValue(sims);
        simsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        simsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        simsField.setToolTipText("<html><html><p style=\"margin: 6px;\"><font size=\"4\">\n\n Select number of simulations.<br>\n (number of chains in Monte Carlo simulations).\n\n</font></p><html>"); // NOI18N
        simsField.setMinimumSize(new java.awt.Dimension(4, 10));
        simsField.addPropertyChangeListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(simsField, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Repetitions");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel2, gridBagConstraints);

        repsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        repsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        repsField.setToolTipText("<html><html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSelect number of repetitions.<br>\n(number of  sample for each chain <br>\nin Monte Carlo Simulations).\n</font></p><html>\n\n\n\n"); // NOI18N
        repsField.setMinimumSize(new java.awt.Dimension(4, 10));
        repsField.setValue(reps);
        repsField.addPropertyChangeListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(repsField, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Min. Annealing Steps"); // NOI18N
        jLabel3.setMinimumSize(new java.awt.Dimension(50, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel3, gridBagConstraints);

        stepsField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        stepsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        stepsField.setToolTipText("<html><html><p style=\"margin: 6px;\"><font size=\"4\">\n\nEnter the minimum number of steps to take in <br>\nthe simulated annealing part of the Markov chain<br>\nMonte Carlo calculation (30 or more preferably).\n\n\n</font></p><html>\n\n\n"); // NOI18N
        stepsField.setMinimumSize(new java.awt.Dimension(4, 10));
        stepsField.setValue(steps);
        stepsField.addActionListener(formListener);
        stepsField.addPropertyChangeListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(stepsField, gridBagConstraints);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == stepsField) {
                JMCMCparams.this.stepsFieldActionPerformed(evt);
            }
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == simsField) {
                JMCMCparams.this.textFiled_proprtyChanged(evt);
            }
            else if (evt.getSource() == repsField) {
                JMCMCparams.this.textFiled_proprtyChanged(evt);
            }
            else if (evt.getSource() == stepsField) {
                JMCMCparams.this.textFiled_proprtyChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

   
 
    private void textFiled_proprtyChanged (java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_textFiled_proprtyChanged
        if (! evt.getPropertyName().equals("value")){ return;}
           
        Object source = evt.getSource();
        if (source == simsField) {    
            sims = ((Number)simsField.getValue()).intValue();
            sims = Math.abs(sims);
            simsField.setValue(sims);
        } else if (source == repsField) {
            reps = ((Number)repsField.getValue()).intValue();
            reps = Math.abs(reps);
            repsField.setValue( reps);
        } else if (source ==stepsField) {
           steps = ((Number)stepsField.getValue()).intValue();
           steps = Math.abs(steps);
           stepsField.setValue(steps);
        }
    }//GEN-LAST:event_textFiled_proprtyChanged

    private void stepsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stepsFieldActionPerformed
    
   
    private void updateValues(){
        sims    = ApplicationPreferences.getMcmcSims();
        reps    = ApplicationPreferences.getMcmcReps();
        steps   = ApplicationPreferences.getMcmcSteps();
                
    }
   
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Set up MCMC params");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new JMCMCparams ());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JFormattedTextField repsField;
    private javax.swing.JFormattedTextField simsField;
    private javax.swing.JFormattedTextField stepsField;
    // End of variables declaration//GEN-END:variables
    boolean isUserProperty;
    
    //Values for the fields
    private int sims    = 50;
    private int reps    = 50;
    private int steps   = 51; 

    
    public int getSims(){return sims;} 
    public void setSims(int aSims){
        sims = aSims;
        simsField.setValue(sims);
        return;} 
    
    public int getReps(){return reps;}
    public void setReps(int aReps){
        reps = aReps;
        repsField.setValue(reps);
        return;
    }
    
    public int getSteps(){return steps;}
    public void setSteps(int aSteps){
        steps = aSteps;
        stepsField.setValue(steps);
        return;
    }
}
