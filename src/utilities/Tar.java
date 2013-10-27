/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import org.apache.tools.tar. TarOutputStream;
import org.apache.tools.tar. TarInputStream;
import org.apache.tools.tar.TarEntry;
import java.io.*;
import utilities.OSConversions.DIRECTIONS;
/**
 *
 * @author apple
 */
public class Tar {
    public final static int BUFFER_SIZE = 1024;
    
    public static void tar(File tarfile,File baseDir, File... files) {
        FileOutputStream fos    =   null;
        TarOutputStream tos     =   null;
        try
        {
           fos                  =   new FileOutputStream(tarfile);
           tos                  =   new TarOutputStream(fos);
           for (File file : files) {
               tar (tos, baseDir,file);
            }
          
        } 
        
        catch (IOException ex) 
        {
            ex.printStackTrace();
        } 
        
        finally 
        {
            try {
                tos.close();
                fos.close();
            } catch (IOException ex) {
               ex.printStackTrace();;
            }
           
        }
      
    }

    public static void untar(File tarfile){
        FileInputStream fis     =   null;
        TarInputStream in       =   null;
        byte buffer[]           =   new byte[BUFFER_SIZE];
        TarEntry tarEntry       =   null;
        File     dir            =   tarfile.getParentFile();
        
        
        try {
            fis     = new FileInputStream(tarfile);
            in      = new TarInputStream(fis);
           
           while( (tarEntry = in.getNextEntry()) != null) {
                
               String filename      = tarEntry.getName();
            if ( filename.endsWith("/") ||  filename.endsWith("\\")){
                // if it is direcotry
                       continue;
            }
            
            filename             = OSConversions.modifyForWindows(filename, DIRECTIONS.TO_WINDOWS);
            File file            = new File(dir, filename  );
            File dstDir          = file.getParentFile();
               
               
               
           if (dstDir.exists() == false ) {dstDir.mkdirs();}
           FileOutputStream out = new   FileOutputStream(file);
           while (true) {
                    int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead <= 0){  break;}
                    out.write(buffer, 0, nRead);
            }
            out.close();
            }
            
             in.close();
             fis.close();
        
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    public static String getRelativePath(File reference, File file){
      String referencePath      =   reference.getPath() + File.separator;
      String filepath           =   file.getPath() ;    
      filepath                  =   filepath.replace( referencePath,"" );
   
      return filepath;
    }
    public static void tar( TarOutputStream tos, File baseDir, File src)  throws IOException{
        byte buffer[]           =   new byte[BUFFER_SIZE];
        if (src.exists() == false){return;}
        
        if (src.isDirectory() == true)
        {
            for (File file : src.listFiles()) {
                tar( tos,baseDir,file);
            }
            
        }    
        else 
        {
                TarEntry ent        =   new  TarEntry(src);
                String filename     =   getRelativePath( baseDir,src);

                filename            =   OSConversions.modifyForWindows(filename, DIRECTIONS.FROM_WINDOWS);;
                ent.setName(filename);
                tos.putNextEntry(ent);

                if (src.exists() == true){
                    
                    FileInputStream in  =   new FileInputStream(src);
                    while (true) {
                            int nRead = in.read(buffer, 0, buffer.length);
                            if (nRead <= 0){  break;}
                            tos.write(buffer, 0, nRead);
                    }

                    in.close();
                    tos.closeEntry();
                }
            }
    }
    
   
    
    public static void main (String [] args){
       File tarFile = new File ( "/Users/apple/Bayes/test.tar");
       File dir     =   new File("/Users/apple/Bayes/Bayes.test.data");
       untar(tarFile);
    }
   
}
