/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FileViewer.java
 *
 * Created on Jan 28, 2009, 11:54:18 AM
 */

package interfacebeans;
import java.awt.Component;
import bayes.JPreferences;
import bayes.ApplicationPreferences;
import bayes.ApplicationConstants;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.text.*;
import java.io.*;
import java.util.Scanner;
import bayes.PackageManager;
import bayes.DirectoryManager;
import bayes.BayesManager;
import applications.model.Model;
import applications.bayesAnalyze.BayesAnalyze;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import utilities.ClipboardManager;
import utilities.EnterAsciiModel;
import utilities.IO;
import static applications.bayesAnalyze.BayesAnalyze.*;

/**
 *
 * @author apple
 */
public class TextViewer extends javax.swing.JPanel implements Viewable{
   // public static Color backgroundColor             =  new java.awt.Color(244, 244, 244);
   // public static Color foregroundColor             = new java.awt.Color(0, 0, 51);
    //public static Color titleColor                  = new java.awt.Color(0,0, 0);
   

    
    public static final int DIVIDER_LOC             = 300;
    public static  Font font                        =  TextViewerPreferences.getTextFont();
    //public static  Font font                               =    new Font("Monospaced", Font.BOLD ,  20);
    private static final long serialVersionUID      =   7526472295622576147L;
    StyledDocument document;
    private String message                          = "";
    private File displayedFile                      =   null;
    private boolean scrollup                        =   true;

     {
        StyleContext context    = new StyleContext();
        document                = new DefaultStyledDocument(context);

        Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
    
        

    }


    /** Creates new form FileViewer */
    private TextViewer() {
        initComponents();
        updateColors( );

        if (ApplicationPreferences.isRedirectJavaStream() == false){
             removeJavaOptions();
        }
        else {
            addJavatOptions();
        }
       
    }
    private static  TextViewer instance           = null;
    public static  TextViewer getInstance () {
        if ( instance == null ) {
            instance = new TextViewer();
        }
        return instance;
    }
    public static void reset(){

        if(instance != null){
            instance = null;
        }
    }

 public Component getMainDisplay(){return this;}

    public void updateColors(){
         Color backgroundColor             = TextViewerPreferences.BACKGROUND_COLOR;
         Color foregroundColor             = TextViewerPreferences.FOREGROUND_COLOR;


       ((AntiAliasedTextPane) textPane).setNimbusBackgroundColor(backgroundColor );
        textPane.setForeground(foregroundColor );
        bayesAnaylezeList.setBackground(backgroundColor );
        bayesAnaylezeList.setForeground(foregroundColor );
        standardList.setBackground(backgroundColor );
        standardList.setForeground(foregroundColor );
        jLabel2.setBackground(backgroundColor );
        jLabel2.setForeground(foregroundColor  );
        jLabel1.setBackground(backgroundColor );
        jLabel1.setForeground(foregroundColor );

        this.repaint();
    }
    private void scrollToBottom(){
       Rectangle r              =   getTextPane().getBounds();
       r.y                      =   r.height - r.height/2;
       //  scroll so that the new text is visible
       getTextPane().scrollRectToVisible(r);
    }
    private void scrollToTop(){
       getTextPane().setCaretPosition(0);
    }

    private void setText(String text, boolean scrollUp){
        if (text == null) {text = "NO INFORMATION TO DISPLAY";}
        getTextPane().setText(text);
        if (scrollUp)   {scrollToTop();         }
        else            { scrollToBottom();     }

    }
    private void setText(String text){
        setText(text,scrollup);
    }

    public void showFile(File file){
        String text = readASCIIFile(file).toString();
        setDisplayedFile(file);
        setText(text);
    }
    public void showFile(File [] files){
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
             String filename =file .getPath();
             sb.append(filename);
             sb.append("\n");
          
             sb.append("\n");
             sb.append("\n");
             String text = readASCIIFile(file).toString();
             sb.append(text);
             sb.append("\n");
             sb.append("\n");
             sb.append("\n");
        }

        setText(sb.toString());
    }
    public void showMessage(String text){
         setMessage(text);
         clearSelections();
         setText(text);
    }
    public  void showMessage(File file){
        StringBuilder sb =  readASCIIFile(file);
        showMessage(sb.toString());
    }
    public  void updateFont(Font afont){
        font = afont;
        textPane.setFont(font);

    }

    public static StringBuilder readASCIIFile(File file){
            StringBuilder sbuilder = new StringBuilder();
            try {
                Scanner scanner = new Scanner (file);
                while ( scanner.hasNextLine()){
                    sbuilder.append (scanner.nextLine());
                    sbuilder.append ("\n");
             }
            scanner.close();

    } catch (FileNotFoundException e ){
        return new StringBuilder ("File "+ file.getPath () + " doesn't exist");
    } catch (IOException e){
        return new StringBuilder ("File "+ file.getPath () + "can't be read");
    }
    return sbuilder;
   }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listsTextSplitPane = new javax.swing.JSplitPane();
        listsListSplitPane = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        bayesAnaylezeListScrollPane = new javax.swing.JScrollPane();
        bayesAnaylezeList =  new javax.swing.JList(BayesAnalyze.BAYES_ANALYZE_TYPE.values());
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        standardScrollPane = new javax.swing.JScrollPane();
        standardList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        textPane =  new interfacebeans.AntiAliasedTextPane(document);
        jPanel3 = new javax.swing.JPanel();
        printButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        SaveAsButton = new javax.swing.JButton();
        editCb = new javax.swing.JCheckBox();
        scrollUpCb = new javax.swing.JCheckBox();
        settingsButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        listsTextSplitPane.setDividerLocation(200);
        listsTextSplitPane.setName("listsTextSplitPane"); // NOI18N
        listsTextSplitPane.setOneTouchExpandable(true);

        listsListSplitPane.setDividerLocation(DIVIDER_LOC);
        listsListSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        listsListSplitPane.setName("listsListSplitPane"); // NOI18N
        listsListSplitPane.setOneTouchExpandable(true);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout());

        bayesAnaylezeListScrollPane.setName("bayesAnaylezeListScrollPane"); // NOI18N

        bayesAnaylezeList.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        bayesAnaylezeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        bayesAnaylezeList.setName("bayesAnaylezeList"); // NOI18N
        bayesAnaylezeList.addMouseListener(formListener);
        bayesAnaylezeList.addListSelectionListener(formListener);
        bayesAnaylezeListScrollPane.setViewportView(bayesAnaylezeList);

        jPanel1.add(bayesAnaylezeListScrollPane, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        jLabel1.setText("Bayes Analyze"); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);
        jPanel1.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        listsListSplitPane.setBottomComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        standardScrollPane.setName("standardScrollPane"); // NOI18N

        standardList.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        standardList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        standardList.setName("standardList"); // NOI18N
        DefaultListModel model =new DefaultListModel();
        for (TYPE type : TYPE.values()) {
            model.addElement(type);
        }
        standardList.setModel(model);
        standardList.addMouseListener(formListener);
        standardList.addListSelectionListener(formListener);
        standardScrollPane.setViewportView(standardList);

        jPanel2.add(standardScrollPane, java.awt.BorderLayout.CENTER);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        jLabel2.setText("Standard"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setOpaque(true);
        jPanel2.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        listsListSplitPane.setTopComponent(jPanel2);

        listsTextSplitPane.setLeftComponent(listsListSplitPane);

        rightPanel.setName("rightPanel"); // NOI18N
        rightPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane.setName("jScrollPane"); // NOI18N

        //javax.swing.JTextPan
        textPane.setEditable(false);
        textPane.setFont(font);
        textPane.setMargin(new java.awt.Insets(20, 20, 10, 10));
        textPane.setName("textPane"); // NOI18N
        textPane.setOpaque(false);
        textPane.addMouseListener(formListener);
        jScrollPane.setViewportView(textPane);

        rightPanel.add(jScrollPane, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        printButton.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        printButton.setText("Print");
        printButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nPrint displayed text.<br>\n\n\n</p><html>\n"); // NOI18N
        printButton.setName("printButton"); // NOI18N
        printButton.addActionListener(formListener);
        jPanel3.add(printButton);

        copyButton.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        copyButton.setText("Copy");
        copyButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nCopy entire text to clipboard.<br>\n\n\n</p><html>\n"); // NOI18N
        copyButton.setName("copyButton"); // NOI18N
        copyButton.addActionListener(formListener);
        jPanel3.add(copyButton);

        SaveButton.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        SaveButton.setText("Save");
        SaveButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nIf the displayed text is read from the file<br>\non the local hard drive, upon \"Save\", this file<br>\nwill be overwritten. Otherwise, call to this button<br>\ndoes nothing.\n\n</p><html>\n"); // NOI18N
        SaveButton.setName("SaveButton"); // NOI18N
        SaveButton.addActionListener(formListener);
        jPanel3.add(SaveButton);

        SaveAsButton.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        SaveAsButton.setText("Save As");
        SaveAsButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSaves displayed text to a file<br>\non the local hard drive. <br>\n\n\n</p><html>"); // NOI18N
        SaveAsButton.setName("SaveAsButton"); // NOI18N
        SaveAsButton.addActionListener(formListener);
        jPanel3.add(SaveAsButton);

        editCb.setSelected( getTextPane ().isEditable());
        editCb.setText("Enable Editing");
        editCb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nEnable editing of the  displayed text.<br>\n\n</p><html>\n"); // NOI18N
        editCb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        editCb.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        editCb.setName("editCb"); // NOI18N
        editCb.addActionListener(formListener);
        jPanel3.add(editCb);

        scrollUpCb.setSelected( this.scrollup);
        scrollUpCb.setText("Scroll Up");
        scrollUpCb.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nThis check box controls whether to scroll <br>\nnew text reports up or down upon display.\n\n\n</p><html>\n"); // NOI18N
        scrollUpCb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        scrollUpCb.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        scrollUpCb.setName("scrollUpCb"); // NOI18N
        scrollUpCb.addActionListener(formListener);
        jPanel3.add(scrollUpCb);

        settingsButton.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        settingsButton.setText("Settings");
        settingsButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nMore options to adjust \"Text Viewer\" settings.\n\n\n</p><html>\n"); // NOI18N
        settingsButton.setName("settingsButton"); // NOI18N
        settingsButton.addActionListener(formListener);
        jPanel3.add(settingsButton);

        rightPanel.add(jPanel3, java.awt.BorderLayout.NORTH);

        listsTextSplitPane.setRightComponent(rightPanel);

        add(listsTextSplitPane, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.ListSelectionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == printButton) {
                TextViewer.this.printButtonActionPerformed(evt);
            }
            else if (evt.getSource() == settingsButton) {
                TextViewer.this.settingsButtonActionPerformed(evt);
            }
            else if (evt.getSource() == copyButton) {
                TextViewer.this.copyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editCb) {
                TextViewer.this.editCbActionPerformed(evt);
            }
            else if (evt.getSource() == SaveAsButton) {
                TextViewer.this.SaveAsButtonActionPerformed(evt);
            }
            else if (evt.getSource() == SaveButton) {
                TextViewer.this.SaveButtonActionPerformed(evt);
            }
            else if (evt.getSource() == scrollUpCb) {
                TextViewer.this.scrollUpCbActionPerformed(evt);
            }
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == standardList) {
                TextViewer.this.standardListMouseClicked(evt);
            }
            else if (evt.getSource() == textPane) {
                TextViewer.this.textPaneMouseClicked(evt);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mousePressed(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == bayesAnaylezeList) {
                TextViewer.this.bayesAnaylezeListMousePressed(evt);
            }
            else if (evt.getSource() == standardList) {
                TextViewer.this.standardListMousePressed(evt);
            }
            else if (evt.getSource() == textPane) {
                TextViewer.this.textPaneMousePressed(evt);
            }
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == bayesAnaylezeList) {
                TextViewer.this.bayesAnaylezeListMouseReleased(evt);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            if (evt.getSource() == bayesAnaylezeList) {
                TextViewer.this.bayesAnaylezeListValueChanged(evt);
            }
            else if (evt.getSource() == standardList) {
                TextViewer.this.standardListValueChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void standardListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_standardListValueChanged
         TYPE type              =   (TYPE) getStandardList ().getSelectedValue();
         updateText(type);

}//GEN-LAST:event_standardListValueChanged
    private void standardListMousePressed (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_standardListMousePressed
        if(!SwingUtilities.isRightMouseButton (evt) )  { return;}

        TYPE type = (TYPE)getStandardList().getSelectedValue();
        if ( type == TYPE.BAYES_PROB_MODEL){
             JPopupMenu popup        = new JPopupMenu ();

            JMenuItem delteMenuItem   = new JMenuItem ("Delete");
            delteMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
                 File bayeOtherDir      =   DirectoryManager.getBayesOtherAnalysisDir();
                 File  file             =   new File(bayeOtherDir, TYPE.BAYES_PROB_MODEL.getSource());
                 file.delete();
                  showProbabilityFile();
            }
             });
            popup.add( delteMenuItem);
            popup.show (evt.getComponent (), evt.getX (), evt.getY ());


        }
        
      
}//GEN-LAST:event_standardListMousePressed
    private void bayesAnaylezeListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_bayesAnaylezeListValueChanged
         BAYES_ANALYZE_TYPE type              =   ( BAYES_ANALYZE_TYPE) getBayesAnaylezeList ().getSelectedValue();
         updateBayesAnalyzeText(type);
}//GEN-LAST:event_bayesAnaylezeListValueChanged
    private void bayesAnaylezeListMousePressed (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bayesAnaylezeListMousePressed
      

        if(!SwingUtilities.isRightMouseButton (evt) )  { return;}

        java.awt.Component c = evt.getComponent();

        BAYES_ANALYZE_TYPE type = ( BAYES_ANALYZE_TYPE) getBayesAnaylezeList ().getSelectedValue();
        if ( type == BAYES_ANALYZE_TYPE.BAYES_PROB_MODEL){
             File [] files              =   getProbabilityFiles();
             showFileList(files, c);
         }

        else if ( type == BAYES_ANALYZE_TYPE.SUMMARY1){
             File [] files              =   getSummary1Files();
             showFileList(files, c);

        }
        else if ( type == BAYES_ANALYZE_TYPE.SUMMARY2){
              File [] files             =   getSummary2Files();
              showFileList(files, c);

        }
        else if ( type == BAYES_ANALYZE_TYPE.SUMMARY3){
              File [] files             =   getSummary3Files();
              showFileList(files, c);

        }
         else if ( type == BAYES_ANALYZE_TYPE.MODEL){
              File [] files          = getModelFiles();
              showFileList(files, c);

        }
    
       else if ( type == BAYES_ANALYZE_TYPE.LOG){
              File [] files             = getBayesLogFiles();
              showFileList(files, c);

        }

        else if ( type == BAYES_ANALYZE_TYPE.OUTPUT){
              File [] files          =  getBayesOutputFiles();
              showFileList(files, c);

        }
        else if ( type == BAYES_ANALYZE_TYPE.STATUS){
              File [] files          =  getBayesStatusFiles();
              showFileList(files, c);

        }



    }//GEN-LAST:event_bayesAnaylezeListMousePressed
    private void standardListMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_standardListMouseClicked
        if(evt.getClickCount() >1 ){
            update();
        }
    }//GEN-LAST:event_standardListMouseClicked
    private void bayesAnaylezeListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bayesAnaylezeListMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_bayesAnaylezeListMouseReleased
    private void textPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textPaneMouseClicked

    }//GEN-LAST:event_textPaneMouseClicked
    private void textPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textPaneMousePressed
       if(!SwingUtilities.isRightMouseButton (evt) )  { return;}

/*
            JPopupMenu popup            =    new JPopupMenu ();

            JMenuItem copyMenuItem     =     new JMenuItem ("Copy All");
            copyMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
                copy();
            }
             });
            popup.add( copyMenuItem);


            File df                 =   this.getDisplayedFile() ;
            if (df != null){
                JMenuItem saveFileMenuItem     =     new JMenuItem  ("Save");
                saveFileMenuItem .addActionListener (new ActionListener (){
              public void actionPerformed (ActionEvent e) {
               saveChangesOfDisplayedFile ();
              }
             });
                popup.add( saveFileMenuItem);
            }


            JMenuItem saveMenuItem     =     new JMenuItem ("Save As");
            saveMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
               save();
            }
             });
            popup.add(saveMenuItem);



            JMenuItem printMenuItem     =     new JMenuItem ("Print");
            printMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
              PrintUtilities.printComponent(getTextPane ());
            }
             });
            popup.add( printMenuItem);


            popup.add( new JSeparator());
            
          
            
            

            boolean isEditable         =     getTextPane ().isEditable();
            
            JMenuItem editMenuItem     =     new JMenuItem  ("Make Editable");
            editMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
                getTextPane ().setEditable( true);

            }
             });
            if (isEditable  == false){ popup.add( editMenuItem);}


             JMenuItem noneditMenuItem     =     new JMenuItem  ("Make Not-Editable");
             noneditMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
                getTextPane ().setEditable(false);

            }
             });
             if (isEditable ){ popup.add( noneditMenuItem);}
             popup.add( new JSeparator());
          

            JMenuItem prefsMenuItem     =     new JMenuItem ("Settings");
            prefsMenuItem.addActionListener (new ActionListener (){
            public void actionPerformed (ActionEvent e) {
              JPreferences.displayTextViewerPreferences();
            }
             });
            popup.add( prefsMenuItem);

            popup.show (evt.getComponent (), evt.getX (), evt.getY ());
*/

    }//GEN-LAST:event_textPaneMousePressed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        Color fgcolor = textPane.getForeground(); 
        Color bgcolor = textPane.getBackground(); 
        try{
           textPane.setBackground(Color.WHITE);
           textPane.setForeground(Color.BLACK);
           textPane.print();
        }
        catch (Exception e){e.printStackTrace();}
        finally{
             textPane.setForeground(fgcolor);
             textPane.setBackground(bgcolor);
        }

    }//GEN-LAST:event_printButtonActionPerformed
    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        JPreferences.displayTextViewerPreferences();
    }//GEN-LAST:event_settingsButtonActionPerformed
    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        copy();
    }//GEN-LAST:event_copyButtonActionPerformed
    private void editCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCbActionPerformed
        boolean shouldEdit = editCb.isSelected();
       getTextPane ().setEditable( shouldEdit);
    }//GEN-LAST:event_editCbActionPerformed
    private void SaveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAsButtonActionPerformed
        save();
    }//GEN-LAST:event_SaveAsButtonActionPerformed
    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
        File df                 =   this.getDisplayedFile() ;
        if (df != null){
           saveChangesOfDisplayedFile ();
        }

    }//GEN-LAST:event_SaveButtonActionPerformed
    private void scrollUpCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scrollUpCbActionPerformed
       boolean shouldScrollup = scrollUpCb.isSelected();
       this.scrollup = shouldScrollup;
    }//GEN-LAST:event_scrollUpCbActionPerformed

    public void updateText(TYPE type){
         if (type == null) {return ;}
         Model model            =   PackageManager.getCurrentApplication();
         File bayeOtherDir      =   DirectoryManager.getBayesOtherAnalysisDir();
         String source          =   type.getSource();
         String modelName       =   model.getProgramName();
         File file              =   null;

         setDisplayedFile(null);

         getTextPane ().setEditable(false);
         editCb.setSelected(false);

         setSelectedStandard();

         
          switch ( type ){


              case  INSTRUCTIONS:  setText(model.getInstructions());
                                    break;

              case CONSOLE_LOG:     file = new File(bayeOtherDir, source);
                                    if (!file.exists()) {
                                         message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                         setText(message);
                                    }
                                    else {
                                         showFile(file);
                                    }
                                    break;



              case BAYES_ACCEPTED:
                                file = new File(bayeOtherDir, source);
                                if (!file.exists()) {
                                      message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                     setText(message);
                                }
                                else {
                                      showFile(file);
                                }
                                break;
            case CONDENSED:
                                file = new File(bayeOtherDir, source);
                                if (!file.exists()) {
                                      message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                     setText(message);
                                }
                                else {
                                      showFile(file);
                                }
                                break;


              case GIVEN_MCMC_VALUES :
                               String fileName = modelName + "."+source;
                               file = new File(bayeOtherDir,  fileName);
                                if (!file.exists()) {
                                     message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                     setText(message);
                                }
                                else {
                                      showFile(file);
                                }
                                break;


             case BAYES_PARAMS: file = new File(bayeOtherDir, source);
                                 if (!file.exists()) {
                                    message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                    setText(message);
                                 }
                                 else {
                                     showFile(file);
                                 }
                                 break;


          case FORTRAN_LST:      EnterAsciiModel emod         =  JShowModels.getInstance().getCurrentlyListedModel() ;

                                 if(emod == null){
                                    message  = "No models are loaded";
                                    setText(message);
                                    break;
                                 }


                                 String filename                    =   emod .getFortranListName();
                                 File dir                           =   DirectoryManager.getModelCompileDir();
                                 file                               =   new File(dir, filename);
                                 if (!file.exists() && emod.isTimedoutComplile() == false) {
                                    message  = "Compile has timed out.\n";
                                    message  += "You can increase build time out time in application properties.";
                                    setText(message);
                                 }
                                 else if (!file.exists()) {
                                    message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                    setText(message);
                                }
                                else {
                                    showFile(file);
                                }
                                break;

           case  SYSTEM_OUT:    file                        =    DirectoryManager.getSystemOutFile();
                                 if (!file.exists()) {
                                    message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                    setText(message);
                                }
                                else {
                                    showFile(file);
                                    //this.getTextPane().getT
                                }
                                break;   
                                
                                
            case  SYSTEM_ERR:    file                        =    DirectoryManager.getSystemErrorFile();
                                 if (!file.exists()) {
                                    message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                    setText(message);
                                }
                                else {
                                    showFile(file);
                                }
                                break;                     

           case  ABSCISSA:
                                 file                               =  DirectoryManager.getAbscissaFile();
                                 if (!file.exists()) {
                                    message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                    setText(message);
                                }
                                else {
                                    showFile(file);
                                }
                                break;



     

        case BAYES_PROB_MODEL:     file = new File(bayeOtherDir, source);
                                    if (!file.exists()) {
                                        message  = "File "+  file.getAbsolutePath() + " doesn't exist";
                                        setText(message);
                                     }
                                     else{
                                        showFile(file);
                                     }
                                    break;

                                   

        }
    }
    public void updateBayesAnalyzeText( BayesAnalyze.BAYES_ANALYZE_TYPE type  ){
         if (type == null) {return ;}
         File bayesAnalDir      =   DirectoryManager.getBayesAnalyzeDir();
         File file              =   null;
         setSelectedBayesAnalyze();
         setDisplayedFile(null);

         getTextPane ().setEditable(false);
         editCb.setSelected(false);

                 switch ( type ){



        case CONSOLE_LOG:            file  = new File(bayesAnalDir,BayesAnalyze.BAYES_ANALYZE_TYPE.BAYES_CONSOLE_LOG );
                                     if (!file.exists()) {
                                        message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                        setText(message);
                                     }
                                     else{
                                         showFile(file);
                                    }
                                    break;
                                    
         case       LOG:      File [] files  =  getBayesLogFiles();
                                     if (files == null || files.length == 0) {
                                        message  = "No log files in "+ bayesAnalDir.getPath();
                                        setText(message);
                                     }
                                     else{
                                         showFile(files[0]);
                                    }
                                    break;


          case BAYES_PARAMS:        file = new File(bayesAnalDir, BayesManager.bAYES_PARAMS);
                                     if (!file.exists()) {
                                        message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                        setText(message);
                                     }
                                     else {
                                         showFile(file);
                                     }
                                     break;

         
          case SUMMARY1:            files  = getSummary1Files();
                                     if (files == null || files.length == 0) {
                                        message  = "No SUMMARY1 files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                     }
                                     else{
                                        showFile(files[0]);
                                    }
                                    break;

          case SUMMARY2:
                                     files  = getSummary2Files();
                                     if (files == null || files.length == 0) {
                                        message  = "No SUMMARY2 files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                     }
                                     else{
                                        showFile(files[0]);
                                    }
                                    break;


         case SUMMARY3:
                                     files  = getSummary3Files();
                                     if (files == null || files.length == 0) {
                                        message  = "No SUMMARY3 files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                     }
                                     else{
                                        showFile(files[0]);
                                    }
                                    break;

          case MODEL:
                                     files  = getModelFiles();
                                     if (files == null || files.length == 0) {
                                        message  = "No Model files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                     }
                                     else{
                                        showFile(files[0]);
                                    }
                                    break;



        case BAYES_PROB_MODEL:      files  = bayesAnalDir.listFiles( new  BayesProbabilitesFileFilter ());
                                    if (files == null || files.length == 0) {
                                        message  = "No probability files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                    }
                                    else{
                                        showFile(files[0]);
                                   }
                                    break;


         case OUTPUT:               files  = bayesAnalDir.listFiles( new  BayesOutputFileFilter());
                                    if (files == null || files.length == 0) {
                                        message  = "No probability files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                    }
                                    else{
                                        showFile(files[0]);
                                   }
                                    break;

         case STATUS:               files  = bayesAnalDir.listFiles( new   BayesStatusFileFilter());
                                    if (files == null || files.length == 0) {
                                        message  = "No probability files in the "+ bayesAnalDir.getPath();
                                        setText(message);
                                    }
                                    else{
                                        showFile(files[0]);
                                   }
                                    break;

         case REGIONS:              file  = DirectoryManager.getRegionsFile();
                                    if (!file.exists()) {
                                        message  = "File "+ file.getAbsolutePath() + " doesn't exist";
                                        setText(message);
                                     }
                                     else {
                                         showFile(file);
                                     }
                                     break;

        }


    }
    public void guiStateChangesUponListItemChange(){
    
    }

    public void showFileList(File [] files, java.awt.Component comp){
             int filelimit          =   50;
             int nfiles             =  files.length;


             if (nfiles > filelimit   ){
                JFileSelector fselector =  JFileSelector.showJFileSelector(files);
                File file               =   fselector.getSelectedFile();
                if (file != null){ this.showFile(file);}

             }
             else{
                 JPopupMenu popup        = new JPopupMenu ();

                  if (files == null) {return;}
                  for (File file : files) {
                      JMenuItem showMenuItem   = new JMenuItem ( file.getName());
                      showMenuItem.addActionListener (new   ShowFileActionListener(file));
                      popup.add( showMenuItem);


                }
                  Point p = MouseInfo.getPointerInfo().getLocation();
                  SwingUtilities.convertPointFromScreen(p, comp );
                  popup.show (comp, p.x, p.y);
             }

    }
    public void update(){
         boolean isStandard     = !getStandardList ().isSelectionEmpty();
         boolean isBayesAnalyze = !getBayesAnaylezeList().isSelectionEmpty();
         if(isStandard){

            TYPE type = ( TYPE)getStandardList ().getSelectedValue();
            updateText(type);
         }
         else if (isBayesAnalyze){

            BayesAnalyze.BAYES_ANALYZE_TYPE type
                    = (  BayesAnalyze.BAYES_ANALYZE_TYPE )getBayesAnaylezeList().getSelectedValue();
            updateBayesAnalyzeText(type);
         }
         else{
            showMessage("");
         }




    }


   public String getText() {
        return getTextPane().getText();
    }
   public void copy (){
         ClipboardManager.putIntoClipboard(getText());
    }
   public void save (){
        JFileChooser fc  = new JFileChooser();
        fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);


         int returnVal = fc.showSaveDialog(fc);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
            File   file     =   fc.getSelectedFile ();
            IO.writeFileFromString(getText(), file);
        } else {
            return;
        }
    }
   public void saveChangesOfDisplayedFile (){
        File   file     =   getDisplayedFile();
        IO.writeFileFromString(getText(), file);
    }


   public void addStandardTypeOption (TYPE type){
         DefaultListModel model    =  (DefaultListModel) getStandardList().getModel();
         int    size                =   model.getSize();
         if (  model.contains(type)){return;}
         else{
            model.addElement(type);
         }
   }
   public void removeStandardTypeOption (TYPE type){
         DefaultListModel model    =  (DefaultListModel) getStandardList().getModel();
         int    size                =   model.getSize();
         if (  model.contains(type)){model.removeElement(type);}
         else{ }

   }
   public void removeJavaOptions(){
        removeStandardTypeOption(TYPE.SYSTEM_OUT);
        removeStandardTypeOption(TYPE.SYSTEM_ERR);

   }
   public void addJavatOptions(){
        addStandardTypeOption(TYPE.SYSTEM_OUT);
        addStandardTypeOption(TYPE.SYSTEM_ERR);


   }


    // convenience methods //



   public static void addJavaRedirectOptions(){
        TextViewer inst   =  TextViewer.getInstance();
        inst.addJavatOptions();

   }
   public static void removeJavaRedirectOptions(){
        TextViewer inst   =  TextViewer.getInstance();
        inst.removeJavaOptions();

   }

    public void showCompileResults(){
       TYPE type = (TYPE) getStandardList ().getSelectedValue();

       if (type == TYPE.FORTRAN_LST){
           updateText(type);
       }
       else {
            getStandardList ().setSelectedValue(TYPE.FORTRAN_LST, true);
       }


       setSelectedStandard();
  

    }
    public void showAbscissa(){
       TYPE type = (TYPE) getStandardList ().getSelectedValue();

       if (type == TYPE.ABSCISSA){
            updateText(type);
       }
       else {
            getStandardList ().setSelectedValue(TYPE.ABSCISSA, true);
       }


       setSelectedStandard();

    }
    public void showInstructions(){
       TYPE type = (TYPE) getStandardList ().getSelectedValue();

       if (type == TYPE.INSTRUCTIONS){
            updateText(type);
       }
       else {
            getStandardList ().setSelectedValue(TYPE.INSTRUCTIONS, true);
       }

       setSelectedStandard();


    }
    public void showProbabilityFile(){
       TYPE type = (TYPE) getStandardList ().getSelectedValue();

       if (type == TYPE.BAYES_PROB_MODEL){
            updateText(type);
       }
       else {
            getStandardList ().setSelectedValue( TYPE.BAYES_PROB_MODEL, true);
       }

       setSelectedStandard();

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SaveAsButton;
    private javax.swing.JButton SaveButton;
    private javax.swing.JList bayesAnaylezeList;
    private javax.swing.JScrollPane bayesAnaylezeListScrollPane;
    private javax.swing.JButton copyButton;
    private javax.swing.JCheckBox editCb;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSplitPane listsListSplitPane;
    private javax.swing.JSplitPane listsTextSplitPane;
    private javax.swing.JButton printButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JCheckBox scrollUpCb;
    private javax.swing.JButton settingsButton;
    private javax.swing.JList standardList;
    private javax.swing.JScrollPane standardScrollPane;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JTextPane getTextPane () {
        return textPane;
    }
    public javax.swing.JList getStandardList () {
        return standardList;
    }
    public javax.swing.JList getBayesAnaylezeList () {
        return bayesAnaylezeList;
    }
    public javax.swing.JScrollPane getBayesAnaylezeListScrollPane () {
        return bayesAnaylezeListScrollPane;
    }
    public javax.swing.JScrollPane getStandardScrollPane () {
        return standardScrollPane;
    }
    public javax.swing.JSplitPane getListsListSplitPane() {
        return listsListSplitPane;
    }
    public javax.swing.JSplitPane getListsTextSplitPane() {
        return listsTextSplitPane;
    }
 
    public void setSelectedStandard(){
         getBayesAnaylezeList ().clearSelection();
        
    }
    public void setSelectedBayesAnalyze(){
        getStandardList ().clearSelection();
    }
    public void clearSelections(){
        getStandardList ().clearSelection();
        getBayesAnaylezeList ().clearSelection();
    }
   
    public String getMessage () {
        return message;
    }
    public void setMessage ( String message ) {
        this.message = message;
    }

   

    public static void main(String argv[]) {

        /*
        TextViewer pane = new TextViewer();
        pane.setText("sasajspojspasjpb \ndsd;lsmdlsm \nlsdm;sdm\njsdk");
        pane.getTextPane().setMargin(new java.awt.Insets(39, 20, 20, 20));
    JFrame f = new JFrame("ColorPane example");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane(new JScrollPane(pane));
    f.setSize(600, 400);
    f.setVisible(true);
         */
       
  }

    public File getDisplayedFile() {
        return displayedFile;
    }
    public void setDisplayedFile(File displayedFile) {
        this.displayedFile = displayedFile;
    }

 

  

  


     public static enum TYPE {
        INSTRUCTIONS         ( "Instructions"               ,  "Instructions"               ),
        BAYES_PROB_MODEL     ( ApplicationConstants.PROBAILITY_FILE_NAME     ,   "Probability model"         ),
        GIVEN_MCMC_VALUES    ( ApplicationConstants.MCMCVALUES_FILE_NAME   ,   "MCMC values"               ),
        BAYES_PARAMS         ( ApplicationConstants.BAYES_PARAMS      ,   "Bayes.params"              ),
        CONSOLE_LOG          ( ApplicationConstants.CONSOLE_LOG_FILE        ,   "Console log"               ),
        BAYES_ACCEPTED       ( ApplicationConstants.BAYES_ACCEPT_FILE     ,   "Bayes accepted"            ),
        ABSCISSA             ( ApplicationConstants.ABSCISSA_FILE_NAME ,    "Image Abscissa"            ),
        CONDENSED            ( ApplicationConstants.CONDENSED_FILE_NAME,    "Bayes Condensed"            ),
        FORTRAN_LST          ( ApplicationConstants.FOSTRANLIST_FILE_NAME       ,   "Fortran.lst"               ),
        SYSTEM_OUT           ( "System.out"        ,   "Standard log"    ),
        SYSTEM_ERR           ( "System.err"        ,   "Error log" );
       // MESSAGE              ( "",                      "Message"        )


        private final String source;
        private final String name;


      
        TYPE(String aname, String aMenuText  ) {
            this.source         = aname;
            this.name           = aMenuText;
         
        }
        public String getSource() {return source;}
        public String getName() {return name;}

        public static TYPE getTypeByName(String aName)
                throws IllegalArgumentException{

            for (TYPE rt : TYPE.values()) {
                    if(aName.equalsIgnoreCase(rt.name)){return rt;}
            }
             throw new IllegalArgumentException("Name "+ aName + " is illegal.");
        }
  
        
        
        @Override
        public String toString() {return name;}




    }
     
   


     public static File [] getSummary1Files(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir .listFiles( new BayesSummary1FileFilter ());
             Arrays.sort(files);
             return files;

    }
     public static File [] getSummary2Files(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir .listFiles( new BayesSummary2FileFilter ());
             Arrays.sort(files);
             return files;

    }
     public static File [] getSummary3Files(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir .listFiles( new BayesSummary3FileFilter ());
             Arrays.sort(files);
             return files;

    }
     public static File [] getModelFiles(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir .listFiles( new BayesModelFileFilter ());
             Arrays.sort(files);
             return files;

    }
     public static File [] getProbabilityFiles(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir.listFiles( new BayesProbabilitesFileFilter ());
             Arrays.sort(files);
             return files;

    }
     public static File [] getBayesLogFiles(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir.listFiles( new BayesLogFileFilter());
             Arrays.sort(files);
             return files;

    }
     public static File [] getBayesOutputFiles(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir.listFiles( new  BayesOutputFileFilter());
             Arrays.sort(files);
             return files;

    }
     public static File [] getBayesStatusFiles(){
             File dir           =   DirectoryManager.getBayesAnalyzeDir();
             File [] files      =  dir.listFiles( new  BayesStatusFileFilter());
             Arrays.sort(files);
             return files;

    }

     class ShowFileActionListener  implements ActionListener {
         File file;
         ShowFileActionListener (File aFile){file  = aFile;}
         public void actionPerformed (ActionEvent e) {
                    showFile(file);
        }
     }


}
