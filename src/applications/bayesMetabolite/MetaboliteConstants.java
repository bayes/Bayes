/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesMetabolite;

import bayes.ParameterPrior.ORDER_TYPE;

/**
 *
 * @author apple
 */
public interface MetaboliteConstants {
    
    public final static  String METABOLIC_HEADER            =   "Metabolic Parameters";
    public final static  String DERIVED_HEADER              =   "Derived Parameters";
    public final static  String COUPLING_CONSTANT_HEADER    =   "No of Coupling Constants";
    public final static  String NUMBER_OF_SITES_HEADER      =   "No of Sites";
    public final static  String METABOLITES_HEADER          =   "No of Resonances";
    
    public final static  String SITE_NAME                   =   "Site_Name";
    public final static  String ABV                         =   "Abv";
    public final static  String TYPE                        =   "Type";
    public final static  String NAME                        =   "Name";
    public final static  String PRIOR                       =   "Prior";
    public final static  String NOT_ORDERED                 =   "(Not Ordered)";
    
    public final static  String FREQUENCY                   =   "Freq:";
    public final static  String RATE                        =   "Rate:";
    public final static  String PRIMARY                     =   "Primary:";
    public final static  String JP                          =   "JP:";
    public final static  String SECONDARY                   =   "Secondary:";
    public final static  String JS                          =   "JS:";
    public final static  String TERTIARY                    =   "Tertiary:";
    public final static  String JT                          =   "JT:";
    public final static  String AMPLITUDE_RATIOS            =   "Amp Ratios:";
    
    public final static  String  NotOrderedPattern          =   "Prior=(Not Ordered)";
    public final static  ORDER_TYPE defaultOrderType        =   ORDER_TYPE.LowHigh;
}
