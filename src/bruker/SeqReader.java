/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

import image.raw.BinaryReader.BINARY_TYPE;
import image.raw.BinaryReader;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Scanner;
import utilities.DisplayText;
import utilities.IO;

/**
 *
 * @author apple
 */
public class SeqReader extends  BinaryReader implements BrukerConstants {
     int zsize                          =   1;
     float  mapping_slope               =   1;
     float  mapping_offset              =   1;
     public static BinaryReader  creatBinaryReader(File file){
        SeqReader seqreader             =  new SeqReader ();
        try{
            seqreader.initializeSeqReader(file);
        }
        catch(Exception e){e.printStackTrace();}
        finally{
            return seqreader ;
        }




    }
    public void initializeSeqReader(File file){
        try{
            	
 
             String   directory   = file.getParent();
             readParamsFiles(directory);

             setSourseFile(file);
             setFirstImageOffsetInBytes(0);
             setGapBetweenImagesInBytes(0);
               
               
             setMapping_TYPE(MAPPING_TYPE.LINEAR);
             setLinearMapOffset(mapping_offset);
             setLinearMapSlope(mapping_slope);

             /*
            if (isComplex()){
                     setNumberOfSlices(zsize/2);
            }
            else{
                     setNumberOfSlices(zsize);
            }
              * 
              */
               
                
            setLoaded(true);
        }
        catch (Exception e){
            e.printStackTrace();
             DisplayText.popupErrorMessage( e.getMessage());
        }

        finally{
        }

    }
    public boolean readParamsFiles(String directory){
        boolean out             =    false;
        
        try{
               
                File reco                   = new File(directory,   BrukerConstants.RECO_FILENAME);
                File d3proc                 = new File(directory,   BrukerConstants.D3PROC_FILENAME);
                File visu_pars              = new File(directory,   BrukerConstants.VISU_PARS_FILENAME);
                
                boolean reco_read      =  read_reco(reco);
                if (reco_read  == false){throw new IllegalParameterFileException(reco );}
                
                boolean d3proc_read    =  read_d3proc(d3proc);
                if (d3proc_read  == false){throw new IllegalParameterFileException(d3proc );}
                
                boolean visu_read         =  read_visu(visu_pars );
                if (visu_read == false){throw new IllegalParameterFileException(visu_pars  );}
                    
                out                     =   true;
                
        }
         catch(IllegalParameterFileException e){
            File errorfile              =   e.getFile();
            String error              = "";
            if (errorfile.exists() == false){
                 error   = String.format(
                                "Error encountered while trying to open Bruker seq file\n"
                            +   "Parameter file %s \n"
                            +   "can not be found.\n"
                            +   "Exiting..."
                            ,
                            errorfile.getPath() );
            }
            else{
                if (errorfile.exists() == false){
                 error   = String.format(
                                "Error encountered while trying to open Bruker seq file\n"
                            +   "Errors encountered while attempting to read parameter file \n"
                            +   " %s .\n"
                            +   "Exiting..."
                            ,
                            errorfile.getPath() );
            }
            }
           

                    
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            return out;
        }
    
    }


    private boolean read_reco(File reco) {
        boolean read            =   false;
        String FileLine;
        int index;


        // set defaults
        setComplex(false);
        setByteOrder(ByteOrder.BIG_ENDIAN);
        setImageType(BINARY_TYPE.GRAY_SIGNED_32BIT_FLOAT);
        setWriteImageDimensions(false);

	try {
             String context         =   IO.readFileToString(reco);
             Scanner scanner        =   new Scanner(context);
             while (scanner.hasNextLine()) {
                 FileLine           =   scanner.nextLine();
                 index              = FileLine.indexOf("=");

                if (FileLine.startsWith("##$RECO_wordtype=")) {
                    if (FileLine.substring(index+1).equalsIgnoreCase("_8bit_unsgn_int")){
                        setImageType(BINARY_TYPE.GRAY_UNSIGNED_8BIT_INT);
                    }
                    else if(FileLine.substring(index + 1).equalsIgnoreCase("_16bit_sgn_int")){
                        setImageType(BINARY_TYPE.GRAY_SIGNED_16BIT_INT);
                    }
                    else if(FileLine.substring(index+1).equalsIgnoreCase("_32bit_sgn_int")) {
                          setImageType(BINARY_TYPE.GRAY_SIGNED_32BIT_INT);
                    }
                    

                  }
                else if (FileLine.startsWith( "##$RECO_image_type=")) {
                  if (FileLine.substring(index+1).equalsIgnoreCase("COMPLEX_IMAGE")){
                    setComplex(true);
                  }
                }
                else if (FileLine.startsWith("##$RECO_byte_order=")) {
                     if (FileLine.substring(index+1).equalsIgnoreCase("bigendian"))       {
                       setByteOrder(ByteOrder.BIG_ENDIAN);
                     }
                     if (FileLine.substring(index+1).equalsIgnoreCase("littleendian"))  {
                        setByteOrder(ByteOrder.LITTLE_ENDIAN);
                     }

                }
                else if (FileLine.startsWith("##$RECO_fov=") ){
                     if (scanner.hasNextDouble()){
                        setImageWidthCm(scanner.nextDouble());
                     }
                     if (scanner.hasNextDouble()){
                        setImageHeightCm(scanner.nextDouble());
                     }
                     setWriteImageDimensions(true);
                }
                else if (FileLine.startsWith("##$RECO_map_slope=") ){
                    if (scanner.hasNextFloat()){
                        mapping_slope  = scanner.nextFloat();
                    }
                }
                else if (FileLine.startsWith("##$RECO_map_offset=") ){
                    if (scanner.hasNextFloat()){
                        mapping_offset  = scanner.nextFloat();
                    }
                }
                else if (FileLine.startsWith("##$RECO_ft_size=") ){
                    String [] dims = scanner.nextLine().trim().split("\\s");
                    if (dims.length == 3){
                        int val = Integer.parseInt(dims[2]);
                        setNumberOfSlices(val);
                    }
                }

            
            }
            scanner.close();
            read    = true;
        }
	catch (Exception e) {
            e.printStackTrace();
	}
        finally{
            return read;
        }
    }
    private boolean read_d3proc(File d3proc) {
        boolean read            =   false;
        String FileLine;
        int index;
 
	try {
          RandomAccessFile f              = new RandomAccessFile( d3proc, "r");
           while ((FileLine = f.readLine()) != null) {
              index = FileLine.indexOf("=");
	      if (FileLine.startsWith("##$IM_SIX=")) {
 	      	 int xsize = getiValue(FileLine.substring(index+1).trim());
                 setWidth(xsize);
                  
	      }
	      if (FileLine.startsWith("##$IM_SIY=")) {
 	      	 int ysize = getiValue(FileLine.substring(index+1).trim());
                  setHeight(ysize);
	      }
	      if (FileLine.startsWith("##$IM_SIZ=")) {
 	      	 zsize = getiValue(FileLine.substring(index+1).trim());

	      }
            }
                read            =   true;
	}
	catch (Exception e) {
            e.printStackTrace();
	}
        finally{
            return read;
        }

    }
    private boolean read_visu(File read_visu) {
        boolean read            =   false;
        String  ECHO_DIM        =   "FG_ECHO";
        String  FGCOMPLEX       =   " FG_COMPLEX";
        String  SLICE_DIM       =   "FG_SLICE";
        String FileLine;
 
	try {
          RandomAccessFile f              = new RandomAccessFile( read_visu, "r");
           while ((FileLine = f.readLine()) != null) {
               if (FileLine.startsWith("##$VisuFGOrderDesc=")){
                   
                  
                   String line          =   f.readLine();
                   /* Typical next line is pf the form:
                    *
                    * (9, <FG_ECHO>, <>, 0, 1) (13, <FG_SLICE>, <>, 1, 2)
                    * 
                    */
                   int curOpenParentIndex     = 0;
                   int curCloseParentIndex    = 0;
                   boolean loopOrderSet       = false;
                   while (line.indexOf("(", curCloseParentIndex) >= 0){
                      curOpenParentIndex        =   line.indexOf("(",curCloseParentIndex);
                      curCloseParentIndex       =   line.indexOf(")", curOpenParentIndex);
                      if ( curCloseParentIndex < 0){
                          curCloseParentIndex   =   line.length()-1;
                      }
                      String parenthesis        =   line.substring(curOpenParentIndex + 1,curCloseParentIndex);
                      int curCommaIndex         =   parenthesis.indexOf(",");
                      String dimStr             =   parenthesis.substring(0,curCommaIndex);
                      int dim                   =   Integer.valueOf(dimStr.trim());
                      
                      if (parenthesis.contains(SLICE_DIM )){
                          this.setNumberOfSlices(dim);
                          if ( loopOrderSet == false){
                              this.setInnerSliceLoop(false);
                          }
                      }
                      else if (parenthesis.contains(ECHO_DIM)){
                          this.setNumberOfElements(dim);
                          if ( loopOrderSet == false){
                              this.setInnerSliceLoop(true);
                          }
                      }
                   }
                  
               }
              
             
	   
            }
                read            =   true;
	}
	catch (Exception e) {
            e.printStackTrace();
	}
        finally{
            return read;
        }

    }

    double getdValue(String theText) {
       Double d = new Double(theText);
       return d.doubleValue();
    }
    int getiValue(String theText) {
        Integer  d = new Integer(theText);
        return d.intValue();
    }
    
    
    
    //***********************************************
// Custom exception class that descends from Java's Exception class.
class IllegalParameterFileException extends Exception
{
  String mistake;
  File file;
//----------------------------------------------
// Default constructor - initializes instance variable to unknown
  public IllegalParameterFileException()
  {
    super();             // call superclass constructor
    mistake = "unknown";
  }
  
//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.
  public IllegalParameterFileException(String err)
  {
    super(err);     // call super class constructor
    mistake = err;  // save message
  }
  //-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.
  public IllegalParameterFileException(File afile)
  {
   file = afile;  // save message
  }
  
//------------------------------------------------  
// public method, callable by exception catcher. It returns the error message.
  public String getError()
  {
    return mistake;
  }
  //------------------------------------------------  
// public method, callable by exception catcher. It returns the error file.
  public File getFile()
  {
    return file;
  }
}
  
}
