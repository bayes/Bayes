/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;
import java.util.ArrayList;
import java.util.Vector;
import java.io.File;
import bayes.ParameterPrior;


/**
 *
 * @author apple
 */
public class MetabolitesAndResonances implements java.io.Serializable {
    private  MetaboliteInfo metabolitesInfo = new MetaboliteInfo();
    private MetaboliteInfo resonancesInfo = new MetaboliteInfo();
    private File isoFile;
    private File resFile;
    public  final static String DEFAULT_RES_FILE_NAME = "marked.RES";
    
    private static MetabolitesAndResonances metabolitesAndResonances = new MetabolitesAndResonances();
    
    public ArrayList<Metabolite> getMetabolites(){
        return getMetabolitesInfo().getMetabolites();
    }
    public ArrayList<Metabolite> getResonances(){
        return getResonancesInfo().getMetabolites();
    }
    
    public void setMetaboliteInfo(MetaboliteInfo info){
        setMetabolitesInfo(info);
    }
    public void setResonanceInfo(MetaboliteInfo info){
        setResonancesInfo(info);
    }
    public void reset(){
        getMetabolitesInfo().reset();
        getResonancesInfo().reset();
        setIsoFile(null);
        setResFile(null);
    }
    
    public Vector <ParameterPrior> getAllPriors(){
       Vector <ParameterPrior>  priors = new  Vector <ParameterPrior>();
       priors.addAll( getMetabolitesInfo().getAllPriors());
       priors.addAll( getResonancesInfo().getAllPriors());
       return priors;
   }
    public ArrayList<Metabolite>   getAllMetabolitesAndResonances(){
         ArrayList<Metabolite> all  = new  ArrayList<Metabolite> ();
         all.addAll(getMetabolites());
         all.addAll(getResonances());
         return all;
    }
    
   
    public static MetabolitesAndResonances getMetabolitesAndResonances () {
        return metabolitesAndResonances;
    }
    public static void setMetabolitesAndResonances ( MetabolitesAndResonances aMetabolitesAndResonances ) {
        metabolitesAndResonances = aMetabolitesAndResonances;
    }
    public boolean isResonances(){
       // if (getMetabolitesInfo().getAllPriors().size() > 0 ) {return true;}
        if (getResonancesInfo().getAllPriors().size()>0 ) {return true;}
        return false;
    }

    public MetaboliteInfo getMetabolitesInfo () {
        return metabolitesInfo;
    }
    public void setMetabolitesInfo ( MetaboliteInfo metabolitesInfo ) {
        this.metabolitesInfo = metabolitesInfo;
    }

    public MetaboliteInfo getResonancesInfo () {
        return resonancesInfo;
    }
    public void setResonancesInfo ( MetaboliteInfo resonancesInfo ) {
        this.resonancesInfo = resonancesInfo;
    }

    public File getIsoFile () {
        return isoFile;
    }
    public void setIsoFile ( File isoFile ) {
        this.isoFile = isoFile;
    }

    public File getResFile () {
        return resFile;
    }
    public void setResFile ( File resFile ) {
        this.resFile = resFile;
    }
}
