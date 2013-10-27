/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import utilities.*;
import java.io.*;
import java.util.List;
/**
 *
 * @author apple
 */
public class ProcparFileWriterForImages extends  ProcparFileWriter {
     public  String getPrototypeFileName(){
        return  IMAGE_PROTOTYPE_FILE ;
     }
     public boolean  writeProcparFile(Procpar pr , File dist){
        String temp           =       null;
        boolean isDone        =       false;

        String content     =     getProcparPrototypeAsString();
        if (content == null) {return false;}

         StringBuilder sb    =   new StringBuilder();

         temp = writeParameter(  FILE_KEY, pr.getFileSource()   , 2,2, 6, 0,0,2,1,3,1);
         sb.append(temp);


         temp = writeParameter(  AT_KEY,  pr.getAt()   , 1,1, 100, 0.001, 0.001,2,1,8203,1);
         sb.append(temp);

         temp = writeParameter(  NP_KEY,  pr.getNp()   ,  7,1,  524288, 32, 2,2,1,11,1);
         sb.append(temp);

         temp = writeParameter(  NV_KEY,  pr.getNv()   ,  1,1, 1000000, 0, 1,2,1,9,1);
         sb.append(temp);

         temp = writeParameter(  NS_KEY,  pr.getNs()   ,  1,1, 1000000, 0, 1,2,1,9,1);
         sb.append(temp);

         float val =    pr.getLengthPhaseEncodeInCM();
         temp = writeParameter(  LPE_KEY,  val,  1,1, 1000000, -1000000 , 0,2,1,0,1);
         sb.append(temp);

         val =    pr.getLengthReadOutInCM();
         temp = writeParameter(  LRO_KEY,  val,  1,1, 1000000, -1000000 , 0,2,1,0,1);
         sb.append(temp);

          val =    pr.getThicknesInMM();
         temp = writeParameter(  THK_KEY,  val,  1,1, 1000000, -1000000 , 0,2,1,0,1);
         sb.append(temp);

         temp = writeParameter(  FN_KEY,  pr.getFn()   , 7,1, 524288 , 64, -2, 3, 1,1 ,1);
         sb.append(temp);

         temp = writeParameter(  FN1_KEY,  pr.getFn1()   , 7,1, 524288 , 64, -2, 3, 1,1 ,1);
         sb.append(temp);

         temp = writeParameter(  FPMULT_KEY,  pr.getFpmult ()  ,  1,1,  1000000000 , 0, 0,3,1,0,0);
         sb.append(temp);


         temp = writeParameter(  RFL_KEY,  pr.getRfl()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);

         temp = writeParameter(  RFP_KEY,  pr.getRfp()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);

         temp = writeParameter(  RFL1_KEY,  pr.getRfl1()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);

         temp = writeParameter(  RFP1_KEY,  pr.getRfp1()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);


         temp = writeParameter(  SFRQ_KEY,  pr.getSfrq()   ,  1,1,  1000000000 , 0, 0,2,1,3,1);
         sb.append(temp);


         temp = writeParameter(  SW_KEY,  pr.getSw()   ,  1,1, 5, 5, 5,2,1,8203,1);
         sb.append(temp);

         temp = writeParameter(  SW1_KEY,  pr.getSw1()   ,  1,1,  2000000, 0, 0,2,1,0,1);
         sb.append(temp);

         temp = writeParameter(  TPE_KEY,  pr.getTpe()   ,  3,1,  14, 14, 14, 2 , 1 , 9192,1);
         sb.append(temp);

         temp = writeParameter(  GRO_KEY,  pr.getGro()   ,  1,1,  1000, -1000, 0, 2 , 1 , 0,1);
         sb.append(temp);

         temp = writeParameter(  GPE_KEY,  pr.getGpe()   ,  1,1,  1000, -1000, 0, 2 , 1 , 0,1);
         sb.append(temp);

         temp = writeParameter(  NI_KEY,  pr.getNi()   ,  1,1,  1000, 0, 1, 2 , 1 , 1,1);
         sb.append(temp);

         temp = writeParameter(  NF_KEY,  pr.getNf()   ,  1,1,  1000, 0, 1, 2 , 1 , 1,1);
         sb.append(temp);

         temp = writeParameter(  CF_KEY,  pr.getCf()   ,  7,1,  1000000, 0, 1, 2 , 1 , 1,1);
         sb.append(temp);



         // write slice indexing
         int ns =   pr.getNs();
         String [] indexes = new String [ns];
         for (int i = 0; i < indexes.length; i++) {
            indexes[i] = "" + i*  pr.getThicknesInMM();

         }

         temp = writeParameter(  PSS_KEY, indexes ,1,1,  1000000, -1000000, 0, 2 , 8 , 1,1);
         sb.append(temp);







         if (pr.isDataArrayed()){
             List<String>  array            =   pr.getArray();
             String [][] arrayValues        =   pr.getArrayValues();


              // clear previous setting for arrayed parameter
            //prior to writing in again.

            // if arrayed paramters are at the body of the procpar prototype file
            content =  deleteArrayedParameters(content, array).toString();

            // if arrayed paramters are at the top of the procpar file that we have just written
            sb      =  deleteArrayedParameters(sb.toString(), array);


            // Write arrayed parameters


             // active key is turned on  (last integer in the argument list = 1)
             temp = writeParameter(  ARRAYDIM_KEY,  pr.getArrayDim()  ,  7,1, 32768 , 1,1,2,1,5,1);
             sb.append(temp);

              // active key is turned on  (last integer in the argument list = 1)
             temp = writeParameter(  ARRAY_KEY, array  ,  2,2,  256 , 0, 0,2,1,1,1);
             sb.append(temp);

             
             for (int k = 0; k <array.size(); k++) {
                temp = writeParameter(  array.get(k), arrayValues[k]  ,  1,1,  10e+17, -10e+17, 0, 2, 1, 0, 1);
                sb.append(temp);

             }
            



         }
         else {
             // active key is turned ON!!!!! (last integer in the argument list = 1)
             temp = writeParameter(  ARRAYDIM_KEY,  pr.getArrayDim()  ,  7,1, 32768 , 1,1,2,1,5,1);
             sb.append(temp);


              // active key is turned off (last integer in the argument list = 0)
             temp = writeParameter(  ARRAY_KEY, ""  ,  2,2,  256 , 0, 0,2,1,1,0);
             sb.append(temp);
         }



         sb.append(content);
         isDone = IO.writeFileFromString(sb.toString(), dist);



        return isDone;
    }
}
