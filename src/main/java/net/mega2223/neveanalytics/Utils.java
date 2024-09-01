package net.mega2223.neveanalytics;

import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;
import net.mega2223.neveanalytics.legacy.Constants;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static void saveTIFF(Number[][] dat, String path, String name) throws IOException {
        final int W = dat.length, H = dat[0].length;
        FieldType fieldType = FieldType.FLOAT;
        int bitsPerSample = fieldType.getBits();

        System.out.println("PROCESSING " + name + ".TIF");
        Rasters img = new Rasters(W, H, 1, fieldType);

        int rowsPerStrip = img.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);

        FileDirectory directory = new FileDirectory();
        directory.setStringEntryValue(FieldTagType.GDAL_NODATA, "-2"); //FIXME
        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(1);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(getFormat(dat[0][0]));
        directory.setWriteRasters(img);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                img.setFirstPixelSample(x, y, dat[x][y]);
            }
        }

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();
        TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        tiffImage = null; directory = null; img = null; //may seem redundant but actually reduces heap memory somehow
    }

    static int getFormat(Number n){
        if(n instanceof Integer || n instanceof Short || n instanceof Long){return TiffConstants.SAMPLE_FORMAT_SIGNED_INT;}
        else if(n instanceof Float || n instanceof Double){return TiffConstants.SAMPLE_FORMAT_FLOAT;}
        else{return TiffConstants.SAMPLE_FORMAT_UNDEFINED;}
    }
}
