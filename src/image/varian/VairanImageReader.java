/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.varian;
import bayes.DirectoryManager;
import fid.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import javax.swing.JOptionPane;


/**
 *
 * @author larry
 */



 
public class VairanImageReader implements  ProcparConstants{
    public static enum CHANEL{   Real, Imag};
    private VairanImageReader(){}
    
    
    
    public static VarianFidImage      readKSpaceFid(File dir){
        // by default kImage.isConstructed = false;
        VarianFidImage fidImage = new  VarianFidImage();
        
        // initialize procpar and Fid files
        File procparFile  = DirectoryManager.getProcparFile(dir);
        File fidFile      = DirectoryManager.getFidFile(dir);
        
        // Are all files in place?
        if ( isProcparAndFidFileExist(dir) == false ) { return  fidImage;}
        
        Procpar procpar           = new Procpar( procparFile);
        
        // Is image?
        if (procpar.isImage() == false ) {return fidImage;}
        
        //
        try{
            FileInputStream     fin =   new FileInputStream(fidFile);
            FileChannel channel     =   fin.getChannel();
            float [] images         =   readVarianFidImage(procpar, channel);
        
            fidImage.setPixels(images);
            fidImage.setProcpar(procpar);
            fidImage.updateImageDimensions(); // must not be called prior to setting procpar
            fidImage.setIsConstructed(true);

            channel.close();
            fin.close();
      } catch(IOException exp){
            exp.printStackTrace();
            return fidImage;
      }
        return fidImage;
    }
    public static boolean       isProcparAndFidFileExist(File dir){
     boolean isLoad = false;
      
     File procparFile  = DirectoryManager.getProcparFile(dir);
     File fidFile      = DirectoryManager.getFidFile(dir);
      
      if (fidFile.exists()== false)
      {
               JOptionPane.showMessageDialog(null, 
                "File "+ fidFile.getPath()+ " doesn't exist." ,
                "alert", javax.swing.JOptionPane.ERROR_MESSAGE);
                return isLoad;
                
      }
      
      if (procparFile.exists()== false)
      {
                JOptionPane.showMessageDialog(null, 
                "Procpar file doesn't exist in " + dir.getPath (),
                "alert", javax.swing.JOptionPane.ERROR_MESSAGE);
                return isLoad;
      }
      return true;
    }

    public static float []      readVarianFidImage(Procpar procpar, FileChannel channel ) throws IOException{
            float [] images                     =   null;

            System.out.println("File Organization "+ procpar.getFileOrganization());
            switch(procpar.getFileOrganization()){
                case STANDARD :     images   =   readStandardKSpaceFile(procpar, channel);
                                    break;
                case COMPRESSED :   images   =   readCompressedKSpaceFile(procpar, channel);
                                    break;
            }

        return images ;
    }
    public static void          skipBytes(FileChannel channel, int numberofBytesToSkip) throws IOException{
            long position       = channel.position();
            long newPosition    = position +   numberofBytesToSkip;
            channel.position(newPosition);
    }
    private static float []     readStandardKSpaceFile(Procpar procpar,FileChannel channel ) throws IOException{
        int nSlices                 = procpar.getNs();
        int nElements               = procpar.getNumberOfElements();
        int nPhaseEncode            = procpar.getNumberOfPhaseEncodePoints();
        int nPointsPerTrace         = procpar.getNp();
        int nSeg                    = procpar.getNseg();
        boolean isEpi               = procpar.isEpi();


        int size                    = nPointsPerTrace*nPhaseEncode* nElements*nSlices;
        float [] images             = new float[size];
        int     pos                 = 0;



        FidFileHeader fileHeader    = FidFileHeader.readFileHeader(channel );
        boolean isNavigatorOn       = procpar.isNavigatorOn();
        boolean trailingNavigator   = procpar.isTrailingNavigator();
        int     bytesPerTrace       = fileHeader.tbytes;
        int     skipBytes           = (isNavigatorOn) ?bytesPerTrace:0;
        //  boolean isConsecutiveSlices = procpar.isConsecutiveSlices();


        for (int curPhaseEncode = 0; curPhaseEncode < nPhaseEncode; curPhaseEncode++) {

            for (int curElement = 0; curElement < nElements; curElement++) {
                FidBlockHeader blockHeader = FidBlockHeader.readBlockHeader(channel );

                for (int curSeg = 0; curSeg < nSeg; curSeg++) {

                    for (int currentSlice = curSeg; currentSlice <nSlices; currentSlice+= nSeg) {
                        int curSlice    = currentSlice;


                        /*
                        // if slices are not consecutive
                        if (isConsecutiveSlices == false){
                            curSlice    =  procpar.getSliceIndex()[currentSlice] ;
                        }
                        */
                       
                        if (trailingNavigator == false){ skipBytes(channel,skipBytes ); }


                        FidData   fidData = FidData.readData(channel, fileHeader, blockHeader);

                        pos = getStartIndexWhenReadFromDisk(procpar, curSlice,curElement,curPhaseEncode,CHANEL.Real );
                        System.arraycopy(fidData.real, 0, images, pos, fidData.real.length);

                        pos = getStartIndexWhenReadFromDisk(procpar, curSlice,curElement,curPhaseEncode,CHANEL.Imag );
                        System.arraycopy(fidData.imag, 0, images, pos, fidData.imag.length);

                        if (trailingNavigator == true){ skipBytes(channel,skipBytes );  }




                }

                }

            }

        }
        if (isEpi){images  =   doReversal(images, procpar); }
        return images;
    }
    private static float []     readCompressedKSpaceFile(Procpar procpar, FileChannel channel) throws IOException{
        float [] images     =    null;
        boolean isEpi       =    procpar.isEpi();

        if (isEpi ){
              images              =    readCompressedEPIFile(procpar,  channel);
        }
        else {
              images              =    readCompressedNoneEPIFile(procpar,  channel);
        }
        return images;
    }
    private static float []     readCompressedNoneEPIFile(Procpar procpar, FileChannel channel) throws IOException{
        int nSlices                 = procpar.getNs();
        int nElements               = procpar.getNumberOfElements();
        int nPhaseEncode            = procpar.getNumberOfPhaseEncodePoints();
        int nPointsPerTrace         = procpar.getNp();
        
        // this nSegment assignment is valid only for Compressed none-EPI images
        // Typically do use procpar.getNseg() method
        int nSegment                =   nPhaseEncode;
        int nTracesPerSegment       =   nPhaseEncode/nSegment ;

        boolean isNavigatorOn       = procpar.isNavigatorOn();
        boolean trailingNavigator   = procpar.isTrailingNavigator();

        List<Integer> peTable       = procpar.getPetable();

        FidFileHeader fileHeader    = FidFileHeader.readFileHeader(channel);
        int size                    = nPointsPerTrace*nPhaseEncode* nElements*nSlices;
        float [] data               = new float[size];
        int     bytesPerTrace       = fileHeader.tbytes;
        int     skipBytes           = (isNavigatorOn) ?bytesPerTrace:0;

        for (int curElement = 0; curElement < nElements; curElement++){
            FidBlockHeader blockHeader = FidBlockHeader.readBlockHeader(channel);
              for (int curSeg= 0; curSeg < nSegment ; curSeg++){
                   for (int currentSlice = 0; currentSlice <nSlices; currentSlice++) {

                       if (trailingNavigator == false){ skipBytes(channel,  skipBytes);}
                       

                       for (int curTrace = 0; curTrace <nTracesPerSegment ; curTrace++) {
                           int curPhaseEncode =    curSeg * nTracesPerSegment + curTrace;
                             int toPhaseEncode  =    peTable.get( curPhaseEncode);
                             FidData   fidData  = FidData.readData(channel, fileHeader, blockHeader);
                              
                            int re_pos = getStartIndexWhenReadFromDisk(procpar, currentSlice,curElement,toPhaseEncode,CHANEL.Real );
                            int im_pos = getStartIndexWhenReadFromDisk(procpar, currentSlice,curElement,toPhaseEncode,CHANEL.Imag );

                       
                            System.arraycopy(fidData.real, 0,  data,re_pos,  fidData.real.length);
                            System.arraycopy(fidData.imag, 0,  data, im_pos, fidData.imag.length);

                       }
                          if (trailingNavigator == true){ skipBytes(channel,  skipBytes);}
                 }
            }
        }
        return data;
    }
    private static float []     readCompressedEPIFile(Procpar procpar, FileChannel channel) throws IOException{
        int nSlices                 = procpar.getNs();
        int nElements               = procpar.getNumberOfElements();
        int nPhaseEncode            = procpar.getNumberOfPhaseEncodePoints();
        int nPointsPerTrace         = procpar.getNp();
        int nSegment                = procpar.getNseg();
        int nTracesPreSegment       = nPhaseEncode/nSegment ;
        boolean isNavigatorOn       = procpar.isNavigatorOn();
        boolean trailingNavigator   = procpar.isTrailingNavigator();
        List<Integer> peTable       = procpar.getPetable();



        FidFileHeader fileHeader    = FidFileHeader.readFileHeader(channel);
        int size                    = nPointsPerTrace*nPhaseEncode* nElements*nSlices;
        float [] data               = new float[size];
        int     bytesPerTrace       = fileHeader.tbytes;
        int     skipBytes           = (isNavigatorOn) ?bytesPerTrace:0;

        for (int curElement = 0; curElement < nElements; curElement++){
            FidBlockHeader blockHeader = FidBlockHeader.readBlockHeader(channel);
              for (int curSeg= 0; curSeg < nSegment ; curSeg++){
                   for (int currentSlice = 0; currentSlice <nSlices; currentSlice++) {
                         if ( trailingNavigator == false){ skipBytes(channel,  skipBytes );}

                         for (int curTrace = 0; curTrace <nTracesPreSegment ; curTrace++) {
                            int curPhaseEncode  =    curSeg * nTracesPreSegment + curTrace;
                            int toPhaseEncode   =    peTable.get( curPhaseEncode);


                            FidData   fidData = FidData.readData(channel, fileHeader, blockHeader);

                            int re_pos = getStartIndexWhenReadFromDisk(procpar, currentSlice,curElement,toPhaseEncode ,CHANEL.Real );
                            int im_pos = getStartIndexWhenReadFromDisk(procpar, currentSlice,curElement,toPhaseEncode,CHANEL.Imag );

                            boolean isReverse =   (curTrace%2 == 1) ? true:false;
                            if (isReverse){
                                fidData.real   = reverseArray(fidData.real);
                                fidData.imag   = reverseArray(fidData.imag);
                            }


                            System.arraycopy(fidData.real, 0,  data,re_pos,  fidData.real.length);
                            System.arraycopy(fidData.imag, 0,  data, im_pos, fidData.imag.length);
                            
                           
                     }
                          if ( trailingNavigator == true){ skipBytes(channel,  skipBytes );}
                }
            }
        }
        return data;
    }



    public static float []      doReversal(float [] unrevesedImage, Procpar procpar){
        int nSlices                 =   procpar.getNs();
        int nElements               =   procpar.getNumberOfElements();
        int nPhaseEncode            =   procpar.getNumberOfPhaseEncodePoints();
        int nPointsPerTrace         =   procpar.getNp();
        int nReadOut                =   procpar.getNumberOfReadOutPoints();
        int size                    =   nPointsPerTrace*nPhaseEncode* nElements*nSlices;
        int nSegment                =   procpar.getNseg();

        List<Integer> peTable       =   procpar.getPetable();
        float [] images             =   new float[size];
        int     pos                 =   0;
        float [] real               =   new float[nReadOut];
        float [] imag               =   new float[nReadOut];


        for (int currentSlice = 0; currentSlice <nSlices; currentSlice++) {

            for (int curElement = 0; curElement < nElements; curElement++){

                    for (int curTrace = 0; curTrace < peTable.size(); curTrace++) {
                   
                        int fromPhaseEncode =   curTrace ;
                        int toPhaseEncode   =   peTable.get( curTrace);

                        boolean isReverse =   (curTrace%2 == 1) ? true:false;
                       
                        /* REAL CHANNEL */

                        pos =   getStartIndex(procpar, currentSlice,curElement,fromPhaseEncode,CHANEL.Real );

                        /* copy real channel */
                        System.arraycopy( unrevesedImage, pos, real, 0, real.length);

                        /* reverse real channel if neccessary */
                        if (isReverse){
                             real    = reverseArray(real);
                        }
                        /* copy real channel to final array of pixels */
                        pos     = getStartIndex(procpar, currentSlice,curElement,toPhaseEncode,CHANEL.Real );
                        System.arraycopy(real, 0,  images, pos, real.length);



                        /*IMAG CHANNEL */

                        pos = getStartIndex(procpar, currentSlice,curElement,fromPhaseEncode,CHANEL.Imag );

                        /* copy imag channel */
                        System.arraycopy(unrevesedImage, pos, imag, 0, imag.length);

                         /* reverse imag channel if neccessary */
                        if (isReverse){
                             imag    = reverseArray(imag);
                        }

                        /* copy imag channel to final array of pixels */
                        pos     = getStartIndex(procpar, currentSlice,curElement,toPhaseEncode,CHANEL.Imag );
                        System.arraycopy(imag, 0,  images, pos, imag.length);


 
                }
                        
                    

            }
        }
        return images;

    }

    public static float []      reverseArray(float [] array){
        int last = array.length -1;
        
        for (int left=0,  right = last; left < right; left++, right--) {
            float temp      = array[left]; 
            array[left]     = array[right]; 
            array[right]    = temp;
        }
        return array;
    }


    public static int           getStartIndex(Procpar aProcpar, int curSlice, int curElement, 
                                int curPhaseEncode,  CHANEL chanel){
        int index                   =   0;
        int nPointsPerTrace         =   aProcpar.getNp();
        int nElements               =   aProcpar.getNumberOfElements();
        int nPhaseEncode            =   aProcpar.getNumberOfPhaseEncodePoints();
        int nReadOut                =   nPointsPerTrace/2;
        
        
        int nPointsInPhaseEncode    =   nPointsPerTrace;
        int nPointsInOneElement     =   nPhaseEncode*nPointsInPhaseEncode ;
        int nPointsInOneSlice       =   nElements*nPointsInOneElement  ;

        
        index   =   curSlice        *   nPointsInOneSlice + 
                    curElement      *   nPointsInOneElement+
                    curPhaseEncode  *   nPointsInPhaseEncode;
        
        
        // do correction based whether data is real or imaginary
        switch (chanel){
            case Real : index +=    0;                  break;
            case Imag : index +=    nReadOut;           break;
        }
     
        return index;
    }

    public static int           getStartIndexWhenReadFromDisk(Procpar aProcpar, int curSlice, int curElement,
                                int curPhaseEncode,  CHANEL chanel){
        int index                   =   0;
        int nPointsPerTrace         =   aProcpar.getNp();
        int nElements               =   aProcpar.getNumberOfElements();
        int nPhaseEncode            =   aProcpar.getNumberOfPhaseEncodePoints();
        int nReadOut                =   nPointsPerTrace/2;


        int nPointsInPhaseEncode    =   nPointsPerTrace;
        int nPointsInOneElement     =   nPhaseEncode*nPointsInPhaseEncode ;
        int nPointsInOneSlice       =   nElements*nPointsInOneElement  ;

        int  []sliceOrder           =   aProcpar.getSliceIndex();

        if (  aProcpar.isConsecutiveSlices() == false){
            curSlice                    =   sliceOrder[curSlice];
        }


        index   =   curSlice        *   nPointsInOneSlice +
                    curElement      *   nPointsInOneElement+
                    curPhaseEncode  *   nPointsInPhaseEncode;


        // do correction based whether data is real or imaginary
        switch (chanel){
            case Real : index +=    0;                  break;
            case Imag : index +=    nReadOut;           break;
        }

        return index;
    }

    

}
