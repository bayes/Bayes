/*
 * ParameterPrior.java
 *
 * Created on June 8, 2007, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */
package bayes;

import java.util.List;
import java.util.Vector;
import bayes.ParameterPrior.PARAMETER_TYPE;


public class ParameterPrior implements  Cloneable, java.io.Serializable {

  
    // priors
    public static enum PRIOR_TYPE {
            UNIFORM         (   "Uniform"),
            GAUSSIAN        (   "Gaussian"),
            EXPONENTIAL     (   "Exponential"),
            POSITIVE        (   "Positive"),
            FIXED_PARAMETER (   "Parameter");

        private final String name;
        PRIOR_TYPE(String aname) {
            this.name       = aname;
        }
        public  String getName()          { return name;}
        public static Vector<String> getTypes () {
            Vector<String> types = new  Vector<String>();
            for (PRIOR_TYPE t : PRIOR_TYPE.values()) {
                types.add(t.getName());
         }
        return types;
    }
        static public PRIOR_TYPE getTypeByName(String aShortName)
            throws IllegalArgumentException
         {
            for (PRIOR_TYPE type: PRIOR_TYPE.values()) {
                if ( type.getName() .equalsIgnoreCase(aShortName)){
                    return type;
                }
            }
            throw new IllegalArgumentException(aShortName);
         }
     }
    public static enum PARAMETER_TYPE {
         NonLinear  ("NonLinear"),
         Frequency  ("Frequency"),
         Parameter  ("Parameter"),
         Amplitude  ("Amplitude")  ;


        private String name;
        PARAMETER_TYPE (String aname){this.name= aname;}
        public static Vector<String> getTypes (){
            Vector<String> types = new Vector<String>();
            for (PARAMETER_TYPE t : PARAMETER_TYPE.values()) {
                types.add(t.toString());
         }
         return types;
        }
        static public PARAMETER_TYPE getTypeByName(String name)
            throws IllegalArgumentException
         {
            for (PARAMETER_TYPE type: PARAMETER_TYPE.values()) {
                if ( name.equalsIgnoreCase(type.toString())){
                    return type;
                }
            }
            throw new IllegalArgumentException(name);
         }

        public String getName() {
            return name;
        }
        public static List< PARAMETER_TYPE> getEnterAsciiTypes(){
            List< PARAMETER_TYPE>  list  = new Vector<PARAMETER_TYPE>( );
            for (PARAMETER_TYPE type :  PARAMETER_TYPE.values()) {
                if (type ==  Frequency ){continue;}
                else{ list.add( type  );}
            }

            return list;
        }
     }
    public static enum ORDER_TYPE {NotOrdered, LowHigh, HighLow        ;
         static public ORDER_TYPE getTypeByName(String name)
            throws IllegalArgumentException
         {
            for (ORDER_TYPE type: ORDER_TYPE.values()) {
                if ( name.equalsIgnoreCase(type.toString())){
                    return type;
                }
            }
            throw new IllegalArgumentException( name);
         }
     }

   
    public double           low                =    0.0; 
    public double           mean               =    0.0;
    public double           high               =    0.0;
    public double           sdev               =    1.0;
    public double           norm               =    -2.0;
    public String           name               =    "default";
    public PRIOR_TYPE      priorType          =    PRIOR_TYPE.UNIFORM;
    private PARAMETER_TYPE   paramType          =    PARAMETER_TYPE.NonLinear;
    public  ORDER_TYPE      order              =    ORDER_TYPE.NotOrdered;
    public boolean          isPriorTypeEditable =   true;
    public boolean          isOrderEditable     =   true;
    public boolean          isPriorEditable     =   true;
    public boolean          isCouplingConstant  =   false;
    public String           message             =   null;
    private static final long serialVersionUID  = 7526472295622776147L;
    public final static  double  AMPLITUDE_LOW  =   -1E+6;
    public final static  double  AMPLITUDE_HIGH =   1E+6;
    public final static  double  AMPLITUDE_MEAN =   0;
    public final static  double  AMPLITUDE_SDEV =   300000;
    public final static  String  AMPLITUDE_NAME =   "Ampl";

  

    public ParameterPrior() {}
    public ParameterPrior(String name) {this.name = name;}
    public ParameterPrior(String name, PRIOR_TYPE apriorType) {
        this.name       = name;
        this.priorType  = apriorType;
    }

    public PARAMETER_TYPE getParameterType() {
        return paramType;
    }
    public void setParameterType(PARAMETER_TYPE paramType) {
        this.paramType = paramType;
        if (paramType == PARAMETER_TYPE.Amplitude ){
            low                 =   ParameterPrior.AMPLITUDE_LOW;
            high                =   ParameterPrior.AMPLITUDE_HIGH;
            mean                =   ParameterPrior.AMPLITUDE_MEAN;
            sdev                =   ParameterPrior.AMPLITUDE_SDEV;
            isOrderEditable     =   false;
            isPriorTypeEditable =   false;
            isPriorEditable     =   false;
            priorType           =   PRIOR_TYPE.GAUSSIAN;
            order               =   ORDER_TYPE.NotOrdered;
        }
    }

    
   @Override
   public ParameterPrior clone(){
        try {
           return (ParameterPrior) super.clone();
        } catch (CloneNotSupportedException ex) {
            System.out.println("clone in Parameter Prior has thrown error");
            ParameterPrior newPrior =  new ParameterPrior(this.name);
            newPrior.low            =  this.low;
            newPrior.high           =  this.high;
            newPrior.mean           =  this.mean;
            newPrior.sdev           =  this.sdev;
            newPrior.priorType      =  this.priorType;
            newPrior.norm           =  this.norm;
            newPrior.setParameterType(this.getParameterType());
            newPrior.order          =  this.order;
            
            newPrior.isPriorTypeEditable        =   this.isPriorTypeEditable;
            newPrior.isOrderEditable            =   this.isOrderEditable;
            newPrior.isPriorEditable            =   this.isPriorEditable;
         
            return newPrior;
        }
    }
   
   @Override
   public String toString(){
       String str;
       str  = "Parameter name       = "+    this.name       +"\n" +
               "         low        = "+    this.low        +"\n"+
               "         mean       = "+    this.mean       +"\n"+
               "         high       = "+    this.high       +"\n"+
               "         sdev       = "+    this.sdev       +"\n"+
               "         norm       = "+    this.norm       +"\n"+    
               "         priorType  = "+    this.priorType  +"\n"+
               "         paramType  = "+    this.getParameterType()  +"\n"+
               "         order      = "+    this.order      +"\n";
    return str;
   }
}
