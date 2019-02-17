/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Tarald
 */
public class Year implements Comparable<Year> {

    private final int year;
    private final Week[] weeks = new Week[52];

    public Year(int year) {
        this.year = year;
        for (int i = 0; i < weeks.length; i++) {
            weeks[i] = new Week(i+1);
        }
    }

    @Override
    public int compareTo(Year o) {
        return Integer.compare(this.year, o.year);
    }

    public int getYear() {
        return year;
    }

    public Week[] getWeeks() {
        return weeks;
    }

}
