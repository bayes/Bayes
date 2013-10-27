/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.File;
import java.io.FileFilter;
import bayes.BayesManager;
       

/**
 *
 * @author apple
 */
public class FileFilters {
    private  FileFilters(){}
    
   static public class   ModelsAndParams  implements   FileFilter{
        public boolean accept(File file) {
            String model    = BayesManager.MODEL_FILE_NAME;
            String params   = BayesManager.BAYES_PARAMS;
            String param    = BayesManager.BAYES_PARAMS;
            if (file.getName().startsWith( model )  == true) { return true;}
            if (file.getName().startsWith( params)  == true) { return true;}
            if (file.getName().startsWith( param )  == true) { return true;}
            return false;
        }
  }
   static public class   FileNameStartsWithBayes  implements   FileFilter{
        public boolean accept(File file) {
            String str1    = "Bayes"; //"Bayes";
            String str2    = "bayes"; //"bayes";
            if (file.getName().startsWith( str1 )  == true) { return true;}
            if (file.getName().startsWith( str2 )  == true) { return true;}
            return false;
        }
}
   static public class   FilesToBeCopyToFidDir  implements   FileFilter{
        public boolean accept(File file) {
            String fid      = BayesManager.FID_FILE_NAME; //"id";
            String text     = BayesManager.FID_TEXTFILE_NAME; //"text";
            String procpar  = BayesManager.PROCPAR_FILE_NAME; //"procpar";
            
            if (file.getName().startsWith( fid    )  == true) { return true;}
            if (file.getName().startsWith( text   )  == true) { return true;}
            if (file.getName().startsWith( procpar)  == true) { return true;}
            return false;
        }
}

}