package net.mega2223.neveanalytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;
import net.mega2223.neveanalytics.objects.LandsatBand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static final int DEBUG_NOTHING = 0, DEBUG_IMPORTANT = 1, DEBUG_TASKS = 2, DEBUG_DETAIL = 3, DEBUG_VERBOSE = 4;
    public static int DEBUG_LEVEL = 0;

    public static void log(String log, int level){
        if(level <= DEBUG_LEVEL){System.out.println(log);}
    }

    public static void saveTIFF(Number[][] dat, String path, String name) throws IOException {
        final int W = dat.length, H = dat[0].length;
        int format = getFormat(dat[0][0]);
        FieldType fieldType = FieldType.getFieldType(format,dat[0][0] instanceof Long ? 32 : 16);
        int bitsPerSample = fieldType.getBits();
        System.out.println("PROCESSING " + name + ".TIF");
        Rasters img = new Rasters(W, H, 1, fieldType);

        int rowsPerStrip = img.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        FileDirectory directory = new FileDirectory();
        directory.setStringEntryValue(FieldTagType.GDAL_NODATA, "0"); //FIXME
        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(1);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(format);
        directory.setWriteRasters(img);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                img.setPixelSample(0,x, y, dat[x][y]);
            }
        }
        dat = null;

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();
        LandsatBand.clearCache();

        try{
            Thread.sleep(100);
            TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        } catch (OutOfMemoryError err) {
            log("Could not save " + name + ".TIF due to heap memory",DEBUG_IMPORTANT);
        } catch (InterruptedException ignored) {}

        tiffImage = null; directory = null; img = null; //may seem redundant but actually reduces heap memory somehow
    }

    public static void saveTIFF(Rasters dat, String path, String name) throws IOException {
        final int W = dat.getWidth(), H = dat.getHeight();
        Number n = dat.getPixel(0, 0)[0];
        int format = getFormat(n);
        FieldType fieldType = FieldType.getFieldType(format,n instanceof Long ? 32 : 16);
        int bitsPerSample = fieldType.getBits();
        System.out.println("PROCESSING " + name + ".TIF");
        Rasters img = new Rasters(W, H, 1, fieldType);

        int rowsPerStrip = img.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        FileDirectory directory = new FileDirectory();
        directory.setStringEntryValue(FieldTagType.GDAL_NODATA, "0"); //FIXME
        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(1);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(format);
        directory.setWriteRasters(img);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                img.setPixelSample(0,x, y, dat.getPixelSample(0,x,y));
            }
        }
        dat = null;

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();
        LandsatBand.clearCache();

        try{
            Thread.sleep(100);
            TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        } catch (OutOfMemoryError err) {
            log("Could not save " + name + ".TIF due to heap memory",DEBUG_IMPORTANT);
        } catch (InterruptedException ignored) {}

        tiffImage = null; directory = null; img = null; //may seem redundant but actually reduces heap memory somehow
    }

    public static void saveTIFF(int W, int H, String path, String name, Number datatype) throws IOException {
        int format = getFormat(datatype);
        FieldType fieldType = FieldType.getFieldType(format,datatype instanceof Long ? 32 : 16);
        int bitsPerSample = fieldType.getBits();

        System.out.println("PROCESSING " + name + ".TIF");
        Rasters img = new Rasters(W, H, 1, fieldType);

        int rowsPerStrip = img.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);

        FileDirectory directory = new FileDirectory();
        directory.setStringEntryValue(FieldTagType.GDAL_NODATA, "0"); //FIXME
        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(1);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(format);
        directory.setWriteRasters(img);

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();
        LandsatBand.clearCache();

        try{
            Thread.sleep(100);
            TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        } catch (OutOfMemoryError err) {
            log("Could not save " + name + ".TIF due to heap memory",DEBUG_IMPORTANT);
        } catch (InterruptedException ignored) {}
        tiffImage = null; directory = null; img = null;
    }

    static int getFormat(Number n){
        if(n instanceof Integer || n instanceof Short || n instanceof Long){return TiffConstants.SAMPLE_FORMAT_SIGNED_INT;}
        else if(n instanceof Float || n instanceof Double){return TiffConstants.SAMPLE_FORMAT_FLOAT;}
        else{return TiffConstants.SAMPLE_FORMAT_UNDEFINED;}
    }

    public static JsonElement readJson(String path) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(path));
    }
}
