package net.mega2223.neveanalytics;

import net.mega2223.neveanalytics.standalonescripts.FileRenamer;
import net.mega2223.neveanalytics.standalonescripts.FileSorter;
import net.mega2223.neveanalytics.standalonescripts.NDSICalculator;
import net.mega2223.neveanalytics.standalonescripts.StatsReportGenerator;

import java.io.*;

public class NeveAnalytics {
    public static int currentArg = 0;
    public static void main(String[] args) throws IOException {
        for (; currentArg < args.length; currentArg++) {
            String act = args[currentArg];
            switch (act){
                case "-sort":
                    FileSorter.main(args);
                    break;
                case "--clean-names":
                    FileRenamer.main(args);
                    break;
                case "--perform-raster-equations":
                    NDSICalculator.main(args);
                    break;
                case "--data-path":
                    currentArg++;
                    Constants.DATA_PATH = args[currentArg];
                    break;
                case "--save-png":
                    NDSICalculator.savePNG = true;
                    break;
                case "--snow-threshold":
                    currentArg++;
                    StatsReportGenerator.SNOW_THRESHOLD = Float.parseFloat(args[currentArg]);
                    break;
                case "--gen-report":
                    StatsReportGenerator.main(args);
                    break;
                case "--gen-metadata":
                    Constants.runtime.exec("INFO.bat",null,new File(Constants.DATA_PATH));
                    break;
                case "--extract-metadata":
                    Process metadataExtract = Constants.runtime.exec(new String[]{Constants.PROPERTIES.get("python_dir").getAsString(),Constants.APP_PATH+"\\Metadata.py"});
                    BufferedReader out = new BufferedReader(new InputStreamReader(metadataExtract.getInputStream()));
                    BufferedReader err = new BufferedReader(new InputStreamReader(metadataExtract.getErrorStream()));
                    String b;

                    while (metadataExtract.isAlive()){
                        if(out.ready()){System.out.println(out.readLine());}
                        if(err.ready()){err.readLine();}
                    }
                    break;
                case "--plot-graph":
                    Process plot = Constants.runtime.exec(new String[]{Constants.PROPERTIES.get("python_dir").getAsString(), Constants.APP_PATH + "\\Plot.py"});
                    while (plot.isAlive()){
                        try {Thread.sleep(100);}catch (InterruptedException ignored){}
                    }
                    break;

            }
        }
    }
}
