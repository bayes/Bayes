/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import bayes.Enums.UNITS;
import java.io.File;
import org.jfree.chart.plot.XYPlot;
/**
 *
 * @author apple
 */
public interface FidViewable {
    public FidReader        getFidReader();
    public FidChartPanel    getChartPanel()  ;  
    public UNITS            getUnits()  ;
    public void             setUnits(UNITS  units)  ;
    public void             updatePlot();
    public boolean          isLoaded();
    public XYPlot           getXYPlot();
    public Procpar          getProcpar();
    public FidPlotData      getFidPlotData ()  ;
    public String           getDataInfo ();
    public void             unloadData ();
    public File             getDataDir();
    public File             getFidDescriptorFile();

}
