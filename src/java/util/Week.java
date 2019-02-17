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
public class Week implements Comparable<Week> {

    private final int week;
    private final Day[] days = new Day[7];
    private double percent;

    public int getWeek() {
        return week;
    }

    @Override
    public int compareTo(Week o) {
        return Integer.compare(this.week, o.week);
    }

    public Day[] getDays() {
        return days;
    }

    public Week(int week) {
        this.week = week;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent() {
        int count = 0;
        double sum = 0;
        for (Day day : days) {
            if (day != null && day.getExers() != null) {
                for (Exer exer : day.getExers()) {
                    double p = exer.getPercent();
                    sum += exer.getPercent();
                    count++;
                }
            }
        }
        if (count > 0) {
            percent = sum / count;
        }
    }
}
