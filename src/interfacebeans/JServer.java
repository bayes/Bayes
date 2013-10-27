/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JServerList.java
 *
 * Created on Jan 27, 2009, 3:55:13 PM
 */

package interfacebeans;
import bayes.ApplicationPreferences;
import utilities.Server;
import javax.swing.*;
import java.util.*;
import bayes.DoCGI;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JServer extends javax.swing.JPanel
     {

    private static  JServer instance = new JServer();
    public JServer() {
         initComponents();
          updateGuiServers();
    }
    public static JServer getInstance(){

        if (instance == null ){instance = new JServer();}
        return instance;
    }

    public static void reset(){
       instance = null;
    }
 



  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        status_buttton = new javax.swing.JButton();
        setServerButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        serverLabel = new javax.swing.JLabel();

        FormListener formListener = new FormListener();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Server"));
        setLayout(new java.awt.GridLayout(2, 0));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        status_buttton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        status_buttton.setText("Status"); // NOI18N
        status_buttton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nShow server availability.\n\n</p><html>\n\n"); // NOI18N
        status_buttton.setName("status_buttton"); // NOI18N
        status_buttton.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel1.add(status_buttton, gridBagConstraints);

        setServerButton.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        setServerButton.setText(" Set  "); // NOI18N
        setServerButton.setToolTipText("<html><p style=\"margin: 6px;\"><font size=\"4\">\n\nSet the server on which<br>\n job will be run.\n\n</p><html>\n\n"); // NOI18N
        setServerButton.setMinimumSize(new java.awt.Dimension(40, 20));
        setServerButton.setName("setServerButton"); // NOI18N
        setServerButton.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel1.add(setServerButton, gridBagConstraints);

        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 5.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        add(jPanel1);

        serverLabel.setFont(new java.awt.Font("Lucida Fax", 1, 18));
        serverLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        serverLabel.setName("serverLabel"); // NOI18N
        add(serverLabel);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == status_buttton) {
                JServer.this.status_butttonActionPerformed(evt);
            }
            else if (evt.getSource() == setServerButton) {
                JServer.this.setServerButtonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void status_butttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_status_butttonActionPerformed
        Server server           =   getServer();
        String url              =   server.getHttpURL ();
        String user             =   server.getUser();
        String password         =   JServerPasswordDialog.getServerPassword ();
        int    usrOption        =   JServerPasswordDialog.getInstance ().getOption ();

        if(   usrOption == JServerPasswordDialog.CANCEL ) {return;}

        StringBuilder sb        =   DoCGI.getLoad (url, user, password);
        AllViewers.showMessage(sb.toString ());
}//GEN-LAST:event_status_butttonActionPerformed
    private void setServerButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setServerButtonActionPerformed
       JPopupMenu popup        = new JPopupMenu ();

        List<Server>  theservers =   getServers();
        for (Server theserver : theservers) {
                ServerMenuItem menuItem   = new ServerMenuItem(theserver);
                popup.add( menuItem );
        }
        if (theservers.isEmpty() == false){
            popup.add(new JSeparator());
        }
        popup.add(new  EditServerMenuItem());

        popup.show ( getServerButton (),  getServerButton ().getX(), getServerButton ().getY());
    }//GEN-LAST:event_setServerButtonActionPerformed
  
    public void updateGuiServers(){

        Server server               =  getServer();
        getServerNameComponent ().setText(server.getShortName());
       // setServerButton.setText(server.getShortName());
        JServerPasswordDialog.getInstance().resetIfNewServer(server.getName());
    }

 


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JButton setServerButton;
    private javax.swing.JButton status_buttton;
    // End of variables declaration//GEN-END:variables
    public Server getServer(){
        return ApplicationPreferences.getCurrentServer();
    }
    public List<Server> getServers(){
        return ApplicationPreferences. getServers();
    }



    public void setActive(boolean enabled){
       // serverNameTextField.setEnabled(enabled);
        setServerButton.setEnabled(enabled);
       // status_buttton.setEnabled(enabled);

    }

    public javax.swing.JButton      getStatusButtton () {
        return status_buttton;
    }
    public javax.swing.JButton      getServerButton () {
        return setServerButton;
    }
    public javax.swing.JLabel       getServerNameComponent () {
        return serverLabel;
    }

    private static final long serialVersionUID = 7526422295622576147L;

    // *************** GETTERS AND SETTERS **********************//
  

    class                       ServerMenuItem extends JMenuItem{
        private Server menuServer;
        ServerMenuItem (Server  aserver){
            super(aserver.getName());
            menuServer = aserver;

             addActionListener (new ActionListener (){
             public void actionPerformed (ActionEvent e) {
                    ServerMenuItem source = (ServerMenuItem)e.getSource();
                    Server server         = source.getMenuServer();
                    ApplicationPreferences.saveAsCurrentServer(server);
                    updateGuiServers();
                }
            });

        }

        public Server getMenuServer () {
            return menuServer;
        }
        public void setMenuServer ( Server menuServer ) {
            this.menuServer = menuServer;
        }
   }
    class                        EditServerMenuItem extends JMenuItem{
        EditServerMenuItem  (){
            super("Edit Servers");
             addActionListener (new ActionListener (){
             public void actionPerformed (ActionEvent e) {
                    JEditServers.showDialog();
                }
            });

        }

   }

    public static void main (String [] args){

    }



}
