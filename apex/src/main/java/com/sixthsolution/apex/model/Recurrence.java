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

import java.util.List;

/**
 * Represents a recurring event.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class Recurrence {

    private Frequency frequency = Frequency.DAILY;

    /**
     * Specifies how often the event should be repeated.
     */
    private int interval = 1;

    /**
     * The date or date-time until which the event should be repeated.
     */
    private LocalDateTime until = null;

    private boolean forever = false;
    /**
     * Days of the week on which the event should be repeated
     */
    private List<WeekDay> byDays;

    public Recurrence(Frequency frequency, int interval, LocalDateTime until, boolean forever,
                      List<WeekDay> byDays) {
        this.frequency = frequency;
        this.interval = interval;
        this.until = until;
        this.forever = forever;
        this.byDays = byDays;
    }

    public Frequency frequency() {
        return frequency;
    }

    public int interval() {
        return interval;
    }

    //TODO @nullable
    public LocalDateTime until() {
        return until;
    }

    public boolean isForever() {
        return forever;
    }

    public List<WeekDay> byDays() {
        return byDays;
    }

    @Override
    public String toString() {
        return "Recurrence{" +
                "frequency=" + frequency +
                ", interval=" + interval +
                ", until=" + until +
                ", forever=" + forever +
                ", byDays=" + byDays +
                '}';
    }
}
