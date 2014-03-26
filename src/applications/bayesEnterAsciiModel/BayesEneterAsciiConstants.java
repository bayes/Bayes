/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.bayesEnterAsciiModel;

/**
 *
 * @author apple
 */
public interface BayesEneterAsciiConstants {
     public static final int        TOTAL_NUMBER_OF_MODELS     = 10;
     public static final String     MODEL                      = "Model";
     public static final String     C_MODEL_PREFIX             = "model_";
     public static final String     DUMMY_FORTRAN               =
                             "         Subroutine Model(CurSet," +"\n"
                            +"     C                   NoOfParams,"+"\n"
                            +"     C                   NoOfDerived,"+"\n"
                            +"     C                   TotalDataValues,"+"\n"
                            +"     C                   MaxNoOfDataValues,"+"\n"
                            +"     C                   NoOfDataCols,"+"\n"
                            +"     C                   NoOfAbscissaCols,"+"\n"
                            +"     C                   NoOfModelVectors,"+"\n"
                            +"     C                   Params,"+"\n"
                            +"     C                   Derived,"+"\n"
                            +"     C                   Abscissa,"+"\n"
                            +"     C                   Gij)"+"\n"
                            +"\n"
                            +"        Implicit  None"+"\n"
                            +"        Integer,       Intent(In)::   CurSet"+"\n"
                            +"        Integer,       Intent(In)::   NoOfParams"+"\n"
                            +"        Integer,       Intent(In)::   NoOfDerived"+"\n"
                            +"        Integer,       Intent(In)::   TotalDataValues"+"\n"
                            +"        Integer,       Intent(In)::   MaxNoOfDataValues"+"\n"
                            +"        Integer,       Intent(In)::   NoOfDataCols"+"\n"
                            +"        Integer,       Intent(In)::   NoOfAbscissaCols"+"\n"
                            +"        Integer,       Intent(In)::   NoOfModelVectors"+"\n"
                            +"        Real (Kind=8), Intent(In)::   Params(NoOfParams)"+"\n"
                            +"        Real (Kind=8), Intent(Out)::  Derived(NoOfDerived)"+"\n"
                            +"        Real (Kind=8), Intent(In)::   Abscissa(NoOfAbscissaCols,MaxNoOfDataValues)"+"\n"
                            +"        Real (Kind=8), Intent(InOut)::Gij(NoOfDataCols,MaxNoOfDataValues,NoOfModelVectors)"+"\n"
                            +"        Integer        CurEntry"+"\n"
                            +"\n"
                            +"        Derived(1) = 0D0"+"\n"
                            +"\n"
                            +"        Return"+"\n"
                            +"        End"+"\n";
     
}
