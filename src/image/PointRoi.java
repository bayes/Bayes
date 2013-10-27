/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;
import java.io.*;
import java.text.*;
import java.util.*;
import ij.process.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

/** This class represents a collection of points. */
public class PointRoi extends Roi {
    protected int maxPoints = 1; // will be increased if necessary
    protected int nPoints;
    protected float[] xSpline,ySpline; // relative image coordinates
    protected int splinePoints = 200;
    static Font font;
    
    /** Creates a new PointRoi using the specified arrays of offscreen coordinates. */
    public PointRoi(int ox, int oy, Component image) {
             super(0, 0, null);
             x          =   ox;
             y          =   oy;
             ic         =   image;
             type       =   POINT; 
             state      =   NORMAL ;
             width      +=  1; 
             height     +=  1;
        }


    @Override
    protected  void handleMouseMove(int ox, int oy) {
    }
    @Override
    protected void handleMouseUp(int sx, int sy) {
       
    }
    @Override
    protected void handleMouseDrag(int sx, int sy) {
      // do  nothing
    }
     @Override
    protected void handleMouseDown(int sx, int sy) {
        if (state==NORMAL ) {
            state   = MOVING;
            startX  = sx;
            startY  = sy;
        }
         ic.repaint();

    }
     @Override
     public int     isHandle(int sx, int sy) {
        return -1;
    }

    /** Draws the points on the image. */


   @Override
    public void draw(Graphics g) {
       int size2 = HANDLE_SIZE/2;
         drawPoint(g, x-size2, y-size2);
     
    }

    void drawPoint(Graphics g, int x, int y) {
        g.setColor(Color.white);
        g.drawLine(x-4, y+2, x+8, y+2);
        g.drawLine(x+2, y-4, x+2, y+8);
        g.setColor(ROIColor);
        g.fillRect(x+1,y+1,3,3);
      
        g.setColor(Color.black);
        g.drawRect(x, y, 4, 4);
    }


 
 

    /** Returns true if (x,y) is one of the points in this collection. */
    @Override
    public boolean contains(int x, int y) {
            if (x==this.x && y==this.y) 
            { return true;
            }
            
            return false;
    }

}