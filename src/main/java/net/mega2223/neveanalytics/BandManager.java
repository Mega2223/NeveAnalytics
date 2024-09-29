package net.mega2223.neveanalytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mega2223.neveanalytics.objects.LandsatBand;
import org.gdal.gdal.InfoOptions;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class BandManager {
    private BandManager(){}
    private static int count = 0;
    public static LandsatBand<?> getAverage(LandsatBand<?>... pictures) throws IOException {
//        LandsatBand<?> ret = LandsatBand.genImage(NeveAnalyitcs.CONFIG.get("dest_dir").getAsString()+"\\averages",
//                pictures[0].nameNoBand+"_AVERAGE_" + count, pictures[0].sizeX,pictures[0].sizeY,0.0F
//                );
        if(pictures.length == 0){return null;}
        for (int i = 0; i < pictures.length; i++) {
            pictures[i].bufferImage();
            //pictures[i].getMask().bufferImage();
        }
        int sX = pictures[0].sizeX; int sY = pictures[0].sizeY;
        Number[][] buffer = new Number[sX][sY];

        for (int x = 0; x < sX; x++) {
            for (int y = 0; y < sY; y++) {
                int amount = 0;
                double sum = 0;
                for (int i = 0; i < pictures.length; i++) {
                    if(pictures[i].hasDataAt(x,y)){
                        amount++;
                        sum += pictures[i].get(x,y).doubleValue();
                    }
                }
                buffer[x][y] = sum / amount;
            }
        }
        count++;
        for (int i = 0; i < pictures.length; i++) {
            //pictures[i].discardBuffer();
            //pictures[i].getMask().discardBuffer();
        }

        return LandsatBand.genImage(
                NeveAnalyitcs.CONFIG.get("dest_dir").getAsString()+"\\averages",
                pictures[0].nameNoBand+"_AVERAGE_" + count,
                buffer);
    }

    public static JsonElement getJSON(LandsatBand<?> band){
        if(!Utils.isGDALInit){Utils.initGDAL();}
        Utils.initGDAL();
        Vector<String> options = new Vector<>();
        options.add("-json");
        String info = gdal.GDALInfo(gdal.Open(
                        band.file.getAbsolutePath(),
                        gdalconst.GA_ReadOnly),
                new InfoOptions(options)
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            Utils.log("WARNING: SLEEP INTERRUPTED, MAY CAUSE ACCESS VIOLATION",Utils.DEBUG_IMPORTANT);
        }
        return JsonParser.parseString(
                info
        );
    }

    public static File fetchJSON(LandsatBand<?> band){
        Utils.log("Fetching json for " + band.name, Utils.DEBUG_DETAIL);
        File js = Utils.doRecursiveSearch(
                band.name + ".json", new File(band.file.getParent())
        );
        if(js != null){
            return js;
        } else {
            try {
                // GDAL breaks when I run directly from the runScript,
                // so I have to create A BATCH FILE with the command and then run said batch file,
                // this is the most CSS moment ever
                String cmd = "gdalinfo -stats -nomd -norat -noct -json " + band.file.getAbsolutePath() + ">>" + band.file.getAbsolutePath() + ".json";
                File f = new File(band.file.getParentFile().getAbsolutePath()+"\\g.bat");
                f.createNewFile();
                BufferedWriter w = new BufferedWriter(new FileWriter(f));
                w.write(cmd);
                w.close();
                Utils.runScript(band.file.getParent()+"\\g.bat",band.file.getParentFile());
                f.delete();
            } catch (IOException notIgnored) {
                throw new RuntimeException(notIgnored);
            }
        }
        return Utils.doRecursiveSearch(
                band.name + ".json", new File(band.file.getParent())
        );
    }
}
