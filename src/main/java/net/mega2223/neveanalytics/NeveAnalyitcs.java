package net.mega2223.neveanalytics;

import com.google.gson.JsonObject;

public class NeveAnalyitcs {

    public static final JsonObject CONFIG = Constants.PROPERTIES;
    
    public static int currentArg = 0;
    
    public static void main(String[] args)  {
        Utils.clearTemp();
        while(currentArg < args.length){

            currentArg++;
        }
    }
}
