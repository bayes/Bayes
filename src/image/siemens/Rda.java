/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.siemens;

import fid.Procpar;
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
public class Rda {
    public final static int  BYTES_PER_POINT    =   8;
    public final static int  EOL_LENGTH         =   2;
    public final static ByteOrder byteOrder     =   ByteOrder.LITTLE_ENDIAN;
    public final static String  FILE_EXT        =   "rda";

    public static String SeriesTimeKey          =   "SeriesTime:";
    public static String DwellTimeKey           =   "DwellTime:";
    public static String MRFrequencyKey         =   "MRFrequency:";
    public static String VectorSizeKey          =   "VectorSize:";
    public static String HEADERSTARTKey         =   ">>> Begin of header <<<";
    public static String HEADERENDKey           =   ">>> End of header <<<";
    private double SeriesTime                   =   0.0;
    private double  DwellTime                   =   0.0;
    private double MRFrequency                  =   0.0;
    private int  VectorSize                     =   0;
    private int  headerSize                     =   0;
    private int  numberOfTraces                 =   0;
    private boolean loaded                      =  false;
    private float [][] kSpaceReal               =  null;
    private float [][] kSpaceImag               =  null;
    private Procpar procpar                     =  new Procpar();
    private File    sourseFile                  =  null;
    private String  errorMessage                =  null;



    public void  read(File file){
         FileInputStream     fin        =   null;
         FileChannel channel            =   null;
         Scanner scanner                =   null;
         Scanner linescanner            =   null;

         int headerBites                =   0;
         setSourseFile(file);

         try {
            scanner     = new Scanner(new BufferedReader(new FileReader(file)));
            while(scanner.hasNextLine()){
                String line     =   scanner.nextLine();
                System.out.println(line);

                int    addBytes             =   line.length()   + EOL_LENGTH ;
                headerBites                 =   headerBites     +   addBytes ;
                setHeaderSize( headerBites);

                if (line.contains(HEADERENDKey   )){
                    break;
                }
                linescanner     =   new Scanner(line);
                String  token   =   linescanner.next();
               // System.out.println(token);
                if(token.equalsIgnoreCase(DwellTimeKey   )){
                    setDwellTime(linescanner.nextDouble());
                }
                else if(token.equalsIgnoreCase(MRFrequencyKey  )){
                    setMRFrequency(linescanner.nextDouble());
                }
                else if(token.equalsIgnoreCase(VectorSizeKey   )){
                    setVectorSize(linescanner.nextInt());
                }
                else if(token.equalsIgnoreCase(SeriesTimeKey   )){
                    setSeriesTime(linescanner.nextDouble());
                }


            }
            linescanner.close();
            scanner.close();


             boolean isParsedCorrectly = isReadCorrectly();
             if ( isParsedCorrectly == false ){   return;  }

             fin                            =   new FileInputStream(file);
             channel                        =   fin.getChannel();

             int pointsPerTrace             =   getVectorSize();
             int sizeInBytess               =   fin.available();
             int dataSizeInBytes            =   2*  pointsPerTrace * BYTES_PER_POINT ;
             int totalDataSize              =   sizeInBytess - headerBites;
             numberOfTraces                 =   totalDataSize/dataSizeInBytes;
             kSpaceReal                     =   new float [numberOfTraces][pointsPerTrace ] ;
             kSpaceImag                     =   new float [numberOfTraces][pointsPerTrace ] ;

             ByteBuffer buffer              =   ByteBuffer.allocateDirect(dataSizeInBytes );  // BUFSIZE = 256
             buffer.order( byteOrder);

            System.out.println("Reading RDA file");
            System.out.println("RDA file size in Bytes " + sizeInBytess);
            System.out.println("Header Size In Bytes  =    "   +  headerBites );
            System.out.println("Binary Data Size In Bytes  =    "   +  totalDataSize );
            System.out.println("Binary Data Size In Bytes  =    "   +   dataSizeInBytes );
            System.out.println("Reading Buffer  Size In Bytes  =    "   +  dataSizeInBytes );

            channel.position(headerBites );
            for (int curTrace = 0; curTrace <numberOfTraces ; curTrace++) {
              //  System.out.println("Reading trace # "+curTrace  + " starting at "+ channel.position() + " in the rda file");
                // need this in order to be able to read to buffer
                 buffer.rewind();
                 channel.read(buffer);


                 buffer.rewind();

                for (int i = 0; i < getVectorSize(); i++) {
                    float re                    = (float)buffer.getDouble();
                    float im                    = (float)buffer.getDouble();
                    kSpaceReal [curTrace][i]    = re;
                    kSpaceImag [curTrace][i]    = im;

                      //if (curTrace  == 1){System.out.println(i+ " "+ re + " "+im); }
                           
                      
                }



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
                linescanner.close();
                scanner.close();
                fin.close();
                channel.close();
            } catch (IOException ex) { }

        }

    }

    public void updateProcpar(){
        int np              =   2 * getVectorSize();
        float at            =   (float) (getVectorSize() * getDwellTime()/1000000);
        float sfrq          =   (float) getMRFrequency();

        getProcpar().setAxis('h');
        getProcpar().setArrayValues(null);
        getProcpar().getArray().clear();
        getProcpar().setArraydim( getNumberOfTraces());
        getProcpar().updateProcpar(at, sfrq, np);
        getProcpar().setFileSource( getSourseFile().getAbsolutePath());

    }
    public boolean isReadCorrectly(){
        setErrorMessage(null);
        if ( this.getDwellTime() <= 0){
            setErrorMessage(String.format("Failed to parse Dwell Time"));
            return false;
        }
        if ( this.getVectorSize() <= 0){
            setErrorMessage(String.format("Failed to parse Vector Size"));
            return false;
        }
        if ( this.getMRFrequency() <= 0){
            setErrorMessage(String.format("Failed to parse MR Frequency"));
            return false;
        }
        setErrorMessage(null);
        return true;
    }

    public int getNumberOfTraces() {
        return numberOfTraces;
    }
    public int getNumberOfTotaldataPoints() {
        return 2*this.getVectorSize();
    }


 

 

    public double getSeriesTime() {
        return SeriesTime;
    }
    public void setSeriesTime(double SeriesTime) {
        this.SeriesTime = SeriesTime;
    }

    public double getDwellTime() {
        return DwellTime;
    }
    public void setDwellTime(double DwellTime) {
        this.DwellTime = DwellTime;
    }

    public double getMRFrequency() {
        return MRFrequency;
    }
    public void setMRFrequency(double MRFrequency) {
        this.MRFrequency = MRFrequency;
    }

    public int getVectorSize() {
        return VectorSize;
    }
    public void setVectorSize(int VectorSize) {
        this.VectorSize = VectorSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }
    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public float [][] getkSpaceReal() {
        return kSpaceReal;
    }
    public void setkSpaceReal(float[][] kSpaceReal) {
        this.kSpaceReal = kSpaceReal;
    }

    public float[][] getkSpaceImag() {
        return kSpaceImag;
    }
    public void setkSpaceImag(float[][] kSpaceImag) {
        this.kSpaceImag = kSpaceImag;
    }

    public File getSourseFile() {
        return sourseFile;
    }
    public void setSourseFile(File sourseFile) {
        this.sourseFile = sourseFile;
    }

    public Procpar getProcpar() {
        return procpar;
    }
    public void setProcpar(Procpar procpar) {
        this.procpar = procpar;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public static void main (String [] args){
        File rda = new File("/Users/apple/Bayes/Bayes.test.data/p21221.rda");
        Rda r = new Rda();
         r.test(rda);
    }
    public void test(File file){
         FileInputStream     fin        =   null;
         FileChannel channel            =   null;
         Scanner scanner                =   null;
         Scanner linescanner            =   null;

         int headerBites                =   0;
         setSourseFile(file);

         try {
            scanner     = new Scanner(new BufferedReader(new FileReader(file)));
            while(scanner.hasNextLine()){
                String line     =   scanner.nextLine();
               // System.out.println(line);

                int    addBytes             =   line.length()   + EOL_LENGTH ;
                headerBites                 =   headerBites     +   addBytes ;
                setHeaderSize( headerBites);

                if (line.contains(HEADERENDKey   )){
                    break;
                }
                linescanner     =   new Scanner(line);
                String  token   =   linescanner.next();
               // System.out.println(token);
                if(token.equalsIgnoreCase(DwellTimeKey   )){
                    setDwellTime(linescanner.nextDouble());
                }
                else if(token.equalsIgnoreCase(MRFrequencyKey  )){
                    setMRFrequency(linescanner.nextDouble());
                }
                else if(token.equalsIgnoreCase(VectorSizeKey   )){
                    setVectorSize(linescanner.nextInt());
                }
                else if(token.equalsIgnoreCase(SeriesTimeKey   )){
                    setSeriesTime(linescanner.nextDouble());
                }


            }
            linescanner.close();
            scanner.close();


             boolean isParsedCorrectly = isReadCorrectly();
             if ( isParsedCorrectly == false ){   return;  }

             fin                            =   new FileInputStream(file);
             channel                        =   fin.getChannel();

             int pointsPerTrace             =   getVectorSize();
             int sizeInBytess               =   fin.available();
             int dataSizeInBytes            =   2*  pointsPerTrace * BYTES_PER_POINT ;
             int totalDataSize              =   sizeInBytess - headerBites;

            System.out.println("Reading RDA file");
            System.out.println("RDA file size in Bytes " + sizeInBytess);
            System.out.println("Header Size In Bytes  =    "   +  headerBites );
            System.out.println("Binary Data Size In Bytes  =    "   +  totalDataSize );
            System.out.println("Binary Data Size In Bytes  =    "   +   dataSizeInBytes );
            System.out.println("Reading Buffer  Size In Bytes  =    "   +  dataSizeInBytes );


             numberOfTraces                 =   totalDataSize/dataSizeInBytes;
             kSpaceReal                     =   new float [numberOfTraces][pointsPerTrace ] ;
             kSpaceImag                     =   new float [numberOfTraces][pointsPerTrace ] ;

             ByteBuffer buffer              =   ByteBuffer.allocateDirect(dataSizeInBytes );  // BUFSIZE = 256
             buffer.order( byteOrder);

             //*



            channel.position( headerBites);
            for (int curTrace = 0; curTrace <numberOfTraces ; curTrace++) {
              //  System.out.println("Reading trace # "+curTrace );
               System.out.println( curTrace + " "+channel.position());
                channel.read(buffer);



                buffer.rewind();

                for (int i = 0; i < getVectorSize(); i++) {
                    float re                    = (float)buffer.getDouble();
                    float im                    = (float)buffer.getDouble();
                    kSpaceReal [curTrace][i]    = re;
                    kSpaceImag [curTrace][i]    = im;
                     
                }

                  buffer.rewind();
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
                linescanner.close();
                scanner.close();
                fin.close();
                channel.close();
            } catch (IOException ex) { }

        }

    }

}
