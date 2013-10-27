/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import bayes.PackageManager;
import java.io.*;
import utilities.*;
import image.*;
import java.util.*;
import ij.io.FileInfo;
import  java.util.Arrays;
/**
 *
 * @author apple
 */
public class Dicom2ImgConverter {
  private ImageDescriptor  descriptor                =  null;
  private ImageDescriptor  curDescriptor             =  null;
  private List <Double> sliceLocationList            =  new  ArrayList<Double>();
  private List <String> seriesList                   =  new  ArrayList<String>();
  private List <String> studyList                    =  new  ArrayList<String>();
  private double        curSliceLocation             =  0.0;
  private int numberOfSlices                         =  0;
  private int numberOfElements                       =  0;
  private int numberOfImages                         =  0;
  private String studyID                             =  null;
  private String seriesNumber                        =  null;
  
   public   boolean readAndWrite(File sourceFile) {
        List<float[][]> allImages   =    null;
        float [] values             =    null;
        values                      =    read(sourceFile, false);

        // make sure that files was read correctly
        if (values == null) {return false;}

         allImages                  =  ImageConvertHelper.populateESRP(values, getDescriptor());
         values                     =   null;


        // write  binary .img file and text ifh file
        // in the curreExp/fid directory
        String srcFilename          =   sourceFile.getName();
        boolean isWrite             =  ImageConvertHelper.writeImgFiles(srcFilename,allImages, getDescriptor());

       return  isWrite;
    }



   public  float [] read(File file, boolean checkHeaderInfo) {
        String message              =    null;
        float [] values             =    null;
        FileInfo fi                 =    null;
        DicomDecoder dd             =    new DicomDecoder(file);
        try {
           fi                      =   dd.readFileInfo();
           values                  =   DicomDecoder.readPixels(fi );
           ImageDescriptor curID   =   assignImageDescriptor(fi);
           ImageDescriptor id      =   this.getDescriptor();
           curSliceLocation        =   dd.sliceLocation;
            System.out.println("Slice Location = "+ dd.sliceLocation);
          

        // initialize first time
        if (id == null) { this.setDescriptor( curID);
           setCurrentDescriptor(curID);
        }
       if (checkHeaderInfo){
        if      ( dd.studyId == null && getStudyID() == null){ /*do nothing - continue*/}
        else if ( dd.studyId == null || getStudyID() == null){return null;}
        else if ( getStudyID().equals( dd.studyId) == false ){return null;}


        if      ( dd.seriesNumber == null && getSeriesNumber()  == null){ /*do nothing - continue*/}
        else if ( dd.seriesNumber == null ||  getSeriesNumber() == null){return null;}
        else if (getSeriesNumber().equals(dd.seriesNumber) == false ){ return null;}
       }



        } catch (NumberFormatException ex) {
             message = String.format( "Number format exception is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is:\n" +
                                      "%s",file.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           values = null;

        } catch (FileNotFoundException ex) {
            message = String.format(  "FileNotFoundException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is:\n" +
                                      "%s",file.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           values = null;
        } catch (IOException ex) {
             message = String.format( "IOException is thrown\n" +
                                      "when parsing file.\n" +
                                      "%s\n"+
                                      "Error message is:\n" +
                                      "%s",file.getPath(),ex.getMessage() ) ;

           DisplayText.popupMessage(message);
           ex.printStackTrace();
           values = null;
        }
        finally {
            
        }

     return values;
    }


   /*
    * I am assuming here the files were already checked to have .dcm extension
    *(e.g. al files are dicom)
    *
    */
   public boolean scanFileHeaders(File [] files){
    for (File file : files) {
             String message              =    null;
             DicomDecoder dd             =    new DicomDecoder(file);
   
         try {
               FileInfo  fi               =   dd.readFileInfo();
                setSeriesNumber(dd.seriesNumber);
                setStudyID(dd.studyId) ;

               // System.out.println("************");
                //System.out.println("Series Number "+ dd.seriesNumber);
                //System.out.println("StudyID "+ dd.studyId);
               if( getSeriesList().contains(dd.seriesNumber) == false){
                    getSeriesList().add(dd.seriesNumber);
               }
               if( getStudyList().contains(dd.studyId) == false){
                    getStudyList().add(dd.studyId);
               }



          }
          catch (FileNotFoundException ex) {
                 message = String.format( "FileNotFoundException is thrown\n" +
                                          "when reading header of the file.\n" +
                                          "%s\n"+
                                          "Error message is:\n" +
                                          "%s",file.getPath(),ex.getMessage() ) ;

               DisplayText.popupMessage(message);
               ex.printStackTrace();
               return false;
         }
         catch (IOException ex) {
                 message = String.format( "IOException is thrown\n" +
                                          "when reading header of the file.\n" +
                                          "%s\n"+
                                          "Error message is:\n" +
                                          "%s",file.getPath(),ex.getMessage() ) ;

               DisplayText.popupMessage(message);
               ex.printStackTrace();
               return false;
            }
            
        }

    return true;
   }
    public   boolean readAndWrite(File [] files) {
       boolean isReadAll = scanFileHeaders(files);
       if ( isReadAll == false) {return false;}
       
       if(this.getStudyList ().size() > 1 || this.getSeriesList().size() > 1){
            Dicom2ImgDialog.showDialog(this);
            if (Dicom2ImgDialog.isDialogCanceled() == true){return false;}
       }
       
        List<float[][]> allImages   =    new  ArrayList<float[][]>();
        float[][]       curImages   =    null;
        float [] values             =    null;
        this.setDescriptor(null);
        Arrays.sort(files);


         for (File file : files) {
             // this will reassign "curSliceLocation"
             values                      =    read(file, true);
             if (values  == null) { continue;}
                
            int index                    =  0;

            if (getSliceLocationList().isEmpty()){
                index           = 0;
                getSliceLocationList().add(curSliceLocation);
                numberOfSlices +=1;

            }

            else if (getSliceLocationList().contains(curSliceLocation) == false){
                int size                     = getSliceLocationList().size();
                double min                   =   getSliceLocationList().get(0);
                double max                   =   getSliceLocationList().get(size - 1);

                if      ( curSliceLocation < min) {index = 0;}
                else if ( curSliceLocation > max){ index = size;}
                else {
                    
                    index = -1 -Collections.binarySearch(getSliceLocationList(),curSliceLocation);

                }

                getSliceLocationList().add(curSliceLocation);
                numberOfSlices +=1;


           }
            else {
                Collections.sort( getSliceLocationList());
                index =  1+ getSliceLocationList().lastIndexOf(curSliceLocation);
                getSliceLocationList().add(curSliceLocation);
            
            }
           numberOfImages +=1;

   

             curImages                  =  ImageConvertHelper.populateSingleImageRP(values, getCurrentDescriptor());
             values                     =   null;

           

            allImages.add(index, curImages);
        
           //   allImages.add(curImages);

       }
        numberOfElements                    =   numberOfImages/numberOfSlices;
        this.getDescriptor().setNumberOfElements(numberOfElements );
        this.getDescriptor().setNumberOfSlices( numberOfSlices);

         List<float[][]>sortedImages   =    new  ArrayList<float[][]>(numberOfImages);

   
        for (int curElem = 0; curElem < numberOfElements ; curElem++) {
            for (int curSlice = 0;  curSlice  < numberOfSlices;  curSlice ++) {
                int newInd      =  curElem *  numberOfSlices + curSlice;
                int oldInd      =  curSlice *  numberOfElements +  curElem;
                float[][] img   =  allImages.get( oldInd);
                sortedImages.add(newInd,img );

            }

       }


        // write  binary .img file and text ifh file
        // in the curreExp/fid directory
        String newName           =   files[0].getParentFile().getName();
        if (this.getStudyID()     != null){newName += "."+this.getStudyID();}
        if (this.getSeriesNumber()!= null){newName += "."+this.getSeriesNumber();}

        boolean isWrite             =  ImageConvertHelper.writeImgFiles(newName,sortedImages , getDescriptor());


       return  isWrite;
    }



   public ImageDescriptor assignImageDescriptor(FileInfo fi){
      String str;
      ImageDescriptor  id   = new ImageDescriptor();
      str                   = fi.directory + fi.fileName;
      id.setSourceFileName(str);

      if (PackageManager.getCurrentApplication() != null){
            str = PackageManager.getCurrentApplication().getProgramName();
            id.setConversionProg(str);
       }

      id.setNumberOfRows(fi.height);
      id.setNumberOfColumns(fi.width);
      id.setNumberOfElements(fi.nImages);
      id.setScaling1(  fi.pixelWidth  );
      id.setScaling2(  fi.pixelHeight  );
      id.setScaling3(  fi.pixelDepth  );
      //id.setBigEndian(!fi.intelByteOrder);
      id.setBigEndian(true);// no matter what original file is , we overwrite it to ne BigEndian

      return id;

   }




     public ImageDescriptor getDescriptor () {
        return descriptor;
    }
     public void setDescriptor ( ImageDescriptor descriptor ) {
        this.descriptor = descriptor;
    }
      public static void main(String [] args){
        Dicom2ImgConverter converter = new Dicom2ImgConverter();
         File file = new File("/Users/apple/BayesSys/Bayes.test.data/Dicom/CR-MONO1-10-chest");
         file = new File("/Users/apple/BayesSys/Bayes.test.data/Dicom/MR-MONO2-8-16x-heart");
        //file = new File("/Users/apple/BayesSys/Bayes.test.data/Dicom/US-RGB-8-epicard");


         converter.readAndWrite(file);

      }

    public ImageDescriptor getCurrentDescriptor () {
        return curDescriptor;
    }
    public void setCurrentDescriptor ( ImageDescriptor tmpDescriptor ) {
        this.curDescriptor = tmpDescriptor;
    }

    public List<Double> getSliceLocationList () {
        return sliceLocationList;
    }
    public void setSliceLocationList ( List<Double> sliceLocationList ) {
        this.sliceLocationList = sliceLocationList;
    }

    public List<String> getSeriesList () {
        return seriesList;
    }
    public void setSeriesList ( List<String> seriesList ) {
        this.seriesList = seriesList;
    }

    public List<String> getStudyList () {
        return studyList;
    }
    public void setStudyList ( List<String> studyList ) {
        this.studyList = studyList;
    }

    public String getStudyID () {
        return studyID;
    }
    public void setStudyID ( String studyID ) {
        this.studyID = studyID;
    }

    public String getSeriesNumber () {
        return seriesNumber;
    }
    public void setSeriesNumber ( String seriesNumber ) {
        this.seriesNumber = seriesNumber;
    }
}
