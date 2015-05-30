/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayes;

/**
 *
 * @author apple
 */
public interface ApplicationConstants {

    
    String RESOURCE_DIR                     = "resources";
    String user_resource                    = "user.properties";
    String backup_resource                  = "backup.properties";
    String default_resource                 = "default.properties";

    String EOL                              = "\n";
    String FILE_SEPARATOR                   = "/";

    String SAMPLE_DATA_DIR_NAME             = "Bayes.test.data";
    String BAYES_MANUAL_FILE_NAME           = "BayesManual.pdf";
    String CLIENT_BAYES_MODEL               = "BayesAsciiModels";
    String CLIENT_SPEC_DIR                  = "Bayes.Predefined.Spec";


    // images
    String DFP                              = "4dfp";
    String IFH                              = "ifh";
    String IMG                              = "img";
    String ABS_IMAGE                        = "LoadedImage_Abs";
    String ABS_IFH_FILE_NAME                =  ABS_IMAGE + "." + DFP + "." + IFH;
    String IMAGE_DIR_NAME                   = "images";
    String IMAGE_FID_DIR                    = "image.fid";
    String IMAG_IMAGE_NAMEP_HASE1           = "BayesPhase2_Imag.4dfp.img";
    String IMAG_IMAGE_NAME_PHASE            = "BayesPhase_Imag.4dfp.img";
    String IM_IMAGE                         = "LoadedImage_Imag";
    String IM_IFH_FILE_NAME                 = IM_IMAGE + "." + DFP + "." + IFH;
    String IM_IMAGE_FILE_NAME               = IM_IMAGE + "." + DFP + "." + IMG;
    String ABS_IMAGE_FILE_NAME              = ABS_IMAGE + "." + DFP + "." + IMG;
    String RE_IMAGE                         = "LoadedImage_Real";
    String RE_IMAGE_FILE_NAME               = RE_IMAGE + "." + DFP + "." + IMG;
    String RE_IFH_FILE_NAME                 = RE_IMAGE + "." + DFP + "." + IFH;
    String REAL_IMAGE_NAME_PHASE            = "BayesPhase_Real.4dfp.img";
    String REAL_IMAGE_NAME_PHASE2           = "BayesPhase2_Real.4dfp.img";
    
    // ascii
    String AFH                              = "afh";
    String DAT                              = "dat";
    String ASCII_DATA_NAME                  = "dat.";
    String INDEX_FORMAT                     = "%04d";
    
    
    // fid
    String FFH                              = "ffh";
    String FID_DIR_NAME                     = "fid";
    String FID_FILE_NAME                    = "fid";
    String PROCPAR_FILE_NAME                = "procpar";
    String FID_LOADED_BY_JAVA               = "Fid File was loaded by java";
    String FID_LOADED_BY_USER               = "Fid File was loaded by user";
    String FID_UNLOADED                     = "Fid has been unloaded";
    String FID_MODEL_HAS_CHANGED            = "Fid model has been changed";
    String FID_TEXTFILE_NAME                = "text";
    String FID_TRACE_CHANGE                 = "Fid trace number has been changed";
    String FID_UNITS_ARE_CHANGED            = "Units in FIDViewer are changed";
    
    String ADD_OR_REOMOVE_RESONANCES        = "Resonances were added or removed";
   
    
    // fiels and dirs
    String ASCII_DIR_NAME                   = "BayesOtherAnalysis";
    String BAYES_ANALYZE                    = "BayesAnalyzeFiles";
    String BAYES_ACCEPT_FILE                = "Bayes.accepted";
    String CONSOLE_LOG_FILE                 = "console.log";
    
    String BAYES_NOISE_FILE_NAME            = "bayes.noise";
    String BAYES_PARAMS                     = "Bayes.params";
    String BAYES_PLOT_FILE_NAME             = "Bayes.Plot.List";
    String MODEL_FID_DIR                    = "Bayes.model.fid";
    String MODEL_FILE_NAME                  = "bayes.model";
    String PROBAILITY_FILE_NAME             = "Bayes.prob.model";
    String REGIONS_FILE_NAME                = "bayes.regions";
    String FOSTRANLIST_FILE_NAME            = "Fortran.lst";
    String MCMCVALUES_FILE_NAME             = "mcmc.values";


    String aBSCISSA_FILE_NAME               = "abscissa";
    String ABSCISSA_FILE_NAME               = "Abscissa";
    String CONDENSED_FILE_NAME              = "Bayes.Condensed.File";
    String bAYES_PARAMS                     = "bayes.params";
    String MCMC_SAMPLE_FILE_NAME            = "Bayes.Mcmc.Samples";
    String LOG_OUT_FILE_NAME                = "System.out.txt";
    String LOG_ERR_FILE_NAME                = "System.err.txt";



    //events
    String BOTH_MARKERS_ARE_SHOWN           = "Both markers are shown in FidChartPanel";
    String CURSOR_A_IS_DELETED              = "Cursor A is deleted in FidChartPanel";
    String CURSOR_A_IS_DRAWN                = "Cursor A is drawn in FidChartPanel";
    String CURSOR_B_IS_DELETED              = "Cursor B is deleted in FidChartPanel";
    String CURSOR_B_IS_DRAWN                = "Cursor B is drawn in FidChartPanel";


    String DATE_FORMAT                      = "dd-MMM-yyyy HH:mm:ss";
    
    String FREQ_PARAM_ADDED                 = "Frequency (and associated rate) parameter added in JAllPriors";
    String FREQ_PARAM_MODIFIED              = "Frequency parameter modified in JAllPriors";
    String FREQ_PARAM_REMOVED               = "Frequency (and associated rate) parameter removed in JAllPriors";
   
   
   
   
    String COMPILE_ERRROR                   = "Compile Error";
    String JRUN_JOB_CANCELED                = "Job is canceled in JRUN";
    String JRUN_JOB_END                     = "Job status is run or canceled in JRUN";
    String JRUN_JOB_START                   = "Job is submitted in JRUN";
    String JRUN_MODEL_IS_RUN                = "Bayes Model has been run by JRUN";
    String MODEL_PROGRAM_NAME               = "bayes_model";
    String PARAMETER_IS_SELECTED            = "New parameter prior is selected in JALLPriors";
    String RESONANCE_MARKED                 = "Resonances was marked";
    String FID_REFERENCE_CHANGED            = "FID Frequency has been changed";
   
    

    // properties
    String get_results                      = ".get_results";
    String get_status                       = ".get_status";
    String server_list                      = "server.list";
    String server_name                      = "server.name";
    String split                            = ",";
    String submit_job                       = ".submit_job";



}
