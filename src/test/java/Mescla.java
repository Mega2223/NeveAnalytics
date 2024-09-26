import net.mega2223.neveanalytics.objects.TimePeriod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

public class Mescla {
    public static void main(String[] args) {
        List<TimePeriod> p = new ArrayList<>();

        for (int y = 1980; y < 2025; y++) {
            for (int m = 1; m <= 12; m+=4) {
                String mm = String.valueOf(m);
                mm = mm.length() < 10 ? "0"+mm : mm;
                Instant instant = Instant.parse(y + "-" + mm + "-01T00:00:00.00Z");
                long beg = instant.toEpochMilli();
                long end = instant.plus(30, ChronoUnit.DAYS).toEpochMilli();
                System.out.println(Instant.ofEpochMilli(end));
                p.add(new TimePeriod(beg,end));
            }
        }
        System.out.println(":)");
    }
}
