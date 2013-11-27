/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JSoftawareUpdates.java
 *
 * Created on Jul 31, 2009, 3:08:35 PM
 */

package interfacebeans;
import bayes.ApplicationPreferences;
import javax.swing.*;
import java.util.*;
import utilities.Server;
import java.awt.MouseInfo;
import javax.swing.text.*;
import java.awt.Color;
/**
 *
 * @author apple
 */
public class JSoftwareUpdates extends javax.swing.JDialog {
    public static final boolean modal           =   true;
    public static final JFrame parent           =   null;
    private static JSoftwareUpdates instance    =   new  JSoftwareUpdates();

    private String message                      =   null;
    List<Server> servers                        =   new  ArrayList<Server>();
    Color highlightColor                        =   new Color (200,200,200);
    MyHighlightPainter myHighlightPainter       =   new MyHighlightPainter(highlightColor);


    private JSoftwareUpdates() {
           super(parent, modal);
           initComponents();
    }

    public static  JSoftwareUpdates getInstance(){

        if (instance == null ){instance = new  JSoftwareUpdates();}
        return instance;
    }

    /** Creates new form JSoftawareUpdates */
    public JSoftwareUpdates(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        ServersPane = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serverList = new javax.swing.JList();
        InfoPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        messagPane = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        buttonPane = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Check for Server Software Updates"); // NOI18N
        setName("Form"); // NOI18N

        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 600));

        ServersPane.setName("ServersPane"); // NOI18N
        ServersPane.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Academy Engraved LET", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Servers");
        jLabel2.setName("jLabel2"); // NOI18N
        ServersPane.add(jLabel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        serverList.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        serverList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        serverList.setName("serverList"); // NOI18N
        serverList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                serverListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(serverList);

        ServersPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(ServersPane);

        InfoPane.setName("InfoPane"); // NOI18N
        InfoPane.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        messagPane.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        messagPane.setName("messagPane"); // NOI18N
        messagPane.setOpaque(false);
        jScrollPane2.setViewportView(messagPane);

        InfoPane.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Academy Engraved LET", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Server Software Information ");
        jLabel1.setName("jLabel1"); // NOI18N
        InfoPane.add(jLabel1, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(InfoPane);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        buttonPane.setName("buttonPane"); // NOI18N

        closeButton.setText("Close"); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Check for Updates"); // NOI18N
        updateButton.setName("updateButton"); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPaneLayout = new org.jdesktop.layout.GroupLayout(buttonPane);
        buttonPane.setLayout(buttonPaneLayout);
        buttonPaneLayout.setHorizontalGroup(
            buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPaneLayout.createSequentialGroup()
                .addContainerGap(548, Short.MAX_VALUE)
                .add(updateButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton))
        );
        buttonPaneLayout.setVerticalGroup(
            buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(closeButton)
                .add(updateButton))
        );

        getContentPane().add(buttonPane, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serverListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_serverListValueChanged
        Server server = (Server)getServerList ().getSelectedValue();
        highlightServerInfo(server);
    }//GEN-LAST:event_serverListValueChanged

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        getUpdateInfo();

    }//GEN-LAST:event_updateButtonActionPerformed

    public void getUpdateInfo(){
        if (this.servers.isEmpty()){
             setMessage("");
        }
        else{
            String text           =   utilities.URLManager.checkForServerSofrwareUpdates(servers);
            setMessage(text);

            removeHighlights();
            getServerList ().clearSelection();
        }
        
    }


    public void highlightServerInfo(Server server){
        removeHighlights();

        if (server == null){return;}

         try {
            Highlighter hilite  =   getMessagPane().getHighlighter();
            Document doc        =   getMessagPane().getDocument();
            String text         =   doc.getText(0, doc.getLength());
            int pos             =   0;

            // Search for pattern
            String pattern      =   server.getName();

            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                 getMessagPane().setCaretPosition(pos);
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                pos += pattern.length();

            }
        } catch (BadLocationException e) {
        }
    }

      // Removes only our private highlights
    public void removeHighlights() {
        Highlighter hilite              =   getMessagPane().getHighlighter();
        Highlighter.Highlight[] hilites =   hilite.getHighlights();

        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }



      public static  JSoftwareUpdates showDialog(){
         JSoftwareUpdates sereverEdit   =   JSoftwareUpdates.getInstance();

        Server theserver                = ApplicationPreferences.getCurrentServer();
        sereverEdit.servers             = ApplicationPreferences.getServers();
        sereverEdit.updateServers(theserver , sereverEdit.servers);

        sereverEdit.setLocation(MouseInfo.getPointerInfo().getLocation());
        sereverEdit.getUpdateInfo();
        sereverEdit.setVisible(true);

        
        return sereverEdit;
    }

      public void updateServers(Server theserver, List <Server> theservers){
           DefaultListModel model = new DefaultListModel ();
           for (Server server : theservers) {
                model.addElement(server);
          }

          getServerList ().setModel( model );

    }



    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JSoftwareUpdates dialog = new JSoftwareUpdates(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel InfoPane;
    private javax.swing.JPanel ServersPane;
    private javax.swing.JPanel buttonPane;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextPane messagPane;
    private javax.swing.JList serverList;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JList                getServerList () {
        return serverList;
    }
    public javax.swing.JTextPane            getMessagPane() {
       return messagPane;
    }


    public String                       getMessage() {
        return message;
    }
    public void                         setMessage(String message) {
        this.message = message;
        if ( getMessagPane() != null ){
            if ( getMessage() != null){
                getMessagPane().setText(message);
            }
            else {
                getMessagPane().setText("");
            }
        }
    }




 
      // A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
}