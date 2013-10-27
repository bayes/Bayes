/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

/**
 *
 * @author apple
 */
public class ImageHelper {
    public static   boolean areEquivalentImages(ImageDescriptor id1, ImageDescriptor id2 ){
        boolean equal = true;
        if (id1 == null || id2 == null){ return false;}
        if (id1.getNumberOfColumns() != id2.getNumberOfColumns() ){return false;}
        if (id1.getNumberOfRows()    != id2.getNumberOfRows   ()  ){return false;}

        /*
         float ar1      =    ImageDescriptor.computeImageAspectRatio(id1);
         float ar2      =    ImageDescriptor.computeImageAspectRatio(id2);
         if (ar1 != ar2) {return false;}
        */
       
        return equal;
    }
    
}
