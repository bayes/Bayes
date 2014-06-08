/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BayesDensityEstimation.java
 *
 * Created on Oct 2, 2009, 9:48:00 AM
 */

package applications.bayesDensityEstimation;

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
import java.util.Vector;
import bayes.BayesManager;
import bayes.Reset;
import utilities.IO;
/**
 *
 * @author apple
 */
public class BayesDensityEstimation extends javax.swing.JPanel  implements applications.model.AsciiModel,
                                          java.beans.PropertyChangeListener,
                                            bayes.ApplicationConstants{
    private String histogramOrder                        =  ORDERS[0];
    private String histogramSize = SIZES[0];

    private final static int HSIZELENGTH           = 10;
    private final static int HORDERLENGTH          = 16;
    public final static String [] ORDERS               ;
    public final static String [] SIZES                ;
    private final static int SIZE_START             = 51;
    private final static int SIZE_STEP              = 50;
    private final static int ORDER_START            = 1;
    private final static int ORDER_STEP             = 1;
    private final static String AUTOMATIC_ORDER     = "Automatic";

  static {
             SIZES = new String[ HSIZELENGTH];
             for (int i = 0; i < SIZES.length; i++) {
                 int size       = SIZE_START    + SIZE_STEP *i;
                 SIZES[i]       = ""+  size;

            }

            ORDERS          =   new String [HORDERLENGTH ];
            ORDERS[0]       =   AUTOMATIC_ORDER;
            for (int i = ORDER_START ; i < ORDERS.length; i += ORDER_STEP) {
                  ORDERS[i] =   ""+ i;

           }
  }


    /** Creates new form BayesDensityEstimation */
    public BayesDensityEstimation() {
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();

        initComponents();
        AllViewers.removePriorsViewer();
      
        BayesManager.pcs.addPropertyChangeListener(this);
        ASCIIDataViewer.getInstance().setScatteringRenderer();
       if(isDeseralized){ JRun.fireJobIDChange();}
       else{AllViewers.showInstructions();}
    }

    public void              setPackageParameters(ObjectInputStream serializationFile) throws Exception{
        String order        = serializationFile.readObject().toString();
        String size         = serializationFile.readObject().toString();
          System.out.println(order + " "+ size);
        this.setHistogramOrder(order);
        this.setHistogramSize(size);

    }
    public void              savePackageParameters(ObjectOutputStream serializationFile){

        try{

           serializationFile.writeObject( getHistogramOrder());
           serializationFile.writeObject( getHistogramSize());

            System.out.println("W "+getHistogramOrder() + " "+ getHistogramSize());

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
         return "DensityEstimationMaxEnt";
         //return "DensityEstimationBinnedGiven";
         //return "BayesBinnedHistograms";
    }
    public String            getExtendedProgramName() { return "Maximum Entropy Method Of Moments Density Estimation" ;}
    public int               getNumberOfAbscissa() {
       return 1;
    }
    public int               getNumberOfDataColumns() {
        return 1;
    }
    public int               getTotalNumberOfColumns(){return 2;}
    public int               getNumberOfPriors() { return 0 ;}
    public StringBuilder     getModelSpecificsForParamsFile(  int PADLEN, String PADCHAR) {
         StringBuilder buffer = new StringBuilder();

         buffer.append(EOL);

         buffer.append (IO.pad("Package Parameters", -PADLEN, PADCHAR ));
         buffer.append(" = "+ "1");
         buffer.append(EOL);

         buffer.append (IO.pad("Histogram Size", -PADLEN, PADCHAR ));
         buffer.append(" = "+ getHistogramSize());
         buffer.append(EOL);

         buffer.append (IO.pad("Histogram Order", -PADLEN, PADCHAR ));
         buffer.append(" = "+ getHistogramOrder());
         buffer.append(EOL);

         return buffer;
    }
    public List <ParameterPrior>  getPriors() {
        return new Vector <ParameterPrior>();
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.MAX_ENTROPY_HISTOGRAM.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              setActive(boolean enabled){
       AllViewers.getInstance().setActive(enabled);
       jserver.setActive(enabled);
       jRun.setEnabled(enabled);
       jResetSave.setActive(enabled);
       getOrderComboBox().setEnabled(enabled);
       getSizeComboBox().setEnabled(enabled);
       setOrderLabel.setEnabled(enabled);
       setSizeLabel.setEnabled(enabled);
    }
    public void              reset(){

            setDefaults();

            //  clear previous run
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
        setHistogramSize(""+SIZE_START);
        setHistogramOrder(""+AUTOMATIC_ORDER );
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

        frame.add(new BayesDensityEstimation ());

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

        jSplitPane1 = new javax.swing.JSplitPane();
        setup_panel = new javax.swing.JPanel();
        jRun = new run.JRun();
        jserver = interfacebeans.JServer.getInstance();
        modelPanel = new javax.swing.JPanel();
        setOrderLabel = new javax.swing.JLabel();
        orderComboBox = new JComboBox(ORDERS);

        ;
        setSizeLabel = new javax.swing.JLabel();
        sizeComboBox = new JComboBox(SIZES);

        ;
        jResetSave = new interfacebeans.JResetSave();
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

        setOrderLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        setOrderLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setOrderLabel.setText("Order");
        setOrderLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        setOrderLabel.setName("setOrderLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(setOrderLabel, gridBagConstraints);

        orderComboBox.setSelectedItem( getHistogramOrder());
        orderComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSpecify the number of Lagrange multipliers to use in the density<br>\nfunction.  If Automatic is set, the package computes the posterior<br>\nprobability for the number of Lagrange multipliers.<br>\n\n</html>\n\n\n"); // NOI18N
        orderComboBox.setName("orderComboBox"); // NOI18N
        orderComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orderComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(orderComboBox, gridBagConstraints);

        setSizeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        setSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setText("Size");
        setSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setName("setSizeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(setSizeLabel, gridBagConstraints);

        sizeComboBox.setSelectedItem( getHistogramSize());
        sizeComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSpecify the size of the output density function.\n\n</html>\n\n"); // NOI18N
        sizeComboBox.setName("sizeComboBox"); // NOI18N
        sizeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sizeComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(sizeComboBox, gridBagConstraints);

        jResetSave.setName("jResetSave"); // NOI18N

        org.jdesktop.layout.GroupLayout setup_panelLayout = new org.jdesktop.layout.GroupLayout(setup_panel);
        setup_panel.setLayout(setup_panelLayout);
        setup_panelLayout.setHorizontalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 209, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1322, Short.MAX_VALUE))
        );
        setup_panelLayout.setVerticalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        setup_panelLayout.linkSize(new java.awt.Component[] {jResetSave, jRun, jserver, modelPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setLeftComponent(setup_panel);

        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(graph_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void orderComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orderComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String seletedValue = (String)evt.getItem();
            setHistogramOrder( seletedValue);
            return;
        }
         clearPreviousRun();
}//GEN-LAST:event_orderComboBoxItemStateChanged
    private void sizeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sizeComboBoxItemStateChanged
         if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String seletedValue = (String)evt.getItem();
            setHistogramSize( seletedValue);
            return;
        }
        clearPreviousRun();
    }//GEN-LAST:event_sizeComboBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel graph_panel;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    private javax.swing.JPanel modelPanel;
    public javax.swing.JComboBox orderComboBox;
    private javax.swing.JLabel setOrderLabel;
    private javax.swing.JLabel setSizeLabel;
    private javax.swing.JPanel setup_panel;
    public javax.swing.JComboBox sizeComboBox;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JComboBox getOrderComboBox() {
        return orderComboBox;
    }
    public javax.swing.JComboBox getSizeComboBox() {
        return sizeComboBox;
    }


    public String getHistogramOrder() {
        return histogramOrder;
    }
    public void setHistogramOrder(String histtogramOrder) {
        this.histogramOrder = histtogramOrder;
        if (getOrderComboBox() != null){
            getOrderComboBox().setSelectedItem(histtogramOrder);
        }
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

    

}
