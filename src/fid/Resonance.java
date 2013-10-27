/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
public class Resonance implements  Cloneable, java.io.Serializable{

//, FrequencyDrawable   
   // !!!!!!!!!! ALL FREQUENCIES ARE ALWAYS IN HERTZ
    
    private double freqInitlVal         = Double.NaN;
    private double freqFinalVal         = Double.NaN;
    private double rateInitlVal         = Double.NaN;
    private double rateFinalVal         = Double.NaN;
    private double j1InitlVal           = Double.NaN;
    private double j1FinalVal           = Double.NaN;
    private double j2InitlVal           = Double.NaN;
    private double j2FinalVal           = Double.NaN;
    private int firstOrder              = 1;
    private int secondOrder             = 1;
    private final UNITS freqUnits       = UNITS.HERTZ;
    private RESONANCE_MODEL resonanceModel = RESONANCE_MODEL.CORRELATED;
    private  ArrayList <Double> amplitudes =  new ArrayList <Double>(); 
    private static ArrayList <Resonance>  resonanceList  =  new ArrayList <Resonance>(); 
    
    
    public String getOrder() {return "(" + getFirstOrder() + "," + getSecondOrder() + ")";}
    public String getName() {
        String str = SPIN_DEGENERACY.getSpinDegeneracy(getFirstOrder()).getName();
        if (getSecondOrder() > 1) { 
            str = str + " Of " + SPIN_DEGENERACY.getSpinDegeneracy(getSecondOrder()).getName()+ "s";
       }
        return str;
    }
    
    
    
    
    public int getJt (){return 0;}
    public int getJs (){return getSecondOrder ();}
   // public int getJp (){return j2FinalVal;}
    
   
    public UNITS getUnits () {
        return UNITS.HERTZ;
    }
    
    
       
   @Override
   public Resonance clone(){
        try {
           return (Resonance) super.clone();
        } catch (CloneNotSupportedException ex) {
           return null;
        }
    }
   @Override
   public String toString(){
       StringBuffer sb = new StringBuffer();
       sb.append("ResonanceModel = "+ getResonanceModel());
       sb.append("\n");
       
       sb.append("firstOrder = "+ getFirstOrder());
       sb.append("\n");
       
       sb.append("secondOrder = "+ getSecondOrder());
       sb.append("\n");
       
       sb.append("freqInitlVal = "+ this.getFreqInitlVal());
       sb.append("\n");
       
       sb.append("freqFinalVal = "+ this.getFreqFinalVal());
       sb.append("\n");
       
       sb.append("rateInitlVal = "+ this.getRateInitlVal());
       sb.append("\n");
       
       sb.append("rateFinalVal = "+ this.getRateFinalVal());
       sb.append("\n");
       sb.append("j1InitlVal = "+ this.getJ1InitlVal());
       sb.append("\n");
       
       sb.append("j1FinalVal = "+ this.getJ1FinalVal());
       sb.append("\n");
       
       sb.append("j2InitlVal = "+ this.getJ2InitlVal());
       sb.append("\n");
       
       sb.append("j2FinalVal = "+ this.getJ2FinalVal());
       sb.append("\n");
       
       sb.append("Frequency Units = "+ this.getUnits().toString());
       sb.append("\n");
       sb.append("\n");
       
       return sb.toString();
   
   }
   
   
   
   
    
    public static  void sort( List  <Resonance> list){
        Collections.sort(list,new  FrequencyComparator() );
    }
    
// <editor-fold defaultstate="collapsed" desc=" Getters and Setters ">
    public static ArrayList<Resonance> getResonanceList () {
        return resonanceList;
    }
    public static void updateResonances(  float refFreqInHertz){

       // update resonance frequencies
      // This will update resonances for BayesAnalyze package
        double val;
        for (Resonance resonance : Resonance.getResonanceList()) {
            val    = resonance.getFreqFinalVal();
            val    = val + refFreqInHertz;
            resonance.setFreqFinalVal(val);

            val    = resonance.getFreqInitlVal();
            val    = val + refFreqInHertz;
            resonance.setFreqInitlVal(val);
        }
    }


    public static void setResonanceList ( ArrayList<Resonance> aResonanceList ) {
        resonanceList = aResonanceList;
    }

    public double getFreqInitlVal () {
        return freqInitlVal;
    }

    public void setFreqInitlVal ( double freqInitlVal ) {
        this.freqInitlVal = freqInitlVal;
    }

    public double getFreqFinalVal () {
        return freqFinalVal;
    }

    public void setFreqFinalVal ( double freqFinalVal ) {
        this.freqFinalVal = freqFinalVal;
    }

    public double getRateInitlVal () {
        return rateInitlVal;
    }

    public void setRateInitlVal ( double rateInitlVal ) {
        this.rateInitlVal = rateInitlVal;
    }

    public double getRateFinalVal () {
        return rateFinalVal;
    }

    public void setRateFinalVal ( double rateFinalVal ) {
        this.rateFinalVal = rateFinalVal;
    }

    public double getJ1InitlVal () {
        return j1InitlVal;
    }

    public void setJ1InitlVal ( double j1InitlVal ) {
        this.j1InitlVal = j1InitlVal;
    }

    public double getJ1FinalVal () {
        return j1FinalVal;
    }

    public void setJ1FinalVal ( double j1FinalVal ) {
        this.j1FinalVal = j1FinalVal;
    }

    public double getJ2InitlVal () {
        return j2InitlVal;
    }

    public void setJ2InitlVal ( double j2InitlVal ) {
        this.j2InitlVal = j2InitlVal;
    }

    public double getJ2FinalVal () {
        return j2FinalVal;
    }

    public void setJ2FinalVal ( double j2FinalVal ) {
        this.j2FinalVal = j2FinalVal;
    }

    public int getFirstOrder () {
        return firstOrder;
    }

    public void setFirstOrder ( int firstOrder ) {
        this.firstOrder = firstOrder;
    }

    public int getSecondOrder () {
        return secondOrder;
    }

    public void setSecondOrder ( int secondOrder ) {
        this.secondOrder = secondOrder;
    }

   
    public  ArrayList<Double> getAmplitudes () {
        return amplitudes;
    }
    public  double [] getAmplitudesAsArray () {
        ArrayList<Double> ampl = getAmplitudes ();
        double [] ampArray   = new double [ampl.size()];
        
        for (int i = 0; i < ampArray.length; i++) {
             ampArray[i] = ampl.get(i);
        }
        return ampArray;
    }
    

    public  void setAmplitudes ( ArrayList<Double> aAmplitudes ) {
        amplitudes = aAmplitudes;
    }


    public RESONANCE_MODEL getResonanceModel () {
        return resonanceModel;
    }

    public void setResonanceModel ( RESONANCE_MODEL resonanceModel ) {
        this.resonanceModel = resonanceModel;
    }

// </editor-fold>
   
    
    static class FrequencyComparator implements Comparator{
        public int compare(Object obj1 , Object obj2){
            Resonance r1  =  ( Resonance) obj1;
            Resonance r2  =  ( Resonance) obj2;
            
            return (int)Math.signum(r1.getFreqFinalVal() - r2.getFreqFinalVal()); 
        }
        public boolean equals(Object obj)  {
            return super.equals(obj);
         }        
    
    }
}
