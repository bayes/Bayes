/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.nifti;

import image.raw.BinaryReader;
import static image.raw.BinaryReader.*;
import image.raw.BinaryReader.BINARY_TYPE;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import utilities.DisplayText;

/**
 *
 * @author apple
 */
public class NiftiLoader {
    private BinaryReader binReader   = null;
    public boolean loadNifti(File file){
       boolean success = false;
       	Nifti1Dataset nds = new Nifti1Dataset(file.getAbsolutePath());
        binReader          = new   BinaryReader();
        try {
                nds.readHeader();
                nds.printHeader();
                
                // set byte order
                ByteOrder byteOrder = (nds.big_endian) ? ByteOrder.BIG_ENDIAN :ByteOrder.LITTLE_ENDIAN ;
                binReader.setByteOrder(byteOrder);
                
                binReader.setComplex(nds.isComplex());
                binReader.setGapBeforeImages(false);
                binReader.setGapBetweenImagesInBytes(0);
                binReader.setFirstImageOffsetInBytes(352);
                binReader.setHeight(nds.XDIM);
                binReader.setWidth(nds.YDIM);
                binReader.setNumberOfSlices(nds.ZDIM);
                binReader.setNumberOfElements(1);
                binReader.setSourseFile(file);
                
                BINARY_TYPE btype = null;
                switch(nds.datatype){
                    case Nifti1Dataset.NIFTI_TYPE_INT32: btype = BINARY_TYPE.GRAY_SIGNED_32BIT_INT; break;
                    case Nifti1Dataset.NIFTI_TYPE_INT16: btype = BINARY_TYPE.GRAY_SIGNED_16BIT_INT; break;
                    case Nifti1Dataset.NIFTI_TYPE_FLOAT32: btype = BINARY_TYPE.GRAY_SIGNED_32BIT_FLOAT; break;
                }
                binReader.setImageType(btype);
                
             binReader.read(file);
             
             success = binReader.isLoaded();
             if ( success == false){
                    String errror = String.format(
                            "Error encountered while trying to read nifti file\n"
                            + "%s\n"
                            + "Error  description is: %s\n."
                            + "Exiting... "
                            ,
                            file.getPath(),
                             binReader.getErrorMessage());
                        DisplayText.popupErrorMessage(errror);
                    return success;
              }
             
                  
                
        }
      
        catch (Exception ex) {
                System.out.println("Error reafding  "+ file.getAbsolutePath()+": "+ex.getMessage());
        }

        return success;
   }

    public BinaryReader getBinReader() {
        return binReader;
    }

    public void setBinReader(BinaryReader binReader) {
        this.binReader = binReader;
    }
}
