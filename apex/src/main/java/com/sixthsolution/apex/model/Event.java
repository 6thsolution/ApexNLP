/*
 * Copyright 2016 6thSolution
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sixthsolution.apex.model;

import org.threeten.bp.LocalDateTime;

/**
 * Represents a single calendar event.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 * @author Rozhin Bayati
 */
public class Event {

    private String title = "";
    private String location = "";
    private LocalDateTime startDateTime = null;
    private LocalDateTime endDateTime = null;
    private boolean isAllDay = false;
    private Recurrence recurrence = null;

    public Event() {

    }

    public Event(String title, String location, LocalDateTime startDateTime,
                 LocalDateTime endDateTime,
                 boolean isAllDay, Recurrence recurrence) {
        this.title = title;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isAllDay = isAllDay;
        this.recurrence = recurrence;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String title() {
        return title;
    }

    public String location() {
        return location;
    }

    public LocalDateTime start() {
        return startDateTime;
    }

    public LocalDateTime end() {
        return endDateTime;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public boolean isRecurrence() {
        return recurrence != null;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public Recurrence recurrence() {
        return recurrence;
    }


    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", isAllDay=" + isAllDay +
                ", recurrence=" + recurrence +
                '}';
    }

}

