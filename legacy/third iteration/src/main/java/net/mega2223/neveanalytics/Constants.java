package net.mega2223.neveanalytics;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;

public class Constants {
    private Constants(){}

    public static String APP_PATH = System.getProperty("user.dir");
    public static String DATA_PATH;
    public static JsonObject PROPERTIES;

    public static Runtime runtime = Runtime.getRuntime();

    static {
        try {
            PROPERTIES = (JsonObject) Utils.readJson(APP_PATH+"\\Configs.json");
            DATA_PATH = PROPERTIES.get("sources_dir").getAsString();
        } catch (FileNotFoundException e) {
            System.out.println("No Configs.json file found, shutting down.");
            throw new RuntimeException(e);
        }
    }

    public static int NO_DATA = 0;
    public static final double NO_DATA_DOUBLE = -2;
    public static final double ZERO_DIV = -399.253D;
}
