/*
 * BayesView.java
 */
package bayes;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Application;

import java.awt.Cursor;
import javax.swing.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.beans.PropertyChangeListener;

import utilities.*;
import interfacebeans.*;
import interfacebeans.JCleanJobRequest;
import interfacebeans.AllViewers;
import interfacebeans.JEditServers;
import load.*;

import applications.bayesExponential.BayesExponential;
import applications.bayesDiffTensor.BayesDiffTensor;
import applications.bayesEnterAscii.BayesEnterAscii;
import applications.bayesMtZ.BayesMtZ;
import applications.bayesMtZKinetics.BayesMtZKinetics;
import applications.bayesPolynomial.BayesPolynomial;
import applications.bayesBF.BayesBF;
import applications.bayesErrInVarsGiven.BayesErrInVarsGiven;
import applications.bayesWater.BayesWater;
import applications.bayesMetabolite.BayesMetabolite;
import applications.bayesAnalyze.BayesAnalyze;
import applications.bayesAnalyze.bayesDensityEstimationKernal.BayesDensityEstimationKernel;
import applications.bayesEnterAsciiModel.BayesEnterAsciiModel;
import applications.bayesFindResonances.BayesFindResonances;
import applications.bayesEnterAsciiPreloaded.BayesEnterAsciiPreloaded;
import applications.bayesPhase.BayesPhase;
import applications.bayesPhase2.BayesPhaseNonlinear;
import applications.bayesAnalyzeImagePixels.AnalyzeImagePixels;
import applications.bayesAnalyzeImagePixelsUnique.AnalyzeImagePixelsUnique;
import applications.bayesBinnedHistogram.BayesBinnedHistogram;
import applications.bayesDensityEstimation.BayesDensityEstimation;
import applications.bayesImageModelSelection.ImageModelSelection;
import applications.bayesTestModel.BayesTestModel;
import applications.model.*;

import java.awt.Component;
import java.awt.Desktop;
import javax.swing.JFileChooser;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.UIManager.LookAndFeelInfo;
import load.gui.JGetBayesAnalyzePeak;
import load.gui.JSeekAndLoadWorkDirJDialog;
import run.JRun;
import utilities.MenuScroller;

/**
 * The application's main frame.
 */
public class BayesView extends FrameView implements
        java.beans.PropertyChangeListener {

    public final static String VERSIONINFO = "/bayes/UpdateLog";
    private final static HashMap<JRadioButtonMenuItem, ModelDescriptor> modelDescrp = new HashMap<JRadioButtonMenuItem, ModelDescriptor>();
    private javax.swing.JRadioButtonMenuItem selectedPackageMenutItem;
    public static boolean debug = false;

    public BayesView(SingleFrameApplication app) {
        super(app);

        try {
            String savedLookAndFeel = ApplicationPreferences.getLookAndFeel();
            /*
            System.out.println("Saved Look and Feel "+  savedLookAndFeel );
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.println( info);
            System.out.println( info.getName());
            System.out.println("");

            }
             *
             */
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (savedLookAndFeel.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }


        } catch (Exception exp) {
            exp.printStackTrace();
        }

        //  Color dcolor = UIManager.getColor("Panel.background");


        //   // This should be called before any swing classes are used
        String antialising = "swing.aatext";
        System.setProperty(antialising, "true");


        System.out.println("Runtime maximum memory " + Runtime.getRuntime().maxMemory());




        java.net.URL url = getClass().getResource("/bayes/resources/wustl_250X115.jpg");
        java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Image img = kit.createImage(url);
        getFrame().setIconImage(img);

        initComponents();

        initModelDesciptons();

        setAcitveLoadButtons(false);


        try {
            BayesIntro bi = new BayesIntro();
            mainPanel.add(bi, java.awt.BorderLayout.CENTER);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        getFrame().setTitle(BayesManager.APPLICATION_TITLE);
        //getFrame().setPreferredSize(new Dimension (1300,900 ));
        DisplayText.frame = this.getFrame();
        //getFrame().setSize(1500, 1800);
        //getFrame().repaint();


        BayesManager.pcs.addPropertyChangeListener(this);

        ApplicationPreferences.refreshAllPreferences();

    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_START)) {
            setActive(false);
        } else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_END)) {
            setActive(true);
        } else if (evt.getPropertyName().equals(bayes.BayesManager.JRUN_JOB_CANCELED)) {
            setActive(true);
        }




    }
    // *********** setup actions *****************//

    @Action
    public void setModel(ModelDescriptor modelDescriptor) {
        System.out.println("Start loading package " + modelDescriptor.getModelTitle());
        killCurrentModel();
        boolean isModelLoaded = makeAndDisplayCurrentModel(modelDescriptor);

        if (isModelLoaded == false) {
            doNoModelSelected("No model is loaded");
        } else {
            setAcitveLoadButtons(isModelLoaded);
        }

    }

    @Action
    public void popupEditExperiments() {
        List<String> dir_names = ApplicationPreferences.getWorkDirs();
        String curWorkDir = ApplicationPreferences.getCurrentWorkDir();
        DirectoryViewer l = DirectoryViewer.showDialog(dir_names, curWorkDir);
        String newWorkDir = l.getCurrenWorkingDir();
        l.dispose();
        l = null; // make sure garbage constructor destroys it
        modifyExperimentMenu(getWorkDirMenu());

        boolean isNewWorkDir = !curWorkDir.equals(newWorkDir);

        if (isNewWorkDir) {
            Component[] c = getWorkDirMenu().getMenuComponents();

            for (Component component : c) {
                if (component instanceof JMenuItem) {
                    JMenuItem mi = (JMenuItem) component;
                    boolean found = mi.getText().trim().equalsIgnoreCase(newWorkDir);
                    if (found) {
                        mi.doClick();
                    }
                } else {
                    continue;
                }
            }
            /* if  workdir didn't change */
        } else {
            if (this.isPackageLoaded() == false) {
                loadDir(curWorkDir);
            }

        }



    }

    private javax.swing.Action getAction(String actionName) {
        ApplicationContext cntxt = Application.getInstance(bayes.BayesApp.class).getContext();
        javax.swing.ActionMap map = cntxt.getActionMap(BayesView.class, this);
        return map.get(actionName);
    }

    // *********** stop setup actions
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        loadAsciiMenu = new javax.swing.JMenu();
        loadAsciiFromFileMenuItem = new javax.swing.JMenuItem();
        loadAsciiFromBayesAnalyzeMenuItem2 = new javax.swing.JMenuItem();
        loadFidMenu = new javax.swing.JMenu();
        loadFidDataMenuItem = new javax.swing.JMenuItem();
        loadSiemensRdaDataMenuItem = new javax.swing.JMenuItem();
        loadSiemensRawDataMenuItem = new javax.swing.JMenuItem();
        text2FidMenuItem = new javax.swing.JMenuItem();
        BrukerBinaryMenuItem = new javax.swing.JMenuItem();
        loadImageMenu = new javax.swing.JMenu();
        varianBinaryMenuItem = new javax.swing.JMenuItem();
        text2FidImageMenuItem = new javax.swing.JMenuItem();
        fdf2imageMenuItem = new javax.swing.JMenuItem();
        imgBinaryMenuItem = new javax.swing.JMenuItem();
        rawBinaryMenuItem = new javax.swing.JMenuItem();
        singled2seqMenuItem = new javax.swing.JMenuItem();
        multiple2seqMenuItem = new javax.swing.JMenuItem();
        text2imageMenuItem = new javax.swing.JMenuItem();
        textMultiColumn2imageMenuItem = new javax.swing.JMenuItem();
        ima2imageMenuItem = new javax.swing.JMenuItem();
        DicomMenuItem = new javax.swing.JMenuItem();
        jpgMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        loadAbscissaMenu = new javax.swing.JMenu();
        loadAbscissaMenuItem = new javax.swing.JMenuItem();
        loadAbscissaProcparMenuItem = new javax.swing.JMenuItem();
        downloadTestDataMenuItem = new javax.swing.JMenuItem();
        downloadManualMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jsaveExperimentMenuItem = new javax.swing.JMenuItem();
        loadExperimentMenuItem = new javax.swing.JMenuItem();
        bacthLoadWorkDirMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        packageMenu = new javax.swing.JMenu();
        exponentialMenuItem = new javax.swing.JRadioButtonMenuItem();
        inversionRecoverylMenuItem = new javax.swing.JRadioButtonMenuItem();
        diffusonTensorMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        enterAsciiMenuItem = new javax.swing.JRadioButtonMenuItem();
        enterAsciiModelMenuItem = new javax.swing.JRadioButtonMenuItem();
        enterAsciiModelTestRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mzTMenuItem = new javax.swing.JRadioButtonMenuItem();
        mzTKineticsMenuItem = new javax.swing.JRadioButtonMenuItem();
        mtZBigMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        bayesAnalyzeMenuItem = new javax.swing.JRadioButtonMenuItem();
        bayesWaterMenuItem = new javax.swing.JRadioButtonMenuItem();
        findResMenuItem = new javax.swing.JRadioButtonMenuItem();
        bayesMetaboliteMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        behrensFisherMenuItem = new javax.swing.JRadioButtonMenuItem();
        ErrInVarsGivenButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        polynomialMenuItem = new javax.swing.JRadioButtonMenuItem();
        redoDpCMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        histogramMenuItem = new javax.swing.JRadioButtonMenuItem();
        binnedHistogramMenuItem = new javax.swing.JRadioButtonMenuItem();
        densityEstimationKernelMenuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        bayesPhaseMenuItem = new javax.swing.JRadioButtonMenuItem();
        bayesPhaseNonLinearMenuItem = new javax.swing.JRadioButtonMenuItem();
        analyzeImagePixelMenuItem = new javax.swing.JRadioButtonMenuItem();
        imageModelSelectionMenuItem = new javax.swing.JRadioButtonMenuItem();
        imagePixelsUniqueMenuItem = new javax.swing.JRadioButtonMenuItem();
        workDirMenu = new javax.swing.JMenu();
        optionsMenu = new javax.swing.JMenu();
        mcmcMenuItem = new javax.swing.JMenuItem();
        setServersMenuItem = new javax.swing.JMenuItem();
        windowSizeMEnu = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        preferencesMenuItem = new javax.swing.JMenuItem();
        utilitiesMenu = new javax.swing.JMenu();
        memoryMonitorMenuItem = new javax.swing.JMenuItem();
        systemInfoMenuItem = new javax.swing.JMenuItem();
        checkSoftwareUpdateMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        manualMenuItem = new javax.swing.JMenuItem();
        homepageMenuItem = new javax.swing.JMenuItem();
        contactUsMenu = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        startJobMonitorButton = new javax.swing.JButton();
        startJobMonitorButton1 = new javax.swing.JButton();
        takeApplicationWindowScreenShot = new javax.swing.JButton();
        currentViewerScreenShot = new javax.swing.JButton();
        packageButtonGroup = null;
        dummyMenuItem = new javax.swing.JRadioButtonMenuItem();
        contactTimeMenuItem = new javax.swing.JRadioButtonMenuItem();
        menuBarMessage = new javax.swing.JLabel();
        jSeparator = new javax.swing.JPopupMenu.Separator();

        FormListener formListener = new FormListener();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(700, 900));
        mainPanel.setLayout(new java.awt.BorderLayout());

        menuBar.setName("menuBar"); // NOI18N
        menuBar.setPreferredSize(new java.awt.Dimension(385, 25));

        fileMenu.setText("File");
        fileMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        fileMenu.setName("fileMenu"); // NOI18N

        loadAsciiMenu.setText("Load Ascii");
        loadAsciiMenu.setName("loadAsciiMenu"); // NOI18N

        loadAsciiFromFileMenuItem.setText("File");
        loadAsciiFromFileMenuItem.setName("loadAsciiFromFileMenuItem"); // NOI18N
        loadAsciiFromFileMenuItem.addActionListener(formListener);
        loadAsciiMenu.add(loadAsciiFromFileMenuItem);

        loadAsciiFromBayesAnalyzeMenuItem2.setText("Bayes Analyze File");
        loadAsciiFromBayesAnalyzeMenuItem2.setName("loadAsciiFromBayesAnalyzeMenuItem2"); // NOI18N
        loadAsciiFromBayesAnalyzeMenuItem2.addActionListener(formListener);
        loadAsciiMenu.add(loadAsciiFromBayesAnalyzeMenuItem2);

        fileMenu.add(loadAsciiMenu);

        loadFidMenu.setText("Load Spectroscopy Fid");
        loadFidMenu.setName("loadFidMenu"); // NOI18N

        loadFidDataMenuItem.setText("Varian FID");
        loadFidDataMenuItem.setName("loadFidDataMenuItem"); // NOI18N
        loadFidDataMenuItem.addActionListener(formListener);
        loadFidMenu.add(loadFidDataMenuItem);

        loadSiemensRdaDataMenuItem.setText("Siemens RDA");
        loadSiemensRdaDataMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad  Siemens RDA file. <br>\n</font></p><html>\n"); // NOI18N
        loadSiemensRdaDataMenuItem.setName("loadSiemensRdaDataMenuItem"); // NOI18N
        loadSiemensRdaDataMenuItem.addActionListener(formListener);
        loadFidMenu.add(loadSiemensRdaDataMenuItem);

        loadSiemensRawDataMenuItem.setText("Siemens RAW");
        loadSiemensRawDataMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad  Siemens RAW file. <br>\n</font></p><html>\n"); // NOI18N
        loadSiemensRawDataMenuItem.setName("loadSiemensRawDataMenuItem"); // NOI18N
        loadSiemensRawDataMenuItem.addActionListener(formListener);
        loadFidMenu.add(loadSiemensRawDataMenuItem);

        text2FidMenuItem.setText("Text File");
        text2FidMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad  ascii (text) file. <br>\n</font></p><html>\n"); // NOI18N
        text2FidMenuItem.setName("text2FidMenuItem"); // NOI18N
        text2FidMenuItem.addActionListener(formListener);
        loadFidMenu.add(text2FidMenuItem);

        BrukerBinaryMenuItem.setText("Bruker Binary");
        BrukerBinaryMenuItem.setName("BrukerBinaryMenuItem"); // NOI18N
        BrukerBinaryMenuItem.addActionListener(formListener);
        BrukerBinaryMenuItem.setVisible(false);
        loadFidMenu.add(BrukerBinaryMenuItem);

        fileMenu.add(loadFidMenu);

        loadImageMenu.setText("Load Image "); // NOI18N
        loadImageMenu.setName("loadImageMenu"); // NOI18N

        varianBinaryMenuItem.setText("Varian k-space FID");
        varianBinaryMenuItem.setName("varianBinaryMenuItem"); // NOI18N
        varianBinaryMenuItem.addActionListener(formListener);
        loadImageMenu.add(varianBinaryMenuItem);

        text2FidImageMenuItem.setText("Text k-space FID");
        text2FidImageMenuItem.setName("text2FidImageMenuItem"); // NOI18N
        text2FidImageMenuItem.addActionListener(formListener);
        loadImageMenu.add(text2FidImageMenuItem);

        fdf2imageMenuItem.setText("FDF");
        fdf2imageMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad single or multiple <br> \nVarian FDF image(s).\n</font></p><html>\n"); // NOI18N
        fdf2imageMenuItem.setName("fdf2imageMenuItem"); // NOI18N
        fdf2imageMenuItem.addActionListener(formListener);
        loadImageMenu.add(fdf2imageMenuItem);

        imgBinaryMenuItem.setText("Binary 4dfp (.img)");
        imgBinaryMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad 4dfp binary  image. <br>\n</font></p><html>\n"); // NOI18N
        imgBinaryMenuItem.setName("imgBinaryMenuItem"); // NOI18N
        imgBinaryMenuItem.addActionListener(formListener);
        loadImageMenu.add(imgBinaryMenuItem);

        rawBinaryMenuItem.setText("General Binary");
        rawBinaryMenuItem.setName("rawBinaryMenuItem"); // NOI18N
        rawBinaryMenuItem.addActionListener(formListener);
        loadImageMenu.add(rawBinaryMenuItem);

        singled2seqMenuItem.setText("Bruker single 2dseq ");
        singled2seqMenuItem.setName("singled2seqMenuItem"); // NOI18N
        singled2seqMenuItem.addActionListener(formListener);
        //singled2seqMenuItem.setVisible(false);
        loadImageMenu.add(singled2seqMenuItem);

        multiple2seqMenuItem.setText("Bruker Stack 2dseq ");
        multiple2seqMenuItem.setName("multiple2seqMenuItem"); // NOI18N
        multiple2seqMenuItem.addActionListener(formListener);
        //singled2seqMenuItem.setVisible(false);
        loadImageMenu.add(multiple2seqMenuItem);

        text2imageMenuItem.setText("Single-Column Text File"); // NOI18N
        text2imageMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad image from a single-column<br> \nascii (text) file.\n</font></p><html>\n"); // NOI18N
        text2imageMenuItem.setName("text2imageMenuItem"); // NOI18N
        text2imageMenuItem.addActionListener(formListener);
        loadImageMenu.add(text2imageMenuItem);

        textMultiColumn2imageMenuItem.setText("Multi-Column Text File");
        textMultiColumn2imageMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad image from a multi-column<br> \nascii (text) file.\n</font></p><html>\n"); // NOI18N
        textMultiColumn2imageMenuItem.setName("textMultiColumn2imageMenuItem"); // NOI18N
        textMultiColumn2imageMenuItem.addActionListener(formListener);
        loadImageMenu.add(textMultiColumn2imageMenuItem);

        ima2imageMenuItem.setText("Siemens IMA");
        ima2imageMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad single or multiple <br> \nSiemens IMA image(s).\n</font></p><html>\n"); // NOI18N
        ima2imageMenuItem.setName("ima2imageMenuItem"); // NOI18N
        ima2imageMenuItem.addActionListener(formListener);
        loadImageMenu.add(ima2imageMenuItem);

        DicomMenuItem.setText("DICOM");
        DicomMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad DICOM images. <br> \n</font></p><html>\n"); // NOI18N
        DicomMenuItem.setName("DicomMenuItem"); // NOI18N
        DicomMenuItem.addActionListener(formListener);
        loadImageMenu.add(DicomMenuItem);

        jpgMenuItem.setText("Common (jpg, png, giff)");
        jpgMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nLoad DICOM images. <br> \n</font></p><html>\n"); // NOI18N
        jpgMenuItem.setName("jpgMenuItem"); // NOI18N
        jpgMenuItem.addActionListener(formListener);
        loadImageMenu.add(jpgMenuItem);

        fileMenu.add(loadImageMenu);

        jSeparator7.setName("jSeparator7"); // NOI18N
        fileMenu.add(jSeparator7);

        loadAbscissaMenu.setText("Load Abscissa");
        loadAbscissaMenu.setName("loadAbscissaMenu"); // NOI18N

        loadAbscissaMenuItem.setText("From File");
        loadAbscissaMenuItem.setName("loadAbscissaMenuItem"); // NOI18N
        loadAbscissaMenuItem.addActionListener(formListener);
        loadAbscissaMenu.add(loadAbscissaMenuItem);

        loadAbscissaProcparMenuItem.setText("From Procpar");
        loadAbscissaProcparMenuItem.setName("loadAbscissaProcparMenuItem"); // NOI18N
        loadAbscissaProcparMenuItem.addActionListener(formListener);
        loadAbscissaMenu.add(loadAbscissaProcparMenuItem);

        fileMenu.add(loadAbscissaMenu);

        downloadTestDataMenuItem.setText("Download Test Data");
        downloadTestDataMenuItem.setName("downloadTestDataMenuItem"); // NOI18N
        downloadTestDataMenuItem.addActionListener(formListener);
        fileMenu.add(downloadTestDataMenuItem);

        downloadManualMenuItem.setText("Download Manual (pdf)");
        downloadManualMenuItem.setName("downloadManualMenuItem"); // NOI18N
        downloadManualMenuItem.addActionListener(formListener);
        fileMenu.add(downloadManualMenuItem);

        jSeparator6.setName("jSeparator6"); // NOI18N
        fileMenu.add(jSeparator6);

        jsaveExperimentMenuItem.setText("Save Working Directory");
        jsaveExperimentMenuItem.setName("jsaveExperimentMenuItem"); // NOI18N
        jsaveExperimentMenuItem.addActionListener(formListener);
        fileMenu.add(jsaveExperimentMenuItem);

        loadExperimentMenuItem.setText("Import Working Directory");
        loadExperimentMenuItem.setName("loadExperimentMenuItem"); // NOI18N
        loadExperimentMenuItem.addActionListener(formListener);
        fileMenu.add(loadExperimentMenuItem);

        bacthLoadWorkDirMenuItem.setText("Import Working Directories in Batch");
        bacthLoadWorkDirMenuItem.setName("bacthLoadWorkDirMenuItem"); // NOI18N
        bacthLoadWorkDirMenuItem.addActionListener(formListener);
        fileMenu.add(bacthLoadWorkDirMenuItem);

        jSeparator9.setName("jSeparator9"); // NOI18N
        fileMenu.add(jSeparator9);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(bayes.BayesApp.class).getContext().getActionMap(BayesView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        packageMenu.setText("Package");
        packageMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        packageMenu.setName("packageMenu"); // NOI18N
        packageMenu.addActionListener(formListener);

        exponentialMenuItem.setText("Exponential"); // NOI18N
        exponentialMenuItem.setName("exponentialMenuItem"); // NOI18N
        exponentialMenuItem.addActionListener(formListener);
        packageMenu.add(exponentialMenuItem);

        inversionRecoverylMenuItem.setText("Inversion Recovery"); // NOI18N
        inversionRecoverylMenuItem.setName("inversionRecoverylMenuItem"); // NOI18N
        inversionRecoverylMenuItem.addActionListener(formListener);
        packageMenu.add(inversionRecoverylMenuItem);

        diffusonTensorMenuItem.setText("Diffusion Tensor"); // NOI18N
        diffusonTensorMenuItem.setName("diffusonTensorMenuItem"); // NOI18N
        diffusonTensorMenuItem.addActionListener(formListener);
        packageMenu.add(diffusonTensorMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        packageMenu.add(jSeparator1);

        enterAsciiMenuItem.setText("Enter Ascii Model"); // NOI18N
        enterAsciiMenuItem.setName("enterAsciiMenuItem"); // NOI18N
        enterAsciiMenuItem.addActionListener(formListener);
        packageMenu.add(enterAsciiMenuItem);

        enterAsciiModelMenuItem.setText("Enter Ascii Model Selection");
        enterAsciiModelMenuItem.setName("enterAsciiModelMenuItem"); // NOI18N
        enterAsciiModelMenuItem.addActionListener(formListener);
        packageMenu.add(enterAsciiModelMenuItem);

        enterAsciiModelTestRadioButtonMenuItem.setText("Test Ascii Model");
        enterAsciiModelTestRadioButtonMenuItem.setName("enterAsciiModelTestRadioButtonMenuItem"); // NOI18N
        enterAsciiModelTestRadioButtonMenuItem.addActionListener(formListener);
        packageMenu.add(enterAsciiModelTestRadioButtonMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        packageMenu.add(jSeparator2);

        mzTMenuItem.setText("Magnetization Transfer"); // NOI18N
        mzTMenuItem.setName("mzTMenuItem"); // NOI18N
        mzTMenuItem.addActionListener(formListener);
        packageMenu.add(mzTMenuItem);

        mzTKineticsMenuItem.setText("Magnetization Transfer Kinetics"); // NOI18N
        mzTKineticsMenuItem.setName("mzTKineticsMenuItem"); // NOI18N
        mzTKineticsMenuItem.addActionListener(formListener);
        packageMenu.add(mzTKineticsMenuItem);

        mtZBigMenuItem.setText("Magnetization Transfer Big"); // NOI18N
        mtZBigMenuItem.setName("mtZBigMenuItem"); // NOI18N
        mtZBigMenuItem.addActionListener(formListener);
        packageMenu.add(mtZBigMenuItem);

        jSeparator5.setName("jSeparator5"); // NOI18N
        packageMenu.add(jSeparator5);

        bayesAnalyzeMenuItem.setText("Bayes Analyze");
        bayesAnalyzeMenuItem.setName("bayesAnalyzeMenuItem"); // NOI18N
        bayesAnalyzeMenuItem.addActionListener(formListener);
        packageMenu.add(bayesAnalyzeMenuItem);

        bayesWaterMenuItem.setText("Bayes Big Peak/Little Peak"); // NOI18N
        bayesWaterMenuItem.setName("bayesWaterMenuItem"); // NOI18N
        bayesWaterMenuItem.addActionListener(formListener);
        packageMenu.add(bayesWaterMenuItem);

        findResMenuItem.setText("Bayes Find Resonance");
        findResMenuItem.setName("findResMenuItem"); // NOI18N
        findResMenuItem.addActionListener(formListener);
        packageMenu.add(findResMenuItem);

        bayesMetaboliteMenuItem.setText("Bayes Metabolite");
        bayesMetaboliteMenuItem.setName("bayesMetaboliteMenuItem"); // NOI18N
        bayesMetaboliteMenuItem.addActionListener(formListener);
        packageMenu.add(bayesMetaboliteMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        packageMenu.add(jSeparator3);

        behrensFisherMenuItem.setText("Behrens-Fisher"); // NOI18N
        behrensFisherMenuItem.setName("behrensFisherMenuItem"); // NOI18N
        behrensFisherMenuItem.addActionListener(formListener);
        packageMenu.add(behrensFisherMenuItem);

        ErrInVarsGivenButtonMenuItem.setText("Errors In Variables Given"); // NOI18N
        ErrInVarsGivenButtonMenuItem.setName("ErrInVarsGivenButtonMenuItem"); // NOI18N
        ErrInVarsGivenButtonMenuItem.addActionListener(formListener);
        packageMenu.add(ErrInVarsGivenButtonMenuItem);

        polynomialMenuItem.setText("Polynomial Models "); // NOI18N
        polynomialMenuItem.setName("polynomialMenuItem"); // NOI18N
        polynomialMenuItem.addActionListener(formListener);
        packageMenu.add(polynomialMenuItem);

        redoDpCMenuItem.setText("Redor Dipolar Coupling"); // NOI18N
        redoDpCMenuItem.setName("redoDpCMenuItem"); // NOI18N
        redoDpCMenuItem.addActionListener(formListener);
        redoDpCMenuItem.setVisible(false);
        packageMenu.add(redoDpCMenuItem);

        jSeparator10.setName("jSeparator10"); // NOI18N
        packageMenu.add(jSeparator10);

        histogramMenuItem.setText("MaxEnt Histograms");
        histogramMenuItem.setName("histogramMenuItem"); // NOI18N
        histogramMenuItem.addActionListener(formListener);
        packageMenu.add(histogramMenuItem);

        binnedHistogramMenuItem.setText("Binned Histograms");
        binnedHistogramMenuItem.setName("binnedHistogramMenuItem"); // NOI18N
        binnedHistogramMenuItem.addActionListener(formListener);
        packageMenu.add(binnedHistogramMenuItem);

        densityEstimationKernelMenuItem.setText("Density Estimation Kernel"); // NOI18N
        densityEstimationKernelMenuItem.setName("densityEstimationKernelMenuItem"); // NOI18N
        densityEstimationKernelMenuItem.addActionListener(formListener);
        packageMenu.add(densityEstimationKernelMenuItem);
        //densityEstimationKernelMenuItem.setVisible(false);

        jSeparator4.setName("jSeparator4"); // NOI18N
        packageMenu.add(jSeparator4);

        bayesPhaseMenuItem.setText("Bayes Phase");
        bayesPhaseMenuItem.setName("bayesPhaseMenuItem"); // NOI18N
        bayesPhaseMenuItem.addActionListener(formListener);
        packageMenu.add(bayesPhaseMenuItem);
        //bayesPhaseMenuItemTest.setVisible(false);

        bayesPhaseNonLinearMenuItem.setText("Bayes Phase Nonlinear");
        bayesPhaseNonLinearMenuItem.setName("bayesPhaseNonLinearMenuItem"); // NOI18N
        bayesPhaseNonLinearMenuItem.addActionListener(formListener);
        packageMenu.add(bayesPhaseNonLinearMenuItem);

        analyzeImagePixelMenuItem.setText("Analyze Image Pixel");
        analyzeImagePixelMenuItem.setName("analyzeImagePixelMenuItem"); // NOI18N
        analyzeImagePixelMenuItem.addActionListener(formListener);
        packageMenu.add(analyzeImagePixelMenuItem);

        imageModelSelectionMenuItem.setText("Image Model Selection");
        imageModelSelectionMenuItem.setName("imageModelSelectionMenuItem"); // NOI18N
        imageModelSelectionMenuItem.addActionListener(formListener);
        packageMenu.add(imageModelSelectionMenuItem);

        imagePixelsUniqueMenuItem.setText("Analyze Image Pixel Unique"); // NOI18N
        imagePixelsUniqueMenuItem.setName("imagePixelsUniqueMenuItem"); // NOI18N
        imagePixelsUniqueMenuItem.addActionListener(formListener);
        packageMenu.add(imagePixelsUniqueMenuItem);

        menuBar.add(packageMenu);

        workDirMenu.setText("WorkDir"); // NOI18N
        workDirMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        workDirMenu.setName("workDirMenu"); // NOI18N
        modifyExperimentMenu(workDirMenu);

        new MenuScroller(workDirMenu);

        menuBar.add(workDirMenu);

        optionsMenu.setText("Settings");
        optionsMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        optionsMenu.setName("optionsMenu"); // NOI18N

        mcmcMenuItem.setText("MCMC Parameters");
        mcmcMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nAssign parameter values for Markov chain <br>\nMonte Carlo (MCMC) simulations \n\n</font></p><html>\n"); // NOI18N
        mcmcMenuItem.setName("mcmcMenuItem"); // NOI18N
        mcmcMenuItem.addActionListener(formListener);
        optionsMenu.add(mcmcMenuItem);

        setServersMenuItem.setText("Server Setup");
        setServersMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nManage and set up servers.\n\n\n</font></p><html>\n"); // NOI18N
        setServersMenuItem.setName("setServersMenuItem"); // NOI18N
        setServersMenuItem.addActionListener(formListener);
        optionsMenu.add(setServersMenuItem);

        windowSizeMEnu.setText("Set Window Size");
        windowSizeMEnu.setName("windowSizeMEnu"); // NOI18N
        windowSizeMEnu.addActionListener(formListener);
        optionsMenu.add(windowSizeMEnu);

        jSeparator8.setName("jSeparator8"); // NOI18N
        optionsMenu.add(jSeparator8);

        preferencesMenuItem.setText("Preferences");
        preferencesMenuItem.setName("preferencesMenuItem"); // NOI18N
        preferencesMenuItem.addActionListener(formListener);
        optionsMenu.add(preferencesMenuItem);

        menuBar.add(optionsMenu);

        utilitiesMenu.setText("Utilities");
        utilitiesMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        utilitiesMenu.setName("utilitiesMenu"); // NOI18N

        memoryMonitorMenuItem.setText("Memory Monitor");
        memoryMonitorMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nMonitor memory usage consumed by this application. \n\n</font></p><html>\n"); // NOI18N
        memoryMonitorMenuItem.setName("memoryMonitorMenuItem"); // NOI18N
        memoryMonitorMenuItem.addActionListener(formListener);
        utilitiesMenu.add(memoryMonitorMenuItem);

        systemInfoMenuItem.setText("System Info");
        systemInfoMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSystem information.\n\n</font></p><html>\n"); // NOI18N
        systemInfoMenuItem.setName("systemInfoMenuItem"); // NOI18N
        systemInfoMenuItem.addActionListener(formListener);
        utilitiesMenu.add(systemInfoMenuItem);

        checkSoftwareUpdateMenuItem.setText("Check for Update");
        checkSoftwareUpdateMenuItem.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nCheck for software updates for all currently set-up servers.\n</font></p><html>\n"); // NOI18N
        checkSoftwareUpdateMenuItem.setName("checkSoftwareUpdateMenuItem"); // NOI18N
        checkSoftwareUpdateMenuItem.addActionListener(formListener);
        utilitiesMenu.add(checkSoftwareUpdateMenuItem);

        menuBar.add(utilitiesMenu);

        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Helvetica", 1, 14));
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem1.setText("Release Notes");
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(formListener);
        helpMenu.add(jMenuItem1);

        manualMenuItem.setText("Online Manual");
        manualMenuItem.setName("manualMenuItem"); // NOI18N
        manualMenuItem.addActionListener(formListener);
        helpMenu.add(manualMenuItem);

        homepageMenuItem.setText("Bayes Analysis Home Page");
        homepageMenuItem.setName("homepageMenuItem"); // NOI18N
        homepageMenuItem.addActionListener(formListener);
        helpMenu.add(homepageMenuItem);

        contactUsMenu.setText("Contact us");
        contactUsMenu.setName("contactUsMenu"); // NOI18N
        contactUsMenu.addActionListener(formListener);
        helpMenu.add(contactUsMenu);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.setVisible(false);

        menuBar.add(helpMenu);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.gray, java.awt.Color.lightGray));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.LINE_AXIS));

        startJobMonitorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/eye.png"))); // NOI18N
        startJobMonitorButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nStart monitoring job submitted to server.<br>\nIf no job is submitted, nothing is executed.<br>\nJob monitoring includes requesting job status<br>\nfrom server in periodic fashion on the background<br>\nthread. Update  frequency can be set by user. <br>\n\n</font></p><html>"); // NOI18N
        startJobMonitorButton.setName("startJobMonitorButton"); // NOI18N
        startJobMonitorButton.addActionListener(formListener);
        statusPanel.add(startJobMonitorButton);

        startJobMonitorButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/clearMemmory.png"))); // NOI18N
        startJobMonitorButton1.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nView and monitor memory allocated to current application.<br>\nMemory deficiency can significantly degrade application performance.<br>\nIn the popup widget, you can try to run java \"garbage collector\"<br>\nin effort to expand available memory.\n\n</font></p><html>"); // NOI18N
        startJobMonitorButton1.setName("startJobMonitorButton1"); // NOI18N
        startJobMonitorButton1.addActionListener(formListener);
        statusPanel.add(startJobMonitorButton1);

        takeApplicationWindowScreenShot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/camera.jpg"))); // NOI18N
        takeApplicationWindowScreenShot.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nTake a snaphot of the entire application window.<br>\nScreen shots can be saved in \"png\" (default), \"jpg\" or \"gif\" formats.<br>\nNote: screen shots may not work in remote<br>\ndesktop enviroment.\n\n</font></p><html>"); // NOI18N
        takeApplicationWindowScreenShot.setName("takeApplicationWindowScreenShot"); // NOI18N
        takeApplicationWindowScreenShot.addActionListener(formListener);
        statusPanel.add(takeApplicationWindowScreenShot);

        currentViewerScreenShot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/cameraVewable.png"))); // NOI18N
        currentViewerScreenShot.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\n\nTake a snaphot of currently displayed viewer.<br>\nIf viewer contains plot (or image), screenshot of a plot (or image) is taken.<br>\nScreen shots can be saved in \"png\" (default), \"jpg\" or \"gif\" formats.<br>\nNote: screen shots may not work in remote<br>\ndesktop enviroment.\n\n</font></p><html>"); // NOI18N
        currentViewerScreenShot.setName("currentViewerScreenShot"); // NOI18N
        currentViewerScreenShot.addActionListener(formListener);
        statusPanel.add(currentViewerScreenShot);

        packageButtonGroup = makePackageButtonGroup();

        packageButtonGroup.add(dummyMenuItem);
        dummyMenuItem.setSelected(true);
        dummyMenuItem.setText("RadioButton");
        dummyMenuItem.setName("dummyMenuItem"); // NOI18N

        contactTimeMenuItem.setText("Contact Time"); // NOI18N
        contactTimeMenuItem.setName("contactTimeMenuItem"); // NOI18N
        contactTimeMenuItem.addActionListener(formListener);

        menuBarMessage.setFont(new java.awt.Font("Helvetica", 1, 14));
        menuBarMessage.setForeground(new java.awt.Color(0, 0, 153));
        menuBarMessage.setName("menuBarMessage"); // NOI18N
        menuBar.add( Box.createHorizontalStrut(70));
        menuBar.add(jSeparator);

        menuBar.add( Box.createHorizontalStrut(70));

        menuBar.add(menuBarMessage);
        jSeparator.setVisible(false);

        jSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator.setMaximumSize(new java.awt.Dimension(30, 32767));
        jSeparator.setName("jSeparator"); // NOI18N
        jSeparator.setPreferredSize(new java.awt.Dimension(20, 0));

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == loadAsciiFromFileMenuItem) {
                BayesView.this.loadAsciiFromFileMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadAsciiFromBayesAnalyzeMenuItem2) {
                BayesView.this.loadAsciiFromBayesAnalyzeMenuItem2ActionPerformed(evt);
            }
            else if (evt.getSource() == loadFidDataMenuItem) {
                BayesView.this.loadFidDataMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadSiemensRdaDataMenuItem) {
                BayesView.this.loadSiemensRdaDataMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadSiemensRawDataMenuItem) {
                BayesView.this.loadSiemensRawDataMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == text2FidMenuItem) {
                BayesView.this.text2FidMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == BrukerBinaryMenuItem) {
                BayesView.this.BrukerBinaryMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == varianBinaryMenuItem) {
                BayesView.this.varianBinaryMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == text2FidImageMenuItem) {
                BayesView.this.text2FidImageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == fdf2imageMenuItem) {
                BayesView.this.fdf2imageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == imgBinaryMenuItem) {
                BayesView.this.imgBinaryMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == rawBinaryMenuItem) {
                BayesView.this.rawBinaryMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == singled2seqMenuItem) {
                BayesView.this.singled2seqMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == multiple2seqMenuItem) {
                BayesView.this.multiple2seqMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == text2imageMenuItem) {
                BayesView.this.text2imageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == textMultiColumn2imageMenuItem) {
                BayesView.this.textMultiColumn2imageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == ima2imageMenuItem) {
                BayesView.this.ima2imageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == DicomMenuItem) {
                BayesView.this.DicomMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == jpgMenuItem) {
                BayesView.this.jpgMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadAbscissaMenuItem) {
                BayesView.this.loadAbscissaMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadAbscissaProcparMenuItem) {
                BayesView.this.loadAbscissaProcparMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == downloadTestDataMenuItem) {
                BayesView.this.downloadTestDataMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == downloadManualMenuItem) {
                BayesView.this.downloadManualMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == jsaveExperimentMenuItem) {
                BayesView.this.jsaveExperimentMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == loadExperimentMenuItem) {
                BayesView.this.loadExperimentMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == bacthLoadWorkDirMenuItem) {
                BayesView.this.bacthLoadWorkDirMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == packageMenu) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == exponentialMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == inversionRecoverylMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == diffusonTensorMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == enterAsciiMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == enterAsciiModelMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == enterAsciiModelTestRadioButtonMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == mzTMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == mzTKineticsMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == mtZBigMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == bayesAnalyzeMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == bayesWaterMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == findResMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == bayesMetaboliteMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == behrensFisherMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == ErrInVarsGivenButtonMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == polynomialMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == redoDpCMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == histogramMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == binnedHistogramMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == densityEstimationKernelMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == bayesPhaseMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == bayesPhaseNonLinearMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == analyzeImagePixelMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == imageModelSelectionMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == imagePixelsUniqueMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
            else if (evt.getSource() == mcmcMenuItem) {
                BayesView.this.mcmcMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == setServersMenuItem) {
                BayesView.this.setServersMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == windowSizeMEnu) {
                BayesView.this.windowSizeMEnuActionPerformed(evt);
            }
            else if (evt.getSource() == preferencesMenuItem) {
                BayesView.this.preferencesMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == memoryMonitorMenuItem) {
                BayesView.this.memoryMonitorMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == systemInfoMenuItem) {
                BayesView.this.systemInfoMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == checkSoftwareUpdateMenuItem) {
                BayesView.this.checkSoftwareUpdateMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == jMenuItem1) {
                BayesView.this.jMenuItem1ActionPerformed(evt);
            }
            else if (evt.getSource() == manualMenuItem) {
                BayesView.this.manualMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == homepageMenuItem) {
                BayesView.this.homepageMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == contactUsMenu) {
                BayesView.this.contactUsMenuActionPerformed(evt);
            }
            else if (evt.getSource() == startJobMonitorButton) {
                BayesView.this.startJobMonitorButtonActionPerformed(evt);
            }
            else if (evt.getSource() == startJobMonitorButton1) {
                BayesView.this.startJobMonitorButton1ActionPerformed(evt);
            }
            else if (evt.getSource() == takeApplicationWindowScreenShot) {
                BayesView.this.takeApplicationWindowScreenShotActionPerformed(evt);
            }
            else if (evt.getSource() == currentViewerScreenShot) {
                BayesView.this.currentViewerScreenShotActionPerformed(evt);
            }
            else if (evt.getSource() == contactTimeMenuItem) {
                BayesView.this.packageMenuActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    // menu action performed
private void packageMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageMenuActionPerformed
     packageMenuAction(evt);
}//GEN-LAST:event_packageMenuActionPerformed
private void startJobMonitorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startJobMonitorButtonActionPerformed
    JRun.startJobMonitor();
}//GEN-LAST:event_startJobMonitorButtonActionPerformed
private void startJobMonitorButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startJobMonitorButton1ActionPerformed
    interfacebeans.MemoryViewer.showMemoryMonitor();
}//GEN-LAST:event_startJobMonitorButton1ActionPerformed
private void takeApplicationWindowScreenShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_takeApplicationWindowScreenShotActionPerformed
    Screenshot.captureScreenShotAndDisplay(getFrame(), "BayesApplication");
}//GEN-LAST:event_takeApplicationWindowScreenShotActionPerformed
private void currentViewerScreenShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentViewerScreenShotActionPerformed
   getAllViewerScreenShot();
}//GEN-LAST:event_currentViewerScreenShotActionPerformed

    private void contactUsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactUsMenuActionPerformed

        sendEmail("gbretthorst@wustl.edu");     }//GEN-LAST:event_contactUsMenuActionPerformed

    private void homepageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homepageMenuItemActionPerformed

        launchLink("http://bayesiananalysis.wustl.edu");     }//GEN-LAST:event_homepageMenuItemActionPerformed

    private void manualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualMenuItemActionPerformed

        launchLink("http://bayes.wustl.edu/Manual/BayesManual.pdf");     }//GEN-LAST:event_manualMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        linkToHomePage();     }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void checkSoftwareUpdateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSoftwareUpdateMenuItemActionPerformed

        JSoftwareUpdates updatePane = JSoftwareUpdates.showDialog();     }//GEN-LAST:event_checkSoftwareUpdateMenuItemActionPerformed

    private void systemInfoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemInfoMenuItemActionPerformed

        showSystemInfo();     }//GEN-LAST:event_systemInfoMenuItemActionPerformed

    private void memoryMonitorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memoryMonitorMenuItemActionPerformed

        interfacebeans.MemoryViewer.showMemoryMonitor();     }//GEN-LAST:event_memoryMonitorMenuItemActionPerformed

    private void preferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuItemActionPerformed

        JPreferences.display();     }//GEN-LAST:event_preferencesMenuItemActionPerformed

    private void windowSizeMEnuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowSizeMEnuActionPerformed

        windowSize();     }//GEN-LAST:event_windowSizeMEnuActionPerformed

    private void setServersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setServersMenuItemActionPerformed

        JEditServers.showDialog();     }//GEN-LAST:event_setServersMenuItemActionPerformed

    private void mcmcMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcmcMenuItemActionPerformed

        JSetMCMC.showDialog();     }//GEN-LAST:event_mcmcMenuItemActionPerformed

    private void bacthLoadWorkDirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bacthLoadWorkDirMenuItemActionPerformed

        batchLoadWorkDir();     }//GEN-LAST:event_bacthLoadWorkDirMenuItemActionPerformed

    private void loadExperimentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadExperimentMenuItemActionPerformed

        loadWorkDir();     }//GEN-LAST:event_loadExperimentMenuItemActionPerformed

    private void jsaveExperimentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsaveExperimentMenuItemActionPerformed

        SaveExperimentGUI.showDialog();     }//GEN-LAST:event_jsaveExperimentMenuItemActionPerformed

    private void downloadManualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadManualMenuItemActionPerformed

        downloadManual();     }//GEN-LAST:event_downloadManualMenuItemActionPerformed

    private void downloadTestDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadTestDataMenuItemActionPerformed

        downloadTestData();     }//GEN-LAST:event_downloadTestDataMenuItemActionPerformed

    private void loadAbscissaProcparMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAbscissaProcparMenuItemActionPerformed

        LoadAndViewData.loadAbscissaFromProcpar();     }//GEN-LAST:event_loadAbscissaProcparMenuItemActionPerformed

    private void loadAbscissaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAbscissaMenuItemActionPerformed

        DataLoader.loadAbscissa();     }//GEN-LAST:event_loadAbscissaMenuItemActionPerformed

    private void DicomMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DicomMenuItemActionPerformed

        DataLoader.loadDicom();     }//GEN-LAST:event_DicomMenuItemActionPerformed

    private void ima2imageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ima2imageMenuItemActionPerformed

        DataLoader.loadImaImage();     }//GEN-LAST:event_ima2imageMenuItemActionPerformed

    private void textMultiColumn2imageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textMultiColumn2imageMenuItemActionPerformed

        DataLoader.loadMultiCoumnAsciiImage();     }//GEN-LAST:event_textMultiColumn2imageMenuItemActionPerformed

    private void text2imageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text2imageMenuItemActionPerformed

        DataLoader.loadSingleCoumnAsciiImage();     }//GEN-LAST:event_text2imageMenuItemActionPerformed

    private void singled2seqMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singled2seqMenuItemActionPerformed

        DataLoader.loadBruker2dseq();     }//GEN-LAST:event_singled2seqMenuItemActionPerformed

    private void rawBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawBinaryMenuItemActionPerformed

        DataLoader.loadGeneralBinary();     }//GEN-LAST:event_rawBinaryMenuItemActionPerformed

    private void imgBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgBinaryMenuItemActionPerformed

        DataLoader.loadImgBinary();     }//GEN-LAST:event_imgBinaryMenuItemActionPerformed

    private void fdf2imageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fdf2imageMenuItemActionPerformed

        DataLoader.loadFdfImage();     }//GEN-LAST:event_fdf2imageMenuItemActionPerformed

    private void text2FidImageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text2FidImageMenuItemActionPerformed

        DataLoader.loadTextFidImage();     }//GEN-LAST:event_text2FidImageMenuItemActionPerformed

    private void varianBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_varianBinaryMenuItemActionPerformed

        VarianBinaryConverter.chooseAndLoadVarianImage();     }//GEN-LAST:event_varianBinaryMenuItemActionPerformed

    private void BrukerBinaryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrukerBinaryMenuItemActionPerformed

        brukerMenuAction(evt);     }//GEN-LAST:event_BrukerBinaryMenuItemActionPerformed

    private void text2FidMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text2FidMenuItemActionPerformed

        DataLoader.loadTextFid();     }//GEN-LAST:event_text2FidMenuItemActionPerformed

    private void loadSiemensRawDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSiemensRawDataMenuItemActionPerformed

        DataLoader.loadSiemenesRawFid();     }//GEN-LAST:event_loadSiemensRawDataMenuItemActionPerformed

    private void loadSiemensRdaDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSiemensRdaDataMenuItemActionPerformed

        DataLoader.loadSiemenesRdaFid();     }//GEN-LAST:event_loadSiemensRdaDataMenuItemActionPerformed

    private void loadFidDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFidDataMenuItemActionPerformed

        DataLoader.loadSpectralFid();     }//GEN-LAST:event_loadFidDataMenuItemActionPerformed

    private void loadAsciiFromBayesAnalyzeMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAsciiFromBayesAnalyzeMenuItem2ActionPerformed

        JGetBayesAnalyzePeak.showDialog();     }//GEN-LAST:event_loadAsciiFromBayesAnalyzeMenuItem2ActionPerformed

    private void loadAsciiFromFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAsciiFromFileMenuItemActionPerformed

        DataLoader.loadAscii();     }//GEN-LAST:event_loadAsciiFromFileMenuItemActionPerformed

    private void multiple2seqMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiple2seqMenuItemActionPerformed
       
        DataLoader.loadBruker2dseqStack(this.getFrame());
    }//GEN-LAST:event_multiple2seqMenuItemActionPerformed

    private void jpgMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jpgMenuItemActionPerformed
       DataLoader.loadCommonImage();
    }//GEN-LAST:event_jpgMenuItemActionPerformed


/****** actions start ************/

private void packageMenuAction(java.awt.event.ActionEvent evt){
    if (evt.getSource().equals(dummyMenuItem)) {
        return;
    }
    /*
    // if currently any job is running - warn user.
    if (preservePrevioslySubmittedJob() == true){
    selectedPackageMenutItem.setSelected(true);
    return;
    }
     */

    // Update selected menu item information
    selectedPackageMenutItem = (JRadioButtonMenuItem) evt.getSource();

    //make sure we have all directories in place
    DirectoryManager.doAnalaysisDir();

    // Delete serialization file.
    // We are switching package - so it is always safe.
    DirectoryManager.getSerializationFile().delete();


    // Clear Menu Bar Message
    this.setMenuBarMsg("");

    // Get model descriptor for selected package menu item.
    // Construct and display model.
    ModelDescriptor modelDescriptor = (ModelDescriptor) getModelDescrptorMap().get(selectedPackageMenutItem);
    setModel(modelDescriptor);
}
private void brukerMenuAction(java.awt.event.ActionEvent evt){
     JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(DirectoryManager.startDir);
    fc.setMultiSelectionEnabled(false);
    fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
    //fc.setFileFilter( new utilities.BayesFileFilters. ImageFileChooserFilter ());
    fc.setDialogTitle("Load Bruker FID");

    int returnVal = fc.showOpenDialog(null);

    if (returnVal == JFileChooser.APPROVE_OPTION) {

        try {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = fc.getSelectedFile();

            // if (file.isDirectory()){}
            LoadAndViewData.loadBrukerFid(file);



        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }


    } else {
        return;
    }

    DirectoryManager.startDir = fc.getCurrentDirectory();
   
}
private void downloadTestData(){
     Server server = JServer.getInstance().getServer();
    DownloadSampleFiles.execute(server);
}
private void linkToHomePage(){
       StringBuilder sb = new StringBuilder();
    sb.append("http://bayesiananalysis.wustl.edu/");
    sb.append(BayesManager.APPLICATION_VERSION);
    sb.append(".html");

    launchLink(sb.toString());
}
private void showSystemInfo(){
        String sysInfo = SystemProperties.getSpecificSystemProperties();
    Viewer.display(sysInfo, "System Information", false);
}
private void windowSize(){
            SingleFrameApplication application = (SingleFrameApplication) this.getApplication();
        JFrame frame = application.getMainFrame();

        JFrameSize.display(frame);
   
}
private void downloadManual(){
          Server server = JServer.getInstance().getServer();
    String url = server.getManualURL();
    DownloadManual serverDownload = new DownloadManual(url);
    serverDownload.setDownloadDesciptor("Bayes Manual");
    DownloadManual.execute(serverDownload);
}
private void getAllViewerScreenShot(){
            Component screenshotCompt = AllViewers.getInstance().getCurrentViewable();
    if (screenshotCompt != null) {
        screenshotCompt = AllViewers.getInstance().getCurrentViewable();
    }

    if (screenshotCompt != null && screenshotCompt.isShowing()) {
        Screenshot.captureScreenShotAndDisplay(screenshotCompt, "BayesScreenShot");
    } else {
        DisplayText.popupErrorMessage("No Viewer is currently displayed.");
    }
}
private void batchLoadWorkDir(){
           // record wurrent working information before the any loading
    File before = DirectoryManager.getSerializationFile();
    boolean existBefore = (before != null && before.exists());
    long modTimeBefore = (existBefore) ? before.lastModified() : 0L;

    JSeekAndLoadWorkDirJDialog leg = JSeekAndLoadWorkDirJDialog.showDialog();

    File after = DirectoryManager.getSerializationFile();
    boolean existAfter = (after != null && after.exists());
    long modTimeAfter = (existAfter) ? after.lastModified() : modTimeBefore;

    modifyExperimentMenu(getWorkDirMenu());

    boolean curDirHasChanged = (modTimeBefore != modTimeAfter);
    if (curDirHasChanged) {
        String cwd = DirectoryManager.getExperimentDirName();
        System.out.println("Loading " + cwd);
        loadDir(cwd);
    }
}
private void loadWorkDir(){
        // record wurrent working information before the any loading
    File before = DirectoryManager.getSerializationFile();
    boolean existBefore = (before != null && before.exists());
    long modTimeBefore = (existBefore) ? before.lastModified() : 0L;

    LoadExperimentGui leg = LoadExperimentGui.showDialog();

    File after = DirectoryManager.getSerializationFile();
    boolean existAfter = (after != null && after.exists());
    long modTimeAfter = (existAfter) ? after.lastModified() : modTimeBefore;

    modifyExperimentMenu(getWorkDirMenu());

    boolean curDirHasChanged = (modTimeBefore != modTimeAfter);
    if (curDirHasChanged) {
        String cwd = DirectoryManager.getExperimentDirName();
        System.out.println("Loading " + cwd);
        loadDir(cwd);
    }
}



/****** actions stop ************/
private void expMenuItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
            return;
        }
        JRadioButtonMenuItem m = (JRadioButtonMenuItem) evt.getSource();
        String dir = m.getText();
        changeWorkDir(dir);


    }

    private void launchLink(String link) {
        Desktop desktop = null;
        URI uri = null;


        try {
            boolean isDesktop = Desktop.isDesktopSupported();
            if (isDesktop == false) {
                DisplayText.popupErrorMessage("Java Desktop is not supported by your OS. Exit...");

            } else {

                desktop = Desktop.getDesktop();
                uri = new URI(link);
                desktop.browse(uri);
            }


        } catch (Exception e) {
            System.err.println("Failed to launch link " + link + " using Desktop function.");
            e.printStackTrace();
            BrowserLaunch.openURL(link);
        } finally {
        }




    }
    private void sendEmail(String email) {
        Desktop desktop = null;


        try {
            boolean isDesktop = Desktop.isDesktopSupported();
            if (isDesktop == false) {
                DisplayText.popupErrorMessage("Java Desktop is not supported by your OS. Exit...");

            } else {
                desktop = Desktop.getDesktop();

                if (email != null && email.length() > 0) {
                    URI uriMailTo = new URI("mailto", email, null);
                    desktop.mail(uriMailTo);
                } else {
                    desktop.mail();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }




    }
    void changeWorkDir(String newdir) {
        // Deserialize model in the "old" directory
        Model oldModel = PackageManager.getCurrentApplication();
        if (oldModel != null) {
            DirectoryManager.leaveExperiment();
        }

        loadDir(newdir);

    }
    void loadDir(String dir) {
        // set new directory
        DirectoryManager.move2Experiment(dir);

        // deserilize modelDescriptor in new directory
        ModelDescriptor modelDescriptor = bayes.Serialize.getDeserializedModelDescriptor();

        if (modelDescriptor == null) {
            doNoModelSelected("Please Select The Package");
        } else {
            checkSelectedPackage(modelDescriptor);
        }
    }
    void killCurrentModel() {
        mainPanel.removeAll();
        Model model = PackageManager.getCurrentApplication();
        if (model != null) {
            killModel(model);
            setMenuBarMsg("");
            // DirectoryManager.cleanCurrentDir();
        }
    }
    void killModel(Model model) {
        try {
            if (model == null) {
                return;
            }
            if (model instanceof java.beans.PropertyChangeListener) {
                PropertyChangeListener listener = (PropertyChangeListener) model;
                BayesManager.pcs.removePropertyChangeListener(listener);
                listener = null;
            }



            PropertyChangeListener[] lis = BayesManager.pcs.getPropertyChangeListeners();
            for (PropertyChangeListener listener : lis) {
                if (listener == this) {
                    continue;
                }
                BayesManager.pcs.removePropertyChangeListener(listener);
                listener = null;
            }

            // make sure JServer is set to zero
            // otherwist it is not being deserialized correctly
            JServer.reset();

            // make sure that static variables in Run are set
            // as status = NotRun and jobID = null
            JRun.setNotRun();



            AllViewers.reset();
            if (model instanceof JPanel) {
                JPanel pane = (JPanel) model;
                pane.removeAll();
                pane = null;
            }
            model = null;
            PackageManager.setCurrentApplication(null);

        } finally {
            setActive(true);
        }


    }
    void updateExperimentChange(JRadioButtonMenuItem menuItem) {
        menuItem.setSelected(true);
        selectedPackageMenutItem = menuItem;
        ModelDescriptor modelDescriptor = (ModelDescriptor) getModelDescrptorMap().get(menuItem);
        setModel(modelDescriptor);
    }
    void checkSelectedPackage(ModelDescriptor modelDescriptor) {
        if (debug) {
            System.out.println("Start loading package " + modelDescriptor.getModelClass());
        }

        Class modelClass = modelDescriptor.getModelClass();
        String constrArg = modelDescriptor.getConstrArg();

        if (debug) {
            System.out.println("Constructor argument is: " + constrArg);
        }

        if (modelDescriptor == null) {
            dummyMenuItem.doClick();
            return;
        }

        Set<JRadioButtonMenuItem> mitems = getModelDescrptorMap().keySet();


        for (JRadioButtonMenuItem mi : mitems) {
            ModelDescriptor md = getModelDescrptorMap().get(mi);

            if (modelClass.equals(md.getModelClass())) {

                // check if  it is an acsii preloaded packages
                if (modelClass.equals(BayesEnterAsciiPreloaded.class)) {

                    if (constrArg.equalsIgnoreCase(md.getConstrArg())) {
                        updateExperimentChange(mi);
                        return;
                    }

                } else {
                    updateExperimentChange(mi);
                    return;
                }

            }

        }


    }
    void setMainFrameTitle() {
        String title = "";

        File file = DirectoryManager.getExperimentDir();
        Model model = PackageManager.getCurrentApplication();
        if (model != null && model.getExtendedProgramName() != null) {
            title += "Package: " + model.getExtendedProgramName();
            title += "       ";
        }
        if (file != null) {
            title += "WorkDir: " + file.getName();
            title += "       ";
        }

        String host = SystemProperties.getHost();
        if (host != null) {
            title += "Host: " + host + "";
        }

        setMainFrameTitle(title);

    }
    void setMainFrameTitle(String title) {
        JFrame mainFrame = BayesApp.getApplication().getMainFrame();
        mainFrame.setTitle(title);
    }
    public void setMenuBarMsg(String msg) {
        boolean visibleText = !msg.isEmpty();
        //   jSeparator.setVisible(visibleText);
        this.menuBarMessage.setText(msg);

    }
    public String getMenuBarMsg() {
        return menuBarMessage.getText();

    }
    public static void setMenuBarMessage(String msg) {
        BayesView bw = BayesApp.getApplication().bayesView;
        if (bw != null) {
            bw.setMenuBarMsg(msg);
        }


    }
    public static String getMenuBarMessage() {
        String out = "";
        try {
            BayesView bw = BayesApp.getApplication().bayesView;
            if (bw != null) {
                out = bw.getMenuBarMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return out;
        }
    }
    boolean makeAndDisplayCurrentModel(ModelDescriptor modelDescriptor) {
        Model model = null;
        Class modelClass = modelDescriptor.getModelClass();
        System.out.println("Model class is " + modelClass);
        //  ApplicationPreferences.getBayesHomeDir();
        try {

            // if model is NOT of BayesEnterAscii family invoke single argument constrictor
            if (modelClass.equals(BayesEnterAsciiPreloaded.class) == false) {

                Constructor constr = modelClass.getConstructor();
                if (debug) {
                    System.out.println("Start constructing package ");
                }
                model = (Model) constr.newInstance();
                if (debug) {
                    System.out.println("Package initialization is complete.");
                }
            } // if model IS FROM BayesEnterAscii family invoke single or two-argument constrictor
            else {


                String constArg = modelDescriptor.getConstrArg();
                if (constArg == null) {
                    Constructor constr = modelClass.getConstructor();
                    model = (Model) constr.newInstance();
                } else {
                    Constructor constr = modelClass.getConstructor(String.class);
                    model = (Model) constr.newInstance(constArg);
                }
            }


            // make sure preloaded packages were loaded
            // For isntance these packages can not be loaded offline
            if (model instanceof BayesEnterAsciiPreloaded) {
                boolean isloaded = ((BayesEnterAsciiPreloaded) model).isLoaded();
                if (isloaded == false) {
                    return false;
                }
            }




        } catch (java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            String errormessage = String.format(
                    "Error is encountered while trying to construct package.\n"
                    + "Error type is : \"java.lang.reflect.InvocationTargetException\"\n"
                    + "Error message is : %s.\n"
                    + "Exiting...",
                    e.getMessage());
            DisplayText.popupErrorMessage(errormessage);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            selectedPackageMenutItem.setSelected(true);
            return false;
        }

        mainPanel.add((javax.swing.JPanel) model);
        mainPanel.revalidate();
        mainPanel.repaint();


        // this line is not neccessary, since during construction
        // model is set be PackageManager.currentApplication
        PackageManager.setCurrentApplication(model);
        setMainFrameTitle();
        return true;

    }
    void doNoModelSelected(String message) {
        dummyMenuItem.doClick();

        killCurrentModel();
        setAcitveLoadButtons(false);


        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();


        JFrame mainFrame = BayesApp.getApplication().getMainFrame();
        mainFrame.setTitle(message);
    }
    boolean preservePrevioslySubmittedJob() {
        boolean preserve = !JCleanJobRequest.cleanSubmittedJobAndSwitchPackage();
        if (preserve) {
            if (selectedPackageMenutItem != null) {
                selectedPackageMenutItem.setSelected(true);
            }
        }

        return preserve;
    }
    JMenu modifyExperimentMenu(JMenu menu) {
        List<String> dirs = ApplicationPreferences.getWorkDirs();
        String curDir = ApplicationPreferences.getCurrentWorkDir();
        ButtonGroup expButtonGroup = new ButtonGroup();

        menu.removeAll();

        // populated experiments
        for (String string : dirs) {

            JMenuItem menuItem = new JRadioButtonMenuItem();
            menuItem.setText(string);
            expButtonGroup.add(menuItem);
            if (string.equalsIgnoreCase(curDir)) {
                menuItem.setSelected(true);
            }
            menu.add(menuItem);
            menuItem.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    expMenuItemStateChanged(evt);
                }
            });
        }

        menu.add(new JSeparator());

        // add menu item to edit experiment list
        JMenuItem editExpMenuItem = new JMenuItem();
        editExpMenuItem.setAction(getAction("popupEditExperiments"));
        editExpMenuItem.setText("Edit");
        menu.add(editExpMenuItem);

        return menu;
    }
    private ButtonGroup makePackageButtonGroup() {
        ButtonGroup bg = new javax.swing.ButtonGroup();
        List<JRadioButtonMenuItem> list = new ArrayList<JRadioButtonMenuItem>();
        JMenu menu = getPackageMenu();


        list = getRadioButtonMenues(menu, list);

        for (JRadioButtonMenuItem jRadioButtonMenuItem : list) {
            bg.add(jRadioButtonMenuItem);
        }

        return bg;
    }
    public static List<JRadioButtonMenuItem> getRadioButtonMenues(JMenu menu, List<JRadioButtonMenuItem> list) {

        Component[] menuItems = menu.getMenuComponents();

        for (Component component : menuItems) {
            if (component instanceof JRadioButtonMenuItem) {
                list.add((JRadioButtonMenuItem) component);
            } else if (component instanceof JMenu) {
                JMenu nestedmenu = (JMenu) component;
                getRadioButtonMenues(nestedmenu, list);
            }
        }

        return list;
    }
    private void setAcitveLoadButtons(boolean isActive) {

        getLoadAsciiMenu().setEnabled(isActive);
        getLoadImageMenu().setEnabled(isActive);
        getLoadFidMenu().setEnabled(isActive);

        getLoadAbscissaMenu().setEnabled(isActive);
        getLoadAbscissaMenuItem().setEnabled(isActive);
        getLoadAbscissaProcparMenuItem().setEnabled(isActive);

        getMcmcMenuItem().setEnabled(isActive);
        getSetServersMenuItem().setEnabled(isActive);

    }

    private void setActive(boolean active) {
        getPackageMenu().setEnabled(active);
        getFileMenu().setEnabled(active);
        getOptionsMenu().setEnabled(active);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem BrukerBinaryMenuItem;
    private javax.swing.JMenuItem DicomMenuItem;
    private javax.swing.JRadioButtonMenuItem ErrInVarsGivenButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem analyzeImagePixelMenuItem;
    private javax.swing.JMenuItem bacthLoadWorkDirMenuItem;
    private javax.swing.JRadioButtonMenuItem bayesAnalyzeMenuItem;
    private javax.swing.JRadioButtonMenuItem bayesMetaboliteMenuItem;
    private javax.swing.JRadioButtonMenuItem bayesPhaseMenuItem;
    private javax.swing.JRadioButtonMenuItem bayesPhaseNonLinearMenuItem;
    private javax.swing.JRadioButtonMenuItem bayesWaterMenuItem;
    private javax.swing.JRadioButtonMenuItem behrensFisherMenuItem;
    private javax.swing.JRadioButtonMenuItem binnedHistogramMenuItem;
    private javax.swing.JMenuItem checkSoftwareUpdateMenuItem;
    private javax.swing.JRadioButtonMenuItem contactTimeMenuItem;
    private javax.swing.JMenuItem contactUsMenu;
    private javax.swing.JButton currentViewerScreenShot;
    private javax.swing.JRadioButtonMenuItem densityEstimationKernelMenuItem;
    private javax.swing.JRadioButtonMenuItem diffusonTensorMenuItem;
    private javax.swing.JMenuItem downloadManualMenuItem;
    private javax.swing.JMenuItem downloadTestDataMenuItem;
    private javax.swing.JRadioButtonMenuItem dummyMenuItem;
    private javax.swing.JRadioButtonMenuItem enterAsciiMenuItem;
    private javax.swing.JRadioButtonMenuItem enterAsciiModelMenuItem;
    private javax.swing.JRadioButtonMenuItem enterAsciiModelTestRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem exponentialMenuItem;
    private javax.swing.JMenuItem fdf2imageMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JRadioButtonMenuItem findResMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JRadioButtonMenuItem histogramMenuItem;
    private javax.swing.JMenuItem homepageMenuItem;
    private javax.swing.JMenuItem ima2imageMenuItem;
    private javax.swing.JRadioButtonMenuItem imageModelSelectionMenuItem;
    private javax.swing.JRadioButtonMenuItem imagePixelsUniqueMenuItem;
    private javax.swing.JMenuItem imgBinaryMenuItem;
    private javax.swing.JRadioButtonMenuItem inversionRecoverylMenuItem;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuItem jpgMenuItem;
    private javax.swing.JMenuItem jsaveExperimentMenuItem;
    private javax.swing.JMenu loadAbscissaMenu;
    private javax.swing.JMenuItem loadAbscissaMenuItem;
    private javax.swing.JMenuItem loadAbscissaProcparMenuItem;
    private javax.swing.JMenuItem loadAsciiFromBayesAnalyzeMenuItem2;
    private javax.swing.JMenuItem loadAsciiFromFileMenuItem;
    private javax.swing.JMenu loadAsciiMenu;
    private javax.swing.JMenuItem loadExperimentMenuItem;
    private javax.swing.JMenuItem loadFidDataMenuItem;
    private javax.swing.JMenu loadFidMenu;
    private javax.swing.JMenu loadImageMenu;
    private javax.swing.JMenuItem loadSiemensRawDataMenuItem;
    private javax.swing.JMenuItem loadSiemensRdaDataMenuItem;
    javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem manualMenuItem;
    private javax.swing.JMenuItem mcmcMenuItem;
    private javax.swing.JMenuItem memoryMonitorMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel menuBarMessage;
    private javax.swing.JRadioButtonMenuItem mtZBigMenuItem;
    private javax.swing.JMenuItem multiple2seqMenuItem;
    private javax.swing.JRadioButtonMenuItem mzTKineticsMenuItem;
    private javax.swing.JRadioButtonMenuItem mzTMenuItem;
    private javax.swing.JMenu optionsMenu;
    javax.swing.ButtonGroup packageButtonGroup;
    private javax.swing.JMenu packageMenu;
    private javax.swing.JRadioButtonMenuItem polynomialMenuItem;
    private javax.swing.JMenuItem preferencesMenuItem;
    private javax.swing.JMenuItem rawBinaryMenuItem;
    private javax.swing.JRadioButtonMenuItem redoDpCMenuItem;
    private javax.swing.JMenuItem setServersMenuItem;
    private javax.swing.JMenuItem singled2seqMenuItem;
    private javax.swing.JButton startJobMonitorButton;
    private javax.swing.JButton startJobMonitorButton1;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenuItem systemInfoMenuItem;
    private javax.swing.JButton takeApplicationWindowScreenShot;
    private javax.swing.JMenuItem text2FidImageMenuItem;
    private javax.swing.JMenuItem text2FidMenuItem;
    private javax.swing.JMenuItem text2imageMenuItem;
    private javax.swing.JMenuItem textMultiColumn2imageMenuItem;
    private javax.swing.JMenu utilitiesMenu;
    private javax.swing.JMenuItem varianBinaryMenuItem;
    private javax.swing.JMenuItem windowSizeMEnu;
    private javax.swing.JMenu workDirMenu;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JRadioButtonMenuItem getErrInVarsGivenButtonMenuItem() {
        return ErrInVarsGivenButtonMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getAnalyzeImagePixelMenuItem() {
        return analyzeImagePixelMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBayesAnalyzeMenuItem() {
        return bayesAnalyzeMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBayesMetaboliteMenuItem() {
        return bayesMetaboliteMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBayesPhaseTestMenuItem() {
        return bayesPhaseMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBayesPhaseNonLinearMenuItem() {
        return bayesPhaseNonLinearMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBayesWaterMenuItem() {
        return bayesWaterMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getBehrensFisherMenuItem() {
        return behrensFisherMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getContactTimeMenuItem() {
        return contactTimeMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getDiffusonTensorMenuItem() {
        return diffusonTensorMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getEnterAsciiMenuItem() {
        return enterAsciiMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getEnterAsciiModelMenuItem() {
        return enterAsciiModelMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getFindResMenuItem() {
        return findResMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getImageModelSelectionMenuItem() {
        return imageModelSelectionMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getInversionRecoverylMenuItem() {
        return inversionRecoverylMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getMtZBigMenuItem() {
        return mtZBigMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getMzTKineticsMenuItem() {
        return mzTKineticsMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getMzTMenuItem() {
        return mzTMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getPolynomialMenuItem() {
        return polynomialMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getRedoDpCMenuItem() {
        return redoDpCMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getHistogramMenuItem() {
        return histogramMenuItem;
    }
    
    public javax.swing.JRadioButtonMenuItem getDesnityEstimationKernelMenuItem() {
        return this.densityEstimationKernelMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getEnterAsciiModelTestRadioButtonMenuItem() {
        return enterAsciiModelTestRadioButtonMenuItem;
    }

    public javax.swing.JRadioButtonMenuItem getImagePixelUniqueRadioButtonMenuItem() {
        return imagePixelsUniqueMenuItem;
    } 
    
    
    public javax.swing.JMenu getPackageMenu() {
        return packageMenu;
    }

    public javax.swing.JMenu getWorkDirMenu() {
        return workDirMenu;
    }

    public javax.swing.JMenu getLoadAbscissaMenu() {
        return loadAbscissaMenu;
    }

    public javax.swing.JMenu getLoadAsciiMenu() {
        return loadAsciiMenu;
    }

    public javax.swing.JMenuItem getLoadAbscissaMenuItem() {
        return loadAbscissaMenuItem;
    }

    public javax.swing.JMenuItem getLoadAbscissaProcparMenuItem() {
        return loadAbscissaProcparMenuItem;
    }

    public void setWorkDirMenu(javax.swing.JMenu workDirMenu) {
        this.workDirMenu = workDirMenu;
    }

    public javax.swing.JMenu getLoadImageMenu() {
        return loadImageMenu;
    }

    public javax.swing.JMenu getLoadFidMenu() {
        return loadFidMenu;
    }

    public javax.swing.JRadioButtonMenuItem getBinnedHistogramMenuItem() {
        return binnedHistogramMenuItem;
    }

    public javax.swing.JMenu getFileMenu() {
        return fileMenu;
    }

    public javax.swing.JMenu getOptionsMenu() {
        return optionsMenu;
    }

    public javax.swing.JMenuItem getMcmcMenuItem() {
        return mcmcMenuItem;
    }

    public javax.swing.JMenuItem getSetServersMenuItem() {
        return setServersMenuItem;
    }

    public boolean isPackageLoaded() {
        Component[] c = getPackageMenu().getMenuComponents();
        boolean isSelected = false;

        for (Component component : c) {
            if (component instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem mi = (JRadioButtonMenuItem) component;
                if (mi.isSelected() && mi != dummyMenuItem) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return isSelected;
    }

    public static HashMap<JRadioButtonMenuItem, ModelDescriptor> getModelDescrptorMap() {
        return modelDescrp;
    }

    private void initModelDesciptons() {
        ModelDescriptor md;

        md = new ModelDescriptor();
        md.setModelClass(BayesExponential.class);
        md.setModelTitle("Given and Unknown Number of Exponentials");
        md.setConstrArg(null);
        getModelDescrptorMap().put(exponentialMenuItem, md);

        md = new ModelDescriptor();
        md.setModelClass(BayesDiffTensor.class);
        md.setModelTitle("Diffusion Tensor Package");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getDiffusonTensorMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesMtZ.class);
        md.setModelTitle("Magnetization Transfer");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getMzTMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesMtZKinetics.class);
        md.setModelTitle("Bayesian Magnetization Transfer Kinetics");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getMzTKineticsMenuItem(), md);

        md = new ModelDescriptor();
        md.setModelClass(BayesPolynomial.class);
        md.setModelTitle("Given and Unknown Number of Polinomials");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getPolynomialMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesBF.class);
        md.setModelTitle("Behrens-Fisher Package");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBehrensFisherMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesErrInVarsGiven.class);
        md.setModelTitle("Errors In Variables");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getErrInVarsGivenButtonMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAscii.class);
        md.setModelTitle("Bayesian Build Your Own 1D Model");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getEnterAsciiMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAsciiModel.class);
        md.setModelTitle("Bayes Ascii Model Selection");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getEnterAsciiModelMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesTestModel.class);
        md.setModelTitle("Bayes Test Model Selection");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getEnterAsciiModelTestRadioButtonMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesDensityEstimation.class);
        md.setModelTitle("Bayes Density Function Estimation");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getHistogramMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesBinnedHistogram.class);
        md.setModelTitle("Bayes Binned Histograms ");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBinnedHistogramMenuItem(), md);
        
        md = new ModelDescriptor();
        md.setModelClass(BayesDensityEstimationKernel.class);
        md.setModelTitle("Kernel Density Estimation");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getDesnityEstimationKernelMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAsciiPreloaded.class);
        md.setModelTitle("Bayes Contact Time Model");
        md.setConstrArg("ContactTime.f");
        getModelDescrptorMap().put(getContactTimeMenuItem(), md);




        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAsciiPreloaded.class);
        md.setModelTitle("Bayes Magnitization Transfer With One Big Z Site");
        md.setConstrArg("MtZBig.f");
        getModelDescrptorMap().put(getMtZBigMenuItem(), md);



        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAsciiPreloaded.class);
        md.setModelTitle("Bayes Redor Dipolor Coupling");
        md.setConstrArg("RedorDpC.f");
        getModelDescrptorMap().put(getRedoDpCMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesEnterAsciiPreloaded.class);
        md.setModelTitle("Bayesian Analysis of Inversion Recovery Data");
        md.setConstrArg("InvRec.f");
        getModelDescrptorMap().put(getInversionRecoverylMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesWater.class);
        md.setModelTitle("Bayes Water");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBayesWaterMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesMetabolite.class);
        md.setModelTitle("Bayes Metabolite");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBayesMetaboliteMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesAnalyze.class);
        md.setModelTitle("Bayes Analyze");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBayesAnalyzeMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesFindResonances.class);
        md.setModelTitle("Bayes Find Resonances");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getFindResMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesPhase.class);
        md.setModelTitle("Bayes Phase");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBayesPhaseTestMenuItem(), md);


        md = new ModelDescriptor();
        md.setModelClass(BayesPhaseNonlinear.class);
        md.setModelTitle("Bayes Phase Nonlinear");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getBayesPhaseNonLinearMenuItem(), md);

        md = new ModelDescriptor();
        md.setModelClass(AnalyzeImagePixels.class);
        md.setModelTitle("Analyze Image Pixels");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getAnalyzeImagePixelMenuItem(), md);

        md = new ModelDescriptor();
        md.setModelClass(ImageModelSelection.class);
        md.setModelTitle("Bayes Image Model Selection");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getImageModelSelectionMenuItem(), md);
        
        md = new ModelDescriptor();
        md.setModelClass(AnalyzeImagePixelsUnique.class);
        md.setModelTitle("Analyze Image Pixels Unique");
        md.setConstrArg(null);
        getModelDescrptorMap().put(getImagePixelUniqueRadioButtonMenuItem(), md);
    }
    /**
     * @return the mcmcMenuItem
     */
}
