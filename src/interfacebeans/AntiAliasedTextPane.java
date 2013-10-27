/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.text.*;

 
import javax.swing.JTextPane;
 
public class AntiAliasedTextPane extends JTextPane {
    boolean lineWrap        =   true;
    private Color bgColor           =   Color.WHITE;
    public AntiAliasedTextPane (){
        super();
        setEditorKit(new WrapEditorKit());


        setOpaque(false);
         // this is needed if using Nimbus L&F - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6687960
        setBackground(new Color(0,0,0,0));


    }
    public AntiAliasedTextPane (javax.swing.text.StyledDocument document){
        super(document);
        setEditorKit(new WrapEditorKit());

        setOpaque(false);
       // this is needed if using Nimbus L&F - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6687960
        setBackground(new Color(0,0,0,0));
    }
     
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);



           g2.setColor(getNimbusBackroundColor());
            g2.fillRect(0, 0, getWidth(), getHeight());

            // uncomment the following to draw an image
            // Image img = ...;
            // g.drawImage(img, 0, 0, this);

        super.paintComponent(g2);
    }

    public Color getNimbusBackroundColor() {
        return bgColor;
    }
    public void setNimbusBackgroundColor(Color bgColor) {
        this.bgColor = bgColor;
    }
  class NoWrapParagraphView extends ParagraphView {
    public NoWrapParagraphView(Element elem) {
        super(elem);
    }

        @Override
    public void layout(int width, int height) {
        super.layout(Short.MAX_VALUE, height);
    }


        @Override
    public float getMinimumSpan(int axis) {
        return super.getPreferredSpan(axis);
    }
}
  class WrapColumnFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
             if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new NoWrapParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }
}
  class WrapEditorKit extends StyledEditorKit {
    ViewFactory defaultFactory=new WrapColumnFactory();

      @Override
      public ViewFactory getViewFactory() {
        return defaultFactory;
    }

}

}