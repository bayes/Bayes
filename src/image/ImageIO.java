/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import bayes.BayesManager;
import utilities.IO;
import static image.ImageDescriptor.*;

public class ImageIO {
    private double maxValue             =   Float.MIN_VALUE;
    private double minValue             =   Float.MAX_VALUE;
    private List<float[][]> imgArray    =   null;

    private ImageIO(){}
    
    
    /* read and write binary files */
    public static void writeBinaryFile( List< float[][] > img, ImageDescriptor   id , File dist)
            throws FileNotFoundException, IOException{
         int numImages              =   img.size();
         int row                    =   id.getNumberOfRows();
         int col                    =   id.getNumberOfColumns();


         int bytesPerPoint          =   id.getBytesPerPoint();
         int filesize               =   row*col* numImages*bytesPerPoint;

         FileOutputStream fos       =   new FileOutputStream(dist);
         FileChannel channel        =   fos.getChannel();
         ByteBuffer buffer          =   null;



         buffer                     =   ByteBuffer.allocateDirect(filesize );
         buffer.order(ByteOrder.BIG_ENDIAN);


        for (float[][] im : img) {

            for (int i = 0; i < row; i++) {
                for (int j = 0; j <col; j++) {
                     float f    = im[j][i];
                     if (bytesPerPoint  == 2){
                         buffer.putShort((short)f);
                    }
                    else if (bytesPerPoint  == 4){
                         buffer.putFloat(f);
                    }
                    else if (bytesPerPoint  == 8){
                         buffer.putDouble(f);
                    }

                }
            }
        }
        buffer.rewind();
        channel.write(buffer);

        channel.close();
        fos.close();
      
    }


     public static void writeBinaryFile( float[][] img, ImageDescriptor   id ,  FileChannel channel)
            throws  IOException{
            writeBinaryFile (-1, img, id,channel);
    }

      public static void writeBinaryFile( int imageNumber, float[][] img, ImageDescriptor   id ,  FileChannel channel)
            throws  IOException{
         int row                    =   id.getNumberOfRows();
         int col                    =   id.getNumberOfColumns();
         int bytesPerPoint          =   id.getBytesPerPoint();
         int datasize               =   row*col*bytesPerPoint;
         ByteBuffer buffer          =   ByteBuffer.allocateDirect(datasize  );

         buffer.order(ByteOrder.BIG_ENDIAN);


        for (int curRow = 0; curRow < row; curRow++) {
            for (int curCol = 0; curCol <col; curCol++) {
                    float f    = img[curCol][curRow];
                    if (bytesPerPoint  == 2){
                         buffer.putShort((short)f);
                    }
                    else if (bytesPerPoint  == 4){
                         buffer.putFloat(f);
                    }
                    else if (bytesPerPoint  == 8){
                         buffer.putDouble(f);
                    }

           }
        }
        buffer.rewind();
        long position       =    imageNumber * datasize;

        if (imageNumber < 0) { channel.write(buffer);}
        else            { channel.write(buffer, position); }
           

    }



    /*Readout is assumed to be contiguous memmory*/
   
    
    public static  ImageIO readImage( File srcImage,ImageDescriptor  id ) throws FileNotFoundException{
         int row                    =   id.getNumberOfRows();
         int col                    =   id.getNumberOfColumns();
         int elm                    =   id.getNumberOfElements();
         int slc                    =   id.getNumberOfSlices();
        
         int bytesPerPoint          =   id.getBytesPerPoint();
         int filesize               =   row*col*elm*slc*bytesPerPoint;
         int numImages              =   elm*slc;
         int position               =   0;

         double maxValue            =   Float.MIN_VALUE;
         double minValue            =   Float.MAX_VALUE;

         boolean isBigEndian        =   id.isBigEndian();

         List< float[][] > img      =   new ArrayList< float[][] > ();
         
         int length                 =   row*col*bytesPerPoint;
         FileInputStream stream     =   new FileInputStream(srcImage);
         FileChannel channel        =   stream.getChannel();
         ByteBuffer buffer          =   null;
            
         buffer                     =  ByteBuffer.allocateDirect(length);
         if(isBigEndian ){ buffer.order(ByteOrder.BIG_ENDIAN);  }
         else{ buffer.order(ByteOrder.LITTLE_ENDIAN); }
            
         try { 
           

            if(bytesPerPoint == 4){

             for (int curImage = 0; curImage <numImages ; curImage++) {
                buffer.rewind();
                channel.read(buffer);
                buffer.rewind();

                float[][] image = new float[col][row] ;

                for (int curRow = 0; curRow < row; curRow++) {
                    for (int curCol = 0; curCol <col; curCol++) {
                        float value             =   buffer.getFloat();
                        image [curCol][curRow]  =  value;
                        if(value  > maxValue){maxValue = value;}
                        if(value  < minValue){minValue = value;}
                    }
                }
                img.add(image);
                
            }
           }
            else if(bytesPerPoint == 8){
             for (int curImage = 0; curImage <numImages ; curImage++) {
                buffer.rewind();
                channel.read(buffer);
                buffer.rewind();
                float[][] image = new float[col][row] ;

                for (int curRow = 0; curRow < row; curRow++) {
                    for (int curCol = 0; curCol <col; curCol++) {
                        float value             =  (float) buffer.getDouble();
                        image [curCol][curRow]  =  value;
                        if(value  > maxValue){maxValue = value;}
                        if(value  < minValue){minValue = value;}
                    }
                }

                img.add(image);
            }
           }
           else if(bytesPerPoint == 2){
             for (int curImage = 0; curImage <numImages ; curImage++) {
                buffer.rewind();
                channel.read(buffer);
                buffer.rewind();
                float[][] image = new float[col][row] ;

                for (int curRow = 0; curRow < row; curRow++) {
                    for (int curCol = 0; curCol <col; curCol++) {
                        float value             =  (float) buffer.getShort();
                        image [curCol][curRow]  =  value;
                        if(value  > maxValue){maxValue = value;}
                        if(value  < minValue){minValue = value;}
                    }
                }

                img.add(image);
            }
           }

           
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        finally{
            try {
                 stream.close();
                 channel.close();
                  
            }catch (IOException ioe) { }
        }
        ImageIO  imageInfo      =   new  ImageIO();
        imageInfo. maxValue     =   maxValue ;
        imageInfo. minValue     =   minValue ;
        imageInfo.imgArray      =   img;
        return  imageInfo;
    }
    
    public static void main (String [] args){
        try {
            File src = new File("/Users/apple/Bayes/exp2/images/LoadedImage_Real.4dfp.img");
            File ifh = new File("/Users/apple/Bayes/exp2/images/LoadedImage_Real.4dfp.ifh");


            //src = new File("/Users/apple/BayesSys/Bayes.test.data/images/paskal/dataMStissue.img");
          // ifh = new File("/Users/apple/BayesSys/Bayes.test.data/images/paskal/dataMStissue.ifh");

            ImageDescriptor id = loadFromDisk(ifh);

            long t1 = System.nanoTime();
             //  readImage(src, id);

              readImage(src, id);
            long t2 = System.nanoTime();
            double time = (t2-t1)*1E-9;
            System.out.println("time "+ time);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* read and write Ascii ImageDescriptor */
    public  static ImageDescriptor  loadFromDisk(File file) {
        String content          =   IO.readFileToString(file);
     return load(content);
}
    public static ImageDescriptor load (String content){
      ImageDescriptor id        =   new ImageDescriptor();
      id.setLoaded(false);
      Scanner scanner           =   null;

      try{
        scanner           =   new Scanner(content);
        while(scanner.hasNextLine() == true ){
            String line             =  scanner.nextLine();
            String []   keyAndValue = readKeyAndValue (line);
            if (keyAndValue  == null) {continue;}

            String key                  =   keyAndValue[0];
            String val                  =   keyAndValue[1];

            if (key.contains( KEYVERSION_OF_KEYS) && val != null){
                id.setKeyVersion(Double.valueOf(val));
            }
            else if (key.contains(KEYCONVERSION_PROGRAM) && val != null){
               id.setConversionProg(val );
            }
            else if (key.contains(KEYPROGRAM_VERSION) && val != null){
               id.setProgVersion(val);
            }
            else if (key.contains(KEYNAME_OF_DATA_FILE  )&& val != null){
               id.setDataFileName(val);
            }
            else if (key.contains(KEY_SOURCE_FILE )&& val != null){
               id.setSourceFileName(val);
            }
            else if (key.contains(KEYPATIENT_ID )&& val != null){
               id.setPatientID(val);
            }
            else if (key.contains(KEYPATIENT_ID )&& val != null){
               id.setPatientID(val);
            }
            else if (key.contains(KEYDATE )&& val != null){
               id.setDATE(val);
            }
            else if (key.contains(KEYNUMBER_FORMAT)&& val != null){
                id.setDataFormat(val);
            }
            else if (key.contains(KEYBYTES_PER_PIXEL)&& val != null){
                 id.setBytesPerPoint(Integer.valueOf(val));
            }
            else if (key.contains(KEYNumberOfDimension)&& val != null){
                 id.setNumberOfDimensions(Integer.valueOf(val));
            }
            else if (key.contains(KEYORIENTATION)&& val != null){
                 id.setOrientation(Integer.valueOf(val));
            }
            else if (key.contains(KEYNumberOfColumns)&& val != null){
                  id.setNumberOfColumns(Integer.valueOf(val));
            }
            else if (key.contains(KEYNumberOfRows)&& val != null){
                  id.setNumberOfRows(Integer.valueOf(val));
            }
            else if (key.contains(KEYNumberOfSlices)&& val != null){
                   id.setNumberOfSlices(Integer.valueOf(val));
            }
            else if (key.contains(KEYNumberOfElements)&& val != null){
                    id.setNumberOfElements(Integer.valueOf(val));
            }
            else if (key.contains(KEYSCALING_FACTOR1)&& val != null){
                    id.setScaling1(Double.valueOf(val));
            }
            else if (key.contains(KEYSCALING_FACTOR2)&& val != null){
                    id.setScaling2(Double.valueOf(val));
            }
            else if (key.contains(KEYSCALING_FACTOR3)&& val != null){
                    id.setScaling3(Double.valueOf(val));
            }
            else if (key.contains(KEY_X_LABEL)&& val != null){
                    id.setxLabel(val);
            }
            else if (key.contains(KEY_Y_LABEL)&& val != null){
                id.setyLabel(val);
            }
            else if (line.contains(LITTLEENDIAN)){
                id.setBigEndian(false);
            }


          }

           scanner.close();
           id.setLoaded(true);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      finally{
            return id;
      }



     
      
  }
    public static String []readKeyAndValue (String line){
      String spr                    =   ":=";
      line                          =    line.trim();
      String [] strArray            =   line.split (spr);
      if (strArray.length < 2) {return null;}
      else {return  new String []{  strArray[0].trim(),strArray[1].trim()}; }

  }



    public  static boolean storeToDisk(ImageDescriptor id, File dist){
        BufferedWriter out         =   null;
        String  EOL                =   BayesManager.EOL;
        String  sp                 =   ":=";
        try{
       
            FileWriter fr      =    new FileWriter( dist);
            out                =    new BufferedWriter(fr);
    
            // line 1
            out.write (IO.pad(INTERFILE, PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ "");
            out.write(EOL);
            
            // line 2
            out.write (IO.pad(  KEYVERSION_OF_KEYS, PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getKeyVersion());
            out.write(EOL);
            
            // line 3
            out.write (IO.pad( KEYCONVERSION_PROGRAM, PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getConversionProg());
            out.write(EOL);
            
            // line 4
            out.write (IO.pad( KEYPROGRAM_VERSION, PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getProgVersion());
            out.write(EOL);
            
            
            // line 5
            out.write (IO.pad( KEYNAME_OF_DATA_FILE , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ dist.getAbsolutePath());
            out.write(EOL);
            
            // line 6
            out.write (IO.pad( KEY_SOURCE_FILE , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getSourceFileName());
            out.write(EOL);
            
            
            // line 7
            out.write (IO.pad( KEYPATIENT_ID  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getPatientID());
            out.write(EOL);
            
            
            // line 8
            out.write (IO.pad( KEYDATE  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDATE());
            out.write(EOL);
            
            // line 9
            out.write (IO.pad( KEYNUMBER_FORMAT  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDataFormat());
            out.write(EOL);
            
            
            // line 10
            out.write (IO.pad( KEYBYTES_PER_PIXEL  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getBytesPerPoint());
            out.write(EOL);
            
            
            // line 11
            out.write (IO.pad( KEYORIENTATION  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getOrientation());
            out.write(EOL);
            

            // line 12
            out.write (IO.pad( KEYNumberOfDimension , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getNumberOfDimensions());
            out.write(EOL);

            // line 12
            out.write (IO.pad(  KEYNumberOfColumns  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getNumberOfColumns());
            out.write(EOL);
             
            // line 13
            out.write (IO.pad(  KEYNumberOfRows  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getNumberOfRows());
            out.write(EOL);
            
            // line 14
            out.write (IO.pad(  KEYNumberOfSlices   , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getNumberOfSlices());
            out.write(EOL);
            
            
            // line 15
            out.write (IO.pad( KEYNumberOfElements , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+id.getNumberOfElements());
            out.write(EOL);
            
            
            // line 16
            out.write (IO.pad(  KEYSCALING_FACTOR1   , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getScaling1());
            out.write(EOL);
            
            
            // line 17
            out.write (IO.pad( KEYSCALING_FACTOR2 , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getScaling2());
            out.write(EOL);
            
            
            // line 18
            out.write (IO.pad( KEYSCALING_FACTOR3 , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getScaling3());
            out.write(EOL);

             // line 18
            out.write (IO.pad( KEYSLICE_THICKNESS , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getScaling3());
            out.write(EOL);

            // line 19
            out.write (IO.pad( KEYBYTEORDER , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getByteOrder());
            out.write(EOL);

            // line 21
            String xLabel  = id.getxLabel();
            if (xLabel != null){
                out.write (IO.pad( KEY_X_LABEL , PAD_LEN, PAD_CHAR));
                out.write (sp);
                out.write(" "+ xLabel );
                out.write(EOL);

            }


            // line 22
            String yLabel  = id.getyLabel();
            if (yLabel != null){
            out.write (IO.pad( KEY_Y_LABEL , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  yLabel);
            out.write(EOL);
            }
            
     
            out.close();
            fr.close ();; 
         } catch (IOException ex) {
            ex.printStackTrace();
            return false;
            
        }
      return true;  
    }
 
    public static String readValueForKey (String content, String key){
      String [] strArray    =   null; // data for abscissa
      String value          =   null; // data for abscissa
      String line           =   "";
      String spr            =   ":=";
      Scanner scanner       =   new Scanner(content); 
      boolean matchFound    =   false;
     
      while(scanner.hasNextLine() == true ){
        line            =    scanner.nextLine();
        matchFound      =   line.contains(key);
        
        if (matchFound ){
               strArray                 =   line.split (spr);
               if (strArray.length < 2) {return null;}
               else {  value            =   strArray[1].trim(); }
               break;
        }
      }
     
      scanner.close(); 
      return value ;
  }





   public static boolean convetLittleToBigEndian(  File srcImage,
                                                    File dstImage,
                                                    ImageDescriptor  id,
                                                    File dstIfh) throws FileNotFoundException, IOException{
        ImageIO imageIO =  readImage( srcImage,id );

        writeBinaryFile( imageIO.getImgArray(), id, dstImage);
        id.setBigEndian(true);
        storeToDisk( id, dstIfh);


      return true ;
  }


    public double getMaxValue () {
        return maxValue;
    }
    public double getMinValue () {
        return minValue;
    }
    public List<float[][]> getImgArray () {
        return imgArray;
    }





    public static BufferedImage toBufferedImage(Image image) {
        if (image == null){return null;}
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha == true) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image
                    .getHeight(null), transparency);
        } catch (HeadlessException e) {
        } // No screen

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha == true) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image
                    .getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            return ((BufferedImage) image).getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        return pg.getColorModel().hasAlpha();
    }
    public static BufferedImage url2BufferedImage(URL url) {
       BufferedImage bimage = null;
       try {
           Image image  = Toolkit.getDefaultToolkit().createImage(url);
           bimage       =   toBufferedImage(image);
       } catch (Exception e) {
            e.printStackTrace();
        }

        return bimage;
    }
    public static Image url2Image(URL url) {
      Image image = null;
       try {
           image  = Toolkit.getDefaultToolkit().createImage(url);
       } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

}
