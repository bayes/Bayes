/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

import applications.model.Model;

public class PackageManager {

  
    private PackageManager(){} // make it a singleton
    
    public  static enum  Package{Exponential, BayesEnterAscii}
    private static Model currentApplication;
    
    
    // *************** getters and setters start *********************//
    public static Model getCurrentApplication() {
        return currentApplication;
    }

    public static void setCurrentApplication(Model aCurrentApplication) {
        currentApplication          = aCurrentApplication;
      //   LoadAndViewData.clearAsciiFileNonCompatibleWithModel(currentApplication);

    }
    
    
    public static boolean isMultipleDataColumnsModel(){
        Model model = getCurrentApplication();
        if ( model == null || model.getNumberOfDataColumns() == 1 ) {
             return false;
        }
        return true;
    }
   // *************** getters and setters  end *********************//
   
}
