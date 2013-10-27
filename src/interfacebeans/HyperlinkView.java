/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;




public class HyperlinkView extends JLabel {
Font srcFont;
public final Color in   = Color.BLUE;
public final Color out  = Color.BLACK;

public HyperlinkView() {
 this("");
}

public HyperlinkView(String text) {
 super(text);



    setForeground(out);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    //srcFont = HyperlinkView.this.getFont();
    //Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
    //attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    //setFont(srcFont.deriveFont(attributes));
    setInactive();
    addMouseListener(new MouseAdapter() {



        @Override
  public void mouseEntered(MouseEvent e) {
        HyperlinkView.this.setActive();

  }

  @Override
  public void mouseExited(MouseEvent e) {
    HyperlinkView.this.setInactive();
  }

 });

}

     void setInactive(){
          String txt   = String.format("<html>" +
             "<font  color=\"black\" >"+
             "<a href=\"\">%s</a>"+
             "</font>"
             ,
            getText());
            setText(txt);
     }
     void setActive(){
          String txt   = String.format("<html>" +
             "<font color=\"blue\" >"+
             "<a href=\"\">%s</a>"+
             "</font>"
             ,
            getText());
            setText(txt);
     }

}