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
                    System.out.println("Set save-png to true");
                    NDSICalculator.savePNG = true;
                    break;
                case "--snow-threshold":
                    currentArg++;
                    System.out.println("Set snow threshold to " + args[currentArg]);
                    StatsReportGenerator.SNOW_THRESHOLD = Float.parseFloat(args[currentArg]);
                    break;
                case "--gen-report":
                    StatsReportGenerator.main(args);
                    break;
                case "--gen-src-metadata":
                    System.out.println("Generating metadata for source files");
                    Process genMetadata = Constants.runtime.exec(new String[]{Constants.APP_PATH+"\\INFO.bat\\"},null,new File(Constants.DATA_PATH));
                    BufferedReader out = new BufferedReader(new InputStreamReader(genMetadata.getInputStream()));
                    BufferedReader err = new BufferedReader(new InputStreamReader(genMetadata.getErrorStream()));
                    while(genMetadata.isAlive()){
                        if(out.ready()){System.out.println(out.readLine());}
                        if(err.ready()){err.readLine();}
                    }
                    break;
                case "--gen-out-metadata":
                    System.out.println("Generating metadata for output files");
                    Process outMetadata = Constants.runtime.exec(new String[]{Constants.APP_PATH+"\\INFO.bat\\"},null,new File(Constants.DATA_PATH+"\\NDSI"));
                    BufferedReader outM = new BufferedReader(new InputStreamReader(outMetadata.getInputStream()));
                    BufferedReader errM = new BufferedReader(new InputStreamReader(outMetadata.getErrorStream()));
                    while(outMetadata.isAlive()){
                        if(outM.ready()){System.out.println(outM.readLine());}
                        if(errM.ready()){errM.readLine();}
                    }
                    break;
                case "--extract-metadata":
                    System.out.println("Extracting metadata to new files");
                    Process metadataExtract = Constants.runtime.exec(new String[]{Constants.PROPERTIES.get("python_dir").getAsString(),Constants.APP_PATH+"\\Metadata.py"});
                    BufferedReader outE = new BufferedReader(new InputStreamReader(metadataExtract.getInputStream()));
                    BufferedReader errE = new BufferedReader(new InputStreamReader(metadataExtract.getErrorStream()));

                    while (metadataExtract.isAlive()){
                        if(outE.ready()){System.out.println(outE.readLine());}
                        if(errE.ready()){errE.readLine();}
                    }
                    break;
                case "--plot-graph":
                    System.out.println("Plotting graph...");
                    Process plot = Constants.runtime.exec(new String[]{Constants.PROPERTIES.get("python_dir").getAsString(), Constants.APP_PATH + "\\Plot.py"});
                    while (plot.isAlive()){
                        try {Thread.sleep(100);}catch (InterruptedException ignored){}
                    }
                    break;

            }
        }
    }
}
