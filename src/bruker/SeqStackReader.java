/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

import image.raw.BinaryReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author apple
 */
public class SeqStackReader {
    private BinaryReader binaryReader       =   null;
    private File sourceDir                  =   null;
    private List<File> sourceLevel1Dirs     =   new ArrayList<File>();
    private String level1DirName            =   "1";
    private String level2DirName            =   "pdata";
    private String level3DirName            =   "1";
    private String filename                 =   "2dseq";
    private List<File>seqFiles              =   new ArrayList<File>();
    private String errors                   =   "";
    public SeqStackReader(){
        this (null);
    }
    public SeqStackReader(File src){
        this(src, new ArrayList<File> ());
    }
    public SeqStackReader(File src, List<File> srcs){
       this.sourceDir          =   src;
       this.sourceLevel1Dirs.clear();
       this.sourceLevel1Dirs.addAll(srcs);
    }
   

    public File getD2SeqFile(){
        String filepath         =   sourceDir.getAbsolutePath() +
                                    "/"+ level1DirName +
                                    "/"+ level2DirName +
                                    "/"+ level3DirName +
                                    filename;
        File file               =   new File(filepath);

        return file;
    }


    public List<File>list2dseqFiles(){
        List<File> files = new ArrayList<File>();
        try{
            List<File> level1DirList    =   sourceLevel1Dirs;

            // get and sort first level directories
            if (level1DirList.isEmpty()) {
                for (File dir :   sourceDir.listFiles()) {
                    if (dir.isDirectory() ){ level1DirList.add(dir); }
                }
            }
            
            
            /* I assume files names are simply integers like 1,2 3
             * However, if file name is a String other ther integer representaion,
             * error witll be thrown for parsing integers. 
             * In the case revert to default sorting
             */
            
            try{
                Collections.sort(level1DirList, new FileNumberNameComparator());
            }
            catch(Exception e){
                 Collections.sort(level1DirList);
            }
            

            for (File dir1 : level1DirList) {

                File dir2               =   new File (dir1, level2DirName );
                if (dir2.exists() == false|| dir2.isDirectory() == false){continue;}

                File dir3               =   new File (dir2, level3DirName );
                if ( dir3 .exists()  == false||  dir3 .isDirectory() == false){continue;}
                File seqfile             =   new File (dir3, filename  );
                if ( seqfile  .exists() && seqfile .isFile()){
                    files.add(seqfile);
                }
            }

        }
        catch(Exception e){ e.printStackTrace();}
        finally{
            return files;
        }
    }

    public BinaryReader read(){

        /*
         * Initially BinaryReder is set to null.
         * This is done so the initial values for binary reader would be
         * assigned from the very first 2dseq image that will be parsed.
         */
         binaryReader                   =   null;
        // reset to default values
        seqFiles.clear();
        errors                          =    "";
        
        StringBuilder erSB              =   new StringBuilder();
        String er                       =   "";

        try{
        
        seqFiles = list2dseqFiles();
        
        
        
        for (File curSeqFile : seqFiles) {
             BinaryReader curReader             =  SeqReader.creatBinaryReader(curSeqFile );
             if (curReader   == null){
                 er = String.format("Failed to intialize reader for %s \n",curSeqFile.getPath() );
                 erSB.append(er);
                 continue;
             }
             curReader.read(curSeqFile);
             if (curReader.isLoaded() == false){
                 er = String.format("Failed to read %s \n",curSeqFile.getPath() );
                 erSB.append(er);
                 continue;}
             
             if (binaryReader== null){
                 binaryReader = curReader;
             }
             else if (  binaryReader.isMergingEquivalent(curReader, true)  == false){
                 // skip current image if it seems to be incompatible with previously loaded
                 er  =   String.format("File %s seems to be incompatible\n"
                        + " with  the files loaded earlier.\n"
                        + "This file is skipped.\n", curSeqFile.getPath());
                 erSB.append(er);

                 continue;
             }
             else{
                 System.out.println("Merging file "+  curSeqFile.getPath());
                 boolean done   =   binaryReader.mergeByElements(curReader);
                 System.out.println("Merging succeeded: "+ done);
                 System.out.println("");
             }
        
              binaryReader.setSourseFile(sourceDir);
        }
       }catch(Exception e){
            e.printStackTrace();
       }
        finally{
            if (erSB != null && erSB.length() > 0){
                System.out.println(erSB);
            }
            return binaryReader;
        }
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
class FileNumberNameComparator implements Comparator<File>{
    @Override
   public int	compare(File f1, File f2){
        int i1                  =   0;
        int i2                  =   0;
        try{
             i1                 =   Integer.parseInt(f1.getName());
             i2                 =   Integer.parseInt(f2.getName());
        }
        catch (Exception e){e.printStackTrace();}
        finally{
            return i1 - i2;
        }


   }

}