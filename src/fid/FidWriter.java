/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
/**
 *
 * @author apple
 */
public class FidWriter {
    private FidBlockHeader  blockHeader      = new FidBlockHeader();
    private FidFileHeader fileHeader         = new FidFileHeader();
    private Procpar      procpar             = new Procpar();

    public void writeFid( File dist, float [][] real, float [][]imag)
    throws IOException{
        /*  Assumptions
         *  fileHeader.nTraces  = 1
         *  fileHeader.S_Float  = 1
         *  fileHeader.S_32     = 0
         *  The latter two conditions are satsified if
         *  fileHeader.status   = 0x59
         *  We also need to set
         *  blockHeader.status  = 0x819
         */
        asignDefaultsForFileHeader();
        asignDefaultsFoBlockHeader();
        writeFidFile(dist, real, imag);
    }
    public void asignDefaultsForFileHeader(){
          //number of blocks in file
     // fileHeader.nblocks     =  set by user, default 1;

        //number of traces per block
       // fileHeader.ntraces     =  set by user, default 1 ;

        //number of elements per trace
      //fileHeader.np          =  set by user;

        //number of bytes per element
        fileHeader.ebytes      =  4 ;

        //number of bytes per trace
        fileHeader.tbytes      = fileHeader.ebytes*fileHeader.np ;

        //number of bytes per block
        fileHeader.bbytes      = fileHeader.tbytes*fileHeader.ntraces
                                    + FidFileHeader.BLOCK_HEADER_LENGTH;

        //software version, file id status
        fileHeader.vers_id     = 0;

        //status of whole file
        //fileHeader.status      = 0x819; // binary representation = 100000011001
        fileHeader.status      = 0x1D; // binary representation = 000000011101

        //number of headers per block
        fileHeader.nbheaders    =  0;

       }
    public void asignDefaultsFoBlockHeader(){
        blockHeader.scale          = 0;
        blockHeader.status         = 0x59; // binary representation = 00000101
        blockHeader.index          = 1;
        blockHeader.mode           = 0;
        blockHeader.ctcount        = 1;
        blockHeader.lpval          = 0f;
        blockHeader.rpval          = 0f;
        blockHeader.lvl            = 0f;
        blockHeader.tlt            = 0f;

       }
    private void writeFidFile(File dst,  float [][] real, float [][]imag) throws IOException{
         FileOutputStream fos       =   new FileOutputStream(dst);
         FileChannel channel        =   fos.getChannel();
         ByteBuffer buffer          =   null;

         FidFileHeader ffh          =   getFileHeader();
         // get total number of traces from file header
         int nTotalTraces           =   ffh.getNumberOfTotalTraces();

         // get number of total points in one trace
         int np                     =   ffh.np;

         // get nmber of complex points in one trace
         int nComplex               =    np/2;

         int bytesPerTrace          =    np* ffh.ebytes;
        // int bufferSize             =    nTotalTraces * bytesPerTrace;
         buffer                     =   ByteBuffer.allocateDirect(bytesPerTrace  );
         buffer.order(ByteOrder.BIG_ENDIAN);


         // write file header
         FidFileHeader.writeFileHeader(channel,getFileHeader());


           for (int curTrace = 0;  curTrace <  nTotalTraces; curTrace++) {
               getBlockHeader().index = (short)( curTrace);
               FidBlockHeader.writeBlockHeader(channel,getBlockHeader());

                   for (int i = 0; i < nComplex; i++) {
                                buffer.putFloat(real[curTrace][i]) ;
                                buffer.putFloat (imag[curTrace][i]) ;
                   }
               buffer.rewind();
               channel.write(buffer);
               buffer.rewind();

          }

         channel.close();
         fos.close();
     }





    public void writeImageFid( File dist, float [] real, float []imag , boolean isSlicesFirst )
    throws IOException{
        /*  Assumptions
         *  fileHeader.nTraces  = 1
         *  fileHeader.S_Float  = 1
         *  fileHeader.S_32     = 0
         *  The latter two conditions are satsified if
         *  fileHeader.status   = 0x59
         *  We also need to set
         *  blockHeader.status  = 0x819
         */
        asignDefaultsForFileHeader();
        asignDefaultsFoBlockHeader();


        FileOutputStream     fout   =  new FileOutputStream(dist);
        FileChannel    channel      =  fout.getChannel();
        if ( isSlicesFirst){
             writeImageFidFileSEPR(channel, real, imag);
        }else{
             writeImageFidFileESPR(channel, real, imag);
        }

        channel.close();
        fout.close();



    }



     private void writeImageFidFileESPR( FileChannel channel,
                                        float []real,
                                        float []imag)
                                        throws IOException
    {
            int nSlices                 = getProcpar().getNs();
            int nElements               = getProcpar().getNumberOfElements();
            int nPhaseEncode            = getProcpar().getNumberOfPhaseEncodePoints();
            int nReadouts               = getProcpar().getNumberOfReadOutPoints();
            int bufferSize              = 2*4*nSlices *nReadouts;
            ByteBuffer buffer           = ByteBuffer.allocateDirect(bufferSize);

            // write file header
            FidFileHeader.writeFileHeader(channel,getFileHeader());



         for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {
            for (int curElement = 0; curElement < nElements; curElement++) {
                 FidBlockHeader.writeBlockHeader(channel,getBlockHeader());

                    for (int curSlice  = 0;  curSlice  <nSlices;  curSlice += 1) {
                        for (int curReadout = 0; curReadout <  nReadouts ; curReadout++) {

                            int pos = getIndexForESPR( curElement,curSlice,curPhaseEncode,
                                            nElements, nSlices,nPhaseEncode,  nReadouts  );
                            int ind = pos + curReadout;
                            buffer.putFloat(real[ind]);
                            buffer.putFloat(imag[ind]);

                        }
                    }

                    buffer.rewind();
                    channel.write(buffer);

                    buffer.rewind();

              }

        }
     }

      private void writeImageFidFileSEPR( FileChannel channel,
                                        float []real,
                                        float []imag)
                                        throws IOException
    {
            int nSlices                 = getProcpar().getNs();
            int nElements               = getProcpar().getNumberOfElements();
            int nPhaseEncode            = getProcpar().getNumberOfPhaseEncodePoints();
            int nReadouts               = getProcpar().getNumberOfReadOutPoints();
            int bufferSize              = 2*4*nSlices *nReadouts;
            ByteBuffer buffer           = ByteBuffer.allocateDirect(bufferSize);

            // write file header
            FidFileHeader.writeFileHeader(channel,getFileHeader());


         for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {
            for (int curElement = 0; curElement < nElements; curElement++) {
                 FidBlockHeader.writeBlockHeader(channel,getBlockHeader());

                    for (int curSlice  = 0;  curSlice  <nSlices;  curSlice += 1) {
                        for (int curReadout = 0; curReadout <  nReadouts ; curReadout++) {

                            int pos = getIndexForSEPR( curElement,curSlice,curPhaseEncode,
                                            nElements, nSlices,nPhaseEncode,  nReadouts  );
                            int ind = pos + curReadout;

                            buffer.putFloat(real[ind]);
                            buffer.putFloat(imag[ind]);

                        }
                    }
                    buffer.rewind();
                    channel.write(buffer);
                    buffer.rewind();

              }

        }

     }

     /*
      *  This will caluclate and return index in
      * onde dimensional array of data, given that arrayis popluated
      * as follows(ESPR)
      * Elements    -   1 loop
      * Slices      -   2 loop
      * PhaseEncode -   3 loop
      * Readout     -   4 loop
      */
    public static int getIndexForESPR(  int curElement,
                                        int curSlice,
                                        int curPhaseencode,
                                        int nElements,
                                        int nSlices,
                                        int nPhaseEncodes,
                                        int nReadouts)
    {
        int imageSize       =  nPhaseEncodes *  nReadouts;
        int sliceSize       =  nSlices *imageSize;


        int index = curElement*sliceSize + curSlice*imageSize + curPhaseencode*nReadouts;


        return index;
    }

    /*
      *  This will caluclate and return index in
      * onde dimensional array of data, given that arrayis popluated
      * as follows(ESPR)
      * Slices      -   1 loop
      * Elements    -   2 loop
      * PhaseEncode -   3 loop
      * Readout     -   4 loop
      */
    public static int getIndexForSEPR(  int curElement,
                                        int curSlice,
                                        int curPhaseencode,
                                        int nElements,
                                        int nSlices,
                                        int nPhaseEncodes,
                                        int nReadouts)
    {
        int imageSize       =  nPhaseEncodes *  nReadouts;
        int elementSize     =  nElements     *  imageSize;


        int index = curSlice*elementSize  + curElement*imageSize + curPhaseencode*nReadouts;


        return index;
    }



    public FidBlockHeader getBlockHeader () {
        return blockHeader;
    }
    public FidFileHeader getFileHeader () {
        return fileHeader;
    }

    public Procpar getProcpar () {
        return procpar;
    }
    public void setProcpar ( Procpar procpar ) {
        this.procpar = procpar;
    }


    public static void main(String [] args){
        int ind = getIndexForESPR(44,2,127, 45,3,128,256);
        System.out.println(ind + 255);
        ind = getIndexForSEPR(0,3,0, 45,3,128,256);
        System.out.println(ind + 255);
    }
}
