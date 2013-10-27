/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
/**
 *
 * @author apple
 */
public class ReadMcmcSamples {
  public static int BYTES_PER_POINT                     =   8;
  public static ByteOrder byteOrder                     =   ByteOrder.BIG_ENDIAN;
 
  
    public static double [] read (File file,int parIndex, int sims, int reps){
        int         length      =   sims * reps;
        double []   data        =   new double [length];   
       
        try{
           
            FileInputStream     fin =   new FileInputStream(file);
            int fileSizeInBytes     =   fin.available();
            BufferedInputStream bin =   new BufferedInputStream(fin, fileSizeInBytes );
            DataInputStream     din =   new DataInputStream(bin);

           int nParams              =   fileSizeInBytes/  ( sims * reps * BYTES_PER_POINT );
           int initialskip          =   BYTES_PER_POINT * ( parIndex - 1);
           int skip                 =   BYTES_PER_POINT * ( nParams  - 1);

           din.skipBytes(initialskip );
           for (int i = 0; i < length ; i++) {
                data [i]    =  din.readDouble();
                //System.out.println(data [i]);
                din.skipBytes(skip);
           }
            
            din.close();
            bin.close();
            fin.close();
      } catch(IOException exp){
            exp.printStackTrace();
            return null;
      }
        return data;
    }


    // this (NIO)seems to work slower that (IO)
    public static double [] readNio (File file,int parIndex, int sims, int reps){


         FileInputStream     fin        =   null;
         FileChannel channel            =   null;
         int         length             =   sims * reps;
         double []   data               =   new double [length];
         try {
             fin                            =   new FileInputStream(file);
             channel                        =   fin.getChannel();
             int sizeInBytess               =   fin.available();
             int bytesPerParameter          =   sims * reps * BYTES_PER_POINT;
             int nParams                    =   sizeInBytess/ bytesPerParameter ;
             int initialskip                =   BYTES_PER_POINT * ( parIndex - 1);
             int skip                       =   BYTES_PER_POINT * nParams ;

             ByteBuffer buffer              =   ByteBuffer.allocateDirect( sizeInBytess  );  // BUFSIZE = 256
             //buffer.order( byteOrder);


            channel.read(buffer);


            int position = initialskip;
           for (int i = 0; i < length ; i++) {
                buffer.position(position) ;
              
                data [i]    = buffer.getDouble();
               //    System.out.println(data [i]);
                position    =   position + skip;
           }




            

         }catch (IOException ex) {
           ex.printStackTrace();
           data =  null;
        }
        finally{
            try {
                if (fin     != null) { fin.close();}
                if (channel != null) { channel.close();}
                 
            } catch (IOException ex) { }
             return data;
        }


    }
    
    public static void main(String [] args){
        File f = new File("/Users/apple/Bayes/exp3/BayesOtherAnalysis/Bayes.Mcmc.Samples");
        long t1 = System.nanoTime();
            //read (f, 3,50,50);
           readNio (f, 3,50,50);
        long t2 = System.nanoTime();

        double time = (t2-t1)*1e-9;
        System.out.println("time "+ time);
    }
}
