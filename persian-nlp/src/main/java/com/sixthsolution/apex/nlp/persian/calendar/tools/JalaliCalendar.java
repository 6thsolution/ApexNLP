package com.sixthsolution.apex.nlp.persian.calendar.tools;

import org.threeten.bp.LocalDate;

/**
 * Created by rozhin on 8/1/2017.
 */
public class JalaliCalendar {
    private int day;
    private int month;
    private int year;
    private int dayofweek;

    public JalaliCalendar(){

    }
    public JalaliCalendar(int year,int month,int day){
        this.day=day;
        this.month=month;
        this.year=year;
    }

    public int getDay(){
        return day;
    }
    public int getMonth(){
        return month;
    }
    public int getYear(){
        return year;
    }
    public int getDayofweek(){
        return dayofweek;
    }

    public JalaliCalendar convertor(LocalDate localDate){
        CalendarTool calendarTool= new CalendarTool(localDate.getYear(),localDate.getMonth().getValue(),localDate.getDayOfMonth());
        int day=calendarTool.getIranianDay();
        int month=calendarTool.getIranianMonth();
        int year = calendarTool.getIranianYear();
        return new JalaliCalendar(year,month,day);
    }
}
