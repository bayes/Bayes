/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.raw;

import image.ImageDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import load.ImageConvertHelper;

/**
 *
 * @author apple
 */
public class BinaryReader {

    public List  <float [][]> getMagnImages() {
        return magnImages;
    }

    public void setMagnImages(List  <float [][]> magnImages) {
        this.magnImages = magnImages;
    }

    public void setRealImages(List  <float [][]> realImages) {
        this.realImages = realImages;
    }

    public void setImagImages(List  <float [][]> imagImages) {
        this.imagImages = imagImages;
    }

    

   

    
    
    public static enum BINARY_TYPE {
        GRAY_UNSIGNED_8BIT_INT      (   "8-bit Gray Signed Int"  , 1),
        GRAY_SIGNED_16BIT_INT       (   "16-bit Gray Signed Int"  , 2),
        GRAY_SIGNED_32BIT_INT       (   "32-bit Gray Signed Int"  , 4),
        GRAY_SIGNED_32BIT_FLOAT     (   "32-bit Gray Signed Float"  , 4),
        GRAY_SIGNED_64BIT_FLOAT     (   "64-bit Gray Signed Float"  , 8),
        GRAY_UNSIGNED_16BIT         (   "16-bit Gray Signed"  , 2),
        GRAY_UBSIGNED_32BIT         (   "32-bit Gray Signed"  , 4);

        public boolean isValid(){
            if (this ==  GRAY_SIGNED_16BIT_INT ){return true;}
            else if(this == GRAY_SIGNED_32BIT_INT ){return true;}
            else if(this == GRAY_SIGNED_32BIT_FLOAT ){return true;}
            else if(this == GRAY_SIGNED_64BIT_FLOAT ){return true;}
            else{
                return false;
            }
        }
        private final String name;
        private int bytes  ;
        BINARY_TYPE(String aname, int abit) {
            this.name           = aname;
            this.bytes           =   abit;
        }
        public String getName() {return name;}
        @Override
        public String toString() {return name;}
        public static BINARY_TYPE getTypeByName(String aName)
                throws IllegalArgumentException{

            for (BINARY_TYPE  unit :BINARY_TYPE .values()) {
                    if(aName.equalsIgnoreCase(unit.name)){return unit;}
            }
             throw new IllegalArgumentException();

        }
       public static List<String> getImageTypeList () {
           List<String> units = new ArrayList<String>();
        for (BINARY_TYPE  unit : BINARY_TYPE .values()) {
            units.add(unit.getName());
        }
        return units;
    }

        public int getBytes() {
            return bytes;
        }
        public void setBytes(int bits) {
            this.bytes = bits;
        }
    }
    public static enum MAPPING_TYPE {
        NONE      (  ),
        LINEAR    ();

      
        MAPPING_TYPE(){}
     

    }
   



    private MAPPING_TYPE mapping_TYPE                   = MAPPING_TYPE.NONE;
    private BinaryReader.BINARY_TYPE  imageType         = BinaryReader.BINARY_TYPE.GRAY_SIGNED_32BIT_INT;
    private int firstImageOffsetInBytes                 = 0;
    private int gapBetweenImagesInBytes                 = 0;
    private int width                                   = 1;
    private int height                                  = 1;
    private int numberSlices                            = 1;
    private int numberOfElements                        = 1;
    private int totalNumberOfImages                     =  1;
    private ByteOrder byteOrder                         =  ByteOrder.BIG_ENDIAN;
    private boolean loaded                              =  false;
    private String  errorMessage                        =  null;
    private List  <float [][]> magnImages               =  new ArrayList <float [][]>();
    private List  <float [][]> realImages               =  new ArrayList <float [][]>();
    private List  <float [][]> imagImages               =  new ArrayList <float [][]>();
    private float linearMapOffset                       =   0f;
    private float linearMapSlope                        =   1f;

    private File    sourseFile                          =  null;
    private double imageHeightCm                        =   0;
    private double imageWidthCm                         =   0;
    private boolean writeImageDimensionLength           =   false;
    private boolean gapBeforeImages                     =   false;
    private boolean flipWidthAndHeight                  =   false;
    private boolean complex                             =   false;
    private boolean realImagesFirst                     =   true;
    private boolean makeMagnitudeImages                 =   true;
    private boolean innerSliceLoop                      =   true;
    private String loadError                            =   "Unknown";
    private boolean debug                               =   true;
     public boolean isMergingEquivalent(BinaryReader reader){
      return isMergingEquivalent(reader, false);
    
    }
    public boolean isMergingEquivalent(BinaryReader reader, boolean skipElementCheck){
        boolean out         =   false;
        try{
             if (this.width != reader.width){
                 if (debug){
                     System.out.println("Non-matching image width:");
                     System.out.println("Reader1.width "+ this.width );
                     System.out.println("Reader2.width "+ reader.width );
                 }
                 out =   false;
             }
             else if( this.height != reader.height){
                  if (debug){
                     System.out.println("Non-matching image height:");
                     System.out.println("Reader1.height "+ this.height);
                     System.out.println("Reader2.height "+ reader.height );
                 }
                         out =   false;
             }
             else if (skipElementCheck == false && this.numberOfElements != reader.numberOfElements){
                 if (debug){
                     System.out.println("Non-matching image element size:");
                     System.out.println("Reader1.element "+ this.numberOfElements );
                     System.out.println("Reader2.element "+ reader.numberOfElements  );
                 }
                           out  = false;
             }
             else if(this.numberSlices!= reader.numberSlices){
                 if (debug){
                     System.out.println("Non-matching image slice size:");
                     System.out.println("Reader1.slice "+ this.numberSlices );
                     System.out.println("Reader2.slice "+ reader.numberSlices  );
                 }
                           out  = false;
             }
             else if(getImageType()   != reader.getImageType()){
                  if (debug){
                     System.out.println("Non-matching image type:");
                     System.out.println("Reader1.type "+ this.getImageType() );
                     System.out.println("Reader2.type "+ reader.getImageType() );
                 }
                            out  = false;
             }
             else if(isComplex()   != reader.isComplex()){
                   if (debug){
                     System.out.println("Non-matching image complex type:");
                     System.out.println("Reader1.complex "+ this.isComplex() );
                     System.out.println("Reader2.complex"+ reader.isComplex() );
                 }
                 
                            out  = false;
             }
             else if(getMapping_TYPE()   != reader.getMapping_TYPE()){
                   if (debug){
                     System.out.println("Non-matching image mapping type:");
                     System.out.println("Reader1.map "+ this.getMapping_TYPE() );
                     System.out.println("Reader2.map"+ reader.getMapping_TYPE() );
                 }
                            out  = false;
             }
             else{
                            out = true;        
             }
             
             
        }
        catch (Exception e){
            e.printStackTrace();
            out                 =   false;
        }
       
        return out;
    
    }
     public boolean mergeByElements(BinaryReader reader){
        boolean out         =   false;
        try{
                getMagnImages().addAll(reader.getMagnImages());
                getRealImages().addAll(reader.getRealImages());
                getImagImages().addAll(reader.getImagImages());
                numberOfElements   = numberOfElements + reader.numberOfElements;
                out               = true;
        }
        catch (Exception e){
            e.printStackTrace();
            out                 =   false;
        }
       
        return out;
    
    }
     public void  read(File file){
         FileInputStream     fin                        =   null;
         FileChannel channel                            =   null;
         setSourseFile(file);


         try {
             if (imageType.isValid() == false){
                    String error = String.format(  "Unsupported file format: %s." ,imageType.toString() );
                    throw new Exception(error);
                           
             }
             fin                                    =   new FileInputStream(file);
             channel                                =   fin.getChannel();

             int numberOfImages                     =   getNumberOfElements()*getNumberOfSlices();
             if (this.isComplex())  {
                    numberOfImages                  =   2 * numberOfImages   ;
             }

             int sizeInBytes                        =   fin.available();
             int offset                             =   getFirstImageOffsetInBytes();
             int bufferSize                         =   sizeInBytes - offset ;
             boolean isGapBeforeImages              =   isGapBeforeImages() ;
             boolean isComplex                      =   isComplex();

             channel.position(offset   );
             ByteBuffer buffer                      =   ByteBuffer.allocateDirect(bufferSize );
             buffer.order( byteOrder);
             channel.read(buffer);
             buffer.rewind();

           

             //*
            System.out.println("Reading Raw image");
            System.out.println("File Size In Bytes          =    "   +   sizeInBytes      );


           
            for (int curImage = 0; curImage < numberOfImages; curImage++) {
                 // skip gap
                 if (isGapBeforeImages ){ skipGap (channel);}

               
                float   [][] pix    =  new float [width ][height ];

                if (this.isFlipWidthAndHeight()){
                 for (int j = 0; j < width; j++) {
                   for (int i = 0; i < height; i++) {
                       float val           =  readToFloat(buffer, imageType);

                        // undo mapping
                        val                 =  getMappedValue(val);
                        pix[j][i]           =   val;
                    }
                  }
                }
                else{
                  for (int i = 0; i < height; i++) {
                     for (int j = 0; j < width; j++) {
                        float val           =  readToFloat(buffer, imageType);
                        val                 =  getMappedValue(val);
                        pix[j][i]           =   val;
                     }
                   }
                 }

                   // skip gap
                   if (isGapBeforeImages  == false){ skipGap (channel);}

                  

                   if (isComplex){
                     if(isRealImagesFirst()){
                         if (curImage < numberOfImages/2){
                            getRealImages().add(pix);
                         }
                         else{
                             getImagImages().add(pix);
                         }
                     }
                     else{
                        if (numberOfImages%2 == 0){
                            getRealImages().add(pix);
                         }
                         else{
                             getImagImages().add(pix);
                         }
                     }

                   }
                   else{
                      getMagnImages().add(pix);
                   }
                  

             }

            if (isComplex() && makeMagnitudeImages){
                 makeMagnitudeImages();
            }
           
            /*
             * IF images are not contigious in slice dimension, 
             * properly reorder them to default (contagious in slice)
             * 
             */
          reshuffleImagesIfNeeded();
            
            setLoaded(true);


        }
        catch (Exception ex) {
           ex.printStackTrace();
           String emsg  =   ex.getMessage();
           if ( emsg != null  &&  emsg.isEmpty() == false ){
                    setErrorMessage(ex.getMessage() );
           }
        }
        finally{
            try {
              
                fin.close();
                channel.close();
            } catch (IOException ex) { }

        }

    }
    private  float readToFloat(ByteBuffer buffer,  BinaryReader.BINARY_TYPE  itype){
         float val                  =   Float.NaN;
          try {
               switch (itype){
                            case GRAY_SIGNED_16BIT_INT:
                                 val      =   buffer.getShort();
                                 break;
                            case GRAY_SIGNED_32BIT_INT:
                                 val      =   buffer.getInt();
                                 break;
                            case GRAY_SIGNED_32BIT_FLOAT:
                                 val      =   buffer.getFloat();
                                 break;
                            case GRAY_SIGNED_64BIT_FLOAT:
                                 val      =   (float)buffer.getDouble();
                                 break;    
                            default:  val = 0f;


                  }
        }
        catch (Exception e){e.printStackTrace();}
        return val;

    }
     public void reshuffleImagesIfNeeded(){
        boolean reshuffle       = !this.isInnerSlicesLoop();
        if (reshuffle){
            reshuffleImages(this.getRealImages());
            reshuffleImages(this.getImagImages());
            reshuffleImages(this.getMagnImages());
        }
    }
     public void reshuffleImagesIfNeeded(List <float [][]> images){
        boolean reshuffle       = !this.isInnerSlicesLoop();
        if (reshuffle){reshuffleImages(images);}
    }
     public void reshuffleImages(List <float [][]> images){
        if (images == null || images.isEmpty()){return;}
         System.out.println("Reshuffling images");
        int nslices             =  this.getNumberOfSlices();
        int nelements           =  this.getNumberOfElements();
        
        
        List <float [][]> hold  =   new ArrayList <float [][]>();
        int curSlice            =   0;
        int curElem             =   0;
        for (int i = 0; i <images.size(); i++) {
             int pos       =  nelements * curSlice   + curElem;
             float [][] curImage            =   images.get( pos);
             hold.add(i, curImage);
            
             curSlice           = curSlice + 1;
             if (curSlice ==nslices){
                 curSlice           =   0;
                 curElem            =   curElem + 1;
             }
        }
        
        images.clear();
        images.addAll(hold);
                
        
    }
    public static float [][] flip(float [][] in){
        int dim1            =   in.length;
        int dim2            =   in[0].length;

        float [][] out      = new float [dim2 ][dim1 ];
        for (int in1 = 0; in1 < dim1 ; in1++) {
            for (int in2 = 0; in2 < dim2; in2++) {
               out [in2][in1] = in [in1][in2];

            }

        }
        return out;
    }
    public void makeMagnitudeImages(){
        getMagnImages().clear();
        for (int i = 0; i < getRealImages().size(); i++) {
            float [][] real             =   getRealImages().get(i);
            float [][] imag             =   getImagImages().get(i);
            float [][] magn             =   new float [width][height];

            for (int w = 0; w < width; w++) {
                 for (int h = 0; h < magn.length; h++) {
                        float re        =   real[w][h];
                        float im        =   imag[w][h];
                        float mg        =   (float)Math.hypot(re, im);
                        magn[w][h]      =   mg;

                    }
            }

            getMagnImages().add(magn);
        }

    }

   public float getMappedValue(float unmappedValue){
             float mappedValue      =    unmappedValue;
             float offset           =   getLinearMapOffset();
             float slope            =   getLinearMapSlope();
             switch (mapping_TYPE  ){

                 case LINEAR:

                   //  mappedValue       =  offset  + (1.0f/slope) * unmappedValue;
                     break;

             }
             

             return mappedValue;
        }
   
    public boolean writeImages(){
       
        boolean isWrite                 =   false;
        try{
            File srcfile                    =   getSourseFile();
            String srcname                  =   srcfile .getName();
            String reName                   =   srcname + "_REAL";
            String imName                   =   srcname + "_IMAG";
            String mgName                   =   srcname + "_MAGNITUDE";
            ImageDescriptor  id             =   getImageDescriptor();

            if(this.isComplex()){
                 isWrite  =   ImageConvertHelper.writeImgFiles( reName, getRealImages(),   id);
                 isWrite  =   ImageConvertHelper.writeImgFiles( imName, getImagImages(),   id);
                 isWrite  =   ImageConvertHelper.writeImgFiles( mgName, getMagnImages(),   id);

            }
            else{
                isWrite     =  ImageConvertHelper.writeImgFiles(srcname, getMagnImages(),id);
            }

            getRealImages().clear();
            getImagImages().clear();
            getMagnitudeImages().clear();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{


            return isWrite;
        }

        
    }
    public  ImageDescriptor  getImageDescriptor(){
         ImageDescriptor  descriptor     =   new ImageDescriptor();

         descriptor.setSourceFileName(getSourseFile().getAbsolutePath());
         descriptor.setBytesPerPoint(4); 
         descriptor.setNumberOfColumns(getWidth());
         descriptor.setNumberOfRows(getHeight());
         descriptor.setNumberOfSlices(getNumberOfSlices());
         descriptor.setNumberOfElements(getNumberOfElements());

         if (isWriteImageDimensions()){
            double tmp  =    getImageWidthCm()/getWidth() * 10;
            descriptor.setScaling1(tmp);

            tmp         =    getImageHeightCm()/getHeight() * 10;
            descriptor.setScaling2(tmp);

         }
        else{
            descriptor.setScaling1(1);
            descriptor.setScaling2(1);
            descriptor.setScaling3(1);

        }

         


         return  descriptor;
     }
    private void skipGap(FileChannel channel) throws IOException{
            long curPos                 = channel.position();
            long nextPos                = curPos + getGapBetweenImagesInBytes();
            channel.position(nextPos);
                        
    }


    public List<float[][]> getMagnitudeImages() {
        return getMagnImages();
    }
    public List<float[][]> getRealImages() {
        return realImages;
    }
    public List<float[][]> getImagImages() {
        return imagImages;
    }


    public int getFirstImageOffsetInBytes() {
        return firstImageOffsetInBytes;
    }
    public void setFirstImageOffsetInBytes(int firstImageOffsetInBytes) {
        this.firstImageOffsetInBytes = firstImageOffsetInBytes;
    }

    public int getGapBetweenImagesInBytes() {
        return gapBetweenImagesInBytes;
    }
    public void setGapBetweenImagesInBytes(int gapBetweenImagesInBytes) {
        this.gapBetweenImagesInBytes = gapBetweenImagesInBytes;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public File getSourseFile() {
        return sourseFile;
    }
    public void setSourseFile(File sourseFile) {
        this.sourseFile = sourseFile;
    }

  

    public BINARY_TYPE getImageType() {
        return imageType;
    }
    public void setImageType(BINARY_TYPE imageType) {
        this.imageType = imageType;
    }

    public MAPPING_TYPE getMapping_TYPE() {
        return mapping_TYPE;
    }
    public void setMapping_TYPE(MAPPING_TYPE mapping_TYPE) {
        this.mapping_TYPE = mapping_TYPE;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public double getImageHeightCm() {
        return imageHeightCm;
    }
    public void setImageHeightCm(double imageHeightCm) {
        this.imageHeightCm = imageHeightCm;
    }

    public double getImageWidthCm() {
        return imageWidthCm;
    }
    public void setImageWidthCm(double imageLengthCm) {
        this.imageWidthCm = imageLengthCm;
    }

    public boolean isWriteImageDimensions() {
        return writeImageDimensionLength;
    }
    public void setWriteImageDimensions(boolean writeImageDimensionength) {
        this.writeImageDimensionLength = writeImageDimensionength;
    }

    public int getNumberOfSlices() {
        return numberSlices;
    }
    public void setNumberOfSlices(int numberSlices) {
        this.numberSlices = numberSlices;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }


    public boolean isGapBeforeImages() {
        return gapBeforeImages;
    }
    public void setGapBeforeImages(boolean gapBeforeImages) {
        this.gapBeforeImages = gapBeforeImages;
    }

    public boolean isFlipWidthAndHeight() {
        return flipWidthAndHeight;
    }
    public void setFlipWidthAndHeight(boolean flipWidthAndHeight) {
        this.flipWidthAndHeight = flipWidthAndHeight;
    }

    public String getLoadError() {
        return loadError;
    }
    public void setLoadError(String loadError) {
        this.loadError = loadError;
    }

     public boolean isComplex() {
        return complex;
    }
    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public boolean isRealImagesFirst() {
        return realImagesFirst;
    }
    public void setRealImagesFirst(boolean realImagesFirst) {
        this.realImagesFirst = realImagesFirst;
    }

    public boolean isMakeMagnitudeImages() {
        return makeMagnitudeImages;
    }
    public void setMakeMagnitudeImages(boolean makeMagnitudeImages) {
        this.makeMagnitudeImages = makeMagnitudeImages;
    }

    public int getTotalNumberOfImages() {
        return totalNumberOfImages;
    }
    public void setTotalNumberOfImages(int totalNumberOfImages) {
        this.totalNumberOfImages = totalNumberOfImages;
    }


    public float getLinearMapOffset() {
        return linearMapOffset;
    }
    public void setLinearMapOffset(float linearMapOffset) {
        this.linearMapOffset = linearMapOffset;
    }

    public float getLinearMapSlope() {
        return linearMapSlope;
    }
    public void setLinearMapSlope(float linearMapSlope) {
        this.linearMapSlope = linearMapSlope;
    }
    
    public boolean isInnerSlicesLoop() {
        return innerSliceLoop;
    }
    public void setInnerSliceLoop(boolean slicesAreContigious) {
        this.innerSliceLoop = slicesAreContigious;
    }
}

