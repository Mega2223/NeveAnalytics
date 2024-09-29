package net.mega2223.neveanalytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;
import net.mega2223.neveanalytics.objects.LandsatBand;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.InfoOptions;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Utils {

    public static final int DEBUG_NOTHING = 0, DEBUG_IMPORTANT = 1, DEBUG_TASKS = 2, DEBUG_DETAIL = 3, DEBUG_VERBOSE = 4;
    public static int DEBUG_LEVEL = 0;

    public static boolean isGDALInit = false;

    public static void log(String log, int level){
        if(level <= DEBUG_LEVEL){System.out.println(log);}
    }

    public static void saveTIFF(Number[][] dat, String path, String name) throws IOException {
        final int W = dat.length, H = dat[0].length;
        int format = getFormat(dat[0][0]);
        FieldType fieldType = FieldType.FLOAT;
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
                img.setFirstPixelSample(x, y, dat[x][y]);
            }
        }
        dat = null;

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();

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
        FieldType fieldType = FieldType.FLOAT;
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
        directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_FLOAT);
        directory.setWriteRasters(img);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                img.setFirstPixelSample(x, y, dat.getFirstPixelSample(x,y));
            }
        }
        dat = null;

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);

        File outFolder = new File(path); outFolder.mkdirs();

        try{
            Thread.sleep(100);
            TiffWriter.writeTiff(new File(outFolder.getAbsolutePath() + "\\" + name + ".TIF"), tiffImage);
        } catch (OutOfMemoryError err) {
            log("Could not save " + name + ".TIF due to heap memory",DEBUG_IMPORTANT);
        } catch (InterruptedException ignored) {}

        tiffImage = null; directory = null; img = null; //may seem redundant but actually reduces heap memory somehow
    }

    public static void saveTIFF(int W, int H, String path, String name) throws IOException {
        Utils.log("Saving image " + name + " W:" + W + " H:" + H,Utils.DEBUG_DETAIL);
        int samplesPerPixel = 1;
        FieldType fieldType = FieldType.FLOAT;
        int bitsPerSample = fieldType.getBits();

        Rasters rasters = new Rasters(W, H, samplesPerPixel, fieldType);

        int rowsPerStrip = rasters.calculateRowsPerStrip(
                TiffConstants.PLANAR_CONFIGURATION_CHUNKY);

        FileDirectory directory = new FileDirectory();
        directory.setImageWidth(W);
        directory.setImageHeight(H);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(
                TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
        directory.setSamplesPerPixel(samplesPerPixel);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(
                TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_FLOAT);
        directory.setWriteRasters(rasters);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                rasters.setFirstPixelSample(x, y, 0);
            }
        }

        TIFFImage tiffImage = new TIFFImage();
        tiffImage.add(directory);
        File f = new File(path + "\\" + name + ".TIF");
        f.getParentFile().mkdirs();
        TiffWriter.writeTiff(f, tiffImage);
    }

    static int getFormat(Number n){
        if(n instanceof Integer || n instanceof Short || n instanceof Long){return TiffConstants.SAMPLE_FORMAT_SIGNED_INT;}
        else if(n instanceof Float || n instanceof Double){return TiffConstants.SAMPLE_FORMAT_FLOAT;}
        else{return TiffConstants.SAMPLE_FORMAT_UNDEFINED;}
    }

    public static JsonElement readJson(String path) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(path));
    }

    public static void clearTemp(){
        String dir = NeveAnalyitcs.CONFIG.get("temp_dir").getAsString();
        File f = new File(dir);
        for(File act : f.listFiles()){
            log("Deleting file " + act.getName(), DEBUG_DETAIL);
            if(!act.delete()){log("WARNING: COULD NOT DELETE " + act.getName(),DEBUG_IMPORTANT);}
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }

    public static void runScript(String[] cmd, File dir) throws IOException {
        Process genMetadata = Runtime.getRuntime().exec(
                cmd, null, dir
        );
        BufferedReader out = new BufferedReader(new InputStreamReader(genMetadata.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(genMetadata.getErrorStream()));

        while(genMetadata.isAlive()){
            if(out.ready()){
                log(out.readLine(), DEBUG_VERBOSE);
            }
            if(err.ready()) {
                log("Script error: " + err.readLine(), DEBUG_IMPORTANT);
            }
        }
    }

    public static void runScript(String cmd, File dir) throws IOException {
        log(cmd,DEBUG_TASKS);
        Process genMetadata = Runtime.getRuntime().exec(
                cmd, null, dir
        );
        BufferedReader out = new BufferedReader(new InputStreamReader(genMetadata.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(genMetadata.getErrorStream()));

        while(genMetadata.isAlive()){
            if(out.ready()){
                log(out.readLine(), DEBUG_VERBOSE);
            }
            if(err.ready()) {
                log("Script error: " + err.readLine(), DEBUG_IMPORTANT);
            }
        }
    }

    public static void debugImg(TIFFImage img){
        StringBuilder out = new StringBuilder();
        List<FileDirectory> fileDirectories = img.getFileDirectories();
        out.append("Debugging image with ").append(fileDirectories.size()).append(" rasters.\n");

        for (FileDirectory act : fileDirectories){
            Rasters raster = act.readRasters();
            out.append("\nRaster: x=").append(raster.getWidth()).append(" y=").append(raster.getHeight()).append("\n ");
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (int x = 0; x < raster.getWidth(); x++) {
                for (int y = 0; y < raster.getHeight(); y++) {
                    Number n = raster.getPixel(x,y)[0];
                    if(n.doubleValue()==0){continue;}
                    min = Math.min(min,n.doubleValue());
                    max = Math.max(max,n.doubleValue());
                }
            }
            out.append("min=").append(min).append(" max=").append(max).append("\n").append(" Entries: \n  ");
            Set<FileDirectoryEntry> entries = act.getEntries();
            for(FileDirectoryEntry entry : entries){
                out.append("Entry ").append(entry.getFieldTag().name()).append(": type=").append(entry.getFieldType().name()).append(" value=").append(entry.getValues()).append("\n  ");
            }
        }
        System.out.println(out);
    }

    public static File doRecursiveSearch(String fileName, File parentDir){
        File[] f = parentDir.listFiles();
        for(File a : f){
            if(a.isDirectory()){
                File r = doRecursiveSearch(fileName,a);
                if(r != null){return r;}
            } else {
                if(a.getName().equals(fileName)){
                    return a;
                }
            }
        }
        return null;
    }

    public static void copyGEOTIFFProperties(File from, File to) {
        log("Cloning geotiff properties: " + from.getName() + " -> " + to.getName(), DEBUG_TASKS);
        LandsatBand.removeFromCache(from);
        LandsatBand.removeFromCache(to);
        Dataset f = gdal.Open(from.getAbsolutePath(), gdalconst.GA_Update);
        Dataset t = gdal.Open(to.getAbsolutePath(), gdalconst.GA_Update);
        t.SetGeoTransform(f.GetGeoTransform());
        t.SetProjection(f.GetProjection());
        t.SetMetadata(f.GetMetadata_Dict());
        log("INFO:\n"+gdal.GDALInfo(t, new InfoOptions(new Vector<>(List.of("-json")))),DEBUG_VERBOSE);
        //f.Close(); t.Close();
        f.delete();
        t.delete();
        try {
            // Loading the GeoTIFF with NGATIFF may generate an acess violation exception if done just after
            // the closing of the output stream, therefore for safety reasons it is best to let the process
            // wait for a while before proceeding
            Thread.sleep(1000);
        } catch (InterruptedException g) {
            throw new RuntimeException(g);
        }
        File fromAux = Utils.doRecursiveSearch(from.getName()+".json",from.getParentFile());
        if(fromAux!=null && fromAux.delete()){
            Utils.log("Deleted previous data json file",Utils.DEBUG_TASKS);
        }
        fromAux = Utils.doRecursiveSearch(from.getName()+".aux.xml",from.getParentFile());
        if(fromAux!=null && fromAux.delete()){
            Utils.log("Deleted previous data xml file",Utils.DEBUG_TASKS);
        }

        File toAux = Utils.doRecursiveSearch(to.getName()+".json",to.getParentFile());
        if(toAux!=null && toAux.delete()){
            Utils.log("Deleted previous data json file",Utils.DEBUG_TASKS);
        }
        toAux = Utils.doRecursiveSearch(to.getName()+".aux.xml",to.getParentFile());
        if(toAux!=null && toAux.delete()){
            Utils.log("Deleted previous data xml file",Utils.DEBUG_TASKS);
        }
//        t.delete();
//        f.delete();
//        runScript(
//                NeveAnalyitcs.CONFIG.get("python_dir").getAsString() + " \"" +
//                        Constants.APP_PATH+"\\Metadata.py\" " + from.getAbsolutePath() + " " + to.getAbsolutePath(),
//                from.getParentFile()
//        );
    }

    public static void initGDAL(){
        if(isGDALInit){return;}
        log("INIT_GDAL",DEBUG_IMPORTANT);
        gdal.AllRegister();
        gdal.UseExceptions();
        log("GDAL VERSION INFO: " +gdal.VersionInfo(),DEBUG_IMPORTANT);
        isGDALInit = true;
    }

    public static float interpolate(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }
}
