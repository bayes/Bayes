/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import bayes.Enums.*;
import utilities.IO;
/**
 *
 * @author apple
 */
public class FidDescriptor {
    final static int   PAD_LEN                          =    33;
    final static String PAD_CHAR                        =    " ";

    public  FidDescriptor(){}


    public static final String KEY_SOURCE_FILE          =   "Source data file";
    public static final String KEYDATE                  =   "Date";
    public static final String KEYDELAY                 =   "Delay(s)";
    public static final String KEYPHASE                 =   "Phase (radians)";
    public static final String KEYREFERENCEHERTZ        =   "Reference (hertz)";
    public static final String KEYLB                    =   "Fourier Weighting (lb)";
    public static final String KEYFN                    =   "Total number of data points (fn)";
    public static final String KEYUNITS                 =   "Units";
    private String sourceFileName                       =    null;
    private String DATE                                 =   getCurrentDate();
    private float phase                                 =   0;
    private float delay                                 =   0;
    private float referenceInHertz                      =   0;
    private float userLB                                =   1;
    private int   userFn                                =   1;
    private UNITS units                                 =   UNITS.HERTZ;

    public static String  getCurrentDate(){
        String DATE_FORMAT      =   "dd-MMM-yyyy HH:mm:ss";
        Calendar cal            = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf    =  new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(cal.getTime());
    }


      @Override
    public  String   toString(){
        StringBuilder sb   =  new StringBuilder();
        String  EOL                =   "\n";
        String  sp                 =   ":=";


        // line

        sb.append (IO.pad( KEY_SOURCE_FILE  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getSourceFileName());
        sb.append(EOL);

        // line
        sb.append (IO.pad( KEYDATE  , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDATE());
        sb.append(EOL);


        // line
        sb.append (IO.pad( KEYDELAY   , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getDelay());
        sb.append(EOL);


        // line
        sb.append (IO.pad( KEYPHASE   , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getPhase());
        sb.append(EOL);

        // line
        sb.append (IO.pad( KEYREFERENCEHERTZ    , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getReferenceInHertz());
        sb.append(EOL);



        // line
        sb.append (IO.pad( KEYLB     , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getUserLB());
        sb.append(EOL);



        // line
        sb.append (IO.pad( KEYFN     , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getUserFn());
        sb.append(EOL);



        // line
        sb.append (IO.pad( KEYUNITS     , PAD_LEN, PAD_CHAR));
        sb.append (sp);
        sb.append(" "+ this.getUnits());
        sb.append(EOL);

      return sb.toString();
    }



    public String getSourceFileName () {
        return sourceFileName;
    }
    public String getDATE () {
        return DATE;
    }
    public float  getPhase () {
        return phase;
    }
    public float  getDelay () {
        return delay;
    }
    public float  getReferenceInHertz () {
        return referenceInHertz;
    }
    public float  getUserLB () {
        return userLB;
    }
    public int    getUserFn () {
        return userFn;
    }
    public UNITS  getUnits () {
        return units;
    }

    public void setSourceFileName ( String sourceFileName ) {
        this.sourceFileName = sourceFileName;
    }
    public void setDATE ( String DATE ) {
        this.DATE = DATE;
    }
    public void setPhase ( float phase ) {
        this.phase = phase;
    }
    public void setDelay ( float delay ) {
        this.delay = delay;
    }
    public void setReferenceInHertz ( float referenceInHertz ) {
        this.referenceInHertz = referenceInHertz;
    }
    public void setUserLB ( float userLB ) {
        this.userLB = userLB;
    }
    public void setUserFn ( int userFn ) {
        this.userFn = userFn;
    }
    public void setUnits ( UNITS units ) {
        this.units = units;
    }

}
