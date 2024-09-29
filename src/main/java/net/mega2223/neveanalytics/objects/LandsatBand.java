package net.mega2223.neveanalytics.objects;

import com.google.gson.JsonElement;
import mil.nga.tiff.*;
import net.mega2223.neveanalytics.BandManager;
import net.mega2223.neveanalytics.Utils;
import org.gdal.gdal.gdal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class LandsatBand<DataType extends Number> {

    public static final ArrayList<LandsatBand<?>> cache = new ArrayList<>();

    public final String path, name, nameNoBand, assossiatedMtlPath, processingLevel;
    public final File file, mtl;
    public TIFFImage image = null;
    public final int landsatID, locPath, locRow, band, year, month, day;
    public int sizeX = -1, sizeY = -1;
    private List<FileDirectory> directories = null;
    public FileDirectory imgDirectory  = null;
    public Rasters raster = null;
    public Set<FileDirectoryEntry> entries  = null;
    public JsonElement data;
    public Number noDataValue;

    LandsatBand<?> mask = null;

    private LandsatBand(String path) throws IOException {
        this.path = path;
        this.file = new File(path);
        this.name = file.getName();
        this.assossiatedMtlPath = name.substring(0,name.length() - 7) + "_MTL.txt";
        this.mtl = new File(assossiatedMtlPath);
        String[] info = this.name.split("\\.")[0].split("_");
        landsatID = Integer.parseInt(info[0].substring(2));
        processingLevel = info[1];
        locPath = Integer.parseInt(info[2].substring(0,3));
        locRow = Integer.parseInt(info[2].substring(3,6));
        year = Integer.parseInt(info[3].substring(0,4));
        month = Integer.parseInt(info[3].substring(4,6));
        day = Integer.parseInt(info[3].substring(6,8));
        int band;
        try{
            band = info.length > 7 ?  Integer.parseInt(info[7].substring(1,2)) : 0;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored){band = -1;}
        this.band = band;
        this.nameNoBand = this.name.substring(0,40);
        image = TiffReader.readTiff(file);
        directories = image.getFileDirectories();
        imgDirectory = directories.get(0);
        Object values = imgDirectory.get(FieldTagType.GDAL_NODATA).getValues();
        if(values instanceof ArrayList<?> ls){
            noDataValue = Float.parseFloat(ls.get(0).toString());
        } else {
            noDataValue = 0.0F;
        }
        this.discardBuffer();
        try { Thread.sleep(100);
        } catch (InterruptedException ignored) {}
    }

    private LandsatBand(String path, String name, String assossiatedMtlPath, String processingLevel, File file, File mtl, int landsatID, int locPath, int locRow, int band, int year, int month, int day, JsonElement data, Number noDataValue){
        this.path = path;
        this.name = name;
        this.assossiatedMtlPath = assossiatedMtlPath;
        this.processingLevel = processingLevel;
        this.file = file;
        this.mtl = mtl;
        this.landsatID = landsatID;
        this.locPath = locPath;
        this.locRow = locRow;
        this.band = band;
        this.year = year;
        this.month = month;
        this.day = day;
        this.data = data;
        this.noDataValue = noDataValue;
        this.nameNoBand = this.name.substring(0,40);
    }

    public void bufferImage() throws IOException {
        if(raster != null && sizeX > 0 && sizeY > 0){return;}
        Utils.log("Loading image " + name + " into memory", Utils.DEBUG_DETAIL);
        Utils.log((cache.size()+1) + " images currently loaded",Utils.DEBUG_VERBOSE);
        gdal.ClearCredentials();
        image = TiffReader.readTiff(file);
        directories = image.getFileDirectories();
        imgDirectory = directories.get(0);
        raster = imgDirectory.readRasters();
        entries = imgDirectory.getEntries();
        sizeX = (int) imgDirectory.getImageWidth(); sizeY = (int) imgDirectory.getImageHeight();
        this.data = BandManager.getJSON(this);
        cache.add(this);
        printCache();

    }

    public void discardBuffer(){
        Utils.log("Unloading image " + name + " from memory", Utils.DEBUG_DETAIL);
        if(raster != null){throw new RuntimeException("G");}
        if(imgDirectory != null){imgDirectory.setCache(false);}
        image = null; directories = null; imgDirectory = null; raster = null; entries = null;
        cache.remove(this);
        try{Thread.sleep(200);} catch (InterruptedException ignored){}
        printCache();
    }

    public DataType get(int x, int y){
        while (this.raster == null){
            try {
                bufferImage();
            } catch (IOException e) {throw new RuntimeException(e);}
        }

        return (DataType) raster.getPixel(Math.min(x,sizeX-1),Math.min(y,sizeY-1))[0];
    }

    public boolean isSameImage(LandsatBand<?> image){
        return landsatID == image.landsatID && locPath == image.locPath && locRow == image.locRow &&
                year == image.year && day == image.day && month == image.month;
    }

    public String getNameNoBand(){
        //return "LC%02d_".formatted(this.landsatID) + this.processingLevel + "_%02d%02d_%04d%02d%02d".formatted(locPath,locRow,year,month,day);
        return nameNoBand;
    }

    public void save() throws IOException {
        boolean buffered = isBuffered();
        if(!buffered){bufferImage();}
        Utils.saveTIFF(raster,this.file.getParent(),name.substring(0,name.length()-4));
        if(!buffered){discardBuffer();}
    }

    public boolean isBuffered(){
        return raster == null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LandsatBand<?> img){
            return isSameImage(img);
        }
        return super.equals(obj);
    }

    public static class BandNotLoadedException extends RuntimeException {
        public BandNotLoadedException(){
            super("Band is not currently loaded.");
        }
    }

    public static void clearCache(){
        while (!cache.isEmpty()) {
            cache.get(0).discardBuffer();
        }
    }
    public static void clearCacheBut(LandsatBand<?> ... bands){
        List<LandsatBand<?>> bandList = List.of(bands);
        while (cache.size() > bands.length) {
            LandsatBand<?> band = cache.get(0);
            if(!bandList.contains(band)){band.discardBuffer();}
        }
    }

    public static void printCache(){
        StringBuilder b = new StringBuilder("cache: ");
        for (LandsatBand act : cache){
            b.append(act.name).append(" ");
        }
        Utils.log(b.toString(),Utils.DEBUG_VERBOSE);
    }

    public static LandsatBand<? extends Number> LoadImage(String path) throws IOException {
        Utils.log("Loading image " + path,Utils.DEBUG_DETAIL);
        return new LandsatBand<>(path);
    }

    public static LandsatBand<? extends Number> genImage(String location, String name, Number[][] data) throws IOException {
        Utils.log("Generating image " + name + ".TIF",Utils.DEBUG_DETAIL);
        Utils.saveTIFF(data,location,name);
        return LoadImage(location+"\\"+name+".TIF");
    }

    public static LandsatBand<? extends Number> genImage(String location, String name, int x, int y, Number datatype) throws IOException {
        Utils.log("Generating image " + name + ".TIF",Utils.DEBUG_DETAIL);
        Utils.saveTIFF(x,y,location,name);
        return LoadImage(location+"\\"+name+".TIF");
    }

    public static void removeFromCache(File f){
        for (int i = 0; i < cache.size(); i++) {
            if(cache.get(i).file.getAbsolutePath().equals(f.getAbsolutePath())){
                cache.get(i).discardBuffer();
            }
        }
    }

    public long getTimeEpoch(){
        return Instant.parse("%04d-%02d-%02dT00:00:00.00Z".formatted(year,month,day)).toEpochMilli();
    }

    public File genPNG(float min, float max, float[] minColor, float[] maxColor, String path) throws IOException {
        this.bufferImage();
        BufferedImage res = new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_4BYTE_ABGR);
        float range = min - max;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                float pixel = raster.getPixel(x, y)[0].floatValue();
                pixel = Utils.interpolate(min,max, pixel);
                int r = (int) Utils.interpolate(minColor[0],maxColor[0],pixel),
                        g = (int) Utils.interpolate(minColor[1],maxColor[1],pixel),
                        b = (int) Utils.interpolate(minColor[2],maxColor[2],pixel);
                int col = (r<<16) + (g<<8) + b;
                res.setRGB(x,y,col);
            }
        }
        File ret = new File(path);
        ImageIO.write(res,"png", ret);
        return ret;
    }

    public boolean hasDataAt(int x, int y) throws IOException {
        boolean buffered = isBuffered();
        if(!buffered){bufferImage();}
        boolean ret = this.get(x,y).floatValue() == this.noDataValue.floatValue();
        if(mask!=null){
            boolean mBuffered = mask.isBuffered();
            if(!mBuffered){mask.bufferImage();}
            ret = ret && mask.hasDataAt(x,y);
            if(!mBuffered){mask.discardBuffer();}
        }
        return ret;
    }

    public LandsatBand<?> getMask() {
        return mask;
    }

    public void setMask(LandsatBand<?> mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return name;
    }
}
