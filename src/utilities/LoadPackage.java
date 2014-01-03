/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import bayes.DirectoryManager;
import fid.Resonance;
import applications.model.*;
import applications.bayesAnalyze.BayesAnalyze;
import applications.bayesFindResonances.BayesFindResonances;
/**
 *
 * @author apple
 */
public class LoadPackage {


    public static void loadPackage(Model model){


        if (model instanceof FidModel){
            if((model instanceof BayesAnalyze) == false && 
                    (model instanceof BayesFindResonances) == false ){
                IO.emptyDirectory(DirectoryManager.getBayesAnalyzeDir());
                Resonance.getResonanceList().clear();
            }
        }
        else if (model instanceof ImageModel){
        }


    }


}
