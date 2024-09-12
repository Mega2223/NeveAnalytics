import net.mega2223.neveanalytics.Constants;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatBand;
import net.mega2223.neveanalytics.objects.LandsatPicture;
import net.mega2223.neveanalytics.objects.Operation;

import java.io.IOException;
import java.util.List;

public class EquationTest {
    public static void main(String[] args) throws IOException {
        Utils.DEBUG_LEVEL = Utils.DEBUG_VERBOSE;
        List<LandsatPicture<? extends Number>> pic = LandsatPicture.scanFolder(Constants.DATA_PATH);
        for (LandsatPicture<?> act : pic){
            //List<? extends LandsatBand<?>> bands = act.getBands();
            Operation.runOperation(act,Utils.readJson(Constants.APP_PATH + "\\operations\\NDSI.json").getAsJsonObject());
        }
    }
}
