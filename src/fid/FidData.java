/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import static java.lang.Math.*;
/**
 *
 * @author larry
 */
public class FidData {
    public float [] real;
    public float [] imag;
    boolean print = false;



   public FidData(){
   }

    public FidData(int length){
        this.real = new float[length];
        this.imag = new float[length];

    }

    public int size(){
        if (real == null || imag == null) {return -1;}
        if (real.length != imag.length) {return -1;}
        return real.length;
    }

    public static FidData readData(FileChannel channel,
        FidFileHeader fileHeader, FidBlockHeader blockHeader ) throws IOException{

        FidData fidData         =   new FidData();
        int np                  =   fileHeader.np;
        int scale               =   (int)pow(2, blockHeader.scale);
        float levelCorrection   =   blockHeader.lvl;
        float tiltCorrection    =   blockHeader.tlt;
        fidData.real            =   new float [ np/2];
        fidData.imag            =   new float [ np/2];

        int length              =  fileHeader.tbytes;
        ByteBuffer buffer       =  ByteBuffer.allocateDirect( length);

        channel.read(buffer);
        buffer.rewind();



        if( fileHeader.s_float == 1)
        {
            if (fidData.print)  { System.out.println("FID values are 32-bit floats (\"float\" type )");}
            for (int i = 0; i < np/2; i++) {
                    float re        = buffer.getFloat();
                    float im        = buffer.getFloat();
                    fidData.real[i] = re*scale    -   levelCorrection ;
                    fidData.imag[i] = im*scale    -   tiltCorrection;

            }
        }
        else if (fileHeader.s_32 == 1)
        {
                if(fidData.print)   { System.out.println("FID values are 32-bit integers (\"int\" type)");}
                for (int i = 0; i < np/2; i++) {

                    float re        =  buffer.getInt();
                    float im        =  buffer.getInt();

            //        System.out.println(re+ "  "+ im);

                    fidData.real[i] = re*scale  -   levelCorrection;
                    fidData.imag[i] = im*scale  -   tiltCorrection;
                }
        }
        else
        {
                if (fidData.print)   { System.out.println("FID values are 16 -bit integers (\"short\" type)");}
                for (int i = 0; i < np/2; i++) {

                fidData.real[i] = buffer.getShort()*scale    -   levelCorrection;
                fidData.imag[i] = buffer.getShort()*scale    -   tiltCorrection;
          }
        }

      return fidData;
    }
}
