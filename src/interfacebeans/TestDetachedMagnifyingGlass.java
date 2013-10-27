/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package interfacebeans;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestDetachedMagnifyingGlass extends Object {

	public TestDetachedMagnifyingGlass(File f) {
		// image frame
		ImageIcon i = new ImageIcon (f.getPath());
		JLabel l = new JLabel (i);

		JFrame imgFrame = new JFrame ("Image");
		imgFrame.getContentPane().add(l);
		imgFrame.pack();
		imgFrame.setVisible(true);
		// magnifying glass frame
		JFrame magFrame = new JFrame ("Mag");
		DetachedMagnifyingGlass mag =
			new DetachedMagnifyingGlass (l, new Dimension (150, 150), 2.0);
	        magFrame.getContentPane().add (mag);
			magFrame.pack(); magFrame.setLocation (new Point (
             imgFrame.getLocation().x + imgFrame.getWidth(),
                imgFrame.getLocation().y));
			magFrame.setVisible(true);
			}

			public static void main (String[] args) {
				JFileChooser chooser = new JFileChooser();
				//chooser.showOpenDialog(null);
				//File f = chooser.getSelectedFile();
                                File f = new File ("/Users/apple/NetBeansProjects/Bayes/src/bayes/resources/icons/folder_icon.png");
				new TestDetachedMagnifyingGlass (f);
			}
		}