package com.sixthsolution.apex.nlp.persian.calendar.tools;

import org.threeten.bp.LocalDate;

import java.util.Arrays;
import java.util.List;

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
    public JalaliCalendar plusDays(int count){
        JalaliCalendar jc;
        int jcday;
        List<Integer> days31 = Arrays.asList(1,3,5,7,8,10,12);
        List<Integer> days30 = Arrays.asList(4,6,9,11);
//        List<Integer> days28 = Arrays.asList(2);

        if (days31.contains(this.getMonth())){
            jcday=this.getDay()+(count%31);
            jc=this.plusMonth(count/31);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);

        }
        else if (days30.contains(this.getMonth())){
            jcday=this.getDay()+(count%30);
            jc=this.plusMonth(count/30);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);

        }
        else{
            jcday=this.getDay()+(count%28);
            jc=this.plusMonth(count/28);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);
        }
    }

    public JalaliCalendar plusMonth(int count){
        JalaliCalendar jc;
        int jcday;
        jcday=this.getMonth()+(count%12);
        jc=this.plusYear(count/12);
        return new JalaliCalendar(jc.getYear(),jcday,this.getDay());
    }
    public JalaliCalendar plusYear(int count){
        return new JalaliCalendar(this.getYear()+count,this.getMonth(),this.getDay());
    }
    public JalaliCalendar minusDays(int count){
        JalaliCalendar jc;
        int jcday;
        List<Integer> days31 = Arrays.asList(1,3,5,7,8,10,12);
        List<Integer> days30 = Arrays.asList(4,6,9,11);
//        List<Integer> days28 = Arrays.asList(2);

        if (days31.contains(this.getMonth())){
            jcday=this.getDay()-(count%31);
            jc=this.minusMonth(count/31);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);

        }
        else if (days30.contains(this.getMonth())){
            jcday=this.getDay()-(count%30);
            jc=this.minusMonth(count/30);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);

        }
        else{
            jcday=this.getDay()-(count%28);
            jc=this.minusMonth(count/28);
            return new JalaliCalendar(jc.getYear(),jc.getMonth(),jcday);
        }
    }

    public JalaliCalendar minusMonth(int count){
        JalaliCalendar jc;
        int jcday;
        jcday=this.getMonth()-(count%12);
        jc=this.minusYear(count/12);
        return new JalaliCalendar(jc.getYear(),jcday,this.getDay());
    }
    public JalaliCalendar minusYear(int count){
        return new JalaliCalendar(this.getYear()-count,this.getMonth(),this.getDay());
    }
}
