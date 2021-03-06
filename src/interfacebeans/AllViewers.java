/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AllViewers.java
 *
 * Created on Jan 26, 2009, 1:54:32 PM
 */

package interfacebeans;
import ascii.ASCIIDataViewer;
import fid.FidViewer;
import fid.FidModelViewer ;
import image.ImageViewer;
import applications.model.*;
import applications.model.FidModel;
import bayes.DirectoryManager;
import bayes.PackageManager;
import java.awt.*;
import java.io.File;
import javax.swing.*;
/**
 *
 * @author apple
 */
public class AllViewers extends javax.swing.JPanel
{
    private static AllViewers instance           = null;

    public static AllViewers getInstance () {
        if ( instance == null ) {
            instance = new AllViewers();
        }
        return instance;
    }
    private AllViewers() {
        initComponents();
        updateCurrentViewer();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        //System.out.println("Start Ascii Viewer");
        asciiViewer = ASCIIDataViewer.getInstance();
        //System.out.println("Stop Ascii Viewer");
        //System.out.println("Start Fid Viewer");
        fidViewer = FidViewer.getInstance();
        //System.out.println("Stop Fid Viewer");
        //System.out.println("Start Image Viewer");
        imageViewer = ImageViewer.getInstance();
        //System.out.println("Stop Image Viewer");
        //System.out.println("Start Prior Viewer");
        priorsViewer = JAllPriors.getInstance();
        //System.out.println("Stop Prior Viewer");
        //System.out.println("Start Fid Model Viewer");
        fidModelViewer = FidModelViewer.getInstance();
        //System.out.println("Stop Fid Model Viewer");
        //System.out.println("Stop Result Viewer");
        resultsViewer = interfacebeans.ResultsViewer.getInstance();
        //System.out.println("Stop Result Viewer");
        textViewer = interfacebeans.TextViewer.getInstance();
        File home           =   DirectoryManager.getBayesDir();
        File curExp         =   DirectoryManager.getExperimentDir();
        FileTreePanel ftp   =   interfacebeans.FileTreePanel.getInstance();
        ftp.setRoot(home);
        ftp.getHighlightFiles().clear();
        if (curExp  != null){
            ftp.getHighlightFiles().add(curExp);
        }
        ftp.updateFiles();
        ExperimentViewer =  ftp;

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        tabs.setToolTipText(""); // NOI18N
        tabs.setName("tabs"); // NOI18N
        tabs.addChangeListener(formListener);

        asciiViewer.setToolTipText(""); // NOI18N
        asciiViewer.setName("asciiViewer"); // NOI18N
        tabs.addTab("Ascii Data Viewer", null, asciiViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView and manage ascii data.\n\n\n</font></p><html>");

        fidViewer.setName("fidViewer"); // NOI18N
        tabs.addTab("FID Data Viewer", null, fidViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView and manage fid data.\n\n\n</font></p><html>");

        imageViewer.setName("imageViewer"); // NOI18N
        tabs.addTab("Image Viewer", null, imageViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView and manage images.\n\n\n</font></p><html>");

        priorsViewer.setName("priorsViewer"); // NOI18N
        tabs.addTab("Prior Viewer", null, priorsViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView and set model priors.\n\n\n</font></p><html>");

        fidModelViewer.setName("fidModelViewer"); // NOI18N
        tabs.addTab("Fid Model Viewer", null, fidModelViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView and or generate FID models for BayesAnalyze, <br>\nBayesFindRes, Bayes Big Peak/Little Peak and BayesMetabolite.\n\n\n</font></p><html>\n\n\n");

        resultsViewer.setName("resultsViewer"); // NOI18N
        tabs.addTab("Plot Results Viewer", null, resultsViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView the histograms, models, residuals and other plots.\n\n\n</font></p><html>\n\n");

        textViewer.setName("textViewer"); // NOI18N
        tabs.addTab("Text Results Viewer", null, textViewer, "<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nView the text results such as the probability for the model, <br>\nMCMC values report, console log and other text outputs.\n\n\n</font></p><html>\n\n\n\n");

        ExperimentViewer.setName("ExperimentViewer"); // NOI18N
        tabs.addTab("File Viewer", ExperimentViewer);

        add(tabs, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements javax.swing.event.ChangeListener {
        FormListener() {}
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (evt.getSource() == tabs) {
                AllViewers.this.tabsStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void tabsStateChanged (javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
        updateCurrentViewer();
    }//GEN-LAST:event_tabsStateChanged
    public void updateCurrentViewer(){
        Component  component            =  getTabs ().getSelectedComponent ();
        if (component instanceof JAllPriors )
        {
            Model model          =   PackageManager.getCurrentApplication();
            JAllPriors.getInstance ().updateForModel (model );
        }

        else if( component instanceof FidViewer)
        {
            FidViewer.getInstance().synchronize();
        }
        else if (component instanceof ResultsViewer )
        {
            File asciiDir       =  DirectoryManager.getBayesOtherAnalysisDir();
            ResultsViewer.getInstance().checkAndload(asciiDir  );
        }
        else if(  component instanceof ASCIIDataViewer)
        {
            ASCIIDataViewer.getInstance ().updateData ();
        }
        else if(  component instanceof ImageViewer)
        {
              ImageViewer.getInstance().loadDefaultImageIfNeeded();
        }
        else if(  component instanceof FidModelViewer)
        {
          FidModelViewer fmv =  FidModelViewer.getInstance();
          if (fmv.isLoaded() == false){
            fmv.loadData();
          }
            fmv.syncronize();
          
        }
        else if( component instanceof FidPriorsHybrid)
        {
            Model model          =   PackageManager.getCurrentApplication();
            JAllPriors.getInstance ().updateForModel (model );

        }
        else if( component instanceof TextViewer)
        {
            TextViewer.getInstance ().update ();

        }
        else if( component instanceof JShowModels)
        {
            JShowModels.getInstance ().updateGUIFromModel();

        }
          else if( component instanceof FileTreePanel)
        {
            FileTreePanel.getInstance ().updateFiles();

        }
            

    }

    public Component getCurrentViewable(){
        Component  component            =  getTabs ().getSelectedComponent ();
        Component  out                  =   null;
        if (component instanceof Viewable )
        {
            out                         =   ((Viewable)component).getMainDisplay();
        }

        return out;
    }
    public void setActive(boolean enabled){
         int tabcounts = tabs.getTabCount();

         for (int i = 0; i < tabcounts; i++) {
             Component curcomp = tabs.getComponentAt(i);
             if (curcomp != textViewer){
                tabs.setEnabledAt(i, enabled);
             }
        }
            /*
             if (curcomp instanceof JAllPriors){
                tabs.setEnabledAt(i, enabled);
             }
             else if (curcomp instanceof JShowModels){
                tabs.setEnabledAt(i, enabled);
             } 
             else if (curcomp == fidPriorsHybridViewer){
                tabs.setEnabledAt(i, enabled);
             }
             else if (curcomp == this.fidViewer){
                tabs.setEnabledAt(i, enabled);
             }
             else if (curcomp == this.fidViewer){
                tabs.setEnabledAt(i, enabled);
             }
             else if (curcomp == this.asciiViewer){
                tabs.setEnabledAt(i, enabled);
             }
             else if (curcomp == this.imageViewer){
                tabs.setEnabledAt(i, enabled);
             }
              else if (curcomp == this.plot){
                tabs.setEnabledAt(i, enabled);
             }

         }
             *
             */
         showTextViewer();
    }


    public static Component getSelectedComponent(){
         Component  component            =  getInstance().getTabs ().getSelectedComponent ();
         return component;
    }
    public static void  setSelectedComponent(Component comp){
        if (comp == null) {return;}

        if (comp instanceof JAllPriors )
        {
            showPriorsViewer();
        }
        else if( comp instanceof FidViewer)
        {
            showFidViewer();
        }
        else if (comp instanceof ResultsViewer )
        {
            showResultsViewer();
        }
        else if(  comp instanceof ASCIIDataViewer)
        {
           showAsciiViewer();
        }
        else if(  comp instanceof ImageViewer)
        {
             showImageViewer();
        }
        else if(  comp instanceof FidModelViewer)
        {
          showFidModelViewer();
        }

        else if( comp instanceof TextViewer)
        {
          showTextViewer();

        }

    }

    public static void showFidViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         if (allViewers .getFidPriorsHybridViewer () != null){
            allViewers.getTabs ().setSelectedComponent(allViewers .getFidPriorsHybridViewer () );
         }
         else {
             allViewers.getTabs ().setSelectedComponent(allViewers.getFidViewer());
         }
        
    }
    public static void showAsciiViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         ASCIIDataViewer.getInstance ().updateData ();
         allViewers.getTabs ().setSelectedComponent(allViewers.getAsciiViewer());
    }
    public static void showImageViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         allViewers.getTabs ().setSelectedComponent(allViewers.getImageViewer());
    }
    public static void showPriorsViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         if (allViewers .getFidPriorsHybridViewer () != null){
            allViewers.getTabs ().setSelectedComponent(allViewers .getFidPriorsHybridViewer () );
         }
         else {
            allViewers.getTabs ().setSelectedComponent(allViewers.getPriorsViewer());
         }
    }
    public static void showResultsViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         allViewers.getTabs ().setSelectedComponent(allViewers.getResultsViewer());

    }
    public static void showFidModelViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         allViewers.getTabs ().setSelectedComponent(allViewers.getFidModelViewer());
    }
    public static void showCodeViewer(){
         AllViewers allViewers  =   AllViewers.getInstance();
         
         try{
            allViewers.getTabs ().setSelectedComponent(allViewers.getModelsViewer());
         }catch (Exception e){}
        
    }
    public static void showTextViewer(){
         AllViewers allViewers  =  AllViewers.getInstance();
         allViewers.getTabs ().setSelectedComponent(allViewers.getTextViewer());
    }
    public void showDefaultViewer(){
        Model model  = PackageManager.getCurrentApplication();
        if (model == null ){ return;}

        if (model instanceof  AsciiModel){
            showAsciiViewer();
        }
        else if (model instanceof FidModel){
            showFidViewer();
        }
        else if (model instanceof ImageModel){
            showImageViewer();
        }
    }


    public static void showMessage(String content){
        TextViewer viewer =  TextViewer.getInstance();
        showTextViewer();
        viewer.showMessage(content);

    }
    public static void showMessage( File file){
        TextViewer viewer =  TextViewer.getInstance();
        showTextViewer();
        viewer.showMessage(file);

    }
  

    public static void reset(){

      if(instance != null){

        instance.removeAll();
        FidViewer.reset();
        FidModelViewer.reset();
        ImageViewer.reset();
        ASCIIDataViewer.reset();
        ResultsViewer.reset() ;
        JAllPriors.reset();
        JShowModels.reset();
        JShowModels.reset();
        TextViewer.reset();
        FidPriorsHybrid.reset();

        instance = null;

      }

    }




    public static void createAndShowGUI() {

         //Disable boldface controls.
       // UIManager.put ("swing.boldMetal", Boolean.FALSE);

        //Create and set up the window.
        JFrame frame = new JFrame ("");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
      //  JPlotResults newContentPane = new JPlotResults( new File("exp2/BayesOtherAnalysis/Bayes.Plot.List"));

        AllViewers newContentPane = new AllViewers( );
        newContentPane.setOpaque (true); //content panes must be opaque
        frame.setContentPane (newContentPane);


        //Display the window.
        frame.pack ();
        frame.setVisible (true);
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
    private javax.swing.JPanel ExperimentViewer;
    private javax.swing.JPanel asciiViewer;
    private javax.swing.JPanel fidModelViewer;
    private javax.swing.JPanel fidViewer;
    private javax.swing.JPanel imageViewer;
    private javax.swing.JPanel priorsViewer;
    private javax.swing.JPanel resultsViewer;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JPanel textViewer;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JPanel fidPriorsHybridViewer    = null;
    private javax.swing.JPanel modelsViewer             = null;

    public javax.swing.JTabbedPane getTabs () {
        return tabs;
    }
    public javax.swing.JPanel getAsciiViewer () {
        return asciiViewer;
    }
    public javax.swing.JPanel getFidViewer () {
        return fidViewer;
    }
    public javax.swing.JPanel getImageViewer () {
        return imageViewer;
    }
    public javax.swing.JPanel getModelsViewer () {
        return modelsViewer;
    }
    public javax.swing.JPanel getPriorsViewer () {
        return priorsViewer;
    }
    public javax.swing.JPanel getResultsViewer () {
        return resultsViewer;
    }
    public javax.swing.JPanel getFidModelViewer () {
        return fidModelViewer;
    }
    public javax.swing.JPanel getTextViewer () {
        return textViewer;
    }
    public javax.swing.JPanel getFidPriorsHybridViewer () {
        return fidPriorsHybridViewer;
    }

    public void setFidPriorsHybridViewer (JPanel hybridViewer) {
       fidPriorsHybridViewer = hybridViewer;
    }
    public void setModelsViewer ( javax.swing.JPanel modelsViewer ) {
        this.modelsViewer = modelsViewer;
    }
    /* Convenience methods */

    public static void showCompileResults(){
        TextViewer.getInstance().showCompileResults();
        AllViewers.showTextViewer();
    }
    public static void showAbscissa(){

        TextViewer.getInstance().showAbscissa();
        AllViewers.getInstance().showTextViewer();
    }
    public static void showInstructions(){
        TextViewer.getInstance().showInstructions();
        AllViewers.showTextViewer();
    }
    
    public static void removePriorsViewer(){
        Component c             = JAllPriors.getInstance(); 
        AllViewers allViewers  =  AllViewers.getInstance();
        
        allViewers.getTabs().remove(c);
        allViewers.revalidate();
    }
    public static void removeEnterAsciiModelViewer(){
         
        AllViewers allViewers  =  AllViewers.getInstance();
        Component c            = JShowModels.getInstance();


        allViewers.getTabs().remove(c);
        allViewers.revalidate();
    }
    public static void removeFidViewer(){
        
        AllViewers allViewers  =  AllViewers.getInstance();
        Component c            =  FidViewer.getInstance();

        allViewers.getTabs().remove(c);
        allViewers.revalidate();
    }
    public static void removeModelViewer(){

        AllViewers allViewers  =  AllViewers.getInstance();
        Component c            =  JShowModels.getInstance();

        allViewers.getTabs().remove(c);
        allViewers.revalidate();
    }

    public static void addFidPriorHybrid(){
       
        AllViewers allViewers  =    AllViewers.getInstance();
        FidPriorsHybrid.reset();
        JPanel pane            =    FidPriorsHybrid.getInstance();
        allViewers.setFidPriorsHybridViewer (  pane);
        String tooltip  =   "<html><p style=\"margin: 6px;\"><font size=\"4\">"+
                            "View fid data and priors."+
                            "</font></p><html>";


        allViewers.getTabs().addTab("FID and Priors Viewer", null,pane, tooltip);
              

        //allViewers.SetColors();
        allViewers.revalidate();
        allViewers.repaint();
    }
    public static void addCodeViewer(){

        AllViewers allViewers  =    AllViewers.getInstance();
        JPanel pane            =    JShowModels.getInstance();

        allViewers.setModelsViewer(  pane);

        String tooltip  =   "<html><p style=\"margin: 6px;\"><font size=\"4\">"+
                            "View Fortran and C code for loaded models."+
                            "</font></p><html>";

        allViewers.getTabs().addTab("Fortran/C Model Viewer", null,pane,tooltip);

        //allViewers.SetColors();
        allViewers.revalidate();
        allViewers.repaint();
    }
   


    
    
}
