package net.mega2223;

import mil.nga.tiff.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static net.mega2223.Converter.log;

public class LandsatImage {

    public final String satelite;
    public final int band;
    public final Date dateTaken;

    public final TIFFImage tiffImage;
    public final List<FileDirectory> directories;
    public final FileDirectory imgDirectory;
    public final Rasters rasters;
    public final int [][] dat;
    private final Set<FileDirectoryEntry> entries;
    private final File directory;

    public LandsatImage(File directory) throws IOException {
        log("Loading " + directory.getName());
        this.directory = directory;
        String[] titleInfo = directory.getName().split("_");
        satelite = titleInfo[0];
        this.band = Integer.parseInt(titleInfo[7].replace(".TIF","").replace("B",""));
        String acquisitionDate = titleInfo[3];
        dateTaken = new Date(
                Integer.parseInt(acquisitionDate.substring(0,4)) - 1900,
                Integer.parseInt(acquisitionDate.substring(4,6)) - 1,
                Integer.parseInt(acquisitionDate.substring(6,8))
        );

        log("METADATA: \nImage date: " + dateTaken);

        tiffImage = TiffReader.readTiff(directory);
        directories = tiffImage.getFileDirectories();
        imgDirectory = directories.get(0);
        rasters = imgDirectory.readRasters();
        entries = imgDirectory.getEntries();


        //directories.get(0).

        log("Directories: " + directories.size());
        int max = 0, min = Integer.MAX_VALUE;
        int xMax = 0, yMax = 0, xMin = 0, yMin = 0;

        final int w = rasters.getWidth(), h = rasters.getHeight();;
        dat = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Number[] pixel = rasters.getPixel(i, j);
                if(pixel.length > 1){
                    System.out.println("WHATYTTTTT");
                    System.exit(0);
                }
                dat[i][j] = (int) pixel[0];
                if((int)pixel[0] != 0){
                    int n = ((int)pixel[0]);
                    //System.out.println(n);
                    if(n > max){xMax = i; yMax = j;}
                    if(n < min){xMin = i; yMin = j;}
                    max = Math.max(n,max);
                    min = Math.min(n,min);
                }
            }
        }

        //System.out.println("MAX: " + max + " (x=" + xMax + ", y=" + yMax + ")");
        //System.out.println("MIN: " + min + " (x=" + xMin + ", y=" + yMin + ")");

        log("MAX: " + max + " (x=" + xMax + ", y=" + yMax + ")");
        log("MIN: " + min + " (x=" + xMin + ", y=" + yMin + ")");

        log("\n");
    }

    public double[][] getCorrected(){
        double[] supposedBoundaries = Calibrator.findSupposedBoundaries(directory);
        return Calibrator.correct(dat,supposedBoundaries);
    }
}
