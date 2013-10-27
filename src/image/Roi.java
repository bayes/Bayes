/*
 * Roi.java
 *
 * Created on November 19, 2007, 11:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package image;
import ij.process.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author apple
 */
public class Roi implements Cloneable{
    public static final int CONSTRUCTING    =   0, 
                            MOVING          =   1,
                            RESIZING        =   2,
                            NORMAL          =   3,
                            MOVING_HANDLE   =   4; // States
    public static final int RECTANGLE       =   0,
                            OVAL            =   1,
                            POLYGON         =   2,
                            FREEROI         =   3,
                            TRACED_ROI      =   4,
                            LINE            =   5, 
                            POLYLINE        =   6,
                            FREELINE        =   7,
                            ANGLE           =   8,
                            COMPOSITE       =   9,
                            POINT           =   10; // Types
    public static final int HANDLE_SIZE     =   5; 
    static final int        NO_MODS         =   0,
                            ADD_TO_ROI      =   1,
                            SUBTRACT_FROM_ROI=  2; // modification states    /** Creates a new instance of Roi */
    
    int startX, startY, x, y, width, height;
    int activeHandle;
    int state;
    int modState =                       NO_MODS;

    protected ImageProcessor cachedMask     =   null;// need this for statsitics calculation for non-rectangular ROI
    protected int type;
    private int xMax                        = Integer.MAX_VALUE ;
    private int yMax                        = Integer.MAX_VALUE;
    protected int oldX, oldY, oldWidth, oldHeight;
    protected String name;
    protected static Color handleColor      = Color.WHITE;
    protected static Color ROIColor         = Color.YELLOW;
    protected static int lineWidth          = 1;
    protected Component ic;
 
    public Roi(int x, int y, Component image) {
        
        setLocation(x, y);
        setImage(image);
        width   =   0;
        height  =   0;
        state   =   CONSTRUCTING;
        type    =   RECTANGLE;
    }

   

    public void draw(Graphics g) {
        
        int sx1 = x;
        int sy1 = y;
        int sx2 = sx1   +   width/2;
        int sy2 = sy1   +   height/2;
        int sx3 = sx1   +   width;
        int sy3 = sy1   +   height;
        
        g.setColor ( ROIColor);
      
        g.drawRect(sx1, sy1, width, height);
        if (state!=CONSTRUCTING) {
            int size2 = HANDLE_SIZE/2;
            drawHandle(g, sx1-size2, sy1-size2);
            drawHandle(g, sx2-size2, sy1-size2);
            drawHandle(g, sx3-size2, sy1-size2);
            drawHandle(g, sx3-size2, sy2-size2);
            drawHandle(g, sx3-size2, sy3-size2);
            drawHandle(g, sx2-size2, sy3-size2);
            drawHandle(g, sx1-size2, sy3-size2);
            drawHandle(g, sx1-size2, sy2-size2);
        }
       }
    protected void grow(int sx, int sy) {
        int xNew = sx;
        int yNew = sy;
        if (type    ==  RECTANGLE) {
            if (xNew < 0) { xNew = 0;}
            if (yNew < 0) { yNew = 0;}
        }
        
        width   = Math.abs(xNew - startX);
        height  = Math.abs(yNew - startY);
        x       = (xNew >= startX) ? startX : startX - width;
        y       = (yNew >= startY) ? startY : startY - height;
        
        if (type == RECTANGLE) {
            if ((x + width)  > getXMax()) { width  = getXMax()  -   x;}
            if ((y + height) > getYMax()) { height = getYMax()  -   y;}
        }
        
        repaintROI(ic);
        oldX        = x;
        oldY        = y;
        oldWidth    = width;
        oldHeight   = height;
    }
    
    protected void moveHandle(int sx, int sy) {
        int ox = sx;
        int oy = sy;
        if (ox  <  0)   { ox  =   0;    }
        if (oy  <  0)   { oy  =   0;    }
        if (ox  > getXMax()) { ox  =   getXMax(); }
        if (oy  > getYMax()) { oy  =   getYMax(); }

        int x1  =   x;
        int y1  =   y;
        int x2  =   x1  +   width;
        int y2  =   y   +   height;
        switch (activeHandle) {
            case 0: x   =   ox; y   =   oy; break;
            case 1: y   =   oy; break;
            case 2: x2  =   ox; y   =   oy; break;
            case 3: x2  =   ox; break;           
            case 4: x2  =   ox; y2  =   oy; break;
            case 5: y2  =   oy; break;
            case 6: x   =   ox; y2  =   oy; break;
            case 7: x   =   ox; break;
        }
        if (x   <   x2){
           width    =   x2  -   x;
        }
        else{
           width    =   1; x    =   x2;
        }
        
        if (    y   <  y2){
           height   =   y2  -   y;
        }
        else{
            height  =   1; y    =   y2;
        }
        
        
        repaintROI(ic);  
        oldX        = x;
        oldY        = y;
        oldWidth    = width;
        oldHeight   = height;
    }
    protected void move(int sx, int sy) {
        int xNew     =  sx;
        int yNew     =  sy;
        x           +=  xNew - startX;
        y           +=  yNew - startY;
        
        if ( type==RECTANGLE) {
            if (x < 0) { x = 0; }
            if (y < 0) {y = 0;}
            if ((x + width ) > getXMax()) {x = getXMax()-width;}
            if ((y + height) > getYMax()){ y = getYMax()-height;}
        }
        startX = xNew;
        startY = yNew;
        
        repaintROI(ic);
        
        oldX        =   x;
        oldY        =   y;
        oldWidth    =   width;
        oldHeight   =   height;
    }
    protected void handleMouseDrag(int sx, int sy) {
        if (ic==null)  { return;}

        switch(state) {
            case CONSTRUCTING:
                grow(sx, sy);
                break;
            case MOVING:
                move(sx, sy);
                break;
            case MOVING_HANDLE:
                moveHandle(sx, sy);
                break;
            default:
                break;
        }
        
    }   
   //   Returns a handle number if the specified screen coordinates are  
   //   inside or near a handle, otherwise returns -1. */
    public int     isHandle(int sx, int sy) {
        //double mag = ic.getMagnification();
        int size        = HANDLE_SIZE   +   3;
        int halfSize    = size/2;

        //Point p = MouseInfo.getPointerInfo().getLocation ();
        //SwingUtilities.convertPointFromScreen(p, ic) ;
        //int sx = p.x;
       // int sy = p.y;
        
        int sx1 = x  -   halfSize;
        int sy1 = y  -   halfSize;
        int sx3 = x  +   width  - halfSize;
        int sy3 = y  +   height - halfSize;
        int sx2 = sx1 + (sx3 - sx1)/2;
        int sy2 = sy1 + (sy3 - sy1)/2;
        
        if (sx>=sx1&&sx<=sx1+size&&sy>=sy1&&sy<=sy1+size) return 0; 
        if (sx>=sx2&&sx<=sx2+size&&sy>=sy1&&sy<=sy1+size) return 1;
        if (sx>=sx3&&sx<=sx3+size&&sy>=sy1&&sy<=sy1+size) return 2;
        if (sx>=sx3&&sx<=sx3+size&&sy>=sy2&&sy<=sy2+size) return 3;
        if (sx>=sx3&&sx<=sx3+size&&sy>=sy3&&sy<=sy3+size) return 4;
        if (sx>=sx2&&sx<=sx2+size&&sy>=sy3&&sy<=sy3+size) return 5;
        if (sx>=sx1&&sx<=sx1+size&&sy>=sy3&&sy<=sy3+size) return 6;
        if (sx>=sx1&&sx<=sx1+size&&sy>=sy2&&sy<=sy2+size) return 7;
        return -1;
    }
    protected void mouseDownInHandle(int handle) {
        state = MOVING_HANDLE;
        activeHandle = handle;
    }
    protected void handleMouseDown(int sx, int sy) {
        if (state==NORMAL ) {
            state   = MOVING;
            startX  = sx;
            startY  = sy;
        }
    }
    protected void handleMouseUp(int screenX, int screenY) {
         state = NORMAL;
         repaintROI(ic);
    }
    protected void handleMouseMove(int screenX, int screenY){
      //DO NOTHING
    }
    protected void   drawHandle(Graphics g, int x, int y) {
            drawHandle(g, x, y,handleColor ) ;
    }
    protected void   drawHandle(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x+1,y+1,3,3);
    }
    
    

    

    protected void repaintROI (Component comp){
        int repaintx   = (x<=oldX)?x:oldX;
        int repainty   = (y<=oldY)?y:oldY;
        int repaintwidth = ((x+width>=oldX+oldWidth)?x+width:oldX+oldWidth)  + 1;
        int repaintheight = ((y+height>=oldY+oldHeight)?y+height:oldY+oldHeight) + 1;
        int m       = 2;
    
        
        repaintx       -=  m; 
        repainty       -=  m;
        repaintwidth   +=  2*m; 
        repaintheight  +=  2*m;
        comp.repaint( repaintx , repainty, repaintwidth, repaintheight);
     }

   protected void repaintROI (Component comp, int x1, int y1, int width, int height){
        comp.repaint(  x , y, width,height);
     }
   /****** getters and setters ******************/   
   public int getType()  { return type; }
   public int getState() { return state;}  
   public Rectangle getBounds() { return new Rectangle(x, y, width, height);}
   public Polygon getPolygon() {
        int[] xpoints = new int[4];
        int[] ypoints = new int[4];
        xpoints[0] = x;
        ypoints[0] = y;
        xpoints[1] = x  +   width;
        ypoints[1] = y;
        xpoints[2] = x  +   width;
        ypoints[2] = y  +   height;
        xpoints[3] = x;
        ypoints[3] = y  +   height;
        return new Polygon(xpoints, ypoints, 4);
    }   
   public String getName() {return name;}
   public String getTypeAsString() {
        String s="";
        switch(type) {
            case RECTANGLE: s="RECTANGLE"; break;
            case POINT: s="POINT"; break;
            default: s="Rectangle"; break;
        }
        return s;
    }
   public static Color getColor() { return ROIColor;}

   /** Returns true if this is a line selection. */
    public boolean isLine() {
        return type>=LINE && type<=FREELINE;
    }

    /** Returns true if this is an area selection. */
    public boolean isArea() {
        return (type>=RECTANGLE && type<=TRACED_ROI) || type==COMPOSITE;
    }

   /** Always returns null for rectangular Roi's */
    public ImageProcessor getMask() {
        return null;
    }
    
   
   public void setLocation(int x, int y) {
        this.x      = x;
        this.y      = y;
        startX      = x;
        startY      = y;
        oldX        = x;
        oldY        = y;
        oldWidth    = 0; 
        oldHeight   = 0;
    }
   public void setImage(Component image) {
        this.ic         =   image;
        cachedMask      =   null;
        if (ic  ==null) {
            ic = null;
            setXMax(99999);
            setYMax(99999);
        } 
        else {
            setXMax(ic.getWidth());
            setYMax(ic.getHeight());
        }
    }  
   public void setName(String name) {this.name = name;}
   public boolean contains(int x, int y) {
        Rectangle r = new Rectangle(this.x, this.y, width, height);
        return r.contains(x, y);
    }
   @Override
   public synchronized Object clone() {
        try { 
            Roi r = (Roi)super.clone();
            return r;
        }
        catch (CloneNotSupportedException e) {return null;}
    }  
   @Override
   public String toString() {
        return ("Roi["+getTypeAsString()+", x="+x+", y="+y+", width="+width+", height="+height+"]");
    }

    public int getXMax () {
        return xMax;
    }
    public void setXMax ( int xMax ) {
        this.xMax = xMax;
    }
    public int getYMax () {
        return yMax;
    }
    public void setYMax ( int yMax ) {
        this.yMax = yMax;
    }
 
}
