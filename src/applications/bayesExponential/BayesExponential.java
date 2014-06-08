/*
 * BayesExponential.java
 *
 * Created on February 5, 2008, 1:39 PM
 */

package applications.bayesExponential;

import run.JRun;
import bayes.PackageManager;
import bayes.DirectoryManager;
import javax.swing.*;
import java.awt.Component;
import java.io.*;
import bayes.JobDirections;
import bayes.WriteBayesParams;
import utilities.DisplayText;
import static load.LoadAndViewData.*;
import bayes.BayesManager;
import bayes.Enums.*;
import ascii.ASCIIDataViewer;
import interfacebeans.AllViewers;
import java.util.List;
import utilities.IO;
import bayes.ParameterPrior;
import bayes.ParameterPrior.ORDER_TYPE;
import bayes.ParameterPrior.PRIOR_TYPE;
import bayes.ParameterPrior.PARAMETER_TYPE;
import bayes.Reset;
import java.awt.Dimension;
import java.util.ArrayList;

public class BayesExponential extends javax.swing.JPanel 
                            implements applications.model.AsciiModel,
                                          java.beans.PropertyChangeListener,
                                            bayes.ApplicationConstants
{

    public BayesExponential() {
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();
        initComponents();
        BayesManager.pcs.addPropertyChangeListener(this);
         if(isDeseralized){ JRun.fireJobIDChange();}
         else{AllViewers.showInstructions();}
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        graph_panel = AllViewers.getInstance ();
        setup_panel = new javax.swing.JPanel();
        jRun = new run.JRun();
        jserver = interfacebeans.JServer.getInstance();
        modelPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        dummyLabel = new javax.swing.JLabel();
        setOrderLabel = new javax.swing.JLabel();
        expNumber_jComboBox = new JComboBox(new String [] {"1","2","3","4",UNKNOWN});

        ;
        jPanel1 = new javax.swing.JPanel();
        jConstantSelected = new javax.swing.JCheckBox();
        analysisPanel = new javax.swing.JPanel();
        includeOutliersCheckBox = new javax.swing.JCheckBox();
        jResetSave = new interfacebeans.JResetSave();

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 800));
        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setBottomComponent(graph_panel);

        setup_panel.setName("setup_panel"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        jserver.setName("jserver"); // NOI18N

        modelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Model"));
        modelPanel.setName("modelPanel"); // NOI18N
        modelPanel.setLayout(new java.awt.GridLayout(2, 0));

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        dummyLabel.setFont(new java.awt.Font("Lucida Grande", 0, 5));
        dummyLabel.setText(" ");
        dummyLabel.setName("dummyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(dummyLabel, gridBagConstraints);

        setOrderLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        setOrderLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setOrderLabel.setText("Order");
        setOrderLabel.setName("setOrderLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(setOrderLabel, gridBagConstraints);

        expNumber_jComboBox.setSelectedItem(numberOfExponentials);
        expNumber_jComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSet number of exponentials.\n</html>\n\n\n"); // NOI18N
        expNumber_jComboBox.setName("expNumber_jComboBox"); // NOI18N
        expNumber_jComboBox.setRenderer(new  CustomCellRenderer());
        expNumber_jComboBox.addItemListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(expNumber_jComboBox, gridBagConstraints);

        modelPanel.add(jPanel2);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout(0, 2));

        jConstantSelected.setSelected(this.isConstant());
        jConstantSelected.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jConstantSelected.setText("Constant   "); // NOI18N
        jConstantSelected.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nInclude constant term into model?\n</html>\n\n\n"); // NOI18N
        jConstantSelected.setEnabled( !isUnknown());
        jConstantSelected.setFocusPainted(false);
        jConstantSelected.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jConstantSelected.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jConstantSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        jConstantSelected.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jConstantSelected.setName("jConstantSelected"); // NOI18N
        jConstantSelected.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbp.gif"))); // NOI18N
        jConstantSelected.setRolloverEnabled(true);
        jConstantSelected.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        jConstantSelected.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        jConstantSelected.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N
        jConstantSelected.addItemListener(formListener);
        jPanel1.add(jConstantSelected, java.awt.BorderLayout.CENTER);

        modelPanel.add(jPanel1);

        analysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Analysis Option"));
        analysisPanel.setName("analysisPanel"); // NOI18N

        includeOutliersCheckBox.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        includeOutliersCheckBox.setSelected(isIncludeOutliers ());
        includeOutliersCheckBox.setText(" Find Outliers"); // NOI18N
        includeOutliersCheckBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n<font color=\"red\" size = \"+1\"><bold> Enable outlier detection.</font></bold><br>  \n(i.e., look for residual values <br>\nthat are larger than 3 standard deviations <br>\n and remove these outliers from the analysis.)</html>\n\n"); // NOI18N
        includeOutliersCheckBox.setContentAreaFilled(false);
        includeOutliersCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        includeOutliersCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        includeOutliersCheckBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        includeOutliersCheckBox.setIconTextGap(16);
        includeOutliersCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        includeOutliersCheckBox.setName("includeOutliersCheckBox"); // NOI18N
        includeOutliersCheckBox.setRolloverEnabled(true);
        includeOutliersCheckBox.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        includeOutliersCheckBox.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        includeOutliersCheckBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N
        includeOutliersCheckBox.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout analysisPanelLayout = new org.jdesktop.layout.GroupLayout(analysisPanel);
        analysisPanel.setLayout(analysisPanelLayout);
        analysisPanelLayout.setHorizontalGroup(
            analysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(analysisPanelLayout.createSequentialGroup()
                .add(2, 2, 2)
                .add(includeOutliersCheckBox)
                .addContainerGap(3, Short.MAX_VALUE))
        );
        analysisPanelLayout.setVerticalGroup(
            analysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(analysisPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(includeOutliersCheckBox)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jResetSave.setName("jResetSave"); // NOI18N

        org.jdesktop.layout.GroupLayout setup_panelLayout = new org.jdesktop.layout.GroupLayout(setup_panel);
        setup_panel.setLayout(setup_panelLayout);
        setup_panelLayout.setHorizontalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(analysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1104, Short.MAX_VALUE))
        );
        setup_panelLayout.setVerticalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .add(setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelPanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(analysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setup_panelLayout.linkSize(new java.awt.Component[] {analysisPanel, jResetSave, jRun, jserver, modelPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setTopComponent(setup_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.ItemListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == includeOutliersCheckBox) {
                BayesExponential.this.includeOutliersCheckBoxActionPerformed(evt);
            }
        }

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getSource() == expNumber_jComboBox) {
                BayesExponential.this.expNumber_jComboBoxItemStateChanged(evt);
            }
            else if (evt.getSource() == jConstantSelected) {
                BayesExponential.this.jConstantSelectedItemStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

      public void initializePriors(){
        double[] x;
        double[] y;
        int n;
        double dataInit;
        double dataFinal;
        double rateHigh;
        double ampInit;
        double ampLow;
        double ampHigh;
        double constLow;
        double constHigh;
       
        File dataFile = ASCIIDataViewer.getInstance().getLastDataFile();
        if (dataFile == null){return;}
        
        try {
           
            x = utilities.IO.nASCI2double(dataFile, 1);
            y = utilities.IO.nASCI2double(dataFile, 2);

        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return;
        }

        if (x != null && y != null){
            n = y.length;
            dataInit = (y[0]+ y[1] + y[2])/3.0;
            dataFinal= (y[n-1]+ y[n-2] + y[n-3])/3.0;
            rateHigh= 20.0/x[n-1];
            ampInit  = dataInit - dataFinal;
            if(ampInit < 0.0){
                ampLow = 3.0*ampInit;
                ampHigh= -ampInit;
            } else{
                ampHigh = 3.0*ampInit;
                ampLow= -ampInit;
            }

            if(dataFinal < 0.0){
                constLow = 3.0*dataFinal;
                constHigh= -dataFinal;
            } else{
                constHigh = 3.0*dataFinal;
                constLow= -dataFinal;
            }



        ParameterPrior p            =   new ParameterPrior();
        if (p.isPriorEditable){
            p.name                = "Rate";
            p.low                 = 0.0;
            p.high                = rateHigh;
            p.mean                = 0.0;
            p.sdev                = (p.high - p.low)/3;
            p.priorType           = PRIOR_TYPE.GAUSSIAN;
            p.order               = ORDER_TYPE.LowHigh;
            p.setParameterType(PARAMETER_TYPE.NonLinear);
            p.isOrderEditable     = false;
            p.isPriorTypeEditable = true;
            p.isPriorEditable     = false;
        }
        allParams.add(p);

        p                           =   new ParameterPrior();
        p.name                      = "Amplitude";
        p.setParameterType(PARAMETER_TYPE.Amplitude);
        allParams.add(p);

        p                           =   new ParameterPrior();
        p.name                      =  "Constant";
        p.setParameterType(PARAMETER_TYPE.Amplitude);
        allParams.add(p);


      }
       return;
    }
        
private void expNumber_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_expNumber_jComboBoxItemStateChanged
    if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) { 
        String deseletedValue = (String)evt.getItem();

        clearPreviousRun();


        if (deseletedValue.equals(UNKNOWN))
        {
             DirectoryManager.getModelProbabilityFile().delete();
        }
        return;
    }
    
    numberOfExponentials =  expNumber_jComboBox.getSelectedItem().toString();
    
    if(getNumberOfExponentials().equals(UNKNOWN)){
        setConstant(true);
            getJConstantSelected().setEnabled(false);
    } else {
            getJConstantSelected().setEnabled(true);
    }
   
}//GEN-LAST:event_expNumber_jComboBoxItemStateChanged
private void jConstantSelectedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jConstantSelectedItemStateChanged
if (listenToEvents == false) { return;}
    setConstant(getJConstantSelected ().isSelected());
    clearPreviousRun();
}//GEN-LAST:event_jConstantSelectedItemStateChanged

private void includeOutliersCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeOutliersCheckBoxActionPerformed
    includeOutliers  = this.getIncludeOutliersCheckBox().isSelected();
    clearPreviousRun();
}//GEN-LAST:event_includeOutliersCheckBoxActionPerformed
    
    public void              setPackageParameters(ObjectInputStream serializationFile) throws Exception{
        List <ParameterPrior>  pp   = (List <ParameterPrior>)serializationFile.readObject();
        boolean curConst            = (Boolean)serializationFile.readObject();
        boolean  isOutl             =  serializationFile.readBoolean();
        String curNoExp             = (String)serializationFile.readObject();


        allParams                  = pp;
        setNumberOfExponentials(curNoExp);
        setIncludeOutliers(isOutl);
        setConstant(curConst);
        
        
    }
    public void              savePackageParameters(ObjectOutputStream serializationFile){
       
        try{
        
            serializationFile.writeObject(allParams);
            serializationFile.writeObject(isConstant());
            serializationFile.writeBoolean(this.isIncludeOutliers());
            serializationFile.writeObject(getNumberOfExponentials());
        
        } 
        catch ( Exception exp){
             DirectoryManager.getSerializationFile().delete();
        }
    }
    public boolean           isReadyToRun() {
        // make sure data has been loaded
        File dir        =   DirectoryManager.getBayesOtherAnalysisDir();
        File[] files    =   ASCIIDataViewer.getInstance().getFiles();

        if (files == null || files.length == 0) {
            DisplayText.popupErrorMessage("You must load data before you run the program.");
            return false;
        }
        
        // make sure that priors were assigned
        if (getPriors() == null){
            DisplayText.popupErrorMessage("Priors were not assigned");
            return false;
        }
        
       // write the parameter file
       boolean bl  =  WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){
           DisplayText.popupErrorMessage("Failed to write Bayes.params file.");
           return false;
       }
        

      
       //  write "job.directions" file
        bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT);
        if(bl == false){
             DisplayText.popupErrorMessage("Failed to write job.directions file.");
            return false;
         }
      
        return true;
    }
    public String            getProgramName() {
        if (isUnknown()){ 
         return "BayesExpUnknown";}
        else   
        {return "BayesExpGiven";}
    }
    public String            getExtendedProgramName() { return "Given and Unknown Number of Exponentials" ;}
    public int               getNumberOfAbscissa() { 
       return 1;
    }
    public int               getNumberOfDataColumns() {
        return 1;
    }
    public int               getTotalNumberOfColumns(){return 2;}
    public int               getNumberOfPriors() {
        return getPriors().size();
    }
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) {
         StringBuilder buffer = new StringBuilder();
         
         buffer.append("\n");
        
         if(isUnknown()){return buffer;}

         buffer.append (IO.pad("Package Parameters", -PADLEN, PADCHAR ));
         buffer.append(" = "+ "2");
         buffer.append(EOL);
        

         buffer.append (IO.pad("Number of Exp", -PADLEN, PADCHAR ));
         buffer.append(" = "+ getNumberOfExponentials());
         buffer.append(EOL);
         
       
         
         String str = (isConstant())? "YES":"NO";
         buffer.append (IO.pad("Constant", -PADLEN, PADCHAR ));
         buffer.append(" = "+ str);
         buffer.append(EOL);


         return buffer;
    }
    public List <ParameterPrior>  getPriors() {
        List <ParameterPrior> priors  = new ArrayList<ParameterPrior>();
        if (allParams.isEmpty()) {
           File dataFile = ASCIIDataViewer.getInstance().getLastDataFile();
           if (dataFile != null){
              initializePriors(); //
           }
        }

       if (allParams.isEmpty() == false) {
            priors.add(allParams.get(0));
            priors.add(allParams.get(1));
            if(isConstant()){
                priors.add(allParams.get(2));
            }
       }
        return  priors;
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.EXPONENTIAL.getInstruction();}
    public boolean           isOutliers(){return isIncludeOutliers ();}
    public void              setActive(boolean enabled){
       AllViewers.getInstance().setActive(enabled);

       expNumber_jComboBox.setEnabled( enabled);
       includeOutliersCheckBox.setEnabled(enabled);
       modelPanel.setEnabled(enabled);
       analysisPanel.setEnabled(enabled);
       setOrderLabel.setEnabled(enabled);
       jResetSave.setEnabled(enabled);
       jserver.setActive(enabled);
       jRun.setEnabled(enabled);
        if ( isUnknown() == false) {
            getJConstantSelected().setEnabled( enabled);
       }


    }
    public void              reset(){
         
        setDefaults();
        clearPreviousRun();

        // show defualt viewer
        AllViewers.getInstance().showDefaultViewer();
        
    }
    public void              clearPreviousRun(){
        // clear outputs of previous run
         Reset.clearAsciiResutls();

         // reset job status
         jRun.reset();

    }
    public void              setDefaults(){

          setNumberOfExponentials("1");
          setConstant(false);
          getJConstantSelected().setEnabled(true);
          setIncludeOutliers(false);
          expNumber_jComboBox.setEnabled(true);
    }
    public void              destroy(){};

    public void              propertyChange(java.beans.PropertyChangeEvent evt){
        
      if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_START)){
          setActive(false);
      }
      else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_END)){
         setActive(true);
         AllViewers.showResultsViewer();
      }
    
      else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_CANCELED)){
          setActive(true);
      }
 
   }
    public static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame(" Exponential Model ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });

        frame.add(new BayesExponential());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analysisPanel;
    private javax.swing.JLabel dummyLabel;
    public javax.swing.JComboBox expNumber_jComboBox;
    private javax.swing.JPanel graph_panel;
    public javax.swing.JCheckBox includeOutliersCheckBox;
    public javax.swing.JCheckBox jConstantSelected;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JLabel setOrderLabel;
    private javax.swing.JPanel setup_panel;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JCheckBox getJConstantSelected () {
        return jConstantSelected;
    }
    public javax.swing.JCheckBox getIncludeOutliersCheckBox () {
        return includeOutliersCheckBox;
    }


    public   List <ParameterPrior>    allParams         =  new ArrayList <ParameterPrior> ();
    private final static String  UNKNOWN                = "Unknown";
    private  String              numberOfExponentials   = "1";
    public  boolean              listenToEvents         = true;
    private  boolean             constant               = false;
    private boolean              includeOutliers        = false;




    public String               getNumberOfExponentials () {
        return numberOfExponentials;
    }
    public  boolean             isConstant(){
        return constant;}
    public  boolean             isUnknown(){
       if (getNumberOfExponentials().equals(UNKNOWN)){
            return true;
       } else {
           return false;}
    }
    public  boolean             isIncludeOutliers () {
        return includeOutliers;
    }

    public void setConstant ( boolean constant ) {
        this.constant = constant;
        if (getJConstantSelected() != null) { getJConstantSelected().setSelected(constant);}
    }
    public void setNumberOfExponentials ( String numberOfExponentials ) {
        this.numberOfExponentials = numberOfExponentials;
        if (expNumber_jComboBox != null){
            expNumber_jComboBox.setSelectedItem(numberOfExponentials);
        }
    }
    public void setIncludeOutliers ( boolean includeOutliers ) {
        this.includeOutliers = includeOutliers;
        if( getIncludeOutliersCheckBox () != null){
             getIncludeOutliersCheckBox ().setSelected( includeOutliers);
        }
    }

   
 


     class CustomCellRenderer extends JLabel implements ListCellRenderer {
     public CustomCellRenderer() {
         super();
    }
     public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            String entry = (String) value;
            setText(entry);
            this.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        if (isSelected) {
          //setBackground(java.awt.Color.YELLOW);
          //setForeground(java.awt.Color.white);
        //  this.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        //  this.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        } else {
          //setBackground(java.awt.Color.white);
         // setForeground(java.awt.Color.black);
        //  this.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
         // this.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }

            return this;
      }
 }

     /**
 *
 * @author apple
 */
    public class ExpOrderPopupMenu extends  JPopupMenu{
        public void handleKnownOrderEvent(String value ){
            DirectoryManager.getModelProbabilityFile().delete();
            numberOfExponentials =   value;
            numberOfExponentials =   value;
            String model = numberOfExponentials + " exp.";
            if ( isConstant()){model =  model + "+ constant";}

            getJConstantSelected().setEnabled(true);
            clearPreviousRun();
        }
        public void handleUnKnownOrderEvent(String value ){
            numberOfExponentials =   value;
            setConstant(true);
            getJConstantSelected().setEnabled(false);
            clearPreviousRun();
        }

    public ExpOrderPopupMenu() {
        super();


        JMenuItem  menu         = new JMenuItem ("1");
        menu.addActionListener (new java.awt.event.ActionListener (){
        public void actionPerformed (java.awt.event.ActionEvent e) {
            handleKnownOrderEvent("1");
           }
        });
        add(  menu );

        menu         = new JMenuItem ("2");
        menu.addActionListener (new java.awt.event.ActionListener (){
        public void actionPerformed (java.awt.event.ActionEvent e) {
            handleKnownOrderEvent("2");
           }
        });
        add(  menu );

        menu         = new JMenuItem ("3");
        menu.addActionListener (new java.awt.event.ActionListener (){
        public void actionPerformed (java.awt.event.ActionEvent e) {
            handleKnownOrderEvent("3");
           }
        });
        add(  menu );

        menu         = new JMenuItem ("4");
        menu.addActionListener (new java.awt.event.ActionListener (){
        public void actionPerformed (java.awt.event.ActionEvent e) {
            handleKnownOrderEvent("4");
           }
        });
        add(  menu );


        add( new JSeparator() );

        menu         = new JMenuItem ("Unknown");
        menu.addActionListener (new java.awt.event.ActionListener (){
        public void actionPerformed (java.awt.event.ActionEvent e) {
            DirectoryManager.getModelProbabilityFile().delete();
            handleUnKnownOrderEvent("Unknown");
           }
        });
        add(  menu );





    }
    }
}
