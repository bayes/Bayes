/*
 * Model.java
 *
 * Created on September 17, 2007, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package applications.model;

import bayes.ParameterPrior;
import java.io.*;
import java.util.List;


public interface Model {
    
   public static String            spr              = "/";
   public boolean                  isReadyToRun();
   public List <ParameterPrior>    getPriors();
   public void                     setPackageParameters(ObjectInputStream serializationFile)  throws Exception;
   public void                     savePackageParameters(ObjectOutputStream savePackageParametersserializationFile);
   public void                     reset();
   public int                      getNumberOfAbscissa();
   public int                      getNumberOfDataColumns();
   public int                      getNumberOfPriors();
   public int                      getTotalNumberOfColumns();
   public StringBuilder            getModelSpecificsForParamsFile(int padlen, String padchar);
   public String                   getProgramName();
   public String                   getExtendedProgramName();
   public String                   getConstructorArg();
   public String                   getInstructions();
   public boolean                  isOutliers();
   public void                     setActive(boolean enabled);
   public void                     setDefaults();
   public void                     clearPreviousRun();
   public void                     destroy();
}
