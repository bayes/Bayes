/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import utilities.InfiniteProgressPanel;
import bayes.PackageManager;

/**
 *
 * @author apple
 */
public abstract class AbstractCallBack  implements java.awt.event.ActionListener {
        boolean isStarted                           =   false;
        private java.awt.Component glassPane        =    null;
        public static String progressMessage        =    null;
        InfiniteProgressPanel progresspane          =    new InfiniteProgressPanel();
        JFrame  frame                               =    getApplicationFrame();


        public static JFrame getApplicationFrame(){
            Component component         =   (Component)PackageManager.getCurrentApplication();
            JFrame aframe               =   (JFrame)SwingUtilities.windowForComponent( component);

            return aframe;
        }
      

        abstract public boolean isKeepRunning();
        abstract public void takeFinalActions();
        abstract public void stopTimer();


        public void actionPerformed(java.awt.event.ActionEvent ev){

           if(isStarted == false){

                progresspane            =   new InfiniteProgressPanel();
                glassPane               =   frame.getGlassPane();
                frame.setGlassPane(progresspane );
                frame.validate();
                progresspane.start();
                isStarted               = true;
       }


          if (isKeepRunning() == false) {
                stopTimer();
                frame.setGlassPane(glassPane);
                frame.validate();
                progresspane.stop();
                takeFinalActions();
                
            }
            else{

                 SwingUtilities.invokeLater( new Runnable(){
                     public void run() {  progresspane.setText(progressMessage);}

                 });

            }

          }
    }
