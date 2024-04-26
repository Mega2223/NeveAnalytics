package net.mega2223.neveanalytics.standalonescripts;

import net.mega2223.neveanalytics.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/***/
public class FileSorter {
    static final List<String> validFormats = List.of("TIF","json","xml","txt");

    public static void main(String[] args) throws IOException {
        File root = new File(Constants.DATA_PATH);
        File[] files = root.listFiles();
        String[][] sep = new String[files.length][2];
        for (int i = 0; i < files.length; i++) {
            File act = files[i];
            String[] name = act.getName().split("\\.");
            if(name.length < 7){continue;}
            String format = name[name.length-1];
            if (validFormats.contains(format)){
                name = name[0].split("_");
                //landsat|inutil|localizacao|data|inutil|inutil|inutil|banda
                String sat = name[0], loc = name[2], dat = name[3], band = name[7];
                File f = new File(Constants.DATA_PATH+"\\Unprocessed\\"+dat+"\\"+loc+"\\"+sat);
                if(!f.exists()){f.mkdirs();}
                sep[i][0] = act.getAbsolutePath();
                sep[i][1] = f.getAbsolutePath()+"\\"+act.getName();
                if(!new File(sep[i][1]).exists()){
                    Files.copy(Path.of(sep[i][0]),Path.of(sep[i][1]));
                    if(new File(sep[i][1]).exists()){act.deleteOnExit();} else {
                        System.out.println("Failed to create file " + f.getAbsolutePath() + "\\" + act.getName());
                    }
                }
                System.out.println(sep[i][0] + " -> " + sep[i][1]);

            }
        }

    }
}