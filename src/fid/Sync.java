/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

/**
 *
 * @author apple
 */
public class Sync {


    public static enum SYNC_LEVEL     {NONE, PHASING, FFT};
      public static SYNC_LEVEL isSynchronizable(FidViewable fidviewable1, FidViewable fidviewable2){
        SYNC_LEVEL sync  =  SYNC_LEVEL.NONE;
        try{
             FidReader extReader         =   fidviewable1.getFidReader();
             FidReader thisReader        =   fidviewable2.getFidReader();
             if (fidviewable1.isLoaded() == false){
                sync  =  SYNC_LEVEL.NONE;
             }
             else if(fidviewable2.isLoaded() == false)
             {
                sync  =  SYNC_LEVEL.NONE;
             }
             else if(extReader.getUserLb() != thisReader.getUserFn())
             {
                   sync =  SYNC_LEVEL.FFT;
             }
             else if (extReader.getUserFn() != thisReader.getUserFn()) {
                    sync =  SYNC_LEVEL.FFT;;
             }
             else if (extReader.getDelay() != thisReader.getDelay()) {
                    sync =  SYNC_LEVEL.PHASING;
             }
             else if (extReader.getPhase0() != thisReader.getPhase0()) {
                    sync =  SYNC_LEVEL.PHASING;
             }
        }
        catch (Exception e){e.printStackTrace();}
        finally{
            return sync;
        }



    }
      public static void sync (FidViewable target, FidViewable standard){
        if ( target == null || standard == null){return;}
        SYNC_LEVEL sl =  isSynchronizable (target, standard);

         if (sl != SYNC_LEVEL.NONE ){

            FidReader targetReader              =    target.getFidReader();
            FidReader standardReader            =    standard.getFidReader();


            targetReader.setDelay(standardReader.getDelay());
            targetReader.setPhase0(standardReader.getPhase0());
            targetReader.setUserFn(standardReader.getUserFn());
            targetReader.setUserLb(standardReader.getUserLb());
            targetReader.setUnits(standardReader.getUnits(), false);
            targetReader.setReferenceFreqInHertz(standardReader.getReferenceFreqInHertz());

            try{ targetReader.storePresistently();
            }catch (Exception e){e.printStackTrace();}



            switch (sl){

                case NONE: break;

                case FFT:

                            target.getFidReader().computeSpectralData(true);
                            target.updatePlot();
                            break;

                case PHASING:

                            target.getFidReader().computeSpectralData(false);
                            target.updatePlot();
                            break;

            }


        }




   }


        
}
