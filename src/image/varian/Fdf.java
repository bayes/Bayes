/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Scanner;
/**
 *
 * @author apple
 */
public class Fdf {
    public final static int  BYTES_PER_POINT    =   4;
    public final static int  EOL_LENGTH         =   1;
    public final static String END_OF_HEADER    =   "\u0000";
    public final static String  FILE_EXT        =   "fdf";
    public final static String  FILE_EXTENSION  =   "."+FILE_EXT ;
    public final static String  DIR_EXTENSION   =   ".img" ;
    public final static String  SPR             =   "=";

    public ByteOrder byteOrder                  =   ByteOrder.LITTLE_ENDIAN;
    public static String RoSizeKey              =   "ro_size";
    public static String PeSizeKey              =   "pe_size";
    public static String ArrayIndexKey          =   "array_index";
    public static String SliceNumberKey         =   "slice_no";
    public static String NSliciesKey            =   "slices";
    public static String NArrayedKey            =   "array_dim";
    public static String BytesPerPointKey       =   "bits";
    private int  readoutSize                    =   0;
    private int  phaseencodeSize                =   0;
    private int  sliceSize                      =   0;
    private int  elementSize                  =   0;
    private int  slice                          =   0;
    private int  element                        =   0;

    private  int  bytesPerPoint                 =  BYTES_PER_POINT;
    private boolean loaded                      =  false;
    private float[] [] pixels                   =  null;
    private File    sourseFile                  =  null;
    private String  errorMessage                =  null;

    public void  read(File file){

         FileInputStream     fin        =   null;
         FileChannel channel            =   null;
         readHeader(file);
         try {
             //boolean isParsedCorrectly = isReadCorrectly();
            // if ( isParsedCorrectly == false ){   return;  }
             fin                            =   new FileInputStream(file);
             channel                        =   fin.getChannel();

             int npe                        =   getPhaseencodeSize();
             int nro                        =   getReadoutSize();
             int sizeInBytess               =   fin.available();
             int dataSizeInBytes            =   npe*nro * getBytesPerPoint();
             int headerSize                 =   sizeInBytess  - dataSizeInBytes;

             ByteBuffer buffer              =   ByteBuffer.allocateDirect(dataSizeInBytes );  // BUFSIZE = 256
             buffer.order( byteOrder);
  
            channel.position(headerSize );
            channel.read(buffer);
            buffer.rewind();



            float [][]   pix                =   new float[npe ][nro] ;
            for (int curRo = 0; curRo < nro; curRo++) {
                for (int curPe = 0; curPe < npe ; curPe++) {
                         float v     = buffer.getFloat();
                         pix [curPe][curRo] = v;
            }
               

            }
            setPixels(pix);
            setSourseFile(file);
            setLoaded(true);

        }catch (FileNotFoundException ex) {
          ex.printStackTrace();
        }
        catch (IOException ex) {
           ex.printStackTrace();
        }
        finally{
            try {
                if (fin     != null) { fin.close();}
                if (channel != null) { channel.close();}
            } catch (IOException ex) { }

        }

    }
    private void  readHeader(File file){
         Scanner scanner                =   null;
         try {
            scanner     = new Scanner(new BufferedReader(new FileReader(file)));

            while(scanner.hasNextLine() ){
                String line     =   scanner.nextLine();
                if  (line.contains(END_OF_HEADER)){break;}

                else if( line.contains( RoSizeKey  )){
                    String val = getValue(line);
                    int ro    =   Integer.parseInt(val);
                    setReadoutSize(ro);
                }
                else if( line.contains( PeSizeKey  )){
                    String val = getValue(line);
                    int pe    =   Integer.parseInt(val);
                    setPhaseencodeSize(pe);
                }
                else if( line.contains( BytesPerPointKey )){
                    String val = getValue(line);
                    int v    =   Integer.parseInt(val);
                    setBytesPerPoint(v / 8);
                }
                else if( line.contains( SliceNumberKey )){
                    String val = getValue(line);
                    int v    =   Integer.parseInt(val);
                    setSlice(v-1);
                }
                else if( line.contains( ArrayIndexKey )){
                    String val = getValue(line);
                    int v    =   Integer.parseInt(val);
                    setElement(v-1);
                }
                else if( line.contains( NArrayedKey  )){
                    String val = getValue(line);
                    int v    =  (int)Double.parseDouble(val);
                    setElementSize(v);
                }
                else if( line.contains( NSliciesKey )){
                    String val = getValue(line);
                    int v    =   Integer.parseInt(val);
                    setSliceSize(v);
                }


            }
            scanner.close();




        }catch (FileNotFoundException ex) {
          ex.printStackTrace();
        }
        catch (IOException ex) {
           ex.printStackTrace();
        }
        finally{
                scanner.close();

        }

    }

  private String getValue(String line){
       String [] array  = line.split(SPR);
       if (array.length == 2){ 
           String str   =   array [1].trim();
           str          =   str.replaceAll(";", "");
           return str ;
       }
       else {return null;}
   }
   public boolean isReadCorrectly(){
        setErrorMessage(null);
        return true;
    }


    public static void main(String [] args){
            File file = new File("/Users/apple/Desktop/real.0001.0002.fdf");

        Fdf fdf =  new   Fdf();
       // System.out.println(IO.readFileToString(file));
       fdf.read(file);
     //   char c = '\n';

    //   System.out.println(c+"a");


        double h = 0x0;
      //  System.out.println(h);
       // System.out.println(Double.toHexString(0.1));
        // System.out.println(rh.SeriesTime);
        // System.out.println(rh.DwellTime);
       //System.out.println(rh.MRFrequency);
     // System.out.println(rh.VectorSize);


    }

    public File     getSourseFile() {
        return sourseFile;
    }
    public void     setSourseFile(File sourseFile) {
        this.sourseFile = sourseFile;
    }

    public int      getReadoutSize() {
        return readoutSize;
    }
    public void     setReadoutSize(int readoutSize) {
        this.readoutSize = readoutSize;
    }

    public int      getPhaseencodeSize() {
        return phaseencodeSize;
    }
    public void     setPhaseencodeSize(int phaseencodeSize) {
        this.phaseencodeSize = phaseencodeSize;
    }

    public String   getErrorMessage() {
        return errorMessage;
    }
    public void     setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean  isLoaded() {
        return loaded;
    }
    public void     setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public int      getBytesPerPoint() {
        return bytesPerPoint;
    }
    public void     setBytesPerPoint(int bytesPerPoint) {
        this.bytesPerPoint = bytesPerPoint;
    }

    public float[][] getPixels() {
        return pixels;
    }
    public void     setPixels(float[][] pixels) {
        this.pixels = pixels;
    }

    public int      getSliceSize() {
        return sliceSize;
    }
    public void     setSliceSize(int sliceSize) {
        this.sliceSize = sliceSize;
    }

    public int      getElementSize() {
        return elementSize;
    }
    public void     setElementSize(int elemenentSize) {
        this.elementSize = elemenentSize;
    }

    public int      getSlice() {
        return slice;
    }
    public void     setSlice(int slice) {
        this.slice = slice;
    }

    public int      getElement() {
        return element;
    }
    public void     setElement(int elemenent) {
        this.element = elemenent;
    }


}
