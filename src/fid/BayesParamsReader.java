/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.Set;
import java.util.Hashtable;
import bayes.DirectoryManager;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
/**
 *
 * @author apple
 */
public class BayesParamsReader implements java.io.Serializable {

   
    public BayesParamsReader (File file){
        if (file.exists() == false) {
            loadedSuccessfully = false;
            return;
        }
        else {
            boolean  isRead = readFile(file);
            loadedSuccessfully = isRead;
        }
    }
    public BayesParamsReader (){
         loadedSuccessfully = false;
    }
    
    private boolean loadedSuccessfully  = false;
    
    // values
    private String fileName;
    private UNITS units;
    private boolean isShims = false;
    private boolean isNoise;
    private RESONANCE_MODEL defaultModel =  RESONANCE_MODEL.CORRELATED;
    private int firstFid;
    private int lastFid;
    private int noFids;
    private int totalPoints;
    private int noiseStart;
    private int modelFid;
    private int shimOrder = 1;
    private int maxCandidates;
    private int maxFreqs        = 10;
    private double defaultLb;
    private double centerPhase;
    private double timeDelay;
    private double [] shimDelta;
    private boolean firstPoint;
    private boolean realConstant;
    private boolean imaginaryConstant;
    private float  specFreq;
    private float userReference;
    
    private Hashtable <SHIMMING, Double> shimValues   =  new Hashtable <SHIMMING,  Double>();
    private ArrayList <Resonance>  resonances         =  new ArrayList <Resonance>(); 
   
    // keys
    public final static  String unitsKey            =   "Units";
    public final static  String isShimsKey          =   "Activate Shims";
    public final static  String isNoiseKey          =   "Noise";
    public final static  String defaultModelKey     =   "Default Model";
    public final static  String firstFidKey         =   "First Fid";
    public final static  String lastFidKey          =   "Last Fid";
    public final static  String noFidsKey           =   "No Fids";
    public final static  String totalPointsKey      =   "Total Points";
    public final static  String complexPointsKey    =   "Complex Points";
    public final static  String noiseStartKey       =   "Noise Start";
    public final static  String modelFidKey         =   "Model Fid";
    public final static  String shimOrderKey        =   "Shim Order";
    public final static  String maxCandidatesKey    =   "Max Candidates";
    public final static  String maxFreqsKey         =   "Max Freqs";
    public final static  String defaultLbKey        =   "Default Lb";
    public final static  String centerPhaseKey      =   "Center Phase";
    public final static  String timeDelayKey        =   "Time Delay";
    public final static  String shimDeltaKey        =   "Shim Delta";
    public final static  String samplingTimeKey     =   "Sampling Time";
    public final static  String firstPointKey       =   "First Point";
    public final static  String realConstantKey     =   "Real Constant";
    public final static  String imaginaryConstantKey=   "Imaginary Constant";
    public final static  String specFreqKey         =   "Spec Freq";
    public final static  String userReferenceKey    =   "User Reference"; 
    
   
    public  boolean readFile ( File file ) {
        FileInputStream fis     = null;
        StringReader str        = null;
        String curValue         = null;
        
        try {
            fis                 = new FileInputStream(file);
            int len             = fis.available();
            byte b[]            = new byte[len];
            fis.read(b);
            String content      = new String(b);
            str                 = new StringReader(content);            
            
            
            curValue            = readValue(str,unitsKey);
            units               = UNITS.getTypeByName(curValue);
            
            curValue            = readValue(str, isShimsKey);
            isShims             = (curValue.equalsIgnoreCase("Yes")) ? true : false;  ;
            
            curValue            = readValue(str, isNoiseKey);
            isNoise             = (curValue.equalsIgnoreCase("Yes")) ? true : false;  ;
            
            curValue            = readValue(str,defaultModelKey);
            defaultModel        = RESONANCE_MODEL.getResonanceModelFromShortNameWithParenthesis(curValue);
            
            curValue            = readValue(str,firstFidKey);
            firstFid            = Integer.parseInt(curValue);
            
            curValue            = readValue(str,lastFidKey);
            lastFid             = Integer.parseInt(curValue);
            
            curValue            = readValue(str,noFidsKey);
            noFids              = Integer.parseInt(curValue);
            
            curValue            = readValue(str,totalPointsKey);
            totalPoints         = Integer.parseInt(curValue);
            
            curValue            = readValue(str,noiseStartKey);
            noiseStart          = Integer.parseInt(curValue);
            
            curValue            = readValue(str,modelFidKey);
            modelFid            = Integer.parseInt(curValue);
            
            curValue            = readValue(str,shimOrderKey);
            shimOrder           = Integer.parseInt(curValue);
            
            curValue            = readValue(str,maxCandidatesKey);
            maxCandidates       = Integer.parseInt(curValue);
            
            curValue            = readValue(str,maxFreqsKey);
            setMaxFreqs(Integer.parseInt(curValue));
            
            curValue            = readValue(str,defaultLbKey);
            defaultLb           = Double.parseDouble(curValue);
            
            
            curValue            = readValue(str,specFreqKey);
            specFreq            = Float.parseFloat(curValue);
            
            curValue            = readValue(str, userReferenceKey);
            userReference       = Float.parseFloat(curValue);
           
            curValue            = readValue(str,centerPhaseKey);
            if (curValue != null){
                setCenterPhase(Double.parseDouble(curValue));
            }
            else {
                setCenterPhase(0.0);     
            }
            
            
            
            curValue            = readValue(str,timeDelayKey);
            if (curValue != null){
                setTimeDelay(Double.parseDouble(curValue));
            }
            else {
                 setTimeDelay(0.0);     
            }
             
            
            curValue            = readValue(str,shimDeltaKey);
            if (curValue != null){
                String [] strVal    =   curValue.split("\\s+");
                setShimDelta(new double[2]);
                getShimDelta()[0]        =   Double.parseDouble( strVal[0].trim());
                getShimDelta()[1]        =   Double.parseDouble( strVal[1].trim());
            }
            else {
                 setShimDelta(new double[]{Double.NaN, Double.NaN});   
            }
            
            
            firstPoint          = content.contains(firstPointKey);
            realConstant        = content.contains(realConstantKey);
            imaginaryConstant   = content.contains(imaginaryConstantKey);
         
            // reads shimming values to hashtable " shimValues"
            shimValues = readShimming(str, this.getShimOrder());
            
            // reads resonances to ArrayList "resonances"
            resonances = readResonances(str, this.getUnits(), this.getSpecFreq(), noFids);
            
            return true;
            
        } catch (IOException e) {            
            e.printStackTrace();
            return false;
        } finally {
            try {
                str.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null,
                                                                        ex);
            }
        }        
        
        
    } 
    
    public static FidModelNumbers readModelNumbers( File file ) {
        FileInputStream fis     = null;
        StringReader str        = null;
        String curValue         = null;
        int val; 
        FidModelNumbers numbers = new FidModelNumbers();
        
        if ( file.exists() == false) {return numbers;}
        
        try {
            fis                 = new FileInputStream(file);
            int len             = fis.available();
            byte b[]            = new byte[len];
            fis.read(b);
            String content      = new String(b);
            str                 = new StringReader(content);            
            
            
            curValue            = readValue(str,firstFidKey);
            val                 = Integer.parseInt(curValue);
            numbers.setFrom(val);
                    
            curValue            = readValue(str,lastFidKey);
            val                 = Integer.parseInt(curValue);
            numbers.setTo(val);
            
            curValue            = readValue(str,noFidsKey);
            val                 = Integer.parseInt(curValue);
            numbers.setBy(val);
          
            numbers.setLoaded(true);
            return numbers;
            
        } catch (IOException e) {            
            e.printStackTrace();
            return numbers;
        } finally {
            try {
                str.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null,
                                                                        ex);
            }
        }        
        
        
    } 
    public static String readValue (StringReader str, String key) {
        try {
            str.reset();
            String line             = null;
            LineNumberReader lnr    = new LineNumberReader(str);
            String pattern          = getPatternForKey(key);
            String value            = null;
            boolean matchFound      = false;

            while ( matchFound == false ) {
                line = lnr.readLine();
                if ( line == null ) {
                    break;
                }
                matchFound = Pattern.matches(pattern, line);
            }

            if ( matchFound ) {
              value = readValue(line);
            }

            return value;
        } catch (IOException ex) {
            Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
  }  
    public static String readIndexedValue(String values, int ind){
           String [] strVals         =  values.split("\\s+"); 
           String value             = strVals[ind];
           return value;
   }
    public static String readValue(String line){
            // get string right to the "=" sign
           String [] strVals         = line.split("=");
           String    value           = strVals[1].trim();
           return value;
   }
    public static String getPatternForKey(String key){
        String pattern = "\\s*" + key + ".*";
        return pattern;
    }
    
    public static String readFileName (StringReader str, String key) {
        try {
            str.reset();
            String line             = null;
            LineNumberReader lnr    = new LineNumberReader(str);
            String value            = null;
            String defValue         = "";

            line = lnr.readLine();
            if ( line == null ) { return defValue;}
            int start   =   line.indexOf('"');
            int end     =   line.indexOf(start + 1,'"');
            
            if (end < 0  && start < 0){
                return defValue;
            }
            
            value = line.substring(start, end);
            
            
                   

            return value;
        } catch (IOException ex) {
            Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
  }  
    
    public static Hashtable <SHIMMING, Double> readShimming  (StringReader  str, int shimOrder){
        try {
            str.reset();
     
       LineNumberReader  lnr            =   new LineNumberReader(str);
       Hashtable <SHIMMING,  Double> ht =   new  Hashtable <SHIMMING, Double>();
       String pattern                   =   getPatternForKey(shimDeltaKey);
       
       while ( true  ){   
               String line = lnr.readLine();
               // if reached end-of_file terminate loop
               if (line == null) { break; } 
               
               if( Pattern.matches( pattern, line)){
                    for (int i = 0; i < shimOrder ; i++) {
                      line = lnr.readLine(); 
                      String [] arr             = line.split("=");
                      String name               = arr[0].trim();
                      String value              = arr[1].trim();
                      SHIMMING shimming         = SHIMMING.getShimmingFromName(name);  
                      double val                = Double.valueOf(value);
                      ht.put(shimming, val);
                    }
               }
        }
            return  ht; 
      } catch (IOException ex) {
            Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null, ex);
            return  new  Hashtable <SHIMMING, Double>();
      }
   }
    
    
  
    public static ArrayList<Resonance> readResonances(StringReader  str, UNITS curUnits, 
                                                        float specFreq, int numberOfFids)  {
       ArrayList<Resonance> resList = new ArrayList<Resonance>();
        try {
           
            str.reset();
            LineNumberReader lnr = new LineNumberReader(str);
            String resHeaderPttrn = "\\s*!\\s*#\\s*Name\\s*Order.*";

            // This loop terminates when end of string is reached;
            while ( true ) {
                String line = lnr.readLine();

                // if reached end-of_file terminate loop
                if ( line == null ) { break; }
                   

                if ( Pattern.matches(resHeaderPttrn, line) ) {
                    line = lnr.readLine();
                    Resonance resonance = readResonance(line, curUnits, specFreq );
                    
                    // skip to line 
                    line = lnr.readLine(); // blank line
                    line = lnr.readLine(); // "!  Fid  Amplitude"
                    
                    for (int i = 0; i <numberOfFids; i++) {
                        line            = lnr.readLine();
                        String  subStr   = line.substring(48).trim();
                        String [] array  = subStr .split("\\s");

                        double val = 0;
                        if (array.length == 1){
                            val  = Double.valueOf(array [0]);

                        }
                        else if(array.length == 2){
                             double cos  = Double.valueOf(array [0]);
                             double sin  = Double.valueOf(array [1]);
                             val         = Math.hypot(cos, sin);
                        }

                        resonance.getAmplitudes().add(val);
                        
                    }
                    
                    resList.add(resonance);
                }
            }


            return resList;
        } catch (IOException ex) {
            Logger.getLogger(BayesParamsReader.class.getName()).log(Level.SEVERE, null, ex);
            return  new ArrayList<Resonance>();
        }
 } 
    public static Resonance readResonance(String line, UNITS curUnits, float specFreq){
      Resonance resonance   = new Resonance();
      UNITS resUnits        = resonance.getUnits(); // currently always returns HERTZ
      int numberLength      = BayesAnalyzeParamsWriter.NUMBER_FIELD_LENGTH;
      double val;
      
      try{
        String str;
        str                     = line.substring(7, 11); // includes parenthesis!!!
        RESONANCE_MODEL resModel = RESONANCE_MODEL.getResonanceModelFromShortNameWithParenthesis(str);
        resonance.setResonanceModel(resModel);
      
        str = line.substring(35, 40).trim(); 
            resonance.setFirstOrder (getSpinDegeneracyOrder(str, 1));
            resonance.setSecondOrder(getSpinDegeneracyOrder(str, 2));
     
        int startIndex  = 48;
        int endIndex    = startIndex + numberLength;
        val             = parseStringToDouble(line, startIndex, endIndex);
       
        
        float floatVal  = (float)val;
        val             = fid.Units.convertUnits( floatVal, specFreq, curUnits,resUnits);
        resonance.setFreqFinalVal(val);
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
        val        = parseStringToDouble(line, startIndex, endIndex);
        
        floatVal   = (float)val;
        val        = fid.Units.convertUnits( floatVal, specFreq, curUnits, UNITS.HERTZ);
        resonance.setFreqInitlVal(val);
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setRateFinalVal(parseStringToDouble(line, startIndex, endIndex));
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setRateInitlVal(parseStringToDouble(line, startIndex, endIndex));
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setJ1FinalVal(parseStringToDouble(line, startIndex, endIndex));
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setJ1InitlVal(parseStringToDouble(line, startIndex, endIndex));
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setJ2FinalVal(parseStringToDouble(line, startIndex, endIndex));
      
        startIndex = endIndex;
        endIndex   = startIndex + numberLength;
            resonance.setJ2InitlVal(parseStringToDouble(line, startIndex, endIndex));
     }

    catch(IllegalArgumentException exp){ 
        exp.printStackTrace();
        return null;
    }  
      return resonance;
      
 }   
   
    
    
    public static float []     readApmplitude (File modelFile, int peaknumber,  int numberofLineToRead ){

      float [] y = null; // data for ordinate 
      String line = "";
      int count   = 1;
      
      try {
          FileReader fr     =   new FileReader(modelFile); 
          BufferedReader in =   new BufferedReader(fr);		
          LineNumberReader lnr = new LineNumberReader(in) ;
          
          boolean matchFound = false;
          while (  matchFound == false  ){   
               line = lnr.readLine();
               if (line == null) break;
               
               if (Pattern.matches(".*?#.*?Name.*Order.*?", line)){
                  line = lnr.readLine();
                  if (Pattern.matches(".*?[CU]P.*?", line) && count == peaknumber){
                         matchFound = true;
                  } else if (Pattern.matches(".*?[CU]P.*?", line)){
                        count+=1;
                  }  else {
                       continue;
                  }
              }      
          }  
          
          
          if (matchFound ){
            lnr.readLine(); // blank line
            lnr.readLine(); // header  line
            y = new float [numberofLineToRead];
            
           if (line.contains("CP")){
                for (int i = 0; i < y.length; i++) {
                     line = lnr.readLine();
                     String str = getString(line, 54);
                     y[i] = Float.valueOf(str).floatValue();               
                }
           } else if (line.contains("UP")){
                  for (int i = 0; i < y.length; i++) {
                     line = lnr.readLine();
                     String cosStr = getString(line, 54);
                     String sinStr = getString(line, 74);
                     float cos     = Float.valueOf(cosStr).floatValue(); 
                     float sin     = Float.valueOf(sinStr).floatValue();
                     
                     y[i] = (float) Math.hypot (cos, sin);               
                }
           }
         }
          
        lnr.close();
        in.close();
        fr.close();
        
    } catch (NumberFormatException e){
            e.printStackTrace();
            return null;      
    } catch (FileNotFoundException e ){
             System.err.printf("File %s is not found.", modelFile.getName ());
             return null;
    } catch (IOException e)	{
             e.printStackTrace();
             return null;
    }      
    return y;
      
  } 
    private static String getString(String line, int position){
      int i1        = line.lastIndexOf ( " ", position);
      int i2        = (line.indexOf( " ", position) > 0)
                          ?line.indexOf( " ", position):line.length();
      if (i1 <0 || i2 < 0) return null;
      String result = line.substring (i1, i2);
      
      return result;
 } 
    
   
    public static int getSpinDegeneracyOrder(String txt, int order){
       // remove parenthesis     
       txt = txt.substring(1, txt.length()-1);
       String [] values = txt.split(",");
      int degen = Integer.valueOf(values[order -1]);
       return degen;
   }
    private static double parseStringToDouble(String line, int startIndex, int endIndex){
       try{
            String str   =  line.substring(startIndex ,endIndex ).trim();
            
            // if empty field
            if (str.length() == 0 ) {return 0.0;}
            return  Double.valueOf(str);
       }
       
       // if reached end-of-file 
       // don't change the return statement  in IndexOutOfBoundsException
       catch(IndexOutOfBoundsException exp){ return 0.0;}
      
   }
    
    
    @Override
    public   String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append(unitsKey          + " = "+ units.getName());
        sb.append("\n");
        
        sb.append(isShimsKey        + " = "+ isShims);
        sb.append("\n");
        
        sb.append(isNoiseKey        + " = "+ isNoise);
        sb.append("\n");
        
        sb.append(defaultModelKey   + " = "+ defaultModel.getShortNameWithParenthesis());
        sb.append("\n");
            
        sb.append(firstFidKey       + " = "+  firstFid);
        sb.append("\n");    
        
        sb.append(lastFidKey        + " = "+  lastFid);
        sb.append("\n");  
        
        sb.append(noFidsKey         + " = "+   noFids);
        sb.append("\n");  
            
        sb.append(totalPointsKey    + " = "+   totalPoints);
        sb.append("\n"); 
        
        sb.append(noiseStartKey     + " = "+   noiseStart);
        sb.append("\n");
        
        sb.append(modelFidKey       + " = " +    modelFid);
        sb.append("\n"); 
        
        
        sb.append(shimOrderKey      + " = " +    shimOrder);
        sb.append("\n"); 
        
        sb.append(maxCandidatesKey  + " = " +    maxCandidates);
        sb.append("\n"); 
        
        sb.append(defaultLbKey      + " = " +    defaultLb);
        sb.append("\n"); 
        
        sb.append(centerPhaseKey    + " = " +    getCenterPhase());
        sb.append("\n"); 
        
        sb.append(timeDelayKey      + " = " +    getTimeDelay());
        sb.append("\n"); 
        
        
        sb.append(shimDeltaKey      +  " = " +  getShimDelta() [0]+ " "+ getShimDelta()[1]);
        sb.append("\n"); 
        
        
        sb.append(firstPointKey +  " = " +  this.isFirstPoint());
        sb.append("\n"); 
        
        sb.append(realConstantKey +  " = " +  isRealConstant());
        sb.append("\n");
        
        
        sb.append(imaginaryConstantKey +  " = " +  isImaginaryConstant());
        sb.append("\n"); 
        
        sb.append(specFreqKey +  " = " +  this.getSpecFreq());
        sb.append("\n"); 
        
        sb.append(userReferenceKey +  " = " +  this.getUserReference());
        sb.append("\n");  
        
       Set <SHIMMING> keys = getShimValues ().keySet();
       for (SHIMMING shim : keys) {
              sb.append(shim.getName()  + " = " +getShimValues ().get(shim)    );
              sb.append("\n");
       }
       
       ArrayList <Resonance> res = this.getResonances();
        for (Resonance resonance : res) {
            sb.append(resonance.toString());
            sb.append("\n");
        }
       
        return sb.toString();
        
    }
    public static void main (String [] args){
        File f = new File("/Users/apple/Bayes/exp7/BayesOtherAnalysis/bayes.model.0001");
        BayesParamsReader br  = new BayesParamsReader(f);
        System.out.println(br);
    }
    
    
// <editor-fold defaultstate="collapsed" desc=" GETTERS AND SETTERS ">

   

    public String getFileName () {
        return fileName;
    }

    public void setFileName ( String fileName ) {
        this.fileName = fileName;
    }

    public UNITS getUnits () {
        return units;
    }

    public void setUnits ( UNITS units ) {
        this.units = units;
    }

    public boolean isNoise () {
        return isNoise;
    }

    public void setIsNoise ( boolean isNoise ) {
        this.isNoise = isNoise;
    }

    public RESONANCE_MODEL getDefaultModel () {
        return defaultModel;
    }

    public void setDefaultModel (RESONANCE_MODEL defaultModel ) {
        this.defaultModel = defaultModel;
    }

    public int getFirstFid () {
        return firstFid;
    }

    public void setFirstFid ( int firstFid ) {
        this.firstFid = firstFid;
    }

    public int getLastFid () {
        return lastFid;
    }

    public void setLastFid ( int lastFid ) {
        this.lastFid = lastFid;
    }

    public int getNoFids () {
        return noFids;
    }

    public void setNoFids ( int noFid ) {
        this.noFids = noFid;
    }

    public int getTotalPoints () {
        return totalPoints;
    }

    public void setTotalPoints ( int totalPoints ) {
        this.totalPoints = totalPoints;
    }

    public int getNoiseStart () {
        return noiseStart;
    }

    public void setNoiseStart ( int noiseStart ) {
        this.noiseStart = noiseStart;
    }

    public int getModelFid () {
        return modelFid;
    }

    public void setModelFid ( int modelFid ) {
        this.modelFid = modelFid;
    }

    public int getShimOrder () {
        return shimOrder;
    }

    public void setShimOrder ( int shimOrder ) {
        this.shimOrder = shimOrder;
    }

    public int getMaxCandidates () {
        return maxCandidates;
    }

    public void setMaxCandidates ( int maxCandidates ) {
        this.maxCandidates = maxCandidates;
    }
    
    public int getMaxFreqs () {
        return maxFreqs;
    }
    
    public double getDefaultLb () {
        return defaultLb;
    }

    public boolean isIsShims () {
        return isShims;
    }

    public void setIsShims ( boolean isShims ) {
        this.isShims = isShims;
    }

    public double getCenterPhase () {
        return centerPhase;
    }

    public void setCenterPhase ( double centerPhase ) {
        this.centerPhase = centerPhase;
    }

    public double getTimeDelay () {
        return timeDelay;
    }

    public void setTimeDelay ( double timeDelay ) {
        this.timeDelay = timeDelay;
    }
    
    
    public double[] getShimDelta () {
        return shimDelta;
    }

    public void setShimDelta ( double[] shimDelta ) {
        this.shimDelta = shimDelta;
    }


   public static File getParamsFile(int index) throws FileNotFoundException{
       File dir     = DirectoryManager.getASCIIDir();
       return getParamsFile(dir, index);
   }
   public static File getParamsFile(File aDir, int anIndex) throws FileNotFoundException{
       File dir     = aDir;
       String index     =  String.format(bayes.BayesManager.INDEX_FORMAT, anIndex);
       String fileName  = bayes.BayesManager.BAYES_PARAMS +"."+index;
       File file        = new File(dir,  fileName);
       
       if (file.exists()){return file;}
       else { throw new FileNotFoundException();}
   }

    public boolean isFirstPoint () {
        return firstPoint;
    }

    public void setFirstPoint ( boolean firstPoint ) {
        this.firstPoint = firstPoint;
    }

    public boolean isRealConstant () {
        return realConstant;
    }

    public void setRealConstant ( boolean realConstant ) {
        this.realConstant = realConstant;
    }

    public boolean isImaginaryConstant () {
        return imaginaryConstant;
    }

    public void setImaginaryConstant ( boolean imaginaryConstant ) {
        this.imaginaryConstant = imaginaryConstant;
    }

    public boolean isLoadedSuccessfully () {
        return loadedSuccessfully;
    }

    public Hashtable<SHIMMING, Double> getShimValues () {
        return shimValues;
    }

    public void setShimValues ( Hashtable<SHIMMING, Double> shimValues ) {
        this.shimValues = shimValues;
    }
    
    public ArrayList<Resonance> getResonances () {
        return resonances;
    }

    public float getSpecFreq () {
        return specFreq;
    }

    public void setSpecFreq ( float specFreq ) {
        this.specFreq = specFreq;
    }

    public float getUserReference () {
        return userReference;
    }

    public void setUserReference ( float userReference ) {
        this.userReference = userReference;
    }
    
    public int getNumberOfResonances(){
       return getResonances ().size();
    }

    public void setMaxFreqs(int maxFreqs) {
        this.maxFreqs = maxFreqs;
    }
// </editor-fold>
    
}
