package net.mega2223;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Converter {

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String IMG_PATH = USER_DIR + "\\img\\input";
    public static final int DEBUG_NOTHING = -1, SHOW_OUTPUT_ONLY = 0, DEBUG_RELEVANT = 1;

    public static int debugLevel = 0;
    public static final int[] VALID_BANDS = new int[]{5, 7};
    public static ArrayList<LandsatImage> images = new ArrayList<>(20);

    public static void main(String[] args) {
        File inputPath = new File(IMG_PATH);
        log("Compiled at: " + Date.from(Instant.now()) + "\n");
        crawl(inputPath);
    }

    public static void calculate(){}

    public static void crawl(File sourceRoot){
        File[] files = sourceRoot.listFiles();
        if(files == null){
            log("Could not access source root at " + sourceRoot.getPath());
            return;
        }
        for (File act : files){
            String name = act.getName();
            if(act.isDirectory()){
                crawl(act);
            } else if (name.substring(name.length()-3).equalsIgnoreCase("tif")) {
                try {
                    genImageStruct(act);}
                catch (IOException e) {throw new RuntimeException(e);}
            }
        }
    }
    public static void genImageStruct(File imageLoc) throws IOException {
        boolean isValid = false;
        for (int i = 0; i < VALID_BANDS.length && !isValid; i++) {
            isValid = imageLoc.getName().contains("B"+VALID_BANDS[i]);
        }
        if(isValid){images.add(new LandsatImage(imageLoc));} else {
            log("Skipping file " + imageLoc.getName() + "\n");
        }
    }
    private static final StringBuilder finalReport = new StringBuilder();
    public static void log(String msg){
        log(msg,0);
    }
    public static void log(String msg, int level){
        if (level == SHOW_OUTPUT_ONLY){finalReport.append(msg);}
        if(debugLevel <= level){
            System.out.println(msg);
        }
    }

}