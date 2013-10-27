/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;
import bayes.ParameterPrior;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import static bayes.Enums.*;
/**
 *
 * @author apple
 */
public class Metabolite implements  Cloneable, java.io.Serializable, fid.FrequencyDrawable, Comparable {
//, FrequencyDrawable 
    public static final String RES_SITE_NAME = "marked";
    protected boolean loadedFromFile = true;

    
    
    protected String siteName;
    protected String degeneracyName;
    protected String abbreviation;
    protected RESONANCE_MODEL resonanceModel;
    protected String amplitudeRatios ;
    


    protected ParameterPrior frequency;
    protected ParameterPrior rate;
    protected ParameterPrior primaryCoupling;
    protected ParameterPrior secondaryCoupling;
    protected ParameterPrior tertiaryCoupling;
    protected int primary           =1;
    protected int secondary         =1;
    protected int tertiary          =1;
    protected int jp                =1;
    protected int js                =1;
    protected int jt                =1;
    
 
   
    // !!!! Metabolites frequency_units are always in PPM !!!
    public static UNITS frequencyUnits    =   UNITS.PPM;
    
    // !!!! Coupling constants units are always in Hertz !!!
    static final UNITS couplingConstantUnits   =   UNITS.HERTZ;


    public StringBuilder generateDegeneracyName(){
         StringBuilder sb  = new  StringBuilder();
         
         sb.append(SPIN_DEGENERACY.getName(primary));
         if (secondary == 1 ) {return sb;}
         
         sb.append(" of ");
         sb.append(SPIN_DEGENERACY.getName(secondary));
         sb.append("s");
         if (tertiary == 1 ) {return sb;}
         
         
         sb.append(" of ");
         sb.append(SPIN_DEGENERACY.getName(tertiary));
         sb.append("s");
         return sb;
    
    }
  
    
    public double getPrimaryFrequency (){
            ParameterPrior freq  = getFrequency();
            return freq.mean;
    }
    public double getPrimaryCouplingFrequency ()    {
        if (primaryCoupling == null) {return 0;}
        else{return primaryCoupling.mean;}
    }    
    public double getSecondaryCouplingFrequency ()  {
        if (secondaryCoupling == null) {return 0;}
        else{return secondaryCoupling.mean;
        }
    }
    public double getTertiaryCouplingFrequency  ()  {
        if (tertiaryCoupling == null) {return 0;}
        else{return tertiaryCoupling.mean;}
    }
    
    public int getPrimaryDegeneracy ()      {return this.getPrimary ();}
    public int getSecondaryDegeneracy ()    {return this.getSecondary();}
    public int getTertiaryDegeneracy ()     {return this.getTertiary();}
   
    public int getJt () {
        return jt;
    }
    public void setJt ( int jt ) {
        this.jt = jt;
    }
    public int getJs () {
        return js;
    }
    public void setJs ( int js ) {
        this.js = js;
    }
    public int getJp () {
        return jp;
    }
    public void setJp ( int jp ) {
        this.jp = jp;
    }
    public int getTertiary () {
        return tertiary;
    }
    public void setTertiary ( int tertiary ) {
        this.tertiary = tertiary;
    }
    public int getSecondary () {
        return secondary;
    }
    public void setSecondary ( int secondary ) {
        this.secondary = secondary;
    }
    public int getPrimary () {
        return primary;
    }
    public void setPrimary ( int primary ) {
        this.primary = primary;
    }
    public  ParameterPrior getRate () {
        return rate;
    }
    public void setRate (  ParameterPrior rate ) {
        this.rate = rate;
    }
    public  ParameterPrior getFrequency () {
        return frequency;
    }
    public void setFrequency (  ParameterPrior frequency ) {
        this.frequency = frequency;
    }
    public String getSiteName () {
        return siteName;
    }
    public void setSiteName ( String siteName ) {
        this.siteName = siteName;
    }
    public String getAbbreviation () {
        return abbreviation;
    }
    public void setAbbreviation ( String abbreviation ) {
        this.abbreviation = abbreviation;
    }
    public String getDegeneracyName () {
        return degeneracyName;
    }
    public void setDegeneracyName ( String degeneracyName ) {
        this.degeneracyName = degeneracyName;
    }
     public RESONANCE_MODEL getResonanceModel () {
        return  resonanceModel;
    }
    public void setResonanceModel ( RESONANCE_MODEL  aresonanceModel ) {
        this. resonanceModel =  aresonanceModel;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("siteName = "+siteName );
        sb.append("\n");
        sb.append("abbreviation = "+abbreviation );
        sb.append("\n");
        sb.append("degeneracyName = "+ degeneracyName );
        sb.append("\n");
        sb.append(this.frequency.toString());
        sb.append("\n");
        sb.append(this.rate.toString());
        sb.append("\n");
        sb.append("primary = "+primary);
        sb.append("\n");
        sb.append("jp = "+ jp);
        sb.append("\n");
        sb.append("secondary = "+secondary);
        sb.append("\n");
        sb.append("js = "+ js);
        sb.append("\n");
        sb.append("tertiary = "+tertiary);
        sb.append("\n");
        sb.append("jt = "+ jt);
        sb.append("\n");
        
        return sb.toString();
    }
    public UNITS getFrequencyUnits () {
        return frequencyUnits;
    }
    public static void setFrequencyUnits ( UNITS units ) {
       frequencyUnits = units;
    }
    public UNITS getCouplingConstantUnits () {
        return couplingConstantUnits;
    }
    public String getAmplitudeRatios () {
        return amplitudeRatios;
    }
    public void setAmplitudeRatios ( String amplitudeRatios ) {
        this.amplitudeRatios = amplitudeRatios;
    }
    public ParameterPrior getCoupling () {
        return primaryCoupling;
    }
    public void setCoupling ( ParameterPrior coupling ) {
        this.primaryCoupling = coupling;
    }
    public ParameterPrior getSecondaryCoupling () {
        return secondaryCoupling;
    }
    public void setSecondaryCoupling ( ParameterPrior secondaryCoupling ) {
        this.secondaryCoupling = secondaryCoupling;
    }
    public ParameterPrior getTertiaryCoupling () {
        return tertiaryCoupling;
    }
    public void setTertiaryCoupling ( ParameterPrior tertiaryCoupling ) {
        this.tertiaryCoupling = tertiaryCoupling;
    }
    
    public boolean isLoadedFromFile () {
        return loadedFromFile;
    }

    public void setLoadedFromFile ( boolean loadedFromFile ) {
        this.loadedFromFile = loadedFromFile;
    }

    public int compareTo(Object o){
        if (o instanceof Metabolite == false) {return -1;}
         Metabolite otherMet    =   (Metabolite) o;
         double diff            =  this.getFrequency().mean  - otherMet.getFrequency().mean;
         return (int)Math.signum(diff);


 }
    @Override
    public boolean equals(Object o){
        if (o == null){return false;}
        if (o instanceof Metabolite == false) {return false;}
         Metabolite otherMet    =   (Metabolite) o;
         return this.getFrequency().mean  == otherMet.getFrequency().mean;


 }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.frequency != null ? (int)this.frequency.mean: 0);
        return hash;
    }
    
        
    
  public static  void sort( List  <Metabolite> list){
        Collections.sort(list,new  FrequencyComparator() );
    }
    
  static class FrequencyComparator implements Comparator{
        public int compare(Object obj1 , Object obj2){
            Metabolite m1  =  ( Metabolite) obj1;
            Metabolite m2  =  ( Metabolite) obj2;
            
            return (int)Math.signum(m1.getFrequency().mean - m2.getFrequency().mean); 
        }
        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object obj)  {
            return super.equals(obj);
         }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
    
    }
// </editor-fold>
   
    
  
}