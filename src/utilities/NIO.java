/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
/**
 *
 * @author apple
 */
public class NIO {
    private NIO(){};


    public static boolean copy (File src, File dst){
        FileChannel srcChannel  = null;
        FileChannel dstChannel  = null;

        try {
        // Create channel on the source
        srcChannel = new FileInputStream(src).getChannel();

        // Create channel on the destination
        dstChannel = new FileOutputStream(dst).getChannel();

        // Copy file contents from source to destination
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

      } catch (IOException e) {
          e.printStackTrace();
          return false;
      }
      finally{
            try {
                // Close the channels
                srcChannel.close();
                dstChannel.close();
            } catch (IOException ex) {
                return false;
            }
      }
      return true;
    }


    // Not Working so far......... :(
    public static  String readFileToString(File file) {
        FileInputStream stream  =   null;
        FileChannel fc          =   null;
        String content          =   null;
      try {
        stream                  =  new FileInputStream(file);
        fc                      = stream.getChannel();
       MappedByteBuffer bb     = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
       // content  = Charset.defaultCharset().decode(bb).toString();

        ByteBuffer buffer          =   ByteBuffer.allocateDirect(stream.available());

        content = buffer.toString();

        stream.close();
       }catch (FileNotFoundException ex) {
                 ex.printStackTrace();
        } catch (IOException ex) {
                ex.printStackTrace();
        }


       return content;
    }

   public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (src.read(buffer) != -1) {
      // prepare the buffer to be drained
      buffer.flip();
      // write to the channel, may block
      dest.write(buffer);
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact();
    }
    // EOF will leave buffer in fill state
    buffer.flip();
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining()) {
      dest.write(buffer);
    }
  }
    public static boolean copyNew (File src, File dst){
        InputStream input                       =   null;
        OutputStream output                     =   null;
        ReadableByteChannel inputChannel        =   null;
        WritableByteChannel outputChannel       =   null;

        try {

        // allocate the stream ... only for example
        input                                   = new FileInputStream(src);
        output                                  = new FileOutputStream(dst);

        // get an channel from the stream
        inputChannel                            = Channels.newChannel(input);
        outputChannel                           = Channels.newChannel(output);

        // copy the channels
        fastChannelCopy(inputChannel, outputChannel);



      } catch (IOException e) {
          e.printStackTrace();
          return false;
      }
      finally{
            try {
                 // closing the channels
                 if (inputChannel != null  ) { inputChannel.close();  }
                 if (outputChannel!= null  ) { outputChannel.close(); }

            } catch (IOException ex) {
                return false;
            }
      }
      return true;
    }

      public static void main (String [] args){
            File src = new File("/Users/apple/BayesSys/images/test/BayesPhase_Imag.4dfp.img");
            File dst = new File("/Users/apple/test");
            String content = null;

            long t1 = System.nanoTime();
               //readImage(src, id);

               IO.copyFile(src, dst);
                //copyNew(src, dst);
                //copy(src, dst);


            long t2 = System.nanoTime();
            double time = (t2-t1)*1E-9;
            System.out.println("time "+ time);
      }
}
