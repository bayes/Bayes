/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;

/**
 *
 * @author apple
 */
public class Statistics {

    private double mean                 =   0;
    private double max                  =   0;
    private double min                  =   0;
    protected double sdev               =   0;
    private double rootMeanSqare        =   0;
    private int numelements             =   0;

    public double getSdev () {
        return sdev;
    }
    public void setSdev ( double sdev ) {
        this.sdev = sdev;
    }

    public double getMin () {
        return min;
    }
    public void setMin ( double min ) {
        this.min = min;
    }

    public double getMax () {
        return max;
    }
    public void setMax ( double max ) {
        this.max = max;
    }

    public double getMean () {
        return mean;
    }
    public void setMean ( double mean ) {
        this.mean = mean;
    }

    public double getRootMeanSqare() {
        return rootMeanSqare;
    }
    public void setRootMeanSqare(double rootMeanSqare) {
        this.rootMeanSqare = rootMeanSqare;
    }

    public int getNumelements() {
        return numelements;
    }
    public void setNumelements(int numelements) {
        this.numelements = numelements;
    }

}
