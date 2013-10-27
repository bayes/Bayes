/*
 * ASCIIDataViewer.java
 *
 * Created on February 6, 2008, 10:05 AM
 */

package ascii;
import bayes.BayesManager;
import java.awt.event.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
 
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.chart.renderer.xy.*;
import bayes.PackageManager;
import applications.model.Model;
import applications.model.FidModel;
import interfacebeans.Viewer;
import java.awt.geom.Ellipse2D;
import org.jfree.chart.axis.NumberAxis;

import utilities.IO;
import bayes.DirectoryManager;
import interfacebeans.Viewable;
import java.util.Arrays;

public class ASCIIDataViewer extends javax.swing.JPanel 
        implements  ActionListener,MouseListener, ListSelectionListener,
                java.beans.PropertyChangeListener, Viewable
 {
     // files for ascii
  
    public static final String DATA_FILE_CHANGE_MESSAGE         = "AsciiFileViewerDataFileChanged";
    public static final String DATA_FILE_LOADED_MESSAGE         = "LoadAndViewDataFileLoaded";
    public static final String DATA_FILE_DELETED_MESSAGE        = "ASCII DATA DELETED";
    private static final long serialVersionUID                  =   7526372295622516147L;
    private static ASCIIDataViewer instance                     =   null;


    private AsciiDescriptor ascciDescriptor                     =   new AsciiDescriptor();
    private final XYSeriesCollection  xydataset                 =   new  XYSeriesCollection();
    private boolean generateTooltips                            =   true;
    private final ChartPanel chartPanel                         =   initializeChartPanel();
    private final CustomCellRenderer   cellrenderer             =   new CustomCellRenderer();



    private ASCIIDataViewer () {
        initComponents ();
        BayesManager.pcs.addPropertyChangeListener(this);

    }
    public static ASCIIDataViewer getInstance() {
        if(instance == null) {
             instance = new ASCIIDataViewer ();
        }
        return instance;
   }


    public static void reset(){

        if (instance == null) {return;}
        instance = null;
    }
    public void unload(){
        getJListOfFiles().removeListSelectionListener(instance);

                List <File> dataFiles =  DirectoryManager.getAsciiDataAndIFHFileListOnDisk();
                for (File file : dataFiles) {
                    if (file.exists())  file.delete();
                }
            
                getXydataset().removeAllSeries();
                getJListOfFiles().setModel(new  DefaultListModel());
        getJListOfFiles().addListSelectionListener(instance);
    }
    private  ChartPanel initializeChartPanel(){
        JFreeChart chart   = ChartFactory.createXYLineChart( 
                                null, null, null,getXydataset(),PlotOrientation.VERTICAL,
                                true, this.generateTooltips, false  );
          NumberAxis rangeaxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
        rangeaxis.setAutoRangeIncludesZero(false);
        XYPlot plot         =   (XYPlot)chart .getPlot();

        /*
        NumberAxis rangeaxis             =  new NumberAxis();
        rangeaxis.setAutoRangeIncludesZero(false);
        final  XYItemRenderer renderer             = new XYLineAndShapeRenderer();
        if (generateTooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        }

        XYPlot plot                     =  new FastXYPlot(     xydataset,
                                                                new NumberAxis(),
                                                                rangeaxis,
                                                                renderer );
        JFreeChart chart           = new JFreeChart (plot) ;
 */
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK  );
        plot.setDomainGridlinePaint(Color.BLACK );

        setRenderrer(plot);

        ChartPanel chartPane   =  new ChartPanel(chart);
        chartPane.setMouseZoomable(true);
        chartPane.setMouseWheelEnabled(true);

        boolean isPannable = ( plot instanceof Pannable);
        if (isPannable ){
               plot.setDomainPannable(true);
                plot.setRangePannable(true);
         }
 // System.out.println(" Stop Initializing chart panel Ascii data viewer");
        return  chartPane;
   }
    private  void setRenderrer( XYPlot plot){

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setAutoPopulateSeriesShape(false);
        renderer.setBaseShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer.setBaseStroke  (   new java.awt.BasicStroke(3), true);
        renderer.setBaseLinesVisible(true);
       // renderer.setBaseShape(new Ellipse2D.Double(-1.0, -1.0, 2.0, 2.0));

        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(false);

   }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane = new javax.swing.JSplitPane();
        plotPane = chartPanel;
        leftPane = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        file_list = new javax.swing.JList();
        tool_pane = new javax.swing.JPanel();
        remove = new javax.swing.JButton();
        infoButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jSplitPane.setDividerLocation(180);
        jSplitPane.setContinuousLayout(true);
        jSplitPane.setName("jSplitPane"); // NOI18N
        jSplitPane.setOneTouchExpandable(true);

        plotPane.setName("plotPane"); // NOI18N
        //plot_pane.add(chartPanel);

        org.jdesktop.layout.GroupLayout plotPaneLayout = new org.jdesktop.layout.GroupLayout(plotPane);
        plotPane.setLayout(plotPaneLayout);
        plotPaneLayout.setHorizontalGroup(
            plotPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 527, Short.MAX_VALUE)
        );
        plotPaneLayout.setVerticalGroup(
            plotPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 412, Short.MAX_VALUE)
        );

        jSplitPane.setRightComponent(plotPane);

        leftPane.setBackground(new java.awt.Color(255, 255, 255));
        leftPane.setName("leftPane"); // NOI18N
        leftPane.setLayout(new java.awt.BorderLayout());

        jScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ascii Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Lucida Grande", 1, 14))); // NOI18N
        jScrollPane.setName("jScrollPane"); // NOI18N

        file_list.setModel(generateDefaultListModel());
        file_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        file_list.setCellRenderer( cellrenderer);
        file_list.setName("file_list"); // NOI18N
        file_list.addMouseListener(this);
        file_list.addListSelectionListener(this);
        jScrollPane.setViewportView(file_list);

        leftPane.add(jScrollPane, java.awt.BorderLayout.CENTER);

        tool_pane.setBackground(new java.awt.Color(255, 255, 255));
        tool_pane.setName("tool_pane"); // NOI18N

        remove.setBackground(new java.awt.Color(255, 255, 255));
        remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/delete.jpg"))); // NOI18N
        remove.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nDelete selected dataset.\n</html>\n\n\n\n"); // NOI18N
        remove.setContentAreaFilled(false);
        remove.setFocusPainted(false);
        remove.setName("remove"); // NOI18N
        remove.setOpaque(true);
        remove.setPreferredSize(new java.awt.Dimension(50, 50));
        remove.addActionListener(this);

        infoButton.setBackground(new java.awt.Color(0, 0, 204));
        infoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bayes/resources/info_48x48.png"))); // NOI18N
        infoButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\nShow information for selected data.\n</html>\n\n"); // NOI18N
        infoButton.setContentAreaFilled(false);
        infoButton.setFocusPainted(false);
        infoButton.setName("infoButton"); // NOI18N
        infoButton.setPreferredSize(new java.awt.Dimension(50, 50));
        infoButton.addActionListener(this);

        org.jdesktop.layout.GroupLayout tool_paneLayout = new org.jdesktop.layout.GroupLayout(tool_pane);
        tool_pane.setLayout(tool_paneLayout);
        tool_paneLayout.setHorizontalGroup(
            tool_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tool_paneLayout.createSequentialGroup()
                .addContainerGap()
                .add(remove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(infoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        tool_paneLayout.setVerticalGroup(
            tool_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tool_paneLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .add(tool_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(remove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(infoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        leftPane.add(tool_pane, java.awt.BorderLayout.PAGE_END);

        jSplitPane.setLeftComponent(leftPane);

        add(jSplitPane, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == remove) {
            ASCIIDataViewer.this.removeActionPerformed(evt);
        }
        else if (evt.getSource() == infoButton) {
            ASCIIDataViewer.this.infoButtonActionPerformed(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == file_list) {
            ASCIIDataViewer.this.file_listAsciiFile_list_MousePressed(evt);
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (evt.getSource() == file_list) {
            ASCIIDataViewer.this.file_listValueChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents
    public Component getMainDisplay(){return this.plotPane;}
    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt){
      if (evt.getPropertyName().equals(BayesManager.JRUN_JOB_START)){
          remove.setEnabled(false);
      }
      
      else if (evt.getPropertyName().equals(BayesManager.JRUN_JOB_END)){
          remove.setEnabled(true);
      }
      
      else if (evt.getPropertyName().equals(BayesManager.JRUN_JOB_CANCELED)){
          remove.setEnabled(true);
      }
 
   }
    private void file_listValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_file_listValueChanged
        if (evt.getValueIsAdjusting() ) { return;}
        File   file                 =  getSelectedFile();
        if (file == null) {
            clearPlot();
            return;
        }
        File   afh                  =  DirectoryManager.getAfhFileForData(file);
        
        AsciiDescriptor ad          =   new  AsciiDescriptor();

        try {
            if (afh.exists()==false) {
                String sourse           =   "Unknown";
                String  srsDesc         =    AsciiDescriptor.SOURCE_TYPE.FILE.UNKNOWN.getInfo();
                AsciiIO.createAndSaveAfhFile(file, afh, sourse,  srsDesc );
            }

            ad                  = AsciiIO.loadFromDisk(afh );
            setAscciDescriptor(ad);

            int [][] plot_instructions  =  getPlotInstructions();
        
            getXydataset().removeAllSeries ();

            int ntrcaces        =    plot_instructions.length;

            readData( plot_instructions, file);
          
            if (ntrcaces > 1){
                 getChart().getLegend().setVisible(true);
            }
            else{
                 getChart().getLegend().setVisible(false);
            }

             this.firePropertyChange(DATA_FILE_CHANGE_MESSAGE, null, file);

        } catch (Exception ex) {
            ex.printStackTrace (); 
            deleteFile(file );
            return;
        }
        
    }//GEN-LAST:event_file_listValueChanged
    private void file_listAsciiFile_list_MousePressed (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_file_listAsciiFile_list_MousePressed
       if(SwingUtilities.isRightMouseButton (evt) || evt.isControlDown()){

            JPopupMenu popup        = new JPopupMenu ();

            if (  getSelectedFile() != null){

                JMenuItem delteMenuItem   = new JMenuItem ("Delete");
                delteMenuItem.addActionListener (new ActionListener (){
                    public void actionPerformed (ActionEvent e) {
                        deleteCurrentlySelectedData ();
                    }
                });
                popup.add( delteMenuItem);


                JMenuItem delteAllMenuItem   = new JMenuItem ("Delete All");
                delteAllMenuItem .addActionListener (new ActionListener (){
                    public void actionPerformed (ActionEvent e) {
                       ASCIIDataViewer.this.unload();
                    }
                });
                popup.add(  delteAllMenuItem );


           

                JMenuItem viewMenuItem     = new JMenuItem ("View as text");
                viewMenuItem .addActionListener (new ActionListener (){
                public void actionPerformed (ActionEvent e) {
                                    viewAsciiFile();
                }});
                popup.add ( viewMenuItem);
                popup.add (new JSeparator());
                JMenuItem infoMenuItem     = new JMenuItem ("Show info");
                infoMenuItem.addActionListener (new ActionListener (){
                public void actionPerformed (ActionEvent e) {
                   viewAsciiInfo();
                }});
                popup.add ( infoMenuItem);
            }


            popup.show (evt.getComponent (), evt.getX (), evt.getY ());
        }
               
    }//GEN-LAST:event_file_listAsciiFile_list_MousePressed
    private void removeActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        deleteCurrentlySelectedData ();
    }//GEN-LAST:event_removeActionPerformed
    private void infoButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        viewAsciiInfo();
}//GEN-LAST:event_infoButtonActionPerformed
    public void fileToXYSeries(File file, int [][] plotInstuctions)throws IOException
    {
            // clear all series
            getXydataset().removeAllSeries ();

            if ( plotInstuctions != null){
                for (int i = 0; i < plotInstuctions.length; i++) {
                    int x_col       = plotInstuctions[i][0] + 1;
                    int y_col       = plotInstuctions[i][1] + 1;
                    double [] x     = IO.nASCI2double (file, x_col);
                    double [] y     = IO.nASCI2double (file, y_col);
                    addDataSeries (x ,y,file.getName());
                }

            }
            else{
                int numCol      =   IO.getNumberOfColumns(file);
                double [] x     =   IO.nASCI2double (file, 1);

                for (int i = 2; i <= numCol ; i++) {
                    double [] y = IO.nASCI2double (file, i);
                    addDataSeries (x ,y,file.getName()+"series"+i);
                }

            }


    }

    public static void      loadFiles( File [] files){
            File dir            =   DirectoryManager.getBayesOtherAnalysisDir();
            AsciiIO.copyAsciiFiles (files,  dir);
            ASCIIDataViewer.getInstance().updateData();
    }

     public void readData(int [][] plot_instructions, File file) throws IOException{
        int ntrcaces        =    plot_instructions.length;
            for (int i = 0; i < ntrcaces; i++) {
                int x_col       = plot_instructions[i][0] + 1;
                int y_col       = plot_instructions[i][1] + 1;
                double [] x     = IO.nASCI2double (file,x_col);
                double [] y     = IO.nASCI2double (file,y_col);
                addDataSeries (x ,y,"Col "+(i+1));
            }

    }

    public void readDataFuture(int [][] plot_instructions, File file) throws IOException{
        int nseries                     =    plot_instructions.length;

        long t1         =System.nanoTime();
        String content                  =    IO.readFileToString(file);
        long t2         =System.nanoTime();double  time = (t2 - t1)*1E-9;
        System.out.println("Time to read File"+time);

    
        // maximum trace number
        int maxTraceNumber                =   0;
        for (int curInts = 0; curInts < plot_instructions.length; curInts++) {
            for (int curVal = 0; curVal < plot_instructions[curInts].length; curVal++) {
                    maxTraceNumber        = Math.max(maxTraceNumber,  plot_instructions[curInts][curVal]);
            }

        }

        // count number of lines in the file
        int numberOfLines           =      0;
        Scanner scanner             =    new Scanner(content );
        while (scanner.hasNextLine()){
                        scanner.nextLine();
                        numberOfLines +=1;
        }
        scanner.close();

        // read all required data
        int numberOfColumnsToRead   =   maxTraceNumber + 1;
        double [][] alldata         =   new double [ numberOfColumnsToRead][numberOfLines];

t1=System.nanoTime();
        scanner             =    new Scanner(content );
        for (int curLine = 0; curLine < numberOfLines ; curLine++) {
            for (int curCol = 0; curCol < alldata.length ; curCol++) {
                    double val  = scanner.nextDouble();
                    alldata[curCol][curLine] = val;
            }
            scanner.nextLine();

        }
        scanner.close();
 t2 =System.nanoTime(); time = (t2 - t1)*1E-9;
 System.out.println("Time to parse file "+time);

t1=System.nanoTime();
         for (int i = 0; i < nseries ; i++) {
            String seriesName           =   "Data Series "+ i;
            XYSeries curSeries          =    new XYSeries(seriesName);
            int [] instructions         =    plot_instructions[i];
            int  colX                   =    instructions[0];
            int  colY                   =    instructions[1];
           //  System.out.println("colX  "+ colX  + "colY  "+ colY );
             for (int curPoint = 0; curPoint < numberOfLines ; curPoint++) {
                    double x  = alldata[colX][curPoint] ;
                    double y  = alldata[colY][curPoint] ;
                    curSeries.add(x, y);
             }
            getXydataset().addSeries(curSeries);
        }

 t2 =System.nanoTime(); time = (t2 - t1)*1E-9;
 System.out.println("Time to populate xydataseries "+time);

    }

    public static void main(String [] arg){
        int [][] plot_instructions = {{0,1}, {0,2}};
        File file        = new File("/Users/apple/Bayes/Exp3/BayesOtherAnalysis/002.dat");
        ASCIIDataViewer v = new ASCIIDataViewer();
        long t1         =System.nanoTime();
        try{
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
             v.readData(plot_instructions, file);
            }

        }catch(Exception e){e.printStackTrace();}



        long t2         =   System.nanoTime();
        double  time = (t2 - t1)*1E-9;
        System.out.println(time);

    }


    public void updateData(){
        
        getJListOfFiles().removeListSelectionListener(instance);

            DefaultListModel newList = generateDefaultListModel();
            getJListOfFiles().setModel(newList);
        getJListOfFiles().addListSelectionListener(instance);
        
        
        if (newList.isEmpty() == false){
            int curInd  =   newList.getSize() - 1;
            File file   =   (File)newList.get(curInd);
            
            getJListOfFiles().setSelectedValue (file, true);
            firePropertyChange(DATA_FILE_LOADED_MESSAGE, null, file);
        }
        else {
            getXydataset().removeAllSeries();
        }
    }

  
    private  void       addDataSeries(double [] x , double [] y, String xySeriesName){
        XYSeries series     = new XYSeries	(xySeriesName);
        for (int i = 0; i < x.length;  i++ ){series.add(x[i], y[i]);}
        getXydataset().addSeries(series );
    } 
    private void         deleteCurrentlySelectedData(){
         if (getJListOfFiles().isSelectionEmpty() ) { return;}
        
         int i                  =   getJListOfFiles().getSelectedIndex(); 
         File  curFile          =   getSelectedFile();
         
         getJListOfFiles().removeListSelectionListener(this);

            deleteFile(curFile);
            

         getJListOfFiles().addListSelectionListener(this);
        
       
         int newInd         = (i > 0)? i-1 : 0;
         getJListOfFiles().setSelectedIndex(newInd);
       
         // if there is no data to plot - clear plot
         if (getJListOfFiles().getModel().getSize() == 0){
             getXydataset().removeAllSeries ();
         }
       
        
    }
    public  void        deleteFile(File file){
        File  afhFile                   =   null;
        String fileSourceName           =   null;
        try{
            afhFile                     =   DirectoryManager.getAfhFileForData(file);
            try{
                fileSourceName              =    getAfhFileSource (afhFile  );
            }
            catch(Exception e){e.printStackTrace();}
            getXydataset().removeAllSeries ();

        }
        catch(Exception e){e.printStackTrace();}
        finally{
             if (file     != null && file.exists()){file    .delete();}
             if (afhFile  != null && afhFile.exists()){ afhFile .delete();}
             DefaultListModel model = generateDefaultListModel();
             ASCIIDataViewer.getInstance().getJListOfFiles().setModel(model);
             ASCIIDataViewer.getInstance().firePropertyChange (DATA_FILE_DELETED_MESSAGE, null, fileSourceName );
        }




    }

    public  void showGUI (JFrame frame) {   
       if (this.isShowing ())  { return;}
       if (frame == null ) {
          frame = new JFrame ();
          frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
       }
       frame.setContentPane (this);
       frame.pack ();
       frame.setVisible (true);
    } 
    public  void showGUI (JComponent pane) {   
       if (pane == null) {
          // showGUI(new JFrame());
           return;
       }
       pane.removeAll();
       pane.add (this);
       pane.revalidate();
       pane.repaint();
       
    } 


   
   
    public void setScatteringRenderer(){

        XYPlot plot         =    getPlot();
   

      //  XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

         XYLineAndShapeRenderer renderer  = new XYLineAndShapeRenderer();
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesShape(false);
        renderer.setBaseShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        renderer.setBaseLinesVisible(false);
        renderer.setBaseShapesFilled(false);
        renderer.setBaseShapesVisible(true);
        renderer.setBasePaint(Color.BLUE);
        
        plot.setRenderer(renderer);
    }
   
     public static   int [][]       getPlotInstructions(){
        Model model = PackageManager.getCurrentApplication();
        if (model == null){ return new int [][] {{0,1}};}
        if (model instanceof FidModel){ return new int [][] {{0,1}};}

        int c                       =   model.getNumberOfDataColumns();
        int [][] plot_instructions  =   new int [c][];

        for (int i = 0; i < plot_instructions.length; i++) {
            plot_instructions[i]    =   new int []{0,i+1};

        }
        return plot_instructions ;
    }
     public  static DefaultListModel generateDefaultListModel(){
        DefaultListModel dm     =   new DefaultListModel();
        File [] files           =    DirectoryManager.getAsciiDataFiles();
        if (files == null) {return dm;}

        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            dm.add(i, files[i]);

        }
        return dm;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList file_list;
    private javax.swing.JButton infoButton;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JPanel leftPane;
    private javax.swing.JPanel plotPane;
    private javax.swing.JButton remove;
    private javax.swing.JPanel tool_pane;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JList getJListOfFiles () {
        return file_list;
    }
    
    //************** setters and getters *********************//

     public  XYPlot getPlot(){
        return  (XYPlot)getChart().getPlot();
     }
     public  JFreeChart getChart(){
        return chartPanel.getChart();
     }
     public  void clearPlot(){
             getXydataset ().removeAllSeries();
     }

    public  File                getSelectedFile(){
        if (getJListOfFiles().isSelectionEmpty() ) {return null;}
        return (File) getJListOfFiles().getSelectedValue() ;

    }
    public  File                getSelectedAFHFile(){
        File imageFile  =  getSelectedFile();
        File file       = DirectoryManager.getAfhFileForData(imageFile);
        return file;
    }
    public  File                getLastDataFile(){
         List <File> files       =   getFileList() ;
         if (files.isEmpty()) {return null;}
         return files.get(files.size() - 1);
    }
    public  boolean             isDataLoaded(){
        int length =  getJListOfFiles ().getModel().getSize();
        if (length > 0) {return true;}
        else {return false;}
    }


    public  List <File>          getFileList()    {
        List <File> list        =     new ArrayList <File> ();
        DefaultListModel lm     =    (   DefaultListModel )getJListOfFiles ().getModel();

        for (int i = 0; i < lm.getSize(); i++) {
           list.add((File)lm.get(i));
       }
        return list;
    }
    public  File[]               getFiles()    {
        List <File> fl          =    getFileList();
        File[] files            =    new File[fl.size()];
        files                   =   fl .toArray(files);

        return files;
    }
    public  String[]             getFileNames()    {
        List <File> fl          =    getFileList();
        String[] names          =   new String[fl.size()];
        for (int i = 0; i < names.length; i++) {
            names[i]    = fl.get(i).getName();
        }

        return names;
    }


    public void                 viewAsciiFile() {
        File sf                 =   getSelectedFile();

         if (sf == null){

         }
         else{
             String content = IO.readFileToString(sf);
             Viewer.display(content, sf.getName());

         }

    }
    public void                 viewAsciiInfo() {
        File sf                 =   getSelectedFile();
        if (sf == null){

         }
        String txt          = getAscciDescriptor().toString();
        Viewer.display(txt, "Info for "+ getSelectedFile().getName() );


    }

    public String               getInfoForDataFile(int imageIndex) {
         File file          =   (File)getJListOfFiles().getModel().getElementAt(imageIndex);
        return getAsciiDataFileSource (file);
    }


    public static String        getAsciiDataFileSource(File file) {
         File afh           =   DirectoryManager.getAfhFileForData(file);
         return getAfhFileSource(afh );
    }
    public static String        getAfhFileSource(File afhfile) {
         AsciiDescriptor ad =   new AsciiDescriptor();
         try {
            ad              =   AsciiIO.loadFromDisk( afhfile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
         return ad.getSourceShortName();
    }
    public static String        getInfoForCurrentDataFile() {
        File file           =    ASCIIDataViewer.getInstance().getSelectedFile();
        if (file == null) {return null;}
         return   getAsciiDataFileSource(file);
    }



    private XYSeriesCollection   getXydataset () {
        return xydataset;
    }
    public  AsciiDescriptor      getAscciDescriptor () {
        return ascciDescriptor;
    }

    public void                 setAscciDescriptor ( AsciiDescriptor ascciDescriptor ) {
        this.ascciDescriptor = ascciDescriptor;
    }
    public void                 setSelectedFile(File file){
        getJListOfFiles().setSelectedValue(file, true) ;
    
    }

 

 
   class CustomCellRenderer extends JLabel implements ListCellRenderer {
        private  final Color HIGHLIGHT_COLOR        = new Color(0, 0, 128);
        private  final long serialVersionUID        = 7526472295628576147L;
        public CustomCellRenderer() {
        setOpaque(true);
        Font font   = new Font("Dialog", 1,16);
        this.setFont(font);
   //setIconTextGap(12);
  }

       public String getToolTip(){

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        String info     = getAscciDescriptor().getFullIunfo();
        Scanner scanner = new Scanner(info);
        while (scanner.hasNextLine()){

             sb.append(scanner .nextLine());
             sb.append("<br>");
        }

        sb.append("</html>");

        return sb.toString();

    }

        public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        File entry = (File) value;
        setText(entry.getName());

    if (isSelected) {
      setBackground(HIGHLIGHT_COLOR);
      setForeground(Color.white);
      setToolTipText(getToolTip());
    } else {
      setBackground(Color.white);
      setForeground(Color.black);
    }
    return this;
  }
 }
}    

