/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;
import image.ImageDescriptor;
import java.util.regex.*;
import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import utilities.*;
/**
 *
 * @author apple
 */
public class BrukerDataInfoOLD implements BrukerConstants {
   private ByteOrder  byteOrder                 =   ByteOrder.BIG_ENDIAN;
   private DATA_FORMAT dataFormat               =   DATA_FORMAT.GO_16_BIT_SGN_INT;
   private ACQ_EXPERIMENT_MODE experimentMode   =   ACQ_EXPERIMENT_MODE.SingleExperiment;

 /********************** Parameter Description Starts ********************************

       The minimal list of ACQP parameters required by the reconstruction is given below.



ACQ_dim         This specifies the dimensionality of the experiment.

ACQ_size        This specifies the matrix size in each direction.
                Note that the acquisition matrix size in the read direction will count
                real and imaginary data points separately,
                so that an ACQ_size[0] value of 512 must be interpreted to mean
                256 complex data values per scan.

NI              This is the number of objects produced by a single repetition of an experiment.
                In a multi-slice multi-echo experiment this is typically the number of slices multiplied by the number of echoes.
                For example in a 2-slice 2-echo experiment, NI would be 4,
                in a single slice 4-echo experiment it would be 4 and
                in a 4-slice single echo experiment it would be 4.

NR              This is the number of repetitions of an experiment that were performed during a single acquisition.
                This is typically done to produce time sequences. For example,D-9-3
                Image Reconstruction a single slice 2D experiment might be repeated 4 times to produce 4 images.
                The 2- slice 2-echo experiment from the previous example (NI=4) might also be repeated 4 times
                to produce a total of 16 images.


BYTORDA         This specifies the byte ordering of the raw data.
                The native byte order- ing for the Indy is big-endian, for Intel i86-processors
                like used in Linux workstations it is little-endian.
                Both are correctly resolved by the reconstruction.


ACQ_word_size   This is the size (in bytes) of a single acquired data word.
                At the moment only 32-bit data words (4 bytes)
                are supported by the acquisition.

ACQ_scan_size   This specifies how many scans are acquired every time the digi- tizer is started.
                This enumeration parameter has the two valid value:
                One_scan and ACQ_phase_factor_scans.
  
              * For most normal imaging experiments, the digitizer is (re)started for every scan
              * and stopped at the end of every scan.
              * For some extremely fast imaging methods (EPI) this won’t work.
              * There the digitizer is started and will run continuously while multiple scans
              * are acquired. ACQ_scan_shift - This describes a ‘shift’
              * which was applied to each acquired scan - to discard the leading points
              * - before transferring the data to the host computer.
              * This shift has the goal of discarding the group delay points
              * at the start of the scan which are caused by the use of digital filters.
              * It is the responsibility of the acquisition to acquire additional data
              * points so that the expected amount of data can be sent to the host
              * for storage in the raw data file and reconstruction.
              * This is important for the reconstruction since it implies that
              * the additional processing required to compensate for the
              * group delay isn’t required.

ACQ_phase_factor This parameter is used together with NI to sort the scans in a
                multi-object acquisition. This parameter will give the number
                of consecutively acquired scans that belong to a single image.
                The simplest example is the standard multi-slice spin echo experiment
                in which a single scan is acquired from every slice before
                the phase encoding is changed. In this case ACQ_phase_factor
                will have a value of 1. It is also possible to acquire two phase
                encoding steps from one slice, then go to the next slice and
                acquire two phase encoding steps, etc. In this case ACQ_phase_factor
                will have a value of 2.

 ACQ_rare_factor This value is only required when a phase encoding mode of ‘Rare’
                has been selected in some direction. Note that only one ‘rare factor’
                is allowed for all processing directions.
                Note also that from the point of view of the reconstruction,
                the phase factor and the rare factor are logically independent parameters
                - although they will usually have the same value,
                the reconstruction does not require or enforce any such restriction.
  
 ACQ_phase_encoding_mode
                This specifies the phase ordering scheme used in Image Reconstruction
                acquiring the raw data. It is an array with one entry for each
                acquisition dimension. The options are
                Read, Linear, Centred, Rare and User_Defined_Encoding.

 ACQ_phase_enc_start
                This specifies the first phase encoding step acquired.
                It must be a value greater than or equal to -1
                (corresponding to maximum negative phase gradient)
                and less than 1 (corresponding to maximum positive phase gradient).

  ACQ_spatial_size_{1|2}
                This gives the length of the ACQ_spatial_phase_{1|2} array
                and is required when a phase encoding mode of User_Defined_Encoding
                is selected in data direction 1- the phase direction
                (respectively 2 - the slice direction).
                This value is then compared with the ACQ_size[1|2] value
                as a validation of the specified user phase encoding.
                Should any inconsistency exist, a Linear phase encoding will be assumed.
  ACQ_spatial_phase_{1|2}
                This gives the exact phase encoding order in the read (respectively slice)
                direction when User_Defined_Encoding is specified.
                This is an array of double values in the interval [-1,1] where -1
                corresponds to maximum nega- tive phase gradient strength
                and the interval between successive (linear) steps is given by 2/ACQ_size[1|2].

  ACQ_obj_order
                This specifies the order in which the various images were acquired
                within a single repetition of an experiment.
                In a typical 4-slice single-echo spin echo experiment,
                the slices are not sampled in the order 0, 1, 2, 3.
                Instead they are sampled in the order 0, 2, 1, 3.
                This is done to improve image quality but means that some
                additional work is required to sort the data during reconstruction.
                In a 4- slice 2-echo experiment the image ordering
                might be 0, 1, 4, 5, 2, 3, 6, 7.
                Images 0 and 1 refer to the first and second echo from the first slice,
                images 4 and 5 are the echoes from the third slice,
                images 2 and 3 are the echoes from the second slice, etc.

   *********************** Parameter Description Ends***********************/


   private int acquisitionDimension             =    1;
   private int [] acquisitionSizes              =    new int []{4};
   private int [] acquisitionObjectOrder        =    null;
   private double [] fieldOfView                =    new double []{1.0, 1.0};
   private int    phase_factor                  =    1;
   private int    rare_factor                   =    1;
   private int    numberOfObjects               =    1; // number of objects per experiment
   private int    numberOfRepetions             =    1; // number of repetions of the experiment
   private int    scanTimeInMilliSeconds        =    1;
   private int    acquisitionShift              =    0;
   private int    digitalShift                  =    -1;
   private int    decim                         =    -1; // decimation
   private int    dspfvs                        =    -1; // firmware version
   private int    np                            =    0; // number of total points (real and imag)per trace in data
   private double sweepWidthPPM                 =    1;
   private double sweepWidthHERTZ               =    1;
   private double spectrometerFrequency         =    1;
   private boolean loaded                       =    false;

   private int pointsInReadout                  =   0;
   private int numberOfPhaseencodes              =   0;
   private int numbersOfSlices                   =   0;
   private int numberOfElements                 =   0;

   public static File acqisitionFileForFidFile(File fidfile){
        File dir        =   fidfile.getParentFile();
        return  getAcqisitionFileInDir(dir);
     }
   public static File methodFileForFidFile(File fidfile){
        File dir                =   fidfile.getParentFile();
        return getMethodFileInDir (dir);
     }
   public static File getMethodFileInDir(File dir){
        File methodFile         =   new File (dir, METHOD_FILE_NAME);
        return methodFile;
    }
   public static File getAcqisitionFileInDir(File dir){
        File acqn       =   new File (dir, bruker.BrukerConstants.ACQN_FILE_NAME);
        return acqn;
     }

   public BrukerDataInfoOLD(){};
   public  void readFilesInDir(File dir){
     File       acnpFile      =     getAcqisitionFileInDir(dir);
     File       methodFile    =     getMethodFileInDir(dir);
     boolean    acpnread      =     false;

     try{

        if ( acnpFile.exists()== false){
             String  message   = String.format(
                     "Acqisition parameter file\n"+"%s\n"+
                      "doesn't exist. Exiting...",
                      acnpFile.getPath());
                      DisplayText.popupDialog(message);
        }
       else{
            acpnread        =   readAcnp(acnpFile );
       }


       if ( methodFile.exists()== true){
            readMethodFile( methodFile);
       }

       calculateDigitalShift();

     



     }
     catch(Exception e){
         e.printStackTrace();
     }
     finally{
         setLoaded(acpnread );
     }
   }

   public ImageDescriptor toImageDescriptor(){
         ImageDescriptor   id           =      new ImageDescriptor ();
         int row                        =      getPointsInReadout() ;
         int col                        =      getNumberOfPhaseEncodes();
         int slc                        =      getNumbersOfSlices();
         int elm                        =      getNumberOfElements();
         double scale1                  =      fieldOfView [0]/col;
         double scale2                  =      fieldOfView [1]/row;
         float scale3                   =      1;


         id.setNumberOfColumns  (col); //nFtPe -  first dimension in IFH
         id.setNumberOfRows     (row); //nFtRo -  second dimension in IFH
         id.setNumberOfElements (elm);
         id.setNumberOfSlices   (slc);
         id.setScaling1(scale1);
         id.setScaling2(scale2);
         id.setScaling3(scale3);



         return id;

    }

   public  void readMethodFile(File methodfile){
      try{

            String content               =   IO.readFileToString(  methodfile);
            Scanner scanner              =   new Scanner(content);

      while(scanner.hasNextLine()){
          String line       =   scanner.nextLine();
          if (line.startsWith("##$PVM_DigShift")){
                Integer val =     getIntegerValue(line);
                setDigitalShift((int) val);
          }


        }


      }
      catch( Exception e){
        e.printStackTrace();
      }
      finally{

      }
      


   }
   public  boolean readAcnp( File file){
      boolean success                   =   false;
      int xsize                         =   1;
      int ysize                         =   1;
      int zsize                         =   1;
      int index1                        =   1;
      int index2                        =   1;
      int zs                            =   1;

       try{
           String content               =   IO.readFileToString( file);
           Scanner scanner              =   new Scanner(content);

          while(scanner.hasNextLine()){
              String line               =   scanner.nextLine();


              // Ni (number of images)
              if (line.startsWith("##$NI=")){
                numberOfObjects  =     getIntegerValue(line);
              }
              // NR (number of repetitions)
              if (line.startsWith("##$NR=")){
                numberOfRepetions =     getIntegerValue(line);
              }
              // acquisition dimension
              else if(line.startsWith("##$ACQ_dim=")){
                acquisitionDimension  =     getIntegerValue(line);
              }

              // byte order
              else if(line.startsWith("##$BYTORDA=")){
                String val =     getStringValue(line);

                 if( val.equalsIgnoreCase("little")){
                    byteOrder  = ByteOrder.LITTLE_ENDIAN;
                 }
                 else{
                    byteOrder = ByteOrder.BIG_ENDIAN;
                 }

                }

              // data format
              else if(line.startsWith("##$GO_raw_data_format=")){
                    String val  =     getStringValue(line);
                    dataFormat  =  DATA_FORMAT.getTypeByName(val);

              }
              // phase factor
              else if(line.startsWith("##$ACQ_phase_factor=")){
                   phase_factor     =    getIntegerValue(line);;

              }
              // rare factor
              else if(line.startsWith("##$ACQ_rare_factor=")){
                   rare_factor     =    getIntegerValue(line);;

              }
              // rare factor
              //else if(line.startsWith("##$ACQ_phase_enc_start")){
             // }


              // field of view
              else if(line.startsWith("##$ACQ_fov=")){
                line                  =       scanner.nextLine();
                fieldOfView           =      readDoubleArray(line);
              }

           //  experiment mode
            else if(line.startsWith("##$ACQ_experiment_mode=")){
                String val              =     getStringValue(line);
                ACQ_EXPERIMENT_MODE mode;

                experimentMode =  ACQ_EXPERIMENT_MODE.getTypeByName(val);

                if (experimentMode != ACQ_EXPERIMENT_MODE.SingleExperiment){
                    DisplayText.popupMessage("The experiment is not acqired\n" +
                                            "in single mode. Exit...");

                    throw new IllegalArgumentException();

                }
            }


           // sweep width ppm
              else if(line.startsWith("##$SW=")){
                sweepWidthPPM =     getDoubleValue(line);
            }
            //sweep width hertz
              else if(line.startsWith("##$SW_h=")){
                sweepWidthHERTZ =     getDoubleValue(line);
            }
            //sweep width hertz
              else if(line.startsWith("##$SFO1=")){
                    spectrometerFrequency =     getDoubleValue(line);
            }
            // scan time
             else if(line.startsWith("##$ACQ_scan_time=")){
               scanTimeInMilliSeconds =     getIntegerValue(line);

            }
           // decimation
              else if(line.startsWith( "##$DECIM=")){
               decim =     getIntegerValue(line);
             }
              // decimation
              else if(line.startsWith( "##$DSPFVS=" )){
                dspfvs =     getIntegerValue(line);
               }

                // acquisition sizes
              else if(line.startsWith("##$ACQ_size=")){
                 index1             = line.indexOf("(");
        	 index2             = line.indexOf(")");
                 String val         =  line.substring(index1 + 1, index2 - 1).trim();
                 int dimensions     =  Integer.parseInt(val);
                 if ( dimensions !=  acquisitionDimension){ acquisitionDimension = dimensions;}
                 line                =    scanner.nextLine();
                 acquisitionSizes    =  readIntegerArray(line);

                 switch (acquisitionDimension)
		 {
		   case 1:
	    	   	break;
		   case 2:
      		   	xsize   = acquisitionSizes [0];
      		   	ysize   = acquisitionSizes [1];
      		   	break;
		   case 3:
      		   	xsize   = acquisitionSizes [0];
      		   	ysize   = acquisitionSizes [1];
      		   	zs      = acquisitionSizes [2];
      		   	break;
		   default:
	    	   	break;
      		 }
              }
              if (line.startsWith("##$ACQ_obj_order=")) {
              	 index1             = line.indexOf("(");
        	 index2             = line.indexOf(")");
                 String val         = line.substring(index1 + 1, index2 - 1).trim();
 	      	 int nobjects       = Integer.parseInt(val);
 	      	 if (nobjects > 1) {
                     acquisitionObjectOrder     =   new int [nobjects];
                     int currentSize            =   0;
                     while (currentSize < nobjects ){
                        line                    =   scanner.nextLine();
                        int [] lineArray        =   readIntegerArray(line);
                        int lasize              =    lineArray.length;
                        System.arraycopy(lineArray, 0 , acquisitionObjectOrder, currentSize, lasize);
                        currentSize             += lasize ;
                     }
                }
             }
           }



         if (getAcquisitionSizes() != null){
            np    = getNumberOfRealAndImagPointsPerTrace();
       }

           if (isImage()){
             pointsInReadout            = acquisitionSizes [0]/2;
             numberOfPhaseencodes        = acquisitionSizes [1];
             numberOfElements           =  numberOfRepetions;
             numbersOfSlices             =  numberOfObjects/numberOfRepetions;

           }


           success = true;
       }
       catch (Exception e){
        e.printStackTrace();
        success = false;
       }
       finally{

           return success;
       }
      
   }


   public static String  getStringValue(String line){
    int start                   = -1;
    start                       =   line.lastIndexOf(KEY_VALUE_SEPARATOR);
    if      (start < 0)           {return null;}

    String outStr               =   line.substring(start+1);
    return outStr.trim();

   }
   public static Integer getIntegerValue(String line){
    Integer out                 = null;
    String outStr               =   getStringValue(line);

    if (outStr  == null) {return null;}

    try{
        out                      =   Integer.valueOf(outStr );
    }
    catch(NumberFormatException nfe){
        nfe.printStackTrace();
    }

    return out;

   }
   public static Double  getDoubleValue(String line){
    Double out                  = null;
    String outStr               =   getStringValue(line);

    if (outStr  == null) {return null;}

    try{
        out                      =   Double.valueOf(outStr );
    }
    catch(NumberFormatException nfe){
        nfe.printStackTrace();
    }

    return out;

   }
   public static String  getStringValuesNoParenthesis(String line){
     String str =  getStringValue(line);
     if (str == null) {return null;}
     
     return  stripParenthesis(str);
     
   }
   public static String  stripParenthesis(String in){
    int start                   = 0;
    int end                     = in.length();
    if (in.startsWith("(")){start = 1;}
    if (in.endsWith  (")")){end   = end -1;}

    String outStr               =   in.substring(start, end);
    return outStr;

   }
   public static int []  readIntegerArray(String line){
     String [] strs         =   line.trim().split(REGEX);
     int    [] ints         =   new int [strs.length];

       for (int i = 0; i < strs .length; i++) {
           try{
               String str       =   strs [i];
               int val          =   Integer.parseInt(str);
               ints [i]         =   val;
            }
            catch(NumberFormatException nfe){
                nfe.printStackTrace();
                return null;
            }

       }

    
    return ints;

   }
   public static double  []  readDoubleArray(String line){
     String [] strs         =   line.trim().split(REGEX);
     double [] vals         =   new double  [strs.length];

       for (int i = 0; i < strs .length; i++) {
           try{
               String str       =   strs [i];
               double val          =   Double.parseDouble(str);
                vals  [i]         =   val;
            }
            catch(NumberFormatException nfe){
                nfe.printStackTrace();
                return null;
            }

       }


    return vals;

   }
   public static String  intArray2String(int [] vals){
        StringBuilder sb = new StringBuilder();
        if (vals == null) {return "null";}

        for (int i : vals) {
           sb.append(i);
           sb.append(" ");
       }

        return sb.toString();

   }
   public static String [] readValue (String content, String key){
      String [] str         =   null; // data for abscissa
      String line           =   "";
      Pattern p             =   Pattern.compile("\\s+");
      String regex          =   p.pattern();
      Scanner scanner       =   new Scanner(content);
      boolean matchFound    =   false;

      while(scanner.hasNextLine()== true && matchFound == false){
        line            =  scanner.nextLine();
        if(line == null) { break;}

        line                    =    line.trim();
        matchFound              =    line.startsWith(key+" ");
        //matchFound = Pattern.matches(key+" "+".*?", line);

        if (matchFound ){
               line             =   scanner.nextLine();
               str              =   line.split (regex);
        }
      }



      scanner.close();
      return str;
  }


   public boolean isImage(){
    if (this.getAcquisitionDimension() >1){
        return true;
    }
    else{
        return false;
    }
   }


   @Override
   public String toString(){
    StringBuilder sb = new StringBuilder();

    sb.append(ACQ_DIM_KEY+ KEY_VALUE_SEPARATOR + getAcquisitionDimension());
    sb.append("\n");

    sb.append(BYTE_ORDER_KEY+ KEY_VALUE_SEPARATOR + getByteOrder());
    sb.append("\n");

    sb.append(NI_KEY+ KEY_VALUE_SEPARATOR + getNumberOfObjects());
    sb.append("\n");

    sb.append(NR_KEY+ KEY_VALUE_SEPARATOR + getNumberOfRepetions());
    sb.append("\n");

    sb.append(GO_RAW_DATA_FORMAT_KEY+ KEY_VALUE_SEPARATOR + this.getDataFormat());
    sb.append("\n");

    sb.append(ACQ_SIZE_KEY+ KEY_VALUE_SEPARATOR + intArray2String(this.getAcquisitionSizes()));
    sb.append("\n");

    sb.append(ACQ_OBJ_ORDER+ KEY_VALUE_SEPARATOR + intArray2String(this.getAcquisitionObjectOrder()));
    sb.append("\n");

    sb.append(ACQ_EXPERIMENT_MODE_KEY+ KEY_VALUE_SEPARATOR + this.getExperimentMode());
    sb.append("\n");

    sb.append(SWEEP_WIDTH_PPM_KEY + KEY_VALUE_SEPARATOR + this.getSweepWidthPPM());
    sb.append("\n");

    sb.append(SWEEP_WIDTH_HERTZ_KEY + KEY_VALUE_SEPARATOR + this.getSweepWidthHERTZ());
    sb.append("\n");

    sb.append(SPECTROMETER_FREQ_KEY + KEY_VALUE_SEPARATOR + this.getSpectrometerFrequency ());
    sb.append("\n");

    sb.append(ACQ_SCANTIME_KEY + KEY_VALUE_SEPARATOR + this.getScanTimeInMilliSeconds());
    sb.append("\n");

    sb.append(ACQ_SCAN_SHIFT_KEY + KEY_VALUE_SEPARATOR + this.getAcquisitionShift());
    sb.append("\n");

    sb.append(PVM_DIGSHIFT_KEY + KEY_VALUE_SEPARATOR + this.getDigitalShift());
    sb.append("\n");

    sb.append(DECIM_KEY + KEY_VALUE_SEPARATOR + this.getDecim());
    sb.append("\n");

    sb.append(DSPFVS_KEY  + KEY_VALUE_SEPARATOR + this.getDspfvs());
    sb.append("\n");

    return sb.toString();

   }

   public void calculateDigitalShift(){
    if(getDigitalShift() >= 0){
        return ; // already asigned. Do nothing and return;
    }

    boolean supportedFirmaware  =   false;
    int     firmwareVersion     =   getDspfvs();
    int     decimation          =   getDecim();


    for (int curFirmwareVersion : FIRMWARE) {
           if(curFirmwareVersion == firmwareVersion){
                supportedFirmaware = true;
                break;
           }
    }
    if (supportedFirmaware == false) { return;} // no matching tables exist. Do nothing and exists


    Double ashift = FIRMWARELIST.get(firmwareVersion).get(decimation );
    if (ashift == null) { return ;}// unknown case

    setDigitalShift((int)Math.round(ashift));
   }


   public static void main(String [] args){

    File file = new File("/Users/apple/BayesSys/greeneggy.yf1/3/acqp");
    file = new File("/Users/apple/BayesSys/Bruker/pulseprogram/acqp");

    BrukerDataInfoOLD di  =  new BrukerDataInfoOLD ();
    di.readAcnp(file);
       for (Integer i : di.acquisitionObjectOrder) {
            System.out.println(i);
       }
      
}



   public int getNumberOfTraces(){
    return this.getNumberOfObjects();
   }
   public int getNumberOfBytesPerTrace(){
        int bytesPerPoint   =  getNumberOfBytesPerPoint();
        int bytesPerTrace   =   getNp()* bytesPerPoint;

       return bytesPerTrace;
   }
   public int getNumberOfBytesPerPoint(){
        int bytesPerPoint =  4;
        DATA_FORMAT df =  getDataFormat ();

       switch(df){

           case  GO_16_BIT_SGN_INT : bytesPerPoint = 2; break;
           case  GO_32BIT_SGN_INT  : bytesPerPoint = 4; break;
           case  GO_32_BIT_FLOAT   : bytesPerPoint = 4; break;

       }

       return bytesPerPoint;
   }
   protected int getNumberOfRealAndImagPointsPerTrace(){
       return getAcquisitionSizes()[0] ;
   }


   public ByteOrder getByteOrder () {
        return byteOrder;
    }
   public void setByteOrder (ByteOrder byteOrder ) {
        this.byteOrder = byteOrder;
    }

   public DATA_FORMAT getDataFormat () {
        return dataFormat;
    }
   public void setDataFormat ( DATA_FORMAT dataFormat ) {
        this.dataFormat = dataFormat;
    }

   public int getAcquisitionDimension () {
        return acquisitionDimension;
    }
   public void setAcquisitionDimension ( int acquisitionDimension ) {
        this.acquisitionDimension = acquisitionDimension;
    }

   public int[] getAcquisitionSizes () {
        return acquisitionSizes;
    }
   public void setAcquisitionSizes ( int[] acquisitionSizes ) {
        this.acquisitionSizes = acquisitionSizes;
    }

   public int getNumberOfObjects () {
        return numberOfObjects;
    }
   public void setNumberOfObjects ( int numberOfObjects ) {
        this.numberOfObjects = numberOfObjects;
    }

   public int[] getAcquisitionObjectOrder () {
        return acquisitionObjectOrder;
    }
   public void setAcquisitionObjectOrder ( int[] acquisitionObjectOrder ) {
        this.acquisitionObjectOrder = acquisitionObjectOrder;
    }

    public boolean isLoaded () {
        return loaded;
    }
    public void setLoaded ( boolean loaded ) {
        this.loaded = loaded;
    }

    public ACQ_EXPERIMENT_MODE getExperimentMode () {
        return experimentMode;
    }
    public void setExperimentMode ( ACQ_EXPERIMENT_MODE experimentMode ) {
        this.experimentMode = experimentMode;
    }

    public double getSweepWidthPPM () {
        return sweepWidthPPM;
    }
    public void setSweepWidthPPM ( double sweepWidthPPM ) {
        this.sweepWidthPPM = sweepWidthPPM;
    }

    public double getSweepWidthHERTZ () {
        return sweepWidthHERTZ;
    }
    public void setSweepWidthHERTZ ( double sweepWidthHHZ ) {
        this.sweepWidthHERTZ = sweepWidthHHZ;
    }

    public int getNumberOfRepetions () {
        return numberOfRepetions;
    }
    public void setNumberOfRepetions ( int numberOfRepetions ) {
        this.numberOfRepetions = numberOfRepetions;
    }

    public int getScanTimeInMilliSeconds () {
        return scanTimeInMilliSeconds;
    }
    public void setScanTimeInMilliSeconds ( int scanTimeInMilliSeconds ) {
        this.scanTimeInMilliSeconds = scanTimeInMilliSeconds;
    }

    public double getSpectrometerFrequency () {
        return spectrometerFrequency;
    }
    public void setSpectrometerFrequency ( double spectrometerFrequency ) {
        this.spectrometerFrequency = spectrometerFrequency;
    }

    public int getAcquisitionShift() {
        return acquisitionShift;
    }
    public void setAcquisitionShift(int acquisitionShift) {
        this.acquisitionShift = acquisitionShift;
    }

    public int getDigitalShift() {
        return digitalShift;
    }
    public void setDigitalShift(int digitalShift) {
        this.digitalShift = digitalShift;
    }

    public int getDecim() {
        return decim;
    }
    public void setDecim(int decim) {
        this.decim = decim;
    }

    public int  getDspfvs() {
        return dspfvs;
    }
    public void setDspfvs(int dspfvs) {
        this.dspfvs = dspfvs;
    }

    public int  getNp() {
        return np;
    }
    public void setNp(int np) {
        this.np = np;
    }

   

    public int getPhaseFactor() {
        return phase_factor;
    }
    public void setPhase_factor(int phase_factor) {
        this.phase_factor = phase_factor;
    }

    public int getRare_factor() {
        return rare_factor;
    }
    public void setRare_factor(int rare_factor) {
        this.rare_factor = rare_factor;
    }
   public int getPointsInReadout() {
        return pointsInReadout;
    }
   public int getNumberOfPhaseEncodes() {
        return numberOfPhaseencodes;
    }
   public int getNumbersOfSlices() {
        return numbersOfSlices;
    }
   public int getNumberOfElements() {
        return numberOfElements;
    }
}
