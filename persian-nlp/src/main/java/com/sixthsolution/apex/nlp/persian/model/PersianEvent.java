package com.sixthsolution.apex.nlp.persian.model;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.model.Recurrence;
import com.sixthsolution.apex.nlp.persian.calendar.tools.JalaliCalendar;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

/**
 * Created by rozhin on 8/1/2017.
 */
public class PersianEvent extends Event{

    private String title = "";
    private String location = "";
    private JalaliCalendar startDate = null;
    private JalaliCalendar endDate= null;
    private LocalTime startTime=null;
    private LocalTime endTime=null;
    private boolean isAllDay = false;
    private PersianRecurrence recurrence = null;


    public PersianEvent(String title, String location, JalaliCalendar startDateTime,
                 JalaliCalendar endDateTime,LocalTime endtime,LocalTime starttime,
                 boolean isAllDay, PersianRecurrence recurrence) {
        this.title = title;
        this.location = location;
        this.startDate = startDateTime;
        this.endDate = endDateTime;
        this.endTime=endtime;
        this.startTime=starttime;
        this.isAllDay = isAllDay;
        this.recurrence = recurrence;
    }


    public JalaliCalendar jalaliStart() {
        return startDate;
    }

    public JalaliCalendar jalaliEnd() {
        return endDate;
    }
    public LocalTime jalaliTimeStart(){return startTime;}
    public LocalTime jalaliTimeEnd(){return endTime;}
    public PersianRecurrence persianRecurrence(){return persianRecurrence();}

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDateTime=" + endDate +
                ",startTime="+startTime+
                ",endTime="+endTime+
                ", isAllDay=" + isAllDay +
                ", recurrence=" + recurrence +
                '}';
    }


}
