/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.siemens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 *
 * @author apple
 */
public class Ima {
    public final static int  BYTES_PER_POINT        =   2;
    public final static int  HEADER_LENGTH          =   6144;
    public final static int  NUMBER_OF_IMAGES       =   1;
    public final static String  FILE_EXT            =   "ima";
    public final static ByteOrder byteOrder         =   ByteOrder.BIG_ENDIAN;


    private boolean loaded                          =  false;
    private String  errorMessage                    =  null;
    private int     imgSize                         =  64;
    private float   [][] pixels                     =  null;
    private File    sourseFile                      =  null;

    public void  read(File file){
         FileInputStream     fin                    =   null;
         FileChannel channel                        =   null;
         setSourseFile(file);

         try {
             fin                                    =   new FileInputStream(file);
             channel                                =   fin.getChannel();


             int sizeInBytes                        =   fin.available();
             int calculatedBinaryDataSize           =   sizeInBytes  - HEADER_LENGTH ;
             int numberOfPixels                     =   calculatedBinaryDataSize/ BYTES_PER_POINT;
             int width                              =   (int) Math.sqrt(numberOfPixels  );
             setImgSize(width) ;

             channel.position(HEADER_LENGTH  );
             ByteBuffer buffer              =   ByteBuffer.allocateDirect(calculatedBinaryDataSize  );  // BUFSIZE = 256
             buffer.order( byteOrder);

             //*
            System.out.println("Reading Siemens IMA image");
            System.out.println("File Size In Bytes          =    "   +   sizeInBytes      );
            System.out.println("Binary Data Size In Bytes   =    "   +    calculatedBinaryDataSize  );
            System.out.println("Square Image Size           =    "   +    getImgSize()    );


            channel.read(buffer);
            buffer.rewind();

            float   [][] pix                       =   new float [width ][width ];
            for (int i = 0; i <width; i++) {
                 for (int j = 0; j < width; j++) {
                    float val      =   buffer.getShort();
                    pix[i][j]       =   val; 
                }
             }


            setPixels(pix);
            setLoaded(true);


        }catch (FileNotFoundException ex) {
          ex.printStackTrace();
        }
        catch (IOException ex) {
           ex.printStackTrace();
        }
        finally{
            try {
                fin.close();
                channel.close();
            } catch (IOException ex) { }

        }

    }
    public static void main(String args[]){
        File file = new File("/Users/apple/Desktop/temp/4197-5-723.ima");

        Ima rh =  new  Ima();
       // System.out.println(IO.readFileToString(file));
       rh.read(file);
        // System.out.println(rh.SeriesTime);
        // System.out.println(rh.DwellTime);
       //System.out.println(rh.MRFrequency);
     // System.out.println(rh.VectorSize);

    }

    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public File getSourseFile() {
        return sourseFile;
    }
    public void setSourseFile(File sourseFile) {
        this.sourseFile = sourseFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getImgSize() {
        return imgSize;
    }
    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
    }

    public float[][] getPixels() {
        return pixels;
    }
    public void setPixels(float[][] pixels) {
        this.pixels = pixels;
    }


}
