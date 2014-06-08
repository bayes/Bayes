/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BayesBinnedHistogram.java
 *
 * Created on Oct 2, 2009, 9:48:00 AM
 */

package applications.bayesAnalyze.bayesDensityEstimationKernal;

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
import interfacebeans.HyperlinkLabel;
import java.util.ArrayList;
import utilities.IO;
/**
 *
 * @author apple
 */
 


public class BayesDensityEstimationKernel extends javax.swing.JPanel  implements applications.model.AsciiModel,
                                          java.beans.PropertyChangeListener,
                                            bayes.ApplicationConstants{

   
    
     public static enum KERNEL_TYPE {  
        AUTOMATIC       (   "Automatic"  ),
        EXPONENTIAL     (   "Exponential"  ),
        UNIFORM         (   "Uniform"  ),
        TRIANGULAR      (   "Triangular"  ),
        EPANECHNIKOV    (   "Epanenchnikov"  ),
        QUARTIC         (   "Quartic"  ),
        TRIWEIGHT       (   "Triweight"  ),
        TRICUBE         (   "Tricube"  ),
        GAUSSIAN        (   "Gaussian"  ),
        COSINE          (   "Cosine");
     
        private final String name;
        KERNEL_TYPE(String aname ) {
            this.name                       =   aname;
        }
        public  String getName()          { return name;}
        @Override
        public String toString (){return name;}
        
    }
    
    
    public final static int DEFAULT_SMOOTHING_STEPS = 5;
    public final static int DEFAULT_SIZE_START      = 51;
    public final static int DEFAULT_SIZE_STEP       = 50;
    public final static Integer [] SIZES                ;
    
    List <ParameterPrior> priors                    =   new ArrayList<ParameterPrior>();
    
    private final int DEFAULT_OUTPUT_SIZE           =   101;
    private  static final KERNEL_TYPE DEFAULT_KERNEL_TYPE  = KERNEL_TYPE.AUTOMATIC;       
            
    private   int outputSize                        = DEFAULT_OUTPUT_SIZE ;
    private  KERNEL_TYPE kernelType                 = DEFAULT_KERNEL_TYPE;
    
    public  final static String FID_PROTOTYPE_FILE      =   "/fid/ProtoType";
    
   static {
             SIZES = new Integer[6];
             for (int i = 0; i < SIZES.length; i++) {
                 int size       = DEFAULT_SIZE_START + DEFAULT_SIZE_STEP *(i);
                 SIZES[i]       = size;

            }
          
  }



    /** Creates new form BayesBinnedHistogram */
    public BayesDensityEstimationKernel() {
        PackageManager.setCurrentApplication(this);
        boolean isDeseralized = bayes.Serialize. deserializeCurrenExperiment();

        initComponents();
       // AllViewers.removePriorsViewer();
      
        BayesManager.pcs.addPropertyChangeListener(this);
        AllViewers.removePriorsViewer();
        ASCIIDataViewer.getInstance().setScatteringRenderer();
       if(isDeseralized){ JRun.fireJobIDChange();}
       else{AllViewers.showInstructions();}
    }

    public void              setPackageParameters(ObjectInputStream serializationFile) throws Exception{
        int size                = serializationFile.readInt();
        KERNEL_TYPE ktype       = (KERNEL_TYPE)serializationFile.readObject();
        
        this.setOutputSize(size);
        this.setKernelType(ktype);

    }
    public void              savePackageParameters(ObjectOutputStream serializationFile){

        try{

           serializationFile.writeInt( getOutputSize());
           serializationFile.writeObject( getKernelType());

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
         return "DensityEstimationKernel";
    }
    public String            getExtendedProgramName() { return "Kernel Density Estimation" ;}
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

         buffer.append (IO.pad("Package Parameters ", -PADLEN, PADCHAR ));
         buffer.append(" = "+ "2");
         buffer.append(EOL);

         
        
         String key             =   "Kernel Type";
         String val             =    this.getKernelType().getName();
         buffer.append (IO.pad( key , -PADLEN, PADCHAR ));
         buffer.append(" = "+ val);
         buffer.append(EOL);
         
         int size        =   getOutputSize();
         String histKey             =   "Output Size";
         buffer.append (IO.pad( histKey , -PADLEN, PADCHAR ));
         buffer.append(" = "+ size);
         buffer.append(EOL);

         
         return buffer;
    }
    public List <ParameterPrior>  getPriors() {
        return  priors ;
    }
    public String            getConstructorArg(){return null;}
    public String            getInstructions(){return PACKAGE_INTSRUCTIONS.DENSITY_ESTIMATION_KERNEL.getInstruction();}
    public boolean           isOutliers(){return false;}
    public void              setActive(boolean enabled){
        AllViewers.getInstance().setActive(enabled);
       jRun.setEnabled(enabled);
       jserver.setActive(enabled);
       jResetSave.setActive(enabled);
       getSizeComboBox().setEnabled(enabled);
       setSizeLabel.setEnabled(enabled);
       kernelTypeComboBox.setEnabled(enabled);
       KernelTypeLabel.setEnabled(enabled);    
    
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

         setKernelType(DEFAULT_KERNEL_TYPE);
         setOutputSize(DEFAULT_OUTPUT_SIZE   );


        
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

        frame.add(new BayesDensityEstimationKernel ());

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
        setSizeLabel = new javax.swing.JLabel();
        sizeComboBox = new JComboBox(SIZES);

        ;
        String name = "Kernel Type";
        String url  =   "http://en.wikipedia.org/wiki/Kernel_(statistics)";
        KernelTypeLabel = new HyperlinkLabel(name, url);
        kernelTypeComboBox = new JComboBox(KERNEL_TYPE.values());

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

        setSizeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        setSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setText("Output Size");
        setSizeLabel.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nThe Output Size specifies the size of the output<br>\nhistogram that is generated at the end of the calculation.\n\n</html>\n"); // NOI18N
        setSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        setSizeLabel.setName("setSizeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(setSizeLabel, gridBagConstraints);

        sizeComboBox.setSelectedItem( getOutputSize());
        sizeComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nThe Output Size specifies the size of the output<br>\nhistogram that is generated at the end of the calculation.\n\n</html>\n\n"); // NOI18N
        sizeComboBox.setName("sizeComboBox"); // NOI18N
        sizeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sizeComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(sizeComboBox, gridBagConstraints);

        KernelTypeLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        KernelTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        KernelTypeLabel.setText("Kernel Type"); // NOI18N
        KernelTypeLabel.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nActivating this button will direct you browser <br>\nto a Wikipedia page that describes each kernel type.\n\n</html>\n"); // NOI18N
        KernelTypeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        KernelTypeLabel.setName("KernelTypeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(KernelTypeLabel, gridBagConstraints);

        kernelTypeComboBox.setSelectedItem( this.getKernelType());
        kernelTypeComboBox.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nSelect a histogram type.<br>\n<br>\nIn statistics, kernel density estimation is a non-parametric <br>\nway of estimating the probability density function of <br>\na random variable.<br>\nKernel density estimation is a fundamental data smoothing problem <br>\nwhere inferences about the population are made based on <br>\na finite data sample. <br> \n<br>\n\n<a href=\"http://en.wikipedia.org/wiki/Kernel_density_estimation\">More info on Histogram Kernels</a> can be accessed by cliking \"Kernel Type\"  label.\n\n</html>\n\n"); // NOI18N
        kernelTypeComboBox.setName("kernelTypeComboBox"); // NOI18N
        kernelTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kernelTypeComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        modelPanel.add(kernelTypeComboBox, gridBagConstraints);

        jResetSave.setName("jResetSave"); // NOI18N

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
                .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1555, Short.MAX_VALUE))
        );
        setup_panelLayout.setVerticalGroup(
            setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setup_panelLayout.createSequentialGroup()
                .add(setup_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jResetSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jserver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setup_panelLayout.linkSize(new java.awt.Component[] {jResetSave, jRun, jserver, modelPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setTopComponent(setup_panel);

        graph_panel.setName("graph_panel"); // NOI18N
        graph_panel.setLayout(new javax.swing.BoxLayout(graph_panel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setBottomComponent(graph_panel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void sizeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sizeComboBoxItemStateChanged
         if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            int size = (Integer)evt.getItem();
            this.outputSize         =   size;
            return;
        }


        clearPreviousRun();
    }//GEN-LAST:event_sizeComboBoxItemStateChanged

    private void kernelTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kernelTypeComboBoxItemStateChanged
         if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            KERNEL_TYPE seletedValue = (KERNEL_TYPE)evt.getItem();
            this.kernelType           = seletedValue;
            return;
        }


        clearPreviousRun();
    }//GEN-LAST:event_kernelTypeComboBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel KernelTypeLabel;
    private javax.swing.JPanel graph_panel;
    private interfacebeans.JResetSave jResetSave;
    private run.JRun jRun;
    private javax.swing.JSplitPane jSplitPane1;
    private interfacebeans.JServer jserver;
    public javax.swing.JComboBox kernelTypeComboBox;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JLabel setSizeLabel;
    private javax.swing.JPanel setup_panel;
    public javax.swing.JComboBox sizeComboBox;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JComboBox getSizeComboBox() {
        return sizeComboBox;
    }
 
    public int getOutputSize() {
        return outputSize;
    }
    public void setOutputSize(int anOutputSize) {
        this.outputSize = anOutputSize;
        if (getSizeComboBox() != null){
            getSizeComboBox().setSelectedItem(anOutputSize);
        }
    }
    
   
   
    
    public KERNEL_TYPE getKernelType() {
        return kernelType;
    }

    public void setKernelType(KERNEL_TYPE aKernelType) {
        kernelType = aKernelType;
        if (kernelTypeComboBox != null){
            kernelTypeComboBox.setSelectedItem(kernelType);
        }
    }

    








    

}
