package com.fiskkit.util.frequency;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by joshuaellinger on 4/6/15.
 */
public class Frequency {
	private static final Logger LOGGER = Logger.getLogger("");
    
    protected int frequencyId = 0;
    protected int startOffset = 0;
    protected int finishOffset = 0;
    protected TimeZone timezone;
    protected String name = "Never";
    protected final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected Calendar start = calendarInit(Calendar.getInstance());
    protected Calendar finish = calendarInit(Calendar.getInstance());

    private Calendar calendarInit(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public Frequency(){
        this.timezone = TimeZone.getTimeZone("America/Los_Angeles");
    }

    public int getId() {
        return frequencyId;
    }

    public final int getStartOffset() {
        return startOffset;
    }

    public final int getFinishOffset() {
        return finishOffset;
    }

    public final String getName() {
        return name;
    }

    public String getFormattedStart(){
        return dateFormat.format(start.getTime());
    }

    public Calendar getFinish(){
        return finish;
    }

    public String getFormattedFinish(){
        return dateFormat.format(finish.getTime());
    }

    public static DateFormat getDateFormat(){
        return dateFormat;
    }

    public int getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(int frequencyId) {
        this.frequencyId = frequencyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        this.start.add(Calendar.DATE, getStartOffset());
        int offset = timezone.getOffset(start.getTimeInMillis());
        this.start.add(Calendar.MILLISECOND, -offset);
        LOGGER.log(Level.FINE, "start date =" + getFormattedStart());
    }

    public void setFinishOffset(int finishOffset) {
        this.finishOffset = finishOffset;
        this.finish.add(Calendar.DATE, getFinishOffset());
        int offset = timezone.getOffset(finish.getTimeInMillis());
        this.finish.add(Calendar.MILLISECOND, -offset);
        LOGGER.log(Level.FINE, "finish date =" + getFormattedFinish());
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public void setFinish(Calendar finish) {
        this.finish = finish;
    }
}
