/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.*;
import java.util.ArrayList;

import java.awt.RenderingHints;

import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.chart.renderer.xy.*;


import java.awt.geom.*;
import java.text.DecimalFormat;
import static bayes.Enums.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.data.Range;
import org.jfree.chart.annotations.*;
import applications.bayesMetabolite.*;
import java.awt.FontMetrics;
import bayes.ApplicationConstants;
/**
 *
 * @author  apple
 */

public class FidChartPanel extends ChartPanel implements ApplicationConstants{

   

    private Line2D                  cursorA;
    private Line2D                  cursorB;
    public Double                   cursorAplotX            = null;
    public Double                   cursorBplotX             = null;
    public Double                   cursorAJavaX            = null;
    public Double                   cursorBJavaX             = null;


   // public final static Color       lineColor               = FidViewerPreferences.CURSOR_COLOR;//Color.RED;
    public final static Color       AXIS_COLOR              = Color.WHITE;
    public final static Color       PLOT_OUTLINE            = Color.RED;
    public final static Color       PLOT_BACKGROUND         = Color.BLACK;
    public final static Color       CHART_BACKGROUND        = Color.BLACK;
    public final static BasicStroke LINE_STROKE             = new BasicStroke(0.5f);
    public final static BasicStroke RES_STROKE              = new BasicStroke(1.5f);

    public final static Color       resColor                    =   Color.YELLOW;
    public final static int         RESONANCE_BAR_HEIGHT        =   30;
    public static final Font        resonanceFont               =   new Font("Dialog", Font.BOLD, 14);
    public static final String      resonanceFormat             =   "%4.4g";
    public static final BasicStroke linestroke                  =   new BasicStroke(2,1,1) ;
    private FID_DATA_TYPE           dataType                    =   FID_DATA_TYPE.SPECTRUM_REAL;
    private FID_CHART_MODE          chartType                   =   FID_CHART_MODE.SPECTRUM;

    private  static  NumberAxis fidDomainAxis         = new NumberAxis();
    private  static  NumberAxis fidRangeAxis          = new NumberAxis();
    static  NumberAxis spectrumDomainAxis             = new NumberAxis();
    static  NumberAxis spectrumRangeAxis              = new NumberAxis();
    private  static  NumberAxis otherRangeAxis        = new NumberAxis();


    public final static int         PLOT_MAX_WIDTH          = 3800;
    public final static int         PLOT_MAX_HEIGHT         = 3800;
    public final static int         PLOT_MIN_WIDTH          = 5;
    public final static int         PLOT_MIN_HEIGHT         = 5;
    public final static int         LOCAL_STAT_RADIUS       = 1;
    private static final long       serialVersionUID        = 4526371295622516147L; 
    private  boolean                isZoomingEnabled        = true;
    protected double                threshold;
   
    
   
    private static double           fidScaling              = 1.0;
    private static double           spectrumScaling         = 1.0;

    protected boolean drawResonances                        = true;
    protected boolean drawMetabolites                       = false;





    private FidChartPanel(JFreeChart aChart){
        
        super(aChart);
        this.setMouseZoomable(true);
        this.setMouseWheelEnabled(true);

        initializeAndSetupChart();
     }
 
    public  static  FidChartPanel getInstance(){
        return new FidChartPanel( initializeChart());
    }
    public  static  JFreeChart   initializeChart(){
       
         final  XYSeriesCollection  xydataset       = new XYSeriesCollection() ;
         final  XYItemRenderer renderer             = new XYLineAndShapeRenderer();
         
         XYPlot xyplot              =  new FastXYPlot( xydataset,
                                                         new NumberAxis(),
                                                         new NumberAxis(),
                                                        renderer );
         JFreeChart chart           = new JFreeChart (xyplot) ;
         return chart;
    }
    //********** Overridden Methods Start ***************//
    @Override
    public void	mouseClicked(java.awt.event.MouseEvent evt) {
          //mousePressedOrDragged(evt);
    }
    
    @Override
    public void	mouseReleased(java.awt.event.MouseEvent evt) {
          
    }
    @Override
    public void	mouseDragged(java.awt.event.MouseEvent evt) {
            mousePressedOrDragged(evt);
    }
    @Override
    public void	mousePressed(java.awt.event.MouseEvent evt) {
            mousePressedOrDragged(evt);
    }
    
    public void mousePressedOrDragged(java.awt.event.MouseEvent evt){
        double posX             =   (double) evt.getX();            // java2D posX
        double posY             =   (double) evt.getY();             // java2D posX
        double maxX             =   getScreenDataArea().getMaxX() -  this.getInsets().right  ; // java2D max
        double maxY             =   getScreenDataArea().getMaxY(); // java 2D
        double minY             =   getScreenDataArea().getMinY(); // java 2D
        double minX             =   getScreenDataArea().getMinX(); // java 2D
        
        
        // IF RIGHT MOUSE BUTTON EVENT
        if (  SwingUtilities.isRightMouseButton(evt) == true){
         // if mouse is outside the plot area
            if (posX > maxX     ||  posX < minX   ) { return;}
            if (posY > maxY     ||  posY  < minY) { return;}
             
            if (getCursorA() == null){ drawCursorA(minX );}
             double posB         =  posX;
             double posA         =  getCursorA().getX1();
             
             if ( posA  == maxX)    { posB = maxX;}
             else if (posB < posA) { posB   = posA+1;}
             drawCursorB(posB);
        }
        
        
        // IF LEFT MOUSE BUTTON EVENT
        else if ( SwingUtilities.isLeftMouseButton(evt) == true
                 && evt.isAltDown() == false)
        {
            if (posX > maxX      || posX < minX   ) { return;}
            if (posY > maxY     ||  posY  < minY) { return;}
            
            if (getCursorB() ==  null){drawCursorA(posX);}
            else {
                double posB         =  getCursorB().getX1();
                double posA         =  getCursorA().getX1();
                double diff = posB - posA;
             
                if ( (posX + diff) > maxX ){ 
                    posB  =   maxX;
                    posA  =   maxX - diff;
                }
                 else{
                    posA  = posX;
                    posB  = posA + diff;
                }
                drawCursorA(posA);
                drawCursorB(posB);
            }
             
         }
        
        // IF MIDDLE MOUSE BUTTON EVENT.
        // OR LEFT MOUSE EVENT ALONG WITH ALT//OPTION BUTTON PRESSED
        else if ( SwingUtilities.isMiddleMouseButton(evt) == true
                            || 
                (  SwingUtilities.isLeftMouseButton(evt) == true
                                             && evt.isAltDown() == true))
        {    
            if (  isZoomingEnabled () == false) { return;}
            if (posX > maxX  ) { return;}
             
          
             scaleRangeAxis(evt);
           
            
        }
    }

    @Override
    public void	paintComponent(java.awt.Graphics g){ 
       g.setColor(PLOT_BACKGROUND);

       cursorB = null;
       cursorA = null;
       
       super.paintComponent(g); 

       if( cursorBJavaX != null){   drawCursorB(cursorBJavaX, g); }
       if( cursorAJavaX != null){   drawCursorA(cursorAJavaX, g); }


       int numSeries                =   getDataset().getSeriesCount();
       if (numSeries > 0){
            if (isDrawResonances ())  {  paintResonances(g);}
            if (isDrawMetabolites ())  { paintMetabolites(g);}
       }
      

       
   }


     public void	paintResonances(java.awt.Graphics g){
         Graphics2D g2D = (Graphics2D)g;
         g2D.setColor(resColor);
         g2D.setStroke(RES_STROKE);
         g2D.setPaintMode();
         g2D.setFont(resonanceFont);

         FidViewable viewer               =  FidViewer.getInstance();
         if (this.getDataType() == FID_DATA_TYPE.FID) {return;}

         FontMetrics fm      =    g2D.getFontMetrics();
         int spacing         =    fm.getHeight()+ 1;


            ArrayList <Resonance> resList   =   Resonance.getResonanceList();
            UNITS hertz                     =   UNITS.HERTZ;
            int   plotH                     =   getHeight();
            int   plotW                     =   getWidth();

            double baseLevel                =   plotH/3;
            double topVetLineLength         =   plotH/12;
            double btmVetLineLength         =   plotH/8;
            double x1                       =   0;
            double y1                       =   0;
            double x2                       =   0;
            double y2                       =   0;
            double prevx2                   =   -1;


           for (Resonance resonance : resList) {
             ArrayList <Double> freqList         =  DrawResonances.getResonanceFrequenciesInHertz(resonance, viewer );
             int  numFreq                        =  freqList.size();

            // draw vertical lines

            for (int i = 0; i < numFreq; i++) {
                double coord        =   freqList.get(i);
                double xunit        =   Units.convertToFidVieweableUnits(coord, hertz,viewer);
                x1                  =   xunit;
                x2                  =   xunit;
                y1                  =   baseLevel;
                double plotx        =   plotX2Java2D(xunit);
                x1                  =   plotx;
                x2                  =   plotx;

                if ( i == 0)  {
                    y2  =   y1 - topVetLineLength    ;
                    

                    // first time skip
                    if (prevx2 < 1){ }
                    else if (  prevx2 - x2 < spacing){  x2  = prevx2 - spacing;}
                      
                    prevx2                  =   x2;
                   
                    String txt              =   getResonanceText(viewer, resonance);
                    AffineTransform tr      =   getLabelTransform ( g2D, fm,y2, x2);
                    drawLabel( g2D, tr, txt);

                }
                else  {
                    y2  =   y1 + btmVetLineLength    ;
                }


                Line2D line = new Line2D.Double(x1, y1, x2, y2);
                g2D.draw(line);
               
            }



            // draw horizontal lines
            if (numFreq > 1)
            {
               double coord;
                coord               =   freqList.get(1);
                x1                  =   Units.convertToFidVieweableUnits(coord, hertz,viewer);
                coord               =   freqList.get(numFreq -1);
                x2                  =   Units.convertToFidVieweableUnits(coord, hertz,viewer);

                x1                  =   plotX2Java2D(x1);
                x2                  =   plotX2Java2D(x2);
                y1                  =   baseLevel;
                y2                  =   baseLevel;

               Line2D line = new Line2D.Double(x1, y1, x2, y2);
               g2D.draw(line);

            }


        }
  }
     public void	paintMetabolites(java.awt.Graphics g){
         Graphics2D g2D = (Graphics2D)g;
         g2D.setColor(resColor);
         g2D.setStroke(RES_STROKE);
         g2D.setPaintMode();
         g2D.setFont(resonanceFont);


         FidViewable viewer               =  FidViewer.getInstance();
         if (this.getDataType() == FID_DATA_TYPE.FID) {return;}

         FontMetrics fm      =    g2D.getFontMetrics();
         int spacing         =    fm.getHeight()+ 1;



          ArrayList< Metabolite> metabs     =   MetabolitesAndResonances.getMetabolitesAndResonances().getAllMetabolitesAndResonances();
            double baseLevel                =   this.getHeight()/2;
            double x1                       =   0;
            double y1                       =   0;
            double x2                       =   0;
            double y2                       =   0;


          for (Metabolite metabolite :  metabs ) { {
             ArrayList <Double> freqList         =  DrawResonances. getMetabolitesFrequenciesInFIDUnits(metabolite, viewer);
             int  numFreq                        =  freqList.size();

            // draw vertical lines and label

            for (int i = 0; i < numFreq; i++) {
                double coord        =   freqList.get(i);
                x1                  =   coord;
                x2                  =   x1;
                y1                  =   baseLevel;
                x1                  =   plotX2Java2D(x1);
                x2                  =   x1;

                if ( i == 0)  {
                    y2  =   y1 - RESONANCE_BAR_HEIGHT  ;

                   
                    int index               =   metabs .indexOf(metabolite) + 1;
                    String txt              =   getMetabliteText(viewer, metabolite, index);
                    double y3               =   baseLevel - RESONANCE_BAR_HEIGHT;
                    AffineTransform tr      =   getLabelTransform ( g2D, fm,y3, x2);
                    drawLabel( g2D, tr, txt);

                }
                else  {
                    y2  =   y1 + RESONANCE_BAR_HEIGHT  ;
                }

                Line2D line = new Line2D.Double(x1, y1, x2, y2);
                g2D.draw(line);


            }


            // draw horizontal lines
            if (numFreq > 1)
            {
                x1                  =   freqList.get(1);
                x2                  =   freqList.get(numFreq -1);
                x1                  =   plotX2Java2D(x1);
                x2                  =   plotX2Java2D(x2);
                y1                  =   baseLevel;
                y2                  =   baseLevel;

               Line2D line = new Line2D.Double(x1, y1, x2, y2);
               g2D.draw(line);

            }
          }
       }
  }
  

    public String getResonanceText(  FidViewable viewer , Resonance resonance  ){
            double val  =   resonance.getFreqFinalVal();
            double cf   =   Units.convertToFidVieweableUnits(val,  resonance.getUnits(),viewer);
            int index   =   Resonance.getResonanceList().indexOf(resonance) + 1;
            String txt  =   String.format(resonanceFormat + " ( "+ index +  " )", cf);
            return  txt;
     }
    public String getMetabliteText(  FidViewable viewer , Metabolite   metabolite, int index ){
            double val  =   metabolite.getPrimaryFrequency();
            double cf   =   Units.convertToFidVieweableUnits(val,  metabolite.getFrequencyUnits(),viewer);
            String txt  =   String.format(resonanceFormat + " ( "+ index +  " )", cf);
            return  txt;
     }

    public void drawLabel( Graphics2D g2D, AffineTransform trns,String txt){
                             
            AffineTransform saveAT  =  g2D.getTransform();

            g2D.transform(trns);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawString(txt, 0, 0);

            // Restore original transform
            g2D.setTransform(saveAT);
     }
    public  AffineTransform  getLabelTransform(Graphics2D g2D, FontMetrics fm, double y, double x){
            double dx               =    x  + fm.getMaxAscent()/2 ;
            double dy               =    y - 5 ;

            AffineTransform trns    =   AffineTransform.getTranslateInstance(dx, dy);
            AffineTransform out     =   AffineTransform.getRotateInstance(-Math.PI/2);
            trns.concatenate(out);

            return  trns;

     }
    //********** Overridden Methods End ***************//
    private void initializeAndSetupChart(){
        JFreeChart  chart = getChart();
        FastXYPlot plot = ( FastXYPlot)chart.getXYPlot();



        boolean isPannable = ( plot instanceof Pannable);
        if (isPannable ){
                plot.setRangePannable(true);
                plot.setDomainPannable(true);
         }
      
        //this.setHorizontalAxisTrace(true);
        chart.setBackgroundPaint(CHART_BACKGROUND);
        chart.removeLegend();
        plot.setOutlinePaint(null);
        plot.setBackgroundPaint(PLOT_BACKGROUND);
        
        Color def         =  new Color(255,255,160,255);//
        Color blue        =  new Color(0.5f,0.5f,1,1f);

     
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
       
        renderer.setSeriesPaint(0, FidViewerPreferences.TRACE_1_COLOR);
        renderer.setSeriesPaint(1, FidViewerPreferences.TRACE_2_COLOR);
        renderer.setSeriesPaint(2, blue);//  blue
        for (int i = 3; i < 30; i++) {
           renderer.setSeriesPaint(3, def);
            
        }
        
        renderer.setSeriesStroke(0, new BasicStroke(1),false);
        renderer.setSeriesStroke(1, new BasicStroke(1),false);
        renderer.setSeriesStroke(2, new BasicStroke(1),false);
        renderer.setBaseStroke  (   new BasicStroke(1), false);
        
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        
        setMaximumDrawHeight( PLOT_MAX_HEIGHT );
        setMaximumDrawWidth(  PLOT_MAX_WIDTH  );
        setMinimumDrawHeight( PLOT_MIN_HEIGHT );
        setMinimumDrawWidth(  PLOT_MIN_WIDTH  );
        
      
        DecimalFormat nf = new DecimalFormat() ;
        //DecimalFormat nf = new DecimalFormat("##########0.#########") ;
        nf.setGroupingUsed(false);
    
        fidDomainAxis.setAxisLinePaint(AXIS_COLOR );
        fidDomainAxis.setLabelPaint(AXIS_COLOR );
        fidDomainAxis.setTickLabelPaint(AXIS_COLOR );
        fidDomainAxis.setNumberFormatOverride(nf);
        fidDomainAxis.setInverted(false);
        fidDomainAxis.setLabel("TIME");
        fidDomainAxis.setAutoRangeStickyZero(false);
        
        
        spectrumDomainAxis.setAxisLinePaint(AXIS_COLOR );
        spectrumDomainAxis.setLabelPaint(AXIS_COLOR );
        spectrumDomainAxis.setTickLabelPaint(AXIS_COLOR );
        spectrumDomainAxis.setNumberFormatOverride(nf);
        spectrumDomainAxis.setInverted(true);
        
       



        boolean rangeTicksVisible = false;
   //      fidRangeAxis.setAxisLinePaint(AXIS_COLOR );
   //      fidRangeAxis.setLabelPaint(AXIS_COLOR );
    //     fidRangeAxis.setTickLabelPaint(AXIS_COLOR );
            fidRangeAxis.setTickLabelPaint(AXIS_COLOR );
        fidRangeAxis.setTickLabelsVisible(rangeTicksVisible);
       // fidRangeAxis.setLabel("DATA");
     
        spectrumRangeAxis.setTickLabelPaint(AXIS_COLOR );
        spectrumRangeAxis.setTickLabelsVisible(rangeTicksVisible);
                
        otherRangeAxis.setTickLabelPaint(AXIS_COLOR );
        otherRangeAxis.setTickLabelsVisible(rangeTicksVisible);
       
     
        fidRangeAxis.setVisible(rangeTicksVisible);
        spectrumRangeAxis.setVisible(rangeTicksVisible);
        otherRangeAxis.setVisible(rangeTicksVisible);

         plot.getRangeAxis().setVisible(false);


   }
    
    public static  void resetRangeScaling (){
            fidScaling              = 1.0;
            spectrumScaling         = 1.0;
    }
    public void expandPlot(){
        if (getCursorA() == null || getCursorB() == null) { return;} 
        double low      = getLowerBoundFromCursorBox();
        double high     = getUpperBoundFromCursorBox();

        Range dataDomainRange   = getDataRangeForDomainAxis();

        low             =   Math.max(dataDomainRange.getLowerBound(), low);
        high            =   Math.min(dataDomainRange.getUpperBound(), high);


        if (high <= low) {return;}

        getDomainAxis().setRange(low, high);
 
        deleteCursorA( );
        deleteCursorB( );        
    }
    public void autoScaleDomainAxis(){
        double scale = 0.05;
        Range range;
        double length;
        double lowerBound;
        double upperBound;

        range           =   getDataRangeForDomainAxis();
        length          =   range.getLength();
        lowerBound      =   range.getLowerBound() - length*scale;
        upperBound      =   range.getUpperBound() + length*scale;

        range           =   new Range(lowerBound,upperBound);
        getDomainAxis().setRange( range);


    }
    public void autoScaleRangeAxis(){
        Range range           =    getRangeForRangeAxis();
        getRangeAxis().setRange( range);
    }
    public Range getRangeForRangeAxis(){
        double scale = 0.05;
        Range range;
        double length;
        double lowerBound;
        double upperBound;

        range           =   getDataRangeForRangeAxis();
        length          =   range.getLength();
        lowerBound      =   range.getLowerBound() - length*scale;
        upperBound      =   range.getUpperBound() + length*scale;

        range           =   new Range(lowerBound,upperBound);
        return range;
    }
    public void autoScale(){
       autoScaleDomainAxis();
       autoScaleRangeAxis();
    }
    
    public double getMouseX(java.awt.event.MouseEvent evt){
          
        int x = evt.getX(); 
        int y = evt.getY(); 
        
        // the following translation takes account of the fact that the chart image may 
        // have been scaled up or down to fit the panel... 
        Point2D p = translateScreenToJava2D(new Point(x, y)); 
        
        // now convert the Java2D coordinate to axis coordinates... 
        XYPlot plot = getChart().getXYPlot(); 
        Rectangle2D dataArea = getChartRenderingInfo().getPlotInfo().getDataArea(); 
        //Rectangle2D dataArea    =   getScreenDataArea();
        
         double xx =  getDomainAxis().java2DToValue( 
            p.getX(), dataArea, plot.getDomainAxisEdge() 
        ); 

        double yy =  getRangeAxis().java2DToValue( 
            p.getY(), dataArea, plot.getRangeAxisEdge() 
        ); 
        return xx;
        /*
        // just for fun, lets convert the axis coordinates back to component coordinates... 
        double xxx = plot.getDomainAxis().valueToJava2D(xx, dataArea, plot.getDomainAxisEdge()); 
        double yyy = plot.getRangeAxis().valueToJava2D(yy, dataArea, plot.getRangeAxisEdge()); 
        Point2D p2 = chartPanel.translateJava2DToScreen(new Point2D.Double(xxx, yyy)); 
        System.out.println("Mouse coordinates are (" + x + ", " + y 
            + "), in data space = (" + xx + ", " + yy + ")."); 
         */
    }
    public double getMouseY(java.awt.event.MouseEvent evt){
          
        int x = evt.getX(); 
        int y = evt.getY(); 
        
        // the following translation takes account of the fact that the chart image may 
        // have been scaled up or down to fit the panel... 
        Point2D p = translateScreenToJava2D(new Point(x, y)); 
        
        // now convert the Java2D coordinate to axis coordinates... 
        XYPlot plot = getChart().getXYPlot(); 
        Rectangle2D dataArea = getChartRenderingInfo().getPlotInfo().getDataArea(); 
        //Rectangle2D dataArea    =   getScreenDataArea();
        
      
        double yy = plot.getRangeAxis().java2DToValue( 
            p.getY(), dataArea, plot.getRangeAxisEdge() 
        ); 
        return yy;
        /*
        // just for fun, lets convert the axis coordinates back to component coordinates... 
        double xxx = plot.getDomainAxis().valueToJava2D(xx, dataArea, plot.getDomainAxisEdge()); 
        double yyy = plot.getRangeAxis().valueToJava2D(yy, dataArea, plot.getRangeAxisEdge()); 
        Point2D p2 = chartPanel.translateJava2DToScreen(new Point2D.Double(xxx, yyy)); 
        System.out.println("Mouse coordinates are (" + x + ", " + y 
            + "), in data space = (" + xx + ", " + yy + ")."); 
         */
    }
    
    public double plotX2Java2D(double x){
       Rectangle2D dataArea    =   getChartRenderingInfo().getPlotInfo().getDataArea(); 
       // Rectangle2D dataArea    =   getScreenDataArea();
       
       XYPlot plot             =   getChart().getXYPlot();
       double xx               =   getDomainAxis().valueToJava2D(x, dataArea, plot.getDomainAxisEdge()); 
       return xx;
    }
    public double plotY2Java2D(double y){
       Rectangle2D dataArea    =   getChartRenderingInfo().getPlotInfo().getDataArea(); 
       // Rectangle2D dataArea    =   getScreenDataArea();
       
       XYPlot plot             =   getChart().getXYPlot();
       double yy               =   getRangeAxis().valueToJava2D(y, dataArea, plot.getRangeAxisEdge()); 
       return yy;
    }
    public Point2D plotXY2JavaPoint2D(double x, double y){
        double xx   =  plotX2Java2D(x);
        double yy   =  plotY2Java2D(y);
        return new Point2D.Double(xx, yy);
    }
    
    public  double  java2DToPlotX(double x){
        
       // the following translation takes account of the fact that the chart image may 
       // have been scaled up or down to fit the panel... 
        Insets insets = getInsets();
         x = (x - insets.left) / this.getScaleX();
         
       Rectangle2D dataArea    =   getChartRenderingInfo().getPlotInfo().getDataArea(); 
       //Rectangle2D dataArea    =   getScreenDataArea();
        XYPlot plot             =   getChart().getXYPlot();
        double xx =getDomainAxis().java2DToValue( 
           x, dataArea, plot.getDomainAxisEdge() );
        
        return xx;
    }
    public  double  java2DToPlotY(double y){
       Rectangle2D dataArea    =   getChartRenderingInfo().getPlotInfo().getDataArea(); 
       
       XYPlot plot             =    getChart().getXYPlot();
       double yy               =    getRangeAxis().java2DToValue( 
                                        y, dataArea, plot.getRangeAxisEdge() );
        
       return yy;
    }
    
    public  void    drawCursorA(  double pos){ 
         drawCursorA(pos, getGraphics());
    }
    public  void    drawCursorB(  double pos){ 
        drawCursorB(pos, getGraphics());
    }
    
    public  void    drawCursorA(  double pos, Graphics g ){ 
        Rectangle2D dataArea = getScreenDataArea(); 
        if(dataArea     ==      null){return; }
        double maxY =    dataArea.getMaxY();
        double minY =    dataArea.getMinY();
    
        Graphics2D g2 = (Graphics2D)g; 
        g2.setStroke(LINE_STROKE  );
        g2.setXORMode( FidViewerPreferences.CURSOR_COLOR);//Color of crosshair
        
       if (  dataArea.getMinX() <= pos && dataArea.getMaxX() >= pos ) {
       
           //^Delete(Overwrite) previous draw line 
           if( getCursorA() !=null ){   g2.draw(getCursorA()); } 
              
            setCursorA(new Line2D.Double(pos,  minY, pos, maxY));
            g2.draw( getCursorA());
           
            cursorAJavaX    =  pos;
            cursorAplotX    =  java2DToPlotX(pos);

            this.firePropertyChange(CURSOR_A_IS_DRAWN ,null, cursorAplotX);
       } 
               
           
        
      }
    public  void    drawCursorB(  double pos, Graphics g){ 
        Rectangle2D dataArea = getScreenDataArea(); 
        if(     dataArea    ==null){ return;}
        
        double maxY =    dataArea.getMaxY();
        double minY =    dataArea.getMinY();
    
        Graphics2D g2 = (Graphics2D) g; 
        g2.setStroke(LINE_STROKE  );
        g2.setXORMode( FidViewerPreferences.CURSOR_COLOR);//Color of crosshair
        
        if (  dataArea.getMinX() <= pos  && dataArea.getMaxX() >= pos ){ 
        
             //^Delete(Overwrite) previous draw line 
            if( getCursorB() !=null ){  g2.draw(getCursorB()); }
            
            setCursorB(new Line2D.Double(pos, minY, pos, maxY )); 
            g2.draw( getCursorB());


            cursorBJavaX    =   pos;
            cursorBplotX    =   java2DToPlotX(pos);

            this.firePropertyChange(CURSOR_B_IS_DRAWN ,null,cursorBplotX);
       } 
    }
    
    public  double  getCursorAPosition(){
        if (getChart() == null){ return 0;}
        if (getCursorA() == null){  return 0;}
        return java2DToPlotX(getCursorA().getX1());
    }
    public  double  getCursorBPosition(){
        if (getChart()  == null){ return 0;}
        if (getCursorB() == null){  return 0;}
        return java2DToPlotX(getCursorB().getX1());
    }
   
    public  double  getBoxPosition(){
        if (getChart() == null){ return 0;}
        if (getCursorA() == null){  return 0;}
        if (getCursorB() == null){  return 0;}
        // boxPos is in PlotCoordinates
        double boxPos = ( getCursorAPosition() +  getCursorBPosition())/2;
        return boxPos;
    }
    public  boolean isBox(){
        if (getCursorA() != null && getCursorB() != null ){return true;}
        else {return false;}
    }
    
    void  deleteCursorA( ){
        if (getCursorA() == null) { return;}
        Rectangle2D dataArea = getScreenDataArea(); 
        if(dataArea==null){return; }
    
        Graphics2D g2 = (Graphics2D) getGraphics(); 
        g2.setStroke(LINE_STROKE  );
        g2.setXORMode( FidViewerPreferences.CURSOR_COLOR);//Color of crosshair
        
        double pos = getCursorA().getX1();
        if (((int) dataArea.getMinX() < pos) && (pos < (int) dataArea.getMaxX())){ 
                //^Delete(Overwrite) previous draw line 
                g2.draw(getCursorA()); 
                cursorAplotX  =  null;
                cursorAJavaX =  null;
      } 
        setCursorA(null);

        this.firePropertyChange(CURSOR_A_IS_DELETED, false, true);
      }
    void  deleteCursorB( ){
        if (getCursorB() == null) { return;}
        Rectangle2D dataArea = getScreenDataArea(); 
        if(dataArea==null){return; }
    
        Graphics2D g2 = (Graphics2D) getGraphics(); 
        g2.setStroke(LINE_STROKE  );
        g2.setXORMode( FidViewerPreferences.CURSOR_COLOR);//Color of crosshair
        
        double pos = getCursorB().getX1();
        if (((int) dataArea.getMinX() < pos) && (pos < (int) dataArea.getMaxX())){ 
                //^Delete(Overwrite) previous draw line 
                g2.draw(getCursorB()); 
                cursorBplotX  = null;
                cursorBJavaX  = null;

      } 
        setCursorB(null);
        this.firePropertyChange(CURSOR_B_IS_DELETED, false, true);
      }
    void  deleteCursors(){
          deleteCursorA( );
          deleteCursorB( );
    }
    
    private void        scaleRangeAxis(java.awt.event.MouseEvent evt){
       
        Range rangeAxisRange    =   getRangeAxisRange();
        Range localDataRange    =   getLocalDataRange(evt, LOCAL_STAT_RADIUS);
        
        double mouse_plot_y     =   getMouseY(evt);
        double localDataMax     =   localDataRange.getUpperBound();
        double localDataMin     =   localDataRange.getLowerBound();

       
        double localDataMaxAbs  =   Math.abs(localDataMax);
        double mouse_plot_yAbs  =   Math.abs(mouse_plot_y);
        
        double scale            =   1.0 ;
        double curScale         =   getRangeScaling();
        double curThreshold     =   getThreshold()*curScale;
        
        if (curThreshold < 0) { return;}
        
        FID_CHART_MODE type  = getChartMode();
        
        
        switch (type){
            case FID  :     if  (mouse_plot_y  < rangeAxisRange.getLowerBound()){
                                       scale                  =  0.6 ;
                            }

                            else if( mouse_plot_y > localDataMax){
                                scale   = 1.4;
                            }
                            else if( mouse_plot_y < localDataMin){
                                 scale   = 0.6;
                            }
                
                           scaleRangeAxis(scale);
                           break;
            case SPECTRUM : 
                            if  (mouse_plot_y  < rangeAxisRange.getLowerBound()){
                                       scale                  =  0.25 ;
                            }  
                            else if  (mouse_plot_y  < -curThreshold){
                                       scale                  =  0.5 ;
                            }  
                            else if (mouse_plot_y  < curThreshold ) {
                                     scale                  =  1.25 ;
                            }   
                            else{
                                   if (localDataMax < curThreshold ){
                                       scale                  =  1.5 ;
                                   }
                                   else {
                                        scale = mouse_plot_yAbs/localDataMaxAbs;
                                   }
                            }

                             scaleRangeAxis(scale);
                    
                            break;
         }
     
       
        
   }
    void                shiftRangeAxis(double  mouse_plot_y ){
        Range rangeAxisRange    =   getRangeAxisRange();

        double upper            =   rangeAxisRange.getUpperBound();
        double lower            =   rangeAxisRange.getLowerBound();
        double offset           =   (0  - mouse_plot_y) ;
        getRangeAxis().setRange(lower + offset, upper +offset);
   }
    void                scaleRangeAxis(double  scale ){
        Range rangeAxisRange    =   getRangeAxisRange();

        double upper            =   rangeAxisRange.getUpperBound();
        double lower            =   rangeAxisRange.getLowerBound();
        getRangeAxis().setRange(lower/scale, upper/scale);
   }



    
    public  Point2D getClosestPoint(java.awt.event.MouseEvent evt){ 
      // get the chart coordinates for the mouse click 
      double  mouseX = getMouseX(evt); // in plot coordinates
       
      // try to locate the closest data point in the (first dataset of the) panel 
      double min_dist = Double.MAX_VALUE; 
      
      XYDataset set     = getChart().getXYPlot().getDataset(); 
      int       n       = set.getItemCount(0); 
      
      Point2D  p        = null;
      for (int j = 0 ; j < n ; j++) { 
         
         double dist = Math.abs(set.getXValue(0,j) - mouseX); 
                   
         if(    dist    <   min_dist){ 
                min_dist = dist ; 
                p = new  Point2D.Double (set.getXValue(0,j), set.getYValue(0,j));
            } 
      } 
      return p;
      
    }
    public  int     getClosestPointIndex(double x_coord){ 
      // try to locate the closest data point in the (first dataset of the) panel 
      double min_dist = Double.MAX_VALUE; 
      
      XYDataset set     = getChart().getXYPlot().getDataset(); 
      int       n       = set.getItemCount(0); 
      int index         = 0;
     
      for (int j = 0 ; j < n ; j++) { 
         
         double dist = Math.abs(set.getXValue(0,j) - x_coord); 
                   
         if(    dist    <   min_dist){ 
                min_dist = dist ; 
                index = j;
         } 
      } 
      return index;
      
    }
    public  int     getClosestPointIndex(java.awt.event.MouseEvent evt){ 
      // get the chart coordinates for the mouse click 
      double  mouseX = getMouseX(evt); // in plot coordinates
      return getClosestPointIndex(mouseX);
    }
    public  int     getClosestPointIndexToCursorA(){ 
      double posA = getCursorAPosition() ;      // in plot coordinates
      return getClosestPointIndex(posA);
    }
    public  int     getClosestPointIndexToCursorB(){ 
      double posB = getCursorBPosition() ;      // in plot coordinates
      return getClosestPointIndex(posB);
    }
    public  double  getLocalMaximum (java.awt.event.MouseEvent evt, int radius){ 
      int index             = getClosestPointIndex(evt);
      
      XYDataset dataset     = getChart().getXYPlot().getDataset(); 
      int       n           = dataset.getItemCount(0); 
      
      int lowIndex      = (index -radius < 0)? 0    : index -radius;
      int highIndex     = (index +radius > n)? n -1 : index +radius;
      double max        =  -Double.MAX_VALUE;
      int maxIndex      = 0;

      for (int j = lowIndex ; j < highIndex ; j++) { 
         
         double y = dataset.getYValue(0,j);
         if(    max    <   y){ 
             maxIndex   =   j;
             max        =   y;
         }
         
      } 
      return max;
      
    }   
    public  double  getLocalMinimum (java.awt.event.MouseEvent evt, int radius){ 
      int index             = getClosestPointIndex(evt);
      
      XYDataset dataset     = getChart().getXYPlot().getDataset(); 
      int       n           = dataset.getItemCount(0); 
      
      int lowIndex      = (index -radius < 0)? 0    : index -radius;
      int highIndex     = (index +radius > n)? n -1 : index +radius;
      double min        =  Double.MAX_VALUE;
      int minIndex      = 0;

      for (int j = lowIndex ; j < highIndex ; j++) { 
         
         double y = dataset.getYValue(0,j);
         if(    min    >   y){ 
             minIndex   =   j;
             min       =   y;
         }
         
      } 
      return min;
      
    }   
    
    public  Range   getDataRangeForRangeAxis(){
      
      XYDataset dataset     = getChart().getXYPlot().getDataset(); 
      
      
      double max            =   -Double.MAX_VALUE;
      double min            =   Double.MAX_VALUE;
      int nSeries           =   dataset.getSeriesCount();
        if (nSeries == 0) {return new Range(0, 1);}

      
        for (int curSeries   = 0; curSeries < nSeries ; curSeries++) {
           int lowIndex          =  0;
           int highIndex         =  dataset.getItemCount(curSeries);


             for (int curItem = lowIndex ; curItem < highIndex ; curItem++) {
         
                 double y = dataset.getYValue(curSeries,curItem);
                 max      = Math.max(max, y);
                 min      = Math.min(min, y);
             }

        }
      Range range = new Range(min, max);
      return range;
    }
    public  Range   getDataRangeForDomainAxis(){

      XYDataset dataset     = getChart().getXYPlot().getDataset();


      double max            =   -Double.MAX_VALUE;
      double min            =   Double.MAX_VALUE;

      int nSeries           =   dataset.getSeriesCount();
      if (nSeries == 0) {return new Range(0, 1);}

      
        for (int curSeries   = 0; curSeries < nSeries ; curSeries++) {
           int  n           =  dataset.getItemCount(curSeries);

           double x0        =   dataset.getXValue(curSeries,0);
           double xn        =   dataset.getXValue(curSeries,n-1);
           double curMax    =   Math.max(x0, xn);
           double curMin    =   Math.min(x0, xn) ;
           
           max              =   Math.max(max,curMax);
           min              =   Math.min(min, curMin);
        }
      Range range = new Range(min, max);
      return range;
    }

    public  Range   getLocalDataRange(java.awt.event.MouseEvent evt, int radius){
       int index             = getClosestPointIndex(evt);

      XYDataset dataset     = getChart().getXYPlot().getDataset();
      int       n           = dataset.getItemCount(0);
      int lowIndex          = (index -radius < 0)? 0    : index -radius;
      int highIndex         = (index +radius > n)? n -1 : index +radius;

      double max            =   -Double.MAX_VALUE;
      double min            =   Double.MAX_VALUE;
      int minIndex          =   0;
      int maxIndex          =   0;

      for (int j = lowIndex ; j < highIndex ; j++) {

         double y = dataset.getYValue(0,j);
         if( max  < y){
             maxIndex   =   j;
             max        =   y;
         }
        if(  min > y){
             minIndex   =   j;
             min        =   y;
         }

      }
      Range localRange = new Range(min, max);
      return localRange;
    }

    public  double  getLowerBoundFromCursorBox(){
        if (getChart()  == null){  return Double.NaN;}
        if (getCursorA() == null){  return Double.NaN;}
        if (getCursorB() == null){  return Double.NaN;}
       
        if (getDomainAxis().isInverted() == true){
               return java2DToPlotX(getCursorB().getX1());
        }
        else{
               return java2DToPlotX(getCursorA().getX1());
        }
    }
    public  double  getUpperBoundFromCursorBox(){
        if (getChart()  == null){  return Double.NaN;}
        if (getCursorA() == null){  return Double.NaN;}
        if (getCursorB() == null){  return Double.NaN;}
       
        if (getDomainAxis().isInverted() == true){
               return java2DToPlotX(getCursorA().getX1());
        }
        else{
               return java2DToPlotX(getCursorB().getX1());
        }
    }

    
    public XYSeriesCollection getDataset(){
        XYPlot plot             =   getChart().getXYPlot();
        return (XYSeriesCollection ) plot.getDataset();
    }
  

    public Line2D       getCursorA () {
        return cursorA;
    }
    public Line2D       getCursorB () {
        return cursorB;
    }
    public void         setCursorA ( Line2D cursorA ) {
        this.cursorA = cursorA;
    }
    public void         setCursorB ( Line2D cursorB ) {
        this.cursorB = cursorB;
    }

    public boolean      isZoomingEnabled () {
        return   isZoomingEnabled;
    }
    public void         setIsZoomingEnabled ( boolean enableZoom ) {
        this.isZoomingEnabled = enableZoom;
    }

    
    public XYPlot       getXYPot(){
        return getChart().getXYPlot();
    }
    public Range        getRangeAxisRange(){
        Range range             = null;
        
        if (getRangeAxis() == null ) {return null;}
            range               =  getRangeAxis().getRange();
        if (range == null) {
             range              =   getRangeAxis().getDefaultAutoRange(); 
        }
        return range;
    }
    public Double       getRangeAxisUpperBound(){
        Range range             =  getRangeAxisRange();
        if (range == null) {    return null ;}
        else               {    return range.getUpperBound();}
    }
    public Double       getRangeAxisLowerBound(){
        Range range             =  getRangeAxisRange();
        if (range == null) {    return null ;}
        else               {    return range.getUpperBound();}
    }
    
   @Override
   protected void               displayPopupMenu(int x, int y){
       super.displayPopupMenu(x, y);
    }
   public    void               plotClearOfDrawings(){
           XYPlot plot                     =   getChart().getXYPlot();
        
        for (Object object : plot.getAnnotations()) {
            XYAnnotation an = (XYAnnotation)  object;
            plot.removeAnnotation(an, false);
        }

    }
  
    public double               getThreshold () {
        return threshold;
    }
    
    public  FID_CHART_MODE      getChartMode () {
        return chartType;
    }
    
    public   NumberAxis         getDomainAxis () {
         FID_CHART_MODE type            =   getChartMode ();
         FID_DATA_TYPE  dataType        =   getDataType ();

         NumberAxis domainAxis          =   null;
         switch(type){
                case FID:               domainAxis =  fidDomainAxis;
                                        break;
                  
                case SPECTRUM:          domainAxis =  spectrumDomainAxis;
                                        break;

                case OTHER  :           if (dataType == FID_DATA_TYPE.FID){
                                               domainAxis =  fidDomainAxis;
                                        }
                                        else {
                                                domainAxis =   spectrumDomainAxis;
                                        }
                                         break;

                default:                domainAxis =  spectrumDomainAxis;
                                        break;
         }
        
        return domainAxis;
    }
    public   NumberAxis         getRangeAxis () {
         FID_CHART_MODE mode        =  this.getChartMode ();

     
         NumberAxis rangeAxis          = null;
         switch(mode){
                case FID:               rangeAxis =  fidRangeAxis;
                                        break;
                  
                case SPECTRUM:          rangeAxis =  spectrumRangeAxis;
                                        break;

                default:                rangeAxis = otherRangeAxis;
                                        //rangeAxis.setAutoRange(true);
                                        Range range           =    getRangeForRangeAxis();
                                        rangeAxis.setRange( range);
                                        break;
         }
        
        return rangeAxis;
    }
    public  double              getRangeScaling () {
         FID_CHART_MODE type    = getChartMode ();
         double scale            =    1.0;
         switch(type){
                case FID:               scale   =  fidScaling;
                                        break;
                  
                case SPECTRUM:          scale =   spectrumScaling;
                                        break;

               default:                 
                                        break;
         }
        
        return scale;
    }
    
    public static double        getFidScaling () {
        return fidScaling;
    }
    public static double        getSpectrumScaling () {
        return spectrumScaling;
    }
    
    public  void                setRangeScaling ( double range_scaling ) {
       FID_CHART_MODE type    = getChartMode ();
       switch ( type) {
                  case FID:             fidScaling        =    range_scaling ; break;
                  case SPECTRUM:        spectrumScaling   =    range_scaling ; break;
                  default:              break;
      }
    }
    public void                 setThreshold ( double threshold ) {
        this.threshold = threshold;
    }
    public void                 setChartMode ( FID_CHART_MODE aChartType ) {
            
            chartType = aChartType;
            updatesDomainAndRangeAxis();
    }

    public void updatesDomainAndRangeAxis(){
            updateDomainAxis ();
            updateRangeAxis ();
   }

    public  void                updateDomainAxis () {
         NumberAxis domainAxis      =   getDomainAxis ();
         getChart().getXYPlot().setDomainAxis(domainAxis);
    }
    public  void                updateRangeAxis () {
         NumberAxis rangeAxis      =   getRangeAxis ();
         getChart().getXYPlot().setRangeAxis( rangeAxis);
         
         
    }

    public FID_DATA_TYPE         getDataType () {
        return dataType;
    }
    public void                  setDataType ( FID_DATA_TYPE dataType ) {
        this.dataType = dataType;
    }

    public boolean              isDrawMetabolites () {
        return drawMetabolites;
    }
    public void                 setDrawMetabolites ( boolean drawMetabolites ) {
        this.drawMetabolites = drawMetabolites;
    }

    public boolean              isDrawResonances () {
        return drawResonances;
    }
    public void                 setDrawResonances ( boolean drawResonances ) {
        this.drawResonances = drawResonances;
    }



}

