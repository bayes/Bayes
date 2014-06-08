/*
 * BayesEnterAsciiPreloaded.java
 *
 * Created on August 13, 2008, 12:01 PM
 */

package applications.bayesEnterAsciiPreloaded;
import bayes.Reset;
import bayes.Enums;
import bayes.PackageManager;
import bayes.WriteBayesParams;
import bayes.JobDirections;
import bayes.BayesManager;
import bayes.DirectoryManager;
import run.JRun;
import javax.swing.*;
import java.io.*;
import utilities.*;
import interfacebeans.*;
import java.util.List;
import static load.LoadAsciiModels.*;
import bayes.ParameterPrior;
import bayes.Enums.*;
import static load.gui.JRemoteFileChooser.*;
/**
 *
 * @author  apple
 */
public class BayesEnterAsciiPreloaded extends javax.swing.JPanel 
                                       implements applications.model.AsciiModel, java.beans.PropertyChangeListener
{

    /** Creates new form BayesEnterAsciiPreloaded */
    public BayesEnterAsciiPreloaded(String systemModel) {
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized =bayes.Serialize.deserializeCurrenExperiment();



        boolean isLoaded  =  loadSysModel(systemModel);
        setLoaded(isLoaded );
        if (isLoaded == false){
            DisplayText.popupErrorMessage(
                    "Error while loading this Bayes Enter Ascii Preloaded package.\n" +
                     "This package can not be run. Abort loadig procedures\n");
        }

        
      //  AllViewers.addCodeViewer();
      //  JShowModels.getInstance().setViewOnly(true);
        initComponents ();
        
        BayesManager.pcs.addPropertyChangeListener(this);
        
     
        
        if(isDeseralized){
           // setMessage(message);
            JRun.fireJobIDChange();
        }
        else {
              AllViewers.showInstructions();
        }
       
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

        jSplitPane1 = new javax.swing.JSplitPane();
        setup_pane = new javax.swing.JPanel();
        jRun = new run.JRun();
        jserver = interfacebeans.JServer.getInstance();
        jPanel1 = new javax.swing.JPanel();
        includeOutliersCheckBox = new javax.swing.JCheckBox();
        dummyLabel = new javax.swing.JLabel();
        jResetSave = new interfacebeans.JResetSave();
        graph_panel = AllViewers.getInstance ();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        setup_pane.setName("setup_pane"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        jserver.setName("jserver"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        includeOutliersCheckBox.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        includeOutliersCheckBox.setSelected(isIncludeOutliers());
        includeOutliersCheckBox.setText("Find Outliers"); // NOI18N
        includeOutliersCheckBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n<font color=\"red\" size = \"+1\"><bold> Enable outlier detection.</font></bold><br>  \n(i.e., look for residual<br>\nvalues that are larger than 3 standard deviations <br>\n and remove these outliers from the analysis.)\n</html>\n\n"); // NOI18N
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel1.add(includeOutliersCheckBox, gridBagConstraints);

        dummyLabel.setText(" ");
        dummyLabel.setName("dummyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 6, 2);
        jPanel1.add(dummyLabel, gridBagConstraints);

        jResetSave.setName("jResetSave"); // NOI18N

        org.jdesktop.layout.GroupLayout setup_paneLayout = new org.jdesktop.layout.GroupLayout(setup_pane);
        setup_pane.setLayout(setup_paneLayout);
        setup_paneLayout.setHorizontalGroup(
            setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_paneLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(410, Short.MAX_VALUE))
        );
        setup_paneLayout.setVerticalGroup(
            setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_paneLayout.createSequentialGroup()
                .add(setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setup_paneLayout.linkSize(new java.awt.Component[] {jPanel1, jResetSave, jRun, jserver}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setLeftComponent(setup_pane);

        graph_panel.setMinimumSize(new java.awt.Dimension(300, 400));
        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setPreferredSize(new java.awt.Dimension(300, 400));
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(graph_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == includeOutliersCheckBox) {
                BayesEnterAsciiPreloaded.this.includeOutliersCheckBoxActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

private void includeOutliersCheckBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeOutliersCheckBoxActionPerformed
    includeOutliers  = this.getIncludeOutliersCheckBox ().isSelected ();
    clearPreviousRun();
}//GEN-LAST:event_includeOutliersCheckBoxActionPerformed

    
    
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
    public void              setPackageParameters(ObjectInputStream ois) throws Exception{
         boolean  isOutl                    = ois.readBoolean();
         setIncludeOutliers(isOutl);

          // Special case for preloaded packages
         // priors are not serialized but rather saved to
         // params file in user repository
          savePriors(false);

    }
    public void              savePackageParameters(ObjectOutputStream oos){

     try{
            oos.writeBoolean(isIncludeOutliers());

      } catch (IOException exp){
            DirectoryManager.getSerializationFile().delete();
            exp.printStackTrace();
      }

    }
    public boolean           isReadyToRun(){
      // make sure data has been loaded
        File dir        =   DirectoryManager.getBayesOtherAnalysisDir();
        File[] files    =   ascii.ASCIIDataViewer.getInstance().getFiles();

        if (files == null || files.length == 0) {
            DisplayText.popupErrorMessage("You must load data before you run the program.");
            return false;
        }

   // make sure the asciiModel has been loaded
       if(getAsciiModel().isLoaded() == false){
           DisplayText.popupErrorMessage("Model is not loaded.");
          return false;
       }
         
        
        
   // make sure all data files exist
         for (File file : files) {
            if (!file.exists()) {
                 DisplayText.popupErrorMessage("Data file " + file.getPath() + "doesn't exist.");
               return false;
            }
        }


     // write the parameter file
       boolean bl  =  WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){

          DisplayText.popupErrorMessage("Failed to write Bayes.params file.");
           return false;
       }

       // save the priors
       savePriors(false);

       //  write "job.directions" file
       bl  =  JobDirections.writeFromProperties(JobDirections. BAYES_SUBMIT_ENTER_ASCII);
       if(bl == false){
          DisplayText.popupErrorMessage("Failed to write job.directions file.");
          return false;
        }
        return true;
    }
    public String            getProgramName(){
       return "BayesEnterAscii";
    }
    public String            getExtendedProgramName() { return "Preloaded ASCII model "+   getAsciiModelName() ;}

    public int               getNumberOfAbscissa(){
        if(getAsciiModel().isLoaded()){   return getAsciiModel().getNumberOfAbscissa();}
        else  { return 0;}
    }
    public int               getNumberOfDataColumns(){
         if(getAsciiModel().isLoaded())  {   return getAsciiModel().getNumberOfDataCols();}
         else                       {return 0;}
    }
    public int               getTotalNumberOfColumns(){
        int curNoCols = getNumberOfDataColumns();
        int curNoAbs  = getNumberOfAbscissa();
        int noOfCols;
        if(curNoAbs == 1){
            noOfCols = curNoAbs + curNoCols;
        } else{
            noOfCols = 1 + curNoAbs + curNoCols;
        }
        return noOfCols;}
    public int               getNumberOfPriors(){return  getAsciiModel().getNumberOfPriors();}
    public StringBuilder     getModelSpecificsForParamsFile(int PADLEN, String PADCHAR){
         int noParams    = 3 +  getAsciiModel().getNumberOfDerived();
         
         StringBuilder buffer = new StringBuilder();
        
         buffer.append(EOL);

         buffer.append (IO.pad("Package Parameters", -PADLEN, PADCHAR ));
         buffer.append(" = "+ noParams);
         buffer.append(EOL);
         
         buffer.append (IO.pad("Model Name", -PADLEN, PADCHAR ));
         buffer.append(" = "+ getAsciiModel().getName());
         buffer.append(EOL);

         buffer.append (IO.pad("Number of Vectors", -PADLEN, PADCHAR ));
         buffer.append(" = "+ getAsciiModel().getNumberOfModelVectors());
         buffer.append(EOL);

         
         int noDerived =  getAsciiModel().getNumberOfDerived();
         buffer.append (IO.pad("Number of Derived", -PADLEN, PADCHAR ));
         buffer.append(" = "+ noDerived );
         buffer.append(EOL);

       
         String [] Derived;
         Derived =  getAsciiModel().getDerived();
         for (int I = 0; I < noDerived; I++) {
             String derivedStr  =   "Derived " + I+1;
             String derivedNum  =    Derived[I];

             buffer.append (IO.pad(derivedStr, -PADLEN, PADCHAR ));
             buffer.append(" = "+ derivedNum  );
             buffer.append(EOL);

         }
         
         return buffer;
     
    };
    public List <ParameterPrior>  getPriors() {
        return getAsciiModel().getPriors();
    }
    public String            getConstructorArg(){ return getAsciiModelName();}
    public String            getInstructions(){
        String noInstructions = "No instrcutions are available for this package";
       if (getAsciiModel().isLoaded() == false) {
        return noInstructions;
       }
        
       String constrArg = getAsciiModel().getName();
        if ( constrArg.equalsIgnoreCase("MtZBig.f")){
            return Enums.PACKAGE_INTSRUCTIONS.MTzBIG.getInstruction();
        }
        else if ( constrArg.equalsIgnoreCase("ContactTime.f")){
            return generateInstructions("Contact Time");
        }
        else if ( constrArg.equalsIgnoreCase("RedorDpC.f")){
            return generateInstructions("Redor Spins");
        }
        else if ( constrArg.equalsIgnoreCase("InvRec.f")){
            return generateInstructions("Inversion Recovery");
        }
        else {
            return noInstructions;
        }
      }
    public boolean           isOutliers(){return isIncludeOutliers ();}
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
        setIncludeOutliers(false);
    }

    public EnterAsciiModel   getAsciiModel(){ return asciiModel;}
    public String            getAsciiModelName(){ return  getAsciiModel().getName();}
    public void              setActive(boolean enabled){
            AllViewers.getInstance().setActive(enabled);

          jserver.setActive(enabled);
          jRun.setEnabled(enabled);
          jResetSave.setActive(enabled);
          includeOutliersCheckBox.setEnabled(enabled);

     }
    public void              destroy(){};

    public  boolean loadSysModel(String modelFileName){
        String systemModelDir       =   DirectoryManager. getSystemModelURL( );
    
         
         boolean alwaysCopyParamsFile   = false;
         
         try{
             asciiModel                     =   loadRemoteSystemModel(systemModelDir,modelFileName, alwaysCopyParamsFile);

             if (getAsciiModel().isLoaded() == false){
                        handleErrorLoad( 
                        "Ascii Model " + getAsciiModel().getName() + " could not be loaded. \n"
                         +
                        "Loading error =  "+ getAsciiModel().getLoadErrorMessage()
              );
                return false;
             }
              JShowModels.getInstance().addModel(this.getAsciiModel());
         
        }catch(Exception exp){
            exp.printStackTrace();
             return false;
       }

         return true;
    }
    
    private static void createAndShowGUI () {
        String modelName    = "Contact Time";
        String modelFileName = "ContactTime.f";
        //Create and set up the window.
        JFrame frame = new JFrame ( modelName);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });


        //Create and set up the content pane.
        BayesEnterAsciiPreloaded newContentPane = new BayesEnterAsciiPreloaded(modelFileName);
        newContentPane.setOpaque (true); //content panes must be opaque
        frame.setContentPane (newContentPane);

        //Display the window.
        frame.pack ();
        frame.setVisible (true);
    }
    public  static void main (String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                createAndShowGUI ();
            }
        });
    }  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dummyLabel;
    private javax.swing.JPanel graph_panel;
    public javax.swing.JCheckBox includeOutliersCheckBox;
    private javax.swing.JPanel jPanel1;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JPanel setup_pane;
    // End of variables declaration//GEN-END:variables
     public javax.swing.JCheckBox getIncludeOutliersCheckBox () {
        return includeOutliersCheckBox;
    }

    private  EnterAsciiModel  asciiModel                        =   new EnterAsciiModel();
    public String message                                       =   "";
    protected boolean includeOutliers                           =   false;
    private   boolean loaded                                    =   false;

     public boolean isIncludeOutliers () {
        return includeOutliers;
    }

   
     public void    savePriors(boolean popupMessages){
      try{
           if(getAsciiModel().isLoaded()){
                String modelName        =   getAsciiModel().getName();
      
               getAsciiModel().overwriteOriginalParamsFile();
               if (popupMessages){
                    String msg =  modelName + ". "+"Priors have been saved";
                    DisplayText.popupMessage(msg );
                }
             }
       }
       catch (Exception e){
            e.printStackTrace();
       }


    }   
   

     public void    handleErrorLoad(String txt){
         DisplayText.popupErrorMessage(txt);
         
    }
     public String  generateInstructions(String programName){
        String noInstructions = "No instrcutions are available for this package";
        if ( programName ==  null) {return  noInstructions;}
        
         StringBuilder sb        = new StringBuilder();
         String sp               =  Enums.sp;
        
         sb.append(sp +   "To use the "+programName+  " package: \n\n");
         sb.append(sp +   "1.  Load an ascii file.\n\n");
         sb.append(sp +   "2.  Review the prior range information, and make appropriate changes.\n\n");
         sb.append(sp +   "3.  Select the server to run the analysis.\n\n");
         sb.append(sp +   "4.  Run the analysis using the \"Run\" button.\n\n");
         sb.append(sp +   "5.  Use \"Get Job\" to get the results from the server.\n\n");
      
        return sb.toString();
      }

     public void             setIncludeOutliers ( boolean includeOutliers ) {
        this.includeOutliers = includeOutliers;
        if( getIncludeOutliersCheckBox () != null){
             getIncludeOutliersCheckBox ().setSelected( includeOutliers);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
   
   
}
