package net.mega2223;

import java.io.File;

public class Calibrator {

    boolean isTuned = false;
    public static final int MIN = 0, MAX = 1, INTERVAL = 2;
    private Calibrator(){}

    public static double[][] correct(int[][] data, double[] supposedBoundaries){
        int[] boundaries = getBoundaries(data);
        double[][] ret = new double[data.length][data[0].length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++) {
                if (data[i][j] == 0){continue;}
                ret[i][j] = supposedBoundaries[MIN] + supposedBoundaries[INTERVAL]*((data[i][j] - boundaries[MIN])/(double)boundaries[INTERVAL]);
            }
        }
        return ret;
    }

    public static double[] findSupposedBoundaries(File file){
        String[] dat = Misc.readFile(file.getAbsolutePath() + ".aux.xml").split("\n");

        int b = dat[4].indexOf(">")+1, e = dat[4].lastIndexOf("<");
        double max = Double.parseDouble(dat[4].substring(b,e));

        b = dat[6].indexOf(">")+1; e = dat[6].lastIndexOf("<");
        double min = Double.parseDouble(dat[6].substring(b,e));

        return new double[]{min,max,max-min};
        //4 min 6 max
    }

    public static int[] getBoundaries(int[][] data){
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int act = data[i][j];
                if (act == 0){continue;}
                max = Math.max(max,act);
                min = Math.min(min,act);
            }
        }
        return new int[]{min,max,max-min};
    }

}
