/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;
import java.io.*;
import interfacebeans.JAllPriors;
import bayes.ParameterPrior;
import utilities.IO;
import java.util.Scanner;
import java.util.ArrayList;
import static bayes.Enums.*;
import bayes.ParameterPrior.ORDER_TYPE;
import bayes.ParameterPrior.PRIOR_TYPE;
import bayes.ParameterPrior.PARAMETER_TYPE;
/**
 *
 * @author apple
 */
public class ISOFileReader  implements MetaboliteConstants{
  
    
    public static MetaboliteInfo    readFile(File file){
        MetaboliteInfo metaboliteInfo   =   new  MetaboliteInfo();
        String content                  =   IO.readFileToString(file);
        Scanner scanner                 =   new Scanner(content);
            
        scanner.nextLine();
        metaboliteInfo.setName(scanner.nextLine());
        scanner.nextLine();


       ArrayList<ParameterPrior> parameterPriors     =  readMetabolicParameterPriors(scanner);
       metaboliteInfo.setParameterPriors(parameterPriors);

       ArrayList<String> derivedParameters    =   readDerivedParameters (scanner);
       metaboliteInfo.setDerivedParameters(derivedParameters);

       ArrayList<ParameterPrior> couplingConstantsPriors = readCouplingConstantsPriors(scanner);
       metaboliteInfo.setCouplingConstantsPriors(couplingConstantsPriors);

       ArrayList<String> siteNames = readSiteNames(scanner);
       metaboliteInfo.setSiteNames(siteNames);


       ArrayList<Metabolite> metabolites    =   readMetabolites(scanner, couplingConstantsPriors);
       metaboliteInfo.setMetabolites(metabolites);

       metaboliteInfo.setLoaded(true);


       scanner.close();

       return metaboliteInfo;
        
    }
    public static Metabolite        readMetabolite(Scanner scanner) throws IllegalArgumentException{
      Metabolite metabolite   = new  Metabolite();
      double norm             = -2;
        String str;
        // read header
        scanner.next(); // skip metabolite number
        
        str                 =    scanner.next();
        str                 =    getValue(str);
        metabolite.setSiteName(str);
        
        str                 =    scanner.next();
        str                 =    getValue(str);
        metabolite.setAbbreviation(str);
        
        str                 =    scanner.next();
        str                 =    getValue(str);
        RESONANCE_MODEL type =   RESONANCE_MODEL.getResonanceModelFromShortName(str);
        metabolite.setResonanceModel(type);
        
        str                 =    scanner.nextLine();
        str                 =    getValue(str);
        metabolite.setDegeneracyName(str);
        
        
        
         // read frequency prior
         ParameterPrior prior = new ParameterPrior();
         str                = scanner.next();
         prior.name         = JAllPriors.FREQ_PARAM_NAME +"_"+metabolite.getAbbreviation();
         prior.low          = scanner.nextDouble();
         prior.mean         = scanner.nextDouble();
         prior.high         = scanner.nextDouble();
         prior.sdev         = scanner.nextDouble();
         prior.norm         = norm;
         prior.setParameterType(PARAMETER_TYPE.Frequency);
         prior.priorType    = PRIOR_TYPE.getTypeByName(scanner.next());
         
         str = scanner.nextLine();
         
         if (str.contains(NotOrderedPattern)) {
            prior.order = ORDER_TYPE.NotOrdered;
         }
         else{
             prior.order = defaultOrderType; 
         }
         metabolite.setFrequency(prior );
        
         
         
         // read rate prior
         prior              = new ParameterPrior();
         str                = scanner.next();
         prior.name         = JAllPriors.RATE_PARAM_NAME +"_"+metabolite.getAbbreviation();
         prior.low          = scanner.nextDouble();
         prior.mean         = scanner.nextDouble();
         prior.high         = scanner.nextDouble();
         prior.sdev         = scanner.nextDouble();
         prior.norm         = norm;
         prior.priorType    = PRIOR_TYPE.getTypeByName(scanner.next());
         str = scanner.nextLine();
         
         if (str.contains(NotOrderedPattern)) {
            prior.order = ORDER_TYPE.NotOrdered;
         }
         else{
             prior.order = ORDER_TYPE.LowHigh; 
         }
         
        
         metabolite.setRate(prior );
        
         
         
         // read Primary
         scanner.next();
         metabolite.setPrimary(scanner.nextInt());
         scanner.nextLine();
         
         // read JP
         scanner.next();
         metabolite.setJp(scanner.nextInt());
         scanner.nextLine();
         
         // read Secondary
         scanner.next();
         metabolite.setSecondary(scanner.nextInt());
         scanner.nextLine();
         
         // read JS
         scanner.next();
         metabolite.setJs(scanner.nextInt());
         scanner.nextLine();
         
         // read Tertiary
         scanner.next();
         metabolite.setTertiary(scanner.nextInt());
         scanner.nextLine();
         
         // read JT
         scanner.next();
         metabolite.setJt(scanner.nextInt());
         scanner.nextLine();
       
         // read Amplitude ratios
         scanner.next(); // read "Ampl"
         scanner.next(); // read "Ratios"
         metabolite.setAmplitudeRatios( scanner.nextLine().trim());
        
         if (scanner.hasNextLine()) {scanner.nextLine();}
     
      
      return metabolite;
      
 }   
    public static ParameterPrior    readParameterPior(Scanner scanner){
      ParameterPrior prior = new ParameterPrior();
      try{
         prior.name         = scanner.next();
         prior.low          = scanner.nextDouble();
         prior.mean         = scanner.nextDouble();
         prior.high         = scanner.nextDouble();
         prior.sdev         = scanner.nextDouble();
         prior.priorType    = PRIOR_TYPE.getTypeByName(scanner.next());
         
         String str         = scanner.nextLine();
         
         if (str.contains(NotOrderedPattern)) {
            prior.order = ORDER_TYPE.NotOrdered;
         }
         else{
             prior.order = defaultOrderType; 
         }
     }

    catch(IllegalArgumentException exp){ 
        exp.printStackTrace();
        return null;
    } 

      return prior;
      
 }   

    public static ArrayList<ParameterPrior>     readMetabolicParameterPriors(Scanner scanner){
          int count = scanner.nextInt();
          scanner.nextLine();
                
          ArrayList<ParameterPrior>   parameterPriors    =   new  ArrayList<ParameterPrior> ();
            for (int i = 0; i < count; i++) {
                ParameterPrior prior =  readParameterPior(scanner);
                parameterPriors.add(prior);
            }
       
          return parameterPriors;
    }
    public static ArrayList<ParameterPrior>     readCouplingConstantsPriors(Scanner scanner){
           int count = scanner.nextInt();
           scanner.nextLine();
           
           
           ArrayList<ParameterPrior> couplingConstantsPriors     =   new ArrayList<ParameterPrior> ();
           for (int i = 0; i < count; i++) {
               ParameterPrior prior =  readParameterPior(scanner);
               prior.isCouplingConstant = true;
               couplingConstantsPriors.add(prior);
                
            }
          return couplingConstantsPriors;
    }
    public static ArrayList<String>             readDerivedParameters(Scanner scanner){
        int count;   
        scanner.nextLine();
        count = scanner.nextInt();
        scanner.nextLine();
            
          
       ArrayList<String>  derivedParameters    =   new ArrayList<String> ();
       for (int i = 0; i < count; i++) {
            derivedParameters.add(scanner.next());

       }
       return  derivedParameters;
    }
    public static ArrayList<String>             readSiteNames(Scanner scanner){
        int count;   
        scanner.nextLine();
        count = scanner.nextInt();
        scanner.nextLine();


        ArrayList<String>     siteNames    =   new ArrayList<String> ();
        for (int i = 0; i < count; i++) {
             siteNames.add(scanner.next());

        }
       return   siteNames;
    }
    public static ArrayList<Metabolite>         readMetabolites(Scanner scanner,  ArrayList<ParameterPrior>  couplingConstants){
        int count;   
        scanner.nextLine();
        count = scanner.nextInt();
        scanner.nextLine();
           
       ArrayList<Metabolite> metabolites    =   new ArrayList<Metabolite>();
           for (int i = 0; i < count; i++) {
                
                Metabolite m = readMetabolite(scanner);
                
                // check if it is resonance or metabolite
                if ( couplingConstants.size() > 0){
                    int primaryIndex =  m.getJp();
                    m.primaryCoupling = couplingConstants.get(primaryIndex -1);

                    int secondaryIndex = m.getJs();
                    m.secondaryCoupling =  couplingConstants.get(secondaryIndex -1);

                    int tertiaryIndex =m.getJt();
                    m.tertiaryCoupling =  couplingConstants.get(tertiaryIndex -1);
                }
                metabolites.add(m);
           }
        return metabolites;
    }
    
    public static String getValue(String keyvalue) throws IllegalArgumentException{
    
    String [] strs = keyvalue.split("=");
    
    if (strs.length != 2 ){ 
        throw new IllegalArgumentException( "Error while parsing . file. \n"+
                "Key-value pair "+  keyvalue + " is illegal"); 
    }
           return strs[1];
    }
    public static void main(String [] args){
    
    String str = "1    Site_Name=GLU3   Abv=GLU3D2345   Type=CP   Name=(Doublet of Doublets of Doublets) + \n"                
     + "  Freq:      51.19927     52.18427     55.22427      0.0200  gaussian  Prior=(Not Ordered)  \n"
     + "  Rate:       0.00000      3.00000     25.00000      3.0000  gaussian     \n" 
     + "  Primary:     2     \n" 
     + "  JP:          3     \n" 
     + "  Secondary:   1     \n" 
     + "  JS:          2     \n" 
     + "  Tertiary:    4     \n" 
     + "  JT:          4     \n" 
     + "  Amp Ratios:  5     \n" ;
        
   // Scanner scanner = new Scanner(str);
      //  readMetabolite(scanner);
        File file = new File ("/Users/apple/BayesSys/Bayes.Predefined.Spec/Glutamate.3.0.ISO");
        //scanner.close();
        readFile(file);
    } 
}
