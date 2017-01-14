package com.fiskkit.util.frequency;


/**
 * Created by joshuaellinger on 4/6/15.
 */
public class DailyFrequency extends Frequency {

    public DailyFrequency() {
        super();
        setFrequencyId(1);
        setStartOffset(-1); // used to be -1, -2 (48 hours) seems to work better
        setFinishOffset(0);
        setName("Daily");
    }

}
