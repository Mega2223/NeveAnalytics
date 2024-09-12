package net.mega2223.neveanalytics.objects;

import com.google.gson.JsonElement;
import jdk.jshell.execution.Util;
import mil.nga.tiff.*;
import net.mega2223.neveanalytics.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class LandsatBand<DataType extends Number> {

    public static final ArrayList<LandsatBand<?>> cache = new ArrayList<>();

    public final String path, name, assossiatedMtlPath;
    public final File file, mtl;
    public TIFFImage image = null;
    public final int landsatID, locPath, locRow, band, year, month, day;
    public int sizeX = -1, sizeY = -1;
    private List<FileDirectory> directories = null;
    public FileDirectory imgDirectory  = null;
    public Rasters raster = null;
    public Set<FileDirectoryEntry> entries  = null;
    public JsonElement data;
    public int noDataValue = 0;

    private LandsatBand(String path) {
        this.path = path;
        this.file = new File(path);
        this.name = file.getName();
        this.assossiatedMtlPath = name.substring(0,name.length() - 7) + "_MTL.txt";
        this.mtl = new File(assossiatedMtlPath);
        String[] info = this.name.split("\\.")[0].split("_");
        landsatID = Integer.parseInt(info[0].substring(2));
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
        this.data = getJSON(this);
    }

    private LandsatBand(String path, String name, String assossiatedMtlPath, File file, File mtl, int landsatID, int locPath, int locRow, int band, int year, int month, int day, JsonElement data){
        this.path = path;
        this.name = name;
        this.assossiatedMtlPath = assossiatedMtlPath;
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
    }

    public void bufferImage() throws IOException {
        if(raster != null && sizeX > 0 && sizeY > 0){return;}
        Utils.log("Loading image " + name + " into memory", Utils.DEBUG_DETAIL);
        Utils.log((cache.size()+1) + " images currently loaded",Utils.DEBUG_VERBOSE);
        image = TiffReader.readTiff(file);
        directories = image.getFileDirectories();
        imgDirectory = directories.get(0);
        raster = imgDirectory.readRasters();
        entries = imgDirectory.getEntries();
        sizeX = (int) imgDirectory.getImageWidth(); sizeY = (int) imgDirectory.getImageHeight();
        cache.add(this);
        printCache();
    }

    public void discardBuffer(){
        Utils.log("Unloading image " + name + " from memory", Utils.DEBUG_DETAIL);
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
        return (DataType) raster.getPixel(x,y)[0];
    }

    public boolean isSameImage(LandsatBand<?> image){
        return landsatID == image.landsatID && locPath == image.locPath && locRow == image.locRow &&
                year == image.year && day == image.day && month == image.month;
    }

    public String getNameNoBand(){
        return band >= 0 ? name.substring(0,name.length() - 7) : name; //TODO
    }

    public void save() throws IOException {
        bufferImage();
        Utils.saveTIFF(raster,this.file.getParent(),name.substring(0,name.length()-4));
        discardBuffer();
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
        while (cache.size() > 0) {
            cache.get(0).discardBuffer();
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
        Number n = TiffReader.readTiff(new File(path)).getFileDirectories().get(0).readRasters().getPixel(0, 0)[0];
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

    static JsonElement getJSON(LandsatBand<?> band){
        try {
            return Utils.readJson(fetchJSON(band).getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static File fetchJSON(LandsatBand<?> band){
        Utils.log("Fetching json for " + band.name, Utils.DEBUG_DETAIL);
        File js = Utils.doRecursiveSearch(
                band.name + ".json", new File(band.file.getParent())
        );
        if(js != null){
            return js;
        } else {
            try {
                //GDAL breaks when I run directly from the runScript so I have to create A BATCH file with the command and then
                //run the batch file, this is the greatest CSS moment ever
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
