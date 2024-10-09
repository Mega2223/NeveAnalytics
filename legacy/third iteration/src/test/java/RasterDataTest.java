import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;
import net.mega2223.neveanalytics.Utils;

import javax.imageio.plugins.tiff.TIFFDirectory;
import java.io.File;
import java.io.IOException;

public class RasterDataTest {
    public static void main(String[] args) throws IOException {
        TIFFImage image = TiffReader.readTiff(new File("C:\\Users\\Imperiums\\Desktop\\menos\\LC08_L1GT_001093_20180316_20200901_02_T2_B3.TIF"));
        Utils.debugImg(image);
    }
}
