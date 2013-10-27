/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import utilities.IO;
/**
 *
 * @author apple
 */
public class ImageDescriptor {
    final static int   PAD_LEN                          =    33;
    final static String PAD_CHAR                        =    " ";
    
   
    public static final String INTERFILE                =   "INTERFILE";
    public static final String KEYVERSION_OF_KEYS       =   "version of keys";
    public static final String KEYCONVERSION_PROGRAM    =   "conversion program";
    public static final String KEYPROGRAM_VERSION       =   "program version";
    
    
    public static final String KEYNAME_OF_DATA_FILE     =   "name of data file";
    public static final String KEY_SOURCE_FILE          =   "source data file name";
    public static final String KEYPATIENT_ID            =   "patient ID ";
    public static final String KEYDATE                  =   "date";
    public static final String KEYNUMBER_FORMAT         =   "number format";
    public static final String KEYBYTES_PER_PIXEL       =   "number of bytes per pixel";
    public static final String KEYORIENTATION           =   "orientation";
    public static final String KEYENDIAN                =   "imagedata byte order";

    public static final String KEYNumberOfDimension     =   "number of dimensions";
    public static final String KEYNumberOfColumns       =   "matrix size [1]";
    public static final String KEYNumberOfRows          =   "matrix size [2]";
    public static final String KEYNumberOfSlices        =   "matrix size [3]";
    public static final String KEYNumberOfElements      =   "matrix size [4]";

    
    public static final String KEYSCALING_FACTOR1       =   "scaling factor (mm/pixel) [1]";
    public static final String KEYSCALING_FACTOR2       =   "scaling factor (mm/pixel) [2]";   
    public static final String KEYSCALING_FACTOR3       =   "scaling factor (mm/pixel) [3]"; 
    public static final String KEYSLICE_THICKNESS       =   "slice thickness (mm/pixel)";
    public static final String KEYBYTEORDER             =   "imagedata byte order";
    public static final String KEY_X_LABEL              =   "x label";
    public static final String KEY_Y_LABEL              =   "y label";


    public static final String BIGENDIAN                =   "bigendian";
    public static final String LITTLEENDIAN             =   "littleendian";
    private int NumberOfDimensions                    =    4;
    protected int NumberOfElements                      =    1;
    protected int NumberOfSlices                        =    1;
    protected int NumberOfColumns                       =    1;
    protected int NumberOfRows                          =    1;
    protected int bytesPerPoint                         =    4;
    protected int orientation                           =    2;
    
    protected String dataFileName                       =    null;
    protected String sourceFileName                     =    null;
    protected String progVersion                        =   "1.0";
 

    protected String dataFormat                         =   "float";
    protected String conversionProg                     =   "None";
    protected String patientID                          =   "N/A";

    protected String DATE                               =   getCurrentDate();
    
    protected double keyVersion                         =   3.3;
    
    protected double scaling1                           =   1.0;
    protected double scaling2                           =   1.0;
    protected double scaling3                           =   1.0;
    
 
    protected boolean loaded                            =    false;
    private boolean bigEndian                           =    true;
    private String xLabel                               =    null;
    private String yLabel                               =     null;


    public static float computeImageAspectRatio( ImageDescriptor id){
        double   aspectRatio      =       1;
        double  widthMMperPix    =       id.getScaling1();
        double  heightMMperPix   =       id.getScaling2();
        double  heightInPix      =       id.getNumberOfRows();
        double  widthtInPix      =       id.getNumberOfColumns();

        aspectRatio              = computeImageAspectRatio(
            widthMMperPix, heightMMperPix, widthtInPix,heightInPix );

        
        return  (float)aspectRatio;
   }
    public static double computeImageAspectRatio(
             double  widthMMperPix,
             double  heightMMperPix,
             double  widthtInPix,
             double  heightInPix
             ){
        double   aspectRatio     =       1;

        double widthInMM         =       widthtInPix * widthMMperPix;
        double heightInMM        =       heightInPix * heightMMperPix;
        aspectRatio              =       widthInMM/heightInMM;
        return  aspectRatio;
   }



    public static String  getCurrentDate(){
        String DATE_FORMAT      =   "dd-MMM-yyyy HH:mm:ss";
        Calendar cal            = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf    =  new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(cal.getTime());
    }
    public int getTotalNumberOfPoints(){
        return  getNumberOfRows ()*getNumberOfColumns ()* getNumberOfSlices () *getNumberOfElements ();
    }

    public double getWidthInCm(){
        double val = getNumberOfColumns () *getScaling1();
        val        =    val/10; // convert to cantimeters
        return val;
    }
    public double getHeightInCm(){
        double val =  getNumberOfRows()*getScaling2();
        val        =    val/10; // convert to cantimeters
        return val;
    }
    public double getSliceSizeInCm(){
        double val =    getNumberOfSlices() *getScaling3();
        val        =    val/10; // convert to cantimeters
        return val;
    }

    // convenience methodes fir MRI images
    public int getNumberOfPhaseEncodes(){
        return  getNumberOfColumns ();
    }
    public int getNumberOfReadouts(){
        return  getNumberOfRows ();
    }

    @Override
    public  String   toString(){
        StringBuilder sb   =  new StringBuilder();
        String  EOL                =   "\n";
        String  sp                 =   ":=";

        // line 1
        sb.append (IO.pad(INTERFILE, PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ "");
        sb.append(EOL);

        // line 2
        sb.append (IO.pad(  KEYVERSION_OF_KEYS, PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getKeyVersion());
        sb.append(EOL);

        // line 3
        sb.append (IO.pad( KEYCONVERSION_PROGRAM, PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getConversionProg());
        sb.append(EOL);

        // line 4
        sb.append (IO.pad( KEYPROGRAM_VERSION, PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getProgVersion());
        sb.append(EOL);


        // line 5
        sb.append (IO.pad( KEYNAME_OF_DATA_FILE , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDataFileName());
        sb.append(EOL);

        // line 6
        sb.append (IO.pad( KEY_SOURCE_FILE , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.  getSourceFileName ());
        sb.append(EOL);


        // line 7
        sb.append (IO.pad( KEYPATIENT_ID  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getPatientID());
        sb.append(EOL);


        // line 8
        sb.append (IO.pad( KEYDATE  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDATE());
        sb.append(EOL);

        // line 9
        sb.append (IO.pad( KEYNUMBER_FORMAT  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDataFormat());
        sb.append(EOL);


        // line 10
        sb.append (IO.pad( KEYBYTES_PER_PIXEL  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getBytesPerPoint());
        sb.append(EOL);


        // line 11
        sb.append (IO.pad( KEYORIENTATION  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getOrientation());
        sb.append(EOL);



        // line 12
        sb.append (IO.pad( KEYNumberOfDimension , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getNumberOfDimensions());
        sb.append(EOL);

        // line 13
        sb.append (IO.pad(  KEYNumberOfColumns  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getNumberOfColumns());
        sb.append(EOL);

        // line 14
        sb.append (IO.pad(  KEYNumberOfRows  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getNumberOfRows());
        sb.append(EOL);

        // line 15
        sb.append (IO.pad(  KEYNumberOfSlices   , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getNumberOfSlices());
        sb.append(EOL);


        // line 16
        sb.append (IO.pad( KEYNumberOfElements , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+this.getNumberOfElements());
        sb.append(EOL);


        // line 17
        sb.append (IO.pad(  KEYSCALING_FACTOR1   , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getScaling1());
        sb.append(EOL);


        // line 18
        sb.append (IO.pad( KEYSCALING_FACTOR2 , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getScaling2());
        sb.append(EOL);


        // line 19
        sb.append (IO.pad( KEYSCALING_FACTOR3 , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getScaling3());
        sb.append(EOL);

    


      return sb.toString();
    }


    public boolean compareDimensions(ImageDescriptor id){

        if (getNumberOfRows()       != id.getNumberOfRows())    { return false;}
        if (getNumberOfColumns ()   != id.getNumberOfColumns ()){ return false;}
        if (getNumberOfSlices ()    != id.getNumberOfSlices ()) { return false;}
        if (getNumberOfElements ()  != id.getNumberOfElements()){ return false;}
        return true;
    }

    
    public int getNumberOfRows () {
        return NumberOfRows;
    }
    public void setNumberOfRows ( int NumberOfRows ) {
        this.NumberOfRows = NumberOfRows;
    }

    public int getNumberOfColumns () {
        return NumberOfColumns;
    }
    public void setNumberOfColumns ( int NumberOfColumns ) {
        this.NumberOfColumns = NumberOfColumns;
    }

    public int getNumberOfSlices () {
        return NumberOfSlices;
    }
    public void setNumberOfSlices ( int NumberOfSlices ) {
        this.NumberOfSlices = NumberOfSlices;
    }

    public int getNumberOfElements () {
        return NumberOfElements;
    }
    public void setNumberOfElements ( int NumberOfElements ) {
        this.NumberOfElements = NumberOfElements;
    }
    
    public boolean isLoaded () {
        return loaded;
    }
    public void setLoaded ( boolean loaded ) {
        this.loaded = loaded;
    }
    
    public String getDataFileName () {
        return dataFileName;
    }
    public void setDataFileName ( String dataFileName ) {
        this.dataFileName = dataFileName;
    }
    
    
    public String getSourceFileName () {
        return sourceFileName;
    }
    public void setSourceFileName ( String sourceFileName ) {
        this.sourceFileName = sourceFileName;
    }
    
    public int getBytesPerPoint () {
        return bytesPerPoint;
    }
    public void setBytesPerPoint ( int bytesPerPoint ) {
        this.bytesPerPoint = bytesPerPoint;
    }

    public String getDataFormat () {
        return dataFormat;
    }
    public void setDataFormat ( String dataFormat ) {
        this.dataFormat = dataFormat;
    }

    public int getOrientation () {
        return orientation;
    }
    public void setOrientation ( int orientation ) {
        this.orientation = orientation;
    }

    public String getConversionProg () {
        return conversionProg;
    }
    public void setConversionProg ( String conversionProg ) {
        this.conversionProg = conversionProg;
    }

    public String getPatientID () {
        return patientID;
    }
    public void setPatientID ( String patientID ) {
        this.patientID = patientID;
    }

    public String getDATE () {
        return DATE;
    }
    public void setDATE ( String DATE ) {
        this.DATE = DATE;
    }

    public double getKeyVersion () {
        return keyVersion;
    }
    public void setKeyVersion ( double keyVerion ) {
        this.keyVersion = keyVerion;
    }

    public String getProgVersion () {
        return progVersion;
    }
    public void setProgVersion ( String progVersion ) {
        this.progVersion = progVersion;
    }

    public double getScaling1 () {
        return scaling1;
    }
    public void setScaling1 ( double scaling1 ) {
        this.scaling1 = scaling1;
    }

    public double getScaling2 () {
        return scaling2;
    }
    public void setScaling2 ( double scaling2 ) {
        this.scaling2 = scaling2;
    }

    public double getScaling3 () {
        return scaling3;
    }
    public void setScaling3 ( double scaling3 ) {
        this.scaling3 = scaling3;
    }

    public boolean isBigEndian () {
        return bigEndian;
    }
    public void setBigEndian ( boolean bigEndian ) {
        this.bigEndian = bigEndian;
    }

    public String getByteOrder() {
        String order    = BIGENDIAN;
        if (isBigEndian () == false ) { order = LITTLEENDIAN;}

        return order;
    }

    public String getxLabel() {
        return xLabel;
    }
    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }
    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public int getNumberOfDimensions() {
        return NumberOfDimensions;
    }
    public void setNumberOfDimensions(int NumberOfDimensions) {
        this.NumberOfDimensions = NumberOfDimensions;
    }




}
