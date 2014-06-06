/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;
import java.util.Vector;
/**
 *
 * @author apple
 */
public class Enums {
     public final static String sp = "   ";
     private Enums(){}
     
     public static enum RESONANCE_MODEL {  
        CORRELATED    (   "Correlated"  , "(CP)", "CP"),
        UNCORRELATED   (  "Uncorrelated", "(UP)", "UP");
     
        private final String name;
        private final String shortNameWithParenthesis;
        private final String shortName;
        RESONANCE_MODEL(String aname, String aShortNameWithParenthesis, String aShortName ) {
            this.name                       =   aname;
            this.shortNameWithParenthesis   =   aShortNameWithParenthesis;
            this.shortName                  =   aShortName;
        }
        public  String getName()          { return name;}
        public  String getShortNameWithParenthesis()     { return shortNameWithParenthesis;}
        public  String getShortName()                    { return shortName ;}
        
        static public RESONANCE_MODEL getResonanceModelFromShortNameWithParenthesis(String aShortNameWithParenthesis)
            throws IllegalArgumentException{
            for (RESONANCE_MODEL resonanceModel: RESONANCE_MODEL.values()) {
                if (resonanceModel.getShortNameWithParenthesis() .equalsIgnoreCase(aShortNameWithParenthesis)){
                    return resonanceModel;
                }
            }
            throw new IllegalArgumentException();
         }
       
        static public RESONANCE_MODEL getResonanceModelFromShortName(String aShortName)
            throws IllegalArgumentException{
            for (RESONANCE_MODEL resonanceModel: RESONANCE_MODEL.values()) {
                if (resonanceModel.getShortName() .equalsIgnoreCase(aShortName)){
                    return resonanceModel;
                }
            }
            throw new IllegalArgumentException();
         }
    }
     public static enum SPIN_DEGENERACY {  
        SINGLET    (   "Singlet"    , 1   ),
        DOUBLET    (   "Doublet"    , 2   ),
        TRIPLET    (   "Triplet"    , 3   ),
        QUARTET    (   "Quartet"    , 4   ),
        QUINTET    (   "Quintet"    , 5   ),
        SEXTET     (   "Sextet"     , 6   ),
        SEPTET     (   "Septet"     , 7   ),
        OCTET      (   "Octet"      , 8   ),
        NONATET    (   "Nonatet"    , 9   ),
        DECATET    (   "Decatet"    , 10  ),
        UNDECATET  (   "Undecatet"  , 11  );
        //DODECATET  (   "Dodecatet"  , 12  );
     
        private final String name;
        private final int  resNumber;
        SPIN_DEGENERACY(String aname, int aResNumber) {
            this.name       = aname;
            this.resNumber  = aResNumber;
        }
        public  String getName()                { return name;}
        public  static SPIN_DEGENERACY getSpinDegeneracy(int i){ 
            for (SPIN_DEGENERACY sd : SPIN_DEGENERACY.values()) {
                if (sd.resNumber == i){ return sd;}
            }
             // return by default
            return SPIN_DEGENERACY.SINGLET;
        }
        public  static String getName(int index)  {
            SPIN_DEGENERACY sd = SPIN_DEGENERACY.getSpinDegeneracy(index);
            return sd.getName();
        }
    }
     public static enum SHIMMING_ORDER {
        NONE    (   "None", 1),
        THREE   (   "3rd",3),
        FIVE    (   "5th",5),
        SEVEN   (   "7th",7);
     
        private final String name; 
        private int          value;
        SHIMMING_ORDER(String aname, int aValue) {
            this.name = aname;
            this.value = aValue;
        }
        public  String  getName()   { return name;}
        public  int     getValue()  { return value;}
        public static Vector<String> getNames()   { 
            Vector <String>list = new Vector <String> (); 
            
            for ( SHIMMING_ORDER v :  SHIMMING_ORDER.values()) {
                list.add(v.getName());
            }

            return list;
        }
        public static SHIMMING_ORDER getShimmingOrderbyValue(int val)  throws IllegalArgumentException  { 
            for ( SHIMMING_ORDER shim :  SHIMMING_ORDER.values()) {
                if (shim.getValue() == val) {
                    return shim;
                }
            }
             throw new IllegalArgumentException("Value "+ val + " is illegal.");
        }
        public static String getShimmingNamebyValue(int val)  throws IllegalArgumentException  {
             for ( SHIMMING_ORDER shim :  SHIMMING_ORDER.values()) {
                if (shim.getValue() == val) {
                    return shim.getName();
                }
            }
             throw new IllegalArgumentException("Value "+ val + " is illegal.");
        }
     }
     public enum SHIMMING {
        RMinus3   (   "R Minus 3",  7),
        RMinus2   (   "R Minus 2",  5),
        RMinus1   (   "R Minus 1",  3),
        Center    (   "R Center" ,  1), 
        RPlus1    (   "R Plus  1",  3),
        RPlus2    (   "R Plus  2",  5),
        RPlus3    (   "R Plus  3",  7);
     
        private final String    name;
        private final int       order;
        SHIMMING(String aname, int order) {
            this.name   = aname;
            this.order  = order;
        }
        public  String   getName()              { return name;}
        public  int      getOrder()             { return order;}
        public static Vector <String> getNames()   { 
            Vector <String> list = new Vector <String>(); 
            for ( SHIMMING v :  SHIMMING.values()) {
                list.add(v.getName());
            }

            return list;
        }
        static public SHIMMING getShimmingFromName(String aName)
                    throws IllegalArgumentException{
            for (SHIMMING shim: SHIMMING.values()) {
                if (shim.getName().equalsIgnoreCase(aName)){
                    return shim;
                }
            }
            throw new IllegalArgumentException(new String ("value "+ aName+ " is Illegal"));
         }
        static public int getIndex(SHIMMING shim)
                    throws IllegalArgumentException{
            SHIMMING [] shims = SHIMMING.values();
            int index = 0;
            for (int i = 0; i < shims.length; i++) {
               if (shim.equals( shims[i])){
                  index = i;
               }
           }
           return index;
         }

    }
     public static enum CONSTANT_MODELS {
        FirstPoint              (   "First Point Problem", true),
        RealConstant            (   "Real Constant",false),
        ImaginaryConstant       (   "Imaginary Constant", false);
     
        private final String name; 
        private boolean      isSelected;
        CONSTANT_MODELS(String aname, boolean isSelected) {
            this.name = aname;
            this.isSelected = isSelected;
        }
        public  String  getName()   { return name;}
        public  boolean isSelected()  { return isSelected;}
        public  void setSelected(boolean bool)  { isSelected = bool;}
    }
     public static enum UNITS {  
        HERTZ     (   "Hertz"  ),
        PPM       (   "PPM"  );
        
        private final String name;
        UNITS(String aname) {this.name       = aname;}
        public String getName() {return name;}
        @Override
        public String toString() {return name;}
        public static UNITS getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (UNITS unit :UNITS.values()) {
                    if(aName.equalsIgnoreCase(unit.name)){return unit;}
            }
             throw new IllegalArgumentException();
            
        }   
      public static Vector<String> getUnitsList () {
       Vector<String> units = new Vector<String>();
        for (UNITS unit : UNITS.values()) {
            units.add(unit.getName());
        }
        return units;
    }
    } 
     
     public static enum FID_CHART_MODE {   FID, SPECTRUM , POWER, OTHER};
     public static enum FID_DATA_TYPE {  
        FID                     (   "FID"  ),
        SPECTRUM_REAL           (   "SPECTRUM REAL"  ),
        SPECTRUM_IMAG           (   "SPECTRUM IMAG"  ),
        SPECTRUM_COMPLEX        (   "SPECTRUM COMPLEX"  ),
        SPECTRUM_AMPLITUDE      (   "SPECTRUM AMPLITUDE"  ),
        SPECTRUM_INTENSITY      (   "SPECTRUM POWER"  );
        
     
        private final String name;
        FID_DATA_TYPE(String aname) {this.name       = aname;}
        public String getName() {return name;}
        @Override
        public String toString(){return name;}
        public static FID_DATA_TYPE getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (FID_DATA_TYPE pt : FID_DATA_TYPE.values()) {
                    if(aName.equalsIgnoreCase(pt.name)){return pt;}
            }
             throw new IllegalArgumentException();
        }   
        
        public static Vector<String> getPlotTypes () {
            Vector<String> types = new Vector<String>();
             for (FID_DATA_TYPE pt : FID_DATA_TYPE.values()) {
                types.add(pt.getName());
            }
            return types;
    }
    
    } 
     public static enum FID_PLOT_TYPE {
         
         Trace          (   "Trace"  ), 
         Data           (   "Data"  ), 
         Model          (   "Model"  ),  
         Residual       (   "Residual"  ),   
         Vertical       (   "Vertical"  ), 
         Overlay        (   "Overlay"  ), 
         Horizontal     (   "Horizontal"  ), 
         Stacked        (   "Stacked"  );
     
        private final String name;
        FID_PLOT_TYPE (String aname) {this.name       = aname;}
        public String getName() {return name;}
        public static  FID_PLOT_TYPE getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for ( FID_PLOT_TYPE pt :  FID_PLOT_TYPE.values()) {
                    if(aName.equalsIgnoreCase(pt.name)){return pt;}
            }
             throw new IllegalArgumentException();
        }   
         public static Vector<String> getPlotTypes () {
            Vector<String> types = new Vector<String>();
             for (FID_PLOT_TYPE pt : FID_PLOT_TYPE.values()) {
               //  if (pt.getName().equals (Trace.getName())){ continue;}
                 types.add(pt.getName());
            }
            return types;
    }
     }
     
     public static enum FID_MODIFY_OPTIONS {
         PHASING        (   "Apply Phasing"  ), 
         FFT_PAD        (   "Apply FFT padding"  ), 
         SET_LB         (   "Set Lb"  ),  
         SET_REF        (   "Set reference"  ),
         SET_UNITS      (   "Set Units"  );
         
         
     
        private final String name;
        FID_MODIFY_OPTIONS (String aname) {this.name       = aname;}
        @Override
        public String toString(){return name;}
        public String getName() {return name;}
   
     }
     
    
     public static enum PHASE_MODEL {  
        COMMON            (   "CommonPhase"  ),
        INDEPENDENT       (   "IndependentPhase"  ),
        AUTOMATIC         (   "AutomaticPhaseSelection"  );
        
     
        private final String name;
        PHASE_MODEL  (String aname) {this.name       = aname;}
        public String getName() {return name;}
        public static String listAll (){
            String str = "("+   COMMON.getName()         + ","
                            +   INDEPENDENT.getName()    + ","
                            +   AUTOMATIC.getName()      +  ".)";
            return str;
            
                    
        }
        public static PHASE_MODEL   getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (PHASE_MODEL   m : PHASE_MODEL .values()) {
                    if(aName.equalsIgnoreCase(m.name)){return m;}
            }
             throw new IllegalArgumentException();
        }   
    
    } 
     public static enum METABOLITE_FILE_TYPE  {IS0, RES}
     
     
     public static enum IMAGE_TYPE {  
        SPIN_ECHO    (   "Spin Echo" ,  "SpinEcho " ),
        EPI          (   "EPI"       ,  "EPI"       ),
        MAP_EPI      (   "Map EPI"  ,  "MapEPI"     );
        
     
        private final String name;
        private final String value;
        IMAGE_TYPE  (String aname, String val) {
            this.name       = aname;
            this.value      = val;
        }
        public String getName() {return name;}
        public String getValue() {return value ;}
        @Override
        public String toString(){return name;}
        public static IMAGE_TYPE   getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (IMAGE_TYPE   im : IMAGE_TYPE .values()) {
                    if(aName.equalsIgnoreCase(im.name)){return im;}
            }
             throw new IllegalArgumentException();
        }   
     } 
     public static enum IMAGE_PROCESS {  
        All             (   "All"    ),
        COMMON          (   "Common"          );
        //ONE             (   "One"     );
        
     
        private final String name;
        IMAGE_PROCESS   (String aname) {this.name       = aname;}
        public String getName() {return name;}
        @Override
        public String toString(){return name;}
        public static IMAGE_PROCESS    getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (IMAGE_PROCESS   im : IMAGE_PROCESS  .values()) {
                    if(aName.equalsIgnoreCase(im.name)){return im;}
            }
             throw new IllegalArgumentException();
        }   
     } 
     public static enum IMAGE_DATA_TYPE {  
        REAL   (   "Real"               ),
        IMAG   (   "Imaginary"          ),
        ABS    (   "Absolute"           );
        
     
        private final String name;
        IMAGE_DATA_TYPE  (String aname) {this.name       = aname;}
        public String getName() {return name;}
        @Override
        public String toString(){return name;}
     } 
     public static enum IMAGE_FORMAT {  
        VARIAN_BINARY    (   "VARIAN binary" ),
        IMG              (   "Binary Image ( .img )"       );
        
     
        private final String name;
        IMAGE_FORMAT   (String aname) {  this.name       = aname;}
          
        
        public String getName() {return name;}
        @Override
        public String toString(){return name;}
     
     } 
     public static enum FILE_ORGANIZATION {  STANDARD  ,  COMPRESSED    } 
     public static enum IMAGE_DIMENSION{  
        WIDTH    (   "width"    , 1   ),
        HEIGHT    (   "height"    , 2   ),
        SLICE    (   "slice"    , 3  ),
        ELEMENT (   "element"  , 4  );
     
        private final String name;
        private final int  integerRepresentation;
        IMAGE_DIMENSION(String aname, int aNumber) {
            this.name       = aname;
            this.integerRepresentation  = aNumber;
        }
        public  String getName()                { return name;}
   
    }
    
       public static enum ABSCISSA{  
        UNIFORM         (   "Uniform"  ),
        NONUNIFORM      (   "NonUniform"  ),
        READ            (   "Read"  );
        
     
        private final String name;
       ABSCISSA (String aname) {this.name       = aname;}
        public String getName() {return name;}
        public String toString() {return name;}
        public static String listAll (){
            String str = "("+   UNIFORM.getName()         + ","
                            +   NONUNIFORM.getName()    + ","
                            +   READ.getName()      +  ".)";
            return str;
            
                    
        }
        public static ABSCISSA   getTypeByName(String aName)
                throws IllegalArgumentException{
            
            for (ABSCISSA   m : ABSCISSA .values()) {
                    if(aName.equalsIgnoreCase(m.name)){return m;}
            }
             throw new IllegalArgumentException();
        }   
    
    }    
  
     
     
     
     
     
     public static enum PACKAGE_INTSRUCTIONS {
         
            EXPONENTIAL         ( sp +  "To use the Exponential package: \n\n"
                                + sp +   "1.  Load an accii file.\n\n"
                                + sp +   "2.  Specify the number of exponentials or specify \"unknown\" to enable\n"
                                + sp +   "    automatic model determination.\n\n"
                                + sp +   "3.  When the number of exponentials is given, specify whether or not\n"
                                + sp +   "    a constant is present.\n\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server." ),
                                   
            POLYNOMIAL          ( sp +  "To use the Polynomial package: \n\n"
                                + sp +   "1.  Load an accii file.\n\n"
                                + sp +   "2.  Specify the polynomial order, or specify \"unknown\" to enable \n" 
                                + sp +   "    automatic model determination.\n\n"
                                + sp +   "3.  Select the server to run the analysis.\n\n"
                                + sp +   "4.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "5.  Use \"Get Job\" to get the results from the server."  ),
                                                
            
                                
            DIFFUSION_TESNOR   (  sp +  "To use the Diffusion tensor package: \n\n"
                                + sp +   "1.  Select the type of data to be loaded: g-vector, b-vector\n"
                                + sp +   "    or b-matrix data.  \n\n"
                                + sp +   "2.  Load a 5 column ascii data set for g-vector or b-vector data.\n"
                                + sp +   "    For b-matrix data load a 8 column data set. \n\n"
                                + sp +   "3.  Select the number of tensors.\n\n"
                                + sp +   "4.  Specify whether a constant is present.\n\n"
                                + sp +   "5.  Select the server to process the job.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."   ),


           ENTER_ASCII         (  sp + "To use the Enter Ascii package: \n\n"
                                + sp +   "1.  Load the Fortran or C model function from the System\n"
                                + sp +   "    or User Models directory. \n\n"  
                                + sp +   "2.  For nonsystem models, build the model using the \"Build\" button.\n\n"
                                + sp +   "3.  Load an ascii file having the number of abscissa specified by\n"
                                + sp +   "    the model parameter file.\n\n"
                                + sp +   "4.  Review the prior range information, and make appropriate changes.\n\n"
                                + sp +   "5.  Select the server to run the analysis.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),                    
         
         TEST_MODEL             (  sp + "To use the Test Model Package package: \n\n"
                                + sp +   "1.  Load the Fortran or C model function from the System\n"
                                + sp +   "    or User Models directory. \n\n"  
                                + sp +   "2.  For nonsystem models, build the model using the \"Build\" button.\n\n"
                                + sp +   "3.  Load an ascii file having the number of abscissa specified by\n"
                                + sp +   "    the model parameter file. Note this file is used mostly to supply an Abscissa.\n\n"
                                + sp +   "4.  Review the prior range information, and make appropriate changes.\n\n"
                                + sp +   "5.  Select the server to run the analysis.\n\n"
                                + sp +   "6.  Run the analysis. When the analysis is run the priors are used\n"
                                + sp +   "    to evaluate the model at the low, peak and high prior ranges\n"
                                + sp +   "    and the priors are sampled randomly 10 times, and the model evaluated.\n"
                                + sp +   "    Each evaluation of the model  is tested to ensure that it generates\n" 
                                + sp +   "    valid results.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),


         MODEL_ENTER_ASCII    ( sp +   "To use the Ascii Model Selection Package: \n\n"
                                + sp +   "1.  Using the Enter Ascii package, load your data and set all of\n"
                                + sp +   "    the prior ranges for all of the models to be tested\n"
                                + sp +   "    before attempting to use this model selection package.\n"
                                + sp +   "\n"
                                + sp +   "2.  Load up to 10 Fortran or C model functions from your\n"
                                + sp +   "    User model directory.  Note these models must use the same\n"
                                + sp +   "    number of abscissa and data columns.\n"
                                + sp +   "\n"
                                + sp +   "3.  Load an ascii file having the number of data columns and\n"
                                + sp +   "    abscissa columns specified by the models.\n"
                                + sp +   "\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server."  ),
                                
                                
          MTz                   ( sp +  "To use the Z magnetization transfer package: \n\n"
                                + sp +   "1.  Load an ascii file having one abscissa and two data columns.\n\n"
                                + sp +   "2.  Review the range information and make appropriate changes.\n\n"
                                + sp +   "3.  Select the server to run the analysis.\n\n"
                                + sp +   "4.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "5.  Use \"Get Job\" to get the results from the server."  ),                    
        
                                
         MTz_KINETICS           ( sp +  "To use the Magnetization transfer kinetics package: \n\n"
                                + sp +   "1.  Load up to 20 three column ascii data sets having\n"
                                + sp +   "    one abscissa and two data columns.\n"
                                +"\n"
                                + sp +   "2.  Enter the temperature at which each data set was taken.\n\n"
                                + sp +   "3.  Enter the uncertainity in the temperature measurements,\n"
                                + sp +   "    this is assumed to be the same for all temperatures.\n\n"
                                + sp +   "4.  Load a viscosity table if something different than\n"
                                + sp +   "    water is used.\n\n"
                                + sp +   "5.  Select the server to run the analysis.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),      

       MTzBIG                 (sp +  "To use the Magnetization transfer kinetics package: \n\n"
                                + sp +   "1.  Load a two column ascii data set.\n"
                                +"\n"
                                + sp +   "2.  Review the prior range information and make appropriate changes.\n\n"
                                + sp +   "3.  Select the server to run the analysis.\n\n"
                                + sp +   "4.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "5.  Use \"Get Job\" to get the results from the server."  ),
                               
       ErrInVars                ( sp +  "To use the Errors In Variables package: \n\n"
                                + sp +   "1.  Indicate which errors are known using the \"Given Errors In:\"\n"
                                + sp +   "    combo box.\n\n"
                                + sp +   "2.  Load an Ascii file having 2, 3 or four colums as indicated.\n\n"
                                + sp +   "3.  Select the polynomial order you wish to analyze.\n\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server."  ),   
        
      Behrens_Fisher            ( sp + "To use the Behrens-Fisher package: \n\n"
                                + sp +   "1.  Load two two-column ascii data sets.\n\n"
                                + sp +   "2.  Review the prior mean and standard deviation ranges and make\n" 
                                + sp +   "    corrections as necessary.\n\n"
                                + sp +   "3.  Select the server to run the analysis.\n\n"
                                + sp +   "4.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "5.  Use \"Get Job\" to get the results from the server."  ),                           
      
     BAYES_METABOLITE           ( sp +  "To use the Bayes Metabolite package: \n\n"
                                + sp +   "1.  Load the FID to be processed.\n\n"
                                + sp +   "2.  Load a previously defined metabolite model.\n\n"
                                + sp +   "3.  Set the referencing to that appropriate to this model.\n\n"
                                + sp +   "4.  Review the prior frequencies and decay rates constants.\n\n" 
                                + sp +   "5.  Select the server to run the analysis.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),
     
    FIND_RESONANCES             ( sp +  "To use the Find Resonances package: \n\n"
                                + sp +   "1.  Load an FID into the current experiment.\n\n"
                                + sp +   "2.  Set the \"Phase Model\" selection:\n"
                                + sp +   "    Common      Each resonance has the same zero and first order phases.\n"
                                + sp +   "    Independent Each resonance has a unique phase.\n"
                                + sp +   "    Automatic   Model selection is used to determine the phase model.\n\n"
                                + sp +   "3.  Specify the maximium number of resonances.\n\n" 
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server."  ),
                                
     BAYES_ANALYZE              ( sp +  "To use the Bayes Analyze package: \n\n"
                                + sp +   "1.  Load an FID into the current experiment.\n\n"
                                + sp +   "2.  Select the phase of the default resonance model.\n\n"
                                + sp +   "3.  Select the maximum number of resonances in the model.\n\n"
                                + sp +   "4.  If desired, mark the resonances yourself.\n\n"
                                + sp +   "5.  Specify any constant models needed in the analysis.\n\n"
                                + sp +   "6.  Select the server to run the analysis.\n\n"
                                + sp +   "7.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "8.  Use \"Get Job\" to get the results from the server."  ),
                                


     BAYES_PHASE             ( sp +  "To use the Bayes Phase package: \n\n"
                                + sp +   "1.  Load the image you wish to phase.\n\n"
                                + sp +   "2.  Select the processing to All or Common.\n\n"
                                + sp +   "3.  Set the noise standard deviation.\n"
                                + sp +    "    a. Draw an ROI in the noise\n"
                                + sp +    "    b. Generate the statistics for that roi\n"
                                + sp +    "    c. Copy the standard deviation into the \"Noise SD\" entry box\n\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server."  ),
     
                                
     BAYES_PHASE_NONLINEAR      ( sp +    "To use the Nonlinear Phasing package: \n\n"
                                + sp +    "1.  Phase the image using Bayes Phase.\n"
                                + sp +    "    This must be done before you even start this package.\n\n"
                                + sp +    "2.  Select the processing to All or Common.\n\n"
                                + sp +    "3.  Set the noise standard deviation.\n"
                                + sp +    "    a. Draw an ROI in the noise\n"
                                + sp +    "    b. Generate the statistics for that roi\n"
                                + sp +    "    c. Copy the standard deviation into the \"Noise SD\" entry box\n\n"
                                + sp +    "4.  Select the server to run the analysis.\n\n"
                                + sp +    "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +    "6.  Use \"Get Job\" to get the results from the server."  ),

      IMAGE_MODEL_SELECTION    ( sp +    "To use the Image Model Selection Package: \n\n"
                                + sp +   "1.  Load and or generate the images that are to be analyzed.1.\n\n"
                                + sp +   "2.  Select, highlight, the ones to be analyzed.\n\n"
                                + sp +   "3.  Load one or more Fortran or C models.\n\n"
                                + sp +   "4.  Set the standard deviation of the noise.\n\n"
                                + sp +   "5.  If the number of data values is approximately equal to\n"
                                + sp +   "    the number of parameters, check the \"Use Gaussian\" check-box.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),

    BAYES_IMAGE_PIXEL          ( sp +  "To use the Analyze Image Pixels package: \n\n"
                                + sp +   "1.  If phased images are to be processed go to the \n"
                                + sp +   "    phasing packages and generate the phase images,\n"
                                + sp +   "    otherwise load the images.\n"
                                +"\n"
                                + sp +   "2.  Load the Fortran or C model function from the system or\n"
                                + sp +   "    user model directory.\n"
                                +"\n"
                                + sp +   "3.  If the model is not build, build the model using the\n"
                                + sp +   "    \"Build\" button.\n\n"
                                + sp +   "4.  Set the standard deviation of the noise.\n\n"
                                + sp +   "5.  Specify any processing options, such as use \"Max Prob\" or\n"
                                + sp +   "    \"Use Gaussian\".\n\n" 
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),

    BAYES_IMAGE_PIXEL_UNIQUE   ( sp +  "To use the Unique Abscissa Analyze Image Pixels package: \n\n"
                                + sp +   "1.  If phased images are to be processed go to the \n"
                                + sp +   "    phasing packages and generate the phase images,\n"
                                + sp +   "    otherwise load the images.\n"
                                +"\n"
                                + sp +   "2.  Load the Fortran or C model function from the system or\n"
                                + sp +   "    user model directory.\n"
                                +"\n"
                                + sp +   "3.  If the model is not build, build the model using the\n"
                                + sp +   "    \"Build\" button.\n\n"
                                + sp +   "4.  Set the standard deviation of the noise.\n\n"
                                + sp +   "5.  Specify any processing options, such as use \"Max Prob\" or\n"
                                + sp +   "    \"Use Gaussian\".\n\n" 
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server."  ),
    BAYES_WATER                ( sp +  "To use the Big Peak/Little Peak Package: \n\n"

                                + sp +   "1.  Load an FID into the current experiment.\n\n"
                                + sp +   "2.  Mark the solvent frequency using the \"Add Solvant\" button.\n\n"
                                + sp +   "3.  Mark the resonances of interest using the \"Add Metabolite\" button. \n\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server."  ),
                                
                                
   BINNED_HISTOGRAM                    ( sp +  "To use the package Binned Histogram package: \n\n"
                                + sp +   "1.  Load a 2 column ascii data set containing samples from the\n"
                                + sp +   "    density function in question.\n"
                                +"\n"

                                + sp +   "2.  Specify the number of bins in the histogram.\n"
                                +"\n"

                                + sp +   "3.  Specify whether the histogram is to be smoothed and the number \n"
                                + sp +   "    of times a 1:2:1 low pass filter is to be applied.\n"
                                +"\n"
                                + sp +   "4.  If the posterior probability for each point in the histogram\n"
                                + sp +   "    is to be output, check the Output Posteriors box.\n"
                                +"\n"
                                + sp +   "5.  Select the server to run the analysis.\n\n"
                                + sp +   "6.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "7.  Use \"Get Job\" to get the results from the server." ),
   
   DENSITY_ESTIMATION_KERNEL   ( sp +  "To use the package Density Estimation Kernel package: \n\n"
                                + sp +   "1.  Load a 2 column ascii data set containing samples from the\n"
                                + sp +   "    density function in question.\n"
                                +"\n"

                                + sp +   "2.  Specify the number the histogram kernel.\n"
                                +"\n"

                                + sp +   "3.  Specify whether the output histogram size. \n"
                                +"\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server." ),
                                
     MAX_ENTROPY_HISTOGRAM       ( sp +  "To use the Density Function Estimation package: \n\n"
                                + sp +   "1.  Load a 2 column ascii data set containing samples from the\n"
                                + sp +   "    density function in question.\n"
                                +"\n"
                                + sp +   "2.  Specify the order of the moment expansion used to represent\n"
                                + sp +   "    the density funciton, or leave this combo box set as automatic\n"
                                + sp +   "    if the package is to determine the optimal expansion order.\n"
                                +"\n"
                                + sp +   "3.  Specify the size of the discrete density function using \"Size\" combo box.\n\n"
                                + sp +   "4.  Select the server to run the analysis.\n\n"
                                + sp +   "5.  Run the analysis using the \"Run\" button.\n\n"
                                + sp +   "6.  Use \"Get Job\" to get the results from the server." ),
     
     
     
      BAYES_TEST_DATA          ( sp  +  "To use the Bayes Test Data package: \n\n"
                                + sp +   "1.  Load the Fortran or C model that describes the model equation\n"
                                + sp +   "    from either the user or system libraries.\n\n"
                                + sp +   "2.  If the model is not built, build the model using \n"
                                + sp +   "    the\"Build\" button.\n\n"
                                + sp +   "3.  Use the \"Prior Viewer\" to set the prior ranges on allof the parameters.\n\n"
                                + sp +   "4.  Run the analysis on the selected server.\n\n"
                                + sp +   "5.  Use \"Get Job\" button to fetch the results from the server.\n\n"
                                + sp +   "6.  The generated test data may be view using the \"Image Viewer\".\n"
                                + sp +   "    The test data is named using the name of your loaded model."  ),
                                ;  
            
            
                                  





            private final String instruction;
      
       PACKAGE_INTSRUCTIONS(String aname) {
            this.instruction       = aname;
        }
        public  String getInstruction()     { return instruction;};
     }
}
