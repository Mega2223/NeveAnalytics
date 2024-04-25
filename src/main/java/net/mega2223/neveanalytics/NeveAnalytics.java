package net.mega2223.neveanalytics;

import net.mega2223.neveanalytics.standalonescripts.FileRenamer;
import net.mega2223.neveanalytics.standalonescripts.FileSorter;
import net.mega2223.neveanalytics.standalonescripts.NDSICalculator;
import net.mega2223.neveanalytics.standalonescripts.StatsReportGenerator;

import java.io.IOException;

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
            }
        }
    }
}
