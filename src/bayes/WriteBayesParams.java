package bayes;
import applications.model.Model;
import applications.model.PriorsInFileModel;
import java.util.*;
import java.io.*;
import ascii.ASCIIDataViewer;
import utilities.IO;


public class WriteBayesParams  {


    public static final int     PADLEN      =  30;
    public static final String  PADCHAR     = " ";




    private WriteBayesParams () { }

    public static boolean writeParamsFile(Model model, File dir){
        File file           =       new File (dir, BayesManager.BAYES_PARAMS  );
        String content      =       writeParams(model).toString();
        boolean isSuccess   =       IO.writeFileFromString(content, file);
        return isSuccess;
    }

    
    public static StringBuilder writeParams(Model model){
        StringBuilder sb        = new StringBuilder();
        StringBuilder header    = writeParamsHeader(model);
        StringBuilder priors    = writeParamsPriors(model);
        StringBuilder footer    = model.getModelSpecificsForParamsFile(PADLEN, PADCHAR);
        
        sb.append(header);
        sb.append(priors);
        sb.append(footer);
        return sb;
     }
    
    public static StringBuilder writeParamsHeader(Model model){
        StringBuilder sb = new StringBuilder();
        String EOL       =  BayesManager.EOL; // end of line char "\n"
        
        sb.append("! "+ model.getProgramName()+ " Package");
        sb.append(EOL);
             
        // line 2
        Calendar cal                    = Calendar.getInstance(TimeZone.getDefault());
        String DATE_FORMAT              = "dd-MMM-yyyy HH:mm:ss";
        java.text.SimpleDateFormat sdf  =  new java.text.SimpleDateFormat(DATE_FORMAT);
        sb.append("! Created " +  sdf.format(cal.getTime()));
        sb.append("  by " + BayesManager.getUser());
        sb.append(EOL);
            
        
        // line3
        sb.append("!");
        sb.append(EOL);
        
        // next line
        sb.append (IO.pad("Output Dir", -PADLEN, PADCHAR ));
        sb.append(" = "+ BayesManager.ASCII_DIR_NAME);
        sb.append(EOL);
        
        
        sb.append (IO.pad("Number Of Abscissa", -PADLEN, PADCHAR ));
        sb.append (" = "+ model.getNumberOfAbscissa());
        sb.append(EOL);
        
        
        sb.append (IO.pad("Number Of Columns", -PADLEN, PADCHAR ));
        sb.append (" = "+ model.getNumberOfDataColumns());
        sb.append(EOL);
             
        String [] files =  ASCIIDataViewer.getInstance().getFileNames();

        sb.append (IO.pad("Number Of Sets", -PADLEN, PADCHAR ));
        sb.append (" = "+ files.length);
        sb.append(EOL);
          
        for (String filename : files) {
            sb.append (IO.pad("File Name", -PADLEN, PADCHAR ));
            sb.append (" = "+  BayesManager.ASCII_DIR_NAME + Model.spr + filename);
            sb.append(EOL);
        }

 
        
        //next line
        sb.append (IO.pad("McMC Simulations", -PADLEN, PADCHAR ));
        sb.append (" = " +   ApplicationPreferences.getMcmcSims());
        sb.append(EOL);


        //next line
        sb.append (IO.pad("McMC Repeats", -PADLEN, PADCHAR ));
        sb.append (" = " + ApplicationPreferences.getMcmcReps());
        sb.append(EOL);

        //next line
        sb.append (IO.pad("Minimum Annealing Steps", -PADLEN, PADCHAR ));
        sb.append (" = "+ ApplicationPreferences.getMcmcSteps());
        sb.append(EOL);
        
        //next line
        sb.append (IO.pad("Histogram Type", -PADLEN, PADCHAR ));
        sb.append (" = "+ "Binned");
        sb.append(EOL);
        
        boolean isOutliers = model.isOutliers();
        String value = (isOutliers)?"Enabled":"Disabled";

        sb.append (IO.pad("Outlier Detection ", -PADLEN, PADCHAR ));
        sb.append (" = "+ value);
        sb.append(EOL);

      
        //next line
        int number_of_priors    = model.getNumberOfPriors();
        if (model instanceof PriorsInFileModel) {number_of_priors = 0;}
        
        sb.append (IO.pad("Number Of Priors", -PADLEN, PADCHAR ));
        sb.append (" = "+ number_of_priors);
        sb.append(EOL);
       
        return sb;
     }
    
    public static StringBuilder writeParamsPriors(Model model){
        StringBuilder sb        =   new StringBuilder();
        int number_of_priors    =   model.getNumberOfPriors();
        
        if (model instanceof PriorsInFileModel) {number_of_priors = 0;}
        
        if (number_of_priors > 0){
                sb.append(BayesManager.EOL);
                sb.append(writePriors(model)); 
        }
            
         return sb;
     }
    public static StringBuilder writePriors(Model model){
        List<ParameterPrior> priors  = model.getPriors  ();
        return writePriors(priors);
     
    }
    public static StringBuilder writePriors( List<ParameterPrior>  priors ){
        StringBuilder buffer = new StringBuilder();
        buffer.append(writePriorHeader());
        
        for (ParameterPrior prior : priors) {
             buffer.append (writePrior(prior));
        }
        return buffer;
    }
    public static StringBuilder writePrior(ParameterPrior  prior ){
        int padlen  = 11;
        String padChar = " ";
        String f  = "%1.4E"; 
        String f1 = "%1.1E"; 
        
        StringBuilder buffer = new StringBuilder();

        buffer.append(utilities.IO.pad(prior.name, padlen+3, padChar) );

        if (prior.low >= 0.0){
            buffer.append( "  "+ String.format (f,prior.low)  );
        } else {
            buffer.append( " " + String.format (f,prior.low)  );
        }

        if (prior.mean >= 0.0){
            buffer.append( "  "+ String.format (f,prior.mean) );
        } else {
            buffer.append( " " + String.format (f,prior.mean) );
        }

        if (prior.high >= 0.0){
            buffer.append( "  "+ String.format (f,prior.high)  );
        } else {
            buffer.append( " " + String.format (f,prior.high)  );
        }


        if (prior.sdev >= 0.0){
            buffer.append( "  "+ String.format (f,prior.sdev) );
        } else {
            buffer.append( " " + String.format (f,prior.sdev) );
        }

        if (prior.norm >= 0.0){
            buffer.append( "  "+ String.format (f1,prior.norm) );
        } else {
            buffer.append(  " " + String.format  (f1,prior.norm));
        }   

        buffer.append("   ");
        buffer.append(utilities.IO.pad(prior.priorType.getName()  , 12, padChar) );
        buffer.append(utilities.IO.pad(prior.order , 13, padChar) );
        buffer.append( " " +prior.getParameterType().getName()  );
        buffer.append(BayesManager.EOL);

        
       return buffer;
    }


    public static StringBuilder writePriorHeader(){
        int padlen              = 11;
        StringBuilder buffer    = new StringBuilder();
        
        buffer.append(utilities.IO.pad("ParamDesc", padlen + 8, PADCHAR) );
        buffer.append(utilities.IO.pad("Low", 9, PADCHAR) );
        buffer.append(utilities.IO.pad("Mean/Rate", 15, PADCHAR) );
        buffer.append(utilities.IO.pad("High", padlen, PADCHAR) );
        buffer.append(utilities.IO.pad("Sigma", padlen, PADCHAR) );
        buffer.append(utilities.IO.pad("Norm", 7, PADCHAR) );
        buffer.append(utilities.IO.pad("PriorType", 10, PADCHAR) );
        buffer.append(utilities.IO.pad("Ordered/NotOrdered", 19, PADCHAR) );
        buffer.append( "ParamType" );
        buffer.append ("\n");
       
        
         return buffer;
    }
    
    
    
}
