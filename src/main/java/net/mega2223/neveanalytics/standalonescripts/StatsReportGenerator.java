package net.mega2223.neveanalytics.standalonescripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import mil.nga.tiff.Rasters;
import net.mega2223.neveanalytics.Constants;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatImage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StatsReportGenerator {
    public static float SNOW_THRESHOLD = .5F;

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

            JsonElement imgInfo = JsonParser.parseReader(new FileReader(files[i].getAbsolutePath()+".json"));
            JsonArray geoTransform = imgInfo.getAsJsonObject().get("geoTransform").getAsJsonArray();

            for (int x = 0; x < raster.getWidth(); x++) {
                for (int y = 0; y < raster.getHeight(); y++) {
                    float pixel = (float) raster.getPixel(x,y)[0];
                    if(pixel >= SNOW_THRESHOLD){snowPixels++;}
                }
            }
            imgAct.discardBuffer();
            printAndBuffer("SNOW PIXELS FOR "+ files[i].getName() +": " + snowPixels + " (Threshold "+SNOW_THRESHOLD+")");
            float x = geoTransform.get(1).getAsFloat(), y = geoTransform.get(5).getAsFloat();
            x = Math.abs(x); y = Math.abs(y);
            printAndBuffer("PIXEL SIZE: (" + x + "," + y + ")");
            printAndBuffer("TOTAL AREA COVERED: " + snowPixels * Math.sqrt(x * y) + ", " + 100*((float)snowPixels)/(raster.getNumPixels()) + "%");
            printAndBuffer("------------------------------------------------------------");
        }
        Utils.saveFile(log.toString(),Constants.DATA_PATH+"\\Coverage.txt");
        log.delete(0,log.length());
    }
    protected static StringBuilder log = new StringBuilder();
    static void printAndBuffer(String data){
        System.out.println(data);
        log.append(data).append("\n");
    }
}
