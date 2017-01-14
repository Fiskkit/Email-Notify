package com.fiskkit.util.frequency;


/**
 * Created by joshuaellinger on 4/6/15.
 */
public class WeeklyFrequency extends Frequency {

    public WeeklyFrequency() {
        super();
        setFrequencyId(2);
        setStartOffset(-7);
        setFinishOffset(0);
        setName("Weekly");
    }
}
