/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.commonImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author apple
 */
public class commonImageReader {
    public static final  int LAST_BIT_MASK  =   0x000000FF; 
    private File source                     =   null;
    private  BufferedImage bufferedImage    =   null;
    private boolean loaded                  =   false;
    private float [][] image                =   null;  
    
    
    public void readJPG (File imageFile){
        setLoaded(false);
        setSource(imageFile);
        try{
            bufferedImage           =   ImageIO.read(getSource());
            int width               = getBufferedImage().getWidth();
            int height              = getBufferedImage().getHeight(null);

            int npoints             = width*height;
            //Get Pixels
            int [] rgbs             =   new int[npoints];
            image                   =   new float [width][height];
              
            getBufferedImage().getRGB(0, 0, width, height, rgbs, 0, width); //Get all pixels
        
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
               
                   int count        =   w + h*width;
                   int cur          =   rgbs[count];
                   int lastByte     =   LAST_BIT_MASK &  cur; 
                   image[w][h]      =   lastByte;
                }
                
            }
                    
            setLoaded(true);  
        }
        catch(Exception e){
            setLoaded(false); 
            e.printStackTrace();
        }
        finally{}
    }
    public List<float [][]>  getImages(){
        List<float [][]> images = new ArrayList<float [][]>();
        if (image != null){
            images.add(image);
        }
        
        return images;
    
    }
    
    public static void main(String [] args){
        commonImageReader jr = new commonImageReader();
        File file = new File ("/Users/apple/Desktop/grayscale.jpg");
        //jr.readJPG(file);
         String[] names = ImageIO.getWriterFormatNames();
      for ( String name: names ){
         
         System.out.println( name );
         }
    }

    public File getSource() {
        return source;
    }
    public void setSource(File source) {
        this.source = source;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public float[][] getImage() {
        return image;
    }
    public void setImage(float[][] image) {
        this.image = image;
    }
    
}
