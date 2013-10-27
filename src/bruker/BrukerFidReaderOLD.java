/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bruker;

import fid.BrukerProcess;
import fid.FidData;
import fid.FidDescriptor;
import java.io.*;
import utilities.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author apple
 */
public class BrukerFidReaderOLD implements BrukerConstants {

    private BrukerDataInfoOLD dataInfo         = new BrukerDataInfoOLD();
    private FidDescriptor fidDescriptor     = new FidDescriptor();
    private boolean loaded = false;
    private float[][] fid_real;
    private float[][] fid_imag;

    public BrukerFidReaderOLD(File fidFile) {

        String message = null;
        File dir = fidFile.getParentFile();

        if (fidFile.exists() == false) {

            message = String.format("Fid file %s\n" +
                    "doesn't exist. Exit.",
                    fidFile.getAbsolutePath());
            DisplayText.popupDialog(message);

            return;

        }


        dataInfo .readFilesInDir(dir);




        if (dataInfo .isLoaded() == false) {
            return;
        }
     

        try {
            FileInputStream fin = new FileInputStream(fidFile);
            BufferedInputStream bin = new BufferedInputStream(fin, (int) Math.pow(2, 10));

            readFidFileNIO(fin);

            bin.close();
            fin.close();
        }
        catch (IOException exp) {
            exp.printStackTrace();
            return;
        }

        this.setLoaded(true);
    }

    private void readFidFileNIO(FileInputStream fin) throws IOException {
        int nTotalTraces            =   this.getParamsReader().getNumberOfTraces();
        FileChannel channel         =   fin.getChannel();

        fid_real = new float[nTotalTraces][];
        fid_imag = new float[nTotalTraces][];


        for (int curTrace = 0; curTrace < nTotalTraces; curTrace++) {
            FidData fidData = readData(channel);


            // flip sign for imaginary component
            // to make Bruker data equvalent to Varian data
            flipImagSign(fidData);


            fid_real[curTrace] = new float[fidData.real.length];
            fid_imag[curTrace] = new float[fidData.imag.length];



            // save raw data
            System.arraycopy(fidData.real, 0, fid_real[curTrace], 0, fidData.real.length);
            System.arraycopy(fidData.imag, 0, fid_imag[curTrace], 0, fidData.imag.length);
        }

        // apply digital shift
        // all points with index < shift are deleted
        applyDgitalShfit();
        this.dataInfo.setNp(fid_real[0].length * 2);
    }

    public FidData getTau(float[] real, float[] imag) {
        int dataLength          = real.length;
        double[][] data         = new double[2][dataLength];

        for (int curPoint = 0; curPoint < dataLength; curPoint++) {
            data[0][curPoint] = real[curPoint];
            data[1][curPoint] = imag[curPoint];


        }

        double[][] fft          =   utilities.cFFT.fft(data,   1);
        double aDelta           =   10;
        double power            =   BrukerProcess.calcTotalPower(data);

        int n                   =   1024;
        double[] work           =   new double[n];

        for (int i = 0; i < n; i++) {
            double t = 0 + i / n;
            double prob = BrukerProcess.aLogpSmoothedData(t, aDelta, fft, power);
            work[i] = prob;

            System.out.println(prob);
        }

        double maxTau = utilities.MathFunctions.findMax(work);

        return null;

    }

    public FidData getTotalTau() {
        int dataLength          =   this.getParamsReader().getNp() / 2;
        int nTraces             =   this.getParamsReader().getNumberOfTraces();


        int n                   =   100;
        double[] sumProb        =   new double[n];
        double[] phases         =   new double[nTraces];
/*
        for (int curTrace = 0; curTrace < nTraces; curTrace++) {

            double[][] data = new double[2][dataLength];
            for (int curPoint = 0; curPoint < dataLength; curPoint++) {
                data[0][curPoint] = fid_real[curTrace][curPoint];
                data[1][curPoint] = fid_imag[curTrace][curPoint];
            }

            double[][] fft      =   utilities.cFFT.fft(data,  1);
            double aDelta       =   10;
            double power        =   BrukerProcess.calcTotalPower(data);

            double maxTau       =   0;
            double[] work       =   new double[n];

            for (int i = 0; i < n; i++) {
                double t        =   0 + i * 0.01 / n;
                double prob     =   BrukerProcess.aLogpSmoothedData(t, aDelta, fft, power);
                sumProb[i]      +=  prob;

                System.out.println(i);
                utilities.MathFunctions.findMaxLoc(sumProb);

            }

            double phase        =   BrukerProcess.calculatePhase(fft);
            phases[curTrace]    =   phase;


        }

        
        System.out.println("");
        for (int i = 0; i <sumProb.length; i++) {
            System.out.println(sumProb [i]);

        }
       
        double maxTau = utilities.MathFunctions.findMaxLoc(sumProb);
        System.out.println("maxtau "+ maxTau);
  */
      double maxTau = 0.408;
       //maxTau = 1;
        System.out.println("maxTau == " + maxTau);
        for (int curTrace = 0; curTrace < nTraces; curTrace++) {
            double[][] data = new double[2][dataLength];
            for (int curPoint = 0; curPoint < dataLength; curPoint++) {
                data[0][curPoint] = fid_real[curTrace][curPoint];
                data[1][curPoint] = fid_imag[curTrace][curPoint];
            }

            double[][] fft          =   utilities.cFFT.fft(data, -  1);
            double phase            =  0;//phases[curTrace];
            double[][] phasedFFT    =   BrukerProcess.applyPhase(fft, maxTau, phase);
            double[][] ifft         =   utilities.cFFT.fft(phasedFFT, 1);

            for (int curPoint = 0; curPoint < dataLength; curPoint++) {
             fid_real[curTrace][curPoint] = (float) ifft[0][curPoint];
             fid_imag[curTrace][curPoint] = (float) ifft[1][curPoint];
            }
        }

   
       /* System.out.println("PHASES");
        for (int i = 0; i < phases.length; i++) {
        //     System.out.println(phases[i]);
        }
        */
        return null;

    }

    public int getMaxTauLoc() {
        int dataLength          =   this.getParamsReader().getNp() / 2;
        int nTraces             =   this.getParamsReader().getNumberOfTraces();


        int n                   =   1000;
        double[] sumSquares     =   new double[n];

        for (int curTrace = 0; curTrace < nTraces; curTrace++) {

            for (int curPoint = 0; curPoint < dataLength; curPoint++) {
                double re       = fid_real[curTrace][curPoint];
                double im       = fid_imag[curTrace][curPoint];

                sumSquares[curPoint] += re * re + im + im;
            }
        }
        int maxIndx = utilities.MathFunctions.findMaxLoc( sumSquares);

        return maxIndx;

    }

    public void applyDgitalShfit() {
        int nTraces             =   this.getParamsReader().getNumberOfTraces();
        // if previous attmept to find computeShift in Bruker data have failed,
        // compute computeShift from fist fid trace, using power maximum to
        // determine the computeShift
        if (this.dataInfo.getDigitalShift() < 0 ) {

                int ashift =  getMaxTauLoc();
                this.dataInfo.setDigitalShift(ashift);


        }

        int shift = this.dataInfo.getDigitalShift();
        System.out.println(" Bruker fid is cicrulary shifted by "+shift + " points" );
        if (shift < 0) {
                DisplayText.popupMessage("Failed to to remove build-up points in Bruker fid.\n" +
                        "This data may be inadequate for further analysis.");
        }




        if (shift == 0) {return;}

        for (int curTrace = 0; curTrace < nTraces; curTrace++) {
            fid_real[curTrace] =   rotate(fid_real[curTrace], shift);
            fid_imag[curTrace] =   rotate(fid_imag[curTrace], shift);
        }



    }


    public FidData readData(FileChannel channel) throws IOException {

        FidData fidData     = new FidData();
        int totalPonts      = getParamsReader().getNumberOfRealAndImagPointsPerTrace();
        fidData.real        = new float[totalPonts / 2];
        fidData.imag        = new float[totalPonts / 2];
        DATA_FORMAT df      = this.getParamsReader().getDataFormat();
        ByteOrder bOrder        = this.getParamsReader().getByteOrder();
        int length = this.getParamsReader().getNumberOfBytesPerTrace();
            ByteBuffer buffer = ByteBuffer.allocateDirect(length);

        buffer.order(bOrder);

        channel.read(buffer);
        buffer.rewind();
        switch (df) {
            case GO_16_BIT_SGN_INT:
                for (int i = 0; i < totalPonts / 2; i++) {
                    float re = buffer.getShort();
                    float im = buffer.getShort();
                    fidData.real[i] = re;
                    fidData.imag[i] = im;
                }
                break;
            case GO_32BIT_SGN_INT:
                for (int i = 0; i < totalPonts / 2; i++) {
                    float re = buffer.getInt();
                    float im = buffer.getInt();
                    fidData.real[i] = re;
                    fidData.imag[i] = im;
                }
                break;
            case GO_32_BIT_FLOAT:
                for (int i = 0; i < totalPonts / 2; i++) {
                    float re = buffer.getFloat();
                    float im = buffer.getFloat();
                    fidData.real[i] = re;
                    fidData.imag[i] = im;
                }
        }





        return fidData;
    }

    public FidData rotateFid(FidData fidData, int shift) {
        FidData fd          =   new FidData();
        fd.real             =   rotate(fidData.real, shift);
        fd.imag             =   rotate(fidData.imag, shift);
        return fd;

    }
    public float [] rotate( float[] data, int shift) {

        int length          =   data.length;
        int newLength       =   length - shift;
        float [] out        =   new float[ newLength ];

        for (int curPoint   = shift; curPoint < length; curPoint++) {
            int index       = curPoint - shift;
            out[index]      = data[curPoint];

        }
        return out;
    }


    public FidData flipImagSign(FidData fidData) {
        float[] reals = fidData.real;
        float[] imags = fidData.imag;

        int length = Math.min(reals.length, imags.length);
        FidData fd = new FidData(length);

        for (int i = 0; i < length; i++) {
            float re = reals[i];
            float im = imags[i];

            fidData.real[i] = re;
            fidData.imag[i] = -im;
        }

        return fd;

    }

    public int computeShift(FidData fidData) {
        float[] reals = fidData.real;
        float[] imags = fidData.imag;
        int shift = -1;
        double max = 0;

        int length = Math.min(reals.length, imags.length);

        for (int curPos = 0; curPos < length; curPos++) {
            float re = reals[curPos];
            float im = imags[curPos];
            double curMax = re * re + im * im;
            if (curMax > max) {
                max = curMax;
                shift = curPos;
            }
        }

        return shift;

    }

    public BrukerDataInfoOLD getParamsReader() {
        return dataInfo;
    }

    public void setParamsReader(BrukerDataInfoOLD paramsReader) {
        this.dataInfo = paramsReader;
    }

    public FidDescriptor getFidDescriptor() {
        return fidDescriptor;
    }

    public void setFidDescriptor(FidDescriptor aFidDescriptor) {
        fidDescriptor = aFidDescriptor;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public float[][] getFid_real() {
        return fid_real;
    }

    public void setFid_real(float[][] fid_real) {
        this.fid_real = fid_real;
    }

    public float[][] getFid_imag() {
        return fid_imag;
    }

    public void setFid_imag(float[][] fid_imag) {
        this.fid_imag = fid_imag;
    }
}
