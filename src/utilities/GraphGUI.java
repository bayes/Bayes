/*
*  GraphGUI.java
*  
*
*  Created by apple on 5/16/07.
*  Copyright 2007 _KAREN  MARUTYAN_
* All rights reserved.
*/
package utilities;
import java.awt.*;
import java.awt.geom.*;


import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.*;

public class GraphGUI {
    
/******************************************************************
*  XYSeries - >Represents a sequence of zero or more data items in the form (x, y). 
*   By default, items in the series will be sorted into ascending order by x-value,
*   and duplicate x-values are permitted.  
 */ 
    

    public static XYSeries createXYSeries (double [] dataX,
                                           double [] dataY,
                                           String xySeriesName){
		if (dataX.length != dataY.length)
		{
		System.out.println("Exiting. In GraphGUI.createXYSeries () dataX.length != dataY.length");
		System.exit(1);
		}
		XYSeries series = new XYSeries	(xySeriesName);
		for (int i = 0; i < dataX.length;  i++ ){
			series.add(dataX[i], dataY[i]);
		}
     return series;
    }
/******************************************************************

*XYSeries - >Represents a sequence of zero or more data items in the form (x, y). 
*By default, items in the series will be sorted into ascending order by x-value,
* and duplicate x-values are permitted. 
*Both the sorting and duplicate defaults can be changed in the constructor. 
*Y-values can be null to represent missing values.

* XYDataset (org.jfree.data.xy, Interface XYDataset) - >
* An interface through which data in the form of (x, y) items can be accessed.
    
*org.jfree.data.xy Class XYSeriesCollection
*Represents a collection of XYSeries objects that can be used as a dataset.
    		
* XYSeriesCollection (org.jfree.data.xy.XYSeriesCollection, 
*Implemented Interfaces:Dataset, DomainInfo, EventListener, IntervalXYDataset)
* -> Represents a collection of XYSeries objects that can be used as a dataset.
*/

	public static XYDataset createXYDataset (   double [] dataX,
                                                    double [] dataY, 
                                                    String xySeriesName){

       XYSeries series = createXYSeries (dataX,dataY,xySeriesName);
	//XYSeriesCollection xyDataset = new XYSeriesCollection();
        //xyDataset.addSeries(series);
        XYDataset xyDataset = new XYSeriesCollection(series);
	return 	xyDataset;
	}
	
/******************************************************************
* "A chart class implemented using the Java 2D APIs.
* The current version supports bar charts, line charts, pie charts and xy plots (including time series data)".

*"JFreeChart coordinates several objects to achieve its aim of being able
* to draw a chart on a Java 2D graphics device: a list of 
* Title objects (which often includes the chart's legend),
* a Plot and a Dataset (the plot in turn manages a domain axis and a range axis).

*You should use a ChartPanel to display a chart in a GUI.

*The ChartFactory class contains static methods for creating 'ready-made' charts".
*/
	public static JFreeChart createChart(XYDataset xyDataset,
							 String title,
							 String xAxisLabel,
							 String yAxisLabel, 
                                                         String  chartType){	

	JFreeChart chart;	
        if(chartType.equals ("ScatterPlot")){
             chart = ChartFactory.createScatterPlot
                     (	title,	// Title
						xAxisLabel,             // X-Axis label
						yAxisLabel,				// Y-Axis label
						xyDataset,              // Dataset.
						PlotOrientation.VERTICAL,
						true,					// Show legend
						true,					// Show tooltips
						true					// Show urls
                     );
	} else {
                    
             chart = ChartFactory.createXYLineChart
                      (	title,	// Title
						xAxisLabel,             // X-Axis label
						yAxisLabel,				// Y-Axis label
						xyDataset,              // Dataset.
						PlotOrientation.VERTICAL,
						true,					// Show legend
						true,					// Show tooltips
						true					// Show urls
                     );
	
	}
    return chart;
  }				 

/******************************************************************/
/*****************************************************************
	/* ChartPanel (JPanel - > org.jfree.chart.ChartPanel).
	 - > A Swing GUI component for displaying a JFreeChart object.
		The panel registers with the chart to receive notification of
		changes to any component of the chart. 
		The chart is redrawn automatically whenever this notification is received.
	*/
	
	public static ChartPanel makeChartPanel(double [] dataX,
                                                double [] dataY, 
                                                String xySeriesName,
                                                String title,
                                                String xAxisLabel,
                                                String yAxisLabel,
                                                String  chartType)
	{
		JFreeChart chart = createChart	(createXYDataset (dataX, dataY, xySeriesName),
                                                title,
                                                xAxisLabel,
                                                yAxisLabel,
                                                chartType);
		ChartPanel chartPanel = new ChartPanel (chart);
		//chartPanel.setPrefferedSize(new Dimension(500, 300));
	
	return chartPanel;
	}
    
    
   /*overriding "makeChartPanel" method */
   public static ChartPanel makeChartPanel( XYDataset dataset, 
                                            String xySeriesName,
                                            String title,
                                            String xAxisLabel,
                                            String yAxisLabel,
                                            String  chartType)
	{
		JFreeChart chart = createChart (dataset,
                                                title,
                                                xAxisLabel,
                                                yAxisLabel,
                                                chartType);
		ChartPanel chartPanel = new ChartPanel (chart);
		//chartPanel.setPrefferedSize(new Dimension(500, 300));
	
	return chartPanel;
	}

/******************************************************************/
/******************************************************************/
	public static void setupChart(JFreeChart chart){
		// DIFERENT PLOT SETTINGS
		chart.addSubtitle(new TextTitle("Output of MCMC")); 
		/*TextTitle source = new TextTitle( 
		"Created by Karen Marutyan"); 
		source.setFont(new Font("SansSerif", Font.PLAIN, 12)); 
		source.setPosition(RectangleEdge.BOTTOM); 
		source.setHorizontalAlignment(HorizontalAlignment.CENTER); 
		chart.addSubtitle(source); 
		*/
		
		//set the backgound color
		chart.setBackgroundPaint(new Color(0xCC, 0xCC, 0xFF));
		//chart.setBackgroundPaint(Color.BLACK);
		
	
		/* Quote "XYPlot (org.jfree.chart.plot.XYPlot) -> A general class
			for plotting data in the form of (x, y) pairs.
			This plot can use data from any class that implements the XYDataset interface.
			XYPlot makes use of an XYItemRenderer to draw each point on the plot.
			By using different renderers, various chart types can be produced.

			The ChartFactory class contains static methods for creating pre-configured charts."
		*/
		//XYPlot plot is an instance variable
		XYPlot plot;
		plot = chart.getXYPlot(); 		
		plot.setBackgroundPaint(Color.white); 
		plot.setDomainCrosshairVisible(true);
		plot.setDomainCrosshairValue(50.0, true);
		plot.setRangeCrosshairVisible(true);
		ValueAxis x_axis = plot.getDomainAxis();
		ValueAxis y_axis = plot.getRangeAxis();	
		x_axis.setLabelFont(new Font("Helvetica", Font.PLAIN, 18));
		y_axis.setLabelFont(new Font("Helvetica", Font.PLAIN, 18));
		
		plot.setDomainCrosshairValue(1.0);
		plot.setRangeCrosshairValue(1.0);
		
		/* Quote "XYLineAndShapeRenderer (org.jfree.chart.renderer.xy.XYLineAndShapeRenderer)
			A renderer that can be used with the XYPlot class.
		*/
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(); 
		renderer.setSeriesShapesVisible(0,true); 
		renderer.setSeriesShapesFilled(0,false);
		renderer.setSeriesPaint(0, Color.black);
		renderer.setDrawOutlines(true);
		renderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
	
	}
}

