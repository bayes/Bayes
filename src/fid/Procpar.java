/*
 * Procpar.java
 *
 * Created on August 27, 2007, 1:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fid;
import java.util.regex.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import bayes.Enums.UNITS;
import bayes.Enums.IMAGE_TYPE;
import bayes.Enums.FILE_ORGANIZATION;
import utilities.IO;

/**
 *
 * @author apple
 */
public class Procpar implements Serializable, ProcparConstants {
    private float sw; // spectral width
    private float sw1; // spectral width phase encode
    private float at; // acquisition time
    private int np; // np/2 number of complex points in the fid
    private int nv      =   -1; // number of phase-encodes (for images)
    private int ns      =   0; // number of slices (for images)
    private int ni      =   0; // number of i?(for images)
    private int nf      =   0; // number of ?
    private int fn      =   -1; // fn/2 = number of points in readout ( the spectrum 0-padded)
    private int fn1     =   -1; // fn/2 = number of points in phase encode  ( the spectrum 0-padded)
    private int cf      =   0;
    
    private float sfrq; // spectrometer frequency
    private float rfp   =   0;
    private float rfl   =   0;
    private float rfp1  =   0;
    private float rfl1  =   0;
    private float lb; // line broadening
    private float lp; // left phase
    private float rp; // right phase
    private float ref; // reference frequency
    private char  axis; // units for x_axis
    private char  dp; // data is stored as 16-bit (dp = ' n')or 32-bit (dp = 'y') integers 

    private float fpmult = 1.0f;
    
    private float tpe ;
    private float gro ;
    private float gpe ;

    private boolean isFn                = false;
    private boolean isFn1               = false;
    private boolean navigatorOn         = false;

    private ArrayList<Integer> fieldMap =   new ArrayList<Integer>();

    private List<String>   array        =   new ArrayList<String>();       // string key for an arrayed item
    private String [][] array_values    =   null;       // actual array
    private int         arraydim        =   1;          // number of values in the 1D array
    private int         dataSize        =   -1;         // number of values in the data
    private int[]       sliceIndex;                     //indecies in which slices were stored
    private boolean     consecutiveSlices = true;       // are slices stored in nonconsecutive order
    private String      fileSource       = null   ;

    private float gdiff                 =   Float.NaN;   // this is a parameter needed in the DiffTensor analysis
    private float tDELTA                =   Float.NaN;   // this is a parameter needed in the DiffTensor analysis
    private float tdelta                =   Float.NaN;   // this is a parameter needed in the DiffTensor analysis
    
    private float lro                   =   Float.NaN; // length readout in cm
    private float lpe                   =   Float.NaN; // length phase encode in cm
    private float thk                   =   Float.NaN; // slice thickness in cm
    private float ppe                   =   0.0f; // default value for ppe MUST be 0

    private int   nseg                  =   1; //number of segments. Default  = 1. Dont change default!
    private int   numberOfFieldMaps     =   0;
    private int   nav_echo              =   1;
    private String  seqfil              =   null;
    private String  seqcon              =   null;

    private boolean nvActive                =   false;

    private boolean isBMatrix               =   false;
    private int bvalsize                    =   0;
    private final static String  bvalrsKEY  =   "bvalrs";
    private final static String  bvalrpKEY  =   "bvalrp";
    private final static String  bvalppKEY  =   "bvalpp";
    private final static String  bvalrrKEY  =   "bvalrr";
    private final static String  bvalssKEY  =   "bvalss";
    private final static String  bvalspKEY  =   "bvalsp";

    private List <String>bvalrs             =   new ArrayList<String>();
    private List <String>bvalrp             =   new ArrayList<String>();
    private List <String>bvalpp             =   new ArrayList<String>();
    private List <String>bvalrr             =   new ArrayList<String>();
    private List <String>bvalss             =   new ArrayList<String>();
    private List <String>bvalsp             =   new ArrayList<String>();

    private List<Integer> petable            =   new ArrayList<Integer>();

    public static final  String regex       =    Pattern.compile("\\s+").pattern();
    private  IMAGE_TYPE  imageType          =   IMAGE_TYPE.SPIN_ECHO;
    
    public Procpar(){};
    public Procpar (String filename) {
         String content             =  IO.readFileToString( new File(filename));
        /*
         readFnInformation          (content);
         readProcparInformation1    (content);
         readProcparInformation2    (content);
         readArrayInformation       (content);
         readDiffusionInformation   (content);
         readBMatrix                (content);
       
         if (isImage() == true){
            readImageInformation    (content);
            readSliceIndexing       (content);
         }
  
        modifyParams();
        */
        this.read(content);
       // print();
    }
    public Procpar (File file) {
         this (file.getPath());
    }

   public  void read (File file){
        String content          =   IO.readFileToString( file);
        read(content);
    }
   private  void read (String  content){
        readProcparSettings(content);
        populateArrayValues (content);
        if (isImage() == true){
            readImageParameters   (content);
         }
        modifyParams();
    }
   private  void readProcparSettings (String  content){
        Scanner scanner         =   new Scanner(content);
        String line             =   null;
        String token            =   null;
        setSw(Float.NaN);
        setAt(Float.NaN);
        setNp(-1);
        setNv(-1);
        setNs(-1);
        setSfrq(Float.NaN);
        setRfp(Float.NaN);
        setRfl(Float.NaN);
        setLb(Float.NaN);
        getArray().clear();
        setArrayValues(null);
        setArraydim(1);

        while(scanner.hasNextLine()){
             token              =   scanner.next();


             //read fn
             if ( token.equalsIgnoreCase(FN_KEY)  ==  true ) {
                line            =   scanner.nextLine();
                boolean on      =   isActive(line);
                setIsFn(on );
                line            =   scanner.nextLine();
                int v           =   getIntegerValue(line);
                setFn(v);
              }

             //read fn1
             else if ( token.equalsIgnoreCase(FN1_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    boolean on      =   isActive(line);
                    setIsFn1(on );

                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setFn1(v);
            }


             //read SW
             else if ( token.equalsIgnoreCase(SW_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setSw(v);
            }


             //read AT
             else if ( token.equalsIgnoreCase(AT_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setAt(v);
             }



            //read NP
             else if ( token.equalsIgnoreCase(NP_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setNp(v);
            }


           //read NV
             else if ( token.equalsIgnoreCase(NV_KEY)  ==  true  ) {
                    line                =   scanner.nextLine();
                    boolean isNvActive  =   Procpar.isActive(line);
                    setNvActive(isNvActive);
                    line                =   scanner.nextLine();
                    int v               =   getIntegerValue(line);
                    setNv(v);
                    
            }


            //read NS
             else if ( token.equalsIgnoreCase(NS_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v         =   getIntegerValue(line);
                    setNs(v);
            }



            //read  SFRQ
            else if ( token.equalsIgnoreCase( SFRQ_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setSfrq(v);
             }




             //read RFP
            else if ( token.equalsIgnoreCase(RFP_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setRfp(v);
             }


             //read RFL
            else if ( token.equalsIgnoreCase(RFL_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setRfl(v);
             }



            //read LB
            else if ( token.equalsIgnoreCase(LB_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setLb(v);
             }




            //read File
            else if ( token.equalsIgnoreCase(FILE_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    String v        =   getStringValue(line);
                      setFileSource(v);
             }


             //read LP
            else if ( token.equalsIgnoreCase(LP_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setLp(v);
             }


            //read RP
            else if ( token.equalsIgnoreCase(RP_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setRp(v);
             }




            //read AXIS
            else if ( token.equalsIgnoreCase(AXIS_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    char v        =   getCharacterValue(line);
                    setAxis(v);
             }


             //read DP
            else if ( token.equalsIgnoreCase(DP_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    char v        =   getCharacterValue(line);
                    setDp(v);
             }




           //read GDIFF
            else if ( token.equalsIgnoreCase(GDIFF_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setGdiff(v);
             }


            //read Tdelta
            else if ( token.equals(Tdelta_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setTdelta(v);
             }


             //read  TDELTA
            else if ( token.equals( TDELTA_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setTDELTA(v);
             }


            //read  bvalrs
            else if ( token.equalsIgnoreCase(bvalrsKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalrs          =   getStringList(line);
                    bvalsize        = bvalrs.size();
           }

           //read  bvalrs
            else if ( token.equalsIgnoreCase(bvalrpKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalrp          =   getStringList(line);
                    bvalsize        =   bvalrp.size();
           }

           //read  bvalrr
            else if ( token.equalsIgnoreCase(bvalrrKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalrr          =   getStringList(line);
                    bvalsize        =   bvalrr.size();
           }

           //read  bvalpp
            else if ( token.equalsIgnoreCase(bvalppKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalpp          =   getStringList(line);
                    bvalsize        =   bvalpp.size();
           }

           //read  bvalss
            else if ( token.equalsIgnoreCase(bvalssKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalss          =   getStringList(line);
                    bvalsize        =   bvalss.size();
           }

           //read  bvalspp
            else if ( token.equalsIgnoreCase(bvalspKEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    bvalsp          =   getStringList(line);
                    bvalsize        =   bvalsp.size();
           }



           //read ArrayDim
             else if ( token.equalsIgnoreCase( ARRAYDIM_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setArraydim(v);
            }


            //read Array
             else if ( token.equalsIgnoreCase( ARRAY_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    List<String> v  =   getStringList(line);
                    setArray(v);

             }
             else {
                    line            =   scanner.nextLine();
             }



        }
        scanner.close();

    }
   private  void populateArrayValues (String  content){
        Scanner scanner         =   new Scanner(content);
        String line             =   null;
        String token            =   null;
        if (getArray().isEmpty()){ return;}

        List<String>   NoImageList         =   new ArrayList<String>();
        NoImageList.addAll(getArray());
        NoImageList.remove(IMAGE_KEY);
        int length                          =   NoImageList.size();
        String [][] values                  =   new String[length][];
        String [] imagevalues               =   null;


        int count                           =   0;
        while(scanner.hasNextLine()){
             token                          =   scanner.next();


            if (  NoImageList.contains(token)==  true  ) {
                    line                =   scanner.nextLine();
                    line                =   scanner.nextLine();
                    String [] v         =   getStringArray(line);
                    values[count]       =   v;
                    count               =   count + 1;
            }
            else if ( token.equalsIgnoreCase(IMAGE_KEY)==  true  ) {
                    line                =   scanner.nextLine();
                    line                =   scanner.nextLine();
                    imagevalues         =   getStringArray(line);
            }
            else {
                    line            =   scanner.nextLine();
            }
        }

       setArrayValues(values);
       if ( NoImageList.isEmpty() == false &&  array_values[0] != null) {
             dataSize     = array_values[0].length;
       }
       else  if(imagevalues  != null){
            dataSize               =   values.length -1 ;
       }

       if(imagevalues  != null){
              for (String string : imagevalues) {
                 int val         =   Integer.valueOf(string);
                 fieldMap.add(val);
                 //if (val == 0){numberOfFieldMaps  +=1;}
                 if (val < 1) {numberOfFieldMaps  +=1;}
           }
       }



    }
   public  void readImageParameters (String  content){
        Scanner scanner         =   new Scanner(content);
        String line             =   null;
        String token            =   null;

        while(scanner.hasNextLine()){
             token                          =   scanner.next();

            // read lro
            if ( token.equalsIgnoreCase( LRO_KEY)  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setLro(v);
            }

             // read lpe
            else if ( token.equalsIgnoreCase(LPE_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setLpe(v);
            }


            // read thk
            else if ( token.equalsIgnoreCase(THK_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setThk(v);
            }


             // read ppe
            else if ( token.equalsIgnoreCase(PPE_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    float v         =   getFloatValue(line);
                    setPpe(v);
            }
            // read NI
            else if ( token.equalsIgnoreCase( NI_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setNi(v);
            }


             // read CF
            else if ( token.equalsIgnoreCase( CF_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setCf(v);
            }


            // read NSEG
            else if ( token.equalsIgnoreCase( NSEG_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    setNseg(v);
            }

             // read SEQFIL
            else if ( token.equalsIgnoreCase( SEQFIL_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    String v        =   getStringValue(line);
                    seqfil          =   v;
            }

           // read SEQCON
            else if ( token.equalsIgnoreCase( SEQCON_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    String v        =   getStringValue(line);
                    seqcon          =   v;
            }
        
            // read NAVIGATOR
            else if ( token.equalsIgnoreCase(NAVIGATOR_KEY )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    String v        =   getStringValue(line);
                    if (v.equalsIgnoreCase("y")){  setNavigatorOn(true);}
                    else                        {setNavigatorOn(false); }

            }
            
            // read nav_echo
            else if ( token.equalsIgnoreCase( NAV_ECHO_KEY  )  ==  true  ) {
                    line            =   scanner.nextLine();
                    line            =   scanner.nextLine();
                    int v           =   getIntegerValue(line);
                    this.setNav_echo(v);

            }


             // read PSS
            else if ( token.equalsIgnoreCase(PSS_KEY )  ==  true  ) {
                    line                =   scanner.nextLine();
                    line                =   scanner.nextLine();
                    List<String> v      =   getStringList(line);
                    readSliceIndexing(v);
            }


             // read PE_TABLE
            else if ( token.equalsIgnoreCase(PETABLE_KEY )  ==  true  ) {
                    line                =   scanner.nextLine();
                    line                =   scanner.nextLine();
                    List<Integer> v     =   getIntegerList(line);
                    setPetable(v);
            }


            else {
                line                =   scanner.nextLine();
            }


        }


        if (seqfil.contains(GEMS_KEY ) == true){
               imageType   =   IMAGE_TYPE.SPIN_ECHO;
        }
        else if (seqfil.contains(SEMS_KEY) == true){
               imageType   =   IMAGE_TYPE.SPIN_ECHO;
        }
        else if (seqfil.contains(EPI_KEY) == true ){
               // if( numberOfFieldMaps  ==0){  imageType  =   IMAGE_TYPE.MAP_EPI;}
         if( numberOfFieldMaps  >0){  imageType  =   IMAGE_TYPE.MAP_EPI;}
         else          { imageType   =   IMAGE_TYPE.EPI;}
        }

        convertPETable ();
        scanner.close();
    }

    public static boolean       isActive(String line){
       boolean active              =   false;
       try{
            String []  strArray          =   line.split(regex);
            int oneTolast                =   strArray.length - 2;
            int status                   =   Integer.valueOf(strArray [ oneTolast]);
            active                       =   (status == 1) ? true : false;

       }
       catch(Exception e){e.printStackTrace();}
       finally{return active;}

    }
    public static int           getIntegerValue(String line){
       int out                          =  0;
       try{
            String []  strArray         =   line.split(regex);
            out                         =   Integer.valueOf(strArray [ 1]);

       }
       catch(Exception e){e.printStackTrace();}
       finally{return out ;}

    }
    public static float         getFloatValue(String line){
       float out                         =  Float.NaN;
       try{
            String []  strArray          =   line.split(regex);
            out                          =   Float.valueOf(strArray [ 1]);

       }
       catch(Exception e){e.printStackTrace();}
       finally{return out ;}

    }
    public static String        getStringValue(String line){
       String []  strArray          =   line.split(regex);
       String out                   =   stripQuotes( strArray [1]);
       return out;
    }
    public static char          getCharacterValue(String line){
      char c                        =    ' ';
      try{
            String []  strArray          =   line.split(regex);
            String out                   =   strArray [1];
            c                       =   out.charAt(1);
      }
      finally{
            return c;
      }
    }
    public static List<String>  getStringList(String line){
      List <String> values  =  new ArrayList<String>();
      try{
          
            line        =   line.replaceAll("\"", "");
            line        =   line.replaceAll(",", " ");
            line        =   line.replaceAll("\\(", " ");
            line        =   line.replaceAll("\\)", " ");
           
            String []  strArray          =   line.split(regex);
            for (int i = 1; i < strArray.length; i++) {
                String str                = strArray[i];
               // if  (str.equals("\"\"")){continue;}
                values.add ( str  );
           }
      }
      catch(Exception e){e.printStackTrace();}
      finally{
            return values;
      }
    }
    public static String []     getStringArray(String line){
      String []  strArray       =   null;
      try{
            List<String> list   =  getStringList( line);
            strArray               =   new  String [ list.size()] ;
            for (int i = 0; i < list.size(); i++) {
                strArray [i] =  list.get(i);
            }
      }
      finally{
            return strArray;
      }
    }
    public static List<Integer> getIntegerList(String line){
      List <Integer> values     =  new ArrayList<Integer>();
      try{

            String []  strArray          =   line.split(regex);
            for (int i = 1; i < strArray.length; i++) {
                String str                = strArray[i];
                int v                     = Integer.parseInt(str);
                values.add ( v  );
           }
      }
      catch(Exception e){
          e.printStackTrace();
          values.clear();
      }
      finally{
            return values;
      }
    }


    public static boolean isImage (File file){
        String content          =   IO.readFileToString( file);

        String line           =   "";
        Scanner scanner       =   new Scanner(content);

      while(scanner.hasNextLine()== true ){
        line                    =    scanner.nextLine();
        line                    =    line.trim();

        boolean matchFound      =    line.startsWith("nv ");
        if (  matchFound == false ){continue;}

        boolean isActive        =    isActive(line);
        if (  isActive == false ){ return false;}

        int skip                  =    scanner.nextInt();
        int aNv                   =    scanner.nextInt();
        if (aNv > 1){return true;}
        else { return false;}


      }


        return false;
    
    }
    public static boolean overwriteFileSource (File src, File dst){
        String content               =   IO.readFileToString( src);
        StringBuilder newContent     =   new StringBuilder();

        Scanner scanner              =   new Scanner(content);
        Pattern p                    =   Pattern.compile("\\s+");
        boolean matchFound           =   false;

       while(scanner.hasNextLine()== true ){
        String line            =  scanner.nextLine();
        if(line == null) { break;}

        String tmp                   =    line.trim();
        matchFound                   =    tmp.startsWith(FILE_KEY+ " ");

        newContent.append(line);
        newContent.append("\n");

        if (matchFound ){
               line                  =   scanner.nextLine().trim();
               String [] strs        =   line.split (regex);
               if (strs.length < 2) {
                   newContent.append(line);
                   newContent.append("\n");
                   break;
               }
               String newLine       = strs[0] + " \""+src.getParentFile().getAbsolutePath()+"\"";
               
               newContent.append(newLine);
               newContent.append("\n");

        }
       
       
      }



      scanner.close();

      boolean writeFile = IO.writeFileFromString(newContent.toString(), dst);
      return writeFile;
    }
    public static String  getFileSource (File procparFile){
        String content               =   IO.readFileToString(  procparFile);
        String result                =   null;

        Scanner scanner              =   new Scanner(content);
        Pattern p                    =   Pattern.compile("\\s+");
        String regex                 =   p.pattern();
        boolean matchFound           =   false;

        while(scanner.hasNextLine()== true ){
            String line            =  scanner.nextLine();
            if(line == null) { break;}

            String tmp                   =    line.trim();
            matchFound                   =    tmp.startsWith(FILE_KEY + " ");

        if (matchFound ){
               line                  =   scanner.nextLine().trim();
               String [] strs        =   line.split (regex);
               if (strs.length < 2) { break;}
               result                = stripQuotes(strs[1]);

        }

      }


      scanner.close();

      return result;
    }


    public static String [] readValue (String content, String key){
      String [] str         =   null; // data for abscissa
      String line           =   "";
      Scanner scanner       =   new Scanner(content);
      boolean matchFound    =   false;

      while(scanner.hasNextLine()== true && matchFound == false){
        line            =  scanner.nextLine();
        if(line == null) { break;}

        line                    =    line.trim();
        matchFound              =    line.startsWith(key+" ");
        //matchFound = Pattern.matches(key+" "+".*?", line);

        if (matchFound ){
               line             =   scanner.nextLine().trim();
               str              =   line.split (regex);
        }
      }



      scanner.close();
      return str;
  }
    public static List <String> readValueToList (String content, String key){
      List <String> values  =  new ArrayList<String>();
      String line           =   "";
      Scanner scanner       =   new Scanner(content);
      boolean matchFound    =   false;

      while(scanner.hasNextLine()== true && matchFound == false){
        line            =  scanner.nextLine();
        if(line == null) { break;}

        line                    =    line.trim();
        matchFound              =    line.startsWith(key+" ");

        if (matchFound ){
               line             =   scanner.nextLine().trim();
               String []str     =   line.split (regex);
               for (int i = 1; i < str.length; i++) {
                values.add ( str[i]);
               }
               break;
        }
      }



      scanner.close();
      return values;
  }

    public static String stripQuotes(String str){
        int start       =   1;
        int end         =   str.length()-1;
        str             =   str.substring(start, end);
        return str;
    }
   
    private void readSliceIndexing (List<String> list){
    
        if (list.isEmpty())  {
            sliceIndex                      =   null;

        }
        else {
             int size                       =   list.size();
             ArrayList<Float> values        =   new  ArrayList<Float> ();
             HashMap<Float, Integer>map     =   new  HashMap<Float, Integer>();
             List<Integer>indexList         =   new  ArrayList<Integer> ();
             sliceIndex                     =   new int  [size];

            float lastVal                    =   - Float.MAX_VALUE;
            for (int i = 0; i < list.size(); i++) {
               float val               =  Float.valueOf(list.get(i));
               values.add(val);
               map.put(val, i);
               if (val <  lastVal ){  consecutiveSlices  =   false;}
               lastVal = val;

            }

            Collections.sort(values);

            for (int i = 0; i < values.size(); i++) {
                float val               = values.get(i);
                int index               = map.get(val);
                indexList.add(index);

            }

           for (int i = 0; i < sliceIndex.length; i++) {
               sliceIndex  [indexList.get(i)] = i;
            }

        }


    }
    private void convertPETable (){
        List<Integer> pe                =   getPetable();
        List<Integer> cpe               =  new ArrayList<Integer>();
        int npe                         =   getNumberOfPhaseEncodePoints();
        int offset                      =   npe/2;
        if (pe.isEmpty()){
            for (int i = 0; i <  npe ; i++) {cpe.add(i);}
        }
        else{
            for (int curPe : pe) {  cpe.add( offset -curPe);}
        }

        this.setPetable(cpe);

    }

    private void modifyParams(){
        ref             =   -( (rfp - rfl) + sw/2);
        // convert to radians
        setRp((float) (rp * Math.PI / 180));
        setLp((float) (lp * Math.PI / 180));

        if ( bvalsize > 0 ){ isBMatrix         = true;}
        else               { isBMatrix         = false;}



    }
    public void updateProcpar(float AT, float SFRQ, int NP ){
        at              =   AT;
        sfrq            =   SFRQ;
        np              =   NP;
        sw              =   np/2/at;
        fn              =   utilities.MathFunctions.getPaddedLength(np);
        lb              =   (float)3.0/at;
        lp              =   0;
        rp              =   0;
        rfl             =   0;
        rfp             =   -sw/2;
        fpmult          =   1.0f;
        ref             =   -( (rfp - rfl) + sw/2);
   }
    
    public void print(){
        System.out.println("sw is "+ sw);
        System.out.println("at is "+ at);
        System.out.println("np is "+ np);
        System.out.println("nv is "+ nv);
        System.out.println("ns is "+ ns);
        System.out.println("fn is "+ fn);
        System.out.println("fn1 is "+ fn1);
        System.out.println("sfrq is "+ sfrq);
        System.out.println("rfp is "+ getRfp());
        System.out.println("rfl is "+ getRfl());
        System.out.println("lb is "+ lb);
        System.out.println("lp is (radians) "+ lp);
        System.out.println("rp is (radians) "+ rp);
        System.out.println("array is "+ array);
       System.out.println("array length is "+ getArray().size());
        System.out.println("arraydim is "+arraydim);
        System.out.println("dataSize is "+dataSize);
        System.out.println("axis is "+  axis);
        System.out.println("dp is "+  getDp());
        System.out.println("ref is "+ ref);
        //System.out.println("is data arrayed?  "+ isArrayed);
        if (this.getPetable().isEmpty() == false){
            System.out.println("pe_table is:");
            for (Integer pe : getPetable()) {
                System.out.print(" "+pe);
            }
            System.out.println("");
        }
       
    }
    public static void main(String args[]){
        File file        =   null;
        file     =   new File("/Users/apple/Bayes/Bayes.test.data/image_IR2.fid/procpar");

        Procpar proc = new Procpar(file);
        proc.setPpe(Float.NaN);
    }
//*********** getters and setters *********************//
// ALL FREQUENCIES ARE ASSUMED TO BE IN HERTZ IN PROCPAR FILE !!! ()    
    public float    getSw () {return sw;}
    public float    getSw1 () {
        return sw1;
    }
    public float    getAt () { return at;}
    public int      getNp () {return np;}
    public int      getNv () {return nv;}
    public int      getNs () {return ns;}
    public int      getFn () {return fn;}
    public float    getSfrq () {return sfrq;}
    public float    getRfp () {return rfp;}
    public float    getRfl () {return rfl;}
    public float    getRfp1 () {
        return rfp1;
    }
    public float    getRfl1 () {
        return rfl1;
    }
    public float    getLb () {return lb;}
    public float    getLp () { return lp;}
    public float    getRp () { return rp;}
    public float    getRef () { return ref;}
    public char     getAxis () { return axis;}
    public char     getIntegerType () { return getDp();}
    public UNITS    getUnits () { 
        UNITS units = (axis == 'p')? UNITS.PPM :UNITS.HERTZ;
        return units;
    }
    public char     getDp() {
        return dp;
    }
    public List<Integer> getPetable() {
        return petable;
    }

    public float    getTpe () {
        return tpe;
    }
    public float    getGro () {
        return gro;
    }
    public float    getGpe () {
        return gpe;
    }
    public int      getNf () {
        return nf;
    }
    public float    getPpe() {
        return ppe;
    }


    public String[][]   getArrayValues () {return array_values;}
    public List <String>    getArray () {return array;}
    public int          getArrayDim(){return arraydim;}
    public int          getNumberOfElements() {
       int numElem = dataSize;
       if (isImage() == true){
        switch(getFileOrganization()){

            case COMPRESSED :numElem = getArrayDim(); break;
            case STANDARD   :numElem = getArrayDim()/getNv(); break;

        }
       
       }
      
        return numElem;     
    }
    public int          getDataSize() {
      return dataSize;
    }
        
    public float [][]   getAbscissa(){
      String [][] strs  =    getArrayValues ();
      if (strs == null) {return null;}
      int         col   =    strs.length;
      int         row   =    strs[0].length;
      float [][] x      =    new float [col][row];
      
      for (int i = 0; i < col; i++) {
           for (int j = 0; j < row; j ++) {
                x[i][j] = Float.valueOf(strs[i][j]);
           }
      }
      return x;
    }
    public String   get3DAbscissa(){
      String [][] strs  =    getArrayValues ();
      if (strs == null){return null;}
      if ( strs[0] == null){return null;}

      StringBuilder sb =    new StringBuilder();
      String space     =    " ";
      
      int col          =    strs.length;
      int row          =    strs[0].length;
        for (int curRow = 0; curRow < row ; curRow++) {
            for (int curCol = 0; curCol < col; curCol++) {
               String str           =   strs [curCol][curRow];
               sb.append(pad(str) + space);
              
            }
             sb.append("\n");
        }

  
      return sb.toString();
    }
    public String   get6DAbscissa(){
      if (this.isBMatrix == false){return null;}

      StringBuilder sb =    new StringBuilder();
      String space     =    " ";
        for (int i = 0; i < this.bvalsize; i++) {
            sb.append(pad (bvalrr.get(i)) + space);
            sb.append(pad (bvalpp.get(i)) + space);
            sb.append(pad (bvalss.get(i)) + space);
            sb.append(pad (bvalrp.get(i)) + space);
            sb.append(pad (bvalrs.get(i)) + space);
            sb.append(pad (bvalsp.get(i)) + space);
            sb.append("\n");

        }


      return sb.toString();
    }
    public String   getXDAbsciss(){
       String result                =  get6DAbscissa();
       if (result == null){ result  =  get3DAbscissa();}
       return result;

    }

   public static String pad(String str ){
        String padding  = new String();
        int padlen      =   20;
        String pad      =   " ";
        int len = Math.abs(padlen) - str.toString().length();

        if (len < 1) { return str;}

        for (int i = 0 ; i < len ; ++i){
             padding = padding + pad;
        }

        return (padlen < 0 ? padding + str : str + padding);
    }

    public int          getAbscissaColumns(){return getArrayValues ().length;}
   
    public boolean      isFn1Activated () {
        return isFn1;
    }
    public boolean      isFnActivated () {
        return isFn;
    }
 

    // if reffrq doesn't exist during initialization (reading from procpar file)
    //reffrq is set equal to sfrq
    public float         getSpectroscoperRefFrequency(){return sfrq;}
    public float         getTimePerSample(){return at/np;}
    public boolean       isDataArrayed(){return !getArray().isEmpty();}
    
    
    // parameters for DiffTensor package
    public Float getGdiff() {return gdiff;}
    public Float getTDELTA() {return tDELTA;}
    public Float getTdelta() { return tdelta;}
    

    
    //****** convenience methods *********//
    public float [] getTime(){
        float [] time = new float[np/2];
        for (int i = 0; i < time.length ; i++) { 
            time[i] = i * at/ (np/2);
         }
        return time;
    }
    public int  getNumberOfPhaseEncodePoints(){
        return getNv();
     }
    public int  getNumberOfReadOutPoints(){
        return getNp()/2;
    }
    public int  getNumberOfPaddedReadOutPoints(){
        return getFn()/2;
    }
    public int  getNumberOfPaddedOfPhaseEncodetPoints(){
        return getFn1()/2;
    }
    // ******** convenience method *********//
    public boolean isImage(){
       if (this.isNvActive() == false){return false;}
       else if (getNv() > 1)  {return true;}
       else {return false;}
    }

    public int          getFn1 () {
        return fn1;
    }
    public float        getLengthReadOutInCM () {
        return lro;
    }
    public float        getLengthPhaseEncodeInCM() {
        return lpe;
    }
    public float        getThicknesInMM () {
        return thk;
    }

    public int[]        getSliceIndex () {
        return sliceIndex;
    }
    public int          getNav_echo() {
        return nav_echo;
    }
    public boolean      isConsecutiveSlices () {
        return consecutiveSlices;
    }
    public boolean      isNavigatorOn () {
        return navigatorOn;
    }
    public boolean      isTrailingNavigator () {
        int navEcho                 = getNav_echo();
        return  (navEcho == 1)? false:true;
    }
   /*_________________________________________*/
   
    public IMAGE_TYPE   getImageType () {
        return imageType;
    }

    public int          getNi () {
        return ni;
    }
    public int          getCf () {
        return cf;
    }
    public String       getSeqfil () {
        return seqfil;
    }
    public String       getSeqcon () {
        return seqcon;
    }
    public int          getNseg () {
        return nseg;
    }
    public float        getFpmult () {
        return fpmult;
    }
    
    public FILE_ORGANIZATION getFileOrganization(){
        if(getNi() == 0 || getNi() == 1){
            return FILE_ORGANIZATION.COMPRESSED;
        }
        else {
            return FILE_ORGANIZATION.STANDARD;
        }
        
    }

    public int getNumberOfFieldMaps () {
        return numberOfFieldMaps;
    }

    public ArrayList<Integer> getFieldMap () {
        if (fieldMap.isEmpty()){
            int numElements = getNumberOfElements();
            for (int curElem = 0; curElem < numElements; curElem++) {
                fieldMap.add(1);
            }
        
        }
        return fieldMap;
    }
    public int getElementNumberdWhenMapsAreIncluded (int elem) {
      List <Integer> fmap       = getFieldMap ();
      int totalcount            = 0;
      int nonmapcount           = 0;

     for (Integer curelem : fmap) {
           if ( isMap( curelem) == false){
              nonmapcount   += 1;
           }
           totalcount        +=1;

           if (nonmapcount == elem){
             break;
           }
     }

      return totalcount;

    }

    public static boolean isMap(int number) {
       if (number == 1){return false;}
       return true;
    }
    public boolean isEpi() {
        return getSeqfil().contains(EPI_KEY);
    }
    public boolean isNvActive() {
        return nvActive;
    }

    public String getFileSource () {
        return fileSource;
    }

    public void setSw ( float sw ) {
        this.sw = sw;
    }
    public void setAt ( float at ) {
        this.at = at;
    }
    public void setNp ( int np ) {
        this.np = np;
    }
    public void setNv ( int nv ) {
        this.nv = nv;
    }
    public void setNs ( int ns ) {
        this.ns = ns;
    }
    public void setNi ( int ni ) {
        this.ni = ni;
    }
    public void setFn ( int fn ) {
        this.fn = fn;
    }
    public void setFn1 ( int fn1 ) {
        this.fn1 = fn1;
    }
    public void setSfrq ( float sfrq ) {
        this.sfrq = sfrq;
    }
    public void setLb ( float lb ) {
        this.lb = lb;
    }
    public void setLp ( float lp ) {
        this.lp = lp;
    }
    public void setRp ( float rp ) {
        this.rp = rp;
    }
    public void setIsFn ( boolean isFn ) {
        this.isFn = isFn;
    }
    public void setIsFn1 ( boolean isFn1 ) {
        this.isFn1 = isFn1;
    }
    public void setArray ( List<String> anarray ) {
        this.array = anarray;
    }
    public void setArrayValues ( String[][] array_values ) {
        this.array_values = array_values;
    }
    public void setArraydim ( int arraydim ) {
        this.arraydim = arraydim;
    }
    public void setGdiff ( float gdiff ) {
        this.gdiff = gdiff;
    }
    public void setTDELTA ( float tDELTA ) {
        this.tDELTA = tDELTA;
    }
    public void setLro ( float lro ) {
        this.lro = lro;
    }
    public void setLpe ( float lpe ) {
        this.lpe = lpe;
    }
    public void setThk ( float thk ) {
        this.thk = thk;
    }
    public void setNseg ( int nseg ) {
        this.nseg = nseg;
    }
    public void setTdelta ( float tdelta ) {
        this.tdelta = tdelta;
    }
    public void setFpmult ( float fpmult ) {
        this.fpmult = fpmult;
    }
    public void setFileSource ( String fileSource ) {
        this.fileSource = fileSource;
    }
    public void setRfp ( float rfp ) {
        this.rfp = rfp;
    }
    public void setRfl ( float rfl ) {
        this.rfl = rfl;
    }
    public void setRfp1 ( float rfp1 ) {
        this.rfp1 = rfp1;
    }
    public void setRfl1 ( float rfl1 ) {
        this.rfl1 = rfl1;
    }

    public void setSw1 ( float sw1 ) {
        this.sw1 = sw1;
    }
    public void setTpe ( float tpe ) {
        this.tpe = tpe;
    }
    public void setGro ( float gro ) {
        this.gro = gro;
    }
    public void setGpe ( float gpe ) {
        this.gpe = gpe;
    }
    public void setCf ( int cf ) {
        this.cf = cf;
    }
    public void setNf ( int nf ) {
        this.nf = nf;
    }
    public void setNavigatorOn ( boolean navigatorOn ) {
        this.navigatorOn = navigatorOn;
    }
    public void setAxis(char axis) {
        this.axis = axis;
    }
    public void setDp(char dp) {
        this.dp = dp;
    }
    public void setPetable(List<Integer> petable) {
        this.petable = petable;
    }
    public void setNvActive(boolean nvActive) {
        this.nvActive = nvActive;
    }
    public void  setDataSize(int aDataSize) {
     dataSize = aDataSize;
    }
    public void setPpe(float ppe) {
        this.ppe = ppe;
    }
  

    public void setNav_echo(int nav_echo) {
        this.nav_echo = nav_echo;
    }



  


  

   

}
   

