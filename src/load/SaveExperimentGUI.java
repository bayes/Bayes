/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SaveExperimentGUI.java
 *
 * Created on Oct 29, 2009, 2:54:32 PM
 */

package load;

import bayes.ApplicationPreferences;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import bayes.DirectoryManager;
import utilities.DisplayText;

/**
 *
 * @author apple
 */
public class SaveExperimentGUI extends javax.swing.JDialog {
    private static  SaveExperimentGUI   instance;
    private File saveDir            =   DirectoryManager.getUserHomeDirectory();
    boolean saveCanceledByUser      =   false;
    
    
    public static SaveExperimentGUI getInstance(){
        if (instance == null){
            instance = new SaveExperimentGUI();
        }
        return instance;

    }
    /** Creates new form SaveExperimentGUI */
    private SaveExperimentGUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    private SaveExperimentGUI() {

        this(new JFrame(),true);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        workDirComboBox = new javax.swing.JComboBox();
        saveDirTextField = new javax.swing.JTextField();
        saveExpDestinationNameTF = new javax.swing.JTextField();
        setButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        buttonPane = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        changeDirButton = new javax.swing.JButton();

        jLabel2.setText("Working Directory");
        jLabel2.setName("jLabel2"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save working directory"); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.BorderLayout(30, 10));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Working Directory");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 2, 2);
        jPanel1.add(jLabel1, gridBagConstraints);

        workDirComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSelect which working directory you wish to save. <br>\nAll currently set working directories are listed. <br>\n\n\n</font></p><html>"); // NOI18N
        workDirComboBox.setName("workDirComboBox"); // NOI18N
        workDirComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                workDirComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 2, 2);
        jPanel1.add(workDirComboBox, gridBagConstraints);

        saveDirTextField.setEditable(false);
        saveDirTextField.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nPath to the destination directory into which<br>\nworking directory will be copied. Use \"Set\" button<br>\nto set this path.\n\n</font></p><html>"); // NOI18N
        saveDirTextField.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.lightGray, java.awt.Color.darkGray, java.awt.Color.lightGray, java.awt.Color.darkGray));
        saveDirTextField.setName("saveDirTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(saveDirTextField, gridBagConstraints);

        saveExpDestinationNameTF.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nName under whcih experiment should be saved.\n\n</font></p><html>"); // NOI18N
        saveExpDestinationNameTF.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.lightGray, java.awt.Color.darkGray, java.awt.Color.lightGray, java.awt.Color.darkGray));
        saveExpDestinationNameTF.setName("saveExpDestinationNameTF"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, workDirComboBox, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), saveExpDestinationNameTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("savedBayesExp");
        binding.setSourceUnreadableValue("savedBayesExp");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(saveExpDestinationNameTF, gridBagConstraints);

        setButton.setText("Set"); // NOI18N
        setButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSelect where you want to save working directory  <br>\n\n</font></p><html>"); // NOI18N
        setButton.setName("setButton"); // NOI18N
        setButton.setPreferredSize(new java.awt.Dimension(50, 22));
        setButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel1.add(setButton, gridBagConstraints);

        jLabel5.setText("Save to location");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel3.setText("Save as");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPanel1.add(jLabel3, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        buttonPane.setName("buttonPane"); // NOI18N

        saveButton.setText("Save"); // NOI18N
        saveButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nStart saving selected working directory  <br>\n\n</font></p><html>"); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        changeDirButton.setText("Cancel"); // NOI18N
        changeDirButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nCancel saving procedures.<br>\n\n</font></p><html>"); // NOI18N
        changeDirButton.setName("changeDirButton"); // NOI18N
        changeDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDirButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPaneLayout = new org.jdesktop.layout.GroupLayout(buttonPane);
        buttonPane.setLayout(buttonPaneLayout);
        buttonPaneLayout.setHorizontalGroup(
            buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPaneLayout.createSequentialGroup()
                .addContainerGap(279, Short.MAX_VALUE)
                .add(changeDirButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(saveButton)
                .addContainerGap())
        );
        buttonPaneLayout.setVerticalGroup(
            buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPaneLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(saveButton)
                    .add(changeDirButton)))
        );

        getContentPane().add(buttonPane, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDirButtonActionPerformed

        close();
    }//GEN-LAST:event_changeDirButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save();
        if(saveCanceledByUser){
            // do nothing
        }
        else{
              close();
        }


      
    }//GEN-LAST:event_saveButtonActionPerformed

    private void workDirComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workDirComboBoxItemStateChanged
    }//GEN-LAST:event_workDirComboBoxItemStateChanged

    private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setButtonActionPerformed
         setPath();
    }//GEN-LAST:event_setButtonActionPerformed
    public void setPath(){

        if ( saveDir  == null){
            saveDir = DirectoryManager.getUserHomeDirectory();

        }

        JFileChooser fc     =   new JFileChooser(saveDir);
        fc.setApproveButtonText("Set");
       // fc.setDialogType(JFileChooser.SAVE_DIALOG );

        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int returnVal = fc.showOpenDialog(null);
        // int returnVal = fc.showSaveDialog(null);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
              saveDir     =   fc.getSelectedFile ();
              getSaveDirTextField().setText(saveDir.getPath());
         }
        //String srcDirName  = getWorkDirComboBox().getSelectedItem().toString();
       // File  dst          = new File (saveDir, srcDirName);

    }
    public void save(){
       saveCanceledByUser   = false;
       SaveExperiment se    = new SaveExperiment();
       String srcDirName    = getWorkDirComboBox().getSelectedItem().toString();
       File srcDir          = DirectoryManager.getExperimentDir(srcDirName);
       if (srcDir  == null || srcDir.exists() == false) {return;}

       String destName      =   getSaveExpDestinationNameTF().getText();
       File destination     =   new File (saveDir, destName );

       boolean      proceed = true;
       if (destination.exists()){
           String message   = String.format(
                   "Destination directory %s\n"
                   + "already exists. Saving experiment will\n"
                   + "erase current content of this directory.\n"
                   + "Do you want to proceed?",  destination.getPath());
             proceed   = DisplayText.popupDialog(message );
       }
       if ( proceed  == false){
            saveCanceledByUser = true;
        return;
       }
       else{
            se.sourceExp     = srcDir;
            se.destinExp     = destination ;
            se.execute();
       }

       
      
    }


    public static void close(){
         SaveExperimentGUI inst =  getInstance();
         
         inst.setVisible(false);
         inst.dispose();
         
      
    }
    public static  SaveExperimentGUI showDialog( ){
        Point p = MouseInfo.getPointerInfo().getLocation();

        return showDialog (p.x, p.y);
    }
    public static SaveExperimentGUI  showDialog(   int x, int y){
          SaveExperimentGUI  inst = getInstance();
          inst.getWorkDirComboBox().removeAllItems();
        
         inst.setLocation(x, y);
         inst.update();
         inst.setVisible(true);
       
         return inst;
    }
    public void update(){
        List <String> workDirs =  ApplicationPreferences.getWorkDirs();
        for (String wd : workDirs) {getWorkDirComboBox().addItem(wd);}

        String curWd           =  ApplicationPreferences.getCurrentWorkDir();
        getWorkDirComboBox().setSelectedItem(curWd);

        getSaveDirTextField().setText(this.getSaveDir().getPath());
            
      
    }
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               // SaveExperimentGUI dialog = new SaveExperimentGUI(new javax.swing.JFrame(), true);
                //dialog.setVisible(true);
                showDialog();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPane;
    private javax.swing.JButton changeDirButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField saveDirTextField;
    private javax.swing.JTextField saveExpDestinationNameTF;
    private javax.swing.JButton setButton;
    private javax.swing.JComboBox workDirComboBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JButton      getRemoveButton() {
        return saveButton;
    }
    public javax.swing.JTextField   getSaveDirTextField() {
        return saveDirTextField;
    }
    public javax.swing.JButton      getSetButton() {
        return setButton;
    }
    public javax.swing.JComboBox    getWorkDirComboBox() {
        return workDirComboBox;
    }
    public javax.swing.JTextField   getSaveExpDestinationNameTF() {
        return saveExpDestinationNameTF;
    }

    public File getSaveDir() {
        return saveDir;
    }
    public void setSaveDir(File saveDir) {
        this.saveDir = saveDir;
    }


   







 }