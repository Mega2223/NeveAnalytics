package net.mega2223.neveanalytics.standalonescripts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mil.nga.tiff.Rasters;
import net.mega2223.neveanalytics.Constants;
import net.mega2223.neveanalytics.objects.LandsatImage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static net.mega2223.neveanalytics.standalonescripts.StatsReportGenerator.SNOW_THRESHOLD;

public class SDNICoverGenerator {
    public static void main(String[] args) throws IOException {
        int snowPixels;
        File folder = new File(Constants.DATA_PATH+"\\NDSI");
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            String[] act = files[i].getName().split("\\.");
            if(act.length <= 1 || !act[act.length-1].equals("TIF")){continue;}

            snowPixels = 0;
            LandsatImage imgAct = new LandsatImage(files[i].getAbsolutePath());

            imgAct.bufferImage();
            Rasters raster = imgAct.raster;

            for (int x = 0; x < raster.getWidth(); x++) {
                for (int y = 0; y < raster.getHeight(); y++) {
                    float pixel = (float) raster.getPixel(x,y)[0];
                    if(pixel >= SNOW_THRESHOLD){snowPixels++;}
                }
            }
            System.out.println("SNOW PIXELS FOR "+ files[i].getName() +": " + snowPixels);
        }

    }
}
