/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import applications.bayesEnterAsciiModel.*;
import java.util.*;
/**
 *
 * @author apple
 */
public class AsciiModelList extends   ArrayList<EnterAsciiModel    >
                         implements  BayesEneterAsciiConstants,
                                     java.io.Serializable
{    String errorMesage; 
     public final static String LIMIT_EXCEEDED_MESSAGE  =   "Number of models exceeded maximum of "
                                                             + TOTAL_NUMBER_OF_MODELS +".\n"
                                                             +"Abort load.";
     public final static String DUPLICATE_MODEL_MESSAGE =    "Model is already loaded."+"\n"; 
    
     public int              getNumberOfAbscissa(){
         if (isEmpty( ) == false ) {
             return get(0).getNumberOfAbscissa();
         }
         else{
            return 0;
         }
    }
     public int              getNumberOfColumns(){
       if (isEmpty( ) == false ) {
             return get(0).getNumberOfDataCols();
         }
         else{
            return 1;
         }
     }
     public int              getNumberOfDerived(){
         if (isEmpty( ) == false ) {
              return get(0).getNumberOfDerived();
         }
         else{
            return 0;
         }
     }
     public int              getNumberOfVectors(){
         if (isEmpty( ) == false ) {
              return get(0).getNumberOfModelVectors();
         }
         else{
            return 0;
         }
     }
     public int              getNumberOfModels(){
         return size();
     }
   
   
     public boolean          addModel(EnterAsciiModel model){
         errorMesage        = null;

         EnterAsciiModel olderInstance =         findDuplicateModel(model);
         if (olderInstance != null){this.remove(olderInstance);}


         boolean  canAdd    = isValid(model);
         if ( canAdd == true) { add(model);}
        
         return canAdd;
     } 
   
    
     public EnterAsciiModel[]  getAsciiModels(){
           return  this.toArray(new EnterAsciiModel[this.size()]);
     }

     public boolean isValid(EnterAsciiModel model){
       boolean canAdd  = true;
        
       if (isLimitExceeded() == true)                   {return false;}
  //     if (isDuplicateModel(model) == true)             {return false;}
       if (isCompatibleModel(model) == false)  {return false;}
        
       return canAdd;
     }


     public EnterAsciiModel          findDuplicateModel( EnterAsciiModel model){
         if (isEmpty( ) == true ) { return null;}
         for (EnterAsciiModel mod : this) {
              if (mod.getName().equals(model.getName())){
                 return mod;
               }
          }
         return null;
     }
     
     public boolean          isCompatibleModel( EnterAsciiModel m2){
           if (isEmpty( ) == true ) { return true;}
           EnterAsciiModel m1       =    get(0);

           String errMsg            =   String.format(
                                            "Model %s is not compatible \n" +
                                            "with previously loaded models\n" +
                                            "(e.g. %s)",
                                            m2.getName(),m1.getName() );



           boolean compatible       =   m2.isCompatible(m1);
         
           if ( compatible == false) { errorMesage = errMsg;}

           return compatible;
     }
     public boolean          isDuplicateModel( EnterAsciiModel model){
         if (isEmpty( ) == true ) { return false;}
         for (EnterAsciiModel mod : this) {
              if (mod.getName().equals(model.getName())){
                 errorMesage     =  model.getName()+ ": " +DUPLICATE_MODEL_MESSAGE;
                 return true;
               }
          }
         return false;
     }
     public boolean          isLimitExceeded( ){
         if (  getNumberOfModels() >= TOTAL_NUMBER_OF_MODELS){
                errorMesage       =    LIMIT_EXCEEDED_MESSAGE;
               return true;
          }
         return false;
     }
 
     public String           getErrorMesage () {
        return errorMesage;
    }
}
