/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesEnterAsciiModel;
import java.io.File;
import utilities.*;
import utilities.EnterAsciiModel;
import bayes.WriteBayesParams;
import applications.model.Model;
import utilities.AsciiModelList;
import static bayes.WriteBayesParams.*;
/**
 *
 * @author apple
 */
public class EnterAsciiModelParamsWriter  implements  BayesEneterAsciiConstants {
   
    private EnterAsciiModelParamsWriter(){}
    
    
    public static boolean       writeParamsFile(Model model, AsciiModelList models, File file){
        String content      = writeParams(model, models).toString();
        boolean isSuccess   = IO.writeFileFromString(content, file);
        return isSuccess;
    }
    public static StringBuilder writeParams(Model model,  AsciiModelList models){
        StringBuilder sb = new StringBuilder();
        
        StringBuilder header  = writeParamsHeader(model, models);
        StringBuilder footer  =  writeModels(models);
        sb.append(header);
        sb.append(footer);
        
        return sb;
     }
    public static StringBuilder writeParamsHeader(Model model,  AsciiModelList models){
        StringBuilder sb        = new StringBuilder();
        StringBuilder header    = WriteBayesParams.writeParamsHeader(model);
        sb.append(header);
        String tmp;
        
        //next line
        tmp = IO.pad("Package Parameters", -PADLEN, PADCHAR );
        tmp = tmp + " = "+ 0;
        sb.append(tmp);
        sb.append("\n");
        
        //next line
        tmp = IO.pad("Number Of Models", -PADLEN, PADCHAR );
        tmp = tmp + " = "+ models.getNumberOfModels();
        sb.append(tmp);
        sb.append("\n");
        sb.append("\n");
        return sb;
     }
    
    
    public static StringBuilder writeModels( AsciiModelList enterAsciimodels){
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i <   enterAsciimodels.size() ; i++) {
           StringBuilder priorSB = writeModel(enterAsciimodels.get(i), i+1);
           sb.append( priorSB);
        }

        return sb;
    }
    public static StringBuilder writeModel(EnterAsciiModel reader, int index){
        StringBuilder sb = new StringBuilder();
        String tmp;
        
        //next line
        tmp = MODEL + " "+ index;
        tmp = IO.pad(tmp, -PADLEN, PADCHAR );
        tmp = tmp + " = " +  reader.getName();
        sb.append(tmp);
        sb.append("\n");
        
        //next line
        tmp = IO.pad("Number of Derived", -PADLEN, PADCHAR );
        tmp = tmp + " = " +  reader.getNumberOfDerived();
        sb.append(tmp);
        sb.append("\n");
        
        for (int i = 0; i < reader.getNumberOfDerived(); i++) {
            tmp = "Derrived" + " "+ (i+1);
            tmp = IO.pad(tmp, -PADLEN, PADCHAR );
            tmp = tmp + " = " +  reader.getDerived()[i];
            sb.append(tmp);
            sb.append("\n");
            
        }
        
        //next line
        tmp = IO.pad("Number of Vectors", -PADLEN, PADCHAR );
        tmp = tmp + " = " +  reader.getNumberOfModelVectors();
        sb.append(tmp);
        sb.append("\n");
        
        //next line
        tmp = IO.pad("Number Of Priors", -PADLEN, PADCHAR );
        tmp = tmp + " = " +  reader.getNumberOfPriors();
        sb.append(tmp);
        sb.append("\n");

        StringBuilder priorStr = WriteBayesParams.writePriors(reader.getPriors());
        sb.append(priorStr);
        sb.append("\n");
        
        return sb;
    }
     
  
}
