/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import bayes.Enums.UNITS;
import applications.bayesMetabolite.Metabolite;
import bayes.DirectoryManager;
import interfacebeans.Viewer;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class FidPopupMenu   extends  JPopupMenu{
 private FidViewable viewer;
    private JMenuItem fidInfoMenuItem               =  new JMenuItem("Data Info"); ;
    ButtonGroup fnButtonGroup                       = new ButtonGroup();

    JMenu setFn                                     = new JMenu("Set Fn");
    JRadioButtonMenuItem k1RadioButtonMenuItem      = new  JRadioButtonMenuItem("1K") ;
    JRadioButtonMenuItem k2RadioButtonMenuItem      = new  JRadioButtonMenuItem("2K") ;
    JRadioButtonMenuItem k4RadioButtonMenuItem      = new  JRadioButtonMenuItem("4K") ;
    JRadioButtonMenuItem k8RadioButtonMenuItem      = new  JRadioButtonMenuItem("8K") ;
    JRadioButtonMenuItem k16RadioButtonMenuItem     = new  JRadioButtonMenuItem("16K") ;
    JRadioButtonMenuItem k32RadioButtonMenuItem     = new  JRadioButtonMenuItem("32K") ;
    JRadioButtonMenuItem k64RadioButtonMenuItem     = new  JRadioButtonMenuItem("64K") ;
    JRadioButtonMenuItem k128RadioButtonMenuItem    = new  JRadioButtonMenuItem("128K") ;
    JRadioButtonMenuItem k256RadioButtonMenuItem    = new  JRadioButtonMenuItem("256K") ;

    JMenu setUnits                                  = new JMenu("Set Units");
    JMenuItem hertzUnitsMenuItem                    = new JMenuItem("Hertz");
    JMenuItem ppmUnitsMenuItem                      = new JMenuItem("PPM");
    JMenuItem phasingMenuItem                       = new JMenuItem("Apply Phasing");
    JMenuItem regionsMenuItem                       = new JMenuItem("Set Regions");
    JMenuItem removeDataMenuItem                    = new JMenuItem("Clear Data");
    JMenuItem serReferenceMenuItem                  = new JMenuItem("Set Reference");
   
    JMenuItem setLbMenuItem                         = new JMenuItem("Set Lb");
    JMenuItem showPlottedDataMenuItem               = new JMenuItem("Show Plotted Data");

    JMenuItem saveAsFid                              = new JMenuItem("Save As Varian Fid");
    JMenuItem saveAsText                             = new JMenuItem("Save As Text");

   

    public FidPopupMenu ( FidViewable aviewer) {
        super();
        viewer                     =   aviewer;


        boolean modelViewer = viewer instanceof FidModelViewer;
       
        FormListener formListener = new FormListener();

        fidInfoMenuItem.addActionListener(formListener);
        add(fidInfoMenuItem);

        add(new JSeparator());
        
        saveAsFid  .addActionListener(formListener);
        add(saveAsFid  );
        
        saveAsText .addActionListener(formListener);
        add(saveAsText  );

        add(new JSeparator());

        showPlottedDataMenuItem.addActionListener(formListener);
        add(showPlottedDataMenuItem);

        removeDataMenuItem.addActionListener(formListener);
        add(removeDataMenuItem);

        add(new JSeparator());

        phasingMenuItem.addActionListener(formListener);
        add(phasingMenuItem);

        regionsMenuItem.addActionListener(formListener);
        if (modelViewer == false){  add(regionsMenuItem);}

       

        fnButtonGroup.add(k1RadioButtonMenuItem);
        k1RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k1RadioButtonMenuItem);

        fnButtonGroup.add(k2RadioButtonMenuItem);
        k2RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k2RadioButtonMenuItem);

        fnButtonGroup.add(k4RadioButtonMenuItem);
        k4RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k4RadioButtonMenuItem);

        fnButtonGroup.add(k8RadioButtonMenuItem);
        k8RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k8RadioButtonMenuItem);

        fnButtonGroup.add(k16RadioButtonMenuItem);
        k16RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k16RadioButtonMenuItem);

        fnButtonGroup.add(k32RadioButtonMenuItem);
        k32RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k32RadioButtonMenuItem);

        fnButtonGroup.add(k64RadioButtonMenuItem);
        k64RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k64RadioButtonMenuItem);

        fnButtonGroup.add(k128RadioButtonMenuItem);
        k128RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k128RadioButtonMenuItem);

        fnButtonGroup.add(k256RadioButtonMenuItem);
        k256RadioButtonMenuItem.addActionListener(formListener);
        setFn.add(k256RadioButtonMenuItem);

        add(setFn);

        add(new JSeparator());

        setLbMenuItem.addActionListener(formListener);
        add(setLbMenuItem);

        serReferenceMenuItem.addActionListener(formListener);
        if (modelViewer == false){  add(serReferenceMenuItem); }
       

        setUnits.setToolTipText("Set Units"); // NOI18N

        ppmUnitsMenuItem.addActionListener(formListener);
        setUnits.add(ppmUnitsMenuItem);

        hertzUnitsMenuItem.addActionListener(formListener);
        setUnits.add(hertzUnitsMenuItem);

        if (modelViewer == false){  add(setUnits);  }


 

       update();

    }

     public void addCustomMenuesToPopupMenu(JPopupMenu addonpopup){
            add(new JSeparator());
            for (Component comp : addonpopup.getComponents()) {
                     add(comp);
            }


    }
     private  void setSelectedRadioButtonForValue(int val){
        JRadioButtonMenuItem rb = getRadioButtonForValue (val);
        if (rb != null) {rb.setSelected(true);}

    }
     public final void update(){
          // set value for fn
        int fn                  =   viewer.getFidReader().getUserFn();
        setSelectedRadioButtonForValue(fn );

     }
     public  JRadioButtonMenuItem getRadioButtonForValue(int val){
        switch(val){

            case 1024   : return  k1RadioButtonMenuItem;
            case 2048   : return  k2RadioButtonMenuItem;
            case 4096   : return  k4RadioButtonMenuItem;
            case 8192   : return  k8RadioButtonMenuItem;
            case 16384  : return  k16RadioButtonMenuItem;
            case 32768  : return  k32RadioButtonMenuItem;
            case 65536  : return  k64RadioButtonMenuItem;
            case 131076 : return  k128RadioButtonMenuItem;
            case 262152 : return  k256RadioButtonMenuItem;
            default     : return  null;

        }

    }


    private void saveAsFidMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
       File dir                         =   viewer.getDataDir();
       if (dir.exists() == false){

       }
       else{    
            copyData(dir );
       }
    }
    private void saveAsTextMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
            String txt          =    viewer.getFidReader().convertToText();
            copyData(txt );
    }
    private void phasingMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        JPhasing.showDialog(viewer);
    }
    private void setLbMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        JSetFourierWeighting.showDialog(viewer);
    }
    private void serReferenceMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        JSetReference.showDialog(viewer);
    }
    private void ppmUnitsMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
            UNITS newUnits      =   UNITS.PPM;
            UNITS plotUnits     =   viewer.getFidReader().getUnits();

            if (newUnits    != plotUnits){
                //viewer.getFidPlotData().changeDomainAxisUnits(plotUnits, newUnits);

                viewer.setUnits(newUnits);

                Metabolite.frequencyUnits       =  newUnits ;
                viewer.updatePlot();
            }

    }
    private void hertzUnitsMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
            UNITS newUnits      =   UNITS.HERTZ;
            UNITS plotUnits     =   viewer.getFidReader().getUnits();

            if (newUnits    != plotUnits){
               // viewer.getFidPlotData().changeDomainAxisUnits(plotUnits, newUnits);

                viewer.setUnits(newUnits);

                Metabolite.frequencyUnits       =  newUnits ;
                viewer.updatePlot();
            }
    }
    private void k1RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,10) );
    }
    private void k2RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
         doFn( (int)Math.pow(2,11) );
    }
    private void k4RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,12) );
    }
    private void k8RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,13) );
    }
    private void k16RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,14) );
    }
    private void k32RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,15) );
    }
    private void k64RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
        doFn( (int)Math.pow(2,16) );
    }
    private void k128RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
         doFn( (int)Math.pow(2,17) );
    }
    private void k256RadioButtonMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
         doFn( (int)Math.pow(2,18) );
    }
    private void regionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
                JRegions.showDialog();
    }
    private void fidInfoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
       Viewer.display(viewer.getDataInfo().toString(),"Loaded Data Info" );
    }
    private void removeDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        viewer.unloadData();
    }
    private void showPlottedDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
         Viewer.display(this.viewer.getFidPlotData ().getDataAsString(),"Plotted Data" );
    }
    public void doFn(int userFn){
       FidReader reader        =   viewer.getFidReader();

       reader.setUserFn(userFn);
       reader .computeSpectralData(true);
       FidIO.storeToDisk(reader.getFidDescriptor(), viewer.getFidDescriptorFile());

      
       viewer.updatePlot();
    }
    public void setUnitsEnabled(boolean enabled ){ setUnits.setEnabled(enabled);}
    public void copyData(File srcDir  ){
        JFileChooser fc                                 = new JFileChooser();
        fc.setMultiSelectionEnabled (false);
        fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG );
        fc.setCurrentDirectory(DirectoryManager.startDir);

            int returnVal                   =   fc.showSaveDialog(this);
            if (returnVal                   !=   JFileChooser.APPROVE_OPTION) { return;}

            DirectoryManager.startDir       =  fc.getCurrentDirectory();
            File dir                        =   fc.getSelectedFile ();

            if (dir.exists() == false){dir.mkdirs();}

            boolean done                    =   IO.copyDirectory(srcDir, dir);
            if (done == false ){
                    DisplayText.popupErrorMessage("Failed to save data.");
            }
    }
    public void copyData(String txt ){
        JFileChooser fc                                 = new JFileChooser();
        fc.setMultiSelectionEnabled (false);
        fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.SAVE_DIALOG );
        fc.setCurrentDirectory(DirectoryManager.startDir);

            int returnVal                   =   fc.showSaveDialog(this);
            if (returnVal                   !=   JFileChooser.APPROVE_OPTION) { return;}

            DirectoryManager.startDir =  fc.getCurrentDirectory();
            
            
            File file                        =   fc.getSelectedFile ();

            boolean done                    =   IO.writeFileFromString(txt, file);
            if (done == false ){
                    DisplayText.popupErrorMessage("Failed to save data.");
            }
    }


    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
           if (evt.getSource() == fidInfoMenuItem) {
               fidInfoMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == removeDataMenuItem) {
               removeDataMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == phasingMenuItem) {
               phasingMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == regionsMenuItem) {
              regionsMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k1RadioButtonMenuItem) {
               k1RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k2RadioButtonMenuItem) {
               k2RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k4RadioButtonMenuItem) {
               k4RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k8RadioButtonMenuItem) {
              k8RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k16RadioButtonMenuItem) {
              k16RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k32RadioButtonMenuItem) {
              k32RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k64RadioButtonMenuItem) {
              k64RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k128RadioButtonMenuItem) {
              k128RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == k256RadioButtonMenuItem) {
              k256RadioButtonMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == setLbMenuItem) {
              setLbMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == serReferenceMenuItem) {
               serReferenceMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == ppmUnitsMenuItem) {
               ppmUnitsMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == hertzUnitsMenuItem) {
               hertzUnitsMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == showPlottedDataMenuItem) {
               showPlottedDataMenuItemActionPerformed(evt);
            }
           else if (evt.getSource() == saveAsFid) {
              saveAsFidMenuItemActionPerformed(evt);
            }
           else if (evt.getSource() == saveAsText) {
              saveAsTextMenuItemActionPerformed(evt);
            }
        }

    }

}
