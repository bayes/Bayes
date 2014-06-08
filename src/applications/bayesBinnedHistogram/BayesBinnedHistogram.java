/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BayesBinnedHistogram.java
 *
 * Created on Oct 2, 2009, 9:48:00 AM
 */

package applications.bayesBinnedHistogram;

import run.JRun;
import bayes.ParameterPrior;
import bayes.PackageManager;
import bayes.DirectoryManager;
import javax.swing.*;
import java.io.*;
import bayes.JobDirections;
import bayes.WriteBayesParams;
import utilities.DisplayText;
import bayes.Enums.*;
import ascii.ASCIIDataViewer;
import interfacebeans.AllViewers;
import java.util.List;
import bayes.BayesManager;
import bayes.Reset;
import java.util.ArrayList;
import utilities.IO;
/**
 *
 * @author apple
 */
public class BayesBinnedHistogram extends javax.swing.JPanel  implements applications.model.AsciiModel,
                                          java.beans.PropertyChangeListener,
                                            bayes.ApplicationConstants{
   
    public final static String UNKNOWN = "Unknown";
    public final static int DEFAULT_SMOOTHING_STEPS = 5;
    public final static int DEFAULT_SIZE_START      = 51;
    public final static int DEFAULT_SIZE_STEP       = 50;
    public final static String [] SIZES                ;
    public final static String [] FILTER_STEPS         ;
    
    private boolean outputPosteriors                =   false;
    List <ParameterPrior> priors                    =   new ArrayList<ParameterPrior>();
    private String histogramSize                     = SIZES[0];
    private String smoothingSteps                    = FILTER_STEPS[0];

   static {
             SIZES = new String[6];
             SIZES[0]           =   UNKNOWN; 
             for (int i = 1; i < SIZES.length; i++) {
                 int size       = DEFAULT_SIZE_START + DEFAULT_SIZE_STEP *(i-1);
                 SIZES[i]       = ""+  size;

            }

             FILTER_STEPS               = new String[12];
             FILTER_STEPS[0]            =   UNKNOWN; 
             for (int i = 1,  val = 0; i < FILTER_STEPS.length; i++, val++) {
                 FILTER_STEPS[i]        = ""+  val;

            }
  }



    /** Creates new form BayesBinnedHistogram */
    public BayesBinnedHistogram() {
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();

        initComponents();
       // AllViewers.removePriorsViewer();
      
        BayesManager.pcs.addPropertyChangeListener(this);
        ASCIIDataViewer.getInstance().setScatteringRenderer();
       if(isDeseralized){ JRun.fireJobIDChange();}
       else{AllViewers.showInstructions();}
    }

    public void              setPackageParameters(ObjectInputStream serializationFile) throws Exception{
        String size             = serializationFile.readObject().toString();
        String smoothSteps      = serializationFile.readObject().toString();
        boolean isOutPriors     = (Boolean)serializationFile.readObject();
        
        this.setHistogramSize(size);
        this.setSmoothingSteps(smoothSteps );
        this.setOutputPosteriors( isOutPriors );

    }
    public void              savePackageParameters(ObjectOutputStream serializationFile){

        try{

           serializationFile.writeObject( getHistogramSize());
           serializationFile.writeObject( getSmoothingSteps());
           serializationFile.writeObject( isOutputPosteriors());

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

   
       // write the parameter file
       boolean bl  =  WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){
           DisplayText.popupErrorMessage("Failed to write parameters file.");
           return false;
       }



       //  write "job.directions" file
        bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT);
        if(bl == false){
             DisplayText.popupErrorMessage("Failed to write job directions file.");
            return false;
         }

        return true;
    }
    public String            getProgramName() {
        //  return "DensityEstimationBinnedGiven";
         return "DensityEstimationBinnedUnknown";
         //return "BayesBinnedHistograms";
    }
    public String            getExtendedProgramName() { return "Binned Density Estimation" ;}
    public int               getNumberOfAbscissa() {
       return 1;
    }
    public int               getNumberOfDataColumns() {
        return 1;
    }
    public int               getTotalNumberOfColumns(){return 2;}
    public int               getNumberOfPriors() {  return getPriors().size() ;}
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) {
        StringBuilder buffer = new StringBuilder();
        String EOL           =  BayesManager.EOL;
        buffer.append(EOL);

         buffer.append (IO.pad("Package Parameters", -PADLEN, PADCHAR ));
         buffer.append(" = "+ "3");
         buffer.append(EOL);
         
         String tmp         = (isOutputPosteriors())? "Yes": "No";
         buffer.append (IO.pad("Output Posteriors", -PADLEN, PADCHAR ));
         buffer.append(" = "+ tmp);
         buffer.append(EOL);
         
         
         String histSizeVal         =   getHistogramSize();
         String histKey             =   "Histogram Size";
         boolean isUnknownHistogramSize      =   isUnknownHistogramSize();
         if(isUnknownHistogramSize ){
             histKey                =   "Select Histogram Size";
             histSizeVal            =   "Yes";
         }
         
         buffer.append (IO.pad( histKey , -PADLEN, PADCHAR ));
         buffer.append(" = "+ histSizeVal);
         buffer.append(EOL);

        
         
         
         String smoothStepVal       =    getSmoothingSteps();
         String smoothStepKey       =   "Smoothing Steps";
         boolean isUnknownSmoothingSteps      =    this.isUnknownSmoothingSteps();
         if(isUnknownSmoothingSteps ){
             smoothStepKey          =   "Select Smoothing Steps";
             smoothStepVal          =   "Yes";
         }
         
         buffer.append (IO.pad(smoothStepKey, -PADLEN, PADCHAR ));
         buffer.append(" = "+ smoothStepVal );
         buffer.append(EOL);
         
         return buffer;
    }
    public List <ParameterPrior>  getPriors() {
           if (priors == null || priors.isEmpty()){
            priors      =   initializePriors();
           }
        return  priors ;
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.BINNED_HISTOGRAM.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              setActive(boolean enabled){
        AllViewers.getInstance().setActive(enabled);
       jRun.setEnabled(enabled);
       jserver.setActive(enabled);
       jResetSave.setActive(enabled);
       getSizeComboBox().setEnabled(enabled);
       getOutputPosteriorsCheckBox().setEnabled(enabled);
       setSizeLabel.setEnabled(enabled);
       getSmoothingStepsComboBox().setEnabled(enabled);
       smoothingStepsLabel.setEnabled(enabled);    



      // smotthingStepsLabel.setEnabled(enabled);

    
    }
    public void              reset(){
            setDefaults();
            clearPreviousRun();
            AllViewers.getInstance().showDefaultViewer();


    }
    public void              clearPreviousRun(){
        // clear outputs of previous run
         Reset.clearAsciiResutls();

         // reset job status
         jRun.reset();

    }
    public void              setDefaults(){

         getSmoothingStepsComboBox().setSelectedIndex(0);

        // this widget is binded to outoutPosterior boolean
         getOutputPosteriorsCheckBox().setSelected(false);

         setHistogramSize(SIZES[0]);
         getSizeComboBox().setSelectedItem(0);


        
    }
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
    public void              destroy(){};

   public  List <ParameterPrior>  initializePriors(){
        List <ParameterPrior> thepriors             =   new ArrayList<ParameterPrior>();
        try{
            ParameterPrior prior                    =    new ParameterPrior("HistogramSize");
            prior.low                               =   10;
            prior.mean                              =   50;
            prior.high                              =   200;
            prior.sdev                              =   20;
            prior.priorType                         =  ParameterPrior.PRIOR_TYPE.GAUSSIAN;
            prior.order                             =   ParameterPrior.ORDER_TYPE.NotOrdered;
            prior.setParameterType(ParameterPrior.PARAMETER_TYPE.NonLinear);
            thepriors.add(prior);
            
            
            prior                                   =   new ParameterPrior("FilterSteps");
            prior.low                               =   0;
            prior.mean                              =   1;
            prior.high                              =   10;
            prior.sdev                              =   3;
            prior.priorType                         =   ParameterPrior.PRIOR_TYPE.EXPONENTIAL;
            prior.order                             =   ParameterPrior.ORDER_TYPE.NotOrdered;
            prior.setParameterType(ParameterPrior.PARAMETER_TYPE.NonLinear);
            thepriors.add(prior);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            return thepriors;
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

        frame.add(new BayesBinnedHistogram ());

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

        jSplitPane1 = new javax.swing.JSplitPane();
        setup_panel = new javax.swing.JPanel();
        jRun = new run.JRun();
        jserver = interfacebeans.JServer.getInstance();
        modelPanel = new javax.swing.JPanel();
        setSizeLabel = new javax.swing.JLabel();
        sizeComboBox = new JComboBox(SIZES);

        ;
        smoothingStepsLabel = new javax.swing.JLabel();
        smoothingStepsComboBox = new javax.swing.JComboBox(FILTER_STEPS);
        jResetSave = new interfacebeans.JResetSave();
        modelPanel1 = new javax.swing.JPanel();
        outputPosteriorsCheckBox = new javax.swing.JCheckBox();
        placeHolderLabel = new javax.swing.JLabel();
        graph_panel = AllViewers.getInstance ();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        setup_panel.setName("setup_panel"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        jserver.setName("jserver"); // NOI18N

        modelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Histogram"));
        modelPanel.setName("modelPanel"); // NOI18N
        modelPanel.setLayout(new java.awt.GridBagLayout());

        setSizeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        setSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setText("Histogram Size");
        setSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setName("setSizeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(setSizeLabel, gridBagConstraints);

        sizeComboBox.setSelectedItem( getHistogramSize());
        sizeComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nGenerate a binned histogram containing<br>\nthe number of bin selected.\n\n</html>\n\n"); // NOI18N
        sizeComboBox.setName("sizeComboBox"); // NOI18N
        sizeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sizeComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(sizeComboBox, gridBagConstraints);

        smoothingStepsLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        smoothingStepsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        smoothingStepsLabel.setText("Smoothing Steps");
        smoothingStepsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        smoothingStepsLabel.setName("smoothingStepsLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(smoothingStepsLabel, gridBagConstraints);

        smoothingStepsComboBox.setSelectedItem( getSmoothingSteps());
        smoothingStepsComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nThe Smoothing Steps specifies the number of<br>\ntimes a \"1:2:1\" low pass filter is to be applied<br>\nto the smoothed histogram.\n\n</html>\n\n"); // NOI18N
        smoothingStepsComboBox.setName("smoothingStepsComboBox"); // NOI18N
        smoothingStepsComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                smoothingStepsComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(smoothingStepsComboBox, gridBagConstraints);

        jResetSave.setName("jResetSave"); // NOI18N

        modelPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        modelPanel1.setName("modelPanel1"); // NOI18N
        modelPanel1.setLayout(new java.awt.GridBagLayout());

        outputPosteriorsCheckBox.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        outputPosteriorsCheckBox.setText("Output Posteriors ");
        outputPosteriorsCheckBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nOutput a plot containing the posterior <br>\nprobability for each point in the histogram.<br>\n\n\n</html>\n"); // NOI18N
        outputPosteriorsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        outputPosteriorsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        outputPosteriorsCheckBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        outputPosteriorsCheckBox.setName("outputPosteriorsCheckBox"); // NOI18N
        outputPosteriorsCheckBox.setRolloverEnabled(true);
        outputPosteriorsCheckBox.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        outputPosteriorsCheckBox.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        outputPosteriorsCheckBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${outputPosteriors}"), outputPosteriorsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        outputPosteriorsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputPosteriorsCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 10, 2);
        modelPanel1.add(outputPosteriorsCheckBox, gridBagConstraints);

        placeHolderLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        placeHolderLabel.setText("   ");
        placeHolderLabel.setName("placeHolderLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        modelPanel1.add(placeHolderLabel, gridBagConstraints);

        org.jdesktop.layout.GroupLayout setup_panelLayout = new org.jdesktop.layout.GroupLayout(setup_panel);
        setup_panel.setLayout(setup_panelLayout);
        setup_panelLayout.setHorizontalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 218, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1353, Short.MAX_VALUE))
        );
        setup_panelLayout.setVerticalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .add(setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(modelPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setup_panelLayout.linkSize(new java.awt.Component[] {jResetSave, jRun, jserver, modelPanel, modelPanel1}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setTopComponent(setup_panel);

        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setBottomComponent(graph_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void sizeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sizeComboBoxItemStateChanged
         if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String seletedValue = (String)evt.getItem();
            setHistogramSize( seletedValue);
            return;
        }


        clearPreviousRun();
    }//GEN-LAST:event_sizeComboBoxItemStateChanged
    private void smoothingStepsComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_smoothingStepsComboBoxItemStateChanged
       if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String seletedValue = (String)evt.getItem();
            this.setSmoothingSteps(seletedValue);
            return;
        } 
        clearPreviousRun();
    }//GEN-LAST:event_smoothingStepsComboBoxItemStateChanged
    private void outputPosteriorsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputPosteriorsCheckBoxActionPerformed
        clearPreviousRun();
    }//GEN-LAST:event_outputPosteriorsCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel graph_panel;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JPanel modelPanel1;
    private javax.swing.JCheckBox outputPosteriorsCheckBox;
    private javax.swing.JLabel placeHolderLabel;
    private javax.swing.JLabel setSizeLabel;
    private javax.swing.JPanel setup_panel;
    public javax.swing.JComboBox sizeComboBox;
    public javax.swing.JComboBox smoothingStepsComboBox;
    private javax.swing.JLabel smoothingStepsLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JComboBox getSizeComboBox() {
        return sizeComboBox;
    }
    public javax.swing.JCheckBox getOutputPosteriorsCheckBox() {
        return outputPosteriorsCheckBox;
    }
    public javax.swing.JComboBox getSmoothingStepsComboBox() {
        return smoothingStepsComboBox;
    }
 
    public String getHistogramSize() {
        return histogramSize;
    }
    public void setHistogramSize(String histogramSize) {
        this.histogramSize = histogramSize;
        if (getSizeComboBox() != null){
            getSizeComboBox().setSelectedItem(histogramSize);
        }
    }
    
    public String getSmoothingSteps() {
        return smoothingSteps;
    }
    public void setSmoothingSteps(String smoothingSteps) {
        this.smoothingSteps = smoothingSteps;
        if (getSmoothingStepsComboBox() != null){
            getSmoothingStepsComboBox().setSelectedItem(smoothingSteps);
        }
    }
   
     
    public boolean isUnknownHistogramSize(){
        String hsize    =   getHistogramSize();
        boolean out     =   hsize.equalsIgnoreCase(UNKNOWN );
        
        return out;
    }
    public boolean isUnknownSmoothingSteps(){
        String val      =   getSmoothingSteps();
        boolean out     =   val.equalsIgnoreCase(UNKNOWN );
        
        return out;
    }
    
    public boolean isOutputPosteriors() {
        return outputPosteriors;
    }
    public void setOutputPosteriors(boolean outputPrios) {
        this.outputPosteriors = outputPrios;
    }

    








    

}
