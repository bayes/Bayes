/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;
import applications.model.Model;
import bayes.DirectoryManager;
import bayes.PackageManager;
import interfacebeans.TextViewer;
import utilities.DisplayText;
import java.util.*;
import java.io.*;
import bayes.BayesManager;
import utilities.IO;
import static ascii.AsciiDescriptor.*;
/**
 *
 * @author apple
 */
public class AsciiIO {
    public final static int INVALID_DATA_FILE           =   -4000;
    public final static int DUPLICATE_DATA_FILE         =   -3000;
    public final static int VALID_DATA_FILE             =   1;



    /* read and write Ascii AscciDescriptor */
    public  static AsciiDescriptor  loadFromDisk(File file) throws FileNotFoundException{
        AsciiDescriptor id      =   new AsciiDescriptor();
        String val              =   null;
        String content          =   IO.readFileToString(file);

        id.setFullIunfo(content);


        val                     =   readValueForKey (content,AsciiDescriptor.KEYDataOrigin);
        if (val != null){
           id.setDataSource(val);
        }
     

        val                     =   readValueForKey (content,AsciiDescriptor.KEY_SOURCE_FILE);
        if (val != null){
           id.setSourceFileName(val);
        }

       
        val                     =   readValueForKey (content,AsciiDescriptor.KEYDATE  );
        if (val != null){
           id.setDATE(val);
        }


        val                     =   readValueForKey (content,AsciiDescriptor. KEYNumberOfColumns  );
        if (val != null){
              id.setNumberOfColumns(Integer.valueOf(val));
        }

        val                     =   readValueForKey (content,AsciiDescriptor. KEYNumberOfRows );
        if (val != null){
              id.setNumberOfRows(Integer.valueOf(val));
        }


        val                     =   readValueForKey (content,AsciiDescriptor. KEYExtraInfo   );
        if (val != null){
              id.setExtraInfo(val);
        }

     return id;
}
    public  static boolean storeToDisk(AsciiDescriptor id, File dist){
        BufferedWriter out         =   null;
        String  EOL                =   BayesManager.EOL;
        String  sp                 =   ":=";
        try{
            dist.createNewFile();
            FileWriter fr      =    new FileWriter( dist);
            out                =    new BufferedWriter(fr);


            // line
            out.write (IO.pad( KEYDataOrigin , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDataSource());
            out.write(EOL);



            // line
            out.write (IO.pad( KEY_SOURCE_FILE , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getSourceFileName());
            out.write(EOL);


            // line
            out.write (IO.pad( KEYDATE  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getDATE());
            out.write(EOL);


             // line
            out.write (IO.pad(  KEYNumberOfColumns  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+ id.getNumberOfColumns());
            out.write(EOL);

            // line 
            out.write (IO.pad(  KEYNumberOfRows  , PAD_LEN, PAD_CHAR));
            out.write (sp);
            out.write(" "+  id.getNumberOfRows());
            out.write(EOL);

            //line
            if (id.getExtralnfo() != null){
                out.write (IO.pad(  KEYExtraInfo , PAD_LEN, PAD_CHAR));
                out.write (sp);
                out.write(" "+  id.getExtralnfo() );
                out.write(EOL);
            }

            out.close();
            fr.close ();;
         } catch (IOException ex) {
            ex.printStackTrace();
            return false;

        }
      return true;
    }
    private static String readValueForKey (String content, String key){
      String [] strArray    =   null; // data for abscissa
      String value          =   null; // data for abscissa
      String line           =   "";
      String spr            =   ":=";
      Scanner scanner       =   new Scanner(content);
      boolean matchFound    =   false;

      while(scanner.hasNextLine() == true ){
        line            =    scanner.nextLine();
        matchFound      =   line.contains(key);

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

    public static boolean createAndSaveAfhFile( File data,
                                                File afhFle,
                                                String src,
                                                String srcDescriptor){
       int col                          =   1;
       int row                          =   1;
        try{
                col                     =   IO.getNumberOfColumns(data);
                row                     =   IO. getNumberOfLines(data.getPath());

                AsciiDescriptor ad       = new  AsciiDescriptor();
                ad.setNumberOfColumns(col);
                ad.setNumberOfRows(row);
                ad.setSourceFileName(src);
                ad.setDataSource(srcDescriptor);
                storeToDisk(ad,afhFle );
       }

        catch (IOException exp) {  return false;}
        return true;
    }





    public static void      copyAsciiFiles(File[] files, File dir) {
        int status = 0;
        for (File file : files) {
           int  i = copyAsciiFile(file, dir);
           if (i == DUPLICATE_DATA_FILE)  { status = DUPLICATE_DATA_FILE;}
        }

        if ( status == DUPLICATE_DATA_FILE){

             String message =   "The duplicate data files were encountered. " +
                                "Duplicate files were not loaded.";

             DisplayText.popupMessage(message);
        }

        // do some clean up
        File bayesModelProbFile = DirectoryManager.getBayesModelProbabilityFile();
        if ( bayesModelProbFile  != null && bayesModelProbFile.exists()){
            bayesModelProbFile.delete();
        }
        if ( TextViewer.getInstance ().isShowing()){
             TextViewer.getInstance ().update ();
        }
    }
    private static int      copyAsciiFile(File file, File dir) {
        if (!isFileValid(file)) { return INVALID_DATA_FILE; }// file is not valid

        if (dir.exists() == false){
            boolean isDone = dir.mkdirs();
            System.out.println("Is directory "+ dir.getAbsolutePath() + " created  == "+ isDone);

        }
        System.out.println("Copying ascii data file into "+dir.getAbsolutePath());



        File dist               =   DirectoryManager.getNewAsciiFile(dir);
        File afhDst             =   DirectoryManager.getAfhFileForData(dist);
        String sourse           =   file.getPath();
        String srcDescriptor    =   AsciiDescriptor.SOURCE_TYPE.FILE.getInfo();
        boolean isAfhFile       =   AsciiIO.createAndSaveAfhFile(file, afhDst, sourse , srcDescriptor);

        if ( isAfhFile == false) {return INVALID_DATA_FILE;}

        IO.win2unixFileCopy(file, dist);

        return VALID_DATA_FILE ;
    }
    public static void      clearAsciiFileNonCompatibleWithModel(Model model) {

        List <File> dataFiles = DirectoryManager.getAsciiDataFileListOnDisk();
        for (File file : dataFiles) {
               if ( isFileValid(file, model, true) == false){

                   ASCIIDataViewer.getInstance().deleteFile(file);
               }
        }


    }
    public static boolean   isFileValid(File file){
        Model model             =   PackageManager.getCurrentApplication();
        return isFileValid(file, model);
    }
    public static boolean   isFileValid(File file, boolean dontshowMessages){
        Model model             =   PackageManager.getCurrentApplication();
        return isFileValid(file, model, dontshowMessages);
    }
    private static boolean  isFileValid(File file, Model model){

            boolean dontWarnUser = false;
            return isFileValid(file, model, dontWarnUser);
    }
    private static boolean  isFileValid(File file, Model model, boolean dontshowMessages){
        if (model == null)      { return true;}

        int col_num_model       =   model.getTotalNumberOfColumns();
        int col_num_file;

        try{ col_num_file    =   IO.getNumberOfColumns(file);}
        catch (IOException exp) {  return false;}


        if (col_num_model != col_num_file){
            if (dontshowMessages == false){
                 String message = String.format ("File %s is not " +
                                            "a valid file. \n"+
                                             "The  number of white-space separated columns (%d) \n" +
                                             "is different from (%d) required by the %s package.\n" ,
                                             file.getPath(),
                                             col_num_file,
                                             col_num_model,
                                             model.getProgramName());

             DisplayText.popupMessage( message);

            }

             return false;
        }

        return true;
    }

    public  static boolean      writeAsciiData(
                                        String content,
                                        String src,
                                        String srcDescriptor,
                                        int numberOfColumns,
                                        int numberOrRows){

        boolean success                 =   false;
        try{

                File dir                =   DirectoryManager.getBayesOtherAnalysisDir();
                File asciiFile          =   DirectoryManager.getNewAsciiFile(dir);
                File afhFile            =   DirectoryManager.getAfhFileForData(asciiFile);
                AsciiDescriptor ad      =   new  AsciiDescriptor();
                ad.setNumberOfColumns(numberOfColumns);
                ad.setNumberOfRows(numberOrRows);
                ad.setSourceFileName(src);
                ad.setDataSource(srcDescriptor);

                IO.writeFileFromString(content, asciiFile );
                AsciiIO.storeToDisk(ad,afhFile  );



                success                 =   true;
       }

        catch (Exception exp) { exp.printStackTrace();}
        finally{
             return success ;
        }


    }
}
