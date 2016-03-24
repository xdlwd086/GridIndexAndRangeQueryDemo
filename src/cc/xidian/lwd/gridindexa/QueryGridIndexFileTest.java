package cc.xidian.lwd.gridindexa;

/**
 * Created by hadoop on 2015/5/25.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class QueryGridIndexFileTest {

    public static void main(String[] args) throws Exception{
		/*=========================建立输入文件的路径，并创建文件对象，用于后面的查询中===========================*/
		/*======================三个测试文件的路径==========================*/
        String filePatha = "TestPointData.txt";//1000个点的测试文件路径
        String filePathb = "TestPointDataMiddle.txt";//5000个点的测试文件路径
        String filePathc = "AllPointDataTest.txt";//178万2426个点的测试文件路径
        File fileAll = new File(filePathc);//创建文件对象，用于后面的查询函数中
		/*====================================================================================*/

		/*=======================给出建立网格索引的条件==========================*/
        String fileGridIndexName = "PointGridIndexFile.txt";
        String fileGridPointName = "PointGridIndexDataFile.txt";
        File fileGridIndex = new File(fileGridIndexName);
        File fileGridPoint = new File(fileGridPointName);
		/*=============================================================*/

        GridIndexHashAll gIHAll = new GridIndexHashAll();//获取全局网格索引对象
        long tIndexCreateStart = System.currentTimeMillis();
        gIHAll = createGridIndexHashAllFromFile(fileGridIndex,fileGridPoint);
        long tIndexCreateEnd = System.currentTimeMillis();
		/*===================给出范围查询的条件并保存为两个查询点=====================*/
        double xQueryStart = 0.23;
        double yQueryStart = 0.17;
        double xQueryEnd = 50.21;
        double yQueryEnd = 30.54;
        Point pQueryStart = new Point(xQueryStart,yQueryStart);//查询范围的起始点
        Point pQueryEnd = new Point(xQueryEnd,yQueryEnd);//查询范围的结束点
		/*==============================================================*/

		/*=================================在没有网格索引的前提下，求出查询范围中所有的点=========================*/
        ArrayList<Point> pQueryResultList = new ArrayList<Point>();
        long tNoIndexQueryStart = System.currentTimeMillis();
        pQueryResultList = noIndexPointFileQuery(fileAll,pQueryStart,pQueryEnd);//进行无索引的点查询
        long tNoIndexQueryEnd = System.currentTimeMillis();
		/*=================输出查询范围中所有的点====================*/
        //Iterator<Point> ipQRL = pQueryResultList.iterator();
        //while(ipQRL.hasNext()){
        //Point p = ipQRL.next();
        //System.out.println(p);
        //}
        System.out.println("查询范围是："+pQueryStart.toString()+","+pQueryEnd.toString());
        System.out.println("网格的横向和纵向增量分别是："+gIHAll.dx+" 和  "+gIHAll.dy);
        System.out.print("普通方式查询结果中点的总数是：");
        System.out.println(pQueryResultList.size());
        System.out.print("在没有建立索引的情况下，范围查询的时间：");
        System.out.println(tNoIndexQueryEnd-tNoIndexQueryStart);
		/*======================================================================================*/

		/*=================================在以哈希的方式建立网格索引的前提下，求出查询范围中所有的点===================*/
        ArrayList<Point> pQueryResultHash = new ArrayList<Point>();
        long tIndexQueryStart = System.currentTimeMillis();
        pQueryResultHash = gridIndexHashPointFileQuery(gIHAll,pQueryStart,pQueryEnd);//进行有索引的点查询
        long tIndexQueryEnd = System.currentTimeMillis();
		/*=================输出查询范围中所有的点===================*/
        //Iterator<Point> ipHQR = pQueryResultHash.iterator();
        //while(ipHQR.hasNext()){
        //Point p = ipHQR.next();
        //System.out.println(p);
        //}
		/*===================打印相关的信息=====================*/
        System.out.print("哈希索引下，查询结果中点的总数是：");
        System.out.println(pQueryResultHash.size());
        System.out.print("在以哈希的方式建立建立索引的情况下，索引信息建立的时间：");
        System.out.println(tIndexCreateEnd-tIndexCreateStart);
        System.out.print("在以哈希的方式建立建立索引的情况下，范围查询的时间：");
        System.out.println(tIndexQueryEnd-tIndexQueryStart);
		/*======================================================================================*/
    }

    /**
     * 函数功能：在读取点数据文件的同时，不建立任何索引，直接对每个点进行判断，找出符合范围查询条件的点
     * @param fileAll：点数据文件，保存着所有点的坐标
     * @param pQueryStart：范围查询的起始点
     * @param pQueryEnd：范围查询的结束点
     * @return：该函数返回的是符合范围查询条件的结果数组
     * @throws Exception
     */
    public static ArrayList<Point> noIndexPointFileQuery(File fileAll,
                                                         Point pQueryStart,Point pQueryEnd) throws Exception{
        ArrayList<Point> pQueryResult = new ArrayList<Point>();
		/*=============在读取点数据文件的同时，不建立任何索引，直接对每个点进行判断，找出符合范围查询条件的点============*/
        if(fileAll.exists()&&fileAll.isFile()){
            InputStreamReader read = new InputStreamReader(new FileInputStream(fileAll));
            BufferedReader bReader = new BufferedReader(read);
            String str;
            String[] strPoint;
            Point p = new Point();
            while((str=bReader.readLine())!=null){
                strPoint = str.split(",");
                p.x = Double.parseDouble(strPoint[0]);
                p.y = Double.parseDouble(strPoint[1]);
                if(((p.x)>(pQueryStart.x))&&((p.x)<(pQueryEnd.x))
                        &&((p.y)>(pQueryStart.y))&&((p.y)<(pQueryEnd.y))){
                    pQueryResult.add(p);
                }
            }
            bReader.close();
        }
		/*=============================================================================*/
        return pQueryResult;//返回查询点集数组
    }
    /**
     * 函数功能：从网格信息文件和网格数据文件中读取信息，将相应的信息赋给全局索引对象的各个属性字段中；
     * @param fileGridIndex：全局索引信息文件对象
     * @param fileGridPoint：全局索引数据文件对象
     * @return 返回的是带有各个字段完整信息的全局索引对象
     * @throws Exception
     */
    public static GridIndexHashAll createGridIndexHashAllFromFile(File fileGridIndex,File fileGridPoint) throws Exception{
        GridIndexHashAll gIHAll = new GridIndexHashAll();//创建全局索引对象
        gIHAll.fileGridIndex = fileGridIndex;
        gIHAll.fileGridPoint = fileGridPoint;

        InputStreamReader read = new InputStreamReader(new FileInputStream(fileGridIndex));
        BufferedReader rGridIndex = new BufferedReader(read);
        String strGridAll = rGridIndex.readLine();
        String[] strGridAllArrays = strGridAll.split(",");
        gIHAll.xStartAll = Integer.parseInt(strGridAllArrays[1]);
        gIHAll.yStartAll = Integer.parseInt(strGridAllArrays[2]);
        gIHAll.dx = Integer.parseInt(strGridAllArrays[3]);
        gIHAll.dy = Integer.parseInt(strGridAllArrays[4]);
        gIHAll.xNum = Integer.parseInt(strGridAllArrays[5]);
        gIHAll.yNum = Integer.parseInt(strGridAllArrays[6]);
        gIHAll.xEndAll = Integer.parseInt(strGridAllArrays[7]);
        gIHAll.yEndAll = Integer.parseInt(strGridAllArrays[8]);

        String strGridOne;
        int gridSize = (gIHAll.xNum)*(gIHAll.yNum);
        //GridIndexHash gIHOne = new GridIndexHash();
        for(int i=0;i<gridSize;i++){
            strGridOne = rGridIndex.readLine();
            String[] strGridOneArrays = strGridOne.split(",");
            GridIndexHash gIHOne = new GridIndexHash(Integer.parseInt(strGridOneArrays[1]),
                    Integer.parseInt(strGridOneArrays[2]));
            //gIHOne.xGrid = Integer.parseInt(strGridOneArrays[1]);
            //gIHOne.yGrid = Integer.parseInt(strGridOneArrays[2]);
            gIHOne.pSize = Integer.parseInt(strGridOneArrays[3]);
            gIHOne.bSize = Long.parseLong(strGridOneArrays[4]);
            gIHAll.gIH.add(gIHOne);
        }
        rGridIndex.close();
        return gIHAll;
    }

    /**
     * 函数功能：对每个网格索引文件中的中的点进行判断，如果符合范围查询条件，则取出放入结果数组中
     * @param gIHAll：全局网格索引对象，保存的是全局索引有关的信息
     * @param xIH：每个网格索引的横向标识
     * @param yIH：每个网格索引的纵向标识
     * @param pQueryStart：范围查询的起始点
     * @param pQueryEnd：范围查询的结束点
     * @return：该函数返回的是符合范围查询条件的结果数组
     * @throws Exception
     */
    public static ArrayList<Point> oneGridPointFileQuery(GridIndexHashAll gIHAll,int xIH,int yIH,
                                                         Point pQueryStart,Point pQueryEnd) throws Exception{
        ArrayList<Point> pQueryResult = new ArrayList<Point>();
		/*=======获取对应网格的数据文件对象,将文件中的点取出，将符合查询条件的点放入结果数组*/
        GridIndexHash gIHash = gIHAll.gIH.get(xIH*(gIHAll.yNum)+yIH);
        GridIndexHash gIHashNext = gIHAll.gIH.get(xIH*(gIHAll.yNum)+yIH+1);
        RandomAccessFile rOneGrid = new RandomAccessFile(gIHAll.fileGridPoint,"rw");
        rOneGrid.seek(gIHash.bSize);//将文件指针调到要读取的文件开始处
        //long toneGridPointQueryStart = System.currentTimeMillis();
        /*=====根据网格中点的字节数，一次性取出网格中所有的点，并将符合查询条件的点放入结果数组中====*/
        byte[] bGridArray = new byte[(int)(gIHashNext.bSize-gIHash.bSize)];
        rOneGrid.read(bGridArray);
        String strOneGridAll = new String(bGridArray,"utf-8");
        String[] strPointArray = strOneGridAll.split("\n");
        Point p = new Point();
        int strLength = strPointArray.length;
        for(int i=0;i<strLength;i++){
            String[] strPoint = strPointArray[i].split(",");
            p.x = Double.parseDouble(strPoint[0]);
            p.y = Double.parseDouble(strPoint[1]);
            if(((p.x)>(pQueryStart.x))&&((p.x)<(pQueryEnd.x))
                    &&((p.y)>(pQueryStart.y))&&((p.y)<(pQueryEnd.y))){
                pQueryResult.add(p);
            }

        }
        rOneGrid.close();
        /*=============================================================================*/
        //long toneGridPointQueryEnd = System.currentTimeMillis();
        //System.out.println("每个网格的查询时间："+(toneGridPointQueryEnd-toneGridPointQueryStart));
        return pQueryResult;
    }
    /**
     * 函数功能：将该网格中的点不经过范围过滤，直接取出
     * @param gIHAll：全局网格索引对象，保存的是全局索引相关的信息
     * @param xIH：每个网格索引的横向标识
     * @param yIH：每个网格索引的纵向标识
     * @return：不经过范围查询过滤，直接范围网格中的所有点
     * @throws Exception
     */
    public static ArrayList<Point> directGetOneGridPoint(GridIndexHashAll gIHAll,int xIH,int yIH) throws Exception{
        ArrayList<Point> pQueryResult = new ArrayList<Point>();
		/*=======获取对应网格的数据文件对象,将文件中的点取出，将符合查询条件的点放入结果数组*/
        GridIndexHash gIHash = gIHAll.gIH.get(xIH*(gIHAll.yNum)+yIH);
        GridIndexHash gIHashNext = gIHAll.gIH.get(xIH*(gIHAll.yNum)+yIH+1);
        RandomAccessFile rOneGrid = new RandomAccessFile(gIHAll.fileGridPoint,"rw");
        rOneGrid.seek(gIHash.bSize);
        byte[] bGridArray = new byte[(int)(gIHashNext.bSize-gIHash.bSize)];
        rOneGrid.read(bGridArray);
        String strOneGridAll = new String(bGridArray,"utf-8");
        String[] strPointArray = strOneGridAll.split("\n");
        Point p = new Point();
        int strLength = strPointArray.length;
        for(int i=0;i<strLength;i++){
            String[] strPoint = strPointArray[i].split(",");
            p.x = Double.parseDouble(strPoint[0]);
            p.y = Double.parseDouble(strPoint[1]);
            pQueryResult.add(p);

        }
        rOneGrid.close();
        return pQueryResult;
    }
    /**
     * 函数功能：在建好的网格索引文件之上进行范围查询，返回符合查询条件的点，
     * @param gIHAll：全局网格索引对象，保存的是全局索引有关的信息
     * @param pQueryStart：范围查询的起始点
     * @param pQueryEnd：范围查询的结束点
     * @return：该函数返回的是符合范围查询条件的结果数组
     * @throws Exception
     */
    public static ArrayList<Point> gridIndexHashPointFileQuery(GridIndexHashAll gIHAll,
                                                               Point pQueryStart,Point pQueryEnd)throws Exception{
        ArrayList<Point> pQueryResult = new ArrayList<Point>();
		/*=======================获取查询起始点和结束点的网格标识=============================*/
        int xQueryIndexStart = (int)((pQueryStart.x-gIHAll.xStartAll)/(gIHAll.dx));
        int yQueryIndexStart = (int)((pQueryStart.y-gIHAll.yStartAll)/(gIHAll.dy));
        int xQueryIndexEnd = (int)((pQueryEnd.x-gIHAll.xStartAll)/(gIHAll.dx));
        int yQueryIndexEnd = (int)((pQueryEnd.y-gIHAll.yStartAll)/(gIHAll.dy));
		/*=======================================================================*/

		/*当查询范围中有完整的网格时，分为两种情况，首先对于与查询边界相交的网格，将这些网格中的点分别取出，然后返回符合查询条件的点，然后是对于完整的网格，直接返回其中所有的点*/
        if(((xQueryIndexEnd-xQueryIndexStart)>1)&&((yQueryIndexEnd-yQueryIndexStart)>1)){
			/*=================首先对于查询边界相交的网格进行处理，分为四种情况，用四个for循环分别进行处理================*/
			/*====================处理位于下面一行的网格中的点======================*/
            for(int x=xQueryIndexStart;x<=xQueryIndexEnd;x++){
                ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,x,yQueryIndexStart,pQueryStart,pQueryEnd);
                pQueryResult.addAll(pOne);
            }
			/*====================处理位于上面一行的网格中的点======================*/
            for(int x=xQueryIndexStart;x<=xQueryIndexEnd;x++){
                ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,x,yQueryIndexEnd,pQueryStart,pQueryEnd);
                pQueryResult.addAll(pOne);
            }
			/*====================处理位于左面一行的网格中的点======================*/
            for(int y=yQueryIndexStart+1;y<yQueryIndexEnd;y++){
                ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,xQueryIndexStart,y,pQueryStart,pQueryEnd);
                pQueryResult.addAll(pOne);
            }
			/*====================处理位于右面一行的网格中点=======================*/
            for(int y=yQueryIndexStart+1;y<yQueryIndexEnd;y++){
                ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,xQueryIndexEnd,y,pQueryStart,pQueryEnd);
                pQueryResult.addAll(pOne);
            }
			/*===================================================================================*/
			/*========然后对完全处于查询范围中的网格进行处理，将这些网格中的点直接返回，添加到查询结果数组中=======*/
            for(int x=xQueryIndexStart+1;x<xQueryIndexEnd;x++){
                for(int y=yQueryIndexStart+1;y<yQueryIndexEnd;y++){
                    ArrayList<Point> pOne= directGetOneGridPoint(gIHAll,x,y);
                    pQueryResult.addAll(pOne);
                }
            }
			/*===================================================================*/
        }
		/*==========================================================================================*/

		/*==========当查询范围中没有完整的网格时，将与查询范围相交的每个网格都取出，并对每个网格中的点进行比较，返回符合查询条件的点=============*/
        else if(((xQueryIndexEnd-xQueryIndexStart)==1)||((yQueryIndexEnd-yQueryIndexStart)==1)){
            //pQueryResult.clear();//将结果数组清空
            for(int x=xQueryIndexStart;x<=xQueryIndexEnd;x++){
                for(int y=yQueryIndexStart;y<=yQueryIndexEnd;y++){
                    ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,x,y,pQueryStart,pQueryEnd);
                    pQueryResult.addAll(pOne);
                }
            }
        }
		/*==========================================================================================*/

		/*====================当查询范围在一个网格内时，将这个网格中的点取出，并过滤出符合查询条件的点===========================*/
        else if(((xQueryIndexEnd-xQueryIndexStart)==0)&&((yQueryIndexEnd-yQueryIndexStart)==0)){
            ArrayList<Point> pOne= oneGridPointFileQuery(gIHAll,xQueryIndexStart,yQueryIndexStart,pQueryStart,pQueryEnd);
            pQueryResult.addAll(pOne);
        }
		/*==========================================================================================*/

        return pQueryResult;//返回查询结果
    }
}
