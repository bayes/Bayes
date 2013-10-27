/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;
import java.io.*;
import java.util.ArrayList;
import utilities.IO;
import bayes.ParameterPrior;
import fid.Units;
import static bayes.Enums.*;
import bayes.ParameterPrior.ORDER_TYPE;
import bayes.ParameterPrior.PRIOR_TYPE;
import bayes.ParameterPrior.PARAMETER_TYPE;
/**
 *
 * @author apple
 */
public class ISOFileWriter implements MetaboliteConstants{
     public static final String newLine             = "\n";
     public static final String pad                 = " ";
     public static final String doubleFormat        = "%6.5f"; 
     public static final UNITS  FREQ_OUT_UNITS      =   UNITS.PPM;
     public static float refFreq                    =   1.0f;
    
     public static boolean writeFile(MetaboliteInfo metaInfo, File file, UNITS freqUnits, float  specFreq){
        BufferedWriter out          =   null;
        String str;
           
        try{
         
            FileWriter fr      =    new FileWriter(file);
            out                =    new BufferedWriter(fr);
            
            out.write(newLine);
            str = IO.pad(metaInfo.getName(),0,0,  pad);
            out.write(str);
            out.write(newLine);
            out.write(newLine);
            
           
            out.write(writeParameterPriors (metaInfo));
            out.write(writeDerivedParameters(metaInfo));
            out.write(writeCouplingConstants(metaInfo));
            out.write(writeSiteNames(metaInfo));
            out.write(writeMetabolites(metaInfo, freqUnits,specFreq));
             
            out.close();
            fr.close ();
            
            return true;
         } catch (IOException ex) {
            ex.printStackTrace();
            return false;
            
        }
    }
    
     public static String writeParameterPriors(MetaboliteInfo metaInfo){
          StringBuilder sb = new StringBuilder();
          
          int frontPadding      =   3;
          int totalPadding      =   8;
           
          ArrayList<ParameterPrior>  paramPriors =  metaInfo.getParameterPriors();
          int count             =   paramPriors.size();
          sb.append(IO.pad(count,frontPadding,totalPadding,  pad));
          sb.append(METABOLIC_HEADER);
          sb.append(newLine);
             
           // write parameter priors  
         for (ParameterPrior parameterPrior : paramPriors) {
                sb.append(  writePrior( parameterPrior, 7));
                sb.append(newLine);
         }
    
           sb.append(newLine);
          
          return sb.toString();
     }
     public static String writeDerivedParameters(MetaboliteInfo metaInfo){
          StringBuilder sb = new StringBuilder();
          int count;
          int frontPadding      =   3;
          int totalPadding      =   7;
           
          ArrayList<String>  derivedParameters   =  metaInfo.getDerivedParameters();
          count                         =    derivedParameters.size();
          sb.append(IO.pad(count,frontPadding,totalPadding,  pad));
          sb.append(DERIVED_HEADER);
          sb.append(newLine);
             
           // write parameter priors  
             for (int i = 0; i < count; i++) {
                String derivedParameter = derivedParameters.get(i);
                sb.append(  derivedParameter);
                sb.append(newLine);
                
            }
          
           sb.append(newLine);
          
          return sb.toString();
     }
     public static String writeCouplingConstants(MetaboliteInfo metaInfo){
          StringBuilder sb = new StringBuilder();
          
          int frontPadding      =   3;
          int totalPadding      =   7;
           
          ArrayList<ParameterPrior>   couplingConstantsPriors  =  metaInfo.getCouplingConstantsPriors();
          int count             =   couplingConstantsPriors.size();    
          sb.append(IO.pad(count,frontPadding,totalPadding,  pad));
          sb.append(COUPLING_CONSTANT_HEADER);
          sb.append(newLine);
             
           // write parameter priors 
          for (ParameterPrior prior: couplingConstantsPriors) {
                sb.append(  writePrior( prior, 4));
                sb.append(newLine);
          }
           sb.append(newLine);
          
          return sb.toString();
     }
     public static String writeSiteNames(MetaboliteInfo metaInfo){
          StringBuilder sb = new StringBuilder();
          int count;
          int frontPadding      =   3;
          int totalPadding      =   7;
           
          ArrayList<String>  siteNames   =   metaInfo.getSiteNames();
          count                 =   siteNames.size();
          sb.append(IO.pad(count,frontPadding,totalPadding,  pad));
          sb.append(NUMBER_OF_SITES_HEADER);
          sb.append(newLine);
             
           // write parameter priors  
             for (int i = 0; i < count; i++) {
                String siteName = siteNames.get(i);
                sb.append( siteName);
                sb.append(newLine);
                
            }
          
           sb.append(newLine);
          
          return sb.toString();
     }
     public static String writeMetabolites(MetaboliteInfo metaInfo,  UNITS freqUnits, float  specFreq){
          StringBuilder sb          = new StringBuilder();
          StringBuilder tmp         = new StringBuilder();
          ParameterPrior prior;
          String str;
          int count;
          int frontPadding      =   3;
          int totalPadding      =   7;
          int totalDigitPadding =   13;
           
          ArrayList<Metabolite> metabolites  =   metaInfo.getMetabolites();
          
          MetaboliteInfo.convertUnits(metabolites,refFreq , Metabolite.frequencyUnits, FREQ_OUT_UNITS);
                                     
          count                      =   metabolites.size() ;
          sb.append(IO.pad(count,frontPadding,totalPadding,  pad));
          sb.append(METABOLITES_HEADER);
          sb.append(newLine);
          sb.append(newLine);
             
           // write parameter priors  
             for (int i = 0; i <metabolites.size(); i++) {
                     
                Metabolite metabolite = metabolites.get(i);
               
                frontPadding      =   0;
                totalPadding      =   5;
                sb.append(IO.pad(i+1,frontPadding,totalPadding,  pad));
                
                tmp         = new StringBuilder();
                tmp.append(SITE_NAME);
                tmp.append("=");
                tmp.append( metabolite.getSiteName());
                frontPadding      =   0;
                totalPadding      =   17;
                sb.append(IO.pad(tmp,frontPadding,totalPadding,  pad));
                
                tmp         = new StringBuilder();
                tmp.append(ABV);
                tmp.append("=");
                tmp.append( metabolite.getAbbreviation());
                frontPadding      =   0;
                totalPadding      =   17;
                sb.append(IO.pad(tmp,frontPadding,totalPadding,  pad));
                
                tmp         = new StringBuilder();
                tmp.append(TYPE);
                tmp.append("=");
                tmp.append( metabolite.getResonanceModel().getShortName());
                frontPadding      =   0;
                totalPadding      =   10;
                sb.append(IO.pad(tmp,frontPadding,totalPadding,  pad));
                
                
                tmp         = new StringBuilder();
                tmp.append(NAME);
                tmp.append("=");
                tmp.append("("+  metabolite.generateDegeneracyName()+")");
                sb.append(tmp);
               
                sb.append(newLine);

               // write frequency
               frontPadding      =   5;
               totalPadding      =   12;
               prior             = metabolite.getFrequency();
               sb.append(IO.pad(FREQUENCY,frontPadding,totalPadding,  pad));

               double val;
                
               frontPadding         =   0;
               totalPadding         =   13;
               
               val                  =   Units.convertUnits(prior.low, specFreq, freqUnits, UNITS.PPM);
               str                  =   String.format(doubleFormat,val);
               frontPadding         =  totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));
               
               val                  =   Units.convertUnits(prior.mean, specFreq, freqUnits, UNITS.PPM);
               str                  =   String.format(doubleFormat,val);
               frontPadding         =   totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               val                  =   Units.convertUnits(prior.high, specFreq, freqUnits, UNITS.PPM);
               str                  =   String.format(doubleFormat,val);
               frontPadding         =   totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               val                  =   Units.convertUnits(prior.sdev, specFreq, freqUnits, UNITS.PPM);
               str                  =   String.format(doubleFormat,val);
               frontPadding         =   totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               sb.append ( IO.pad(prior.priorType.getName().toLowerCase(),2,12,  pad));
               if (prior.order == ORDER_TYPE.NotOrdered){
                    sb.append(PRIOR + "="+ NOT_ORDERED);
               }
               

               sb.append(newLine);
              
              
              // write rate
               frontPadding         =   5;
               totalPadding         =   12;
               prior                = metabolite.getRate();
               sb.append(IO.pad(RATE,frontPadding,totalPadding,  pad));


               frontPadding         =   0;
               totalPadding         =   13;
               str = String.format(doubleFormat,prior.low);
               frontPadding         =  totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               str = String.format(doubleFormat,prior.mean);
               frontPadding         =  totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               str = String.format(doubleFormat,prior.high);
               frontPadding         =  totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               str = String.format(doubleFormat,prior.sdev);
               frontPadding         =  totalDigitPadding - str.length();
               sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));

               sb.append ( IO.pad(prior.priorType.getName().toLowerCase(),2,12,  pad));
               if (prior.order == ORDER_TYPE.NotOrdered){
                    sb.append(PRIOR + "="+ NOT_ORDERED);
               }
               

              sb.append(newLine);
              
              frontPadding      =   5;
              totalPadding      =   18;
              
              sb.append(IO.pad(PRIMARY,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getPrimary());
              sb.append(newLine);
              
              sb.append(IO.pad(JP,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getJp());
              sb.append(newLine);
              
              sb.append(IO.pad(SECONDARY,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getSecondary());
              sb.append(newLine);
              
              sb.append(IO.pad(JS,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getJs());
              sb.append(newLine);
              
              sb.append(IO.pad(TERTIARY,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getTertiary());
              sb.append(newLine);
              
              sb.append(IO.pad(JT,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getJt());
              sb.append(newLine);
              
              sb.append(IO.pad( AMPLITUDE_RATIOS,frontPadding,totalPadding,  pad));
              sb.append(metabolite.getAmplitudeRatios());
              sb.append(newLine);
              
              if (i < metabolites.size()-1){
                sb.append(newLine);
              }
              
          }
          return sb.toString();
     }
     
     
     public static String writePrior(ParameterPrior prior, int namePadding){
          StringBuilder sb = new StringBuilder();
          String str;
          int frontPadding ;
          int totalDigitPadding = 11;
           
          frontPadding       =  0;
          sb.append ( IO.pad(prior.name,frontPadding, namePadding,  pad));
          
          
          
          str = String.format(doubleFormat,prior.low);
          frontPadding       =  totalDigitPadding - str.length();
          sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));
          
          str = String.format(doubleFormat,prior.mean);
          frontPadding       =  totalDigitPadding - str.length();
          sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));
          
          str = String.format(doubleFormat,prior.high);
          frontPadding       =  totalDigitPadding - str.length();
          sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));
          
          str = String.format(doubleFormat,prior.sdev);
          frontPadding       =  totalDigitPadding - str.length();
          sb.append ( IO.pad(str,  frontPadding, totalDigitPadding,  pad));
          
          sb.append ( IO.pad(prior.priorType.getName().toLowerCase(),2,20,  pad));
          
          return sb.toString();
     }
     
    
      public static void main(String [] args){
    
        File file = new File ("/Users/apple/BayesSys/Bayes.Predefined.Spec/Example.ISO");
     
    } 
}