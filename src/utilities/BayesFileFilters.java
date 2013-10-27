/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import bayes.BayesManager;
import bayes.ApplicationConstants;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import image.siemens.Ima;
import image.siemens.Raw;
import image.siemens.Rda;
import image.varian.Fdf;


/**
 *
 * @author apple
 */
public class BayesFileFilters {
    public static final String dotRes  = ".RES";
    public static final String dotISO  = ".ISO";
    
    public static class SelectFortranAndCFiles extends javax.swing.filechooser.FileFilter {
        public boolean accept(File f) {

                boolean bl = f.getName().contains(".f")
                            ||f.getName().contains(".c");
       
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return true;
                }
                else {
                 return false;
            }
             }
        public String getDescription() {
            String message = "Selects .f and .c files";
            return message;
        }
    }
    public static class SelectFortranAndCFilesNoDir extends javax.swing.filechooser.FileFilter{
        public boolean accept(File f) {

                boolean bl = f.getName().contains(".f")
                            ||f.getName().contains(".c");
       
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return false;
                }
                else {
                 return false;
            }
             }
        public String getDescription() {
            String message = "Selects .f and .c files";
            return message;
        }
    } 

    public static class ISOFileChooserFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File f) {

                boolean bl = f.getName().endsWith(dotISO) ;
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return true;
                }
                else {
                 return false;
            }
             }
        public String getDescription() {
            String message = "Selects ISO files";
            return message;
        }
     
    }
    public static class ISOFileFilter        implements FileFilter {
        public boolean accept(File f) {

                boolean bl = f.getName().endsWith(dotISO) ;
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return true;
                }
                else {
                 return false;
            }
             }
     
    }
    public static class ResFileChooserFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File f) {

                boolean bl = f.getName().endsWith(dotRes) ;
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return true;
                }
                else {
                 return false;
            }
             }
        public String getDescription() {
            String message = "Selects Res files";
            return message;
        }
     
    }
    public static class ResFileFilter        implements FileFilter {
        public boolean accept(File f) {

                boolean bl = f.getName().endsWith(dotRes) ;
                if (bl) {
                    return true;
                }
                else if (f.isDirectory()){
                    return true;
                }
                else {
                 return false;
            }
             }
     
    }
    
    public static class ModelFileFilter implements FileFilter{
           
        public boolean accept(File f) {
                boolean bl = f.getName().startsWith( BayesManager.MODEL_FILE_NAME) ;
                
                if (bl) {return true;}
                else if (f.isDirectory()){  return false;}
                else {return false;}
                 
            }
    }

    public static class ImageFileChooserFilter extends javax.swing.filechooser.FileFilter{
           
        public boolean accept(File file) {
                boolean dontAccept  = false;
                boolean accept      = true;
                if (file.isDirectory()){  return accept;}

                String end          =   "."+ BayesManager.IMG;
                boolean bl          =   file.getName().endsWith( end ) ;
                if (bl == false) {return dontAccept;}
                
                String ifhEnd       =   "."+ BayesManager.IFH;
                String ifhName      =   file.getName().replace(end, ifhEnd);
                File  dir           =   file.getParentFile();
                File ihfFile        =   new File (dir,ifhName  ) ;
                if (ihfFile.exists() == false) {return dontAccept;}
               
                return  accept;
            }
        public String getDescription() { return ".img files";}
        
    }
    public static class ProcparFileChooserFilter extends javax.swing.filechooser.FileFilter{

           public boolean accept(File f) {
                        String pr = ApplicationConstants.PROCPAR_FILE_NAME;
                        if      ( f.isDirectory()) { return true;}
                        else if ( f.getName().contains(pr ) ==true  ){return true;}
                        else {return false;}
              }
           public String getDescription() {return "Procpar"; }

    }
    public static  class AsciiFileFilter    implements FileFilter {
        List<File> asciiFiles;
        public AsciiFileFilter(List<File> files){
            asciiFiles = files;
        }
        public boolean accept(File file) {
            if ( asciiFiles.contains(file)      == true)  { return false;}
            if (file.getName().endsWith(".c")   == true) { return false;}
            if (file.getName().endsWith(".f")   == true) { return false;}
            if (file.getName().endsWith(".so")  == true) { return false;}
            if (file.getName().endsWith(".lst") == true) { return false;}
            if (file.getName().equals( BayesManager.BAYES_NOISE_FILE_NAME )   == true) { return false;}
            if (file.getName().equals(BayesManager.ABSCISSA_FILE_NAME)== true) { return false;}
            if (file.getName().equals(BayesManager.PROBAILITY_FILE_NAME))      {return false;}
            return true;
        }
    };
    public static  class DYCOMFileFilter    implements FileFilter {

        public boolean accept(File file) {
            if ( file.isDirectory()      == true)  { return false;}
            if (file.getName().endsWith(".dcm")   == true) { return true;}
            return false;
        }
    };
    public static  class SiemensRDAFileFilter   extends    javax.swing.filechooser.FileFilter{

         public boolean accept(File file) {
            if ( file.isDirectory()      == true)  { return true;}
            if (file.getName().endsWith(Rda.FILE_EXT )   == true) { return true;}
            return false;
        }
         public String getDescription() {
            String message = "Siemens RDA";
            return message;
        }
    };
    public static  class SiemensRAWFileFilter   extends    javax.swing.filechooser.FileFilter{

         public boolean accept(File file) {
            if ( file.isDirectory()      == true)  { return true;}
            if (file.getName().endsWith(Raw.FILE_EXT )   == true) { return true;}
            return false;
        }
         public String getDescription() {
            String message = "Siemens RAW";
            return message;
        }
    };
    public static  class SiemensImaFileFilter   extends    javax.swing.filechooser.FileFilter{

         public boolean accept(File file) {
            if ( file.isDirectory()      == true)  { return true;}
            if (file.getName().endsWith("."+Ima.FILE_EXT )   == true) { return true;}
            return false;
        }
         public String getDescription() {
            String message = "Siemens Ima images";
            return message;
        }
    };
    public static  class VarainFdfFileFilter   extends    javax.swing.filechooser.FileFilter{

         public boolean accept(File file) {
            if ( file.isDirectory()      == true)  { return true;}
            if (file.getName().endsWith("."+Fdf.FILE_EXT )   == true) { return true;}
            return false;
        }
         public String getDescription() {
            String message = "Varian fdf images";
            return message;
        }
    };
}    
    
    
