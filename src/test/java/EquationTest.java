import jdk.jshell.execution.Util;
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
        Utils.initGDAL();
        List<LandsatPicture<? extends Number>> pic = LandsatPicture.scanFolder(Constants.DATA_PATH);
        Utils.clearTemp();
        for (LandsatPicture<?> act : pic){
            //List<? extends LandsatBand<?>> bands = act.getBands();
            String name = act.getBand(3).getNameNoBand() + "_NSDI";
            if(Utils.doRecursiveSearch(name+".TIF",act.getBand(3).file.getParentFile()) != null){
                System.out.println("File already exists for " + name);
                continue;
            }
            Operation.runOperation(act,Utils.readJson(Constants.APP_PATH + "\\operations\\NSDI.json").getAsJsonObject(),
                    name,
                    NeveAnalyitcs.CONFIG.get("dest_dir").getAsString()
                    ,true);
            LandsatBand.clearCache();
            System.out.println("Done for " + act.getBands().get(0).getNameNoBand());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }
    }
}
