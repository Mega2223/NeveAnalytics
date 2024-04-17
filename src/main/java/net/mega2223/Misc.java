package net.mega2223;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Misc {
    public static String readFile (String path){

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder ret = new StringBuilder();
            String line = reader.readLine();
            while(line != null){
                ret.append(line).append("\n");
                line = reader.readLine();
            }
            return ret.toString();
        } catch (IOException e) {
            RuntimeException runtimeException = new RuntimeException("There is no directory such as " + path);
            e.printStackTrace();
            throw runtimeException;
        }

    }
}
