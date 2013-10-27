/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;
import java.util.List;
import bayes.ParameterPrior.ORDER_TYPE;
/**
 *
 * @author larry
 */
public class IsPriorReadyToRun {
      
    public static String isPriorReadyToRun( List <ParameterPrior>priors  ) {
       if (  priors == null) { return null;}

       String error                 =  testPriorOrders ( priors);
       if (error != null){return error;}
        
       ParameterPrior errorPrior      = testBounds (priors);
     
       if ( errorPrior != null){
            error                    = errorPrior.message;
            errorPrior.message       = null;
           return error;
       }

        return null;
    }



    public static String testGaussianPrior(ParameterPrior p){
        String errorMessage = null;

        if(p.mean < p.low){
             errorMessage = p.name + ": MEAN less than LOW";
        }
        else if (p.mean > p.high){
             errorMessage  = p.name + ": MEAN greater than HIGH";
        }

        else if(p.high < p.low){
             errorMessage  = p.name + ": HIGH less than LOW";
        }
        else if(p.sdev <= 0.0){
           errorMessage = p.name + ": SDEV less than or equal to zero";
       }

        return   errorMessage;
    }
    public static String testPositivePrior(ParameterPrior p){
        String errorMessage = null;
        if(p.mean < p.low){
             errorMessage = p.name + ": PEAK less than LOW";
        }
        else if (p.mean > p.high){
             errorMessage  = p.name + ": PEAK greater than HIGH";
        }
           
        else if(p.high < p.low){
             errorMessage  = p.name + ": HIGH less than LOW";
        }

        return   errorMessage;
    }
    public static String testEXPONENTIALPrior(ParameterPrior p){
        String errorMessage = null;
        
        if(p.low < 0.0){
            errorMessage =  p.name + ": MEAN is less than zero";
        }
        else if(p.mean <= 0.0){
            errorMessage =  p.name + ": RATE is less or equal to zero";
        }
        else if(p.mean < p.low){
             errorMessage =  p.name + ": RATE is less than LOW";
        }
        else if (p.mean > p.high){
             errorMessage  = p.name + ": RATE is greater than HIGH";
        }

        else if(p.high < p.low){
             errorMessage  = p.name + ": HIGH less than LOW";
        }

        return   errorMessage;
    }
    public static String testUnifromPrior(ParameterPrior p){
        String errorMessage = null;

       if(p.high < p.low){
             errorMessage  = p.name + ": HIGH less than LOW";
       }
       else {
             p.mean         = (p.high + p.low)/2.0;
      }

        return   errorMessage;
    }
    public static String testFixedParameterPrior(ParameterPrior p){
        String errorMessage = null;
        p.high              = p.mean;
        p.low               = p.mean;
        return   errorMessage;
    }

    public static String testPriorOrders( List <ParameterPrior> priors){
       if ( priors == null)             { return   null ;}
       

       ParameterPrior lowHighPr             =   null;
       ParameterPrior higLowhPr             =   null;
       String error                         =   String.format(
                                                "Either %s or %s ordering, but not both,\n" +
                                                "can be used to assign priors. Exit run...."
                                                ,
                                                ORDER_TYPE.HighLow.toString(),
                                                ORDER_TYPE.LowHigh.toString());

       for (ParameterPrior p :  priors) {
           if (p.order ==  ORDER_TYPE.HighLow){
               if (lowHighPr!= null){return error;}
               else if ( higLowhPr == null){higLowhPr = p;}

           }
           else if (p.order  == ParameterPrior.ORDER_TYPE.LowHigh){
                if (higLowhPr!= null){return error;}
                else if ( lowHighPr == null){lowHighPr = p;}
           }

        }
        return null;
    }
    public static ParameterPrior testBounds(List <ParameterPrior> priors){
       if (  priors == null) { return null;}
       String error                         =   null;

       for (ParameterPrior p :   priors) {
            p.message                      =    null;
           switch(p.priorType){

                case GAUSSIAN:          error = testGaussianPrior(p);break;

                case UNIFORM:           error = testUnifromPrior(p);break;

                case EXPONENTIAL:       error = testEXPONENTIALPrior(p);break;

                case POSITIVE:          error = testPositivePrior(p); break;

                case  FIXED_PARAMETER:   testFixedParameterPrior(p);break;

            }
            if (error != null){
                 error           =   String.format(
                                    "Error for prior %s.\n" +
                                    "%s",
                                     p.name,
                                     error);
                p.message       =   error;
                return p;
            }


        }
        return null;
    }

  
  

  
}
