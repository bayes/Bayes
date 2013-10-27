/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.siemens;

import fid.Procpar;
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
public class Raw {
    public final static int  BYTES_PER_POINT        =   4;
    public final static int  HEADER_LENGTH          =   6144;
    public final static int  NUMBER_COMPLEX_POINTS  =   1024;
    public final static int  NUMBER_OF_TRACES       =   1;
    public final static String  FILE_EXT            =   "raw";
    public final static ByteOrder byteOrder         =   ByteOrder.BIG_ENDIAN;
    public final static float   SPEC_FREQ           =   63.613126f;
    public final static float  AT                   =   0.512f;
    public final static float Dwell_Time            =   (float) (2*AT/16384);


    private boolean loaded                          =  false;
    private float [] kSpaceReal                     =  null;
    private float [] kSpaceImag                     =  null;
    private File    sourseFile                      =  null;
    private String  errorMessage                    =  null;
    private Procpar procpar                         =  new Procpar();


    public void  read(File file){
         FileInputStream     fin                    =   null;
         FileChannel channel                        =   null;
         setSourseFile(file);

         try {
             fin                                    =   new FileInputStream(file);
             channel                                =   fin.getChannel();
             

             int sizeInBytes                        =   fin.available();
             int dataSizeInBytes                    =   2* NUMBER_COMPLEX_POINTS* BYTES_PER_POINT ;
             int calculatedHeaderSize               =   sizeInBytes  - dataSizeInBytes;

             channel.position(HEADER_LENGTH  );
             ByteBuffer buffer              =   ByteBuffer.allocateDirect(dataSizeInBytes );  // BUFSIZE = 256
             buffer.order( byteOrder);

             //*
            System.out.println("Reading Siemens RAW file");
            System.out.println("File Size In Bytes          =    "   +   sizeInBytes      );
            System.out.println("Binary Data Size In Bytes  =    "   +   dataSizeInBytes );
            System.out.println("Header Lenght estimated    =    "   +   calculatedHeaderSize);
           

           
            channel.read(buffer);
            buffer.rewind();

            setkSpaceReal(new float[  NUMBER_COMPLEX_POINTS ]);
            setkSpaceImag(new float[  NUMBER_COMPLEX_POINTS ]);
            for (int i = 0; i < NUMBER_COMPLEX_POINTS; i++) {
                float re        = buffer.getFloat();
                float im        = buffer.getFloat();
                getkSpaceReal() [i]   = re;
                getkSpaceImag() [i]   = im;
            }
            updateProcpar();
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
    public void updateProcpar(){
        int np              =    2 * NUMBER_COMPLEX_POINTS;
        float at            =   (float) (0.512);
        float sfrq          =   SPEC_FREQ;

        getProcpar().setAxis('h');
        getProcpar().setArrayValues(null);
        getProcpar().getArray().clear();
        getProcpar().setArraydim( NUMBER_OF_TRACES );
        getProcpar().updateProcpar(at, sfrq, np);
        getProcpar().setFileSource( getSourseFile().getAbsolutePath());

    }
    public float[][] getkSpaceRealAsFloat() {
        float[]   in           =    getkSpaceReal();
        int        size         =   in.length;
        float[][]  out          =   new float[1][ size];
        for (int i = 0; i < size ; i++) {
           out[0][i]            =   in[i];
        }

        return out;
    }
    public float[][]  getkSpaceImagAsFloat() {
        float[]   in           =   getkSpaceImag();
        int        size         =   in.length;
        float[][]  out          =   new float[1][ size];
        for (int i = 0; i < size ; i++) {
           out[0][i]            =   in[i];
        }

        return out;
    }
       public static void main(String args[]){
        File file = new File("/Users/apple/Desktop/d117.raw");

        Raw rh =  new  Raw();
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

    public float[] getkSpaceReal() {
        return kSpaceReal;
    }
    public void setkSpaceReal(float[] kSpaceReal) {
        this.kSpaceReal = kSpaceReal;
    }

    public float [] getkSpaceImag() {
        return kSpaceImag;
    }
    public void setkSpaceImag(float[] kSpaceImag) {
        this.kSpaceImag = kSpaceImag;
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

    public Procpar getProcpar() {
        return procpar;
    }

    public void setProcpar(Procpar procpar) {
        this.procpar = procpar;
    }
}
