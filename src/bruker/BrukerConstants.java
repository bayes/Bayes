/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

import java.util.*;

/**
 *
 * @author apple
 */
public interface BrukerConstants {


    public static final String REGEX                     =   java.util.regex.Pattern.compile("\\s+").pattern();

    public static enum DATA_FORMAT {
        GO_16_BIT_SGN_INT   ( 2, "Signed 16 Bit Integer"),
        GO_32BIT_SGN_INT    ( 4, "Signed 32 Bit Integer"),
        GO_32_BIT_FLOAT     ( 4, "Signed 32 Bit Float");

        private int numberOfBytes               =   2;
        private String description              =   "";
        DATA_FORMAT(int size, String name){
            numberOfBytes  = size;
            description    = name;
        }

         static public  DATA_FORMAT getTypeByName(String name)
            throws IllegalArgumentException
         {
            for ( DATA_FORMAT type: DATA_FORMAT.values()) {
                if ( name.equalsIgnoreCase(type.toString())){
                    return type;
                }
            }
            throw new IllegalArgumentException( name);
         }

        public int getNumberOfBytes() {
            return numberOfBytes;
        }

    };
    public static enum ACQ_EXPERIMENT_MODE {
        SingleExperiment,
        MultipleReceiverExperiment,
        MultipleExperimentOneReceiver;

         static public  ACQ_EXPERIMENT_MODE getTypeByName(String name)
            throws IllegalArgumentException
         {
            for ( ACQ_EXPERIMENT_MODE type: ACQ_EXPERIMENT_MODE.values()) {
                if ( name.equalsIgnoreCase(type.toString())){
                    return type;
                }
            }
            throw new IllegalArgumentException( name);
         }

    };
    
     public static enum ACQ_MODE {
        REAL   ( "Real"),
        COMPLEX   ( "Complex"),
        SEQUENTIAL   (  "Sequential"),
        DQD  (  "Dqd");

        private String description              =   "";
        ACQ_MODE( String name){
            description    = name;
        }



       

    };


    public final static String ACQN_FILE_NAME           =   "acqp";
    public final static String METHOD_FILE_NAME         =   "method";
    public final static String KEY                      =   "##";
    public final static String PARAM_KEY                =   KEY+"$";
    public final static String KEY_VALUE_SEPARATOR      =   "=";

    public final static String BYTE_ORDER_KEY           =   "BYTORDA";
    public final static String GO_RAW_DATA_FORMAT_KEY   =   "GO_raw_data_format";
    public final static String ACQ_DIM_KEY              =   "ACQ_dim";
    public final static String ACQ_SIZE_KEY             =   "ACQ_size";
    public final static String ACQ_OBJ_ORDER            =   "ACQ_obj_order";
    public final static String NI_KEY                   =   "##$NI";// number of objects per experiment
    public final static String NR_KEY                   =   "NR";// number of repetions of the experiment
    public final static String ACQ_EXPERIMENT_MODE_KEY  =   "ACQ_experiment_mode";
    public final static String SWEEP_WIDTH_PPM_KEY      =   "SW";
    public final static String SWEEP_WIDTH_HERTZ_KEY    =   "SW_h";
    public final static String SPECTROMETER_FREQ_KEY    =   "SFO1";// SFO1 = BF1 + O1
    public final static String ACQ_SCANTIME_KEY         =   "ACQ_scan_time";
    public final static String ACQ_SCAN_SHIFT_KEY       =   "ACQ_scan_shift";
    public final static String PVM_DIGSHIFT_KEY         =   "PVM_DigShift";
    public final static String DECIM_KEY                =   "DECIM";
    public final static String DSPFVS_KEY               =   "DSPFVS";

    public static final String RECO_FILENAME            = "reco";
    public static final String PROC2S_FILENAME          = "proc2s";
    public static final String D3PROC_FILENAME          = "d3proc";
    public static final String VISU_PARS_FILENAME       = "visu_pars";
    public final static String ACQUS_FILE_NAME          =  "acqus";


    // parsing proc2s file
     public static final String BYTORDP                = "##$BYTORDP=";


      //parsing d3proc file

      public static final String  DATTYPE               =   "##$DATTYPE= ";



    public final static int [] FIRMWARE                =    new int []{10, 11, 12} ;
    public final static Map<Integer,Double> DSPFVS10   =   new HashMap<Integer,Double>(){
        {
            this.put(   2, 44.7500);
            this.put(   3, 33.5000);
            this.put(   4, 66.6250);
            this.put(   6, 59.0833);
            this.put(   8, 68.5625);
            this.put(  12, 60.3750);
            this.put(  16, 69.5313);
            this.put(  24, 61.0208);
            this.put(  32, 70.0156);
            this.put(  48, 61.3438);
            this.put(  64, 70.2578);
            this.put(  96, 61.5052);
            this.put( 128, 70.3789);
            this.put( 192, 61.5859);
            this.put( 256, 70.4395);
            this.put( 384, 61.6263);
            this.put( 512, 70.4697);
            this.put( 768, 61.6465);
            this.put(1024, 70.4849);
            this.put(1536, 61.6566);
            this.put(2048, 70.4924);

        }
    };
    public final static Map<Integer,Double> DSPFVS11   =   new HashMap<Integer,Double>(){
        {
            this.put(   2, 46.0000);
            this.put(   3, 36.5000);
            this.put(   4, 48.0000);
            this.put(   6, 50.1667);
            this.put(   8, 53.2500);
            this.put(  12, 69.5000);
            this.put(  16, 72.2500);
            this.put(  24, 70.1667);
            this.put(  32, 72.7500);
            this.put(  48, 70.5000);
            this.put(  64, 73.0000);
            this.put(  96, 70.6667);
            this.put( 128, 72.5000);
            this.put( 192, 71.3333 );
            this.put( 256, 72.2500);
            this.put( 384, 71.6667);
            this.put( 512, 72.1250);
            this.put( 768, 71.8333);
            this.put(1024, 72.0625);
            this.put(1536, 71.9167);
            this.put(2048, 72.0313);

        }
    };
    public final static Map<Integer,Double> DSPFVS12   =   new HashMap<Integer,Double>(){
        {
            this.put(   2, 46.311);
            this.put(   3, 36.530);
            this.put(   4, 47.870);
            this.put(   6, 50.229);
            this.put(   8, 53.289);
            this.put(  12, 69.551);
            this.put(  16, 71.600);
            this.put(  24, 70.184);
            this.put(  32, 72.138);
            this.put(  48, 70.528);
            this.put(  64, 72.348);
            this.put(  96, 70.700 );
            this.put( 128, 72.524 );



        }
    };
    public final static Map<Integer,  Map<Integer,Double>> FIRMWARELIST= new HashMap<Integer,  Map<Integer,Double>> (){
        {
            Integer             key;
             Map<Integer,Double> value;

            key                         = FIRMWARE [0];
            value                       =  DSPFVS10;
            this.put(key ,   value);

            key                         = FIRMWARE [1];
            value                       =  DSPFVS11;
            this.put(key ,   value);

            key                         = FIRMWARE [2];
            value                       =  DSPFVS12;
            this.put(key ,   value);
        }


    };
}
