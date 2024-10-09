import net.mega2223.neveanalytics.NeveAnalyitcs;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.legacy.objects.LandsatImage;
import net.mega2223.neveanalytics.objects.LandsatBand;
import net.mega2223.neveanalytics.objects.LandsatPicture;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataCopyTest {
    public static void main(String[] args) throws IOException {
        Utils.DEBUG_LEVEL = Utils.DEBUG_VERBOSE;
        Utils.initGDAL();

        String from = NeveAnalyitcs.CONFIG.get("sources_dir").toString().replace("\"","");
        String to = NeveAnalyitcs.CONFIG.get("dest_dir").toString().replace("\"","");

        List<LandsatPicture<? extends Number>> fr = LandsatPicture.scanFolder(from);
        List<LandsatPicture<? extends Number>> t = LandsatPicture.scanFolder(to);

        for(LandsatPicture<?> fromPic : fr){
            for (LandsatPicture<?> toPic : t){
                LandsatBand<?> fromBand = fromPic.getBands().get(0);
                LandsatBand<?> toBand = toPic.getBands().get(0);
                boolean isSame = fromBand.isSameImage(toBand);
                if(isSame){
                    Utils.copyGEOTIFFProperties(fromBand.file,toBand.file);
                }
            }
        }
    }
}
