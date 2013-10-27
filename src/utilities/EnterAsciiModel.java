/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import bayes.DirectoryManager;
import java.io.*;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import bayes.Enums.*;
import bayes.ParameterPrior;
import bayes.ParameterPrior.ORDER_TYPE;
import bayes.ParameterPrior.PRIOR_TYPE;
import bayes.ParameterPrior.PARAMETER_TYPE;


/**
 *
 * @author larry
 */
public class EnterAsciiModel implements java.io.Serializable {
    public static final String LOADED_MESSAGE          = "Loaded";
    public static final String NOT_LOADED_MESSAGE      = "No Models are loaded";
    public static final String PARAMETR_EXTENSION      =  "params";



    private int NumberOfAbscissa                        =   1;
    private int NumberOfDataCols                        =   1;
    private int NumberOfModelVectors                    =   0;
    private int NumberOfPriors                          =   0;
    private int NumberOfDerived                         =   0;
    private List <ParameterPrior>Priors                 =   new ArrayList <ParameterPrior>() ;
    private File paramsFile                             =   null;
    private File modelFile                              =   null;
    protected String loadErrorMessage                   =   null;
    private String [] Derived                           =   null;
    protected boolean loaded                            =   false;
    private boolean    built                            =   false;
    private boolean    timedoutComplile                 =   false;
    private  final static String MAP_Enabled            = "  Enabled";
    private static final String EOL                     =   "\n";
    public static final String DEFNAME                  =  "NewModel.f";
    private String code                                 =   "";
    private String name                                 =   DEFNAME ;

    //default model
    public EnterAsciiModel(){
         setLoaded(false);
     }
    
     //  model and params files can be in the different directory
    public EnterAsciiModel(File aModelFile, File aParamsFile){
        boolean isLoaded = loadModel(aModelFile, aParamsFile);
        this.setLoaded(isLoaded);
    };
    public EnterAsciiModel(String aCode, String paramString){
        boolean isLoaded = loadModel(aCode,paramString);
        this.setLoaded(isLoaded);

    };


    private boolean loadModel(File mFile, File pFile){
        loadErrorMessage    =     null;
        setLoaded(false);

        if (mFile.exists() == false){
            loadErrorMessage =  String.format(
                                    "Model file %s \n" +
                                     "is not found. Abort model load...", mFile.getPath());
            return false;
        }
        else if ( pFile.exists() == false){
            loadErrorMessage =  String.format(
                    "Parameter file %s \n" +
                    "is not found. Abort model load...", pFile.getPath());
            return false;
        }


        modelFile               = mFile;
        paramsFile              = pFile;
        setNameFromFile();

        String pContent         = IO.readFileToString(pFile)  ;
        String mContent         = IO.readFileToString(mFile)  ;

        if (pContent == null){
            loadErrorMessage =  String.format(
                            "Failed to read %s file \n" +
                            "Abort model load...", pFile.getPath());
             return false;
       }
        if (mContent == null){
            loadErrorMessage =  String.format(
                            "Failed to read %s file \n" +
                            "Abort model load...", mFile.getPath());
             return false;
       }

      

         return  loadModel(mContent, pContent);
    }
    private boolean loadModel(  String aCode, String  params){
        loadErrorMessage    =     null;
        setLoaded(false);
        setCode(aCode);
       try{
             readParams(params);
       }
       catch( PriorParsingException exp){
             loadErrorMessage =  exp.getMessage();
             return false;
       }
       catch(Exception exp){
              loadErrorMessage =  String.format(
                            "Error while reading following line in the params file: \n" +
                             "\"%s\" \n" +
                            "Abort model load...",  exp.getMessage());
             return false;
       }

         return true;
    }


    public  void readFile(File file) throws IOException,
                                            NumberFormatException,
                                            InputMismatchException,
                                            NoSuchElementException,
                                            IllegalArgumentException,
                                            PriorParsingException 
    {
       String content                    =  IO.readFileToString(file);
       readParams(content);
    
    }
     public  void readParams(String content) throws
                                            NumberFormatException,
                                            InputMismatchException,
                                            NoSuchElementException,
                                            IllegalArgumentException,
                                            PriorParsingException
    {
       Scanner scanner                  =   new Scanner ( content);
       String line;

       // read Number of Abscissa
      if (scanner.hasNextInt()){
              setNumberOfAbscissa(scanner.nextInt());
              line                             =   scanner.nextLine();
       }
       else{
            line                                =  scanner.nextLine();
            throw new InputMismatchException(line );
       }


        // read Number of Vectors
       if (scanner.hasNextInt()){
              setNumberOfModelVectors(scanner.nextInt());
              line                             =   scanner.nextLine();
       }
       else{
            line                                =  scanner.nextLine();
            throw new InputMismatchException(line );
       }



        // read Number of Data Columns
       if (scanner.hasNextInt()){
              setNumberOfDataCols(scanner.nextInt());
              line                             =   scanner.nextLine();
       }
       else{
            line                                =  scanner.nextLine();
            throw new InputMismatchException(line );
       }


       // read Number of Priors
       if (scanner.hasNextInt()){
              setNumberOfPriors(scanner.nextInt());
              line                             =   scanner.nextLine();
       }
       else{
            line                                =  scanner.nextLine();
            throw new InputMismatchException(line );
       }


       for (int i = 0; i < NumberOfPriors; i++) {
            line                        =   scanner.nextLine();
            if (line.contains("MAP:")){
                 line                   =   scanner.nextLine();
            }
            ParameterPrior p            =   readParameterPrior(line);
            Priors.add(p);
       }

        setNumberOfDerived(scanner.nextInt());
        setDerived(new String[getNumberOfDerived()]);
        line                            =   scanner.nextLine();

        for (int I = 0; I < getNumberOfDerived();I++){
                    getDerived()[I]          = scanner.nextLine().trim();
        }

        scanner.close();

    }
    public static ParameterPrior readParameterPrior(String line) 
                    throws  PriorParsingException
    {
        ParameterPrior prior    = new ParameterPrior();
        Scanner scanner         = new Scanner (line);
        String str              = null;
        String message          = null;
        String [] theArray      = null;
        
        try {
            prior.name              = scanner.next();
        }
        
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read double.";
            message         +=    message + "\n";
            message         =     "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        
        }
        
        // read low, mean, high, sdev, norm
        try {
            prior.low               = scanner.nextDouble(); 
            prior.mean              = scanner.nextDouble(); 
            prior.high              = scanner.nextDouble(); 
            prior.sdev              = scanner.nextDouble();
            if (scanner.hasNextDouble()){
                 prior.norm              = scanner.nextDouble(); 
            }
           
        }
        catch ( InputMismatchException exp)
        {
            str             =       scanner.next();
            message         =       str + " can not be converted to double";
            message         =       message + "\n";
            message         =       message + "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read double.";
            message         =    message + "\n";
            message         =    message + "Error in line: "+ line;
             throw  new  PriorParsingException (message);
        
        }
        
        
        
        // read prior type
        try {
            str                 =   scanner.next();
            theArray            =   str.split("\\(");
            String typeName     =   theArray[0]; 
            prior.priorType     =   PRIOR_TYPE.getTypeByName(typeName);
          
        }
        catch (IllegalArgumentException exp){
                
            message         =     exp.getMessage() + " is not a legal prior type.";
            message         =     message + "\n";
            message         =     message +  "Error in line: "+ line;
             throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read prior type.";
            message         =    message + "\n";
            message         =    message +  "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        
        }
        
        
        
        
        // read isPriorEditable
         try {
            str                 =   theArray[1];
            theArray            =   str.split("\\)");
            if(theArray[0].equals("ne"))        { prior.isPriorTypeEditable     = false;} 
            else  if (theArray[0].equals("e"))  {prior.isPriorTypeEditable     = true; }
            else                                { throw  new  PriorParsingException (str);}
         }
        
         catch (ArrayIndexOutOfBoundsException exp){
                
            message         =     str + " must be in the form priorType(ne) or priorType(e).";
            message         =     message + "\n";
            message         =     message + "Error in line: "+ line;
             throw  new  PriorParsingException (message);
        }
         catch (IllegalArgumentException exp){
                
            message         =     exp.getMessage() + " is not a legal prior type.";
            message         =     message + "\n";
            message         =     message +  "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read prior type.";
            message         =    message + "\n";
            message         =    message +  "Error in line: "+ line;
           throw  new  PriorParsingException (message);
        
        }
        
        
        
        
       // read order type 
        try {
            str                 = scanner.next();
            theArray            = str.split("\\(");
            String Order        = theArray[0];  
            prior.order         = ORDER_TYPE.getTypeByName(Order);
           
            
        }
        catch (IllegalArgumentException exp){
                
            message         =     exp.getMessage() + " is not a legal order type.";
            message         =     message + "\n";
            message         =     message + "Error in line: "+ line;
           throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read prior type.";
            message         =    message + "\n";
            message         =    message +  "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        
        }
        
        
        
        
        // read isOrderEditable
        try{ 
            str                 = theArray[1];
            theArray            = str .split("\\)");
            if(theArray[0].equals("ne"))        { prior.isOrderEditable         = false;} 
            else  if (theArray[0].equals("e"))  {prior.isOrderEditable          = true; }
            else                                {
                message         =   "Failed to read whether order is editable.\n";
                message         =    message +  "Error in line: "+ line;
                throw  new  PriorParsingException (message);
            }
        }
        
         catch (ArrayIndexOutOfBoundsException exp){
                
            message         =     str + " must be in the form priorOrder(ne) or  priorOrder(e).";
            message         =    message + "\n";
            message         =    message +  "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        }
         catch (IllegalArgumentException exp){
                
            message         =     exp.getMessage() + " is not legal prior type.";
            message         =     message + "\n";
            message         =     message + "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read prior type.";
            message         =    message + "\n";
            message         =    message + "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        
        }
        
       
        try{  
            String paramType    = scanner.next();
            prior.setParameterType(PARAMETER_TYPE.getTypeByName(paramType));
        } 
         catch (IllegalArgumentException exp){
                
            message         =     exp.getMessage() + " is not legal parameter type.";
            message         +=    message + "\n";
            message         +=     "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        }
        catch (NoSuchElementException exp){
            
            message         =   "Input is exausted while  trying to read prior type.";
            message         +=    message + "\n";
            message         +=     "Error in line: "+ line;
            throw  new  PriorParsingException (message);
        
        }
       
        scanner.close();
        return prior;
    }


    public EnterAsciiModel cloneModel(){
         String  params         =   writeParamsContent().toString();
         EnterAsciiModel copy   =   new EnterAsciiModel(getCode(), params);
         if (paramsFile!= null){
            copy.paramsFile        =   new File (paramsFile.getAbsolutePath());
         }
         if (modelFile!= null){
            copy.modelFile         =   new File(modelFile.getAbsolutePath());
         }
         copy.setName(getName());
         copy.setBuilt(false);
         return copy;
    }
    public static boolean writeModelFiles( String modelName,
                                            File dir,
                                            EnterAsciiModel model
                                           ){

        if (dir.exists() == false){ dir.mkdirs();}
        String modelFileName        = modelName;
        String paramsFileName       = modelName;

        if (isCModel(modelName)){
            // do nothing
        }
        else if(isFortranModel(modelName))
        {
            // do nothing
        }
        else {
            modelFileName           = modelName + ".f";
        }
        paramsFileName              =   getParamsFileName(modelFileName);

        File paramsFile             =   new File(dir,paramsFileName  );
        File modelFile              =   new File(dir,modelFileName   );

        model.paramsFile            =   paramsFile ;
        model.modelFile             =   modelFile ;
        model.setName(modelFileName );

        if (modelFile.exists()){
            String message  =   String.format(
                    "Model %s already exists in\n" +
                    "the User model directory.\n" +
                    "Do you want to overwrite this model?", modelName );

            boolean proceed =  DisplayText.popupDialog(message);
            if (proceed == false){return  false;}

        }

        boolean success         =   false;
        String aCode            =   model.getCode();
        success                 =   IO.writeFileFromString(aCode,modelFile);
        if (success == false){return false;}

        String params           =   model.writeParamsContent().toString();
        success                 =   IO.writeFileFromString(params,paramsFile );
        if (success == false){return false;}


        return true;
    }

    public StringBuilder writeParamsContent(){
       StringBuilder sb             =   new StringBuilder();
       String pad                   =   "  ";
       sb.append(pad +  this.getNumberOfAbscissa()      + pad + "Number of Abscissa"+EOL);
       sb.append(pad +  this.getNumberOfModelVectors()  + pad + "Number of model vectors"+EOL);
       sb.append(pad +  this.getNumberOfDataCols()      + pad + "Number of data cols"+EOL);
       sb.append(pad +  this.getNumberOfPriors()        + pad + "Number of Priors"+EOL);

       sb.append(wrteParameterPrior(getPriors()));


       sb.append(pad +  this.getNumberOfDerived()       + pad +  "Number of Derived parameters"+EOL);

       for (String d : this.getDerivedList()) {
                sb.append( d +EOL);
       }
    

       return sb;

    }
    private static StringBuilder wrteParameterPrior(ParameterPrior prior ) {
      StringBuilder sb                  =   new StringBuilder();
      String padChar                    =   " ";
      String f                          =   "% 1.4E";
      
      sb .append(utilities.IO.pad(prior.name, 14, padChar));
      sb.append(" " + String.format(f,prior.low));
      sb.append(" " + String.format(f,prior.mean));
      sb.append(" " + String.format(f,prior.high));
      sb.append(" " + String.format(f,prior.sdev));
      //sb.append(" " + String.format(f,prior.norm));
      sb.append(" ");

      String priorType      =   prior.priorType.getName();
      String order          =   prior.order.name();
      String paramType      =   prior.getParameterType().toString();


      if(prior.isPriorTypeEditable == false){ priorType +=  "(ne)"; }
      else                                  { priorType +=  "(e)";     }

      if(prior.isOrderEditable == false)    { order     +=  "(ne)"; }
      else                                  { order     +=  "(e)";  }

      sb.append(utilities.IO.pad(priorType,16,padChar));
      sb.append(utilities.IO.pad(order,15,padChar));
      sb.append(utilities.IO.pad(paramType,15,padChar));
     return sb;
    }
    private static StringBuilder wrteParameterPrior(List<ParameterPrior> priors ) {
      StringBuilder sb                  =   new StringBuilder();
      
      // write non-Amplitude paraeters first
      for (ParameterPrior parameterPrior : priors) {
           if (parameterPrior.getParameterType() == PARAMETER_TYPE.Amplitude){
                continue;
           }    
           sb.append( wrteParameterPrior(parameterPrior) + EOL);
       }
       for (ParameterPrior parameterPrior :  priors) {
           if (parameterPrior.getParameterType() == PARAMETER_TYPE.Amplitude){
                 sb.append( wrteParameterPrior(parameterPrior)+EOL);
           }
           else {continue; }
       }
     return sb;
    }
    public void overwriteOriginalParamsFile(  ){
           File dst                        =    getParamsFile();
           String content                  =    writeParamsContent().toString();
           IO.writeFileFromString(content, dst);
    }


    public void writeParameterFile( File dstDir )  {
        String aCode                    =    writeParamsContent().toString();
        String filename                 =    getParamsFileName();
        File   dst                      =    new File (dstDir , filename);
        IO.writeFileFromString(aCode, dst);

    }
    public void writeModelFile( File dstDir )  {
        String aCode                    =   this.getCode();
        String filename                 =   this.getName();
        File   dst                      =   new File (dstDir , filename);
        IO.writeFileFromString(aCode, dst);

    }

 
    public static File     getParamsFileToRead(File modelFile){
        String paramFileName    =   getParamsFileName(modelFile);
        File dir                =   modelFile.getParentFile();
        File paramsFile         =   new File( dir ,paramFileName);

        return paramsFile;

     }

    public boolean          isCompatible( EnterAsciiModel amodel){
           if ( getNumberOfAbscissa() != amodel.getNumberOfAbscissa()) {
                return false;
           }
           if ( getNumberOfDataCols() != amodel.getNumberOfDataCols()) {
                return false;
            }

           return true;
     }

    @Override
    public String toString(){
        return this.getName();
    }
    public String getInfo(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("NumberOfAbscissa "+ getNumberOfAbscissa());
        sb.append("\n");
        
        sb.append("NumberOfDataCols "+ getNumberOfDataCols());
        sb.append("\n");
        
        sb.append("NumberOfModelVectors "+ getNumberOfModelVectors());
        sb.append("\n");
        
        sb.append("NumberOfPriors "+ getNumberOfPriors());
        sb.append("\n");
        
        sb.append("NumberOfDerived "+ getNumberOfDerived());
        sb.append("\n");
       
        sb.append("MAP_Enabled "+ getMAP_Enabled());
        sb.append("\n");
      
        for (ParameterPrior parameterPrior :  getPriors()) {
            sb.append( parameterPrior.toString());
            sb.append("\n");
        }
        
        for (int i = 0; i <getNumberOfDerived(); i++) {
            sb.append( getDerived()[i]);
             sb.append("\n");
        }
        
        return sb.toString();
  
    }

   public List<String> getParameterNames(){
         List<String> names         = new ArrayList<String>();
         List<ParameterPrior>  pr   =   getPriors();
         for (ParameterPrior p :pr ) {
                names.add(p.name);
         }
         return names;
    }
   public List<ParameterPrior> getPriors(){
         return  Priors;
    }
   public List<ParameterPrior> getNonAmplitudeParameteList(){
         List<ParameterPrior> prlist     = new ArrayList<ParameterPrior>();
         for (ParameterPrior p : getPriors() ) {
                 if ( p.getParameterType() == ParameterPrior.PARAMETER_TYPE.Amplitude){
                 continue;
                 }
                 else { prlist.add(p);}
                 
                 
                
                 
        }
         return  prlist;
    }
   public  void setParameteList(List<ParameterPrior> prlist){
       int size                 =   prlist.size();
       int nModelVectors        =   0;
       for (ParameterPrior p : prlist) {
          if (p.getParameterType() == PARAMETER_TYPE.Amplitude){
                nModelVectors +=1;
           }

       }
       this.Priors      =    prlist;
       this.setNumberOfPriors(size);
       this.setNumberOfModelVectors( nModelVectors);
    }
   public  void syncModel(){
       int nModelVectors        =   0;
       for (ParameterPrior p : getPriors()) {
          if (p.getParameterType() == PARAMETER_TYPE.Amplitude){
                nModelVectors +=1;
           }

       }
       this.setNumberOfPriors(getPriors().size());
       this.setNumberOfModelVectors( nModelVectors);
    }


   public  void addParameter(ParameterPrior newParameter){
       List<ParameterPrior>  list    = getPriors();
       list.add(newParameter);
       setParameteList(list);
   }
   public  void removeParameter(ParameterPrior newParameter){
       List<ParameterPrior>  list    = getPriors();
       list.remove(newParameter);
       setParameteList(list);
   }
   public  void removeAllParameters(){
       List<ParameterPrior>  list    = getPriors();
       list.clear();
       setParameteList(list);
   }

   public List<String> getDerivedList(){
         List<String> values         = new ArrayList<String>();
         if (getDerived() != null){
             for (String str :getDerived() ) {
                values.add(str);
         }
         }

         return values;
    }
   public void setDerivedList(List<String>  derivedList){
       int size                 =   derivedList.size();
       String [] d              = new  String[size];
       for (int i = 0; i <size; i++) {
          d[i]          =  derivedList.get(i);

       }
        setDerived(d);
        this.setNumberOfDerived(size);
    }
   public  void addDerived(String derived){
       List<String>  list    = getDerivedList ();
       list.add( derived);
       setDerivedList(list);
   }
   public  void removeDerived(String derived){
       List<String>  list    = getDerivedList ();
       list.remove(derived);
       setDerivedList(list);
   }
   public  void removeAllDerived(){
       List<String>  list    = getDerivedList ();
       list.clear();
       setDerivedList(list);
   }

   public String   getLoadErrorMessage () {
        return loadErrorMessage;
    }
   public String   getStatusMessage( ) {
        String status           =   "";
        boolean isLoaded        =   this.isLoaded();
        boolean isBuilt         =   this.isBuilt();
        String  mName           =   this.getName();
        if (isLoaded  == false){
            status  = NOT_LOADED_MESSAGE;
            return status;
        }



        status              =    mName  + ", "+  EnterAsciiModel.NOT_LOADED_MESSAGE;;

        if( isBuilt == true){
           status           =   mName  + ", BUILT";
        }
        else{
           status           =   mName +", Not Built";
        }

        return status;
    }
   
    public void     setLoaded ( boolean loaded ) {
        this.loaded = loaded;
    }
    public File     getParamsFile () {
        return paramsFile;
    }
    public File     getModelFile () {
        return modelFile;
    }
   
    
    public String   getModelLibName(){
        String prefix           = getModelPrefix();
        String modelLibName     = "lib" + prefix + ".so";
        return modelLibName;
    }
    public String   getModelPrefix(){
        String modelName        = getName ();
        int    n                = modelName.length();
        String modelPrefix      = modelName.substring(0,n-2);
        return modelPrefix;
    }
    public String   getModelSufix(){
        String modelName        = getName ();
        int    n                = modelName.length();
        String modelSuffix      = modelName.substring(n-2,n);
        return modelSuffix;
    }
    public String   getFortranListName(){
        String prefix            = getModelPrefix();
        String aname             =  prefix + ".lst";
        return aname;
    }
    public String   getParamsFileName(){
        String prefix           = getModelPrefix();
        String modelLibName     = prefix + "."+PARAMETR_EXTENSION ;
        return modelLibName;
    }
    public static String   getParamsFileName(File modelFie){
        String modelFileName    =   modelFie.getName();
        return getParamsFileName(modelFileName );

     }
    public static String   getParamsFileName(String name){
        int n                   =   name.length();
        String paramFileName    =   name.substring(0,n-2) + "."+PARAMETR_EXTENSION;
        return paramFileName;

     }
 


    public static boolean  isCModel(String modelname){
         if (modelname != null){
             return modelname.endsWith(".c");
         }
         else{
            return false;
         }
    }
    public static boolean  isFortranModel(String modelname){
         if (modelname != null){
             return modelname.endsWith(".f");
         }
         else{
            return false;
         }
    }
    public boolean  isLoaded () {
        return loaded;
    }
    public boolean  isBuilt() {
        return built;
    }
    public void     setBuilt(boolean built) {
        this.built = built;
    }
   



    public boolean updateModelBuilt(File dir){
        
        List <File> soFiles         =   getSoFiles(dir);
        boolean isBuilt             =   !soFiles.isEmpty();

        setBuilt(isBuilt);
        return isBuilt;

     }

    public static boolean updateModelBuilt( EnterAsciiModel m){
        File   dir                   =   DirectoryManager.getModelCompileDir();
        return  m.updateModelBuilt(dir);

     }


    public static List <File> getSoFiles(File dir){
        File [] files               =   dir.listFiles();
        List<File> soFiles         =   new ArrayList<File>();
        try{
            for (File file : files) {
            if (file.getName().endsWith(".so")){
                soFiles.add(file);
            }
            }
        
        }
        catch (Exception e){e.printStackTrace();}
        finally{
            return soFiles;
        }

     }
    public static void    removeSoFiles(File dir){
        List <File> files   =   getSoFiles(dir);

        for (File file : files) {
              file.delete();
        }
    }



    public String   generateErrorMessage(Exception ex){
      StringBuilder sb = new StringBuilder();
      sb.append( "Error occured while parsing parameter file. ");
      sb.append("\n");  
      sb.append( "Error type =  "+ ex.getClass().getName());
      sb.append("\n");

      String excepError = ex.getMessage();
      if (excepError != null){
         sb.append( "Error message : "+ excepError );
         sb.append("\n");

      }
      sb.append(getModelFile().getName() + " is not loaded.");
      sb.append("\n");
      return sb.toString();
    }

    
    public static void main(String [] args){
        // File file = new File("/Users/apple/Bayes/BayesAsciiModels/ContactTime.params");
       // EnterAsciiModel  r = new EnterAsciiModel (file,file);
       // System.out.println(r);
          String f                        =   "% 1.8E";
          double v                        = -5.478;
          System.out.println( String.format(f, v ));
           System.out.println( String.format(f, -v ));
    }

    public int getNumberOfAbscissa () {
        return NumberOfAbscissa;
    }
    public int getNumberOfDataCols () {
        return NumberOfDataCols;
    }
    public int getNumberOfModelVectors () {
        return NumberOfModelVectors;
    }
    public int getNumberOfPriors () {
        return NumberOfPriors;
    }
    public int getNumberOfDerived () {
        return NumberOfDerived;
    }
    public String getMAP_Enabled () {
        return MAP_Enabled;
    }
    public String[] getDerived() {
        return Derived;
    }


    public void setNumberOfAbscissa(int NumberOfAbscissa) {
        this.NumberOfAbscissa = NumberOfAbscissa;
    }
    public void setNumberOfDataCols(int NumberOfDataCols) {
        this.NumberOfDataCols = NumberOfDataCols;
    }
    public void setNumberOfModelVectors(int NumberOfModelVectors) {
        this.NumberOfModelVectors = NumberOfModelVectors;
    }
    public void setNumberOfPriors(int NumberOfPriors) {
        this.NumberOfPriors = NumberOfPriors;
    }
    public void setNumberOfDerived(int NumberOfDerived) {
        this.NumberOfDerived = NumberOfDerived;
    }
    public void setDerived(String[] aDerived) {
        this.Derived = aDerived;
          if (aDerived != null){
            this.setNumberOfDerived(aDerived.length);
        }
        else {
           this.setNumberOfDerived(0);
        }
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        if (name ==null){
            File file = getModelFile ();
            if (file != null)   {name   =  file.getName();}
            else                {name   =  DEFNAME ;}
        }

        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNameFromFile() {
         File file = getModelFile ();
         if (file != null)   {name   =  file.getName();}
         else                {name   =  DEFNAME;}

    }

    public boolean  isTimedoutComplile() {
        return timedoutComplile;
    }
    public void     setTimedoutComplile(boolean timedoutComplile) {
        this.timedoutComplile = timedoutComplile;
    }
  



   

    static class PriorParsingException extends Exception {
        public PriorParsingException(String message) {
            super(message);
        }
    }

}
