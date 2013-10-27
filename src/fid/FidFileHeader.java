/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
/**
 *
 * @author larry
 */
public class FidFileHeader {
    public static final int  HEADER_LENGTH = 32; // in bytes 
     public static final int BLOCK_HEADER_LENGTH = 28; // in bytes
     public    int nblocks; //number of blocks in file
     public    int ntraces   = 1; //number of traces per block
     public    int np;      //number of elements per trace
     public    int ebytes;  //number of bytes per element
     public    int tbytes;  //number of bytes per trace
     public    int bbytes;  //number of bytes per block
     public    short vers_id;//software version, file id status
     public    short status;//status of whole file
     public    int nbheaders;//number of headers per block
     
     public    int s_data;              // 0 = no data, 1 = data
     public    int s_spec;              // 0 = FID, 1 = spectrum
     public    int s_32;                // 
     public    int s_float;             // 0 = integer, 1 = floating point
     public    int s_complex;           // 0 = real, 1 = complex
     public    int s_hypercomplex;      //1 = hypercomplex
        
     public    int s_acqpar;
     public    int s_secnd;
     public    int s_transf;
     public    int s_np;
     public    int s_nf;
     public    int s_ni;
     public    int s_ni2;
     //****** header status string bits ends *******/       
    public static FidFileHeader readFileHeader(FileChannel channel ) throws IOException{
        FidFileHeader fileHeader =  new FidFileHeader();
        ByteBuffer buffer        =  ByteBuffer.allocateDirect(HEADER_LENGTH);
        channel.read(buffer);
        buffer.rewind();

        fileHeader.nblocks     =  buffer.getInt() ;
        fileHeader.ntraces     =  buffer.getInt() ;
        fileHeader.np          =  buffer.getInt() ;
        fileHeader.ebytes      =  buffer.getInt() ;
        fileHeader.tbytes      =  buffer.getInt() ;
        fileHeader.bbytes      =  buffer.getInt() ;
        fileHeader.vers_id     =  buffer.getShort();
        fileHeader.status      =  buffer.getShort();
        fileHeader.nbheaders   =  buffer.getInt();

       // fileHeader.printAllParameters();
        fileHeader.readStatus(fileHeader.status);
        return fileHeader;
    }

    public static void writeFileHeader(DataOutputStream dout,  FidFileHeader fileHeader) throws IOException{
        dout.writeInt(fileHeader.nblocks ) ;
        dout.writeInt(fileHeader.ntraces ) ;
        dout.writeInt(fileHeader.np ) ;
        dout.writeInt(fileHeader.ebytes ) ;
        dout.writeInt(fileHeader.tbytes ) ;
        dout.writeInt(fileHeader.bbytes ) ;
        dout.writeShort(fileHeader.vers_id) ;
        dout.writeShort( fileHeader.status) ;
        dout.writeInt(fileHeader.nbheaders) ;
    }
    public static void writeFileHeader(FileChannel channel,  FidFileHeader fileHeader) throws IOException{
        ByteBuffer buffer        =  ByteBuffer.allocateDirect(HEADER_LENGTH);

        buffer.putInt(fileHeader.nblocks);
        buffer.putInt(fileHeader.ntraces);
        buffer.putInt(fileHeader.np);
        buffer.putInt(fileHeader.ebytes);
        buffer.putInt(fileHeader.tbytes);
        buffer.putInt(fileHeader.bbytes);
        buffer.putShort(fileHeader.vers_id);
        buffer.putShort(fileHeader.status);
        buffer.putInt(fileHeader.nbheaders);

        buffer.rewind();
        channel.write(buffer);

    }

    public void printAllParameters(){
        System.out.println("nblocks = "  + nblocks);
        System.out.println("ntraces = "+ntraces);
        System.out.println("np = "+np);
        System.out.println("ebytes = "+ebytes );
        System.out.println("tbytes = "+tbytes );
        System.out.println("bbytes = "+bbytes );
        System.out.println("vers_id  = "+ vers_id );
        System.out.println("status = "+status);
        System.out.println("nbheaders = " + nbheaders);
        System.out.println(Integer.toBinaryString (status)) ;
   }  
    
    public void readStatus(short s){
         String str = Integer.toBinaryString(Integer.reverse(s));
         str = str.substring (0, 15) ;
       
         s_data         =  Integer.parseInt (str.substring(0,1)); 
         s_spec         =  Integer.parseInt (str.substring(1,2));
         s_32           =  Integer.parseInt (str.substring(2,3));
         s_float        =  Integer.parseInt (str.substring(3,4));
         s_complex      =  Integer.parseInt (str.substring(4,5));
         s_hypercomplex =  Integer.parseInt (str.substring(5,6));
         
         /* BIT 6 IS MISSING*/
        
         s_acqpar   =   Integer.parseInt (str.substring(7,8));
         s_secnd    =   Integer.parseInt (str.substring(8,9));
         s_transf   =   Integer.parseInt (str.substring(9,10));
         
         /* BIT 10 IS MISSING */
         
         s_np       =   Integer.parseInt (str.substring(11,12));
         s_nf       =   Integer.parseInt (str.substring(12,13));
         s_ni       =   Integer.parseInt (str.substring(13,14));
         s_ni2      =   Integer.parseInt (str.substring(14,15));
        
          /* BIT 15 IS MISSING */
   }



    public int getNumberOfTotalTraces () {
        return ntraces * nblocks;
    }

    public static void main(String [] args){

        Integer val =   0x59;
        System.out.println(val.toBinaryString(val));
         System.out.println(val.toBinaryString(0x819));
        
    }
}
