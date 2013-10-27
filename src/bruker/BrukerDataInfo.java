/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bruker;

import java.io.File;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.regex.Pattern;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class BrukerDataInfo implements BrukerConstants{
   private ByteOrder  byteOrder                 =   ByteOrder.BIG_ENDIAN;
   private DATA_FORMAT dataFormat               =   DATA_FORMAT.GO_16_BIT_SGN_INT;
   private ACQ_EXPERIMENT_MODE experimentMode   =   ACQ_EXPERIMENT_MODE.SingleExperiment;
   private ACQ_MODE acqMode                     =   ACQ_MODE.COMPLEX;
   
   private double [] fieldOfView                =    new double []{1.0, 1.0};
   private int    numberOfRepetions             =    1; // number of repetions of the experiment
   private int    scanTimeInMilliSeconds        =    1;
   private int    acquisitionShift              =    0;
   private int    digitalShift                  =    -1;
   private int    dspfvs                        =    -1; // firmware version
   private int    decim                         =    -1; // decimation
   private int    np                            =    0; // number of total points (real and imag)per trace in data
   private int    ns                            =    1; // number of traces (scane) per fid
   private double frequencyOffsetHz             =    0; // frequency offset in Hz
   private double sweepWidthHERTZ               =    1;
   private double sweepWidthPPM                 =   1;
   private double spectrometerFrequency         =    1;
   private boolean loaded                       =    false;

   public String   errorString                  =   null;

   public static File getParameterFileInDir(File dir){
        File acqn       =   new File (dir, bruker.BrukerConstants.ACQUS_FILE_NAME);
        return acqn;
     }

   public BrukerDataInfo(){};
   public  void readFilesInDir(File dir){
     File       paramFile      =     getParameterFileInDir(dir);
     boolean    paramRead      =     false;

     try{

        if ( paramFile.exists()== false){
             String  message   = String.format(
                     "Parameter file\n"+"%s\n"+
                      "doesn't exist. Exiting...",
                      paramFile.getPath());
                      DisplayText.popupDialog(message);
        }
       else{
            paramRead        =   readParameters(paramFile );
       }


     }
     catch(Exception e){
         e.printStackTrace();
     }
     finally{
         setLoaded(paramRead );
     }
   }


   public  boolean readParameters( File file){
      boolean success                   =   false;

       try{
           String content               =   IO.readFileToString( file);
           Scanner scanner              =   new Scanner(content);

          while(scanner.hasNextLine()){
              String line               =   scanner.nextLine();

              //Check number of dimensions
              if (line.startsWith("##$PARMODE")){
                int parmode =     getIntegerValue(line);
                if (parmode != 0){
                    this.errorString = "Only 1D can be parsed";
                     throw new IllegalArgumentException (errorString);
                }
              }
              // Get Data Type
              else if (line.startsWith("##$DTYPA")){
                int dtype =     getIntegerValue(line);
                
                if (dtype == 2){
                    dataFormat  =  DATA_FORMAT.GO_32_BIT_FLOAT;
                }
                else{
                     dataFormat  =  DATA_FORMAT.GO_32BIT_SGN_INT;
                }
                /*
                 * if (dtype!= 0 || dtype != 2){
                    this.errorString = "Only integer or float data can be parsed";
                    throw new IllegalArgumentException (errorString);
                }
                 */
              }
              
              //Get byte ordering
              else  if (line.startsWith("##$BYTORDA")){
                int abyteOrder =     getIntegerValue(line);
                if (abyteOrder == 1){
                    byteOrder  = ByteOrder.LITTLE_ENDIAN;
                }
                else{
                    byteOrder  = ByteOrder.BIG_ENDIAN;
                }
              }
              
               //Get points per trace
              else if (line.startsWith("##$TD=")){
                int anpoints =     getIntegerValue(line);
                    np        =   anpoints;
                
              }
              
              // Get Data Type
              //AQ_mod is acquisition mode (0=Real;1=Complex;2=Sequential;3=DQD)
              else if (line.startsWith("##$AQ_mod")){
                int aqmode =     getIntegerValue(line);
                if (aqmode == 1){
                   this.acqMode     =   ACQ_MODE.COMPLEX;
                }
                else if (aqmode == 3){
                    this.acqMode     =   ACQ_MODE.SEQUENTIAL;
                }
                else{
                    this.errorString = "Only cane read data with acquisition mode set to 1 or 3";
                    this.errorString = errorString + "\n";
                    this.errorString = errorString + "Acquisition mode in the parameter file is: " +aqmode;
                    
                }
              }
              
            //NS is number of scans per FID
              else if (line.startsWith("##$NS=")){
                int ans =     getIntegerValue(line);
                    ns        =   ans;
                
              }
              
              
              //O1 is offset frequency of carrier in Hz
              else if (line.startsWith("##$O1=")){
                double foh =     getDoubleValue(line);
                   frequencyOffsetHz      =  foh;
                
              }
              
              
              //sweep width in Hetz
              else if (line.startsWith("##$SW=")){
                double swh =     getDoubleValue(line);
                   sweepWidthPPM     =  swh;
                
              }
              
              
              //sweep width in ppm
              else if (line.startsWith("##$SW_h=")){
                double swh =     getDoubleValue(line);
                   sweepWidthHERTZ     =  swh;
                
              }
              
              //sweep width in Hetz
              else if (line.startsWith("##$SFO1=")){
                double swh =     getDoubleValue(line);
                   spectrometerFrequency     =  swh;
                
              }
              
              //decimation
              else if (line.startsWith("##$DECIM=")){
                int dcm =     getIntegerValue(line);
                   this.decim    =  dcm;
                
              }
                // software version
              else if(line.startsWith( "##$DSPFVS=" )){
                int sftver=     getIntegerValue(line);
                this.dspfvs   = sftver;
               }


             
              
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

   @Override
   public String toString(){
    StringBuilder sb = new StringBuilder();


    sb.append(BYTE_ORDER_KEY+ KEY_VALUE_SEPARATOR + getByteOrder());
    sb.append("\n");

    sb.append(GO_RAW_DATA_FORMAT_KEY+ KEY_VALUE_SEPARATOR + this.getDataFormat());
    sb.append("\n");


    return sb.toString();

   }
   
   public int getNumberOfTraces(){
    return this.getNs();
   }


   public static void main(String [] args){

    File file = new File("/Users/apple/BayesSys/greeneggy.yf1/3/acqp");
    file = new File("/Users/apple/BayesSys/Bruker/pulseprogram/acqp");

    BrukerDataInfoOLD di  =  new BrukerDataInfoOLD ();
    di.readAcnp(file);
    /*
       for (Integer i : di.acquisitionObjectOrder) {
            System.out.println(i);
       }
     * 
     */
      
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



    public boolean isLoaded () {
        return loaded;
    }
    public void setLoaded ( boolean loaded ) {
        this.loaded = loaded;
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

    public int getDecim() {
        return decim;
    }
    public void setDecim(int decim) {
        this.decim = decim;
    }


    public int  getNp() {
        return np;
    }
    public void setNp(int np) {
        this.np = np;
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


    public int getDigitalShift() {
        return digitalShift;
    }
    public void setDigitalShift(int digitalShift) {
        this.digitalShift = digitalShift;
    }
 
    public ACQ_MODE getAcqMode() {
        return acqMode;
    }

    public void setAcqMode(ACQ_MODE acqMode) {
        this.acqMode = acqMode;
    }

    public int getNs() {
        return ns;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

    public double getFrequensyOffsetHz() {
        return frequencyOffsetHz;
    }

    public void setFrequensyOffsetHz(double frequensyOffsetHz) {
        this.frequencyOffsetHz = frequensyOffsetHz;
    }

    public int getDspfvs() {
        return dspfvs;
    }

    public void setDspfvs(int dspfvs) {
        this.dspfvs = dspfvs;
    }

}

