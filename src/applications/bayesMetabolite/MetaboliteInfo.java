/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;
import bayes.ParameterPrior;
import java.util.ArrayList;
import java.util.Vector;
import fid.Units;
import java.util.Collection;
import bayes.Enums.UNITS;
/**
 *
 * @author apple
 */
public class MetaboliteInfo implements java.io.Serializable{

    protected boolean                       loaded                  =    false;
    private String                          name                    =   "";
    protected ArrayList<ParameterPrior>     parameterPriors         =    new ArrayList<ParameterPrior>();
    protected ArrayList<String>             derivedParameters       =    new ArrayList<String>();
    protected ArrayList<ParameterPrior>     couplingConstantsPriors =    new ArrayList<ParameterPrior>();
    protected ArrayList<String>             siteNames               =    new ArrayList<String>();;
    protected ArrayList<Metabolite>         metabolites             =    new ArrayList<Metabolite>();



   public void removeMetabolite(Metabolite m){
       Collection <Metabolite> metabs = new ArrayList <Metabolite>();
       if (m != null) {metabs.add(m);}
       removeMetabolites(metabs);

    }
   public void removeMetabolites(Collection <Metabolite> metabs){
       for (Metabolite metabolite : metabs) {
            if (metabolites.contains(metabolite)  == false) {continue;}
            metabolites.remove(metabolite);
       }
        updatePriorsFromMetabolites();


    }
   public void updatePriorsFromMetabolites(){
      //  ArrayList<ParameterPrior> parameterPrs  = new ArrayList<ParameterPrior>();
      //  ArrayList<ParameterPrior> couplingPrs   = new ArrayList<ParameterPrior>();
        ArrayList<String>siteNms                = new ArrayList<String>();
        for (Metabolite m : this.getMetabolites()) {
        //   parameterPrs.add(m.getFrequency());
          // parameterPrs.add(m.getRate());
         //  couplingPrs.add(m.getCoupling());
           
           String sitename = m.getSiteName();
           if (siteNms.contains (sitename) == false){
                siteNms.add(m.getSiteName());
           }
          
       }
     //  this.setParameterPriors(parameterPrs);
      // this.setCouplingConstantsPriors( couplingPrs);
       this.setSiteNames(siteNms);
   }

    
    public static  ArrayList<Metabolite> convertUnits(ArrayList<Metabolite> metabolites, float reffreq, UNITS oldUnits, UNITS newUnits){
       ParameterPrior prior;
        for (Metabolite metabolite : metabolites) {
            prior       = metabolite.frequency;
            prior.high  = Units.convertUnits(prior.high, reffreq, oldUnits, newUnits);
            prior.mean  = Units.convertUnits(prior.mean, reffreq, oldUnits, newUnits);
            prior.low   = Units.convertUnits(prior.low,  reffreq, oldUnits, newUnits);
            prior.sdev  = Units.convertUnits(prior.sdev, reffreq, oldUnits, newUnits);
        }
        return metabolites;
    }
    
    public void reset(){
        loaded              = false;
        parameterPriors.clear();
        couplingConstantsPriors.clear();
        metabolites.clear();
        derivedParameters.clear();
        siteNames.clear();
        
    }
    public ArrayList<Metabolite> getNonLoadedMetabolites(){
        ArrayList<Metabolite> metabs  = new  ArrayList<Metabolite>();
        for (Metabolite metabolite :  metabolites ) {
            if(metabolite.isLoadedFromFile() == false){
                    metabs .add(metabolite);
            }
        }
        return metabs;
    }
    
    public Vector <ParameterPrior> getAllPriors(){
       Vector <ParameterPrior>  priors = new  Vector <ParameterPrior>();
       priors.addAll( parameterPriors);
       priors.addAll( couplingConstantsPriors);
       for (Metabolite m :  metabolites) {
           priors.add(m.getFrequency());
           priors.add(m.getRate());
           
       }
       return priors;
   }
    
    public  ArrayList<Metabolite> getMetabolites () {
        return metabolites;
    }
    public void setMetabolites ( ArrayList<Metabolite> theMetabolites ) {
        metabolites = theMetabolites;
    }

    public Metabolite getMetabolites ( int index ) {
        return this.metabolites.get(index);
    }
    public void setMetabolites ( int index, Metabolite newMetabolites ) {
        this.metabolites.set(index, newMetabolites);
    }

    public  ArrayList<String>  getSiteNames () {
        return siteNames;
    }
    public void setSiteNames ( ArrayList<String>  siteNames ) {
        this.siteNames = siteNames;
    }

    public String getSiteNames ( int index ) {
        return this.siteNames.get(index);
    }
    public void setSiteNames ( int index, String newSiteNames ) {
        this.siteNames.set(index, newSiteNames);
    }

    public ArrayList <ParameterPrior> getCouplingConstantsPriors () {
        return couplingConstantsPriors;
    }
    public void setCouplingConstantsPriors ( ArrayList <ParameterPrior> couplingConstantsPriors ) {
        this.couplingConstantsPriors = couplingConstantsPriors;
    }

    public ParameterPrior getCouplingConstantsPriors ( int index ) {
        return this.couplingConstantsPriors.get(index);
    }
    public void setCouplingConstantsPriors ( int index,
                                             ParameterPrior newCouplingConstantsPriors ) {
        this.couplingConstantsPriors.set(index, newCouplingConstantsPriors);
    }

    public ArrayList<String>   getDerivedParameters () {
        return derivedParameters;
    }
    public void setDerivedParameters (ArrayList<String>    aderivedParameters) {
        this.derivedParameters = aderivedParameters;
    }

    public String getDerivedParameters ( int index ) {
        return this.derivedParameters.get(index);
    }
    public void setDerivedParameters ( int index, String newDerivedParameters ) {
        this.derivedParameters.set(index, newDerivedParameters);
    }


    public ArrayList <ParameterPrior> getParameterPriors () {
        return parameterPriors;
    }
    public void setParameterPriors ( ArrayList <ParameterPrior> parameterPriors ) {
        this.parameterPriors = parameterPriors;
    }

    public ParameterPrior getParameterPriors ( int index ) {
        return this.parameterPriors.get(index);
    }
    public void setParameterPriors ( int index, ParameterPrior newParameterPriors ) {
        this.parameterPriors.set(index,newParameterPriors);
    }

    public boolean isLoaded () {
        return loaded;
    }
    public void setLoaded ( boolean loaded ) {
        this.loaded = loaded;
    }

    public String getName () {
        return name;
    }
    public void setName ( String name ) {
        this.name = name;
    }

}
