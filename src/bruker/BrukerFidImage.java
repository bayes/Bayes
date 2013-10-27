/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bruker;

/**
 *
 * @author apple
 */
public class BrukerFidImage {
    private BrukerDataInfoOLD    dataInfo                  =   null;
    private   float []        pixels                    =   null;
    private   boolean         isConstructed             =   false;
    private int               outputReadOutLength       =   0   ;
    private int               outputPhaseEncodeLength   =   0;

    public BrukerDataInfoOLD getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(BrukerDataInfoOLD dataInfo) {
        this.dataInfo = dataInfo;
    }

    public float[] getPixels() {
        return pixels;
    }

    public void setPixels(float[] pixels) {
        this.pixels = pixels;
    }

    public boolean isIsConstructed() {
        return isConstructed;
    }

    public void setIsConstructed(boolean isConstructed) {
        this.isConstructed = isConstructed;
    }

    public int getOutputReadOutLength() {
        return outputReadOutLength;
    }

    public void setOutputReadOutLength(int outputReadOutLength) {
        this.outputReadOutLength = outputReadOutLength;
    }

    public int getOutputPhaseEncodeLength() {
        return outputPhaseEncodeLength;
    }

    public void setOutputPhaseEncodeLength(int outputPhaseEncodeLength) {
        this.outputPhaseEncodeLength = outputPhaseEncodeLength;
    }
}
