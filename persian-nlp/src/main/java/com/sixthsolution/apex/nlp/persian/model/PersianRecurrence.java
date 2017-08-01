package com.sixthsolution.apex.nlp.persian.model;

/**
 * Created by rozhin on 8/1/2017.
 */
import com.sixthsolution.apex.model.Frequency;
import com.sixthsolution.apex.model.WeekDay;
import com.sixthsolution.apex.nlp.persian.calendar.tools.JalaliCalendar;

import java.util.List;

/**
 * Represents a recurring event.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 * @author Rozhin Bayati
 */
public class PersianRecurrence {

    private Frequency frequency = Frequency.DAILY;

    /**
     * Specifies how often the event should be repeated.
     */
    private int interval = 1;

    /**
     * The date or date-time until which the event should be repeated.
     */
    private JalaliCalendar until = null;

    private boolean forever = false;
    /**
     * Days of the week on which the event should be repeated
     */
    private List<WeekDay> byDays;

    public PersianRecurrence(Frequency frequency, int interval,JalaliCalendar until, boolean forever,
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
    public JalaliCalendar until() {
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

