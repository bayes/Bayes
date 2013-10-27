/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import static bayes.Enums.*;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;
import org.jfree.chart.renderer.xy.*;

import java.util.*;
import java.awt.Color;
import java.awt.BasicStroke;
import interfacebeans.JAllPriors;
import org.jfree.data.xy.XYSeries;
/**
 *
 * @author apple
 */
public class FidPlotData   {
    static final long       serialVersionUID        =   7523471295622516147L;
    public static UNITS     LAST_SET_UNITS          =   null;
    private FID_PLOT_TYPE   plotType                =   FID_PLOT_TYPE.Trace;
    private FidChartPanel   chartPanel              =   FidChartPanel. getInstance();
    private FidReader       fidReader               =   new FidReader();// object to read  and processfid files
    XYSeriesCollection      xyData                  =   getChartPanel().getDataset() ;
    Color                   freqSeriesColor         =   Color.YELLOW ;
    private int     trace                           =   0;

    public  FidPlotData(){ }
   


    public String getDataAsString(){
        StringBuilder sb   = new StringBuilder();
        String format               =   "%+04.8E";
        String pad                  =   "\t";
        String eol                  =   System.getProperty("line.separator"  );

        try{
           XYSeriesCollection dataset  = xyData;
           if (dataset .getSeriesCount() > 0){
           int nseries              =   dataset.getSeriesCount() ;
           int nitems               =   dataset.getItemCount(0);

            for (int curItem = 0; curItem <  nitems ; curItem++) {
                double val          =   dataset.getXValue(0, curItem);
                String tmp          =   String.format(format,val );
                sb.append(tmp  );
                for (int curSeries = 0; curSeries < nseries; curSeries++) {
                    val             =   dataset.getYValue(curSeries, curItem);
                    tmp             =   String.format(format,val );
                    sb.append(pad );
                    sb.append(tmp );
                }
                sb.append(eol );
            }

        }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            return sb.toString();
        }

    }
 
    void updateSeries(){
       if (getFidReader().isFidReaderLoaded() == false) {return;}
       float []    x                    =   getXcoordinates();
       
       switch  (getPlotType()){   
            
                case Trace:     updateFidViewerSeries(xyData, x,getTrace());
                                break;
                                
                case Data:      updateModelData(xyData, x);
                                break;
                                
                case Model:     updateModelModel(xyData, x);
                                break;
		
                case Residual:  updateModelResidual(xyData, x);
                                break;
             
                case Horizontal : updateModelHorizonatl(xyData, x);
                                break;
                                
                case Vertical : updateModelVertical(xyData, x);
                                break;
                                
                case Stacked :  updateModelStacked(xyData, x);
                                break;
                                
                case Overlay :  updateModelOverlay(xyData, x);
                                break;
	
          }
   }
    void updateAxis () {
        FID_DATA_TYPE type      =   getDataType();
        NumberAxis x_axis       =   getDomainAxis();
        NumberAxis y_axis       =   getRangeAxis();
       
        switch ( type ) {
            case FID: break;
            default: 
                    // adjust for units conversions
                   UNITS currentUnits      =   fidReader.getUnits();

                    if (LAST_SET_UNITS == null){
                        LAST_SET_UNITS = currentUnits;
                    }
                    else if (currentUnits !=LAST_SET_UNITS ){
                            changeDomainAxisUnits(x_axis, LAST_SET_UNITS, currentUnits);
                    }

                    // set labels
                    y_axis.setLabel(getDataType().name());
                    if ( currentUnits == UNITS.PPM ) {
                        x_axis.setLabel("FREQUENCY (PPM)");
                    } else if ( currentUnits == UNITS.HERTZ ) {
                        x_axis.setLabel("FREQUENCY (Hertz)");
                    }
                    break;
        }
          
    }
   void changeDomainAxisUnits( UNITS oldUnits, UNITS newUnits){
        NumberAxis axis        =     getDomainAxis();
       changeDomainAxisUnits (axis , oldUnits, newUnits) ;
    }
   void changeDomainAxisUnits(NumberAxis axis, UNITS oldUnits, UNITS newUnits){
       LAST_SET_UNITS         =     newUnits;
       try{
               Procpar procpar        =     getFidReader().getProcpar();
               double low             =     axis.getRange().getLowerBound();
               double high            =     axis.getRange().getUpperBound();

               if (oldUnits == newUnits){return;}

               switch(newUnits){
                   case HERTZ : low     =  Units.ppm2hertz(procpar, low);
                                high    =  Units.ppm2hertz(procpar, high);
                                break;

                   case PPM   : low     =  Units.hertz2ppm(procpar, low);
                                high    =  Units.hertz2ppm(procpar, high);
                                break;

               }

               axis.setRange(low, high);

       }
       catch (Exception e) {
           LAST_SET_UNITS          = null;
           e.printStackTrace();


       }


    }
   void changeAxisWithReferenceFrequency(float shiftInRefFreqInHertz){
        UNITS units             =   fidReader.getUnits();
        NumberAxis axis         =   this.getDomainAxis();
        float refFreq           =   getProcpar().getSfrq();
        float shiftInRefFreq    =   shiftInRefFreqInHertz;
        double low              =   axis.getRange().getLowerBound();
        double high             =   axis.getRange().getUpperBound();

        if (units != UNITS.HERTZ){
              shiftInRefFreq = Units.convertUnits((float) shiftInRefFreqInHertz, refFreq, UNITS.HERTZ, units);

        }

       axis.setRange(low +shiftInRefFreq , high + shiftInRefFreq);

    }


    void updatePlotSettings(){
       
       switch  (getPlotType()){   
            
                case Trace:     getChartPanel().setIsZoomingEnabled (true);
                                break;
                                
                case Data:      getChartPanel().setIsZoomingEnabled (true);
                                break;
                                
                case Model:     getChartPanel().setIsZoomingEnabled (true);
                                break;
		
                case Residual:  getChartPanel().setIsZoomingEnabled (true);
                                break;
             
                case Horizontal : getChartPanel().setIsZoomingEnabled (true);
                                  break;
                                
                case Vertical : getChartPanel().setIsZoomingEnabled (false);
                                break;
                                
                case Stacked :  getChartPanel().setIsZoomingEnabled (false);
                                break;
                                
                case Overlay :  getChartPanel().setIsZoomingEnabled (false);
                                break;
	
          }
   }
    public float []          getXcoordinates(){
       switch (getDataType()){
           case FID : return getFidReader().getProcpar().getTime();
           default  : return getFrequency ();
       }
       
   }
    
    public void setColorForSeries(String seriesName){
        XYSeriesCollection allSeries    = this.getDataset();
        XYLineAndShapeRenderer renderer =  this.getRenderer();
        
        int i = 0;
        for ( XYSeries series : (List <XYSeries>) allSeries.getSeries()) {
             if (JAllPriors.isFrequencySeries(series))
             { 
                 renderer.setSeriesPaint( i, freqSeriesColor);
                 renderer.setSeriesStroke(i, new BasicStroke(1.0f));
                 
                  String curSeriesName = (String)series.getKey(); 
                    if(seriesName.equals(curSeriesName)){
                       renderer.setSeriesPaint(  i, Color.WHITE); 
                       renderer.setSeriesStroke(i, new BasicStroke(2.0f));
                    }
             }
                 
            
             i+=1;
        }
        
    }

    public void reset(){
        getFidReader().kill();
        setFidReader(new FidReader());
        getChartPanel().getDataset().removeAllSeries();

        setTrace(0);
        System.gc();
    }
    public void resetStatic(){
        LAST_SET_UNITS = null;

    }
    public void updateFidViewerSeries(XYSeriesCollection list,  float  [] x, int trace){
        list.removeAllSeries();
        float[] real_fid                =   getFidReader().getFidReal()[trace];
        float[] imag_fid                =   getFidReader().getFidImag()[trace];
        float[] real_Spectra            =   getFidReader().getRealSpectra()[trace];
        float[] imag_Spectra            =   getFidReader().getImagSpectra()[trace];
        float[] ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[trace];
        float[] powr_Spectra            =   getFidReader().getPowerSpectra()[trace];
        float   scale                   =   getRangeScaling(); 
        float   shift                   =   0;   
        
       switch ( getDataType()) {
           case FID          :  list.addSeries(createSeries(x, real_fid, "", scale, shift));
                                list.addSeries(createSeries(x, imag_fid, "", scale, shift));
                                break;
                                
            case SPECTRUM_REAL: 
                                float   yMax           =   getMax(real_Spectra);
                                
                                list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                               // list.addSeries(createLineSeries(x,threshold , "", scale, shift));
                               // list.addSeries(createLineSeries(x,-threshold,  "", scale, shift));
                               
                                JAllPriors jallPriors   =   JAllPriors.getInstance() ;
                                double max              =   0.3  *  yMax;
                                
                                ArrayList<XYSeries> freqSeries  = jallPriors.getFrequencySeries(max);
                                
                                int i = this.getDataset().getSeriesCount();
                                for ( XYSeries fSeries : freqSeries) {
                                       list.addSeries(fSeries); 
                                       XYLineAndShapeRenderer renderer =  this.getRenderer();
                                       int serIndex = i;
                                       renderer.setSeriesPaint(serIndex  , freqSeriesColor);
                                       i +=1;
                                 
                                }
                                break;
                                

            case SPECTRUM_IMAG: 
                                list.addSeries(createSeries(x, imag_Spectra, "", scale, shift));
                                break;
                                

            case SPECTRUM_COMPLEX:  
                                    list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    list.addSeries(createSeries(x, imag_Spectra, "", scale, shift));
                                    break;
           
            case SPECTRUM_AMPLITUDE:
                                     list.addSeries(createSeries(x, ampl_Spectra, "", scale, shift));
                                     break;

            case SPECTRUM_INTENSITY: 
                                     list.addSeries(createSeries(x, powr_Spectra, "", scale, shift));
                                     break;
        }
       
    
   }
    public void updateModelData (XYSeriesCollection list,  float  [] x ) {
        list.removeAllSeries();
        float[] real_fid                =   getFidReader().getFidReal()[0];
        float[] imag_fid                =   getFidReader().getFidImag()[0];
        float[] real_Spectra            =   getFidReader().getRealSpectra()[0];
        float[] imag_Spectra            =   getFidReader().getImagSpectra()[0];
        float[] ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[0];
        float[] powr_Spectra            =   getFidReader().getPowerSpectra()[0];
        float   scale                   =   getRangeScaling(); 
        float   shift                   =   0;   
        
        switch ( getDataType()) {
            
            case FID:               list.addSeries(createSeries(x,real_fid, "", scale, shift));
                                    break;
                
            case SPECTRUM_REAL:     list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    break;
                                    
            case SPECTRUM_IMAG:     list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_COMPLEX:  list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_AMPLITUDE: list.addSeries( createSeries(x,ampl_Spectra, "", scale, shift));
                                     break;

            case SPECTRUM_INTENSITY:list.addSeries (createSeries(x,powr_Spectra, "", scale, shift));
                                     break;
        }
    }
    public void updateModelModel( XYSeriesCollection list,  float  [] x ) {
        list.removeAllSeries();
        float[] real_fid                =   getFidReader().getFidReal()[1];
        float[] imag_fid                =   getFidReader().getFidImag()[1];
        float[] real_Spectra            =   getFidReader().getRealSpectra()[1];
        float[] imag_Spectra            =   getFidReader().getImagSpectra()[1];
        float[] ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[1];
        float[] powr_Spectra            =   getFidReader().getPowerSpectra()[1];
        float   scale                   =   this.getRangeScaling(); 
        float   shift                   =   0;   
        
        switch ( getDataType()) {
            
            case FID:               list.addSeries(createSeries(x,real_fid, "", scale, shift));
                                    break;
                
            case SPECTRUM_REAL:     list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    break;
                                    
            case SPECTRUM_IMAG:     list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_COMPLEX:  list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_AMPLITUDE: list.addSeries(createSeries(x,ampl_Spectra, "", scale, shift));
                                     break;

            case SPECTRUM_INTENSITY:list.addSeries(createSeries(x,powr_Spectra, "", scale, shift));
                                     break;
        }
        
    }
    public void updateModelResidual( XYSeriesCollection list, float  [] x ) {
        list.removeAllSeries();
        float[] real_fid                =   getFidReader().getFidReal()[2];
        float[] imag_fid                =   getFidReader().getFidImag()[2];
        float[] real_Spectra            =   getFidReader().getRealSpectra()[2];
        float[] imag_Spectra            =   getFidReader().getImagSpectra()[2];
        float[] ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[2];
        float[] powr_Spectra            =   getFidReader().getPowerSpectra()[2];
        float   scale                   =   getRangeScaling(); 
        float   shift                   =   0;   
        
        switch ( getDataType()) {
            
            case FID:               list.addSeries(createSeries(x,real_fid, "", scale, shift));
                                    break;
                
            case SPECTRUM_REAL:     list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    break;
                                    
            case SPECTRUM_IMAG:     list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_COMPLEX:  list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    break;

            case SPECTRUM_AMPLITUDE: list.addSeries(createSeries(x,ampl_Spectra, "", scale, shift));
                                     break;

            case SPECTRUM_INTENSITY:list.addSeries(createSeries(x,powr_Spectra, "", scale, shift));
                                     break;
        }
        
    }
    public void updateModelHorizonatl( XYSeriesCollection list,  float  [] x ) {
        list.removeAllSeries();
        float[] real_fid    ;
        float[] imag_fid    ;
        float[] real_Spectra ;
        float[] imag_Spectra;
        float[] ampl_Spectra;
        float[] powr_Spectra;
        float   scale                   =   getRangeScaling(); 
        float   shift                   =   0; 
        int     nTotal                  =   getFidReader().getNumberOfTotalTraces();  
        
        switch ( getDataType()) {
            
            case FID:               for (int i = 0; i < nTotal; i += 1) {
                                        real_fid                =   getFidReader().getFidReal()[i];
                                        list.addSeries(createSeries(x,real_fid, "", scale, shift));
                                    }
                                    break;
                
            case SPECTRUM_REAL:     for (int i = 0; i < nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                    }
                                    break;
                                    
            case SPECTRUM_IMAG:     for (int i = 0; i < nTotal; i += 1) {
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    }
                                    break;

            case SPECTRUM_COMPLEX:  for (int i = 0; i < nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        list.addSeries(createSeries(x,real_Spectra, "", scale, shift));
                                        list.addSeries(createSeries(x,imag_Spectra, "", scale, shift));
                                    }
                                    break;

            case SPECTRUM_AMPLITUDE: for (int i = 0; i < nTotal; i += 1) {
                                        ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[i];
                                        list.addSeries(createSeries(x, ampl_Spectra, "", scale, shift));
                                     }
                                     break;

            case SPECTRUM_INTENSITY: for (int i = 0; i < nTotal; i += 1) {
                                        powr_Spectra            =   getFidReader().getPowerSpectra()[i];
                                        list.addSeries(createSeries(x,powr_Spectra, "", scale, shift));
                                     }
                                     break;
        }
        
    }
    public void updateModelVertical(XYSeriesCollection list,  float  [] x  ) {
       list.removeAllSeries();
        float[] real_fid    ;
        float[] imag_fid    ;
        float[] real_Spectra ;
        float[] imag_Spectra;
        float[] ampl_Spectra;
        float[] powr_Spectra;
        float   scale                   =   1; 
        float   shift                   =   0; 
        int     nTotal                  =   getFidReader().getNumberOfTotalTraces();
        
        
        switch ( getDataType()) {
            
            case FID:              real_fid                 =   getFidReader().getFidReal()[0];
                                   shift                    =   getRange(real_fid);
                                   
                                   for (int i = 0; i < 3; i += 1) {
                                        real_fid                =   getFidReader().getFidReal()[i];
                                        list.addSeries(createSeries(x, real_fid, "", scale, shift*(i-1)));
                                   }
                                   
                                   break;
                                   
                
                                   
            case SPECTRUM_REAL:    real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i < 3; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,shift*(i-1)));
                                   }
                                   
                                   break;
               
                                   
            case SPECTRUM_IMAG:    imag_Spectra            =   getFidReader().getImagSpectra()[1];
                                   shift                   =   getRange(imag_Spectra);
                                   
                                   for (int i = 0; i < 3; i += 1) {
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*(i-1)));
                                   }
                                   
                                   break;
                                   
                                   

            case SPECTRUM_COMPLEX: real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i < nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,  shift*(i-1)));
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*(i-1)));
                                    }
                                    break;
                                    
                                    

            case SPECTRUM_AMPLITUDE:ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[0];
                                    shift                   =   getRange(ampl_Spectra);
                                   
                                    for (int i = 0; i < 3; i += 1) {
                                        ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[i];
                                        list.addSeries(createSeries(x,  ampl_Spectra, "", scale,  shift*(i-1)));
                                    }
                                   
                                   break;

            case SPECTRUM_INTENSITY: powr_Spectra            =   getFidReader().getPowerSpectra()[0];
                                    shift                   =   getRange(powr_Spectra);
                                   
                                    for (int i = 0; i < 3; i += 1) {
                                        powr_Spectra            =   getFidReader().getPowerSpectra()[i];
                                        list.addSeries(createSeries(x,  powr_Spectra, "", scale,  shift*(i-1)));
                                    }
                                   
                                   break;
        }
        
    }
    public void updateModelStacked(XYSeriesCollection list,  float  [] x  ) {
        list.removeAllSeries();
        float[] real_fid    ;
        float[] imag_fid    ;
        float[] real_Spectra ;
        float[] imag_Spectra;
        float[] ampl_Spectra;
        float[] powr_Spectra;
        float   scale                   =   1; 
        float   shift                   =   0; 
        int     nTotal                  =   getFidReader().getNumberOfTotalTraces();
        
        
        switch ( getDataType()) {
            
            case FID:              real_fid                 =   getFidReader().getFidReal()[0];
                                   shift                    =   getRange(real_fid);
                                   
                                   for (int i = 0; i < nTotal; i += 1) {
                                        real_fid                =   getFidReader().getFidReal()[i];
                                        list.addSeries(createSeries(x, real_fid, "", scale, shift*(i-1)));
                                   }
                                   
                                   break;
                                   
                
                                   
            case SPECTRUM_REAL:    real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i <  nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,shift*(i-1)));
                                   }
                                   
                                   break;
               
                                   
            case SPECTRUM_IMAG:    imag_Spectra            =   getFidReader().getImagSpectra()[0];
                                   shift                   =   getRange(imag_Spectra);
                                   
                                   for (int i = 0; i <  nTotal; i += 1) {
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*(i-1)));
                                   }
                                   
                                   break;
                                   
                                   

            case SPECTRUM_COMPLEX: real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i < nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        imag_Spectra            =   getFidReader().getRealSpectra()[i];
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,  shift*(i-1)));
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*(i-1)));
                                    }
                                    break;
                                    
                                    

            case SPECTRUM_AMPLITUDE:ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[0];
                                    shift                   =   getRange(ampl_Spectra);
                                   
                                    for (int i = 0; i <  nTotal; i += 1) {
                                        ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[i];
                                        list.addSeries(createSeries(x,  ampl_Spectra, "", scale,  shift*(i-1)));
                                    }
                                   
                                   break;

            case SPECTRUM_INTENSITY: powr_Spectra            =   getFidReader().getPowerSpectra()[0];
                                    shift                   =   getRange(powr_Spectra);
                                   
                                    for (int i = 0; i <  nTotal; i += 1) {
                                        powr_Spectra            =   getFidReader().getPowerSpectra()[i];
                                        list.addSeries(createSeries(x,  powr_Spectra, "", scale,  shift*(i-1)));
                                    }
                                   
                                   break;
        }
        
    }
    public void updateModelOverlay(XYSeriesCollection list,  float  [] x  ) {
        list.removeAllSeries();
        float[] real_fid    ;
        float[] imag_fid    ;
        float[] real_Spectra ;
        float[] imag_Spectra;
        float[] ampl_Spectra;
        float[] powr_Spectra;
        float   scale                   =   1; 
        float   shift                   =   0; 
        int     nTotal                  =   getFidReader().getNumberOfTotalTraces();
        int     m;
        
        
        switch ( getDataType()) {
            
            case FID:              real_fid                 =   getFidReader().getFidReal()[0];
                                   shift                    =   getRange(real_fid);
                                   
                                   for (int i = 0; i < nTotal; i += 1) {
                                        real_fid                =   getFidReader().getFidReal()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x, real_fid, "", scale, shift*m));
                                   }
                                   
                                   break;
                                   
                
                                   
            case SPECTRUM_REAL:    real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i <  nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,shift*(m)));
                                   }
                                   
                                   break;
               
                                   
            case SPECTRUM_IMAG:    imag_Spectra            =   getFidReader().getImagSpectra()[0];
                                   shift                   =   getRange(imag_Spectra);
                                   
                                   for (int i = 0; i <  nTotal; i += 1) {
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*m));
                                   }
                                   
                                   break;
                                   
                                   

            case SPECTRUM_COMPLEX: real_Spectra            =   getFidReader().getRealSpectra()[0];
                                   shift                   =   getRange(real_Spectra);
                                   
                                   for (int i = 0; i < nTotal; i += 1) {
                                        real_Spectra            =   getFidReader().getRealSpectra()[i];
                                        imag_Spectra            =   getFidReader().getImagSpectra()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x,  real_Spectra, "", scale,  shift* m));
                                        list.addSeries(createSeries(x,  imag_Spectra, "", scale, shift*m));
                                    }
                                    break;
                                    
                                    

            case SPECTRUM_AMPLITUDE:ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[0];
                                    shift                   =   getRange(ampl_Spectra);
                                   
                                    for (int i = 0; i <  nTotal; i += 1) {
                                        ampl_Spectra            =   getFidReader().getAmplitudeSpectra()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x,  ampl_Spectra, "", scale,  shift*m));
                                    }
                                   
                                   break;

            case SPECTRUM_INTENSITY: powr_Spectra            =   getFidReader().getPowerSpectra()[0];
                                    shift                   =   getRange(powr_Spectra);
                                   
                                    for (int i = 0; i <  nTotal; i += 1) {
                                        powr_Spectra            =   getFidReader().getPowerSpectra()[i];
                                        m                       =   traceNumberToShiftNumber(i);
                                        
                                        list.addSeries(createSeries(x,  powr_Spectra, "", scale,  shift*m));
                                    }
                                   
                                   break;
        }
        
    }




    private static int      traceNumberToShiftNumber(int i){
         int m;
         if         (i == 0) {   m   =   0;}
         else if    (i == 1) {   m   =   0;}
         else if    (i == 2) {   m   =   1;}
         else                {   m   =   2;}
         return m;                                   
    }
    public static float     getRange ( float[] data ) {
        float range = 0;
        for (float f : data) {
            if ( range < Math.abs(f) ) {
                range =  Math.abs(f);
            }
        }
        
        return range * 2;
    }
    public static float     getMax ( float[] data ) {
      float realMax                   =   data[0];
      
      for (int i = 0; i <  data.length; i++) {
            if (  data[i] > realMax ) {
                realMax = data[i];
            }
        }
      return realMax;
    }
    
    public static XYSeries  createSeries ( float[] x, float[] y, String name ) {
        return  createSeries(x, y,name,1, 0);
    }
    public static XYSeries  createSeries ( float[] x, float[] y, String name, float scale, float shift ) {
        XYSeries series                 =   new XYSeries(name);
       
        for (int i = 0; i < x.length; i += 1) {
            series.add(x[i], y[i]* scale+ shift, false);
        }
        return series;
    }
    public static XYSeries  createLineSeries ( float[] x, float lineY, String name, float scale, float shift ) {
        XYSeries series                 =   new XYSeries(name);
       
        for (int i = 0; i < x.length; i += 1) {
            series.add(x[i], lineY* scale+ shift, false);
        }
        return series;
    }
    

    
    public float[]          getFrequency () {
        Procpar procpar         =   getFidReader().getProcpar();
        float[] frequency       =   fidReader.getFrequencyInHertz ();
                

        // if current units are PPM  - convert to PPM
        if (fidReader.getUnits() == UNITS.PPM ) {
            for (int i = 0; i < frequency.length; i++) {
                float f      = frequency[i];
                frequency[i] = Units.hertz2ppm(procpar, f);
            }
        }
        return frequency;
    }
    public  float           getRangeScaling( ) {
        return (float)(getChartPanel().getRangeScaling());
    }
    public double           getNoiseThreshold (){
        double   threshold      =   getFidReader().getNoiseThreshHold()[getTrace()];
        return threshold;
  }
    public FidChartPanel    getChartPanel () {
        return chartPanel;
    }
    
    public XYSeriesCollection getDataset(){
        XYSeriesCollection series  =  (XYSeriesCollection )  getChartPanel ().getChart().getXYPlot().getDataset();
        return  series ;
    }
    public XYLineAndShapeRenderer getRenderer(){
           return (XYLineAndShapeRenderer)getChartPanel().getChart().
                   getXYPlot().getRenderer();
    }
    
    
  
    public FID_DATA_TYPE    getDataType () {
        return getChartPanel().getDataType();
    }
    public FID_PLOT_TYPE    getPlotType () {
        return plotType;
    }
  
    public float            getReferenceInCurrentUnits () {
        UNITS curUnits          =  fidReader.getUnits();
        float   ref             =  fidReader.getReferenceFreqInHertz();
        
        if (curUnits == UNITS.HERTZ) {
            return ref;
        }
        else if (curUnits == UNITS.PPM){
            ref = Units.hertz2ppm(getFidReader().getProcpar(), ref);
        }
        return ref;
    }
    public FidReader        getFidReader () {
        return fidReader;
    }
 
    public int              getTrace () {
        return trace;
    }
    public Procpar          getProcpar(){
        return getFidReader ().getProcpar();
    }

    public  NumberAxis      getRangeAxis () {
        return getChartPanel().getRangeAxis();
    }
    public  NumberAxis      getDomainAxis () {
        return this.getChartPanel().getDomainAxis();
    }
    

    public void setDataType ( FID_DATA_TYPE aDataType ) {
           getChartPanel().setDataType(aDataType);
           switch (aDataType) {
                case FID:
                                    getChartPanel().setChartMode(FID_CHART_MODE.FID);
                                    break;

                default:
                                    getChartPanel().setChartMode(FID_CHART_MODE.SPECTRUM);
                                    break;
             
            }
    }
    public void setPlotType ( FID_PLOT_TYPE aPlotType ) {
        plotType = aPlotType;
       
        switch (plotType) {
                case Stacked:
                                    getChartPanel().setChartMode(FID_CHART_MODE.OTHER);
                                    break;
                case Vertical:
                                    getChartPanel().setChartMode(FID_CHART_MODE.OTHER);
                                    break;                    
               
                case Overlay:
                                    getChartPanel().setChartMode(FID_CHART_MODE.OTHER);
                                    break;

                default:            FID_DATA_TYPE aDataType = this.getChartPanel().getDataType();
                                    this.setDataType(aDataType);
                                    break;
             
             
       }
    }

    public void setFidReader ( FidReader fidReader ) {
        this.fidReader = fidReader;
    }
    public void setTrace ( int trace ) {
        this.trace = trace;
    }
    public void setChartPanel ( FidChartPanel chartPanel ) {
        this.chartPanel = chartPanel;
    }
   
}
