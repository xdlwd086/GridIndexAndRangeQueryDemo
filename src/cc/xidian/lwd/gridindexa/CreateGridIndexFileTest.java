package cc.xidian.lwd.gridindexa;

/**
 * Created by hadoop on 2015/5/25.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CreateGridIndexFileTest {

    public static void main(String[] args) throws Exception{
		/*=========================建立输入文件的路径，并创建文件对象，用于后面的查询中===========================*/
		/*======================三个测试文件的路径==========================*/
        String filePatha = "TestPointData.txt";//1000个点的测试文件路径
        String filePathb = "TestPointDataMiddle.txt";//5000个点的测试文件路径
        String filePathc = "AllPointDataTest.txt";//178万2426个点的测试文件路径
        File fileAll = new File(filePathc);//创建文件对象，用于后面的查询函数中
		/*====================================================================================*/

		/*=======================给出建立网格索引的条件==========================*/
        int xStartAll = -180;
        int yStartAll = -90;
        int xEndAll = 180;
        int yEndAll = 90;
        int dx = 20;//每个网格单元的横向增量
        int dy = 10;//每个网格单元的纵向增量
        String fileNameGridPoint = "PointGridIndexDataFile.txt";
        String fileNameGridIndex = "PointGridIndexFile.txt";
		/*=============================================================*/

        GridIndexHashAll gIHAll;//获取全局网格索引对象
        gIHAll = new GridIndexHashAll(xStartAll,yStartAll,xEndAll,yEndAll,dx,dy,fileNameGridIndex,fileNameGridPoint);

		/*=====================================以Hash的方式建立网格索引=================================*/
        long tGridIndexStartHash = System.currentTimeMillis();
        createPointGridIndexHashFile(fileAll,gIHAll);//建立关于点的网格索引，并将相应的点保存到文件中
        long tGridIndexEndHash = System.currentTimeMillis();
        System.out.print("以哈希方式建立网格索引所使用的时间：");
        System.out.println(tGridIndexEndHash - tGridIndexStartHash);
		/*======================================================================================*/
    }
    /**
     * 函数功能：在读入数据文件的过程中，直接建立网格索引，将每个网格中对应的点存入文件中
     * @param fileAll：点数据文件，保存着所有点的坐标
     * @param gIHAll：全局网格索引对象，保存有关全局索引的相关信息
     * @throws Exception
     */
    public static void createPointGridIndexHashFile(File fileAll,GridIndexHashAll gIHAll)throws Exception{
		/*===================生成网格索引的标识，并保存到全局索引对象中=========================*/
        for(int x=0;x<gIHAll.xNum;x++){
            for(int y=0;y<gIHAll.yNum;y++){
                gIHAll.gIH.add(new GridIndexHash(x,y));
            }
        }
		/*======================================================================*/

		/*==============在读取文件的同时，将每个点通过Hash的方式放到每个网格中的点数组中==============*/
        if(fileAll.exists()&&fileAll.isFile()){
            InputStreamReader read = new InputStreamReader(new FileInputStream(fileAll));
            BufferedReader bReader = new BufferedReader(read);
            String str;
            while((str=bReader.readLine())!=null){
                String[] strPoint = str.split(",");
                double x = Double.parseDouble(strPoint[0]);
                double y = Double.parseDouble(strPoint[1]);
                Point p = new Point(x,y);
                int xP = (int)((p.x-gIHAll.xStartAll)/(gIHAll.dx));
                int yP = (int)((p.y-gIHAll.yStartAll)/(gIHAll.dy));
                GridIndexHash gIHPoint = gIHAll.gIH.get(xP*(gIHAll.yNum)+yP);//获取点对应的网格索引对象
                gIHPoint.pAL.add(p);
            }
            read.close();
        }
		/*================================================================================*/

        //将每个网格对象中点数组保存的所有点写入全局索引数据文件中，将每个网格中点数组中的点在文件中所占的总字节数保存到每个网格对象的bSize字段中
        gIHAll.writeFileGridPoint(gIHAll);

        //将全局网格索引信息和每个网格对象的索引信息保存到全集索引信息文件中
        gIHAll.writeFileGridIndex(gIHAll);
    }
}

