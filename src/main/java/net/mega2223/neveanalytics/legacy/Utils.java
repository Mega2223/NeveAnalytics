package net.mega2223.neveanalytics.legacy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
    public static void saveFile(String data, String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){file.createNewFile();}
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
        writer.write(data);
        writer.close();
    }
}
