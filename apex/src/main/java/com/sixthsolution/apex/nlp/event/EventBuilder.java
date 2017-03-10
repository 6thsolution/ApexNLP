package com.sixthsolution.apex.nlp.event;

import com.sixthsolution.apex.model.Event;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class EventBuilder {

    private LocalTime startTime = null;
    private LocalTime endTime = null;

    public Event build(LocalDateTime source) {
        if (startTime == null) {
            startTime = source.toLocalTime();
        }
        if (endTime == null) {
            endTime = startTime.plusHours(1);
        }
        LocalDateTime startDateTime = LocalDateTime.of(source.toLocalDate(), startTime);
        LocalDateTime endDateTime = LocalDateTime.of(source.toLocalDate(), endTime);

        return new Event("", startDateTime, endDateTime, false, null);
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


}