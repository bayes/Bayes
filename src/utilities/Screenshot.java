/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 *
 * @author apple
 */
public class Screenshot {

    public static IMAGE_TYPE getDEFAULT_IMAGE_TYPE() {
        return DEFAULT_IMAGE_TYPE;
    }

    public static void setDEFAULT_IMAGE_TYPE(IMAGE_TYPE aDEFAULT_IMAGE_TYPE) {
        DEFAULT_IMAGE_TYPE = aDEFAULT_IMAGE_TYPE;
    }
    private File screenshotFile             =   null;
    private boolean success                 =   false;
    private String message                  =   null;
    public static  SimpleDateFormat sdf     =   new SimpleDateFormat("yyyyMMddHHmmss");
    public File getScreenshotFile() {
        return screenshotFile;
    }
    public void setScreenshotFile(File screenshotFile) {
        this.screenshotFile = screenshotFile;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static enum IMAGE_TYPE {
        GIF      ( "gif" ),
        PNG      (  "png"),
        JPG      (  "jpg");

        private String type;
        IMAGE_TYPE(String name){
            type = name;
        }

        public String getType() {
            return type;
        }
        public void setType(String javaname) {
            this.type = javaname;
        }
        
       public static IMAGE_TYPE getImageTypeByValue(String val)  throws IllegalArgumentException  { 
            for ( IMAGE_TYPE type : IMAGE_TYPE.values()) {
                if (type.getType().equalsIgnoreCase(val)) {
                    return type;
                }
            }
             throw new IllegalArgumentException("Value "+ val + " is illegal.");
        }
 } 
/**
method to capture screen shot
@param String uploadPath to save screen shot as image
@returns boolean true if capture successful else false
*/

    private static IMAGE_TYPE  DEFAULT_IMAGE_TYPE = IMAGE_TYPE.PNG;
    
    
    public static void setDefaultType(String newdef){
       
        IMAGE_TYPE type = IMAGE_TYPE.getImageTypeByValue(newdef);
        setDEFAULT_IMAGE_TYPE(type);
    }
    
   /**************************************************************************?
     * 
     * @param destimage
     * @param type
     * @return 
     */
    public static File captureScreenShot(File destimage , IMAGE_TYPE type) {
    
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return captureScreenShot (screenRect, type, destimage);
    }
     public static File captureComponentWindowScreenShot( Component comp,  File destimage) 
     {
        return captureComponentWindowScreenShot (comp, getDEFAULT_IMAGE_TYPE(),destimage);
     }
     public static File captureComponentWindowScreenShot( Component comp,IMAGE_TYPE type , File destimage) 
     {
         
        Point p                 = comp.getLocationOnScreen();
        Dimension dim           = comp.getSize();  
        Rectangle bounds  = new Rectangle(p, dim);
        return captureScreenShot ( bounds,type ,destimage);
     }
    public static File captureRootWindowScreenShot(File destimage) {
    
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return captureScreenShot (screenRect, getDEFAULT_IMAGE_TYPE(), destimage);
    }
   
    public static File captureRootWindowScreenShot( Component comp,  File destimage) 
     {
        Component frame = SwingUtilities.getRoot(comp);
        Rectangle bounds = frame.getBounds();
        return captureScreenShot (bounds, getDEFAULT_IMAGE_TYPE(),destimage);
     } 
    public static File captureScreenShot( Component comp, IMAGE_TYPE type, File destimage) 
     {
        Component frame = SwingUtilities.getRoot(comp);
        Rectangle bounds = frame.getBounds();
        return captureScreenShot (bounds, type, destimage);
     } 
    public static File captureScreenShot(Rectangle bounds,IMAGE_TYPE type,  File destimage) 
    {
        File out               =    null;
        BufferedImage capture;
        try {
            capture = new Robot().createScreenCapture(bounds);
            
            String filename     =   destimage.getName();
            String dir          =   destimage.getParent();
            String imgType      =   type.getType();
            String ext          =   "."+imgType;
            if (filename.endsWith(ext) == false){
                filename        =   filename +ext;
                if (dir == null){dir="";}
                destimage       =   new File (dir, filename);
                
            }
            
            //Make sure file exists
            if (destimage.exists() == false){
                destimage.createNewFile();
            }
            
            // screen shot image will be save at given path with name "screen.jpeg"
            ImageIO.write(capture, type.getType(), destimage); 
            out     = destimage;
        } catch (AWTException awte) {
            awte.printStackTrace();
            out     =   null;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
           out     =   null;
        }
       
        return out;
     } 
    
    
     public static  Screenshot captureScreenShotAndDisplay(
                            Component comp,
                            String filename) 
    {
        
        
        String dir              =   System.getProperty("user.home");
        Date date               =   new Date();
        String name             =   filename+"."+sdf.format(date);  
        File file               =   new File (dir,name); 
        Point p                 =   comp.getLocationOnScreen();
        Dimension dim           =   comp.getSize();  
        Rectangle bounds        =   new Rectangle(p, dim);
        return captureScreenShotFlex (bounds,DEFAULT_IMAGE_TYPE , file);
     } 
    
    public static  Screenshot captureScreenShotFlex(
                         Rectangle bounds,
                         IMAGE_TYPE type,  
                         File destfile) 
    {
        Screenshot shot             =   new  Screenshot();
         destfile                   =    captureScreenShot(bounds, type,destfile);
        if (destfile!= null && destfile.exists()){
            shot.setScreenshotFile(destfile);
            shot.setSuccess(true);
            try{
                Desktop desktop = Desktop.getDesktop();
                desktop.open(destfile);
            }
            catch (Exception e){
                e.printStackTrace();
                  String msg         =   String.format("Screen shot was saved to \n %s ", destfile.getPath());
                  DisplayText.popupMessage(msg);
            }
        }
        else {
            shot.setScreenshotFile(null);
            shot.setSuccess(false);
            String err = String.format("Failed to take a screenshot");
            DisplayText.popupMessage(err);
        }
       
       
        return shot;
     } 
    
    
    public static void main(String [] ars){
        File file = new File("/Users/apple");
        try{
             if (file.exists() == false){file.createNewFile();}
        }
        catch(Exception e){e.printStackTrace();}
       
        captureRootWindowScreenShot(file);
    }
//   
}
