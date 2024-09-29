package net.mega2223.neveanalytics.objects;

public class TimePeriod {
    final long start, end;
    public TimePeriod(long startEpoch, long endEpoch){
        this.start = startEpoch;
        this.end = endEpoch;
    }

    public boolean isFromPerdiod(int time){
        return time >= start && time <= end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
