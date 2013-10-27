/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author apple
 */
public class HyperLinkEmailLabel  extends HyperlinkView {
    String email;


    public HyperLinkEmailLabel(String text, String anemail) {
        super(text);
        this.email = anemail;


        addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
       super.mouseClicked(e);

        URI uriMailTo       = null;
        Desktop desktop     =   Desktop.getDesktop();
        try {
            if ( email != null && email.length() > 0) {
                uriMailTo = new URI("mailto", email, null);
                desktop.mail(uriMailTo);
            } else {
                desktop.mail();
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(URISyntaxException use) {
            use.printStackTrace();
        }
      }
     });
}

    public HyperLinkEmailLabel() {
        this("", null);
    }





}
