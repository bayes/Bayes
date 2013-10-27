/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;
import java.util.*;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Point;


/**
 *
 * @author apple
 */
public class ImageManager  implements ImageConstants{
  
    public static Roi roi                                 =     null;
    public static Roi previousRoi                         =     null; 
    public static Rectangle clip                          =     new Rectangle(0,0,0,0);  
    public static boolean showAllROIs                     =     true;
    public static int TOOL_ID                             =     Roi.RECTANGLE;
    private static float horizontalScale                  =     1.0f;
    private static  float verticalScale                   =     1.0f;
    protected static List< float[][] > allImages          =     new ArrayList< float[][] > ();
    protected static ImageDescriptor imageDescriptor      =     null;
   
   
    public static void reset(){
       System.out.println("Image Manager reset");
        ImageManager.setHorizontalScale(1.0f);
        ImageManager.setVerticalScale(1.0f);
        roi                                 =   null;
        previousRoi                         =   null;  
        showAllROIs                         =   true;
        clip                                =   new Rectangle(0,0,0,0);
        TOOL_ID                             =   Roi.RECTANGLE; 
        imageDescriptor                     =   null;
        allImages.clear();
   }
    
   
   /** Deletes the current region of interest. Makes a copy
        of the current ROI so it can be recovered by the
        Edit/Restore Selection command. */
    public static void killRoi() {
        if (roi!=null) {
            saveRoi(); 
            roi = null;
            if (TOOL_ID == Roi.RECTANGLE ){
                updateScaledRoiBounds(previousRoi.getBounds());
            }
        }
    }
    public static void killRoiWithoutSaving() {
        if (roi!=null) { roi = null;}
    }
    public static void saveRoi() {
        if (roi!=null) {
            Rectangle r = roi.getBounds();
            if ( roi.getType() == Roi.RECTANGLE  ){
                if (r.width>0 && r.height>0) {
                    previousRoi = (Roi)roi.clone();
                }
            }
        }
    }
    public  static void drawRoi(Graphics g) {
         if (roi == null) {return;}
         roi.draw(g);
    }
    
    public static Rectangle updateScaledRoiBounds( Rectangle roiBounds ){
        
        float     hScl       = getHorizontalScale();
        float     vScl       = getVerticalScale();
     
        
        double offsetH      = roiBounds.getX()/ hScl;
        double offsetV      = roiBounds.getY() /vScl;
        double unscaledEndX = roiBounds.getX() + roiBounds.getWidth();
        double unscaledEndY = roiBounds.getY() + roiBounds.getHeight();
        
        int    scaledStartX = (int) Math.floor( offsetH ); 
        int    scaledStartY = (int) Math.floor(offsetV);
        int    scaledEndX   = clip.x+ (int) Math.ceil(unscaledEndX/ hScl); 
        int    scaledEndY   = clip.y +(int) Math.ceil(unscaledEndY/ vScl);
        
        int    x            = clip.x        + scaledStartX;  
        int    y            = clip.y        + scaledStartY;  
        int    width        = scaledEndX - x;  
        int    height       = scaledEndY - y;  
        
        clip.setBounds(x, y, width, height);
       // System.out.println(clip);
        return clip;
     
     }
    public static Point getScaledPoint( Point mousePoint){
        
        float     hScl       = getHorizontalScale();
        float     vScl       = getVerticalScale();
         
        int    x            = clip.x        + (int) Math.floor(mousePoint.x/hScl);  
        int    y            = clip.y        + (int) Math.floor(mousePoint.y/vScl);  
        
        return new Point(x,y);
     
     }
    public  static void createNewRoi(int sx, int sy, Component ic) {
        killRoi();
        switch (TOOL_ID) {
            case Roi.RECTANGLE  : roi = new Roi(sx, sy, ic);break;
                
            case Roi.OVAL       :  /* roi = new OvalRoi(sx, sy, ic) */;break;
                
            case Roi.POLYGON    : break;
            case Roi.POLYLINE   : break;
            case Roi.ANGLE      : /*roi = new PolygonRoi(sx, sy, ic)*/;break;
            case Roi.FREEROI    : break;
            case Roi.FREELINE   : /*roi = new FreehandRoi(sx, sy, ic)*/;break;
            case Roi.LINE       : /*roi = new Line(sx, sy, ic)*/; break;
            case Roi.POINT      : /*roi = new PointRoi(sx, sy, this)*/; break;
               
        }
    }
    public static boolean isRectangleRoi(){
    return (TOOL_ID == Roi.RECTANGLE);
  }
    public static float getImageAspectRatio(){
        ImageDescriptor id      =       getImageDescriptor ();
        return   getImageAspectRatio(id);
   } 
    public static float getImageAspectRatio( ImageDescriptor id){
        float aspectRatio       =       1;
        double widthMMperPix    =       id.getScaling2();
        double heightMMperPix   =       id.getScaling1();
        double heightInPix      =       id.getNumberOfRows();
        double widthtInPix      =       id.getNumberOfColumns();
        
        double widthInMM        =       widthtInPix * widthMMperPix;
        double heightInMM       =       heightInPix * heightMMperPix;
        aspectRatio             =       (float) (widthInMM/heightInMM);
        return  aspectRatio;
   } 
   
    // static getters and setters //
    public static float[][] getPixels ( int curSlice,  int curElement ) {
         // curSlice and curElement are starting from 0
         int slc            =   getImageDescriptor().getNumberOfSlices();
         int index          =   curSlice + curElement*slc;
        
         float[][] pixels   =   ImageManager.getAllImages().get(index);
         return  pixels;
   }
    public static List<float[][]> getAllImages () {
        return allImages;
    }
    public static void setAllImages ( List<float[][]> aAllImages ) {
        allImages = aAllImages;
    }
    public static ImageDescriptor getImageDescriptor () {
        return imageDescriptor;
    }
    public static void setImageDescriptor ( ImageDescriptor aImageDescriptor ) {
        imageDescriptor = aImageDescriptor;
    }
    public static float getHorizontalScale () {
        return horizontalScale;
    }
    public static void setHorizontalScale ( float magnification ) {
        ImageManager.horizontalScale = magnification;
    }
    public static float getVerticalScale () {
        return verticalScale;
    }
    public static void setVerticalScale ( float aVerticalScale ) {
        verticalScale = aVerticalScale;
    }
}
