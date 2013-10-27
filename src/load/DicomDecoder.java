/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package load;
import java.io.*;
import java.util.*;
import ij.io.*;
import ij.*;
import ij.process.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author apple
 */
public class DicomDecoder {

    private static final int PIXEL_REPRESENTATION   = 0x00280103;
    private static final int TRANSFER_SYNTAX_UID    = 0x00020010;
    private static final int SLICE_THICKNESS        = 0x00180050;
    private static final int SLICE_SPACING          = 0x00180088;
    private static final int SAMPLES_PER_PIXEL      = 0x00280002;
    private static final int PHOTOMETRIC_INTERPRETATION = 0x00280004;
    private static final int PLANAR_CONFIGURATION   = 0x00280006;
    private static final int NUMBER_OF_FRAMES       = 0x00280008;
    private static final int ROWS                   = 0x00280010;
    private static final int COLUMNS                = 0x00280011;
    private static final int PIXEL_SPACING          = 0x00280030;
    private static final int BITS_ALLOCATED         = 0x00280100;
    private static final int WINDOW_CENTER          = 0x00281050;
    private static final int WINDOW_WIDTH           = 0x00281051;
    private static final int RESCALE_INTERCEPT      = 0x00281052;
    private static final int RESCALE_SLOPE          = 0x00281053;
    private static final int RED_PALETTE            = 0x00281201;
    private static final int GREEN_PALETTE          = 0x00281202;
    private static final int BLUE_PALETTE           = 0x00281203;
    private static final int ICON_IMAGE_SEQUENCE    = 0x00880200;
    private static final int ITEM                   = 0xFFFEE000;
    private static final int ITEM_DELIMINATION      = 0xFFFEE00D;
    private static final int SEQUENCE_DELIMINATION  = 0xFFFEE0DD;
    private static final int PIXEL_DATA             = 0x7FE00010;
    private static final int IMAGE_POSITION         = 0x00200032;
    private static final int SLICE_LOCATION         = 0x00201041;
    private static final int STUDY_ID               = 0x00200010;
    private static final int SERIES_NUMBER          = 0x00200011;
    public static final char[] hexDigits            = { '0','1','2','3','4',
                                                        '5','6','7','8','9',
                                                        'A','B','C','D','E','F'};

    private static final int AE=0x4145, AS=0x4153, AT=0x4154, CS=0x4353, DA=0x4441, DS=0x4453, DT=0x4454,
        FD=0x4644, FL=0x464C, IS=0x4953, LO=0x4C4F, LT=0x4C54, PN=0x504E, SH=0x5348, SL=0x534C,
        SS=0x5353, ST=0x5354, TM=0x544D, UI=0x5549, UL=0x554C, US=0x5553, UT=0x5554,
        OB=0x4F42, OW=0x4F57, SQ=0x5351, UN=0x554E, QQ=0x3F3F;

    private static Properties dictionary;

    private static final int ID_OFFSET              = 128;  //location of "DICM"
    private static final String DICM                = "DICM";

    private BufferedInputStream f;
    private int location                            = 0;
    private boolean littleEndian                    = true;

    private int elementLength;
    private int vr;  // Value Representation
    private static final int IMPLICIT_VR = 0x2D2D; // '--'
    private byte[] vrLetters = new byte[2];
    private int previousGroup;
    private String previousInfo;
    private StringBuffer dicomInfo = new StringBuffer(1000);
    private boolean dicmFound; // "DICM" found at offset 128
    private boolean oddLocations;  // one or more tags at odd locations
    private boolean bigEndianTransferSyntax = false;
    double windowCenter, windowWidth;
    double rescaleIntercept, rescaleSlope;
    
    double sliceLocation                   =   1.0;
    String studyId                         =   null;
    String seriesNumber                    =   null;
    boolean inSequence;
    private File sourceFile;
    BufferedInputStream inputStream;

    public DicomDecoder(File srcFile) {
        this.sourceFile  =   srcFile;
    
        if (dictionary==null) {
            DicomDictionary d = new DicomDictionary();
            dictionary = d.getDictionary();
        }
    }

    String getString(int length) throws IOException {
        byte[] buf = new byte[length];
        int pos = 0;
        while (pos<length) {
            int count = f.read(buf, pos, length-pos);
            pos += count;
        }
        location += length;
        return new String(buf);
    }

     int getByte() throws IOException {
        int b = f.read();
        if (b ==-1) {throw new IOException("unexpected EOF");}
        ++location;
        return b;
    }

    int getShort() throws IOException {
        int b0 = getByte();
        int b1 = getByte();
        if (littleEndian){
            return ((b1 << 8) + b0);
        }
        else{
            return ((b0 << 8) + b1);
        }
            
    }

    final int getInt() throws IOException {
        int b0 = getByte();
        int b1 = getByte();
        int b2 = getByte();
        int b3 = getByte();
        if (littleEndian)
            return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
        else
            return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
    }

    double getDouble() throws IOException {
        int b0 = getByte();
        int b1 = getByte();
        int b2 = getByte();
        int b3 = getByte();
        int b4 = getByte();
        int b5 = getByte();
        int b6 = getByte();
        int b7 = getByte();
        long res = 0;
        if (littleEndian) {
            res += b0;
            res += ( ((long)b1) << 8);
            res += ( ((long)b2) << 16);
            res += ( ((long)b3) << 24);
            res += ( ((long)b4) << 32);
            res += ( ((long)b5) << 40);
            res += ( ((long)b6) << 48);
            res += ( ((long)b7) << 56);
        } else {
            res += b7;
            res += ( ((long)b6) << 8);
            res += ( ((long)b5) << 16);
            res += ( ((long)b4) << 24);
            res += ( ((long)b3) << 32);
            res += ( ((long)b2) << 40);
            res += ( ((long)b1) << 48);
            res += ( ((long)b0) << 56);
        }
        return Double.longBitsToDouble(res);
    }

    float getFloat() throws IOException {
        int b0 = getByte();
        int b1 = getByte();
        int b2 = getByte();
        int b3 = getByte();
        int res = 0;
        if (littleEndian) {
            res += b0;
            res += ( ((long)b1) << 8);
            res += ( ((long)b2) << 16);
            res += ( ((long)b3) << 24);
        } else {
            res += b3;
            res += ( ((long)b2) << 8);
            res += ( ((long)b1) << 16);
            res += ( ((long)b0) << 24);
        }
        return Float.intBitsToFloat(res);
    }

    byte[] getLut(int length) throws IOException {
        if ((length&1)!=0) { // odd
            String dummy = getString(length);
            return null;
        }
        length /= 2;
        byte[] lut = new byte[length];
        for (int i=0; i<length; i++)
            lut[i] = (byte)(getShort()>>>8);
        return lut;
    }

    int getLength() throws IOException {
        int b0 = getByte();
        int b1 = getByte();
        int b2 = getByte();
        int b3 = getByte();

        // We cannot know whether the VR is implicit or explicit
        // without the full DICOM Data Dictionary for public and
        // private groups.

        // We will assume the VR is explicit if the two bytes
        // match the known codes. It is possible that these two
        // bytes are part of a 32-bit length for an implicit VR.

        vr = (b0<<8) + b1;

        switch (vr) {
            case OB: case OW: case SQ: case UN:
                // Explicit VR with 32-bit length if other two bytes are zero
                    if ( (b2 == 0) || (b3 == 0) ) return getInt();
                // Implicit VR with 32-bit length
                vr = IMPLICIT_VR;
                if (littleEndian)
                    return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
                else
                    return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
            case AE: case AS: case AT: case CS: case DA: case DS: case DT:  case FD:
            case FL: case IS: case LO: case LT: case PN: case SH: case SL: case SS:
            case ST: case TM:case UI: case UL: case US: case UT: case QQ:
                // Explicit vr with 16-bit length
                if (littleEndian)
                    return ((b3<<8) + b2);
                else
                    return ((b2<<8) + b3);
            default:
                // Implicit VR with 32-bit length...
                vr = IMPLICIT_VR;
                if (littleEndian)
                    return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
                else
                    return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
        }
    }

    int getNextTag() throws IOException {
        int groupWord = getShort();
        if (groupWord==0x0800 && bigEndianTransferSyntax) {
            littleEndian = false;
            groupWord = 0x0008;
        }
        int elementWord = getShort();
        int tag = groupWord<<16 | elementWord;
        elementLength = getLength();

        // hack needed to read some GE files
        // The element length must be even!
        if (elementLength==13 && !oddLocations) elementLength = 10;

        // "Undefined" element length.
        // This is a sort of bracket that encloses a sequence of elements.
        if (elementLength==-1) {
            elementLength = 0;
            inSequence = true;
        }
        //IJ.log("getNextTag: "+tag+" "+elementLength);
        return tag;
    }

    String getDicomInfo() {
        String s = new String(dicomInfo);
        char[] chars = new char[s.length()];
        s.getChars(0, s.length(), chars, 0);
        for (int i=0; i<chars.length; i++) {
            if (chars[i]<' ' && chars[i]!='\n') chars[i] = ' ';
        }
        return new String(chars);
    }

    void addInfo(int tag, String value) throws IOException {
        String info = getHeaderInfo(tag, value);
        if (inSequence && info!=null && vr!=SQ) info = ">" + info;
        if (info!=null &&  tag!=ITEM) {
            int group = tag>>>16;
            //if (group!=previousGroup && (previousInfo!=null&&previousInfo.indexOf("Sequence:")==-1))
            //  dicomInfo.append("\n");
            previousGroup = group;
            previousInfo = info;
            dicomInfo.append(tag2hex(tag)+info+"\n");
        }
    
    }

    void addInfo(int tag, int value) throws IOException {
        addInfo(tag, Integer.toString(value));
    }

    String getHeaderInfo(int tag, String value) throws IOException {
        if (tag==ITEM_DELIMINATION || tag==SEQUENCE_DELIMINATION) {
            inSequence = false;
        }
        String key = i2hex(tag);
        //while (key.length()<8)
        //  key = '0' + key;
        String id = (String)dictionary.get(key);
        if (id!=null) {
            if (vr==IMPLICIT_VR && id!=null)
                vr = (id.charAt(0)<<8) + id.charAt(1);
            id = id.substring(2);
        }
        if (tag==ITEM){
             return id!=null?id+":":null;
        }
           
        if (value!=null){
             return id+": "+value;
        }
           
        switch (vr) {
            case FD:
                if (FD==8){
                     value = Double.toString(getDouble());
                }
                   
                else{
                    for (int i=0; i<elementLength; i++) getByte();
                }
                    
                break;
            case FL:
                if (FD==8){
                     value = Float.toString(getFloat());
                }
                   
                else{
                     for (int i=0; i<elementLength; i++) {
                         getByte();
                     }
                }
                   
                break;
            case UT:
                throw new IOException("ImageJ cannot read UT (unlimited text) DICOMs");
            case AE: case AS: case AT: case CS: case DA: case DS: case DT:  case IS: case LO:
            case LT: case PN: case SH: case ST: case TM: case UI:
                value = getString(elementLength);
                break;
            case US:
                if (elementLength==2)
                    value = Integer.toString(getShort());
                else {
                    value = "";
                    int n = elementLength/2;
                    for (int i=0; i<n; i++)
                        value += Integer.toString(getShort())+" ";
                }
                break;
            case IMPLICIT_VR:
                value = getString(elementLength);
                if (elementLength>44) value=null;
                break;
            case SQ:
                value = "";
                boolean privateTag = ((tag>>16)&1)!=0;
                if (tag!=ICON_IMAGE_SEQUENCE && !privateTag)
                    break;
                // else fall through and skip icon image sequence or private sequence
            default:
                long skipCount = (long)elementLength;
                while (skipCount > 0) skipCount -= f.skip(skipCount);
                location += elementLength;
                value = "";
        }
        if (value!=null && id==null && !value.equals(""))
            return "---: "+value;
        else if (id==null)
            return null;
        else
            return id+": "+value;
    }

    static char[] buf8 = new char[8];

    /** Converts an int to an 8 byte hex string. */
    String i2hex(int i) {
        for (int pos=7; pos>=0; pos--) {
            buf8[pos] = hexDigits[i&0xf];
            i >>>= 4;
        }
        return new String(buf8);
    }

    char[] buf10;

    String tag2hex(int tag) {
        if (buf10==null) {
            buf10 = new char[11];
            buf10[4] = ',';
            buf10[9] = ' ';
        }
        int pos = 8;
        while (pos>=0) {
            buf10[pos] = hexDigits[tag&0xf];
            tag >>>= 4;
            pos--;
            if (pos==4) pos--; // skip coma
        }
        return new String(buf10);
    }

    void getSpatialScale(FileInfo fi, String scale) {
        double xscale=0, yscale=0;
        int i = scale.indexOf('\\');
        if (i>0) {
            yscale = s2d(scale.substring(0, i));
            xscale = s2d(scale.substring(i+1));
        }
        if (xscale!=0.0 && yscale!=0.0) {
            fi.pixelWidth = xscale;
            fi.pixelHeight = yscale;
            fi.unit = "mm";
        }
    }

    double s2d(String s) {
        Double d;
        try {d = new Double(s);}
        catch (NumberFormatException e) {d = null;}
        if (d!=null)
            return(d.doubleValue());
        else
            return(0.0);
    }

    boolean dicmFound() {
        return dicmFound;
    }


  FileInfo getFileInfo() throws IOException {
      FileInfo fi               = new FileInfo();
      long skipCount;
      int bitsAllocated         = 16;

      fi.fileName               = sourceFile.getName();
      fi.directory              = sourceFile.getParent();
      fi.fileFormat             = FileInfo.RAW;
      fi.width                  = 0;
      fi.height                 = 0;
      fi.offset                 = 0;
      fi.intelByteOrder         = true;
      fi.fileType               = FileInfo.GRAY16_UNSIGNED;
      fi.fileFormat             = FileInfo.DICOM;
      fi.unit                   =     "mm";


        int samplesPerPixel         = 1;
        int planarConfiguration     = 0;
        String photoInterpretation  = "";

        if (inputStream!=null) {
            f = inputStream;
            f.mark(200000);
        } else{
            f = new BufferedInputStream(new FileInputStream(this.sourceFile));
        }
          

        skipCount = (long)ID_OFFSET;
        while (skipCount > 0) {
            skipCount -= f.skip( skipCount );
        }
        location += ID_OFFSET;

        String str = getString(4);
        if (! str.equals(DICM)) {
            if (inputStream==null) {
                f.close();
            }
            if (inputStream!=null){
                f.reset();
            }
            else{
                f = new BufferedInputStream(new FileInputStream(this.sourceFile));
            }
            location = 0;
        } else {
            dicmFound = true;
        }

        boolean decodingTags    = true;
        boolean signed          = false;

        while (decodingTags) {
            int tag = getNextTag();
            if ((location&1)!=0){ // DICOM tags must be at even locations
                 oddLocations = true;
            }
               
            if (inSequence) {
                addInfo(tag, null);
                continue;
            }
            String s;
            switch (tag) {
                case TRANSFER_SYNTAX_UID:
                    s = getString(elementLength);
                    addInfo(tag, s);
                    if (s.indexOf("1.2.4")>-1||s.indexOf("1.2.5")>-1) {
                        f.close();
                        String msg = "Cannot open compressed DICOM images.\n \n";
                        msg += "Transfer Syntax UID = "+s;
                        throw new IOException(msg);
                    }
                    if (s.indexOf("1.2.840.10008.1.2.2")>=0){
                           bigEndianTransferSyntax = true;
                    }
                      
                    break;
                case NUMBER_OF_FRAMES:
                    s = getString(elementLength);
                    addInfo(tag, s);
                    double frames = s2d(s);
                    if (frames >1.0){
                         fi.nImages = (int)frames;
                    }
                       
                    break;
                case SAMPLES_PER_PIXEL:
                    samplesPerPixel = getShort();
                    addInfo(tag, samplesPerPixel);
                    break;
                case PHOTOMETRIC_INTERPRETATION:
                    photoInterpretation = getString(elementLength);
                    addInfo(tag, photoInterpretation);
                    break;
                case PLANAR_CONFIGURATION:
                    planarConfiguration = getShort();
                    addInfo(tag, planarConfiguration);
                    break;
                case ROWS:
                    fi.height = getShort();
                    addInfo(tag, fi.height);
                    break;
                case COLUMNS:
                    fi.width = getShort();
                    addInfo(tag, fi.width);
                    break;
                case PIXEL_SPACING:
                    String scale = getString(elementLength);
                    getSpatialScale(fi, scale);
                    addInfo(tag, scale);
                    break;
                case SLICE_THICKNESS:

                case SLICE_SPACING:
                    String spacing = getString(elementLength);
                    fi.pixelDepth  = s2d(spacing);
                    addInfo(tag, spacing);
                    break;
                case BITS_ALLOCATED:
                    bitsAllocated = getShort();
                    if (bitsAllocated==8){
                         fi.fileType = FileInfo.GRAY8;
                    }
                    else if (bitsAllocated==32){
                         fi.fileType = FileInfo.GRAY32_UNSIGNED;
                    }
                    addInfo(tag, bitsAllocated);
                    break;
                case PIXEL_REPRESENTATION:
                    int pixelRepresentation = getShort();
                    if (pixelRepresentation==1) {
                        fi.fileType = FileInfo.GRAY16_SIGNED;
                        signed = true;
                    }
                    addInfo(tag, pixelRepresentation);
                    break;
                case WINDOW_CENTER:
                    String center = getString(elementLength);
                    int index = center.indexOf('\\');
                    if (index!=-1) {
                        center = center.substring(index+1);
                    }
                    windowCenter = s2d(center);
                    addInfo(tag, center);
                    break;
                case WINDOW_WIDTH:
                    String width = getString(elementLength);
                    index = width.indexOf('\\');
                    if (index!=-1){
                        width = width.substring(index+1);
                    }
                    windowWidth = s2d(width);
                    addInfo(tag, width);
                    break;
                case RESCALE_INTERCEPT:
                    String intercept = getString(elementLength);
                    rescaleIntercept = s2d(intercept);
                    addInfo(tag, intercept);
                    break;
                case RESCALE_SLOPE:
                    String slop = getString(elementLength);
                    rescaleSlope = s2d(slop);
                    addInfo(tag, slop);
                    break;
                case RED_PALETTE:
                    fi.reds = getLut(elementLength);
                    addInfo(tag, elementLength/2);
                    break;
                case GREEN_PALETTE:
                    fi.greens = getLut(elementLength);
                    addInfo(tag, elementLength/2);
                    break;
                case BLUE_PALETTE:
                    fi.blues = getLut(elementLength);
                    addInfo(tag, elementLength/2);
                    break;
                case PIXEL_DATA:
                    // Start of image data...
                    if (elementLength!=0) {
                        fi.offset = location;
                        addInfo(tag, location);
                        decodingTags = false;
                    } else{
                         addInfo(tag, null);
                    }
                       
                    break;
                case 0x7F880010:
                    // What is this? - RAK
                    if (elementLength!=0) {
                        fi.offset = location+4;
                        decodingTags = false;
                    }
                    break;
              case IMAGE_POSITION:
                     String imPos = getString(elementLength);
                     addInfo(tag, imPos);
                    // System.out.println("image position "+ imPos);
                     break;

             case  SLICE_LOCATION:
                     String sliceLocationStr = getString(elementLength);
                     addInfo(tag,  sliceLocationStr);

                     try{
                        sliceLocation = Double.parseDouble(sliceLocationStr);
                     }
                     catch(NumberFormatException exp){
                        exp.printStackTrace();
                        sliceLocation = 1;
                     }
                     break;
            case  STUDY_ID:
                     studyId = getString(elementLength);
                     addInfo(tag, studyId);
                  
                     break;
           case  SERIES_NUMBER:
                     seriesNumber = getString(elementLength);
                     addInfo(tag,  seriesNumber);

                     break;



                default:
                    // Not used, skip over it...
                    addInfo(tag, null);
            }
        } // while(decodingTags)

        if (fi.fileType==FileInfo.GRAY8) {
            if (fi.reds!=null && fi.greens!=null && fi.blues!=null
            && fi.reds.length==fi.greens.length
            && fi.reds.length==fi.blues.length) {
                fi.fileType = FileInfo.COLOR8;
                fi.lutSize = fi.reds.length;

            }
        }

        if (fi.fileType==FileInfo.GRAY32_UNSIGNED && signed){
            fi.fileType = FileInfo.GRAY32_INT;
        }

        if (samplesPerPixel==3 && photoInterpretation.startsWith("RGB")) {
            if (planarConfiguration==0){
                fi.fileType = FileInfo.RGB;
            }
            else if (planarConfiguration==1){
                fi.fileType = FileInfo.RGB_PLANAR;
            }
        } else if (photoInterpretation.endsWith("1 ")){
                 fi.whiteIsZero = true;
        }
               

        if (!littleEndian){
                 fi.intelByteOrder = false;
        }
           

        if (inputStream!=null){
                 f.reset();
        }
           
        else{
                f.close();
        }
          // System.out.println(this.dicomInfo);
        return fi;
    }

  public static float [] readPixels(FileInfo fi)
          throws FileNotFoundException, IOException
  {
        float[] pixels              =   null;

        // System.out.println(dd.dicomInfo);
        FileOpener fo       = new FileOpener(fi);
        ImagePlus imp       = fo.open(false);
        ImageProcessor ip   = imp.getProcessor();

        /*
        int stackSize   =   imp.getStackSize();
        int nFrames     =   imp.getNFrames();
        int width       =   ip.getWidth();
        int heigt       =   ip.getHeight();

        int totalPixels =  width* heigt* nFrames * stackSize ;

        if (stackSize > 1){

            pixels  = new float [ totalPixels];


        }
        else {
            pixels = getFloatPixels(ip);
        }

*/
        pixels =  getFloatPixelsFromStack(imp);

        return pixels;

  }

  private static float [] getFloatPixels(ImageProcessor ip){
        float[] pixels = null;
        if (ip instanceof  FloatProcessor){}
        else {
            ip = ip.convertToFloat();
        }
        pixels = (float[]) ip.getPixels();

        return pixels;

  }
  private static float [] getFloatPixelsFromStack(ImagePlus imp){
        imp.setSlice(1);
        ImageProcessor ip   =   imp.getProcessor();
        int stackSize       =   imp.getStackSize();
        int nFrames         =   imp.getNFrames();
        int width           =   ip.getWidth();
        int heigt           =   ip.getHeight();
        int totalPixels     =   width* heigt* nFrames * stackSize ;
        float[] pixels      =   new float[totalPixels];


        for (int curStack = 1, pos = 0; curStack <= stackSize ; curStack++) {
            imp.setSlice(curStack);
            ip                  =   imp.getProcessor();
            float [] curPixels  =  getFloatPixels(ip);

            System.arraycopy(curPixels, 0, pixels , pos,curPixels.length);
            pos+=curPixels.length;
      }


        return pixels;

  }


 public FileInfo readFileInfo()
          throws FileNotFoundException, IOException
  {
        BufferedInputStream bis     = null;

        bis                 = new BufferedInputStream(new FileInputStream(this.sourceFile));
        this.inputStream    = bis;

        FileInfo fi         = this.getFileInfo();
        if(bis  != null) {bis.close();}
        return fi ;

  }




    public static void main(String [] args){
        BufferedInputStream bis = null;
        float[] pixels           =   null;
        try {
            File file = new File("/Users/apple/BayesSys/Bayes.test.data/Dicom/CR-MONO1-10-chest");
          //  file = new File("/Users/apple/BayesSys/Bayes.test.data/images/standard.fid/fid");

            bis = new BufferedInputStream(new FileInputStream(file));
            DicomDecoder dd = new DicomDecoder(file);
             dd.inputStream = bis;
            FileInfo fi = null;
            try {
                fi = dd.getFileInfo();
                System.out.println(dd.dicomInfo);
                System.out.println(fi);
                  FileOpener fo = new FileOpener(fi);
            ImagePlus imp = fo.open(false);
            ImageProcessor ip = imp.getProcessor();
            if (ip instanceof  FloatProcessor){
                System.out.println("float processor");
            }
            else {
                System.out.println(ip.getClass());
                ip = ip.convertToFloat();
            }
            pixels = (float[]) ip.getPixels();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(DicomDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println(pixels);
   }

}
