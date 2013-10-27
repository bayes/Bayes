/*
 * BayesAnalyzeFileFilter.java
 *
 * Created on September 24, 2007, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fid;
import javax.swing.filechooser.FileFilter;
import java.io.File;
/**
 *
 * @author apple
 */
 public class BayesAnalyzeFileFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        boolean bl = f.getName().contains(".fid");
       
        if (bl) {
                return true;
        } else {
                return false;
        }
     }


    //The description of this filter
    public String getDescription() {
        return "fid folders only";
    }
    private String getExtension(File f) {
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 &&  i < s.length() - 1) return s.substring(i+1).toLowerCase();
    return "";
  }
}