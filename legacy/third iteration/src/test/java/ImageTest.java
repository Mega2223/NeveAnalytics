import net.mega2223.neveanalytics.Constants;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatBand;
import net.mega2223.neveanalytics.objects.LandsatPicture;

import java.io.IOException;
import java.util.List;

public class ImageTest {
    public static void main(String[] args) throws IOException {
        Utils.DEBUG_LEVEL = Utils.DEBUG_DETAIL;
        List<LandsatPicture<? extends Number>> pic = LandsatPicture.scanFolder(Constants.DATA_PATH);
        for (LandsatPicture<?> act : pic){
            List<? extends LandsatBand<?>> bands = act.getBands();
            System.out.println(bands.size());
            for (LandsatBand<? extends Number> act2 : bands){
                act2.bufferImage();
                System.out.println(act2.get(0,0));
                System.out.println(act2.get(0,0).getClass().getName());
                act2.discardBuffer();
            }
        }
    }
}
