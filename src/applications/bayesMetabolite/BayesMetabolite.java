/*
 * BayesMetabolite.java
 *
 * Created on August 27, 2008, 10:16 AM
 */

package applications.bayesMetabolite;
import bayes.PackageManager;
import bayes.WriteBayesParams;
import bayes.JobDirections;
import bayes.BayesManager;
import bayes.DirectoryManager;
import run.JRun;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import utilities.*;
import interfacebeans.*;
import fid.*;
import load.LoadMetabolites;
import load.gui.JRemoteFileChooser;
import applications.model.*;
import static bayes.Enums.*;
import bayes.ParameterPrior;
import static load.gui.JRemoteFileChooser.*;
import bayes.ParameterPrior.ORDER_TYPE;
import bayes.ParameterPrior.PRIOR_TYPE;
import bayes.ParameterPrior.PARAMETER_TYPE;
import bayes.Reset;
/**
 *
 * @author  apple
 */
public class BayesMetabolite extends javax.swing.JPanel 
                        implements  FidModel,PriorsInFileModel,
                                    java.beans.PropertyChangeListener{



    /** Creates new form BayesMetabolite */
    public BayesMetabolite() {
        LoadPackage.loadPackage(this);
        PackageManager.setCurrentApplication(this);
        boolean isDeserialized = bayes.Serialize.deserializeCurrenExperiment();


        AllViewers.removeFidViewer();
        AllViewers.removePriorsViewer();
        AllViewers.addFidPriorHybrid();
       // JAllPriors.getInstance().setAllowDeleteParameters(true);

        boolean cleanBayesAnalyzeAndModelFIles = !isDeserialized;
        
        FidViewer.getInstance().getPopupMenu().setUnitsEnabled(false);
        FidViewer.getInstance().getFidReader().setUnits(UNITS.PPM, cleanBayesAnalyzeAndModelFIles);

        FidModelViewer.getInstance().setVisibleModelBuild(false);
        FidModelViewer.getInstance().getFidReader().setUnits(UNITS.PPM, cleanBayesAnalyzeAndModelFIles);

        initComponents();
        
        BayesManager.pcs.addPropertyChangeListener(this);
        
        if(isDeserialized){JRun.fireJobIDChange();}
        
         
        // clear BayesAnalyzeFiles directory.
        // IT is not used in this package
        File BayesAnalyzeFilesdir =  DirectoryManager.getBayesAnalyzeDir();
        IO.emptyDirectory( BayesAnalyzeFilesdir);
        
         
        
        FidViewer.getInstance().getChartPanel().setDrawMetabolites(true);
        JAllPriors.getInstance().setUpdateFreqPriors (false);
        JAllPriors.getInstance().addPropertyChangeListener(this);


        if(isDeserialized){
            AllViewers.showFidViewer();
            JRun.fireJobIDChange();
        }
        else {
            AllViewers.showInstructions();
        }
    }
    
    public void              propertyChange(java.beans.PropertyChangeEvent evt){
      if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_END)  ){
          this.setActive(true);
          AllViewers.showResultsViewer();
      }
 
      else if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_END)  ){
          this.setActive(true);
      }
      
      else if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_START)  ){
          this.setActive(false);   
      }
      else if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_CANCELED)  ){
          this.setActive(true);   
      }

      else if(evt.getPropertyName().equals(BayesManager.FID_LOADED_BY_USER  )  ){
          reset();  
      }
       
      else if(evt.getPropertyName().equals(BayesManager.FID_LOADED_BY_JAVA)  ){
              updateOnDeserialization();

      }
    
      else if(evt.getPropertyName().equals(BayesManager.FREQ_PARAM_REMOVED)  ){
          ParameterPrior param = (ParameterPrior)evt.getNewValue();
          RemoveFreqOrRateParameter(param);
      }
       else if(evt.getPropertyName().equals(BayesManager.FID_UNITS_ARE_CHANGED)  ){
           clearPreviousRun();
      }  
 
     
   }
    public void              setPackageParameters(ObjectInputStream serializationFile)throws Exception{
           
       MetabolitesAndResonances m   =  (MetabolitesAndResonances)serializationFile.readObject();
       UNITS freqUnits              =  (UNITS)serializationFile.readObject();
       
       
        MetabolitesAndResonances.setMetabolitesAndResonances(m);
        Metabolite.frequencyUnits   =   freqUnits;
         
    }
    public void              savePackageParameters(ObjectOutputStream serializationFile){
        try{
  
            serializationFile.writeObject(this.getMetabolitesAndResonances());
            serializationFile.writeObject(Metabolite.frequencyUnits);
            
        } catch (Exception exp){
            File file  = DirectoryManager.getSerializationFile();
            file.delete();
        }
    }
    public boolean           isReadyToRun() {
       File dir                 =   DirectoryManager.getBayesOtherAnalysisDir();
       
       
       // make sure data is loaded
        boolean isDataLoaded = FidViewer. isValidFidData( );
        if (isDataLoaded = false) {
          DisplayText.popupDialog("You must load data before you run the program");
          return false;
        }
        
     MetabolitesAndResonances metabolitesAndResonances = this.getMetabolitesAndResonances();  
     boolean isMetabolitesMarked   = metabolitesAndResonances.isResonances();
     if (isMetabolitesMarked == false){

         DisplayText.popupErrorMessage( "No resonances are loaded or marked.\n" +
                                   "Please load .Res files or mark nuisance\n" +
                                   "metabolite using \"Add Resonance\" button.");
             return false;
     }
 

      // write .Iso and .Res files
        
        writeIsoAndResFiles();
    
         // write the parameter file
    
       boolean bl               =   WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){
          DisplayText.popupErrorMessage(  "Failed to write Bayes.params file.");
          return false;
       }   
        
     //  write "job.directions" file
      PackageManager.setCurrentApplication(this);
      bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT);
      if(bl == false){
        DisplayText.popupErrorMessage("Failed to write job.directions file.");
        return false;
      }
        return true;
    }
    public String            getProgramName() { return "BayesMetabolite";}
    public String            getExtendedProgramName() { return "Bayes Metabolite" ;}
    public int               getNumberOfAbscissa() {  return 1;}
    public int               getNumberOfDataColumns() { return 1;}
    public int               getTotalNumberOfColumns(){return 1;}
    public int               getNumberOfPriors() {
      return  JAllPriors.getInstance(). getParams().size();
    }
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) {
         StringBuilder buffer   =   new StringBuilder();
         int nPackageParams     =   7;
         String tmp;
          
         tmp                    =  IO.pad("Package Parameters", -PADLEN, PADCHAR ); 
         buffer.append(tmp  + " = "+nPackageParams);
         buffer.append(BayesManager.EOL);
                 
         String fidFileRelativePath = BayesManager.FID_DIR_NAME;
         tmp                    =  IO.pad("Fid  dir", -PADLEN, PADCHAR ); 
         buffer.append(tmp  + " = " + fidFileRelativePath);
         buffer.append(BayesManager.EOL);
         
         tmp                    =  IO.pad("Model Fid Name", -PADLEN, PADCHAR );        
         buffer.append(tmp  + " = " + BayesManager.MODEL_FID_DIR );
         buffer.append(BayesManager.EOL);
         
         boolean isIsotopomerTable =  isIsotopomerTable();
         tmp                    =  IO.pad("Isotopomer Tables", -PADLEN, PADCHAR );   
         int nTables            =  (isIsotopomerTable) ? 1:0;
         buffer.append(tmp  + " = " + nTables );
         buffer.append(BayesManager.EOL);
         
         String str;
         if (isIsotopomerTable ){
             str                    =  getMetabolitesAndResonances().getIsoFile().getName();
             tmp                    =  IO.pad("Table", -PADLEN, PADCHAR ); 
             buffer.append(tmp  + " = " + BayesManager.ASCII_DIR_NAME + spr +str);
             buffer.append(BayesManager.EOL);
         }

       // if (isResonanceTable()== true){

            str        =   getMetabolitesAndResonances().getResFile().getName();
            tmp                    =  IO.pad("Resonance Table", -PADLEN, PADCHAR );
            buffer.append(tmp  + " = " + BayesManager.ASCII_DIR_NAME + spr +str);
            buffer.append(BayesManager.EOL);
      // }
        
         
         FidViewer viewer   =   FidViewer.getInstance();
         float refInHerz    =   viewer.getFidReader().getReferenceFreqInHertz();
         float reffreq      =   viewer.getProcpar().getSpectroscoperRefFrequency();
         float ref          =   Units.convertUnits(refInHerz, reffreq,  UNITS.HERTZ, UNITS.PPM);
         
         tmp                    =  IO.pad("Reference", -PADLEN, PADCHAR );
         buffer.append(tmp  + " = " + ref);
         buffer.append(BayesManager.EOL);
         
         tmp                    =  IO.pad("Spec Reference", -PADLEN, PADCHAR );
         buffer.append(tmp  + " = " + reffreq);
         buffer.append(BayesManager.EOL);
 
         return buffer;
        
    }
    public List<ParameterPrior>  getPriors() {
        return  JAllPriors.getInstance().getParams();
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.BAYES_METABOLITE.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              reset(){
            JAllPriors priors = JAllPriors.getInstance();
            priors.setNoParams();
            getMetabolitesAndResonances().reset();


            setDefaults();
            clearPreviousRun();

            FidViewer.getInstance().updatePlot();

            // show defualt viewer
            AllViewers.getInstance().showDefaultViewer();

    }
     public void              fidReferenceFrequncyChange(){
        reset();
    }
    public void              clearPreviousRun(){
        Reset.clearFidResutls();


         // reset job status
         jRun.reset();

    }
    public void              setDefaults(){

    }
    public void              setActive(boolean isEnabled){
         AllViewers.getInstance().setActive(isEnabled);

        shiftFreqLeft_btn.setEnabled(isEnabled);
        adjustFreq_pane.setEnabled(isEnabled);
        shiftFreqLeft_btn.setEnabled(isEnabled);
        shiftFreqRight_btn.setEnabled(isEnabled);
        graph_pane.setEnabled(isEnabled);
        loadSystemMetabolite_btn.setEnabled(isEnabled);
        loadSystemResonance_btn.setEnabled(isEnabled);
        loadUserResonance_btn.setEnabled(isEnabled);
        loadUserMetabolite_btn.setEnabled(isEnabled);
        save_btn.setEnabled(isEnabled);
        addFreqParam.setEnabled(isEnabled);
        removeResButton.setEnabled(isEnabled);
        jLabel1.setEnabled(isEnabled);
        jLabel2.setEnabled(isEnabled);
        jserver.setActive(isEnabled);
        jRun.setEnabled(isEnabled);
        resetButton.setEnabled(isEnabled);
    }
    public void              destroy(){};
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        tool_pane = new javax.swing.JPanel();
        jRun = new run.JRun();
        adjustFreq_pane = new javax.swing.JPanel();
        shiftFreqLeft_btn = new javax.swing.JButton();
        shiftFreqRight_btn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        loadSystemMetabolite_btn = new javax.swing.JButton();
        loadUserMetabolite_btn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        loadSystemResonance_btn = new javax.swing.JButton();
        loadUserResonance_btn = new javax.swing.JButton();
        adjustFreq_pane1 = new javax.swing.JPanel();
        addFreqParam = new javax.swing.JButton();
        removeResButton = new javax.swing.JButton();
        setAndResetPane = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        save_btn = new javax.swing.JButton();
        jserver = new interfacebeans.JServer();
        graph_pane = AllViewers.getInstance();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        tool_pane.setName("tool_pane"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        adjustFreq_pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Frequencies"));
        adjustFreq_pane.setName("adjustFreq_pane"); // NOI18N
        adjustFreq_pane.setLayout(new java.awt.GridBagLayout());

        shiftFreqLeft_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        shiftFreqLeft_btn.setText("Shift Left");
        shiftFreqLeft_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nPressing this button will shift current resonances<br>\nto the left by amout equal to a distance between<br>\nthe cursors that are placed on the FID plot.<br>\nClick left or right mouse buttons to place cursors.<br>\nDragging mouse while left or right button is pressed<br>\nwill modify cursor's positions.\n\n</html>\n"); // NOI18N
        shiftFreqLeft_btn.setName("shiftFreqLeft_btn"); // NOI18N
        shiftFreqLeft_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        adjustFreq_pane.add(shiftFreqLeft_btn, gridBagConstraints);

        shiftFreqRight_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        shiftFreqRight_btn.setText("Shift Right"); // NOI18N
        shiftFreqRight_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nPressing this button will shift current resonances<br>\nto the right by amout equal to a distance between<br>\nthe cursors that are placed on the FID plot.<br>\nClick left or right mouse buttons to place cursors.<br>\nDragging mouse while left or right button is pressed<br>\nwill modify cursor's positions.\n\n</html>"); // NOI18N
        shiftFreqRight_btn.setName("shiftFreqRight_btn"); // NOI18N
        shiftFreqRight_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        adjustFreq_pane.add(shiftFreqRight_btn, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Load"));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel1.setText("Metabolites");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jLabel1, gridBagConstraints);

        loadSystemMetabolite_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        loadSystemMetabolite_btn.setText("System");
        loadSystemMetabolite_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad metabolites from  predefined .ISO file \"<br>\nlocated in the server System repository.\n</html>\n"); // NOI18N
        loadSystemMetabolite_btn.setName("loadSystemMetabolite_btn"); // NOI18N
        loadSystemMetabolite_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(loadSystemMetabolite_btn, gridBagConstraints);

        loadUserMetabolite_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        loadUserMetabolite_btn.setText("User");
        loadUserMetabolite_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad metabolites from  predefined .ISO file \"<br>\nlocated in the local User repository.\n</html>\n"); // NOI18N
        loadUserMetabolite_btn.setName("loadUserMetabolite_btn"); // NOI18N
        loadUserMetabolite_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(loadUserMetabolite_btn, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel2.setText("Resonances");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jLabel2, gridBagConstraints);

        loadSystemResonance_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        loadSystemResonance_btn.setText("System ");
        loadSystemResonance_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad  resonances  from  predefined .RES file \"<br>\nlocated in the server System repository.\n</html>\n"); // NOI18N
        loadSystemResonance_btn.setName("loadSystemResonance_btn"); // NOI18N
        loadSystemResonance_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(loadSystemResonance_btn, gridBagConstraints);

        loadUserResonance_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        loadUserResonance_btn.setText("User ");
        loadUserResonance_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad  resonances  from  predefined .RES file \"<br>\nlocated in the local User repository.\n</html>\n"); // NOI18N
        loadUserResonance_btn.setName("loadUserResonance_btn"); // NOI18N
        loadUserResonance_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(loadUserResonance_btn, gridBagConstraints);

        adjustFreq_pane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Resonances"));
        adjustFreq_pane1.setName("adjustFreq_pane1"); // NOI18N
        adjustFreq_pane1.setLayout(new java.awt.GridBagLayout());

        addFreqParam.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        addFreqParam.setText("Add ");
        addFreqParam.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nPlace cursors on the max and min locations<br>\n of the metabolite resonance  in the plot. <br>\nThen press this button.\n</html>\n\n\n"); // NOI18N
        addFreqParam.setName("addFreqParam"); // NOI18N
        addFreqParam.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        adjustFreq_pane1.add(addFreqParam, gridBagConstraints);

        removeResButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        removeResButton.setText("Remove ");
        removeResButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nTo delete  resonance(s), place both cursors<br>\nin the plot and press this button.<br>\nAll resonance(s) located between the cursors <br>\nwill be deleted. If no cursors (or only a single <br>\ncursor) are displayed, no action will be taken. <br>\n\n\n</html>\n\n\n"); // NOI18N
        removeResButton.setName("removeResButton"); // NOI18N
        removeResButton.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        adjustFreq_pane1.add(removeResButton, gridBagConstraints);

        setAndResetPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Save/Reset"));
        setAndResetPane.setName("setAndResetPane"); // NOI18N
        setAndResetPane.setLayout(new java.awt.GridBagLayout());

        resetButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        resetButton.setText("Reset");
        resetButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSave current working directory.\n\n</html>\n\n\n\n"); // NOI18N
        resetButton.setName("resetButton"); // NOI18N
        resetButton.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        setAndResetPane.add(resetButton, gridBagConstraints);

        save_btn.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        save_btn.setText("Save ISO and RES");
        save_btn.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nUpdate .ISO and .Res files in local \"User\" repository<br>\nand in the current working directory.\n</html>\n\n\n"); // NOI18N
        save_btn.setName("save_btn"); // NOI18N
        save_btn.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        setAndResetPane.add(save_btn, gridBagConstraints);

        jserver.setName("jserver"); // NOI18N

        org.jdesktop.layout.GroupLayout tool_paneLayout = new org.jdesktop.layout.GroupLayout(tool_pane);
        tool_pane.setLayout(tool_paneLayout);
        tool_paneLayout.setHorizontalGroup(
            tool_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tool_paneLayout.createSequentialGroup()
                .addContainerGap()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 314, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adjustFreq_pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adjustFreq_pane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(setAndResetPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(323, Short.MAX_VALUE))
        );
        tool_paneLayout.setVerticalGroup(
            tool_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adjustFreq_pane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(adjustFreq_pane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .add(setAndResetPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .add(tool_paneLayout.createSequentialGroup()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(tool_paneLayout.createSequentialGroup()
                .add(jserver, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addContainerGap())
        );

        tool_paneLayout.linkSize(new java.awt.Component[] {adjustFreq_pane, adjustFreq_pane1, jPanel1, jRun, jserver, setAndResetPane}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setLeftComponent(tool_pane);

        graph_pane.setName("graph_pane"); // NOI18N
        graph_pane.setLayout(new javax.swing.BoxLayout(graph_pane, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(graph_pane);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == shiftFreqLeft_btn) {
                BayesMetabolite.this.shiftFreqLeft_btnActionPerformed(evt);
            }
            else if (evt.getSource() == shiftFreqRight_btn) {
                BayesMetabolite.this.shiftFreqRight_btnActionPerformed(evt);
            }
            else if (evt.getSource() == loadSystemMetabolite_btn) {
                BayesMetabolite.this.loadSystemMetabolite_btnActionPerformed(evt);
            }
            else if (evt.getSource() == loadUserMetabolite_btn) {
                BayesMetabolite.this.loadUserMetabolite_btnActionPerformed(evt);
            }
            else if (evt.getSource() == loadSystemResonance_btn) {
                BayesMetabolite.this.loadSystemResonance_btnActionPerformed(evt);
            }
            else if (evt.getSource() == loadUserResonance_btn) {
                BayesMetabolite.this.loadUserResonance_btnActionPerformed(evt);
            }
            else if (evt.getSource() == addFreqParam) {
                BayesMetabolite.this.addFreqParamActionPerformed(evt);
            }
            else if (evt.getSource() == removeResButton) {
                BayesMetabolite.this.removeResButtonActionPerformed(evt);
            }
            else if (evt.getSource() == resetButton) {
                BayesMetabolite.this.resetButtonActionPerformed(evt);
            }
            else if (evt.getSource() == save_btn) {
                BayesMetabolite.this.save_btnActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

private void loadUserMetabolite_btnActionPerformed(java.awt.event.ActionEvent evt) { 
 
    loadUserMetabolite(null);
    AllViewers.showFidViewer();

    clearPreviousRun();
}
private void loadSystemMetabolite_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSystemMetabolite_btnActionPerformed
       String password               =   JServerPasswordDialog.getServerPassword();
       int    usrOption              =   JServerPasswordDialog.getInstance().getOption();
       if(   usrOption == JServerPasswordDialog.CANCEL ) {return ;}

       Server server                 =   JServer.getInstance().getServer();
       String url                    =   server.getHttpURL();
       String pathToListDir          =   server.getRelativePathToSpecDir();
       String user                   =   server.getUser();
       String bayesMetabURL          =   DirectoryManager. getSystemPredefinedSpecURL();


       try {

            callRemoteFileChooserMetabolites(url ,pathToListDir ,user, password);

           if ( JRemoteFileChooser.filename == null){return;}
       }
        catch (FileNotFoundException ex) {
           DisplayText.popupErrorMessage("Failed to list files on "+url+".");
           return;
       }   
        
       try {

             File metabFile              = LoadMetabolites.loadRemoteSystemMetaboliteFile(bayesMetabURL , JRemoteFileChooser.filename);
             loadUserMetabolite(metabFile);
       }
       catch(Exception ex){
            DisplayText.popupErrorMessage("Failed to download"+ JRemoteFileChooser.filename + " from "+ bayesMetabURL);
            return;
       }

       AllViewers.showFidViewer();
        clearPreviousRun();
}//GEN-LAST:event_loadSystemMetabolite_btnActionPerformed
private void loadSystemResonance_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSystemResonance_btnActionPerformed
       String password               =   JServerPasswordDialog.getServerPassword();
       int    usrOption              =   JServerPasswordDialog.getInstance().getOption();
       if(   usrOption == JServerPasswordDialog.CANCEL ) {return ;}

       Server server                 =   JServer.getInstance().getServer();
       String url                    =   server.getHttpURL();
       String pathToListDir          =   server.getRelativePathToSpecDir();
       String user                   =   server.getUser();
      
       String bayesMetabURL          =   DirectoryManager. getSystemPredefinedSpecURL();

       try {

            callRemoteFileChooserResonances(url , pathToListDir , user, password);

           if ( JRemoteFileChooser.filename == null){return;}
       }
        catch (FileNotFoundException ex) {
           DisplayText.popupErrorMessage("Failed to list files on "+url+".");
           ex.printStackTrace();
           return;
       }
      
      
        try {
            File resFile                =  LoadMetabolites.loadRemoteSystemMetaboliteFile(bayesMetabURL , JRemoteFileChooser.filename);
            loadUserResonances(resFile);
       }
       catch(Exception ex){
            DisplayText.popupErrorMessage("Failed to download JRemoteFileChooser.filename from "+ bayesMetabURL);
            return;
       }   


       clearPreviousRun();

        AllViewers.showFidViewer();

      
}//GEN-LAST:event_loadSystemResonance_btnActionPerformed
private void loadUserResonance_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadUserResonance_btnActionPerformed
    loadUserResonances(null);

    clearPreviousRun();
     AllViewers.showFidViewer();
}//GEN-LAST:event_loadUserResonance_btnActionPerformed
private void shiftFreqLeft_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftFreqLeft_btnActionPerformed
    double val = FidViewer.getInstance().getCursorsDifference();
    shiftFrequencies( val);

    clearPreviousRun();
  
}//GEN-LAST:event_shiftFreqLeft_btnActionPerformed
private void shiftFreqRight_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftFreqRight_btnActionPerformed
    double val = FidViewer.getInstance().getCursorsDifference();
    shiftFrequencies(  - val);

    clearPreviousRun();
}//GEN-LAST:event_shiftFreqRight_btnActionPerformed
private void addFreqParamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFreqParamActionPerformed
  addParameter();
  clearPreviousRun();
}//GEN-LAST:event_addFreqParamActionPerformed
private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_btnActionPerformed
   writeIsoAndResFiles();
   clearPreviousRun();
}//GEN-LAST:event_save_btnActionPerformed
private void removeResButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeResButtonActionPerformed
    removeParameter ();
    clearPreviousRun();
}//GEN-LAST:event_removeResButtonActionPerformed
private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
    reset();
}//GEN-LAST:event_resetButtonActionPerformed
    public void loadUserMetabolite(File metabFile ){
        if (metabFile == null) {
        
            File bayesModelDir =  DirectoryManager.getUserPredefinedSpecDir();
            if(!bayesModelDir.exists ()){  bayesModelDir.mkdirs(); }

            JFileChooser fc             = new JFileChooser(bayesModelDir);
            fc.setMultiSelectionEnabled(false);
            fc.addChoosableFileFilter(new BayesFileFilters.ISOFileChooserFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal               =   fc.showOpenDialog (fc);
            if (returnVal              !=   JFileChooser.APPROVE_OPTION) { return;}
            metabFile              =   fc.getSelectedFile();       
        }
        
        MetabolitesAndResonances metabolitesAndResonances = getMetabolitesAndResonances();
        JAllPriors.getInstance().getParams().clear();
        metabolitesAndResonances.getMetabolitesInfo().reset();
        
        File loadedFile             =   LoadMetabolites.loadUserMetaboliteFile(metabFile, METABOLITE_FILE_TYPE.IS0);
        MetaboliteInfo minfo        =   ISOFileReader.readFile(loadedFile );                                                      
        
        
        metabolitesAndResonances.setMetaboliteInfo(minfo);
        metabolitesAndResonances.setIsoFile(loadedFile);
          FidViewer viewer = FidViewer.getInstance();
        
        // Metabolite frequency units are initially in PPM.
        // Connvert them to Fid Units.
        float reffreq               =   viewer.getProcpar().getSpectroscoperRefFrequency();
        UNITS fidUnits              =   viewer.getUnits();
        
        
        MetaboliteInfo.convertUnits(metabolitesAndResonances.getMetabolites(),reffreq,UNITS.PPM, fidUnits );
        Metabolite.frequencyUnits   =   fidUnits; 
        
        JAllPriors.getInstance().setParams(metabolitesAndResonances.getAllPriors());
        viewer.updatePlot();
    
}
    public void loadUserResonances(File resFile ){
        if (resFile  == null) {
        
            File bayesModelDir =  DirectoryManager.getUserPredefinedSpecDir();
            if(!bayesModelDir.exists ()){  bayesModelDir.mkdirs(); }

            JFileChooser fc             = new JFileChooser(bayesModelDir);
            fc.setMultiSelectionEnabled(false);
            fc.addChoosableFileFilter(new BayesFileFilters.ResFileChooserFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal               =   fc.showOpenDialog (fc);
            if (returnVal              !=   JFileChooser.APPROVE_OPTION) { return;}
            resFile                     =   fc.getSelectedFile();       
        }
        JAllPriors jallPriors                             =  JAllPriors.getInstance();
        MetabolitesAndResonances metabolitesAndResonances = getMetabolitesAndResonances();
        ArrayList<Metabolite> resonances                  = metabolitesAndResonances.getResonances();
      
        
        ArrayList<Metabolite> nonLoadedMetabs             =  metabolitesAndResonances.getResonancesInfo().getNonLoadedMetabolites();
        
        jallPriors.getParams().clear();
        metabolitesAndResonances.getResonancesInfo().reset();
        
        
        File loadedFile               =   LoadMetabolites.loadUserMetaboliteFile(resFile, METABOLITE_FILE_TYPE.RES);
        MetaboliteInfo resInfo        =   ISOFileReader.readFile(loadedFile );                                                      
        
        
        metabolitesAndResonances.setResonanceInfo(resInfo);
        metabolitesAndResonances.setResFile(loadedFile);
        resonances                       = metabolitesAndResonances.getResonances();
        
        
        resInfo.getSiteNames().set(0, Metabolite.RES_SITE_NAME);
        int i = nonLoadedMetabs.size()+1;
        String index;
        for (Metabolite metabolite : resonances) {
            index = formatter.format(i);
            metabolite.setSiteName(Metabolite.RES_SITE_NAME);
            metabolite.setAbbreviation(Metabolite.RES_SITE_NAME + index);
            metabolite.getFrequency().name = JAllPriors.FREQ_PARAM_NAME +"_"+metabolite.getAbbreviation();
            metabolite.getRate().name      = JAllPriors.RATE_PARAM_NAME +"_"+metabolite.getAbbreviation();
            i+=1; 
        }
        
      
        resonances.addAll(0,  nonLoadedMetabs);
        
        FidViewer viewer = FidViewer.getInstance();
        
        // Metabolite frequency units are initially in PPM.
        // Connvert them to Fid Units.
        float reffreq               =   viewer.getProcpar().getSpectroscoperRefFrequency();
        UNITS fidUnits              =   viewer.getUnits();
        MetaboliteInfo.convertUnits(metabolitesAndResonances.getResonances(),reffreq,UNITS.PPM, fidUnits );
        Metabolite.frequencyUnits   =   fidUnits; 
       
        JAllPriors.getInstance().setParams(metabolitesAndResonances.getAllPriors());
        viewer.updatePlot();
}
    
    public void RemoveFreqOrRateParameter  (ParameterPrior param) {
        ArrayList<Metabolite> metparams =   MetabolitesAndResonances.getMetabolitesAndResonances().getMetabolites();
        ArrayList<Metabolite> resparams =   MetabolitesAndResonances.getMetabolitesAndResonances().getResonances();
      
        removeMetaboliteForFreqOrRatePrior(metparams, param);
        removeMetaboliteForFreqOrRatePrior( resparams, param);
   
        FidViewer.getInstance().updatePlot();
    }
    private void removeMetaboliteForFreqOrRatePrior (List <Metabolite> metabolites, ParameterPrior param){
       for (Metabolite metabolite :  metabolites) {
               double frequency         =   metabolite.getFrequency().mean;
               double rate              =   metabolite.getRate().mean;
               
                if (frequency == param.mean){
                     metabolites.remove(metabolite);
                     break;
                }
                else if (rate  ==  param.mean){
                      metabolites.remove(metabolite);
                     break;
                }
        }
    }
    
    public void updateOnDeserialization(){
         FidViewer viewer = FidViewer.getInstance();
        
        // Metabolite frequency units are initially in PPM.
        // Connvert them to Fid Units.
        float reffreq               =   viewer.getProcpar().getSpectroscoperRefFrequency();
        UNITS fidUnits              =   viewer.getUnits();
        UNITS metaUnits             =   Metabolite.frequencyUnits; 
       
        MetabolitesAndResonances metabolitesAndResonances = getMetabolitesAndResonances();
        MetaboliteInfo.convertUnits(metabolitesAndResonances.getMetabolites(),reffreq,metaUnits, fidUnits );
        MetaboliteInfo.convertUnits(metabolitesAndResonances.getResonances() ,reffreq,metaUnits, fidUnits );
        Metabolite.frequencyUnits   =   fidUnits; 
        
        JAllPriors.getInstance().setParams(metabolitesAndResonances.getAllPriors());
        viewer.updatePlot();
        FidViewer.getInstance().updatePlot();
    }
    public void writeIsoAndResFiles(){
       writeIsoFile();
       writeResFile();
    }
    public void writeIsoFile(){
        File userIsoDir             =   DirectoryManager.getUserPredefinedSpecDir();
        MetabolitesAndResonances m  =   getMetabolitesAndResonances();
        FidViewer  fv               =   FidViewer.getInstance();
        
        if (m.getMetabolitesInfo().isLoaded() == false )  {return ;}
        
        File isoFile                 =  m.getIsoFile();
        File userIsoFile             =  new File(userIsoDir,isoFile.getName() );
        
        
        ISOFileWriter.writeFile(m.getMetabolitesInfo(), isoFile, fv.getUnits(),  fv.getProcpar().getSpectroscoperRefFrequency ());
        IO.copyFile(isoFile, userIsoFile);
    }
    public void writeResFile(){
        File userIsoDir             =   DirectoryManager.getUserPredefinedSpecDir();
        File asciDir                =   DirectoryManager.getBayesOtherAnalysisDir();
        MetabolitesAndResonances m  =   getMetabolitesAndResonances();
        MetaboliteInfo resonances   =   m.getResonancesInfo() ;
        FidViewer  fv               =   FidViewer.getInstance();
        
        if (resonances.isLoaded() == false || resonances.getMetabolites().size()== 0){return;}
        
        if ( m.getResFile() == null)
        {
            String name             = MetabolitesAndResonances.DEFAULT_RES_FILE_NAME;
            File resFile            = new File (asciDir, name);
            m.setResFile(resFile);
            
            ISOFileWriter.writeFile(m.getResonancesInfo(), resFile , fv.getUnits(),  fv.getProcpar().getSpectroscoperRefFrequency ());
            
        }
        else {
        
            File resFile                 =  m.getResFile();
            File userResFile             =  new File(userIsoDir,resFile.getName() );
        
            ISOFileWriter.writeFile(m.getResonancesInfo(), resFile,  fv.getUnits(),  fv.getProcpar().getSpectroscoperRefFrequency ());
            IO.copyFile(resFile, userResFile);
        }
        
        
    }
    
    
    public void shiftFrequencies(double val){
        List<Metabolite> allMetabolites       =   getMetabolitesAndResonances().getAllMetabolitesAndResonances();
        List<String>         sites             =   getDisplayedSites();
        String               currentSite;

        for (Metabolite m : allMetabolites) {
            currentSite = m.siteName;
            if (sites.contains(currentSite) ==  true){
            m.getFrequency().mean += val;
            m.getFrequency().high += val;
            m.getFrequency().low  += val;
        }
        }
        FidViewer.getInstance().updatePlot();
        JAllPriors.getInstance().updateGUI();
    }
    public List<String>  getDisplayedSites(){
        FidViewer fidViewer                   =     FidViewer.getInstance();
        double lowFreq                        =     fidViewer.getChartPanel().getDomainAxis().getRange().getLowerBound();
        double highFreq                       =     fidViewer.getChartPanel().getDomainAxis().getRange().getUpperBound();
        List<Metabolite> allMetabolites       =     getMetabolitesAndResonances().getAllMetabolitesAndResonances();
        List<String>         sites            =     new   ArrayList<String> ();
        String               currentSite;

       for (Metabolite m : allMetabolites) {
            if(m.frequency.mean >= lowFreq &&  m.frequency.mean <= highFreq ){
                currentSite = m.siteName;
                if (sites.contains(currentSite) ==  false){
                    sites.add(currentSite);
                }
            }
       }
      return sites ;
    }  
    
    
    public  Collection<Metabolite> getDisplayedMetabolites(){
        Set<Metabolite>   selectedMetabs         =   new TreeSet <Metabolite>();

        try{
            FidViewer fidViewer                   =     FidViewer.getInstance();
            double lowFreq                        =     fidViewer.getChartPanel().getDomainAxis().getRange().getLowerBound();
            double highFreq                       =     fidViewer.getChartPanel().getDomainAxis().getRange().getUpperBound();
            List<Metabolite> allMetabolites       =     getMetabolitesAndResonances().getAllMetabolitesAndResonances();

           for (Metabolite m : allMetabolites) {
                if(m.frequency.mean >= lowFreq &&  m.frequency.mean <= highFreq ){
                     selectedMetabs.add(m);
                }
            }
         }
         finally{
                    return selectedMetabs;
          }

  }
    public  Collection<Metabolite> getMetabolitesBetweenCursors(){
        Set<Metabolite>   selectedMetabs         =   new TreeSet <Metabolite>();

        try{
            FidViewer fidViewer                   =     FidViewer.getInstance();
             if (FidViewer.getInstance().isShowing() == false){
                 AllViewers.showFidViewer();
            }


            if (fidViewer.getDataType() != FID_DATA_TYPE.SPECTRUM_REAL){
                fidViewer.setDataType(FID_DATA_TYPE.SPECTRUM_REAL);
            }




            //double lowFreq        =     fidViewer.getChartPanel().getDomainAxis().getRange().getLowerBound();
            //double highFreq       =     fidViewer.getChartPanel().getDomainAxis().getRange().getUpperBound();


            if (    fidViewer.getChartPanel().getCursorA() != null
                   &&
                    fidViewer.getChartPanel().getCursorB() != null  )
            {
                double lowFreq                 =   fidViewer.getChartPanel().getLowerBoundFromCursorBox();
                double highFreq                =   fidViewer.getChartPanel().getUpperBoundFromCursorBox();
           
                
                List<Metabolite> allMetabolites       =     getMetabolitesAndResonances().getAllMetabolitesAndResonances();

               for (Metabolite m : allMetabolites) {
                    if(m.frequency.mean >= lowFreq &&  m.frequency.mean <= highFreq ){
                         selectedMetabs.add(m);
                    }
                }

            }

         }
         finally{
                    return selectedMetabs;
          }

  }
    
    
    public  void addParameter () {
        JAllPriors priors                   =  JAllPriors.getInstance();
        FidViewer  fidViewer                =  FidViewer.getInstance();
        Metabolite metabolite               =  new Metabolite();
        ArrayList<Metabolite> metabolites   =  getMetabolitesAndResonances().getResonances();
        int index                           =  metabolites.size()+1;
        
        String ind                          = formatter.format(index);
        metabolite.setLoadedFromFile(false);
        metabolite.setSiteName(Metabolite.RES_SITE_NAME);
        metabolite.setAbbreviation(Metabolite.RES_SITE_NAME + index);
        metabolite.setResonanceModel(RESONANCE_MODEL.CORRELATED);
        metabolite.setDegeneracyName("(Singlet)");
        metabolite.setAmplitudeRatios("1");
               
        
        
        if (FidViewer.getInstance().isShowing() == false){
                 AllViewers.showFidViewer();
        }
        
        
        if (fidViewer.getDataType() != FID_DATA_TYPE.SPECTRUM_REAL){
            fidViewer.setDataType(FID_DATA_TYPE.SPECTRUM_REAL);
        }
 
        
        
        if (    fidViewer.getChartPanel().getCursorA() == null ||
                fidViewer.getChartPanel().getCursorB() == null  ) 
        {
       
               javax.swing.JOptionPane.showMessageDialog(null, 
                "To add  a resonance \n" +
                "both cursors must be shown in the graph.",
                "alert", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
        }
        
        
        
        // make sure plot won't get updated multiple times
        fidViewer.setUpdatePlot(false);
        
        double lowF                 =   fidViewer.getChartPanel().getLowerBoundFromCursorBox();
        double highF                =   fidViewer.getChartPanel().getUpperBoundFromCursorBox();
        
        // add frequency parameter
        ParameterPrior prior        =  new ParameterPrior();
        
        prior.name                  =   JAllPriors.FREQ_PARAM_NAME+"_"+Metabolite.RES_SITE_NAME+ ind;
        prior.order                 =   ORDER_TYPE.LowHigh;
        prior.low                   =   lowF;
        prior.high                  =   highF;
        prior.mean                  =   (prior.low +prior.high)/2;
        prior.priorType             =   PRIOR_TYPE.GAUSSIAN;
        prior.setParameterType(PARAMETER_TYPE.Frequency);
        prior.sdev                  =   (prior.high - prior.low)/6;
        prior.isOrderEditable       =   false;
        prior.isPriorTypeEditable   =   false;
       
        metabolite.setFrequency(prior);
        
       
        // add rate parameter
        prior                       =  new ParameterPrior();
        float at                    =  FidViewer.getInstance().getProcpar().getAt(); 
       
        prior.name                  =   JAllPriors.RATE_PARAM_NAME+"_"+Metabolite.RES_SITE_NAME+ ind;
        prior.mean                  =   1/at;
        prior.low                   =   0;
        prior.high                  =   20*3/at;
        prior.priorType             =   PRIOR_TYPE.POSITIVE;
        prior.setParameterType(PARAMETER_TYPE.NonLinear);
        prior.order                 =   ORDER_TYPE.NotOrdered;
        prior.sdev                  =   (prior.high - prior.low)/6;
        prior.isOrderEditable       =   false;
        
        metabolite.setRate(prior);
        
        
       
        MetabolitesAndResonances metabolitesAndResonances = getMetabolitesAndResonances();
        metabolitesAndResonances.getResonances().add(metabolite);
        Vector <ParameterPrior> ps =  metabolitesAndResonances.getAllPriors();
        priors.setParams(metabolitesAndResonances.getAllPriors()); 
        
        
        // restore default value for plot updating flag
        fidViewer.setUpdatePlot(true);
        fidViewer.updatePlot();
    }
    public  void removeParameter () {
        Collection<Metabolite> displayedMetabs  = getMetabolitesBetweenCursors();
        if (displayedMetabs.isEmpty() ){return;}

        MetabolitesAndResonances metabolitesAndResonances = getMetabolitesAndResonances();
       
        // metabolitesAndResonances.getMetabolitesInfo().removeMetabolites(displayedMetabs);
        metabolitesAndResonances.getResonancesInfo().removeMetabolites(displayedMetabs);

        Vector <ParameterPrior> ps =  metabolitesAndResonances.getAllPriors();

        JAllPriors priors                   =  JAllPriors.getInstance();
      
        priors.setParams(ps);

        FidViewer  fidViewer                =  FidViewer.getInstance();




        // make sure plot won't get updated multiple times
        fidViewer.setUpdatePlot(false);

        // restore default value for plot updating flag
        fidViewer.setUpdatePlot(true);
        fidViewer.updatePlot();
    }



    public static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("Bayes Metabolite");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });
        frame.add(new BayesMetabolite());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
//        FidViewer.getInstance().updatePlot();
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
    private javax.swing.JButton addFreqParam;
    private javax.swing.JPanel adjustFreq_pane;
    private javax.swing.JPanel adjustFreq_pane1;
    private javax.swing.JPanel graph_pane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JButton loadSystemMetabolite_btn;
    private javax.swing.JButton loadSystemResonance_btn;
    private javax.swing.JButton loadUserMetabolite_btn;
    private javax.swing.JButton loadUserResonance_btn;
    private javax.swing.JButton removeResButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton save_btn;
    private javax.swing.JPanel setAndResetPane;
    private javax.swing.JButton shiftFreqLeft_btn;
    private javax.swing.JButton shiftFreqRight_btn;
    private javax.swing.JPanel tool_pane;
    // End of variables declaration//GEN-END:variables
    public static final NumberFormat formatter      = new DecimalFormat("000");
  
    public boolean isIsotopomerTable(){
        MetabolitesAndResonances m  =   getMetabolitesAndResonances();
        
        return m.getMetabolitesInfo().isLoaded();
    }
    public boolean isResonanceTable(){
        MetabolitesAndResonances m  =   getMetabolitesAndResonances();
        return m.getResonancesInfo().isLoaded();
    }
    
    public MetabolitesAndResonances getMetabolitesAndResonances () {
        return  MetabolitesAndResonances.getMetabolitesAndResonances();
    }
   
    public void setMetabolitesAndResonances ( MetabolitesAndResonances metabolitesAndResonances ) {
        MetabolitesAndResonances.setMetabolitesAndResonances(metabolitesAndResonances);
    }
}
