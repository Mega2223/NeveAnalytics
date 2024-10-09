package net.mega2223.neveanalytics.legacy.standalonescripts;

import net.mega2223.neveanalytics.legacy.Constants;
import net.mega2223.neveanalytics.legacy.objects.LandsatImage;
import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NDSICalculator {
    public static boolean savePNG = false;
    public static void main(String[] args) throws IOException {
        System.out.println("Performing raster calculations");
        File root = new File(Constants.DATA_PATH+"\\Unprocessed");
        File[] dates = root.listFiles();
        for (int i = 0; i < dates.length; i++) {
            if(!dates[i].isDirectory()){continue;}
            File[] locations = dates[i].listFiles();
            for (int j = 0; j < locations.length; j++) {
                File[] satelites = locations[j].listFiles();
                for (int k = 0; k < satelites.length; k++) {
                    if(!satelites[k].isDirectory()){continue;}
                    File[] files = satelites[k].listFiles();
                    if(files == null || files.length < 2){continue;}
                    File[] images = new File[2]; int img = 0;
                    for (int f = 0; f < files.length && img < 2; f++) {
                        String[] name = files[f].getName().split("\\.");
                        if(name[name.length-1].equals("TIF")){images[img] = files[f];img++;}
                    }
                    if(images[1] == null){
                        System.out.println("Could not find enough images for raster equations at " + satelites[k].getAbsolutePath());
                        continue;
                    }
                    System.out.println("Calculating for " + satelites[k].getPath());
                    calculateNDSI(images[0],images[1]);
                }
            }
        }
    }

    public static void calculateNDSI(File A, File B) throws IOException {
        LandsatImage bandUpper = new LandsatImage(A.getAbsolutePath());
        LandsatImage bandLower = new LandsatImage(B.getAbsolutePath());
        if(bandUpper.band < bandLower.band){
            LandsatImage buf = bandLower; bandLower = bandUpper; bandUpper = buf;
        }
        bandLower.bufferImage();
        bandUpper.bufferImage();

        final int x = bandLower.raster.getWidth(), y = bandLower.raster.getHeight();
        double[][] dat = new double[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Number lowerPixel = bandLower.raster.getPixel(i,j)[0];
                Number upperPixel = bandUpper.raster.getPixel(i,j)[0];
                int lowerI = lowerPixel instanceof Integer ? (int) lowerPixel : (short) lowerPixel;
                int upperI = upperPixel instanceof Integer ? (int) upperPixel : (short) upperPixel;

                if(upperI == Constants.NO_DATA || lowerI == Constants.NO_DATA){
                    dat[i][j] = Constants.NO_DATA_DOUBLE;
                    continue;
                }
                //double lowerPixelD = lowerPixel, upperPixelD = upperPixel;
                dat[i][j] = (lowerI - upperI)/(double)(upperI + lowerI);
            }
        }
        String outName = bandUpper.name.substring(0, bandUpper.name.length() - 7);
        if(savePNG){savePNG(dat, outName);}
        saveTIFF(dat,outName);
        bandLower.discardBuffer(); bandUpper.discardBuffer();
    }

    public static void saveTIFF(double[][] dat, String name) throws IOException {
        final int W = dat.length, H = dat[0].length;
        FieldType fieldType = FieldType.FLOAT;
        int bitsPerSample = fieldType.getBits();

        System.out.println("PROCESSING " + name + ".TIF");
        Rasters img = new Rasters(W, H, 1, fieldType);

        int rowsPerStrip = img.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);

        FileDirectory directory = new FileDirectory();
        directory.setStringEntryValue(FieldTagType.GDAL_NODATA, "-2"); //FIXME
        //Set<FileDirectoryEntry> ogEntries = original.imgDirectory.getEntries();

       /* for (FileDirectoryEntry act : ogEntries) {
            String fieldTagName = act.getFieldTag().name();
            String fieldTypeName = act.getFieldType().name();
            Object values = act.getValues();
            switch (fieldTagName){
                //case "GeoKeyDirectory":
                //   directory.setUnsignedIntegerListEntryValue(act.getFieldTag(), (List<Integer>) values);
                //   break;
                case "GDAL_NODATA":
                    directory.setStringEntryValue(act.getFieldTag(), "-2"); //FIXME
                    break;
                /*case "GeoAsciiParams":
                    ArrayList<String> valuesArr = (ArrayList<String>) values;
                    directory.setStringEntryValue(act.getFieldTag(), valuesArr.get(0));
                    break;
                case "TileOffsets":
                    //directory.setTileOffsets((ArrayList<Int>) values);
                    break;
                case "TileByteCounts":
                    //directory.setTileByteCounts((ArrayList<Integer>) values);
                    break;
                case "ModelPixelScale":
                    directory.setModelPixelScale((List<Double>) values);
                    break;
            }
            System.out.println(fieldTypeName);
            System.out.println(fieldTagName);
            System.out.println(values);
            System.out.println("-----");
        }*/

        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(1);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_FLOAT);
        directory.setWriteRasters(img);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                img.setFirstPixelSample(x, y, (float)dat[x][y]);
            }
        }

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(Constants.DATA_PATH + "\\NDSI\\"); outFolder.mkdirs();
        TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        tiffImage = null; directory = null; img = null; //may seem redundant but actually reduces heap memory somehow

    }

    public static void savePNG(double[][] dat, String name) throws IOException {
        System.out.println("PROCESSING " + name + ".png");
        BufferedImage teste = new BufferedImage(dat.length,dat[0].length,BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < dat.length; i++) {
            for (int j = 0; j < dat[i].length; j++) {
                if(dat[i][j] == Constants.NO_DATA_DOUBLE){teste.setRGB(i,j,0); continue;}
                int act = (int) ((dat[i][j]+1) * .5D * 255);
                act = Math.max(Math.min(act,255),0);
                //if(act!=0){System.out.println(dat[i][j] + "->" + act);}
                teste.setRGB(i,j,act|(act<<8)|(act<<16));
            }
        }
        File outFolder = new File(Constants.DATA_PATH + "\\NDSI_PNG\\"); outFolder.mkdirs();
        ImageIO.write(teste,"png",new File(outFolder.getAbsolutePath()+"\\"+name+".png"));
        //System.exit(0);
    }
}
