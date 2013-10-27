/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

import java.io.File;

/**
 *
 * @author apple
 */
public class Bruker2DSeqFileFilter extends javax.swing.filechooser.FileFilter{
    public  boolean	accept(File file) {
        if (file.isDirectory()){return true;}

        String name             =    file.getName();
        boolean validName       =  name.equals("2dseq");

        return validName;

    }
    public String getDescription(){
        return "Bruker Binary";
    }
}
