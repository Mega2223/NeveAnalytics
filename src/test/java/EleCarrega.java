import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatPicture;

import java.io.IOException;
import java.util.List;

public class EleCarrega {
    public static void main(String[] args) throws IOException {
        Utils.DEBUG_LEVEL = Utils.DEBUG_VERBOSE;
        List<LandsatPicture<? extends Number>> g = LandsatPicture.scanFolder("C:\\Users\\Imperiums\\Desktop\\menos");
        for (int i = 0; i < g.size(); i++) {
            System.out.println(g.get(i).getBands().get(0).getNameNoBand());
        }
    }
}
