/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.commonImage;

import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author apple
 */
public class commoneImageFileFilter extends javax.swing.filechooser.FileFilter{
    public  boolean	accept(File file) {
        if (file.isDirectory()){return true;}
        boolean validExtention        =   false;
        String[] extensions = ImageIO.getWriterFormatNames();
     
        String name             =    file.getName();
        for (String extention : extensions ) {
            if (name.endsWith("."+extention)){
                validExtention = true;
                break;
            }
        }
       
        

        return validExtention;

    }
    public String getDescription(){
        return "Jpg image";
    }

}
