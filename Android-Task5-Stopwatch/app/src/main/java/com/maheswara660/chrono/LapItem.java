package com.maheswara660.chrono;

import java.util.Locale;

public class LapItem {
    private final int lapNumber;
    private final long splitTimeMillis;
    private final long totalTimeMillis;

    public LapItem(int lapNumber, long splitTimeMillis, long totalTimeMillis) {
        this.lapNumber = lapNumber;
        this.splitTimeMillis = splitTimeMillis;
        this.totalTimeMillis = totalTimeMillis;
    }

    public int getLapNumber() {
        return lapNumber;
    }

    public long getSplitTimeMillis() {
        return splitTimeMillis;
    }

    public long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    public String getLapNumberFormatted() {
        return String.format(Locale.US, "Lap %02d", lapNumber);
    }

    public String getSplitTimeFormatted() {
        return "+" + formatMillis(splitTimeMillis);
    }

    public String getTotalTimeFormatted() {
        return formatMillis(totalTimeMillis);
    }

    public static String formatMillis(long timeMillis) {
        long minutes = (timeMillis / 1000) / 60;
        long seconds = (timeMillis / 1000) % 60;
        long millis = (timeMillis % 1000) / 10; // 2 digit milliseconds (hundredths of a second)

        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, millis);
    }
}
