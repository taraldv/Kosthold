/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Calendar;

/**
 *
 * @author Tarald
 */
public class Day implements Comparable<Day>{
    private final Calendar calendar;
    private final Exer[] exers;

    public Day(Calendar calendar, Exer[] ovelseArr) {
        this.calendar = calendar;
        this.exers = ovelseArr;
    }

    @Override
    public int compareTo(Day o) {
        return this.calendar.compareTo(o.calendar);
    }

    
    
    public Calendar getCalendar() {
        return calendar;
    }

    public Exer[] getExers() {
        return exers;
    }
    
}
