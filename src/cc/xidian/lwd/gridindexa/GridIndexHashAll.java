package cc.xidian.lwd.gridindexa;

/**
 * Created by hadoop on 2015/5/25.
 */
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

public class GridIndexHashAll {
    public int xStartAll;//总范围起始点的横坐标
    public int yStartAll;//总范围起始点的纵坐标
    public int xEndAll;//总范围结束点的横坐标
    public int yEndAll;//总范围结束点的纵坐标
    public int dx;//每个网格的横向增量
    public int dy;//每个网格的纵向增量
    public int xNum;//网格索引的横向网格总数
    public int yNum;//网格索引的纵向网格总数
    public ArrayList<GridIndexHash> gIH;//网格索引数组
    public File fileGridIndex;//存储索引信息的文件
    public File fileGridPoint;//存储索引后的点的数据文件

    public GridIndexHashAll(){
        this.gIH = new ArrayList<GridIndexHash>();
//        if(this.fileGridPoint.exists()){
//            this.fileGridPoint.delete();
//        }
//        if(fileGridIndex.exists()){
//            this.fileGridIndex.delete();
//        }
    }
    public GridIndexHashAll(int xStartAll,int yStartAll,int xEndAll,int yEndAll,int dx,int dy){
        this.xStartAll = xStartAll;
        this.yStartAll = yStartAll;
        this.xEndAll = xEndAll;
        this.yEndAll = yEndAll;
        this.dx = dx;
        this.dy = dy;
        this.xNum = (xEndAll-xStartAll)/dx;//计算所得
        this.yNum = (yEndAll-yStartAll)/dy;//计算所得
        this.gIH = new ArrayList<GridIndexHash>();
    }
    public GridIndexHashAll(int xStartAll,int yStartAll,int xEndAll,int yEndAll,int dx,int dy,
                            String fileNameGridPoint){
        this.xStartAll = xStartAll;
        this.yStartAll = yStartAll;
        this.xEndAll = xEndAll;
        this.yEndAll = yEndAll;
        this.dx = dx;
        this.dy = dy;
        this.xNum = (xEndAll-xStartAll)/dx;//计算所得
        this.yNum = (yEndAll-yStartAll)/dy;//计算所得
        this.gIH = new ArrayList<GridIndexHash>();
        this.fileGridPoint = new File(fileNameGridPoint);
        if(fileGridPoint.exists()){
            fileGridPoint.delete();
        }
    }
    public GridIndexHashAll(int xStartAll,int yStartAll,int xEndAll,int yEndAll,int dx,int dy,
                            String fileNameGridIndex,String fileNameGridPoint){
        this.xStartAll = xStartAll;
        this.yStartAll = yStartAll;
        this.xEndAll = xEndAll;
        this.yEndAll = yEndAll;
        this.dx = dx;
        this.dy = dy;
        this.xNum = (xEndAll-xStartAll)/dx;//计算所得
        this.yNum = (yEndAll-yStartAll)/dy;//计算所得
        this.gIH = new ArrayList<GridIndexHash>();
        this.fileGridIndex = new File(fileNameGridIndex);
        if(fileGridIndex.exists()){
            fileGridIndex.delete();
        }
        this.fileGridPoint = new File(fileNameGridPoint);
        if(fileGridPoint.exists()){
            fileGridPoint.delete();
        }
    }
    public String toString(){
        return "#"+","+this.xStartAll+","+this.yStartAll+","+this.dx+","+this.dy+","
                +this.xNum+","+this.yNum+","+this.xEndAll+","+this.yEndAll+","+"#";
    }
    /**
     * 函数功能：将全局网格对象中网格索引的信息和每个网格对象中索引信息保存到索引信息文件中
     * @param gIHAll：全局索引对象
     * @throws Exception
     */
    public void writeFileGridIndex(GridIndexHashAll gIHAll) throws Exception{
        Iterator<GridIndexHash> itGridIndex = gIHAll.gIH.iterator();
        RandomAccessFile rGridIndex = new RandomAccessFile(gIHAll.fileGridIndex,"rw");
        String strGridAll = gIHAll.toString()+"\n";
        byte[] bGridAll = strGridAll.getBytes();
        rGridIndex.write(bGridAll);
        while(itGridIndex.hasNext()){
            GridIndexHash gIHOne = itGridIndex.next();
            int gID = gIHOne.xGrid*gIHAll.yNum+gIHOne.yGrid;
            String strGrid = Integer.toString(gID)+","+gIHOne.toString()+"\n";
            byte[] bGrid = strGrid.getBytes();
            rGridIndex.write(bGrid);
        }
        rGridIndex.close();
    }
    /**
     * 函数功能：传入全局索引对象，将每个网格中的点数组中的点写入到全局网格数据文件中
     * @param gIHAll：全局索引对象
     * @throws Exception
     */
    public void writeFileGridPoint(GridIndexHashAll gIHAll)throws Exception{
        RandomAccessFile rGridPoint = new RandomAccessFile(gIHAll.fileGridPoint,"rw");//使用随机类进行文件写入操作
        Iterator<GridIndexHash> itGridPoint = gIHAll.gIH.iterator();//获取网格索引数组的迭代器
        long pGridStart = rGridPoint.getFilePointer();//获取网格中的点写入前的偏移位置
        while(itGridPoint.hasNext()){
            GridIndexHash gIHash = itGridPoint.next();//获取每一个网格对象
            Iterator<Point> itPoint = gIHash.pAL.iterator();//获取每个网格对象中点数组的迭代器
            //long pGridStart = rGridPoint.getFilePointer();//获取网格中的点写入前的偏移位置
            long pGridEnd = rGridPoint.getFilePointer();//获取网格中的点写入后的偏移位置
            Point p = new Point();
            while(itPoint.hasNext()) {
                p = itPoint.next();
                String strPoint = p.toString() + "\n";
                byte[] bPoint = strPoint.getBytes();
                rGridPoint.write(bPoint);//将每个网格对象中点数组中的所有点写入全局网格数据文件中
            }
            gIHash.bSize = pGridEnd - pGridStart;//计算每个网格中点数组中的点在全局网格数据文件中所占的字节数
        }
        rGridPoint.close();
    }


}

