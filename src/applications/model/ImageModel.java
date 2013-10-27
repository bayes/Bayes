/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.model;
import  java.io.File;
import java.util.Collection;
/**
 *
 * @author apple
 */
public interface ImageModel extends Model {

    public  Collection <File> getFilesToTar();

}
