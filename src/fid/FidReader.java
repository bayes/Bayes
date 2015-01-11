/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.io.*;
import static java.lang.Math.*;
import   java.nio.channels.FileChannel;
import bayes.Enums.UNITS;
import bayes.DirectoryManager;
import utilities.DisplayText;
/**
 *
 * @author apple
 */
public class FidReader {
    private Procpar         procpar;
    private FidFileHeader   fileHeader;
    private FidDescriptor   fidDescriptor   =   new FidDescriptor ();
    private File            procparFile ;
    public  File            fidFile     ;
    public  File            ffhFile     ;
    
    private boolean         doneLoading = false;
    
    private float[][]       fid_real;  
    private float[][]       fid_imag;
    private float[][]       unPhasedRealSpectra;
    private float[][]       unPhasedImagSpectra;
    private float[][]       realSpectra;
    private float[][]       imagSpectra;
    private float[][]       amplitudeSpectra;
    private float[][]       powerSpectra;
    private double[]        noiseThreshHold;


    public void kill (){
        fid_real            = null;
        fid_imag            = null;
        unPhasedRealSpectra = null;
        unPhasedImagSpectra = null;
        realSpectra         = null;
        imagSpectra         = null;
        amplitudeSpectra    = null;
        powerSpectra        = null;
    }


    public FidReader(){

            }
    public FidReader(File dir, boolean loadedFromFidViewer){
          doneLoading                   =   false;
          procparFile                   =   DirectoryManager.getProcparFile(dir);
          fidFile                       =   DirectoryManager.getFidFile(dir);
          ffhFile                       =   DirectoryManager.getFidDesciptorFile();
          //ffhFile                       =   new File(dir, bayes.BayesManager.FFH );
      
      if (fidFile.exists()== false){
          System.out.println("Fid file "+ fidFile.getAbsolutePath()+ "\n"+" doesn't exist in " + dir.getPath ());
          return;
      }
      
      if (procparFile.exists()== false){
          System.out.println("Procpar file"+procparFile.getAbsolutePath()+ "\n"+" doesn't exist in " + dir.getPath ());
          return;
      }
      
      procpar                       =   new Procpar( procparFile.getPath());
      fidDescriptor                 =    FidIO.loadFromDiskBackedByProcpar(ffhFile, procpar );

      // make sure fid descriptor is upto date or, if doesn't already exist, is created
      FidIO.storeToDisk(fidDescriptor , ffhFile );
      
      if (procpar.isImage() == true) {
          DisplayText.popupErrorMessage("The fid file in " + dir.getPath () + " \n directory is " +
                                                "an image. Abort load.");
          return;
      }
      
      try{
        FileInputStream     fin =  new FileInputStream(fidFile);
        readFidFile(fin);
        fin.close();



        File textFile       =   DirectoryManager.writeTextFile(fidFile);
        if (textFile == null || textFile.exists() == false){
            throw new IOException ("Failed to write text file.");
        }
      } catch(IOException exp){
        exp.printStackTrace();
        return;
      }
      
      doneLoading = true;
    }
    
    

    private void readFidFile(FileInputStream fin) throws IOException{
        FileChannel channel         =   fin.getChannel();
        fileHeader                  =   FidFileHeader.readFileHeader(channel);
        int nTotalTraces            =   getNumberOfTotalTraces();

        fid_real                    =   new float   [nTotalTraces][];
        fid_imag                    =   new float   [nTotalTraces][];
        unPhasedRealSpectra         =   new float   [nTotalTraces][];
        unPhasedImagSpectra         =   new float   [nTotalTraces][];
        realSpectra                 =   new float   [nTotalTraces][];
        imagSpectra                 =   new float   [nTotalTraces][];
        amplitudeSpectra            =   new float   [nTotalTraces][];
        powerSpectra                =   new float   [nTotalTraces][];
        noiseThreshHold             =   new double  [nTotalTraces];

        int numberOfBlocks          =   fileHeader.nblocks;
        int numberOfTRacesPerBlock  =   fileHeader.ntraces;
        int curData                 =   0;
        for (int curBlock = 0;  curBlock <  numberOfBlocks ;  curBlock++) {
                 FidBlockHeader blockHeader = FidBlockHeader.readBlockHeader(channel);
                for (int curTrace = 0; curTrace < numberOfTRacesPerBlock; curTrace++) {
                    FidData   fidData           = FidData.readData( channel, fileHeader, blockHeader);

                    curData                     =   curBlock * numberOfTRacesPerBlock + curTrace;

                    fid_real [curData]          =   new float [ fidData.real.length];
                    fid_imag [curData]          =   new float [ fidData.imag.length];

                    // save raw data
                    System.arraycopy (fidData.real, 0,  fid_real [curData] , 0, fidData.real.length);
                    System.arraycopy (fidData.imag, 0,  fid_imag [curData] , 0, fidData.imag.length);
                 

                }
         }


     }


    public boolean computeSpectralData(boolean doFTT) {
        int numTraces           =   getNumberOfTotalTraces();
        int numOfPnts           =   getUserFn()/2;
        float [] [] spectrum    ;
        
        try{
            for (int i = 0;  i <  numTraces;  i ++) {

                if ( doFTT ){
                    FidData fidData             =   applyWeightingInTimeDomain(   fid_real[i] ,fid_imag[i], procpar);
                    spectrum                    =   doFFT(fidData,procpar);


                    unPhasedRealSpectra[i]      =   new float [numOfPnts];
                    unPhasedImagSpectra[i]      =   new float [  numOfPnts];
                    System.arraycopy ( spectrum[0], 0,  unPhasedRealSpectra[i] , 0, numOfPnts);
                    System.arraycopy ( spectrum[1], 0,  unPhasedImagSpectra[i] , 0, numOfPnts);

                }
                else {
                   spectrum         =   new float [2][numOfPnts];
                   System.arraycopy ( unPhasedRealSpectra[i], 0,  spectrum [0]  , 0, numOfPnts);
                   System.arraycopy ( unPhasedImagSpectra[i], 0,  spectrum [1]  , 0, numOfPnts);

                }

                spectrum                    =   applyVnmrPhasing(spectrum, procpar, getUserFn());
                spectrum                    =   applyUserPhasing(spectrum);

                realSpectra [i]             =   spectrum[0];
                imagSpectra[i]              =   spectrum[1];



                noiseThreshHold[i]          =   calculateNoiseThreshold (i);

             }
                computeAmplitudeAndPowerSpectra();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
     }
    public float [][] getProcparPhaseSpectrumSpectrum(int traceNumber) {
        int numTraces           =   getNumberOfTotalTraces();
        int numOfPnts           =   getUserFn()/2;
        float [] [] spectrum    ;


       spectrum                 =   new float [2][numOfPnts];
       System.arraycopy ( unPhasedRealSpectra[traceNumber], 0,  spectrum [0]  , 0, numOfPnts);
       System.arraycopy ( unPhasedImagSpectra[traceNumber], 0,  spectrum [1]  , 0, numOfPnts);
       spectrum                 =   applyVnmrPhasing(spectrum, procpar, getUserFn());
       return  spectrum ;
     }


    public double calculateNoiseThreshold (int  curTrace){
        float[] real            =   getRealSpectra()[curTrace];
        float[] imag            =   getImagSpectra()[curTrace];

      return calculateNoiseThreshold(real, imag);
  }  
    public double calculateNoiseThreshold (  float[] real , float[] imag ){
        int length              =   real.length;

        double sd               = 0.0;
        for (int i = 0; i < length; i++) {

            sd   = sd + log(sqrt(real[i]*real[i] + imag[i]* imag[i]));

        }

       sd = 2 * exp(sd / length);

      return sd;
  }


    public static void main (String [] args){
    }


    public FidData          applyWeightingInTimeDomain( float [] real, float [] imag , Procpar aprocpar ){ 
         
           int   n                  =   aprocpar.getNp ()/2;
           float timedelta          =   aprocpar.getAt ()/n;
           FidData fd               =   new FidData();
           float tau                =   getUserLb();
           
           fd.real = new float[n];
           fd.imag = new float[n];
           
        
           for (int i = 0; i < n; i++) {
            float attenuation  = (float)exp(- i * PI*timedelta*tau);
     
             fd.real[i]             = real[i] * attenuation;
             fd.imag[i]             = imag[i] * attenuation;
             
            }
        return fd;
    }
    public float[][]        applyUserPhasing(float [][] spec){
         
          float[] freq        =     getFrequencyInHertz();
          float phi           =     getPhase0();
          float t             =     getDelay();

          return applyPhasing (spec, freq, phi, t);
    }
    public float[]          getFrequencyInHertz(  ) {
        float ref               =  getReferenceFreqInHertz ();
        int n                   =  getUserFn()/2;
        float sw                =  procpar.getSw ();
        float[] frequency       =  new float[n];

        // calculate frequencies in Hertz
        for (int i = 0; i < n; i++) {
             frequency[i] = sw/2 - (n-i) * sw/n - ref ;
        }
        return frequency;
   }
    public static float[][] applyVnmrPhasing(float [][] spec, Procpar aprocpar, int userFn){
        int   n     =   userFn/2 ;
        float rp    =   aprocpar.getRp();
        float lp    =   aprocpar.getLp();
        float dteta = (float)(2*PI)/n;


        for (int i = 0; i < n ; i++) {
             float w        = - (float)PI +  i * dteta;
             float phase    = (lp*n - 2*lp)/(2*(n-1)) + rp + lp* w* n/(n-1)/(2*(float)PI);


             float real  = spec [0][i];
             float imag  = spec [1][i];
             spec [0][i] =(float) ( real*cos (phase) + imag* sin(phase));
             spec [1][i] =(float) ( imag*cos (phase) - real* sin(phase));
        };
        return spec;
    }
    public static float[][] applyPhasing( float [][] spec, float[] freq ,   float phase0, float timedelay){
          float [] realSpectrum                     =   spec[0];
          float [] imagSpectrum                     =   spec[1];
          int   length                              =  Math.min(realSpectrum.length,  imagSpectrum.length);


          for (int i = 0; i < length; i++) {
                float f     = freq[i];
                float phase = phase0 - 2 * (float) PI * timedelay *f;

                float real  = realSpectrum[i];
                float imag  = imagSpectrum[i];
                spec [0][i] =(float) ( real*cos (phase) + imag* sin(phase));
                spec [1][i] =(float) ( imag*cos (phase) - real* sin(phase));
            }
           return spec;
    }

    public  float[][]       doFFT(FidData fd, Procpar aprocpar){
       
        return doFFT(fd.real, fd.imag,aprocpar );
    }
    public  float[][]       doFFT( float[] real, float [] imag , Procpar aprocpar){
        int n               =   aprocpar.getNp()/2 ;
        int n_pad           =   getUserFn()/2 ;
        /*
        if (n_pad < n){
            String err      =   String.format("Padded length (%s) in FFT must be equal or larger than original data size (%s).",
                    n_pad ,  n );
            throw new IllegalArgumentException ( err  );
        }
         * 
         */
   


        float [][]spec      =   new float [2][];
       
        spec[0]             =   real;
        spec[1]             =   imag;
     
      
        spec                =   utilities.cFFT.normalize(spec, n);
        spec                =   utilities.cFFT. truncateOrPad(spec, n_pad);

        spec                =   utilities.cFFT.shift    (spec, (float)Math.PI );
        spec                =   utilities.cFFT.fft      (spec, 1);
        //spec                =   utilities.cFFT.fft      (spec, procpar.getFftSign());


        
        return spec;
    }

    
    public void computeAmplitudeAndPowerSpectra(){
            int numberOfTraces      = getNumberOfTotalTraces ();
            int pointsPerTrace      = getRealSpectra()[0].length;
            
            amplitudeSpectra        = new float [numberOfTraces][pointsPerTrace];
            powerSpectra            = new float [numberOfTraces][pointsPerTrace];
            
            float re, im;
            
            for (int i = 0; i < numberOfTraces ; i++) {
                for (int j = 0; j <pointsPerTrace; j++) {
                    re = getRealSpectra() [i][j];
                    im = getImagSpectra() [i][j];
                    amplitudeSpectra [i][j] = (float) Math.hypot(re, im);
                    powerSpectra [i][j]     = (float) (Math.pow(re,2)+  Math.pow(im,2));
                    
                }
            
            }
                   
    
    }

    public String convertToText(){
        StringBuilder buf       =   new StringBuilder();
        String eol              =   System.getProperty("line.separator");
         float[][] real = getFidReal();
            float[][] imag = getFidImag();
            int numTraces = getNumberOfTotalTraces();
            for (int curTrace = 0; curTrace < numTraces; curTrace++) {
                for (int i = 0; i < real[curTrace].length; i++) {
                    buf.append( real[curTrace][i]);
                    buf.append( " ");
                    buf.append( imag[curTrace][i]);
                    buf.append( " ");
                    buf.append( eol);
                }
            }
          return buf.toString();
    }


// <editor-fold defaultstate="collapsed" desc=" GETTERS ">
    public int getNumberOfTotalTraces () {
        if ( fileHeader == null ) {
            return 0;
        }
        return fileHeader.ntraces * fileHeader.nblocks;
    }
    public FidDescriptor getFidDescriptor () {
        return fidDescriptor;
    }
    public boolean isFidReaderLoaded () {
        if ( isDoneLoading() == false ) {
            return false;
        }
        if ( procpar == null ) {
            return false;
        }
     
        
        File curFidDir = bayes.DirectoryManager.getFidDir();
        if ( curFidDir == null || curFidDir.exists() == false ) {
            return false;
        }
        
        return true;
    }


    
    public File getProcparFile () {
        return procparFile;
    }
    public Procpar getProcpar () {
        return procpar;
    }

    public float[][] getAmplitudeSpectra () {
        return amplitudeSpectra;
    }
    public float[][] getPowerSpectra () {
        return powerSpectra;
    }
    public float[][] getRealSpectra () {
        return realSpectra;
    }
    public float[][] getImagSpectra () {
        return imagSpectra;
    }
    
    public boolean isDoneLoading () {
        return doneLoading;
    }
   
    
    
 
    public float    getUserLb () {
        return  fidDescriptor.getUserLB();
    }
    public  int      getUserFn() {
        return fidDescriptor.getUserFn();
    }
    public float    getPhase0 () {
        return fidDescriptor.getPhase();
    }
    public float    getDelay () {
        return fidDescriptor.getDelay();
    }
    public float    getReferenceFreqInHertz () {
        return fidDescriptor.getReferenceInHertz();
    }
    public UNITS    getUnits () {
         return fidDescriptor.getUnits();
    }
   
    public double[]         getNoiseThreshHold () {
        return noiseThreshHold;
    }
    
    public float[][] getFidReal () {
        return fid_real;
    }
    public float[][] getFidImag () {
        return fid_imag;
    }

   


    public  void setUserLb ( float aLb ) {
        fidDescriptor.setUserLB(aLb);
    }
    public  void setUserFn ( int aFn ) {
        fidDescriptor.setUserFn(aFn);
    }
    public  void setDelay ( float aDelay ) {
      fidDescriptor.setDelay(aDelay);
    }
    public  void setPhase0 ( float phase ) {
        fidDescriptor.setPhase(phase);
    }
    public  void setReferenceFreqInHertz ( float fHertz ) {
       fidDescriptor.setReferenceInHertz(fHertz);
    }
    public  void setUnits    ( UNITS newUnits ) {
        setUnits (newUnits, true);
    }
    public  void setUnits    ( UNITS newUnits, boolean clearBayesAnalyze) {
          // record current units
          UNITS oldUnits   =   fidDescriptor.getUnits();


          if (clearBayesAnalyze){
              // clean BayesAnalyze and FidModel Files
              bayes.DirectoryManager.cleanFidModelAndBayesAnalyzeDirs();

              // clear resonances
              Resonance.getResonanceList().clear();

               // delete fid models from memory
              FidModelViewer.getInstance().unloadData();
          }
          // write to ffh file
          fidDescriptor.setUnits(newUnits) ;
          if (ffhFile == null){
               ffhFile     = DirectoryManager.getFidDesciptorFile();
          }
          FidIO.storeToDisk(fidDescriptor, ffhFile  );

          // fire untisChangeEvnts
          bayes.BayesManager.fireFidUnitsChanged(oldUnits, newUnits);
    }
    public  void storePresistently(){
        if (ffhFile != null){ FidIO.storeToDisk(fidDescriptor, ffhFile  );}
        
    }

    public void setFidDescriptor ( FidDescriptor afidDescriptor ) {
        fidDescriptor = afidDescriptor;
    }


  
// </editor-fold> 
    
    
}
