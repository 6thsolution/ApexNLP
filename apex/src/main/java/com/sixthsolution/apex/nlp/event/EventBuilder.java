package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.model.Event;

import com.sixthsolution.apex.model.Recurrence;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */

public class EventBuilder {

    private LocalTime startTime = null;
    private LocalTime endTime = null;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location = "";
    private Recurrence recurrence =null;

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReccurence(Recurrence reccurence ){this.recurrence=reccurence;}

    public Event build(LocalDateTime source) {
        if (startTime == null) {
            startTime = source.toLocalTime();
        }
        if (endTime == null) {
            endTime = startTime.plusHours(1);
        }
        if (startDate == null) {
            startDate = source.toLocalDate();
        }
        if (endDate == null) {
            endDate = startDate;
        }


        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        return new Event("", location, startDateTime, endDateTime, false, recurrence);
    }

}