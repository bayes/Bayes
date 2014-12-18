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
public class ProcparFileWriterForFid extends  ProcparFileWriter{
   
     public  String getPrototypeFileName(){
        return   FID_PROTOTYPE_FILE;
     }
     public boolean  writeProcparFile(Procpar pr , File dist){
        StringBuilder procparContent  = procpar2StringBuilder(pr);
        if(procparContent == null) {return false;}
        boolean isDone                  = IO.writeFileFromString(procparContent.toString(), dist);
        return isDone;
    }
    
     public StringBuilder procpar2StringBuilder(Procpar pr ){
       String temp           =       null;
       String content     =     getProcparPrototypeAsString();
       StringBuilder sb    =   new StringBuilder();

       if (content == null) {return null;}


         temp = writeParameter(  FILE_KEY, pr.getFileSource()   , 2,2, 6, 0,0,2,1,3,1);
         sb.append(temp);


         temp = writeParameter(  AT_KEY,  pr.getAt()   , 1,1, 100, 0.001, 0.001,2,1,11,1);
         sb.append(temp);

         temp = writeParameter(  FN_KEY,  pr.getFn()   , 7,1, 524288 , 64, -2, 3, 1,1 ,1);
         sb.append(temp);

         temp = writeParameter(  NP_KEY,  pr.getNp()   ,  7,1, 128000, 64, 64,2,1,11,1);
         sb.append(temp);

         temp = writeParameter(  SW_KEY,  pr.getSw()   ,  1,1, 5, 5, 5,2,1,8203,1);
         sb.append(temp);

         temp = writeParameter(  SFRQ_KEY,  pr.getSfrq()   ,  1,1,  1000000000 , 0, 0,2,1,3,1);
         sb.append(temp);

         temp = writeParameter(  LB_KEY,  pr.getLb()   ,  1,1,  100000 ,  -100000, 0,3,1,1,1);
         sb.append(temp);

         //!!!!!! what to do with reffrq - ask Larry



         temp = writeParameter(  RFL_KEY,  pr.getRfl()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);

         temp = writeParameter(  RFP_KEY,  pr.getRfp()   ,  1,1,  1000000000 , -1000000000, 0,4,1,1,1);
         sb.append(temp);

         temp = writeParameter(  FPMULT_KEY,  pr.getFpmult ()  ,  1,1,  1000000000 , 0, 0,3,1,0,0);
         sb.append(temp);
         
         temp =  writeSimpleParameter(  FFT_TYPE_KEY, new String []{FFT_TYPE_KEY +" "+pr.getFftSign(), "0"});
         sb.append(temp);


         if (pr.isDataArrayed()){
             List <String>      arrayList   =   pr.getArray();
             String [][] arrayValues    =   pr.getArrayValues();

            // clear previous setting for arrayed parameter
            //prior to writing in again.

            // if arrayed paramters are at the body of the procpar prototype file
            content =  deleteArrayedParameters(content, arrayList).toString();

            // if arrayed paramters are at the top of the procpar file that we have just written
            sb      =  deleteArrayedParameters(sb.toString(), arrayList);

            // Write arrayed parameters
             String[] array = arrayList.toArray(new String[arrayList.size()]);  
            
             // active key is truned on  (last integer in the argument list = 1)
             temp = writeParameter(  ARRAYDIM_KEY,  pr.getArrayDim()  ,  7,1, 32768 , 1,1,2,1,5,1);
             sb.append(temp);

              // active key is truned on  (last integer in the argument list = 1)
             temp = writeParameter(  ARRAY_KEY, array  ,  2,2,  256 , 0, 0,2,1,1,1);
             sb.append(temp);

             for (int i = 0; i < array.length; i++) {
                temp = writeParameter(  array[i], arrayValues[i]  ,  1,1,  10e+17, -10e+17, 0, 2, 1, 0, 1);
                sb.append(temp);

             }

         }
         else {
             // active key is truned onn (last integer in the argument list = 1)
             temp = writeParameter(  ARRAYDIM_KEY,  pr.getArrayDim()  ,  7,1, 32768 , 1,1,2,1,5,1);
             sb.append(temp);


              // active key is truned off (last integer in the argument list = 0)
             temp = writeParameter(  ARRAY_KEY, ""  ,  2,2,  256 , 0, 0,2,1,1,0);
             sb.append(temp);
         }



         sb.append(content);

         return sb;
     }
     
     
     public static void main(String [] args){

      new ProcparFileWriterForFid(). getProcparPrototypeAsString();
     }
}
