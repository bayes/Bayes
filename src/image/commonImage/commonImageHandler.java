/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.commonImage;

import image.ImageDescriptor;
import image.raw.BinaryReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author apple
 */
public class commonImageHandler {
    private commonImageReader imageReader       = new commonImageReader ();
    private ImageDescriptor id                  =   new ImageDescriptor();
    private BinaryReader binaryReader           =   new BinaryReader();
    
    public boolean handleHpegFile (File soruceFile){
        boolean success         =   false;
        imageReader                  = new commonImageReader ();
        try{
            imageReader.readJPG(soruceFile);
            if (imageReader.isLoaded() == false){
                throw new IllegalArgumentException();
            }
            
           binaryReader.setSourseFile( getSourceFile ()); 
           binaryReader.setComplex(false);
           binaryReader.setHeight(this.getHeight());
           binaryReader.setWidth(this.getWidth());
           binaryReader.setMagnImages(this.getImages());
            
            
            success             =   binaryReader.writeImages();
        }
        catch(Exception e){
            e.printStackTrace();
            success             =   false;
        }
        finally{
            return success;
        }
    
    }
    
    private File getSourceFile (){
        return imageReader.getSource();
    }
    private int getWidth(){
        return imageReader.getBufferedImage().getWidth();
    }
    private int getHeight(){
        return imageReader.getBufferedImage().getHeight();
    }
     private List<float [] []> getImages(){
        List<float [][]> images = new ArrayList<float [][]>();
        images.addAll(imageReader.getImages());
        
        return images;
    }
      
      public static void main(String [] args){
        commonImageReader jr = new commonImageReader();
        File file = new File ("/Users/apple/Desktop/grayscale.jpg");
    jr.readJPG(file);
    }
}

