/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.util.*;
import java.io.*;
import bayes.BayesManager;
import bayes.Enums.UNITS;
import utilities.IO;
import static fid.FidDescriptor.*;
/**
 *
 * @author apple
 */
public class FidIO {

     public  static boolean storeToDisk(FidDescriptor id, File dist){

        BufferedWriter out         =   null;
        String  EOL                =   BayesManager.EOL;
        String  sp                 =   ":=";
        try{
            if (dist.getParentFile().exists() == false){
                dist.getParentFile().mkdirs();
            }

            FileWriter fr      =    new FileWriter( dist);
            out                =    new BufferedWriter(fr);

            // line
            out.write (IO.pad( KEY_SOURCE_FILE  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getSourceFileName());
            out.write(EOL);

            // line
            out.write (IO.pad(  KEYDATE , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDATE());
            out.write(EOL);

            // line
            out.write (IO.pad(  KEYDELAY  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDelay());
            out.write(EOL);

            // line
            out.write (IO.pad(  KEYPHASE  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getPhase());
            out.write(EOL);

              // line
            out.write (IO.pad( KEYREFERENCEHERTZ   , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getReferenceInHertz());
            out.write(EOL);


            // line
            out.write (IO.pad( KEYLB   , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getUserLB());
            out.write(EOL);

            // line
            out.write (IO.pad( KEYFN    , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getUserFn());
            out.write(EOL);

            // line
            out.write (IO.pad(KEYUNITS     , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getUnits());
            out.write(EOL);
            
            out.close();
            fr.close ();
         } catch (IOException ex) {
            ex.printStackTrace();
            return false;

        }
      return true;
    }
    

     public static String readValueForKey (String content, String key){
      String [] strArray    =   null; // data for abscissa
      String value          =   null; // data for abscissa
      String line           =   "";
      String spr            =   ":=";
      Scanner scanner       =   new Scanner(content);
      boolean matchFound    =   false;

      while(scanner.hasNextLine() == true ){
        line                =    scanner.nextLine();
        matchFound          =   line.contains(key);

        if (matchFound ){
               strArray                 =   line.split (spr);
               if (strArray.length < 1) {return null;}
               else {  value            =   strArray[1].trim(); }
               break;
        }
      }

      scanner.close();
      return value ;
  }
     public  static FidDescriptor  loadFromDisk(File file){
        FidDescriptor  id      =   new  FidDescriptor ();
        String val              =   null;
        if (file.exists() == false) {return id;}
        String content          =   IO.readFileToString(file);

        val                     =   readValueForKey (content,KEY_SOURCE_FILE);
        if (val != null){
           id.setSourceFileName(val);
        }

       
        val                     =   readValueForKey (content,KEYDATE  );
        if (val != null){
           id.setDATE(val);
        }


        val                     =   readValueForKey (content,KEYDELAY  );
        if (val != null){
              id.setDelay(Float.valueOf(val));
        }

        val                     =   readValueForKey (content, KEYPHASE  );
        if (val != null){
              id.setPhase(Float.valueOf(val));
        }
        
        val                     =   readValueForKey (content, KEYREFERENCEHERTZ   );
        if (val != null){
              id.setReferenceInHertz(Float.valueOf(val));
        }

        val                     =   readValueForKey (content, KEYLB    );
        if (val != null){
              id.setUserLB(Float.valueOf(val));
        }


        val                     =   readValueForKey (content, KEYFN    );
        if (val != null){
              id.setUserFn(Integer.valueOf(val));
        }


        val                     =   readValueForKey (content, KEYUNITS    );
        if (val != null){
            UNITS units = UNITS.HERTZ;
            try{
                units  = UNITS.getTypeByName(val);
            }
            catch( IllegalArgumentException exp){
                exp.printStackTrace();
            }
            id.setUnits( units);
        }


     return id;
}
     public  static FidDescriptor  loadFromDiskBackedByProcpar(File file, Procpar procpar) {
        FidDescriptor fd            = procparToFidDescriptor(procpar);
        if (file != null && file.exists()){
            String content              =    IO.readFileToString(file);
            String val                  =    "";

            val                     =   readValueForKey (content,KEY_SOURCE_FILE);
            if (val != null){
               fd.setSourceFileName(val);
            }

            val                     =   readValueForKey (content,KEYDATE  );
            if (val != null){
                fd.setDATE(val);
            }

            val                     =   readValueForKey (content,KEYDELAY  );
            if (val != null){
                  fd.setDelay(Float.valueOf(val));
            }

            val                     =   readValueForKey (content, KEYPHASE  );
            if (val != null){
                  fd.setPhase(Float.valueOf(val));
            }

            val                     =   readValueForKey (content, KEYREFERENCEHERTZ   );
            if (val != null){
                  fd.setReferenceInHertz(Float.valueOf(val));
            }

            val                     =   readValueForKey (content, KEYLB    );
            if (val != null){
                  fd.setUserLB(Float.valueOf(val));
            }

            val                     =   readValueForKey (content, KEYFN    );
            if (val != null){
                  fd.setUserFn(Integer.valueOf(val));
            }

            val                     =   readValueForKey (content, KEYUNITS    );
            if (val != null){
                try{
                   UNITS units  = UNITS.getTypeByName(val);
                    fd.setUnits( units);
                }
                catch( IllegalArgumentException exp){
                    exp.printStackTrace();
                }

            }

        }
        
     return fd;
}

     public static int getNearestLargerPowerOf2(int in){
        int out             =    16;
        while (out < in){
            out     =   2 * out;
        }
        return out;

    }

     // convenience methodes
     public static boolean createAndSaveFfhFile(Procpar procpar, File ffhFle){
        FidDescriptor fd       = procparToFidDescriptor(procpar);
        FidIO.storeToDisk(fd,ffhFle );
        return true;
   }
     // convenience methodes
     public static FidDescriptor procparToFidDescriptor(Procpar procpar){
        FidDescriptor fd       = new   FidDescriptor();
        try{
             fd.setDelay(0);
            fd.setPhase(0);
            fd.setUserLB(procpar.getLb());
            fd.setUserFn( procpar.getFn());
            fd.setReferenceInHertz( procpar.getRef());
            fd.setSourceFileName(procpar.getFileSource());
            fd.setUnits(procpar.getUnits());

            if (procpar.isFnActivated()){
                 fd.setUserFn( procpar.getFn());
            }
            else{
                 int fn =   getNearestLargerPowerOf2(procpar.getNp());
                  fd.setUserFn(fn);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
             return fd;
        }



   }


}
