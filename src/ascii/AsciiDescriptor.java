/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import utilities.IO;
/**
 *
 * @author apple
 */
public class AsciiDescriptor {
    public enum SOURCE_TYPE {

            FILE                ("loaded from file"),
            PEAK_PICK           ("peak-pick"),
            EXTRACTED_PIXEL     ("extracted from image pixel(s)"),
            HISTOGRAM_PIXEL     ("histogram of image pixels"),
            MCMC_SAMPLES        ("mcmc samples"),
            MEAN_OVER_PIXELS    ("mean over ROI pixels"),
            UNKNOWN             ("unknown");

       private String info;
       SOURCE_TYPE(String anInfo){
            setInfo(anInfo);
       }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }


    final static int   PAD_LEN                          =    33;
    final static String PAD_CHAR                        =    " ";




    public static final String KEY_SOURCE_FILE          =   "source data file name";
    public static final String KEYDATE                  =   "date";
    public static final String KEYNumberOfColumns       =   "number of columns";
    public static final String KEYNumberOfRows          =   "number of rows";
    public static final String KEYExtraInfo             =   "info";
    public static final String KEYDataOrigin            =   "data source";


    protected int NumberOfColumns                       =    1;
    protected int NumberOfRows                          =    1;
    protected String sourceFileName                     =    null;
    protected String DATE                               =    getCurrentDate();


    protected String pixelInfo                          =   null;
    protected String dataSource                         =   SOURCE_TYPE.FILE.getInfo();
    private   String fullIunfo                           =   "";




    public String getSourceShortName(){
        String shortname    = this.getSourceFileName();
        File file           = new File (shortname);

        try {
                String str =  file.getName();
                shortname  = str;
        }
        catch (Exception exp){}


        return shortname ;
    }

    public static String  getCurrentDate(){
        String DATE_FORMAT      =   "dd-MMM-yyyy HH:mm:ss";
        Calendar cal            = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf    =  new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(cal.getTime());
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

        // line
        sb.append (IO.pad( KEYDataOrigin  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  getDataSource ());
        sb.append(EOL);

        // line 
        sb.append (IO.pad( KEY_SOURCE_FILE , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.  getSourceFileName ());
        sb.append(EOL);

        // line 
        sb.append (IO.pad( KEYDATE  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDATE());
        sb.append(EOL);

        // line 
        sb.append (IO.pad(  KEYNumberOfColumns  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getNumberOfColumns());
        sb.append(EOL);

        // line 
        sb.append (IO.pad(  KEYNumberOfRows  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+  this.getNumberOfRows());
        sb.append(EOL);

        // line
        String pixInfo = this.getExtralnfo();
        if(pixInfo != null){
            sb.append (IO.pad( KEYExtraInfo  , PAD_LEN, PAD_CHAR));
            sb.append (sp);
            sb.append(" "+  pixInfo);
            sb.append(EOL);
        }


      

      return sb.toString();
    }


    public int      getNumberOfRows () {
        return NumberOfRows;
    }
    public void     setNumberOfRows ( int NumberOfRows ) {
        this.NumberOfRows = NumberOfRows;
    }

    public int      getNumberOfColumns () {
        return NumberOfColumns;
    }
    public void     setNumberOfColumns ( int NumberOfColumns ) {
        this.NumberOfColumns = NumberOfColumns;
    }


    public String   getSourceFileName () {
        return sourceFileName;
    }
    public void     setSourceFileName ( String sourceFileName ) {
        this.sourceFileName = sourceFileName;
    }

    public String   getDATE () {
        return DATE;
    }
    public void     setDATE ( String DATE ) {
        this.DATE = DATE;
    }

 
    public String   getExtralnfo () {
        return pixelInfo;
    }
    public void     setExtraInfo ( String extra ) {
        this.pixelInfo = extra;
    }



    public String   getDataSource () {
        return dataSource;
    }
    public void     setDataSource ( String dataSource ) {
        this.dataSource = dataSource;
    }

    public String   getFullIunfo() {
        return fullIunfo;
    }
    public void     setFullIunfo(String fullIunfo) {
        this.fullIunfo = fullIunfo;
    }

}
