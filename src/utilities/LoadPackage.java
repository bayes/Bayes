/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import bayes.DirectoryManager;
import java.io.*;
import fid.Resonance;
import applications.model.*;
import applications.bayesAnalyze.BayesAnalyze;
import utilities.*;
/**
 *
 * @author apple
 */
public class LoadPackage {


    public static void loadPackage(Model model){


        if (model instanceof FidModel){
            if((model instanceof BayesAnalyze) == false){
                IO.emptyDirectory(DirectoryManager.getBayesAnalyzeDir());
                Resonance.getResonanceList().clear();
            }
        }
        else if (model instanceof ImageModel){
        }


    }


}
