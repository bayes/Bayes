/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bruker;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import utilities.IO;

/**
 *
 * @author apple
 */
public class BrukerDspTable {
      public  final static String DSP_TABLE_FILE      =   "/bruker/DSP_TABLE";
     
      public static final String DECIM_KEY = "DECIM";
      public static final String DSPFVS10_KEY = "DSPFVS 10";
      public static final String DSPFVS11_KEY = "DSPFVS 11";
      public static final String DSPFVS12_KEY = "DSPFVS 12";
      public static final String DSPFVS13_KEY = "DSPFVS 13";
      
      public static Map<Integer, Double>  DSPFVS_10 = new HashMap<Integer, Double>();
      public static Map<Integer, Double>  DSPFVS_11 = new HashMap<Integer, Double>();
      public static Map<Integer, Double>  DSPFVS_12 = new HashMap<Integer, Double>();
      public static Map<Integer, Double>  DSPFVS_13 = new HashMap<Integer, Double>();
      
      static {
          parseDSPTable();
      }
      
      public static Double getDelay(int decim, int sdpfvs){
          Double out = null;
          Map<Integer, Double> lookuptable = null;
          
          if(sdpfvs == 10){lookuptable = DSPFVS_10; }
          if(sdpfvs == 11){lookuptable = DSPFVS_11; }
          if(sdpfvs == 12){lookuptable = DSPFVS_12; }
          if(sdpfvs == 13){lookuptable = DSPFVS_13; }
          
          if(lookuptable != null){
              out = lookuptable.get(decim);
          }
          
          return out;
      }
      public  static void  parseDSPTable(){
          parseDSPTable(readDspTableAsString());
      }
        
      public  static String    readDspTableAsString(){
         String content =   null;
         InputStream is =   BrukerDspTable.class.getResourceAsStream(DSP_TABLE_FILE);
         content    =   IO.readInputStreamToString(is);
         return content;
     }
      public  static void  parseDSPTable(String content){
         Scanner scanner = new Scanner(content);
         Scanner linescanner = null;
         if(scanner.hasNextLine()){
             String headerline = scanner.nextLine();
         }
         while(scanner.hasNextLine()){
             String line = scanner.nextLine();
             linescanner = new Scanner(line);
             Integer decim = null;
             Double dspfvs = null;
             if(linescanner.hasNextInt()){
                 decim = linescanner.nextInt();
             }
             if(linescanner.hasNextDouble()){
                 dspfvs = linescanner.nextDouble();
                 DSPFVS_10.put(decim, dspfvs);
             }
             if(linescanner.hasNextDouble()){
                 dspfvs = linescanner.nextDouble();
                 DSPFVS_11.put(decim, dspfvs);
             }
             if(linescanner.hasNextDouble()){
                 dspfvs = linescanner.nextDouble();
                 DSPFVS_12.put(decim, dspfvs);
             }
             if(linescanner.hasNextDouble()){
                 dspfvs = linescanner.nextDouble();
                 DSPFVS_13.put(decim, dspfvs);
             }
             linescanner.close();
         }
        
         if(scanner != null){
             scanner.close();
         }
       
     }
      
      
      public static void main(String []args){
//          for (Integer key : DSPFVS_10.keySet()) {
//              System.out.println(key + " = "+ DSPFVS_10.get(key) );
//              System.out.println(key + " = "+ DSPFVS_11.get(key) );
//              System.out.println(key + " = "+ DSPFVS_12.get(key) );
//              System.out.println(key + " = "+ DSPFVS_13.get(key) );
//              System.out.println("");
//              
//          }
          
          System.out.println(getDelay(4, 12));
          System.out.println(getDelay(23, 12));
          System.out.println(getDelay(2, 13));
          System.out.println(getDelay(4, 20));
      }
           
}
