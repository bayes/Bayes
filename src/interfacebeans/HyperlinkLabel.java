/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import utilities.BrowserLaunch;

public class HyperlinkLabel extends HyperlinkView {
private String url;

public HyperlinkLabel(String text, String aurl) {
 super(text);
 this.url = aurl;


 addMouseListener(new MouseAdapter() {
  @Override
  public void mouseClicked(MouseEvent e) {
   super.mouseClicked(e);
     BrowserLaunch.openURL(url);
  }
 });
}

public HyperlinkLabel() {
 this("", null);
}

public String getUrl() {
 return url == null ? "" : url;
}
public void setUrl(String url) {
 this.url = url;
}


    public static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame(" Bayes Analyze ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.add(new HyperlinkLabel("aha", "http://bayes.wustl.edu"));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });

    }

}