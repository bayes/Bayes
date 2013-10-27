/*
 * FidBlockHeader.java
 *
 * Created on August 22, 2007, 9:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;


public class FidBlockHeader {
    
    /** Creates a new instance of FidBlockHeader */
    public FidBlockHeader () {
    }
        public short scale     ;// scaling factor
        public short status    ;// status of data in block
        public short index     ;// block index
        public short mode      ;// mode of data in the block
        public int   ctcount   ;// ct value for FID
        public float lpval     ;// f2(2D - f1) left phase in phasefile
        public float rpval     ;// f2(2D - f1) right phase in phasefile
        public float lvl       ;// level drift corrections
        public float tlt       ;// tilt drift corrections
        
         /****** header status string bits starts *******/
         public    int s_data;      // 0 = no data, 1 = data
         public    int s_spec;      // 0 = FID, 1 = spectrum
         public    int s_32;        // 
         public    int s_float;     // 0 = integer, 1 = floating point
         public    int s_complex;   // 0 = real, 1 = complex
         public    int s_hypercomplex; //1 = hypercomplex

         public    int more_blocks;
         public    int np_cmplx;
         public    int nf_cmplx;
         public    int ni_cmplx;
         public    int ni2_cmplx;
     /****** header status string bits ends *******/  
     /* block header is 28 bytes*/     
    

    public  static FidBlockHeader readBlockHeader(FileChannel channel)
                            throws IOException{
        FidBlockHeader blockHeader = new  FidBlockHeader();
        ByteBuffer buffer          =  ByteBuffer.allocateDirect( FidFileHeader.BLOCK_HEADER_LENGTH);
        channel.read(buffer);
        buffer.rewind();

        blockHeader.scale          = buffer.getShort();
        blockHeader.status         = buffer.getShort();
        blockHeader.index          = buffer.getShort();
        blockHeader.mode           = buffer.getShort();
        blockHeader.ctcount        = buffer.getInt();
        blockHeader.lpval          = buffer.getFloat();
        blockHeader.rpval          = buffer.getFloat();
        blockHeader.lvl            = buffer.getFloat();
        blockHeader.tlt            = buffer.getFloat();
        blockHeader.readStatus( blockHeader.status);

        return blockHeader;
    }

   public static void writeBlockHeader(DataOutputStream dout,   FidBlockHeader blockHeader) throws IOException{
        dout.writeShort( blockHeader.scale ) ;
        dout.writeShort( blockHeader.status ) ;
        dout.writeShort( blockHeader.index ) ;
        dout.writeShort( blockHeader.mode  ) ;
        dout.writeInt( blockHeader.ctcount ) ;
        dout.writeFloat( blockHeader.lpval  ) ;
        dout.writeFloat( blockHeader.rpval  ) ;
        dout.writeFloat( blockHeader.lvl) ;
        dout.writeFloat( blockHeader.tlt) ;
    }
   public static void writeBlockHeader(FileChannel channel,   FidBlockHeader blockHeader) throws IOException{
        ByteBuffer buffer        =  ByteBuffer.allocateDirect(FidFileHeader.BLOCK_HEADER_LENGTH);

        buffer.putShort(blockHeader.scale );
        buffer.putShort(blockHeader.status );
        buffer.putShort(blockHeader.index );
        buffer.putShort(blockHeader.mode );
        buffer.putInt(blockHeader.ctcount);
        buffer.putFloat( blockHeader.lpval);
        buffer.putFloat( blockHeader.rpval);
        buffer.putFloat( blockHeader.lvl);
        buffer.putFloat( blockHeader.tlt);

        buffer.rewind();
        channel.write(buffer);

    }
   public  void readStatus(short s){
        int reversedStatus          =   Integer.reverse(s);
        String str                  =   Integer.toBinaryString(reversedStatus);
        if (str.equals("0")){ str   =   "0000000000000000";}

        str         = str.substring (0, 15) ;
       
         s_data         =  Integer.parseInt (str.substring(0,1)); 
         s_spec         =  Integer.parseInt (str.substring(1,2));
         s_32           =  Integer.parseInt (str.substring(2,3));
         s_float        =  Integer.parseInt (str.substring(3,4));
         s_complex      =  Integer.parseInt (str.substring(4,5));
         s_hypercomplex =  Integer.parseInt (str.substring(5,6));
         
         /* BIT 6 IS MISSING*/
        
         more_blocks    = Integer.parseInt (str.substring(7,8));
         np_cmplx       = Integer.parseInt (str.substring(8,9));
         nf_cmplx       = Integer.parseInt (str.substring(9,10));
         ni_cmplx       = Integer.parseInt (str.substring(10,11));
         ni2_cmplx      = Integer.parseInt (str.substring(11,12));
          
         /* BIT 12 IS MISSING */ 
         /* BIT 13 IS MISSING */
         /* BIT 14 IS MISSING */
         /* BIT 15 IS MISSING */
   
   } 
     
    public void print(){
        System.out.println("scale  = "  + scale );
        System.out.println("status = "  + status);
        System.out.println("index = "   + index);
        System.out.println("mode =  "   +  mode);
        System.out.println("ctcount = " + ctcount );
        System.out.println("lpval = "   + lpval );
        System.out.println("rpval = "   + rpval );
        System.out.println("lvl = "     +  lvl );
        System.out.println("tlt  = "    + tlt );

   } 
}
