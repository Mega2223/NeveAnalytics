import net.mega2223.neveanalytics.Utils;
import org.gdal.gdal.InfoOptions;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.util.Vector;

public class InfoTest {
    public static void main(String[] args) {
        Utils.initGDAL();
        Vector<String> options = new Vector<>();
        options.add("-json");
        String info = gdal.GDALInfo(gdal.Open(
                "C:\\Users\\Imperiums\\Desktop\\menos\\LC08_L1TP_232093_20150121_20200910_02_T1_B6.TIF",
                gdalconst.GA_Update),
                new InfoOptions(options)
        );
        System.out.println(info);
    }
}
