/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fid;

/**
 *
 * @author apple
 */
public class FidModelNumbers implements java.io.Serializable{

    protected int by            =   1;
    protected int from          =   1 ;
    protected int to            =   1;
    protected boolean loaded    =   false;



    public boolean isLoaded () {
        return loaded;
    }
    public void setLoaded ( boolean loaded ) {
        this.loaded = loaded;
    }

    public int getTo () {
        return to;
    }
    public void setTo ( int to ) {
        this.to = to;
    }

    public int getFrom () {
        return from;
    }
    public void setFrom ( int from ) {
        this.from = from;
    }

    public int getBy () {
        return by;
    }
    public void setBy ( int by ) {
        this.by = by;
    }

}
