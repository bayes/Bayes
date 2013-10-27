/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;
import bayes.BayesManager;
import utilities.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
/**
 *
 * @author apple
 */
public abstract class ProcparFileWriter  implements ProcparConstants {
    private Procpar procpar                             =   new Procpar();
    public  final static String         space           =   " ";
    public  final static String FID_PROTOTYPE_FILE      =   "/fid/ProtoType";
    public  final static String IMAGE_PROTOTYPE_FILE    =   "/fid/ImagePrototypeProcpar";
   


    public abstract boolean writeProcparFile(Procpar pr , File dist);






    public static String writeParameter(    String key,
                                            Object values,
                                            int subtype,
                                            int basictype,
                                            double maximumValue,
                                            double mininumValue,
                                            double stepSize,
                                            int gGroup,
                                            int dGroup,
                                            int protection,
                                            int active){


          StringBuilder sb           =   new  StringBuilder();
          String        EOL          =   BayesManager.EOL;
          boolean isArray            =   values.getClass().isArray();
          String format              =   "########################.###########";
          DecimalFormat  formatter   =   new DecimalFormat(format);

          // first line
          sb.append(key);
          sb.append(space);
          sb.append(subtype);
          sb.append(space);
          sb.append(basictype);
          sb.append(space);
          sb.append( formatter.format(maximumValue) );
          sb.append(space);
          sb.append( formatter.format(mininumValue) );
          sb.append(space);
          sb.append(  formatter.format(stepSize) );
          sb.append(space);
          sb.append(gGroup);
          sb.append(space);
          sb.append(dGroup);
          sb.append(space);
          sb.append(protection);
          sb.append(space);
          sb.append(active);
          sb.append(space);
          sb.append(64);
          sb.append(space);
          sb.append(EOL);

          // second line
          StringBuilder nextLine        = null;
          if (isArray){
             nextLine =  writeArrayedSecondLine(values, basictype, space, EOL);
          }
          else{
             nextLine = writeNotArrayedSecondLine(values, basictype, space, EOL);
          }
           sb.append(nextLine);

          // third line
          sb.append(0);
          sb.append(EOL);


         return sb.toString();


    }

    public static StringBuilder writeNotArrayedSecondLine( Object values, int basictype, String space, String EOL) {
         StringBuilder sb           =   new  StringBuilder();

        // if number
         if (basictype == 1){
              Number value = (Number)values;
              sb.append(1);
              sb.append(space);
              sb.append(value);

          }

         // if string
         else if (basictype == 2){
              sb.append(1);
              sb.append(space);
              sb.append("\"");
              sb.append(values.toString());
              sb.append("\"");
              }
        else {
                 sb.append(0);

              }
              sb.append(EOL);

         return  sb;
    }
    public static StringBuilder writeArrayedSecondLine( Object values, int basictype, String space, String EOL) {
         StringBuilder sb           =   new  StringBuilder();

         // if number
         if (basictype == 1){
              String [] doubleValues    =   (String []) values;;
              int size                  =    doubleValues.length;

              sb.append(size );
              sb.append(space);
              for (int i = 0; i <size ; i++) {
                   sb.append(doubleValues[i]);
                   sb.append(space);
              }

          }
         // if string
          else if (basictype == 2){
              String [] stringValues    =   (String []) values;
              int size                  =    stringValues.length;
              sb.append(1);
              sb.append(space);
              sb.append("\"");

              if (size == 1){
                  sb.append(stringValues[0]);
              }
              else{
                 // opening parenthesis
                 sb.append("(");

                 for (int i = 0; i < size; i++) {
                   sb.append(stringValues[i]);

                   // if not the last parameter, insert comma
                   if (i != size -1){
                       sb.append(",");
                   }

                }
                  // closing parenthesis
                  sb.append(")");
              }


              sb.append("\"");
              }

              sb.append(EOL);

         return  sb;
    }

    public static StringBuilder  deleteArrayedParameters (String content, List< String > array) {
        StringBuilder sb        =   new StringBuilder();
        Scanner scanner         =   new Scanner (content);
        String EOL              =       BayesManager.EOL;
        while(scanner.hasNextLine()){
            String line         =   scanner.nextLine();
            boolean matchFound  =   false;

            for (String string : array) {
                String curPar  =string + " ";
                matchFound =line.startsWith(curPar);
            }
         
            if (matchFound){
                String tmp      = line.trim().split(space )[2];
                int basicType   = Integer.parseInt(tmp);
                if (basicType != 2){
                     // skip two more lines
                        line    = scanner.nextLine();
                        line    = scanner.nextLine();

                }else {
                    line        = scanner.nextLine();
                    tmp         = line.trim().split(space )[0];
                    int skip    = Integer.parseInt(tmp);
                    for (int i = 0; i <skip; i++) {
                       scanner.nextLine();

                    }

                }


            }
            else{
                sb.append(line);
                sb.append(EOL);
            }

        }

        return sb;
    }

   public  String    getProcparPrototypeAsString(){
       
         String content             =   null;

         InputStream is             =   getClass().getResourceAsStream(getPrototypeFileName());
         content                    =   IO.readInputStreamToString(is);
   
         return content;
    }
   private   boolean isStringParameter( String line ){

              String [] strArray    =  line.trim().split(space );
              if (strArray == null || strArray.length == 0) {return false;}


              String tmp            =   strArray[2];
              int basicType         =   Integer.parseInt(tmp);

              if (basicType == 2){return true; }
              else {return false;}
   }
   private   boolean isStringParameterInProcpar (String paramName, String procparConetent){
        Scanner scanner         =       new Scanner (procparConetent);

        while(scanner.hasNextLine()){
            String line     =     scanner.nextLine();
            if (line.startsWith( paramName + space)){

                boolean isString =  isStringParameter( line);
                return isString;
            }
        }


        return false;
   }

   public   boolean isStringParameterInProcpar (String paramName){
        String content =  getProcparPrototypeAsString();
        if(content == null) {return false;}

        return isStringParameterInProcpar(paramName, content);
   }


    public Procpar getProcpar () {
        return procpar;
    }
    public void setProcpar ( Procpar procpar ) {
        this.procpar = procpar;
    }

    public abstract String getPrototypeFileName();

}
