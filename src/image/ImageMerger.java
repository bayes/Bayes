/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import bayes.Enums.IMAGE_DIMENSION;
import image.raw.BinaryReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author apple
 */
public class ImageMerger {
   
   public BinaryReader  mergeByElements( List<BinaryReader> inputReaders){
       BinaryReader out                 =   null;
       if(inputReaders.isEmpty()){
           throw new IllegalArgumentException("List of readers is empty");
       }
       List<BinaryReader> filterForEquivalent = filterForEquivalent(inputReaders);
       BinaryReader reader                    = filterForEquivalent.get(0);
       try{
           for (BinaryReader curReader : filterForEquivalent) {
               reader.mergeByElements(curReader);
           }
           
           out                                    = reader;
       
       }
       catch (Exception e){
           e.printStackTrace();
       
       }
       finally{
       
       }
       
       
       return out;
       
   } 
   
   
   public List<BinaryReader> filterForEquivalent( List<BinaryReader> readers){
       List<BinaryReader> equivalenReaders  =   new ArrayList<BinaryReader>();
       BinaryReader etalon                  =   readers.get(0);
       equivalenReaders.add(etalon);
       try{
           for (BinaryReader currentReader : equivalenReaders) {
               boolean isEquivalent        = etalon.isMergingEquivalent(currentReader );
               if(isEquivalent){
                   equivalenReaders.add(currentReader);
               }
           }
       }
       catch (Exception e){
           e.printStackTrace();
       
       }
       finally{
       
       }
       
       
       return equivalenReaders;
       
   } 
}
