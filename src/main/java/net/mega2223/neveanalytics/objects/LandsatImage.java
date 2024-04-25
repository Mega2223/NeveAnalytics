package net.mega2223.neveanalytics.objects;

import mil.nga.tiff.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class LandsatImage {
    public final String path, name, assossiatedMtlPath;
    public final File file, mtl;
    public TIFFImage image = null;
    public final int landsatID, locPath, locRow, band, year, month, day;
    private List<FileDirectory> directories = null;
    public FileDirectory imgDirectory  = null;
    public Rasters raster = null;
    public Set<FileDirectoryEntry> entries  = null;

    public LandsatImage(String path){
        this.path = path;
        this.file = new File(path);
        String title = file.getName();
        this.name = title;
        this.assossiatedMtlPath = name.substring(0,name.length() - 7) + "_MTL.txt";
        this.mtl = new File(assossiatedMtlPath);
        String[] info = title.split("\\.")[0].split("_");
        landsatID = Integer.parseInt(info[0].substring(2));
        locPath = Integer.parseInt(info[2].substring(0,3));
        locRow = Integer.parseInt(info[2].substring(3,6));
        year = Integer.parseInt(info[3].substring(0,4));
        month = Integer.parseInt(info[3].substring(4,6));
        day = Integer.parseInt(info[3].substring(6,8));
        band = info.length > 7 ?  Integer.parseInt(info[7].substring(1,2)) : 0;
    }

    public void bufferImage() throws IOException {
        image = TiffReader.readTiff(file);
        directories = image.getFileDirectories();
        imgDirectory = directories.get(0);
        raster = imgDirectory.readRasters();
        entries = imgDirectory.getEntries();
    }
    public void discardBuffer(){
        image = null; directories = null; imgDirectory = null; raster = null; entries = null;
    }
}
