/*
 * BayesAnalyze.java
 *
 * Created on April 28, 2008, 10:29 AM
 */

package applications.bayesAnalyze;
import bayes.BayesView;
import java.awt.Component;
import bayes.Reset;
import run.Run.RUN_STATUS;
import run.JRun;
import fid.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import bayes.ParameterPrior;
import bayes.PackageManager;
import bayes.DirectoryManager;
import bayes.JobDirections;
import utilities.DisplayText;
import bayes.BayesManager;
import utilities.IO;
import utilities.LoadPackage;
import utilities.PositiveIntegerInputVerifier;
import fid.FidViewer;
import fid.FidModelViewer;
import fid.FidModelNumbers;
import interfacebeans.*;
import applications.model.FidModel;
import static bayes.Enums.*;

/**
 *
 * @author  apple
 */
public class BayesAnalyze   extends     javax.swing.JPanel                         
                            implements  FidModel,java.beans.PropertyChangeListener
                                        
{
    public BayesAnalyze () {
        LoadPackage.loadPackage(this);
        PackageManager.setCurrentApplication(this);
        
        boolean isDeserialized = bayes.Serialize.deserializeCurrenExperiment();

      
        initComponents();
        
        
        AllViewers.removePriorsViewer();
        BayesManager.pcs.addPropertyChangeListener(this);
        if(isDeserialized){JRun.fireJobIDChange();}
       
        
        updateResonancesMessage ();
        if(isDeserialized == false) { 
            AllViewers.showInstructions();}
        else{
            AllViewers.showFidViewer();
        }
    }
        
    
    public void              setPackageParameters(ObjectInputStream serializationFile){}
    public void              savePackageParameters(ObjectOutputStream serializationFile){}
    public boolean           isReadyToRun() {
       FidViewer viewer             =    FidViewer.getInstance();
        // make sure data is loaded
        if (viewer.isLoaded() == false) {
            DisplayText.popupMessage("You must load data before you run the program");
            return false;
        }

        // makew sure Lb is not set to zero
        float lb                =    viewer.getFidReader().getUserLb();
        if (lb == 0){
                DisplayText.popupMessage("Please, set non-zero value for LB.\n" +
                        "Exit run.");
                 return false;
        }

    
        boolean bl;
       // write the parameter file
       
       File dir = DirectoryManager.getBayesAnalyzeDir();
       if (dir.exists() == false) {dir.mkdirs();}
       
          bl  =  BayesAnalyzeParamsWriter.writeParamsFile(this,dir );
           if (bl == false){
                DisplayText.popupMessage("Failed to write Bayes.params file.");
                 return false;
           }
      
       //  write "job.directions" file

         bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT, BayesManager.bAYES_PARAMS,
                                                    getProgramName(), dir);
         if(bl == false){
             DisplayText.popupMessage ("Failed to write job.directions file.");
             return false;
         }
        return true;
    }
    public String            getProgramName() { return "bayes_analyze";}
    public String            getExtendedProgramName() { return "Bayes Analyze" ;}
    public int               getNumberOfAbscissa() {  return 1;}
    public int               getNumberOfDataColumns() { return 1;}
    public int               getTotalNumberOfColumns(){return 2;}
    public int               getNumberOfPriors() {  return  0;}
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR) { return new StringBuilder();}
    public List <ParameterPrior>  getPriors() {
        return new Vector <ParameterPrior>();
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){
        return PACKAGE_INTSRUCTIONS.BAYES_ANALYZE.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              reset(){
        reset(true);
    }
    
    public void              clearPreviousRun(){
         setIgnoreEvents(true);
          try{
                
                // this will clear ascii results
                Reset.clearFidResutls();

                // reset Jrun
                jRun.reset();

                // delete bayes analyze from previous runs
                cleanBayesAnalyzeDir(true, true);

          }
          catch (Exception e){e.printStackTrace();}
          finally{
              setIgnoreEvents(false)  ;

          }
    }
    public void              setDefaults(){
            FidViewer fviewer           = FidViewer.getInstance();
            updateResonancesMessage ();
            setParamsReader(new BayesParamsReader());
            setMaxNewRes(defaultMaxNewRes);
            setShimmingOrder(SHIMMING_ORDER.NONE);



            boolean firstpoint  =   true;
            getConstantModels ().FirstPoint.setSelected( firstpoint);
            getFirstPoint_jrb ().setSelected( firstpoint);
            
            boolean imagConstant  =   false;
            getConstantModels ().ImaginaryConstant.setSelected(imagConstant);
            getImagOffset_jrb ().setSelected(imagConstant );

            boolean realConstant  =   false;
            getConstantModels ().RealConstant.setSelected(realConstant);
            getRealOffset_jrb ().setSelected(realConstant);


            getPhaseComboBox ().setSelectedItem(RESONANCE_MODEL.CORRELATED);

            markResoances.resetToDefaults();
            jRemoveResonances.resetToDefaults();
            fromToByFId.resetToDefaults();

            setSignalAndNoise.resetToDefaults(fviewer );

    }
    public void              setActive ( boolean isActive) {
            phaseLabel.setEnabled(isActive);
            getFirstPoint_jrb().setEnabled(isActive);
            getImagOffset_jrb().setEnabled(isActive);
            maxNewRes_ft.setEnabled(isActive);
            maxNewRes_lbl.setEnabled(isActive);
            getRealOffset_jrb().setEnabled(isActive);
            getShimmingCB().setEnabled(isActive);
            phaseLabel.setEnabled(isActive);
            getSaveExp_button().setEnabled(isActive);
            getPhaseComboBox ().setEnabled(isActive);
            shimmingLabel.setEnabled(isActive);
            resetButton.setEnabled(isActive);

            // custom widgets
            jRun.setEnabled(isActive);
            fromToByFId.setActive( isActive);
            markResoances.setActive( isActive);
            jRemoveResonances.setActive(isActive);
            setSignalAndNoise.setActive(isActive);
            jserver.setActive(isActive);

             AllViewers.getInstance().setActive(isActive);
    }
    public void              propertyChange(java.beans.PropertyChangeEvent evt){
        if(this.isIgnoreEvents() == true ){return;}
        
        FidViewer fidViewer         = FidViewer.getInstance();
        boolean isFidLoaded         = fidViewer.isLoaded();
       
        if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_END)  ){
         if (isFidLoaded == false) {return;}
          
         updateParametersAfterJobWasRetreivedFromServer();

          FidModelViewer.getInstance().unloadData();
          
          setActive(true);
          AllViewers.showFidViewer();
          if (fidViewer.getDataType() != FID_DATA_TYPE.SPECTRUM_REAL){
               fidViewer.setDataType(FID_DATA_TYPE.SPECTRUM_REAL);
          }
       }
        
        else if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_START)  ){
          setActive(false);
       }
       else if(evt.getPropertyName().equals(BayesManager.JRUN_JOB_CANCELED)  ){
          setActive(true);
          markResoances.resetToDefaults();
       }
      
       else if(evt.getPropertyName().equals(BayesManager.JRUN_MODEL_IS_RUN)  ){
          markResoances.resetToDefaults();
       }
       else if(evt.getPropertyName().equals(BayesManager.FID_LOADED_BY_USER  )  ){
          updateParametersOnFidLoad();
        }
       
       else if(evt.getPropertyName().equals(BayesManager.FID_LOADED_BY_JAVA)  ){
          updateParametersOnFidLoad();
       }
       else if(evt.getPropertyName().equals( BayesManager.FID_UNLOADED)  ){
            updateResonancesMessage ();
       }
      else if(evt.getPropertyName().equals(BayesManager.FID_UNITS_ARE_CHANGED)  ){
           clearPreviousRun();
      }       


   
        else if(evt.getPropertyName().equals(BayesManager.RESONANCE_MARKED)  ){
            Resonance res = (Resonance) evt.getNewValue();
            res.setResonanceModel(getResModel());
            
            ArrayList <Resonance> resonances =  Resonance.getResonanceList();
            resonances.add(res);
            Resonance.sort( resonances);
            
            updateResonancesMessage ();
            if (this.isIgnoreEvents() == false){
                clearPreviousRun();
            }
            
              fidViewer.updatePlot();
        }
      
       
   }
    public void              destroy(){};
    public void              reset(boolean setDefaults){
          setIgnoreEvents(true);
          try{
              if(setDefaults){
                    setDefaults();
              }
          
             clearPreviousRun();


            FidViewer.getInstance().resetModelAndResonances();
            File modelDir           = DirectoryManager.getFidModelDir();
            File bayesAnalizedir    = DirectoryManager.getBayesAnalyzeDir();

            IO.emptyDirectory(bayesAnalizedir);
            IO.deleteDirectory(modelDir);

            FidModelViewer.getInstance().unloadData();
            updateResonancesMessage ();


             FidViewer.getInstance().updatePlot();

          }
          catch (Exception e){e.printStackTrace();}
          finally{
              setIgnoreEvents(false)  ;

               // show defualt viewer
                AllViewers.getInstance().showDefaultViewer();
          }
           
    }
    
    public static void cleanBayesAnalyzeDir(boolean keepProbFiles, boolean keepNoiseFile ){
        File baDir                          =   DirectoryManager.getBayesAnalyzeDir();
        FileFilter filter                   =   new  ALLBayesAnalyzeExceptProbFilesFilter();
        File noiseFile                      =   DirectoryManager.getBayesNoiseFile(baDir);
        File [] files                       =   null;
      
        if (keepProbFiles){
            files   = baDir.listFiles(filter);
        }
        else {
            files   = baDir.listFiles();
        }
        for (File file : files) {
            if(keepNoiseFile && file.getAbsolutePath().equals(noiseFile.getPath())){
                continue;
            }
            else{
                 file.delete();
            }
           
        }

    }
 
    public void     updateParametersAfterJobWasRetreivedFromServer(){
         FidViewer.getInstance().updatePlotAndBayesAnalyzeParameters();
         updateResonancesMessage ();
  
    } 
    public void     updateParametersOnFidLoad(){
         FidViewer fviewer          =   FidViewer.getInstance();
         boolean isBayesAnalyze     =   fviewer .getParamsReader().isLoadedSuccessfully();
         setIgnoreEvents(true);

         try{
            setDefaults();
        
             if(isBayesAnalyze){
                    setFromBayesModel();
                    JRun.setStatus(RUN_STATUS.RUN);
             }

             updateResonancesMessage ();
         
         }catch(Exception e){}
         finally{
            setIgnoreEvents(false);
         }

         
    }
    
  
    public void     setFromBayesModel (){
             FidViewer fv           =   FidViewer.getInstance();
             BayesParamsReader br   =   fv.getParamsReader();
             FidModelNumbers fmn    =   FidViewer.getModelNumbers();
             if ( fmn .isLoaded()  == false){
                    fmn.setTo(fv.getNumberOfTraces());
              }

             
             fromToByFId.setFrom(fmn.getFrom());
             fromToByFId.setTo(fmn.getTo());
             fromToByFId.setBy(fmn.getBy());

             setSignalAndNoise.setIgnoreEvents(true);
             setSignalAndNoise.setSignal(br.getTotalPoints());
             setSignalAndNoise.setNoise(br.getNoiseStart());
             setSignalAndNoise.setIgnoreEvents(false);

             getShimmingCB().setSelectedItem(SHIMMING_ORDER.getShimmingNamebyValue(br.getShimOrder()));
             maxNewRes_ft.setValue(br. getMaxFreqs());


             getFirstPoint_jrb().setSelected(br.isFirstPoint());
             getRealOffset_jrb().setSelected(br.isRealConstant());
             getImagOffset_jrb().setSelected(br.isImaginaryConstant());

             setResModel(br.getDefaultModel());
             getPhaseComboBox ().setSelectedItem(getResModel());
           
         
    }

 
    public int      getNumberOfGlobalModelParameters(){
        int i            = 0;
        i               += 1; // center phase
        i               += 1; // time delay
        
        int shimOrder           = getShimmingOrder ();
        BayesParamsReader br    = this.getParamsReader();
        
        if (  shimOrder > 1 && br.isLoadedSuccessfully() ){
           i    += 1; // shim delta delay
           i    += shimOrder; // shim delta delay
        }
     
       return i;
    }  
    public void     updateResonancesMessage () {
         String message              =   RES_MESSAGE+ getResonances().size();
         BayesView.setMenuBarMessage(message);
         //setResonancesMessage (message  );
    }

  
    
    public static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame(" Bayes Analyze ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });
        frame.add(new BayesAnalyze());

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
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        phase_bg = new javax.swing.ButtonGroup();
        model_bg = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        graph_pane = AllViewers.getInstance ();
        jScrollPane1 = new javax.swing.JScrollPane();
        runPanel = new javax.swing.JPanel();
        widgets = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        saveExp_button = new javax.swing.JButton();
        jserver = interfacebeans.JServer.getInstance();
        fromToByFId =  ( fromToByFId != null) ?
        fromToByFId:
        new applications.bayesAnalyze.SetFromToTraces();
        setSignalAndNoise = (setSignalAndNoise == null)?new SetSignalFilterNoise():setSignalAndNoise;
        markResoances = new applications.bayesAnalyze.JMarkResonance();
        model_pane1 = new javax.swing.JPanel();
        firstPoint_jrb = new javax.swing.JRadioButton();
        realOffset_jrb = new javax.swing.JRadioButton();
        imagOffset_jrb = new javax.swing.JRadioButton();
        jRun = new run.JRun();
        jRemoveResonances = new applications.bayesAnalyze.JRemoveResonances();
        shimming_pane = new javax.swing.JPanel();
        shimmingLabel = new javax.swing.JLabel();
        shimmingCB = new javax.swing.JComboBox( SHIMMING_ORDER.getNames());
        maxNewRes_lbl = new javax.swing.JLabel();
        maxNewRes_ft = new javax.swing.JFormattedTextField();
        phaseLabel = new javax.swing.JLabel();
        phaseComboBox = new javax.swing.JComboBox(  RESONANCE_MODEL.values());

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(130);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        graph_pane.setMinimumSize(new java.awt.Dimension(100, 100));
        graph_pane.setLayout(new javax.swing.BoxLayout(graph_pane, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setBottomComponent(graph_pane);

        jScrollPane1.setBorder(null);

        runPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Save/Reset"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        resetButton.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        resetButton.setText("Reset");
        resetButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nReset current experiment:<br>\nThe results from previous run will be deleted<br>\nand all parametes will be set to their default values.<br>\n\n</p><html>\n\n\n"); // NOI18N
        resetButton.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(resetButton, gridBagConstraints);

        saveExp_button.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        saveExp_button.setText("Save");
        saveExp_button.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nCopy the Bayes Analyze files  <br>\nto the original Fid directory.\n\n</html>\n\n\n\n"); // NOI18N
        saveExp_button.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(saveExp_button, gridBagConstraints);

        fromToByFId.setBorder(javax.swing.BorderFactory.createTitledBorder("Set Traces"));
        fromToByFId.addPropertyChangeListener(formListener);

        setSignalAndNoise.setBorder(javax.swing.BorderFactory.createTitledBorder("Set Signal/Noise"));
        setSignalAndNoise.addPropertyChangeListener(formListener);

        markResoances.setBorder(javax.swing.BorderFactory.createTitledBorder("Mark Resonance"));

        model_pane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Constant Models"));
        model_pane1.setLayout(new java.awt.GridBagLayout());

        firstPoint_jrb.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        firstPoint_jrb.setSelected(constantModels.FirstPoint.isSelected());
        firstPoint_jrb.setText("First  Point");
        firstPoint_jrb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nEstimate the value of the first<br>\ncomplex data value.\n</html>\n\n\n"); // NOI18N
        firstPoint_jrb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        firstPoint_jrb.setIconTextGap(10);
        firstPoint_jrb.setPreferredSize(new java.awt.Dimension(70, 20));
        firstPoint_jrb.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbp.gif"))); // NOI18N
        firstPoint_jrb.setRolloverEnabled(true);
        firstPoint_jrb.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        firstPoint_jrb.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        firstPoint_jrb.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N
        firstPoint_jrb.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 1, 5);
        model_pane1.add(firstPoint_jrb, gridBagConstraints);

        realOffset_jrb.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        realOffset_jrb.setSelected(constantModels.RealConstant.isSelected());
        realOffset_jrb.setText("Real Offset");
        realOffset_jrb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nEstimate the value of an offset <br>\nin the real channel.\n</html>\n\n"); // NOI18N
        realOffset_jrb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        realOffset_jrb.setIconTextGap(10);
        realOffset_jrb.setPreferredSize(new java.awt.Dimension(70, 20));
        realOffset_jrb.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbp.gif"))); // NOI18N
        realOffset_jrb.setRolloverEnabled(true);
        realOffset_jrb.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        realOffset_jrb.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        realOffset_jrb.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N
        realOffset_jrb.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 5);
        model_pane1.add(realOffset_jrb, gridBagConstraints);

        imagOffset_jrb.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        imagOffset_jrb.setSelected(constantModels.ImaginaryConstant.isSelected());
        imagOffset_jrb.setText("Imag Offset");
        imagOffset_jrb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nEstimate the value of an offset <br>\nin the  imaginary channel.\n</html>\n\n\n\n                   "); // NOI18N
        imagOffset_jrb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rb.gif"))); // NOI18N
        imagOffset_jrb.setIconTextGap(10);
        imagOffset_jrb.setPreferredSize(new java.awt.Dimension(70, 20));
        imagOffset_jrb.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbp.gif"))); // NOI18N
        imagOffset_jrb.setRolloverEnabled(true);
        imagOffset_jrb.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbr.gif"))); // NOI18N
        imagOffset_jrb.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbrs.gif"))); // NOI18N
        imagOffset_jrb.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/buttons/rbs.gif"))); // NOI18N
        imagOffset_jrb.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 2, 5);
        model_pane1.add(imagOffset_jrb, gridBagConstraints);

        jRemoveResonances.setBorder(javax.swing.BorderFactory.createTitledBorder("Remove Resonances"));
        jRemoveResonances.addPropertyChangeListener(formListener);

        shimming_pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));
        shimming_pane.setLayout(new java.awt.GridBagLayout());

        shimmingLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        shimmingLabel.setText("Shim. Order");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        shimming_pane.add(shimmingLabel, gridBagConstraints);

        shimmingCB.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        shimmingCB.setSelectedItem(shimmingOrder.getName());
        shimmingCB.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSet shimming order.\n</html>\n\n\n"); // NOI18N
        shimmingCB.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        shimming_pane.add(shimmingCB, gridBagConstraints);

        maxNewRes_lbl.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        maxNewRes_lbl.setText("Max New Res:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        shimming_pane.add(maxNewRes_lbl, gridBagConstraints);

        maxNewRes_ft.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        maxNewRes_ft.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxNewRes_ft.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nThe maximum number of new resonances.\n</html>\n\n\n"); // NOI18N
        maxNewRes_ft.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        maxNewRes_ft.setInputVerifier(new PositiveIntegerInputVerifier());
        maxNewRes_ft.setValue(maxNewRes);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        shimming_pane.add(maxNewRes_ft, gridBagConstraints);

        phaseLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        phaseLabel.setText("Phase");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        shimming_pane.add(phaseLabel, gridBagConstraints);

        phaseComboBox.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        phaseComboBox.setSelectedItem(shimmingOrder.getName());
        phaseComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSet resonance phase model<br>\n\"CP\" - correlated phase<br>\n\"UP\" - uncorrelated phase.\t\n\n\n\n\n</html>\n\n"); // NOI18N
        phaseComboBox.setRenderer(new ModelCellRenderer());
        phaseComboBox.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        shimming_pane.add(phaseComboBox, gridBagConstraints);

        org.jdesktop.layout.GroupLayout widgetsLayout = new org.jdesktop.layout.GroupLayout(widgets);
        widgets.setLayout(widgetsLayout);
        widgetsLayout.setHorizontalGroup(
            widgetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(widgetsLayout.createSequentialGroup()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(model_pane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(shimming_pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fromToByFId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(setSignalAndNoise, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(markResoances, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRemoveResonances, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
        );
        widgetsLayout.setVerticalGroup(
            widgetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(widgetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jRemoveResonances, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, markResoances, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(widgetsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, shimming_pane, 0, 0, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, model_pane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(setSignalAndNoise, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(fromToByFId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jRun, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
            .add(jserver, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
        );

        widgetsLayout.linkSize(new java.awt.Component[] {fromToByFId, jPanel1, jRemoveResonances, jRun, jserver, markResoances, model_pane1, setSignalAndNoise, shimming_pane}, org.jdesktop.layout.GroupLayout.VERTICAL);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        runPanel.add(widgets, gridBagConstraints);

        jScrollPane1.setViewportView(runPanel);

        jSplitPane1.setTopComponent(jScrollPane1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == resetButton) {
                BayesAnalyze.this.resetButtonActionPerformed(evt);
            }
            else if (evt.getSource() == saveExp_button) {
                BayesAnalyze.this.saveExp_buttonActionPerformed(evt);
            }
            else if (evt.getSource() == firstPoint_jrb) {
                BayesAnalyze.this.firstPoint_jrbActionPerformed(evt);
            }
            else if (evt.getSource() == realOffset_jrb) {
                BayesAnalyze.this.realOffset_jrbActionPerformed(evt);
            }
            else if (evt.getSource() == imagOffset_jrb) {
                BayesAnalyze.this.imagOffset_jrbActionPerformed(evt);
            }
            else if (evt.getSource() == shimmingCB) {
                BayesAnalyze.this.shimmingCBActionPerformed(evt);
            }
            else if (evt.getSource() == phaseComboBox) {
                BayesAnalyze.this.phaseComboBoxActionPerformed(evt);
            }
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == fromToByFId) {
                BayesAnalyze.this.fromToByFIdPropertyChange(evt);
            }
            else if (evt.getSource() == setSignalAndNoise) {
                BayesAnalyze.this.setSignalAndNoisePropertyChange(evt);
            }
            else if (evt.getSource() == jRemoveResonances) {
                BayesAnalyze.this.jRemoveResonancesPropertyChange(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void shimmingCBActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shimmingCBActionPerformed

        int i             =  getShimmingCB().getSelectedIndex ();
        shimmingOrder     =  SHIMMING_ORDER.values ()[i];

        if (this.isIgnoreEvents () == false){
            clearPreviousRun();
        }
}//GEN-LAST:event_shimmingCBActionPerformed
    private void fromToByFIdPropertyChange (java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fromToByFIdPropertyChange
        if (evt.getPropertyName ().equalsIgnoreCase ("value")){
            if (this.isIgnoreEvents () == false){
                clearPreviousRun();
            }
        }
}//GEN-LAST:event_fromToByFIdPropertyChange
    private void imagOffset_jrbActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagOffset_jrbActionPerformed
       getConstantModels ().ImaginaryConstant.setSelected( getImagOffset_jrb().isSelected());
       if (this.isIgnoreEvents () == false){
           clearPreviousRun();
       }
}//GEN-LAST:event_imagOffset_jrbActionPerformed
    private void realOffset_jrbActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realOffset_jrbActionPerformed
        getConstantModels ().RealConstant.setSelected(getRealOffset_jrb().isSelected());
        if (this.isIgnoreEvents () == false){
            clearPreviousRun();
       }
}//GEN-LAST:event_realOffset_jrbActionPerformed
    private void firstPoint_jrbActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstPoint_jrbActionPerformed
       getConstantModels ().FirstPoint.setSelected(getFirstPoint_jrb().isSelected());
       if (this.isIgnoreEvents () == false){
            clearPreviousRun();
       }
}//GEN-LAST:event_firstPoint_jrbActionPerformed
    private void saveExp_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveExp_buttonActionPerformed
        File asciiDir       =   DirectoryManager.getBayesAnalyzeDir ();
        FileFilter filter   =   new fid.FileFilters. ModelsAndParams ();

        Object[] options = {"Yes, save", "Don't save"};

        int n = JOptionPane.showOptionDialog (SwingUtilities.getWindowAncestor (this),
                "Saving experiment will overwrite all result files \n" +
                "in the original fid directory. \n" +
                "Do you want to proceed?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title
        if (n != JOptionPane.OK_OPTION) {return;}

        // overwrite fist line in bayes.params.* and bayes.model.* files
        File[] children     =  asciiDir.listFiles (  filter);
        for (File file : children) {
            BayesAnalyzeParamsWriter.overwriteBayesFileHeader (file);
        }

        // copy file to original and current fidDirectory
        filter                  =   new fid.FileFilters.FileNameStartsWithBayes ();
        children                =   asciiDir.listFiles (  filter);

        String fidSourcePath    =   FidViewer.getInstance().getProcpar().getFileSource();
        File fidSource          =   new File(fidSourcePath);
        if (fidSource.exists ()){
            File distDir        =  null;
            if (fidSource.isDirectory()){
                distDir         =   fidSource;
            }
            else{
                 distDir         =   fidSource.getParentFile();
            }
            for (File file : children) {
                IO.copyFile (file, new File ( distDir , file.getName ()));
            }
        
        // overwrie units if necessary and copy fid procar file
        UNITS currentUnits      =   FidViewer.getInstance().getUnits();    
        File procpar            =   DirectoryManager.getProcparFile();
        if (currentUnits!= null && procpar != null){
            File dstProcparFile     =   DirectoryManager.getProcparFile(distDir);
            Procpar.overwriteUnits(procpar, dstProcparFile, currentUnits);
        }
    
        
        }
    }//GEN-LAST:event_saveExp_buttonActionPerformed
    private void phaseComboBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phaseComboBoxActionPerformed
        RESONANCE_MODEL resMod =  (RESONANCE_MODEL )getPhaseComboBox ().getSelectedItem();
        setResModel( resMod );
        if (this.isIgnoreEvents () == false){
             clearPreviousRun();
       }
}//GEN-LAST:event_phaseComboBoxActionPerformed
    private void setSignalAndNoisePropertyChange (java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_setSignalAndNoisePropertyChange
        if (evt.getPropertyName().equals(SetSignalFilterNoise.SIGNAL_NOISE_PROPERTY_CHANGE)){
            if (this.isIgnoreEvents () == false){
            //clearPreviousRun();
            reset(false);
            
       }
        }
    }//GEN-LAST:event_setSignalAndNoisePropertyChange
    private void jRemoveResonancesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jRemoveResonancesPropertyChange
            if(this.isIgnoreEvents() == true ){return;}
            if(evt.getPropertyName().equals(BayesManager.ADD_OR_REOMOVE_RESONANCES)  ){

                updateResonancesMessage ();
                if (isIgnoreEvents() == false){
                    clearPreviousRun();
                }
                FidViewer.getInstance().updatePlot();
            }
    }//GEN-LAST:event_jRemoveResonancesPropertyChange
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed
    public static boolean isBayesAnalyzeRun(){
        boolean ran         =   true;
        boolean notRan      =   false;

        File bayesAnalDir   =   DirectoryManager.getBayesAnalyzeDir();

        if(bayesAnalDir == null || bayesAnalDir.exists() == false ) {return notRan;}


        File [] files       = bayesAnalDir.listFiles(new  ALLBayesAnalyzeExceptProbFilesFilter());
        if(files == null || files.length <1 ){return notRan;}

        return ran;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton firstPoint_jrb;
    private applications.bayesAnalyze.SetFromToTraces fromToByFId;
    private javax.swing.JPanel graph_pane;
    private javax.swing.JRadioButton imagOffset_jrb;
    private javax.swing.JPanel jPanel1;
    private applications.bayesAnalyze.JRemoveResonances jRemoveResonances;
    private run.JRun jRun;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private applications.bayesAnalyze.JMarkResonance markResoances;
    private javax.swing.JFormattedTextField maxNewRes_ft;
    private javax.swing.JLabel maxNewRes_lbl;
    private javax.swing.ButtonGroup model_bg;
    private javax.swing.JPanel model_pane1;
    private javax.swing.JComboBox phaseComboBox;
    private javax.swing.JLabel phaseLabel;
    private javax.swing.ButtonGroup phase_bg;
    private javax.swing.JRadioButton realOffset_jrb;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel runPanel;
    private javax.swing.JButton saveExp_button;
    private applications.bayesAnalyze.SetSignalFilterNoise setSignalAndNoise;
    private javax.swing.JComboBox shimmingCB;
    private javax.swing.JLabel shimmingLabel;
    private javax.swing.JPanel shimming_pane;
    private javax.swing.JPanel widgets;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JComboBox getPhaseComboBox (){
         return  phaseComboBox;
     }
    public javax.swing.JRadioButton getFirstPoint_jrb () {
        return firstPoint_jrb;
    }
    public javax.swing.JRadioButton getImagOffset_jrb () {
        return imagOffset_jrb;
    }
    public javax.swing.JRadioButton getRealOffset_jrb () {
        return realOffset_jrb;
    }
    public javax.swing.JButton getSaveExp_button () {
        return saveExp_button;
    }
    public javax.swing.JComboBox getShimmingCB () {
        return shimmingCB;
    }


    public final static int defaultMaxNewRes        =   10;
    private static final long serialVersionUID      =   6170037639785281128L;
    
    public SHIMMING_ORDER shimmingOrder             =   SHIMMING_ORDER.NONE;
    private CONSTANT_MODELS constantModels ;
    private RESONANCE_MODEL resModel                 =   RESONANCE_MODEL.CORRELATED;
    public final static String STATUS_FILE_PREFIX   =  "bayes.status.";
    private BayesParamsReader paramsReader          =   new BayesParamsReader ();
    private boolean ignoreEvents                    =   false;
    public final static String RES_MESSAGE          =   "Number of resonances = ";
    
    private int maxNewRes                           =   defaultMaxNewRes ;
   
    /*******************************************/
// <editor-fold defaultstate="collapsed" desc=" GETTERS ">
    public boolean isShimmingOn () {
        if ( shimmingOrder == SHIMMING_ORDER.NONE ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isNoise () {
        return setSignalAndNoise.isNoise();
    }

    public boolean isFirstPoint () {
        return getFirstPoint_jrb().isSelected();
    }

    public boolean isRealOffset () {
        return getRealOffset_jrb().isSelected();
    }

    public boolean isImagOffset () {
        return getImagOffset_jrb().isSelected();
    }

    public boolean isCorrelated () {
        return (getResModel() == RESONANCE_MODEL.CORRELATED);
    }

    public boolean isUncorrelated () {
        return (getResModel() == RESONANCE_MODEL.UNCORRELATED);
    }    

    
    public boolean isIgnoreEvents () {
        return ignoreEvents;
    }
    
    
    public static File getParamslFile () {   
        File dir            = DirectoryManager.getBayesAnalyzeDir();
        File file           = new File (dir, BayesManager.bAYES_PARAMS  );
        return  file ;
    }

    public String getDefaultModel () {
        return getResModel().getShortNameWithParenthesis();
    }

    public int getFirstFid () {
        return fromToByFId.getFrom();
    }

    public int getShimmingOrder () {
        return shimmingOrder.getValue();
    }

    public int getLastFid () {
        return fromToByFId.getTo();
    }

    public int getByFid () {
        return fromToByFId.getBy();
    }

    public int getSignal () {
        return setSignalAndNoise.getSignal();
    }

    public int getNoise () {
        return setSignalAndNoise.getNoise();
    }

    public int getTotalPoints () {
        return FidViewer.getInstance().getProcpar().getNp();
    }

    public int getTotalComplexPoints () {
        return getTotalPoints() / 2;
    }

    public float getTotalSamplingTime () {
        float at = FidViewer.getInstance().getProcpar().getAt();
        return at;
    }    

    public float getSamplingTime () {
        float at = FidViewer.getInstance().getProcpar().getAt();
        float signal = getSignal(); // declare signal float to prevent integer devision

        float total = getTotalPoints();
        float part = signal / total;
        float st = part * at;        
        return st;
    }    

   public CONSTANT_MODELS getConstantModels () {
        return constantModels;
    }



    public int getTotalModel () {
        int n = Resonance.getResonanceList().size();
        if ( getFirstPoint_jrb().isSelected() ) {
            n += 1;
        }
        if ( getRealOffset_jrb().isSelected() ) {
            n += 1;
        }
        if ( getImagOffset_jrb().isSelected() ) {
            n += 1;
        }
        return n;
    }    

    public int getMaximumResonances () {
        return ((Number) maxNewRes_ft.getValue()).intValue();
    }

    public ArrayList<String> getConstantsModel () {        
        ArrayList<String> list = new ArrayList<String>();
        if ( isFirstPoint() ) {
            list.add("First Point Problem");
        }
        if ( isRealOffset() ) {
            list.add("Real Constant");
        }
        if ( isImagOffset() ) {
            list.add("Imaginary Constant");
        }
        
        return list;
    }

    public ArrayList<Resonance> getResonances () {
        return Resonance.getResonanceList();
    }

    public int getMaxNewRes () {
        if ( maxNewRes_ft != null ) {
            maxNewRes = ((Number) maxNewRes_ft.getValue()).intValue();
        }
        return maxNewRes;
    }
    
    public BayesParamsReader getParamsReader () {
        return paramsReader;
    }
    
 
 
     

    


    public RESONANCE_MODEL getResModel () {
        return resModel;
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" SETTERS ">
      
   
    public void setMaxNewRes ( int maxNewRes ) {
        this.maxNewRes = maxNewRes;
        if ( maxNewRes_ft != null ) {
            maxNewRes_ft.setValue(this.maxNewRes);
        }
    }
    public void setParamsReader ( BayesParamsReader paramsReader ) {
        this.paramsReader = paramsReader;
    }
    public void setShimmingOrder (SHIMMING_ORDER shimOrder ) {
        shimmingOrder =  shimOrder;
        getShimmingCB().setSelectedItem( shimmingOrder.getName());
    }
    public void setIgnoreEvents ( boolean ignoreEvents ) {
        this.ignoreEvents = ignoreEvents;
    }
    
    public void setResModel ( RESONANCE_MODEL resModel ) {
        this.resModel = resModel;
    }
    public void setConstantModels ( CONSTANT_MODELS constantModels ) {
        this.constantModels = constantModels;
    }

 
 // </editor-fold>



      public static enum BAYES_ANALYZE_TYPE {
        BAYES_PROB_MODEL     (   "Probability model"         ),
        BAYES_PARAMS         (   "Bayes.params"              ),
        CONSOLE_LOG          (   "Console log"               ),
        LOG                  (   "Log"                       ),
        OUTPUT               (   "Output"                       ),
        MODEL                (   "Model"                     ),
        STATUS               (   "Status"                       ),
        SUMMARY1             (   "Summary1 (Best Model)"       ),
        SUMMARY2             (   "Summary2 (Best Summary)"     ),
        SUMMARY3             (   "Summary3 (Best Regions)"     ),
        REGIONS              (   "Regions"     );;

        private final String name;

        public final static String BAYES_SUMMARY1           = "bayes.summary1";
        public final static String BAYES_SUMMARY2           = "bayes.summary2";
        public final static String BAYES_SUMMARY3           = "bayes.summary3";
        public final static String BAYES_PROBABILITY        = "bayes.probabilities";
        public final static String BAYES_CONSOLE_LOG        = "console.log";
        public final static String BAYES_LOG                = "bayes.log";
        public final static String BAYES_STATUS             = "bayes.status";
        public final static String BAYES_OUTPUT             = "bayes.output.";
        public final static String BAYES_MODEL              = "bayes.model";

        BAYES_ANALYZE_TYPE ( String aMenuText  ) {
            this.name           = aMenuText;

        }
        public String getName() {return name;}

        @Override
        public String toString() {return name;}




    }


     public static class BayesSummary1FileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_SUMMARY1);
                    return  bl;
            }
        }
     public static class BayesSummary2FileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_SUMMARY2);
                    return  bl;
            }
        }
     public static class BayesSummary3FileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_SUMMARY3);
                    return  bl;
            }
        }

     public static class BayesModelFileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_MODEL);
                    return  bl;
            }
        }
     public static class BayesProbabilitesFileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_PROBABILITY);
                    return  bl;
            }
        }
     public static class BayesLogFileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_LOG);
                    return  bl;
            }
        }
     public static class BayesOutputFileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_OUTPUT);
                    return  bl;
            }
        }
     public static class BayesStatusFileFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_STATUS);
                    return  bl;
            }
        }
     public static class ALLBayesAnalyzeExceptProbFilesFilter implements FileFilter{

            public boolean accept(File f) {
                    if (f.isDirectory()){  return false;}
                    boolean bl = !f.getName().startsWith(  BAYES_ANALYZE_TYPE.BAYES_PROBABILITY);
                    return  bl;
            }
    }


    class ModelCellRenderer extends DefaultListCellRenderer {

    /* This is the only method defined by ListCellRenderer.  We just
     * reconfigure the Jlabel each time we're called.
     */
        @Override
    public Component getListCellRendererComponent(
        JList list,
	Object value,   // value to display
	int index,      // cell index
	boolean iss,    // is the cell selected
	boolean chf)    // the list and the cell have the focus
    {
        /* The DefaultListCellRenderer class will take care of
         * the JLabels text property, it's foreground and background
         * colors, and so on.
         */
        super.getListCellRendererComponent(list, value, index, iss, chf);

        /* We additionally set the JLabels icon property here.
         */
        RESONANCE_MODEL resmodel = (RESONANCE_MODEL) value;
         setText(resmodel.getShortName());

	return this;
    }
}


}