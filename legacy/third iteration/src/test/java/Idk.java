import net.mega2223.neveanalytics.Utils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.util.Vector;

public class Idk {
    public static void main(String[] args) {
        Utils.initGDAL();
        Dataset a = gdal.Open("C:\\Users\\Imperiums\\Desktop\\temp\\dest\\LC08_L1TP_232092_20170416_20200904_02_T1_NSDI.TIF", gdalconst.GA_Update);
        Dataset b = gdal.Open("C:\\Users\\Imperiums\\Desktop\\temp\\dest\\LC08_L1TP_232093_20160108_20200907_02_T1_NSDI.TIF",gdalconst.GA_Update);

    }
}
