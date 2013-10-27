/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;
import java.awt.*;
import java.awt.geom.*;
import ij.IJ;
import ij.process.*;;
/**
 *
 * @author apple
 */
public class PolygonRoi extends Roi {

    protected int maxPoints                             =   1000; // will be increased if necessary
    protected int[] xPoints                             =   null ; //image coordinates
    protected int[] yPoints                             =   null  ;//image coordinates
    protected int nPoints                               =   0;
    protected float[] xSpline,ySpline; // relative image coordinates
    protected int splinePoints                          =   200;
    Rectangle clip;

    private boolean userCreated;

    long mouseUpTime = 0;



    /** Starts the process of creating a new user-generated polygon or polyline ROI. */
    public PolygonRoi(int sx, int sy, Component image) {
        super(sx, sy, image); // sets x,ym state = contsructing,width =0, height = 0 and so on
        type                =   POLYGON;
        xPoints             =   new int[maxPoints];
        yPoints             =   new int[maxPoints];
        nPoints             =   2;
        width               =   1;
        height              =   1;
        userCreated         =   true;
    }

    private void drawStartBox(Graphics g) {
            g.drawRect(startX-4, startY-4, 8, 8);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(ROIColor);
        if (xSpline!=null) {
            if (type==POLYLINE || type==FREELINE) {
                    drawSpline(g, xSpline, ySpline, splinePoints, false);
            }
            else{
                    drawSpline(g, xSpline, ySpline, splinePoints, true);
            }
                  
        } else {
            if (type==POLYLINE || type==FREELINE || type==ANGLE || state==CONSTRUCTING) {
                g.drawPolyline(xPoints, yPoints, nPoints);
            } else{
                g.drawPolygon(xPoints, yPoints, nPoints);
            }
                
            if (state==CONSTRUCTING && type!=FREEROI && type!=FREELINE){
                drawStartBox(g);
            }
                
        }

        if ((xSpline!=null||type==POLYGON||type==POLYLINE||type==ANGLE)
        && state!=CONSTRUCTING ) {
            int size2 = HANDLE_SIZE/2;
            if (activeHandle>0){
                 drawHandle(g, xPoints[activeHandle-1]-size2, yPoints[activeHandle-1]-size2);
            }
               
            if (activeHandle<nPoints-1){
                 drawHandle(g, xPoints[activeHandle+1]-size2, yPoints[activeHandle+1]-size2);
            }


            drawHandle(g,  xPoints[0]-size2,  yPoints[0]-size2, ROIColor);
            for (int i=1; i<nPoints; i++){
                 drawHandle(g, xPoints[i]-size2, yPoints[i]-size2);
            }
               
        }
    }

    private void drawSpline(Graphics g, float[] xpoints, float[] ypoints, int npoints, boolean closed) {
        if (ic==null) { return;}
        float xf            =   x;
        float yf            =   y;
        Graphics2D g2d      =   (Graphics2D)g;
        GeneralPath path    =   new GeneralPath();

        path.moveTo(xpoints[0]+xf, ypoints[0]+yf);
        for (int i=0; i<npoints; i++){
             path.lineTo(xpoints[i]+xf, ypoints[i]+yf);
        }
               
        if (closed){
            path.lineTo(xpoints[0] + xf, ypoints[0]+yf);
        }
            
        g2d.draw(path);
    }

   
    @Override
    protected void grow(int sx, int sy) {
        // Overrides grow() in Roi class
    }
    @Override
    protected void handleMouseMove(int ox, int oy) {
        if (state ==CONSTRUCTING ){
             drawRubberBand(ox, oy);
        }

    }

    void drawRubberBand(int ox, int oy) {
        int x1              =   xPoints[nPoints-2]+x;
        int y1              =   yPoints[nPoints-2]+y;
        int x2              =   xPoints[nPoints-1]+x;
        int y2              =   yPoints[nPoints-1]+y;
        int xmin            =   9999;
        int ymin            =   9999;
        int xmax            =   0;
        int ymax            =   0;
        if (x1<xmin)  { xmin=x1;}
        if (x2<xmin)  { xmin=x2;}
        if (ox<xmin)  { xmin=ox;}
        if (x1>xmax)  { xmax=x1;}
        if (x2>xmax)  { xmax=x2;}
        if (ox>xmax)  { xmax=ox;}
        if (y1<ymin)  { ymin=y1;}
        if (y2<ymin)  { ymin=y2;}
        if (oy<ymin)  { ymin=oy;}
        if (y1>ymax)  { ymax=y1;}
        if (y2>ymax)  { ymax=y2;}
        if (oy>ymax)  { ymax=oy;}

      
        xPoints[nPoints-1] = ox;
        yPoints[nPoints-1] = oy;
        ic.repaint();
    }

    void finishPolygon() {
        Polygon poly        =   new Polygon(xPoints, yPoints, nPoints);
        Rectangle r         =   poly.getBounds();
        x                   =   r.x;
        y                   =   r.y;
        width               =   r.width;
        height              =   r.height;
      
      
        state               = NORMAL;
        repaintROI(ic);
        oldX                =   x;
        oldY                =   y;
        oldWidth            =   width;
        oldHeight           =   height;
    }

    @Override
    protected void moveHandle(int sx, int sy) {
        xPoints[activeHandle] = sx;
        yPoints[activeHandle] = sy;
        if (xSpline!=null) {
       
            oldX            =    x;
            oldY            =    y;
            oldWidth        =    width;
            oldHeight       =   height;
        } else {
           resetBoundingRect();
        }
        ic.repaint();
    }
    @Override
    protected void handleMouseUp(int sx, int sy) {
        if (state==MOVING)
            {state      = NORMAL; return;}
        if (state==MOVING_HANDLE) {
            state       =   NORMAL;
            cachedMask  =   null; //mask is no longer valid
            oldX        =   x;
            oldY        =   y;
            oldWidth    =   width;
            oldHeight   =   height;
            return;
        }
        if (state ==CONSTRUCTING) {
            boolean samePoint               = (xPoints[nPoints-2]==xPoints[nPoints-1] && yPoints[nPoints-2]==yPoints[nPoints-1]);
            Rectangle biggerStartBox        = new Rectangle(startX -5, startY-5, 10, 10);


            if (nPoints>2 && (biggerStartBox.contains(sx, sy)
            || (sx ==startX &&  sy ==startY)
            || (samePoint && (System.currentTimeMillis()-mouseUpTime)<=500))) {

                nPoints--;
                //addOffset();
                finishPolygon();
                return;
            } else if (!samePoint) {
                mouseUpTime = System.currentTimeMillis();

                //add point to polygon

                if (nPoints==xPoints.length){
                     enlargeArrays();
                }
                xPoints[nPoints] = xPoints[nPoints-1];
                yPoints[nPoints] = yPoints[nPoints-1];

                nPoints++;

            }

            else if (nPoints == 2) {
                mouseUpTime = System.currentTimeMillis();

                //add point to polygon
                xPoints[0]  = startX;
                yPoints[0]  =  startY;


            }

      
       }// if constructing ends
    }
    @Override
    public boolean contains(int x, int y) {
        if (!super.contains(x, y)){
            return   false;
        }

        Polygon poly = new Polygon(xPoints, yPoints, nPoints);
        return poly.contains(x, y);
    }
    //* Returns a handle number if the specified screen coordinates are
   //     inside or near a handle, otherwise returns -1.
    @Override
    public int isHandle(int sx, int sy) {
        if (!(xSpline!=null||type==POLYGON||type==POLYLINE||type==ANGLE||type==POINT))
        { return -1;}
        int size = HANDLE_SIZE+5;
        int halfSize = size/2;
        int handle = -1;
        int sx2, sy2;
        for (int i=0; i<nPoints; i++) {
            sx2         =   xPoints[i]  -   halfSize;
            sy2         =   yPoints[i]  -   halfSize;
            if (sx >= sx2 && sx <= sx2 + size && sy >= sy2 && sy <= sy2 + size) {
                handle = i;
                break;
            }
        }
        return handle;
    }
   @Override
    public Polygon getPolygon() {
        int n;
        int[] xpoints1, ypoints1;
        if (xSpline!=null) {
            n = splinePoints;
            xpoints1 = toInt(xSpline);
            ypoints1 = toInt(ySpline);
        } else {
            n = nPoints;
            xpoints1 = xPoints;
            ypoints1 = yPoints;
        }
    
        return new Polygon(xpoints1, ypoints1, n);
    }
    @Override
    protected void move(int xNew, int yNew) {
        int deltax           =  xNew  - startX;
        int delaty           =  yNew - startY;


        addOffset(deltax, delaty);
    
        startX = xNew;
        startY = yNew;

        ic.repaint();
        resetBoundingRect();
        
        oldX        =   x;
        oldY        =   y;
        oldWidth    =   width;
        oldHeight   =   height;
    }

     @Override
     public ImageProcessor getMask() {
         if (cachedMask!=null && cachedMask.getPixels()!=null){
            return cachedMask;
         }


        PolygonFiller pf = new PolygonFiller();
        if (xSpline!=null){
             pf.setPolygon(toInt(xSpline), toInt(ySpline), splinePoints);
        }
        else{
             pf.setPolygon(xPoints, yPoints, nPoints);
        }

        cachedMask = pf.getMask(width, height);
        return cachedMask;
    }



    void resetBoundingRect() {
        int xmin            =   Integer.MAX_VALUE;
        int xmax            =   -xmin;
        int ymin            =   xmin;
        int ymax            =   xmax;
        int xx, yy;
        for(int i=0; i<nPoints; i++) {
            xx = xPoints[i];
            if (xx<xmin)  { xmin=xx;}
            if (xx>xmax)  { xmax=xx;}
            yy = yPoints[i];
            if (yy<ymin)  {ymin=yy;}
            if (yy>ymax)  {ymax=yy;}
        }
   
        x           =  xmin;
        y           =  ymin;
        width       =   xmax    -   xmin;
        height      =   ymax    -   ymin;
    }


  

    protected void mouseDownInHandle(int handle, int sx, int sy) {
        if (state==CONSTRUCTING) { return;}
           
        if (IJ.altKeyDown() && !(nPoints<=3 && type!=POINT)) {
            deleteHandle(sx, sy);
            return;
        } else if (IJ.shiftKeyDown() && type!=POINT) {
            addHandle(sx, sy);
            return;
        }
        state               =   MOVING_HANDLE;
        activeHandle        =   handle;
      
    }

    void deleteHandle(int ox, int oy) {
        if (ic==null)  { return;}
        if (nPoints<=1) {return;}
           
        boolean splineFit   =  (xSpline != null);
        xSpline             =   null;
        Polygon points      =   getPolygon();
        modState            =   NO_MODS;

        int pointToDelete   =   getClosestPoint(ox, oy, points);
        Polygon points2     =   new Polygon();
        for (int i=0; i<points.npoints; i++) {
            if (i!=pointToDelete){
                 points2.addPoint(points.xpoints[i], points.ypoints[i]);
            }
               
        }
     
        //imp.setRoi(new PolygonRoi(points2, type));
        if (splineFit){
           // ((PolygonRoi)imp.getRoi()).fitSpline(splinePoints);
        }
                
    }

    void addHandle(int ox, int oy) {
        if (ic ==null) { return;}
        boolean splineFit = xSpline != null;
        xSpline                 =   null;
        Polygon points          =   getPolygon();
        int n                   =   points.npoints;
        modState                =    NO_MODS;

        int pointToDuplicate    =  getClosestPoint(ox, oy, points);
        Polygon points2 = new Polygon();
        for (int i2=0; i2<n; i2++) {
            if (i2==pointToDuplicate) {
                int i1 = i2-1;
                if (i1==-1)  { i1 = isLine()?i2:n-1;}
                int i3 = i2+1;
                if (i3==n)   { i3 = isLine()?i2:0;  }
                int x1 = points.xpoints[i1]  + 2*(points.xpoints[i2] - points.xpoints[i1])/3;
                int y1 = points.ypoints[i1] + 2*(points.ypoints[i2] - points.ypoints[i1])/3;
                int x2 = points.xpoints[i2] + (points.xpoints[i3] - points.xpoints[i2])/3;
                int y2 = points.ypoints[i2] + (points.ypoints[i3] - points.ypoints[i2])/3;
                points2.addPoint(x1, y1);
                points2.addPoint(x2, y2);
            } 
            else{
                points2.addPoint(points.xpoints[i2], points.ypoints[i2]);
            }
        }
        if (type==POINT){
            //imp.setRoi(new PointRoi(points2.xpoints, points2.ypoints, points2.npoints));
        }
        else {
            if (splineFit){
                 // ((PolygonRoi)imp.getRoi()).fitSpline(splinePoints);
            }
              
        }
    }

    int getClosestPoint(int x, int y, Polygon points) {
        int index = 0;
        double distance = Double.MAX_VALUE;
        for (int i=0; i<points.npoints; i++) {
            double dx = points.xpoints[i] - x;
            double dy = points.ypoints[i] - y;
            double distance2 = Math.sqrt(dx*dx+dy*dy);
            if (distance2<distance) {
                distance = distance2;
                index = i;
            }
        }
        return index;
    }

    protected void addOffset(int offsetX, int offsetY) {
        for (int i=0; i<nPoints; i++) {
            xPoints[i] = xPoints[i]+offsetX;
            yPoints[i] = yPoints[i]+offsetY;
        }
    }

    // Returns the number of XY coordinates.
    public int getNCoordinates() {
        if (xSpline!=null)
            return splinePoints;
        else
            return nPoints;
    }

    // Returns this ROI's X-coordinates, which are relative
    //    to origin of the bounding box.
    public int[] getXCoordinates() {
        if (xSpline!=null){
                  return toInt( xSpline);
        }
          
        else{
             return xPoints;
        }
           
    }

    // Returns this ROI's Y-coordinates, which are relative
    //    to origin of the bounding box.
    public int[] getYCoordinates() {
        if (xSpline!=null){
            return toInt(ySpline);
        }
            
        else{
             return yPoints;
        }
           
    }

    // Returns this PolygonRoi as a Polygon.
  
   
  


    void enlargeArrays() {
        int[] xp2temp       =   new int[maxPoints*2];
        int[] yp2temp       =   new int[maxPoints*2];

        System.arraycopy(xPoints, 0, xp2temp, 0, maxPoints);
        System.arraycopy(yPoints, 0, yp2temp, 0, maxPoints);
        
        xPoints                 =   xp2temp;
        yPoints                 =   yp2temp;
        maxPoints           *=  2;
    }

    private int[] toInt(float[] arr) {
        int n = arr.length;
        int[] temp = new int[n];
        for (int i=0; i<n; i++){
             temp[i] = (int)Math.floor(arr[i]+0.5);
        }
           
        return temp;
    }

    private float[] toFloat(int[] arr) {
        int n           =   arr.length;
        float[] temp    =   new float[n];
        for (int i=0; i<n; i++){
                 temp[i] = arr[i];
        }
        
        return temp;
    }
   //*/
}
