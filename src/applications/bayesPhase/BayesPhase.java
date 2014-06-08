/*
 * BayesPhase.java
 *
 * Created on November 7, 2008, 9:43 AM
 */

package applications.bayesPhase;
import run.JRun;
import bayes.ParameterPrior;
import bayes.PackageManager;
import bayes.DirectoryManager;
import utilities.IO;
import javax.swing.*;
import java.io.*;
import fid.Procpar;
import bayes.JobDirections;
import bayes.WriteBayesParams;
import utilities.DisplayText;
import interfacebeans.*;
import bayes.BayesManager;
import bayes.Enums.*;
import utilities.LoadPackage;
import image.ImageViewer;
import applications.model.ImageModel;
import bayes.Reset;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import utilities.PositiveFloatInputVerifier;
/**
 *
 * @author  apple
 */
public class BayesPhase extends javax.swing.JPanel
                implements  ImageModel, PropertyChangeListener,  bayes.ApplicationConstants {
              
                        
    private IMAGE_PROCESS process               =   IMAGE_PROCESS.All;
    private double noiseStandardDeviation       =   0;
    protected boolean generateAbsicssa          = true;
    private boolean nonLinearPhasing            = true;
 

    /** Creates new form BayesPhase */
    public BayesPhase() {
        LoadPackage.loadPackage(this);
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();
        
        
        initComponents();
        
        BayesManager.pcs.addPropertyChangeListener(this);
        ImageViewer.getInstance().addPropertyChangeListener(this);
        
        if(isDeseralized){
             AllViewers.showImageViewer();
             JRun.fireJobIDChange();
        }
        else { AllViewers.showInstructions();}
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
        tools = new javax.swing.JPanel();
        jRun = new run.JRun();
        jPanel2 = new javax.swing.JPanel();
        nonlibearLabel = new javax.swing.JLabel();
        process_cb = new javax.swing.JComboBox(IMAGE_PROCESS.values());
        noiseStdDevLabel = new javax.swing.JLabel();
        stdDevField = new javax.swing.JFormattedTextField();
        nonLineraPhasingChekckBox = new javax.swing.JCheckBox();
        generateAbscissaComboBox = new javax.swing.JCheckBox();
        process_lbl1 = new javax.swing.JLabel();
        generateAbscissaLabel = new javax.swing.JLabel();
        jResetSave = new interfacebeans.JResetSave();
        jserver = new interfacebeans.JServer();
        graph_panel = AllViewers.getInstance();

        FormListener formListener = new FormListener();

        setPreferredSize(new java.awt.Dimension(1200, 750));
        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        tools.setName("tools"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        nonlibearLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        nonlibearLabel.setText("Nonlinear Phasing");
        nonlibearLabel.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nWhen the Nonlinear check box is activated<br>\nthe nonlinear phasing routine is also run.  <br>\nRunning this routine is the default.<br>\n\n\n</html>"); // NOI18N
        nonlibearLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nonlibearLabel.setName("nonlibearLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(nonlibearLabel, gridBagConstraints);

        process_cb.setSelectedItem(this.getProcess());
        process_cb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSelect how the data are to be phased.<br>\n<font color=\"red\" size = \"+1\"><bold>All</font></bold>, Each image is phased positive<br> \n<font color=\"red\" size = \"+1\"><bold>Common</font>, Each image has the phase of the currently displayed image.<br> \n</html>\n\n"); // NOI18N
        process_cb.setName("process_cb"); // NOI18N
        process_cb.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(process_cb, gridBagConstraints);

        noiseStdDevLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        noiseStdDevLabel.setText("Noise SD");
        noiseStdDevLabel.setName("noiseStdDevLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(noiseStdDevLabel, gridBagConstraints);

        stdDevField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.##########"))));
        stdDevField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        stdDevField.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSet noise standard deviation.\n</html>\n\n"); // NOI18N
        stdDevField.setInputVerifier(new PositiveFloatInputVerifier());
        stdDevField.setName("stdDevField"); // NOI18N
        stdDevField.setValue( getNoiseStandardDeviation ());
        stdDevField.addPropertyChangeListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(stdDevField, gridBagConstraints);

        nonLineraPhasingChekckBox.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        nonLineraPhasingChekckBox.setContentAreaFilled(false);
        nonLineraPhasingChekckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        nonLineraPhasingChekckBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        nonLineraPhasingChekckBox.setIconTextGap(16);
        nonLineraPhasingChekckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        nonLineraPhasingChekckBox.setName("nonLineraPhasingChekckBox"); // NOI18N
        nonLineraPhasingChekckBox.setRolloverEnabled(true);
        nonLineraPhasingChekckBox.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        nonLineraPhasingChekckBox.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        nonLineraPhasingChekckBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nonLinearPhasing}"), nonLineraPhasingChekckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        nonLineraPhasingChekckBox.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(nonLineraPhasingChekckBox, gridBagConstraints);

        generateAbscissaComboBox.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        generateAbscissaComboBox.setContentAreaFilled(false);
        generateAbscissaComboBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        generateAbscissaComboBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        generateAbscissaComboBox.setIconTextGap(16);
        generateAbscissaComboBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        generateAbscissaComboBox.setName("generateAbscissaComboBox"); // NOI18N
        generateAbscissaComboBox.setRolloverEnabled(true);
        generateAbscissaComboBox.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        generateAbscissaComboBox.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        generateAbscissaComboBox.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${generateAbsicssa}"), generateAbscissaComboBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        generateAbscissaComboBox.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(generateAbscissaComboBox, gridBagConstraints);

        process_lbl1.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        process_lbl1.setText("Process");
        process_lbl1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        process_lbl1.setName("process_lbl1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(process_lbl1, gridBagConstraints);

        generateAbscissaLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        generateAbscissaLabel.setText("Generate Abscissa");
        generateAbscissaLabel.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nGenerate abscissa<br>\n\n\n\n</html>"); // NOI18N
        generateAbscissaLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateAbscissaLabel.setName("generateAbscissaLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(generateAbscissaLabel, gridBagConstraints);

        jResetSave.setName("jResetSave"); // NOI18N

        jserver.setName("jserver"); // NOI18N

        org.jdesktop.layout.GroupLayout toolsLayout = new org.jdesktop.layout.GroupLayout(tools);
        tools.setLayout(toolsLayout);
        toolsLayout.setHorizontalGroup(
            toolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );
        toolsLayout.setVerticalGroup(
            toolsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jResetSave, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
            .add(jRun, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
            .add(jserver, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );

        toolsLayout.linkSize(new java.awt.Component[] {jPanel2, jResetSave, jRun, jserver}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setTopComponent(tools);

        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setBottomComponent(graph_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == process_cb) {
                BayesPhase.this.process_cbActionPerformed(evt);
            }
            else if (evt.getSource() == nonLineraPhasingChekckBox) {
                BayesPhase.this.nonLineraPhasingChekckBoxActionPerformed(evt);
            }
            else if (evt.getSource() == generateAbscissaComboBox) {
                BayesPhase.this.generateAbscissaComboBoxActionPerformed(evt);
            }
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == stdDevField) {
                BayesPhase.this.stdDevFieldPropertyChange(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

private void process_cbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_process_cbActionPerformed
        setProcess((IMAGE_PROCESS) getProcessComboBox().getSelectedItem());
          clearPreviousRun();
}//GEN-LAST:event_process_cbActionPerformed
private void stdDevFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stdDevFieldPropertyChange
    if (evt.getPropertyName().equalsIgnoreCase("value") == false){return;}
    double nsd  =   (double) (Double)getStdDevField().getValue();
    
    
    setNoiseStandardDeviation( nsd );
     clearPreviousRun();
}//GEN-LAST:event_stdDevFieldPropertyChange

private void nonLineraPhasingChekckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonLineraPhasingChekckBoxActionPerformed
     clearPreviousRun();
}//GEN-LAST:event_nonLineraPhasingChekckBoxActionPerformed

private void generateAbscissaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateAbscissaComboBoxActionPerformed
    clearPreviousRun();
}//GEN-LAST:event_generateAbscissaComboBoxActionPerformed
    public void              setPackageParameters(ObjectInputStream ois) throws Exception{
            IMAGE_PROCESS   imp                 =   ( IMAGE_PROCESS)ois.readObject();
            double   noiseSdev                  =   ( Double)ois.readObject();
            boolean  nonLinearPhs               =   ( Boolean)ois.readObject();
            boolean  isGenerateAbscissa         =   ( Boolean)ois.readObject();

            this.setProcess(imp);
            this.setNoiseStandardDeviation(noiseSdev);
            this.setNonLinearPhasing( nonLinearPhs);
            this.setGenerateAbsicssa(isGenerateAbscissa);
    }
    public void              savePackageParameters(ObjectOutputStream oos){
       
        try{

             oos.writeObject(this.getProcess());
             oos.writeObject(this.getNoiseStandardDeviation());
             oos.writeObject(this.isNonLinearPhasing());
             oos.writeObject(this.isGenerateAbsicssa());
        } 
        catch ( Exception exp){
             DirectoryManager.getSerializationFile().delete();
        }
    }
    public boolean           isReadyToRun() {
        // make sure data has been loaded
        File dir            =   DirectoryManager.getBayesOtherAnalysisDir();
        File fidFile        =   DirectoryManager.getImageFidFile();
        File prpFile        =   DirectoryManager.getImageProcparFile();
        
        if (fidFile.exists() == false) {
            DisplayText.popupMessage(fidFile.getPath() + " fid file is missing.\nExit run");
            return false;
        }
        
       if (prpFile.exists() == false) {
            
            DisplayText.popupMessage(prpFile.getPath() + " procpar file is missing.\nExit run");
            return false;
        }
        boolean  isImage = Procpar.isImage(prpFile);
        if (isImage == false){
            DisplayText.popupMessage("Fid data is not an image.\nExit run");
            return false;
        }


      // make sure a standard deviation was set
        double  sd = getNoiseStandardDeviation();
         {
            if (sd <= 0) {
               DisplayText.popupErrorMessage("Noise standard deviation is not set.\nExiting run.");
               return false;
            }
        }


        
       // write the parameter file
       boolean bl  =  WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){
            DisplayText.popupErrorMessage("Failed to write Bayes.params file.");
           return false;
       }
        

      
       //  write "job.directions" file
         PackageManager.setCurrentApplication(this);
         bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT_IMAGE);
         if(bl == false){
            
            DisplayText.popupErrorMessage("Failed to write job.directions file.");
             
            return false;
         }
      
        return true;
    }
    public String            getProgramName() {
         return "BayesPhase";
    }
    public String            getExtendedProgramName() { return "Bayes Phase" ;}
    public int               getNumberOfAbscissa() { 
       return 1;
    }
    public int               getNumberOfDataColumns() {
         /*make sure default is 1. Otherwise extracted image pixel
          *in the absense of loaded model, won't display
          */
        return 1 ;
    }
    public int               getTotalNumberOfColumns(){return 2;}
    public int               getNumberOfPriors() {
       return getPriors().size();
    }
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) {
            StringBuilder sb        =   new StringBuilder();
            ImageViewer iv          =   ImageViewer.getInstance();
            int curElement          =   iv.getCurrentElement() ;
            int curSlice            =   iv.getCurrentSlice() ;



            if ( curElement == 0) { curElement = 1;}
            if ( curSlice == 0)   { curSlice = 1;}


            // if possible - get original element number in
            // preprocessed image fid.
            // if no maprs are present  - this will just return input value
           curElement  =getCurrentElementNumberInPreProcessedFid( curElement );



            
            sb.append( IO.pad("Package Parameters", -PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append(9);
            sb.append(EOL);
            
         
           
         
            sb.append( IO.pad("Current Slice", -PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append(curSlice);
            sb.append(EOL);


            sb.append( IO.pad("Cur Array Element", -PADLEN, PADCHAR ));
            sb.append(" = ") ;
            sb.append(curElement);
            sb.append(EOL);

            sb.append( IO.pad("Process Images", -PADLEN, PADCHAR ));
            sb.append(" = ") ;
            sb.append(getProcess().toString());
            sb.append(EOL);


            sb.append( IO.pad("Run NonLinear Phasing", -PADLEN, PADCHAR ));
            sb.append(" = ") ;
            sb.append(this.runNonLinearPhasing());
            sb.append(EOL);


            sb.append( IO.pad("Noise Std. Dev.", -PADLEN, PADCHAR ));
            sb.append(" = ") ;
            sb.append(getNoiseStandardDeviation());
            sb.append(EOL);

            sb.append( IO.pad("Write Imaginary", -PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append("Yes"); 
            sb.append(EOL);


            sb.append( IO.pad("Generate Abscissia ", -PADLEN, PADCHAR ));
            sb.append(" = ") ;
            sb.append(this.generateAbscissia());
            sb.append(EOL);

            
            sb.append( IO.pad("Original Fid File Dir", -PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append(Procpar.getFileSource( DirectoryManager.getImageProcparFile()));
            sb.append(EOL);
         
            sb.append( IO.pad("Input Fid Dir", -PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append(BayesManager.IMAGE_FID_DIR);
            sb.append(EOL);
         
            sb.append( IO.pad("Output Image Dir Name",-PADLEN, PADCHAR )); 
            sb.append(" = ") ;
            sb.append(BayesManager.IMAGE_DIR_NAME);
            sb.append(EOL);
              
         
        
         return sb;
    }
    public List<ParameterPrior>getPriors() {
        return new Vector<ParameterPrior>();
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.BAYES_PHASE.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              reset(){
          
           JAllPriors .reset();
            setDefaults();
            clearPreviousRun();
            // show default viewer
            AllViewers.getInstance().showDefaultViewer();
    }

    public void              clearPreviousRun(){
        Reset.clearImageResutls();


         // reset job status
         jRun.reset();

    }
    public void              setDefaults(){
             setGenerateAbsicssa(true);
             getGenerateAbscissaComboBox().setSelected(isGenerateAbsicssa());

             setNonLinearPhasing(true);
             getNonLineraPhasingComboBox().setSelected(isNonLinearPhasing());

             setNoiseStandardDeviation(0f);
             getStdDevField ().setValue(getNoiseStandardDeviation());

             setProcess(IMAGE_PROCESS.All);
             getProcessComboBox().setSelectedItem(getProcess());
    }

    public void              setActive(boolean enabled){
       AllViewers.getInstance().setActive(enabled);

       jserver.setActive(enabled);
       jRun.setEnabled(enabled);
       jResetSave.setActive(enabled);
       getStdDevField ().setEnabled(enabled);
       getGenerateAbscissaComboBox().setEnabled(enabled);
       getNonLineraPhasingComboBox().setEnabled(enabled);
       getProcessComboBox().setEnabled(enabled);
       generateAbscissaLabel.setEnabled(enabled);
       nonlibearLabel.setEnabled(enabled);
       noiseStdDevLabel.setEnabled(enabled);
       process_lbl1.setEnabled(enabled);

    }
    public void              propertyChange(java.beans.PropertyChangeEvent evt){
        
      if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_START)){
          setActive(false);
      }
      
      else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_END)){
         setActive(true);
          ImageViewer.getInstance().updateImageList();
          AllViewers.showImageViewer();
          return;
      }
      
      else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_CANCELED)){
          setActive(true);
      }
  
   }

   public Collection <File> getFilesToTar(){

        List<File> files     =   new  ArrayList<File>();
        files.add (DirectoryManager.getBayesOtherAnalysisDir());// asciiDIR
        files.add ( DirectoryManager.getImageFidDir());


        return files;
    }
   public void              destroy(){};

    public int getCurrentElementNumberInPreProcessedFid(int elem){

            File pfile        = DirectoryManager.getImageProcparFile();
            try{
                Procpar procpar   =  new Procpar(pfile) ;
                int rawElem       = procpar.getElementNumberdWhenMapsAreIncluded(elem);
                return rawElem;

            }
            catch (Exception exp ){
                exp.printStackTrace();
                return elem;
            }

    }


    public static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("Bayes Phase ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });

        frame.add(new BayesPhase());

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
    public javax.swing.JCheckBox generateAbscissaComboBox;
    private javax.swing.JLabel generateAbscissaLabel;
    private javax.swing.JPanel graph_panel;
    private javax.swing.JPanel jPanel2;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JLabel noiseStdDevLabel;
    public javax.swing.JCheckBox nonLineraPhasingChekckBox;
    private javax.swing.JLabel nonlibearLabel;
    private javax.swing.JComboBox process_cb;
    private javax.swing.JLabel process_lbl1;
    private javax.swing.JFormattedTextField stdDevField;
    private javax.swing.JPanel tools;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JFormattedTextField getStdDevField () {
        return stdDevField;
    }
    public javax.swing.JCheckBox getGenerateAbscissaComboBox() {
        return generateAbscissaComboBox;
    }
    public javax.swing.JCheckBox getNonLineraPhasingComboBox() {
        return nonLineraPhasingChekckBox;
    }
    public javax.swing.JComboBox getProcessComboBox() {
        return process_cb;
    }
   
    public IMAGE_PROCESS    getProcess () {
        return process;
    }
    public void         setProcess ( IMAGE_PROCESS process ) {
        this.process = process;
    }

    public double       getNoiseStandardDeviation() {
        return noiseStandardDeviation;
    }
    public void         setNoiseStandardDeviation(double noiseStandardDeviation) {
        this.noiseStandardDeviation = noiseStandardDeviation;
    }

    public boolean      isNonLinearPhasing() {
        return nonLinearPhasing;
    }
    public void         setNonLinearPhasing(boolean nonLinearPhasing) {
        this.nonLinearPhasing = nonLinearPhasing;
    }

    public boolean      isGenerateAbsicssa() {
        return generateAbsicssa;
    }
    public void         setGenerateAbsicssa(boolean generateAbsicssa) {
        this.generateAbsicssa = generateAbsicssa;
    }


     public String runNonLinearPhasing(){
        if (isNonLinearPhasing())   {return "Yes";}
        else                        {return "No";}
     }
     public String generateAbscissia(){
        if (isGenerateAbsicssa())   {return "Yes";}
        else                        {return "No";}
     }


}
