package cc.xidian.lwd.gridindexa;

import java.util.ArrayList;

public class GridIndexHash {
    public int xGrid;
    public int yGrid;
    public ArrayList<Point> pAL;
    public int pSize;
    public long bSize;

    public GridIndexHash(){
        this.xGrid = 0;
        this.yGrid = 0;
        this.pAL = new ArrayList<Point>();
        this.pSize = 0;
        this.bSize = 0;
    }
    public GridIndexHash(int xGrid,int yGrid){
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.pAL = new ArrayList<Point>();
        this.pSize = 0;
        this.bSize = 0;
    }
    public String toString(){
        return this.xGrid+","+this.yGrid+","+this.pAL.size()+","+this.bSize;
    }
}

