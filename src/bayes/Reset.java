/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import interfacebeans.ResultsViewer;
import load.LoadAndViewData;

/**
 *
 * @author apple
 */
public class Reset {

    public static void clearAsciiResutls(){
       // keep data files, but delete results from previous run
        LoadAndViewData.clearBayesOtherAnalysisDirectory( true);

       // delete results from previous ruun
        ResultsViewer.getInstance().resetToEmpty();
    }
    public static void clearFidResutls(){

       // keep data files, but delete results from previous run
        LoadAndViewData.clearBayesOtherAnalysisDirectory( true);
       // delete results from previous ruun
        ResultsViewer.getInstance().resetToEmpty();

    }
    public static void clearImageResutls(){
        // keep data files, but delete results from previous run
        LoadAndViewData.clearBayesOtherAnalysisDirectory( true);
       // delete results from previous ruun
        ResultsViewer.getInstance().resetToEmpty();
    }
}
