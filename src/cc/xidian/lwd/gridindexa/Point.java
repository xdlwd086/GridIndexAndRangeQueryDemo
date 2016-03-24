package cc.xidian.lwd.gridindexa;

/**
 * Created by hadoop on 2015/5/25.
 */
public class Point {
    public double x;
    public double y;

    public Point(){}
    public Point(double x,double y){
        this.x = x;
        this.y = y;
    }
    public String toString(){
        return this.x+","+this.y;
    }
}
