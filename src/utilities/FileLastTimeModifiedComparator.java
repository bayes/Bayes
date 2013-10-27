/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.File;
import java.util.Comparator;
/**
 *
 * @author apple
 */
public class FileLastTimeModifiedComparator implements Comparator<File> {

    public  int	compare(File f1, File f2) {
        long t1 = f1.lastModified();
        long t2 = f2.lastModified();

        if      (t1  > t2) { return  1;}
        else if (t1  < t2) { return -1;}
        else               { return  0;}
    }
}
