import net.mega2223.neveanalytics.Constants;
import net.mega2223.neveanalytics.NeveAnalyitcs;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatBand;
import net.mega2223.neveanalytics.objects.LandsatPicture;
import net.mega2223.neveanalytics.objects.Operation;
import org.gdal.gdal.gdal;

import java.io.IOException;
import java.util.List;

public class EquationTest {
    public static void main(String[] args) throws IOException {
        Utils.DEBUG_LEVEL = Utils.DEBUG_VERBOSE;
        org.gdal.gdal.gdal.AllRegister();
        System.out.println( gdal.VersionInfo());
        List<LandsatPicture<? extends Number>> pic = LandsatPicture.scanFolder(Constants.DATA_PATH);
        Utils.clearTemp();
        for (LandsatPicture<?> act : pic){
            //List<? extends LandsatBand<?>> bands = act.getBands();
            Operation.runOperation(act,Utils.readJson(Constants.APP_PATH + "\\operations\\NSDI.json").getAsJsonObject(),
                    act.getBand(3).getNameNoBand()+"NSDI",
                    NeveAnalyitcs.CONFIG.get("dest_dir").getAsString()
                    ,true);
            LandsatBand.clearCache();
        }
    }
}
