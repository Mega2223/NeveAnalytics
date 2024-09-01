import net.mega2223.neveanalytics.legacy.Constants;
import net.mega2223.neveanalytics.legacy.objects.LandsatImage;

import java.io.File;
import java.io.IOException;

public class RasterTest {
    public static void main(String[] args) throws IOException {
        File root = new File(Constants.DATA_PATH);
        File[] files = root.listFiles();

        for (int i = 0; i < files.length; i++) {
            File act = files[i];
            String[] name = act.getName().split("\\.");
            if (name.length == 2 && name[1].equalsIgnoreCase("TIF")){
                System.out.println(act.getName());
                LandsatImage actact = new LandsatImage(act.getAbsolutePath());
                actact.bufferImage();
                int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;

                for (int x = 0; x < actact.raster.getWidth(); x++) {
                    for (int y = 0; y < actact.raster.getHeight(); y++) {
                        int p = (int) actact.raster.getPixel(x,y)[0];
                        if(p == Constants.NO_DATA){continue;}
                        max = Math.max(p,max); min = Math.min(p,min);
                    }
                }
                System.out.println(max + ":" + min);
            }
            //System.exit(0);
        }
    }
}
