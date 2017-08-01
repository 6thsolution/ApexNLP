package com.sixthsolution.apex.nlp.persian.event;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.event.EventBuilder;
import com.sixthsolution.apex.nlp.persian.calendar.tools.JalaliCalendar;
import com.sixthsolution.apex.nlp.persian.model.PersianEvent;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;


/**
 * Created by rozhin on 8/1/2017.
 */
public class PersianEventBuilder extends EventBuilder {
    private LocalTime startTime = null;
    private LocalTime endTime = null;
    private JalaliCalendar startDate;
    private JalaliCalendar endDate;
    private String location = "";
    private PersianRecurrence recurrence =null;

    public void setStartDate(JalaliCalendar startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(JalaliCalendar endDate) {
        this.endDate = endDate;
    }

    public void setRecurrence(PersianRecurrence recurrence){this.recurrence=recurrence;}

    @Override
    public Event build(LocalDateTime source) {
        JalaliCalendar jalaliCalendar=new JalaliCalendar();
        jalaliCalendar=jalaliCalendar.convertor(source.toLocalDate());

        if (startTime == null) {
            startTime = source.toLocalTime();
        }
        if (endTime == null) {
            endTime = startTime.plusHours(1);
        }
        if (startDate == null) {
            startDate = jalaliCalendar;
        }
        if (endDate == null) {
            endDate = jalaliCalendar;
        }



        return new PersianEvent("", location, startDate, endDate,endTime,startTime, false, recurrence);
    }
}
