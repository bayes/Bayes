/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesEnterAsciiModel;
import java.io.File;
import java.util.*;
import utilities.IO;
import bayes.BayesManager;

/**
 *
 * @author apple
 */
public class Files  implements  BayesEneterAsciiConstants{
     public static final String ASCII_MODEL_FORMAT              = "%02d";
     private static final String CODE_END_MARKER                = "Delete From Here";
     private Files(){}
     
     public static void     writeDummyFortranFile (File dir, int index){
        String content                  = DUMMY_FORTRAN;
        String newContent               = overwriteFortanText(content, index);
        
        String fileName                 = String.format(MODEL +ASCII_MODEL_FORMAT + ".f", index);
        File file                       = new File(dir, fileName);
        IO.writeFileFromString(newContent, file);
     }
    public static void     overwriteAndCopyFortanOrCFile(File src,File dst, int index){
         if(src.getName().toLowerCase().endsWith(".f")){
             overwriteAndCopyFortanFile(src, dst, index);
         }
         else{
             overwriteAndCopyCFile(src, dst, index);
         }
         
     }
     public static void     overwriteAndCopyFortanFile(File src,File dst, int index){
         String oldContent              = IO.readFileToString(src);
         String newContent              =  overwriteFortanText(oldContent, index);
         IO.writeFileFromString(newContent, dst);
     }
     public static String   overwriteFortanText(String content, int index){
         StringBuilder sb               =   new StringBuilder();
         Scanner scanner                =   new Scanner(content);


         String modelName               =   getModelName(index);
         String oldFirstLine            =   scanner.nextLine();
         String newFirstLine            =   oldFirstLine.replace(MODEL,  modelName);
         sb.append(newFirstLine );
         sb.append(BayesManager.EOL);


         while(scanner.hasNextLine()){
            String nextLine  = scanner.nextLine();
            if (nextLine.contains(CODE_END_MARKER)){
                break;

            }
            else {

                sb.append(nextLine);
                sb.append(BayesManager.EOL);
            }
         }
         return sb.toString();
     }
       public static void     overwriteAndCopyCFile(File src,File dst, int index){
         String oldContent              = IO.readFileToString(src);
         String newContent              =  overwriteCText(oldContent, index);
         IO.writeFileFromString(newContent, dst);
     }
      public static String   overwriteCText(String content, int index){
         String modelName               =   getModelName(index);
         return  content.replace(C_MODEL_PREFIX,modelName.toLowerCase()+"_" );
      
     }
     
      
     
     private static String  getModelName(int index){
         String name   = String.format(MODEL +ASCII_MODEL_FORMAT, index);
         return name;
     }
     public static String   getStandardModelFileName(String modelname, int index){
         String filename      =     getModelName (index);
         int ind              =     modelname.lastIndexOf(".");
         String ext           =     modelname.substring(ind + 1,modelname.length());
         String name          =     filename+ "."+ ext;
         return name;
     
     }
   
    
}
