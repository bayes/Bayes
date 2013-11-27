/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FidPriorsHybrid.java
 *
 * Created on Jan 29, 2009, 9:36:54 AM
 */

package interfacebeans;
import fid.FidViewer;
import bayes.ParameterPrior;
import bayes.ApplicationConstants;
import bayes.ParameterPrior.PARAMETER_TYPE;
import java.awt.Component;

/*
 *
 * @author apple
 */
public class FidPriorsHybrid extends javax.swing.JPanel implements Viewable{



   private static  FidPriorsHybrid instance           = null;

    public static FidPriorsHybrid getInstance () {
        if ( instance == null ) {
            instance = new  FidPriorsHybrid();
        }
        return instance;
    }
    private FidPriorsHybrid() {
        initComponents();
    }

    public static void reset(){

        if(instance != null){
            instance.removeAll();
            FidViewer.reset();
            JAllPriors.reset();
            instance = null;
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane = new javax.swing.JSplitPane();
        fidViewer = FidViewer.getInstance();
        priorViewer = JAllPriors.getInstance();

        FormListener formListener = new FormListener();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jSplitPane.setDividerLocation(800);
        jSplitPane.setContinuousLayout(true);
        jSplitPane.setName("jSplitPane"); // NOI18N
        jSplitPane.setOneTouchExpandable(true);

        fidViewer.setName("fidViewer"); // NOI18N
        jSplitPane.setLeftComponent(fidViewer);

        priorViewer.setName("priorViewer"); // NOI18N
        priorViewer.addPropertyChangeListener(formListener);
        jSplitPane.setRightComponent(priorViewer);

        add(jSplitPane);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.beans.PropertyChangeListener {
        FormListener() {}
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == priorViewer) {
                FidPriorsHybrid.this.priorViewerPropertyChange(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void priorViewerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_priorViewerPropertyChange
        if(evt.getPropertyName().equals(ApplicationConstants.PARAMETER_IS_SELECTED)  ){
             ParameterPrior prior = (ParameterPrior) evt.getNewValue();
             if(prior.getParameterType() ==  PARAMETER_TYPE.Frequency){
                   FidViewer.getInstance().getFidPlotData().setColorForSeries(prior.name);
         }
       }
        else if ( evt.getPropertyName().equals(ApplicationConstants.FREQ_PARAM_MODIFIED) ) {
            FidViewer.getInstance().updatePlot();
        }
        else if ( evt.getPropertyName().equals(ApplicationConstants.FREQ_PARAM_REMOVED) ) {
            FidViewer.getInstance().updatePlot();
        }
        else if( evt.getPropertyName().equals(ApplicationConstants.FREQ_PARAM_ADDED) ) {
            FidViewer.getInstance().updatedLoadedState();
        }

    }//GEN-LAST:event_priorViewerPropertyChange

 public Component getMainDisplay(){return FidViewer.getInstance().getMainDisplay();}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fidViewer;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JPanel priorViewer;
    // End of variables declaration//GEN-END:variables

}