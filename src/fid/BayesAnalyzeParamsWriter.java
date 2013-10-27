/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

import applications.bayesAnalyze.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;
import bayes.BayesManager;
import load.LoadAndViewData;
import bayes.DirectoryManager;
import utilities.IO;
import fid.FidViewer;
import fid.FidReader;
import fid.Procpar;
import fid.Resonance;
import fid. Units;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
public class BayesAnalyzeParamsWriter {
 private BayesAnalyzeParamsWriter() { }
 final static int    NUMBER_FIELD_LENGTH    =  16;
 final static String FILE_HEADER            = "! File:"    ;
 final static String MODEL_HEADER           = "!   #  Name";
 final static String RESONANCE_HEADER       = "!   #  Name                        Order          "
                                            + "Frequency       Init Value     Decay Rate      "
                                            + "Init Value        Primary J       Init Value    "
                                            + "Secondary J      Init Value"; 
 final static int   PAD_LEN                 =   -12;
 final static String PAD_CHAR               =    " ";

 
   
     public static boolean writeParamsFile(BayesAnalyze ba, File dir){
        FidViewer viewer            =   FidViewer.getInstance();
        BufferedWriter out          =   null;
        UNITS curUnits              =   viewer.getUnits();

        try{
            if (!dir.exists ()) { dir.mkdirs();}
       
            File file          =    BayesAnalyze.getParamslFile();
            FileWriter fr      =    new FileWriter(file.getAbsoluteFile ());
            out                =    new BufferedWriter(fr);
            
            
            // write first 12 lines
            StringBuilder header =  writeParamsFileHeader();
            out.write(header.toString());
           
           
            // line 13
            out.write("    Units          = "+ curUnits.getName().toUpperCase());
            out.write("\n");
            
            // line 14
            String str = (ba.isShimmingOn())? "YES" : "NO";
            out.write("    Activate Shims = "+ str);
            out.write("\n");
            
            // line 15  - default
            out.write("    Activate Delay = YES");
            out.write("\n");
            
            // line 16  - default
            out.write("    Data Type      = VNMR");
            out.write("\n");
            
            // line 17
            str = (ba.isNoise())? "YES" : "NO";
            out.write("    Noise          = "+ str);
            out.write("\n");
            
            // line 18 - default
            out.write("    Output         = BRIEF");
            out.write("\n");
            
            // line 19
            out.write("    Default Model  = "+ ba.getDefaultModel());
            out.write("\n");
            
            // line 20
            int val       = ba.getFirstFid();
            out.write("    First Fid      =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 21
            val        = ba.getLastFid();
            out.write("    Last Fid       =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 22
            val       = ba.getByFid();
            out.write("    No Fids        =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 23
            val       = ba.getSignal();
            out.write("    Total Points   =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 24
            val       = ba.getSignal()/2;
            out.write("    Complex Points =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");;
            
            // line 25
            val       = ba.getNoise();
            out.write("    Noise Start    =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 26
            val       =  ba.getTotalPoints();
            out.write("    Model Points   =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 27
            val       =  1;
            out.write("    Model Fid      =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 28
            val     = 0;
            out.write("    Prior Odds     =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 29
            float fval     = ba.getSamplingTime();
            out.write("    Sampling Time  =" + IO.padToChar(""+fval, PAD_LEN, PAD_CHAR,'.'));
            out.write("\n");
            
            // line 30
            fval     = viewer.getProcpar().getSpectroscoperRefFrequency();
            out.write("    Spec Freq      =" + IO.padToChar(""+fval, PAD_LEN, PAD_CHAR,'.'));
            out.write("\n");
            
            // line 31
            fval     = viewer.getFidPlotData().getReferenceInCurrentUnits();
            out.write("    User Reference =" + IO.padToChar(""+fval, PAD_LEN, PAD_CHAR,'.'));
            out.write("\n");
            
            // line 32
            fval     = viewer.getFidPlotData().getReferenceInCurrentUnits();
            out.write("    True Reference ="+IO.padToChar(""+fval, PAD_LEN, PAD_CHAR,'.'));
            out.write("\n");
            
            // line 33
            val       = ba.getTotalModel();
            out.write("    Total Models   =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 34
            val       = ba.getShimmingOrder();
            out.write("    Shim Order     =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 35
            val       = ba.getMaximumResonances();
            out.write("    Max Freqs      =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 36 - default
            val   = 1;
            out.write("    Max Candidates =" + IO.pad(""+val, PAD_LEN, PAD_CHAR));
            out.write("\n");
            
            // line 37
            fval    =   viewer.getFidReader().getUserLb();
            out.write("    Default Lb     ="+IO.padToChar(""+fval, PAD_LEN, PAD_CHAR,'.'));
            out.write("\n");
            
            // line 38
            out.write("\n");
            
            // line 39 to the end 
            
            out.write(writePhaseAndShimmingParameters(ba));
            out.write(writeModels(ba).toString());
            
            out.close();
            fr.close ();; 
         } catch (IOException ex) {
            ex.printStackTrace();
            return false;
            
        }
      return true;  
    }
     public static  StringBuilder writeParamsFileHeader(){

            StringBuilder sb   =    new StringBuilder();
            // Static.EOF = "\n"
            // line 1
            sb.append(writeFileName());
            sb.append(BayesManager.EOL);
            
            // line 2
            sb.append("! Bayesian Analysis Input Parameter File");
            sb.append(BayesManager.EOL);
             
            // line 3
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";
            java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat(DATE_FORMAT);
            sb.append("! Created " +  sdf.format(cal.getTime()) + "  by "
                + BayesManager.getUser());
            sb.append(BayesManager.EOL);
            
            
            // line 4
            sb.append(BayesManager.EOL);
            
            // line 5
            sb.append(" 31 Configuration Parameters");
            sb.append(BayesManager.EOL);
            
            // line 6
            sb.append(BayesManager.EOL);;
            
            // line 7
            sb.append("    File Version   =       3.000");
            sb.append(BayesManager.EOL);
            
            // line 8
            String fidDir   =  DirectoryManager.getFidDir().getName();
            String spr      =  BayesAnalyze.spr;
            
            String fidName = fidDir + spr + "fid";
           
            sb.append("    Fid File       = "+ fidName);
            sb.append(BayesManager.EOL);
            
            // line 9
            String procpar = fidDir + spr + "procpar";
            sb.append("    Procpar File   = "+ procpar);
            sb.append(BayesManager.EOL);
            
            // line 10
            sb.append("    Analysis Dir   = "+ DirectoryManager.getASCIIDir().getName());
            sb.append(BayesManager.EOL);
            
            // line 11
            sb.append("    Model Dir      = "+ BayesManager.MODEL_FID_DIR);
            sb.append(BayesManager.EOL);
            
             // line 12 - default
            sb.append("    Model Dir Org  = DATA");
            sb.append(BayesManager.EOL);
            
         
      return sb;  
    }
     public static  StringBuilder writeModelFileHeader(){

            StringBuilder sb   =    new StringBuilder();
            // Static.EOF = "\n"
            // line 1
            sb.append(writeFileName());
            sb.append(BayesManager.EOL);
            
            // line 2
             sb.append(BayesManager.EOL);
             
            
            // line 3
            sb.append(" 31 Configuration Parameters");
            sb.append(BayesManager.EOL);
            
            // line 4
             sb.append(BayesManager.EOL);
            
            // line 5
            sb.append("    File Version   =       3.000");
            sb.append(BayesManager.EOL);
            
            // line 6
            String fidDir   =  DirectoryManager.getFidDir().getName();
            String spr      =  BayesAnalyze.spr;
            
            String fidName = fidDir + spr + "fid";
           
            sb.append("    Fid File       = "+ fidName);
            sb.append(BayesManager.EOL);
            
            // line 7
            String procpar = fidDir + spr + "procpar";
            sb.append("    Procpar File   = "+ procpar);
            sb.append(BayesManager.EOL);
            
            // line 8
            sb.append("    Analysis Dir   = "+ DirectoryManager.getBayesAnalyzeDir().getName());
            sb.append(BayesManager.EOL);
            
            // line 9
            sb.append("    Model Dir      = "+ BayesManager.MODEL_FID_DIR);
            sb.append(BayesManager.EOL);
            
            // line 10 - default
            sb.append("    Model Dir Org  = DATA");
            sb.append(BayesManager.EOL);
      return sb;  
    }
     
     public static  StringBuilder writeModels(BayesAnalyze ba ){
         StringBuilder sb               =   new StringBuilder();
         FidViewer fv                   =   FidViewer.getInstance();
         Procpar procpar                =   fv.getProcpar();
         ArrayList<Resonance> resList   =   ba.getResonances();
         String f                       =   "%-15.12f"; 
         String padChar                 =   " ";
         UNITS curUnits                 =   fv.getUnits();   
         int    numFieldLength          =   NUMBER_FIELD_LENGTH ;
         
         int modelIndex = 0;
   
         for (CONSTANT_MODELS model : ba.getConstantModels().values()) {
            if (model.isSelected() == false) {continue;}
            String ind      = ""+ (modelIndex + 1);
            String str      = IO.pad(ind, -5, " ")+ "  " +model.getName();
            sb.append(MODEL_HEADER); 
            sb.append("\n");
            sb.append(str); 
            sb.append("\n");
            sb.append("\n"); 
            ++modelIndex;
         }
         
         Resonance.sort(resList);
         for (int i = 0; i <resList.size(); i++) {
            Resonance res   = resList.get(i);
            String ind      = ""+ (modelIndex + 1);
            String str;
            Double val;
            
            sb.append(RESONANCE_HEADER); 
            sb.append("\n");
            
            sb.append(IO.pad(ind, -5, " "));
            
            str = res.getResonanceModel(). getShortNameWithParenthesis();
            sb.append(IO.pad(str, -6, " ")+ " ");
            
            str = res.getName();
            sb.append(IO.pad(str, 23, " "));
            
            str = res.getOrder();
            sb.append(IO.pad(str, 6, " "));
            
            str = "0"; 
            sb.append(IO.pad(str, 7, " "));
            
            // value in Hertz or PPM
            val =  res.getFreqFinalVal();
            val =  Units.convertUnits(val, procpar, res.getUnits(), curUnits);
            str =  doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            // value in Hertz or PPM
            val =  res.getFreqInitlVal();
            val =  Units.convertUnits(val, procpar, res.getUnits(), curUnits);
            str =  doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            // values in 1/seconds (no conversion needed)
            str = doubleValToString(f,res.getRateFinalVal(), numFieldLength, padChar  );
            sb.append(str);
            
            // values in 1/seconds (no conversion needed)
            str = doubleValToString(f,res.getRateInitlVal(), numFieldLength, padChar  );
            sb.append(str);
            
            // value in Hertz only
            val = res.getJ1FinalVal();
            str = doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            // value in Hertz only
            val = res.getJ1InitlVal();
            str = doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            
            // value in Hertz only
            val = res.getJ2FinalVal();
            str = doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            // value in Hertz only
            val = res.getJ2InitlVal();
            str = doubleValToString(f,val, numFieldLength, padChar );
            sb.append(str);
            
            sb.append("\n");
            sb.append("\n"); 
             ++modelIndex;
         }
         
         return sb;
     }
     private static String doubleValToString(String frmt, double val, int padlen, String padChar){
         String str = "";
         if (new Double(val).equals(Double.NaN)){
            return IO.pad(str,   padlen, " ");
         }
         else{
             str = String.format(frmt, val);
             str = str.substring(0,  padlen -1);
             return IO.pad(str,   padlen, padChar);
            }
     
     }
     public static String writeFileName(){
          String  str = "! File: \""+ FidViewer.getInstance().getProcpar().getFileSource()+"\"";
          return  str;
      }
     
    public static  String writePhaseAndShimmingParameters(BayesAnalyze ba ){
         StringBuilder sb        =    new StringBuilder();
         BayesParamsReader br    =    ba.getParamsReader();
         int n                   =    ba.getNumberOfGlobalModelParameters();
         String     str          =    utilities.IO.pad(n, -4, " ")+ " Global Model Parameters";
         String f                =   "%-15.12f";
         double val;
         
         sb.append(str);
         sb.append("\n");
         sb.append("\n");
         
         
         val = (br.isLoadedSuccessfully()) ?  br.getCenterPhase () : 0;
         str = String.format(f,val );
         sb.append("    Center Phase   = "+ str);
         sb.append("\n");
         
         val = (br.isLoadedSuccessfully()) ? br.getTimeDelay ()  :0;
         str = String.format(f,val );
         sb.append("    Time Delay     = "+str);
         sb.append("\n");
        
         if (  br.isLoadedSuccessfully() == true){
            int order = ba.getShimmingOrder ();
            if ( order  > 1){
                    str = String.format(f,br.getShimDelta ()[0] ) + " "
                            + String.format(f, br.getShimDelta ()[1] );
                
                sb.append("    Shim Delta     = "+str);
                sb.append("\n");

                for (SHIMMING shim: SHIMMING.values()) {
                     if(shim.getOrder() <= order ){
                        String key = utilities.IO.pad(shim.getName(), 9, 18, " ");
                        
                        Double curVal = br.getShimValues ().get(shim);
                        if ( curVal == null){
                            val = 0;
                        }
                        else {
                            val =  curVal;
                        }
                        str = String.format(f , val);
                        sb.append(key + " = "+str);
                        sb.append("\n");
                     }
                 }
            }
            
         }
          sb.append("\n");   
         return sb.toString();
     }
     
     
    public static String  overWriteField (String  line, String newVal){
            String [] strings = line.split("=");
            String newLine    = strings[0]+ "=" +  IO.pad(newVal, PAD_LEN, PAD_CHAR);     
        return newLine;
   }
    public static String  overWriteFileContent (String content,  String fieldPattern, String val) {
         Scanner scanner        =   new Scanner(content);
         String  newContent     =   new String(content);
        
         String line;
         
         
         while(scanner.hasNextLine()){
            line = scanner.nextLine();
             if( Pattern.matches( fieldPattern, line)){
                   String newLine = overWriteField(line,val);
                   newContent =  content.replace(line, newLine);
               }
         
         }
     
         scanner.close();
         return newContent;
     
   }
    public static void    overWriteFile (File file,  String fieldPattern, String  val) {
     
         String content         = IO.readFileToString(file);
         String curPattern      = fieldPattern;
         String curValue        = val;
         String newContent      = overWriteFileContent(content,curPattern, curValue);
         IO.writeFileFromString(newContent, file);
       
   }
    
    public static void    overwriteBayesFileHeader (File file) {
     
         String content         =   IO.readFileToString(file);
         Scanner scanner        =   new Scanner(content);
         String  newContent     =   new String(content);
         String line            =   scanner.nextLine();
         
        
         
        
         if (line.startsWith(FILE_HEADER)){
         
            String newLine      =   writeFileName();
            newContent          =  content.replace(line, newLine);
            IO.writeFileFromString(newContent, file);
         }
         scanner.close();
    }
    public static void    overwriteModelFileHeader (File file) {
         StringBuilder header   =   writeModelFileHeader();
         String content         =   IO.readFileToString(file);
         Scanner scanner        =   new Scanner(content);
         
         int length             =   0;
        
         for (int i = 0; i < 10; i++) {
            String line            =   scanner.nextLine();
            length                 +=  line.length() + 1 ;
           
            
        }
        String newContent = header + content.substring(length);
        scanner.close();
        
        IO.writeFileFromString(newContent, file);
         
    }
   
    public static void    updateModelFidInFile(  File   file, int modelFidIndex){
        
         if (file.exists()   == false) {return;}
         
         String modelFidPattern         = BayesParamsReader.getPatternForKey(BayesParamsReader.modelFidKey);
         String content                 = IO.readFileToString(file);
         String fidMod                  =  modelFidIndex +"";
         
         String newContent              = content;
                newContent              = BayesAnalyzeParamsWriter.
                                          overWriteFileContent(
                                          newContent,  modelFidPattern, fidMod);
         IO.writeFileFromString(newContent, file);
    }
    
     public static void main(String [] args){
         File file = new File("/Users/apple/Bayes/exp7/BayesOtherAnalysis/bayes.model.0001");
        // overWriteFile (file,  curPattern, value);
     }
}
