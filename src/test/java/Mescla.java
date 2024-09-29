import net.mega2223.neveanalytics.BandManager;
import net.mega2223.neveanalytics.Utils;
import net.mega2223.neveanalytics.objects.LandsatBand;
import net.mega2223.neveanalytics.objects.LandsatPicture;
import net.mega2223.neveanalytics.objects.TimePeriod;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

public class Mescla {
    public static void main(String[] args) throws IOException {
        List<TimePeriod> p = new ArrayList<>();

        for (int y = 1980; y < 2025; y++) {
            for (int m = 1; m <= 12; m+=4) {
                String mm = String.valueOf(m);
                mm = mm.length() < 10 ? "0"+mm : mm;
                Instant instant = Instant.parse(y + "-" + mm + "-01T00:00:00.00Z");
                long beg = instant.toEpochMilli();
                long end = instant.plus(30, ChronoUnit.DAYS).toEpochMilli();
                //System.out.println(Instant.ofEpochMilli(end));
                p.add(new TimePeriod(beg,end));
            }
        }
        System.out.println(":)\n\n");

        Utils.DEBUG_LEVEL = Utils.DEBUG_VERBOSE;
        List<LandsatPicture<? extends Number>> images = LandsatPicture.scanFolder("C:\\Users\\Imperiums\\Desktop\\temp\\dest");

        for(TimePeriod time : p){
            System.out.println("Going for TimePerdiod " + Instant.ofEpochMilli(time.getStart()) + " - " + Instant.ofEpochMilli(time.getEnd()));
            List<LandsatBand<? extends Number>> current = new ArrayList<>();
            for (LandsatPicture<?> actPic : images){
                LandsatBand<?> band = actPic.getBands().get(0);
                long bandTime = band.getTimeEpoch();
                if(bandTime >= time.getStart() && bandTime <= time.getEnd()){
                    System.out.println("Band " + band.name + "found");
                    current.add(band);
                }
            }
            LandsatBand<?>[] bandArray = new LandsatBand[current.size()];
            current.toArray(bandArray);
            System.out.println("buffering");
            for (int i = 0; i < bandArray.length; i++) {
                bandArray[i].bufferImage();
            }
            System.out.println("bufffered");
            System.out.println(BandManager.getAverage(bandArray));
            LandsatBand.clearCache();
        }

    }
}
