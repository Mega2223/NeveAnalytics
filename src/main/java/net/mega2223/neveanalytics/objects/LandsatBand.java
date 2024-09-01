package net.mega2223.neveanalytics.objects;

import mil.nga.tiff.*;
import net.mega2223.neveanalytics.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class LandsatBand<DataType extends Number> {
    public final String path, name, assossiatedMtlPath;
    public final File file, mtl;
    public TIFFImage image = null;
    public final int landsatID, locPath, locRow, band, year, month, day;
    public int sizeX = -1, sizeY = -1;
    private List<FileDirectory> directories = null;
    public FileDirectory imgDirectory  = null;
    public Rasters raster = null;
    public Set<FileDirectoryEntry> entries  = null;

    public static LandsatBand<? extends Number> LoadImage(String path) throws IOException {
        Number n = TiffReader.readTiff(new File(path)).getFileDirectories().get(0).readRasters().getPixel(0, 0)[0];
        return new LandsatBand<>(path);
    }

    public static LandsatBand<? extends Number> genImage(String location, String name, Number[][] data) throws IOException {
        Utils.saveTIFF(data,location,name);
        return LoadImage(location+"\\"+name);
    }

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
        band = info.length > 7 ?  Integer.parseInt(info[7].substring(1,2)) : 0;
    }

    private LandsatBand(String path, String name, String assossiatedMtlPath, File file, File mtl, int landsatID, int locPath, int locRow, int band, int year, int month, int day){
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
    }

    @Override
    public LandsatBand clone() {
        return new LandsatBand(path,name,assossiatedMtlPath,file,mtl,landsatID,locPath,locRow,band,year,month,day);
    }

    public void bufferImage() throws IOException {
        image = TiffReader.readTiff(file);
        directories = image.getFileDirectories();
        imgDirectory = directories.get(0);
        raster = imgDirectory.readRasters();
        entries = imgDirectory.getEntries();
        sizeX = (int) imgDirectory.getImageWidth(); sizeY = (int) imgDirectory.getImageHeight();
    }

    public void discardBuffer(){
        image = null; directories = null; imgDirectory = null; raster = null; entries = null;
    }

    public DataType get(int x, int y){
        if(image != null){
            return (DataType) raster.getPixel(x,y)[0];
        }
        return null;
    }

    public boolean isSameImage(LandsatBand<?> image){
        return landsatID == image.landsatID && locPath == image.locPath && locRow == image.locRow &&
                year == image.year && day == image.day && month == image.month;
    }

    public String getNameNoBand(){
        return name.substring(0,name.length() - 7);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LandsatBand<?> img){
            return isSameImage(img);
        }
        return super.equals(obj);
    }
}
