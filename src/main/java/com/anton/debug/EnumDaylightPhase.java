package com.anton.debug;

public enum EnumDaylightPhase {
    DAY(3000, 9000),
    EVENING(9000, 15000),
    NIGHT(15000, 21000),
    MORNING(21000, 3000);

    public int start;
    public int end;

    EnumDaylightPhase(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
