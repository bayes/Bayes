/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author apple
 */




public class PlotInfo {

   

    

    
       public static enum RESULTS_PLOT_TYPE {
       // RESIDUAL            (   "Resid"  ),
       // RESIDUAL_ONLY       (   "ResidOnly"  ),
        //OUTLIER             (   "Outlier"  ),
        LINE                (   "Line"  ),
        SCATTER             (   "Scatter"  ),
        SCATTER_SMALL       (   "ScatterSmall"  ),
        SIMS                (   "Sims"  ),
        ERROR_BARS          (   "ErrorBars"  ),
        MCMC_SAMPLES        (   "MCMCSamples"  ),
        CONTOUR             (   "Contour"  ),
        BARCHART            (   "BarChart"  );


        private final String name;
        RESULTS_PLOT_TYPE (String aname) {this.name       = aname;}
        public String getName() {return name;}

        public static RESULTS_PLOT_TYPE  getTypeByName(String aName)
                throws IllegalArgumentException{
            for (RESULTS_PLOT_TYPE  pt : RESULTS_PLOT_TYPE .values()) {
                    if(aName.equalsIgnoreCase(pt.name)){return pt;}

            }
            throw new IllegalArgumentException();
        }

    }
       public static enum EXTRA_TYPE {
        NOT_USED            (  "Not Used"  ),
        MEAN                (  "Mean"      ),
        OUTLIER             (  ""          );


        private String name   =   null;
        EXTRA_TYPE (String aname) {this.name       = aname;}
        public String getName() {return name;}
        public boolean isOutlier(){
            return this.equals(OUTLIER);
        }

        
        public static  EXTRA_TYPE getTypeByName(String aName){
           EXTRA_TYPE  t        =       EXTRA_TYPE.OUTLIER;
           aName                =       aName.trim();
           
           if (aName.startsWith(EXTRA_TYPE.NOT_USED.getName())){
                t = EXTRA_TYPE.NOT_USED;
           }
           else if(aName.startsWith(EXTRA_TYPE. MEAN .getName())){
                t = EXTRA_TYPE. MEAN ;
           }

            return t;
        }

        

      

    }

    public static void          parseTypePlotInfo(PlotInfo plotInfo, String typeStr){

        /*
         * Input String should look like
         * Line 1 2 3 4 | Data, Model, Resid
         * or
         * Line 1 2 3 4
         *
         */
        Scanner scanner     =  new Scanner (typeStr);
        String type         =  scanner.next();


        plotInfo.getTraces().clear();
        while (scanner.hasNextInt()){
             int k               = scanner.nextInt();
             plotInfo.getTraces().add( k );
        }


        // look for and try to assign labels
        try{
        if (scanner.hasNext()){
            boolean prooceed  = scanner.next().equals("|");
            if (prooceed ){
                 String [] curlabels  =   scanner.nextLine().split(",");
                 for (String label : curlabels) {
                   plotInfo.getLablesForLinePlot().add(label .trim());
                }
            }
        }
       }catch (Exception e){e.printStackTrace();}




        // For backward compatibility make RESULTS_PLOT_TYPE  = LINE
        // by default
        RESULTS_PLOT_TYPE PLOT_TYPE  = RESULTS_PLOT_TYPE.LINE;
        try{ PLOT_TYPE = RESULTS_PLOT_TYPE.getTypeByName( type);}
        catch (Exception exp){}


        plotInfo.setPlotType (PLOT_TYPE);

        scanner.close();
  }
    public static void          parseExtraPlotInfo(PlotInfo plotInfo, String extrStr){


        EXTRA_TYPE et  =  EXTRA_TYPE.NOT_USED;
        et             =  EXTRA_TYPE.getTypeByName(extrStr);
        plotInfo.setExtra(et);
        plotInfo.setDataInfo(extrStr);

  }



    protected String info;
    protected String bin;
    protected String name;
    protected String title;
    protected String x_label;
    protected String y_label;
    private   String dataInfo;
    private   ArrayList <Integer> traces = new ArrayList <Integer>();
    private   RESULTS_PLOT_TYPE plotType;
    private   EXTRA_TYPE extra;
    private   StringBuilder plotInstuctions  = new  StringBuilder();
    protected boolean viewSampels       =   false;
    private List <String> labels        =   new ArrayList<String>();


    public final static int ABSCISS_COL        =   1;
    public final static int DATA_COL           =   2;
    public final static int MODEL_COL          =   3;
    public final static int RESIDUAL_COL       =   4;



    public static void main(String [] arg){
              String format               =   "%04.8E";
              String out        =   String.format(format, 0.0000565746);
              System.out.println(out);
    }
    
    public boolean isHistogram(){
        String str              = getBin ();
        int    val              = Integer.valueOf(str) ;
        boolean isHistogram     = (val > 0)? true : false;
        return isHistogram;
    }
    public boolean isResidualOnly(){
        List <Integer>  yy          =   getOrdinataColumnsForLinePlot ();
        if (yy.get(0) == RESIDUAL_COL){ return true;}
        else { return false; }
    }
    public boolean isResidual(){
        List <Integer>  yy =   getOrdinataColumnsForLinePlot ();
        if (    yy.size()== 3  &&
                yy.get(0) == 2 &&
                yy.get(1) == 3 &&
                yy.get(2) == 4){
            return true;
        }
        else { return false; }
    }
    public List <String>  getLablesForLinePlot () {
        return labels ;
    }


    public int getAbscissaForScatterPlot () {
        return this.getTraces().get(0);
    }
    public int getOrdinataForScatterPlot () {
        return this.getTraces().get(1);
    }
    public int getAbscissaColumnForLinePlot () {
        int i = this.getTraces().get(0);
        return i;
    }
    public List <Integer>  getOrdinataColumnsForLinePlot () {
        List <Integer> yy       = new ArrayList<Integer> ();
        List <Integer> tr       = this.getTraces();

        for (int i = 1; i < tr.size(); i++) {
             int y              =   tr.get(i);
             yy.add(y);

        }

        return yy;
    }




    @Override
    public String toString(){
        return info;
    }


    public String getY_label () {
        return y_label;
    }
    public void setY_label ( String y_label ) {
        this.y_label = y_label;
    }

    public String getX_label () {
        return x_label;
    }
    public void setX_label ( String x_label ) {
        this.x_label = x_label;
    }

    
    public String getTitle () {
        return title;
    }
    public void setTitle ( String title ) {
        this.title = title;
    }

    public String getName () {
        return name;
    }
    public void setName ( String name ) {
        this.name = name;
    }

    public String getBin () {
        return bin;
    }
    public void setBin ( String bin ) {
        this.bin = bin;
    }

    public String getInfo () {
        return info;
    }
    public void setInfo ( String info ) {
        this.info = info;
    }
    
    public ArrayList<Integer> getTraces () {
        return traces;
    }
    public void setTraces ( ArrayList<Integer> traces ) {
        this.traces = traces;
    }

    public RESULTS_PLOT_TYPE getPlotType () {
        return plotType;
    }
    public void setPlotType ( RESULTS_PLOT_TYPE plotType ) {
        this.plotType = plotType;
    }
    
    public boolean isViewSampels () {
        return viewSampels;
    }
    public void setViewSampels ( boolean viewSampels ) {
        this.viewSampels = viewSampels;
    }

    public EXTRA_TYPE getExtra() {
        return extra;
    }
    public void setExtra(EXTRA_TYPE extra) {
        this.extra = extra;
    }


    public String getDataInfo() {
        return dataInfo;
    }
    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
    }

    public StringBuilder getPlotInstuctions() {
        return plotInstuctions;
    }
    public void setPlotInstuctions(StringBuilder plotInstuctions) {
        this.plotInstuctions = plotInstuctions;
    }
}
