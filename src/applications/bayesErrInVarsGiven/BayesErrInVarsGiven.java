 /*
 * BayesErrInVarsGiven.java
 *
 * Created on February 15, 2008, 9:34 AM
 */

package applications.bayesErrInVarsGiven;

import run.JRun;
import bayes.ParameterPrior;
import bayes.PackageManager;
import bayes.DirectoryManager;
import javax.swing.*;
import java.io.*;
import bayes.JobDirections;
import bayes.WriteBayesParams;
import utilities.DisplayText;
import utilities.IO;
import interfacebeans.*;
import bayes.Enums.*;
import bayes.BayesManager;
import ascii.ASCIIDataViewer;
import ascii.AsciiIO;
import bayes.Reset;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author  larry
 */
public class BayesErrInVarsGiven extends javax.swing.JPanel implements applications.model.AsciiModel,
                                         java.beans.PropertyChangeListener, bayes.ApplicationConstants{


 public enum ERROR_TYPE {

        NOTGIVEN("Not Given", "NOT_GIVEN", 2, "Requires two column ASCII data"),
        XY("X and Y", "GIVEN_XY", 4, "Requires four column ASCII data"),
        XONLY("X only", "GIVEN_X", 3, "Requires three column ASCII data"),
        YONLY("Y only",  "Y only", 3,"Requires three column ASCII data");
        
        private String title        = "";
        private String  value       = "";
        private String help         = "";
        private int numberColumns   = 1;

        ERROR_TYPE(String atitle ,String avalue, int ncol, String ahelp) {
            title               =  atitle;
            value               = avalue;
            numberColumns       = ncol;
            help                = ahelp;
        }

        @Override
        public String toString() {
            return getTitle();
        }
        public String getTitle() {
            return title;
        }
        public String getValue() {
            return value;
        }
        public String getHelp() {
            return help;
        }
        public int getNumberColumns() {
            return numberColumns;
        }

        public static String [] titles (){
           ERROR_TYPE [] types  =   ERROR_TYPE.values();
           int size             =   types .length;
           String [] titles     =   new String [size];

            for (int i = 0; i < titles.length; i++) {
                 titles[i]      = types [i].getTitle();
            }

           return titles;
        }
    };

public BayesErrInVarsGiven() {
        
    startErrInVarsGiven();
    }
    
public void startErrInVarsGiven(){
        
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();
        initComponents ();


        AllViewers.removePriorsViewer();
        ASCIIDataViewer asciiViewer = ASCIIDataViewer.getInstance();
        asciiViewer.addPropertyChangeListener(this);

        BayesManager.pcs.addPropertyChangeListener(this);
    
        
        if(isDeseralized){
            JRun.fireJobIDChange();
        }
       else { AllViewers.showInstructions();}
}    
public void propertyChange(java.beans.PropertyChangeEvent evt){
        
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setup_pane = new javax.swing.JPanel();
        jRun = new run.JRun();
        jPanel2 = new javax.swing.JPanel();
        orderLabel = new javax.swing.JLabel();
        jSetPolyOrder = new JComboBox(new String[]{" 0"," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10"});
        ;
        dummyLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        dataTypeComboBoxLabel = new javax.swing.JLabel();
        jSetGivenErrors = new JComboBox( ERROR_TYPE.values());
        ;
        messageLabel = new javax.swing.JLabel();
        jserver = interfacebeans.JServer.getInstance();
        jResetSave = new interfacebeans.JResetSave();
        graph_panel = AllViewers.getInstance();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        setup_pane.setMaximumSize(new java.awt.Dimension(32767, 200));
        setup_pane.setName("setup_pane"); // NOI18N

        jRun.setName("jRun"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(" Model"));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        orderLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        orderLabel.setText(" Order:"); // NOI18N
        orderLabel.setName("orderLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(orderLabel, gridBagConstraints);

        jSetPolyOrder.setSelectedItem(polynomialOrder);
        jSetPolyOrder.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSet polynomial order.\n\n</html>\n\n"); // NOI18N
        jSetPolyOrder.setName("jSetPolyOrder"); // NOI18N
        jSetPolyOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetPolyOrderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 4, 2);
        jPanel2.add(jSetPolyOrder, gridBagConstraints);

        dummyLabel.setText(" ");
        dummyLabel.setName("dummyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 18;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(dummyLabel, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Options"));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        dataTypeComboBoxLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        dataTypeComboBoxLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dataTypeComboBoxLabel.setText("Given Errors In:"); // NOI18N
        dataTypeComboBoxLabel.setName("dataTypeComboBoxLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel3.add(dataTypeComboBoxLabel, gridBagConstraints);

        jSetGivenErrors.setSelectedItem(this.errorType);
        jSetGivenErrors.setName("jSetGivenErrors"); // NOI18N
        jSetGivenErrors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetGivenErrorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel3.add(jSetGivenErrors, gridBagConstraints);

        messageLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        messageLabel.setText(errorType.getHelp());
        messageLabel.setName("messageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 5, 2);
        jPanel3.add(messageLabel, gridBagConstraints);

        jserver.setName("jserver"); // NOI18N

        jResetSave.setName("jResetSave"); // NOI18N

        org.jdesktop.layout.GroupLayout setup_paneLayout = new org.jdesktop.layout.GroupLayout(setup_pane);
        setup_pane.setLayout(setup_paneLayout);
        setup_paneLayout.setHorizontalGroup(
            setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_paneLayout.createSequentialGroup()
                .addContainerGap()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(308, 308, 308))
        );
        setup_paneLayout.setVerticalGroup(
            setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, setup_paneLayout.createSequentialGroup()
                .add(setup_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jserver, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setup_paneLayout.linkSize(new java.awt.Component[] {jPanel2, jPanel3, jResetSave, jRun, jserver}, org.jdesktop.layout.GroupLayout.VERTICAL);

        add(setup_pane, java.awt.BorderLayout.PAGE_START);

        graph_panel.setMinimumSize(new java.awt.Dimension(110, 110));
        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        add(graph_panel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jSetGivenErrorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSetGivenErrorsActionPerformed
        errorType  =        (ERROR_TYPE)jSetGivenErrors.getSelectedItem();
        setTextMessage(errorType.getHelp());


        AsciiIO.clearAsciiFileNonCompatibleWithModel(this);
        clearPreviousRun();

        
       
    }//GEN-LAST:event_jSetGivenErrorsActionPerformed
    private void jSetPolyOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSetPolyOrderActionPerformed
        polynomialOrder = (String)jSetPolyOrder.getSelectedItem();
        clearPreviousRun();
    }//GEN-LAST:event_jSetPolyOrderActionPerformed
        
    public void             setPackageParameters(ObjectInputStream serializationFile)throws Exception{
        
       ERROR_TYPE type      = (ERROR_TYPE)serializationFile.readObject();
       String order          = (String)serializationFile.readObject();

       errorType            =  type;
       polynomialOrder      =   order;
        
    }
    public void             savePackageParameters(ObjectOutputStream serializationFile){
         
    try{
        serializationFile.writeObject(errorType);
        serializationFile.writeObject(polynomialOrder);
        
    } catch (IOException exp){
	     DirectoryManager.getSerializationFile().delete();        
   }
    } 
    public boolean          isReadyToRun(){

        File dir            =   DirectoryManager.getBayesOtherAnalysisDir();
        File[] files        =   ASCIIDataViewer.getInstance().getFiles();

        if (files == null) {
            DisplayText.popupErrorMessage( "You must load data before you run the program.");
            return false;
        }
        if (files.length != 1) {
            DisplayText.popupErrorMessage(  "Only one data set allowed in this package");
            return false;
        }

        for (File file : files) {
            if (!file.exists()) {
                String msg = "file " + file + "doesn't exist";
                DisplayText.popupErrorMessage(  msg);
                return false;
            }
        }
        
// make sure the data file has the right number of columns
        int curNoOfCols     = 0;
        try{
           curNoOfCols = IO.getNumberOfColumns(files[0]);
        
        }catch (IOException exp) { 
            return false;
        }
        
        if(curNoOfCols != errorType.getNumberColumns()){
                String msg = "The input file has " + curNoOfCols 
                           + " while this analysis requires "
                           + errorType.getNumberColumns() + " column Ascii Data";
                DisplayText.popupErrorMessage(  msg);
                return false;
        }
        

       boolean bl  =  WriteBayesParams.writeParamsFile(this, dir);
       if (bl == false){
           DisplayText.popupErrorMessage( "Failed to write .params file.");
           return false;
       }

       bl  =  JobDirections.writeFromProperties(JobDirections.BAYES_SUBMIT);
       if(bl == false){
          DisplayText.popupErrorMessage( "Failed to write job.directions file.");
          return false;
        }

        return true;
    }    
    public String           getProgramName(){return "BayesErrInVarsGiven";}
    public String           getExtendedProgramName() { return "Error In Variations Given Package" ;}
    public int              getNumberOfAbscissa(){return 1;}
    public int              getNumberOfDataColumns(){return 1;}
    public int              getTotalNumberOfColumns(){return errorType.getNumberColumns();}
    public int              getNumberOfPriors(){return 0;}
    public StringBuilder    getModelSpecificsForParamsFile(int PADLEN, String PADCHAR){
        return writeParams(PADLEN,  PADCHAR);
    };
    public List <ParameterPrior>  getPriors() {
        return new Vector<ParameterPrior>();
    }
    public String           getConstructorArg(){return null;}
    public String           getInstructions(){return PACKAGE_INTSRUCTIONS.ErrInVars.getInstruction();}
    public boolean          isOutliers(){return false;}
    public void             reset(){
            setDefaults();
            clearPreviousRun() ;
            // show defualt viewer
            AllViewers.getInstance().showDefaultViewer();
    }
    public void             clearPreviousRun(){
        // clear outputs of previous run
         Reset.clearAsciiResutls();

         // reset job status
         jRun.reset();

    }
    public void             setDefaults(){
           
            
            errorType       =   ERROR_TYPE.NOTGIVEN;
            polynomialOrder = "0";

            jSetPolyOrder.setSelectedItem(polynomialOrder);
            jSetGivenErrors.setSelectedItem(errorType);
    }
    public void             setActive(boolean enabled){
           AllViewers.getInstance().setActive(enabled);

          jSetPolyOrder.setEnabled(enabled);
          jSetGivenErrors.setEnabled(enabled);
          jserver.setActive(enabled);
          jRun.setEnabled(enabled);
          jResetSave.setActive(enabled);
          messageLabel.setEnabled(enabled);
          dataTypeComboBoxLabel.setEnabled(enabled);
          orderLabel.setEnabled(enabled);
    }

    public static void      createAndShowGUI() {
        JFrame frame = new JFrame ("Fit A Given Polynomial with Errors In Both Variables");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
           @Override
             public void windowClosing(java.awt.event.WindowEvent evt) {
                DirectoryManager.shutDownDirectory();
            }
        });
        BayesErrInVarsGiven newContentPane = new BayesErrInVarsGiven();
        newContentPane.setOpaque (true);
        frame.setContentPane (newContentPane);
        frame.pack ();
        frame.setVisible (true);
    }
    public static void      main(String[] args) {
       javax.swing.SwingUtilities.invokeLater (new Runnable () {
            public void run (){createAndShowGUI();}});
    } 
    public StringBuilder writeParams(int PADLEN, String PADCHAR){
         StringBuilder buffer   = new StringBuilder();

         buffer.append (IO.pad("Package Parameters", -PADLEN, PADCHAR ));
         buffer.append(" = "+ "2");
         buffer.append(EOL);
         

         buffer.append (IO.pad("Polynomial Order", -PADLEN, PADCHAR ));
         buffer.append(" = "+ polynomialOrder);
         buffer.append(EOL);
         
         buffer.append (IO.pad("Errors Given", -PADLEN, PADCHAR ));
         buffer.append(" = "+ errorType.getValue());
         buffer.append(EOL);
         
         return buffer;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dataTypeComboBoxLabel;
    private javax.swing.JLabel dummyLabel;
    private javax.swing.JPanel graph_panel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    public static javax.swing.JComboBox jSetGivenErrors;
    public static javax.swing.JComboBox jSetPolyOrder;
    private interfacebeans.JServer jserver;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JPanel setup_pane;
    // End of variables declaration//GEN-END:variables

    private void setTextMessage(String message){
        messageLabel.setText( message);
    }
    private static String polynomialOrder = "0";
    private ERROR_TYPE  errorType         =  ERROR_TYPE.NOTGIVEN;





}