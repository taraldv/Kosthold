/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.gson.Gson;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.TreeSet;
import util.Day;
import util.Exer;
import util.Week;
import util.Year;

/**
 *
 * @author Tarald
 */
public class Kalender {
    private final static HashSet<String> uniqueExer = new HashSet<>();
    private final static HashSet<Integer> uniqueYear = new HashSet<>();

    public static String getJSON(ResultSet rset) throws Exception {
        Day[] arr = parseSql(rset);
        addWeight(arr);
        Year[] years = createFinal(arr);
        calcWeeklyPercent(years);
        return new Gson().toJson(years);

    }

    public static Day[] dayTesting(ResultSet rset) throws Exception {
        Day[] arr = parseSql(rset);
        addWeight(arr);
        return arr;
    }

    public static Year[] yearTest(ResultSet rset) throws Exception {
        Day[] arr = parseSql(rset);
        addWeight(arr);
        Year[] years = createFinal(arr);
        return years;

    }

    private static void calcWeeklyPercent(Year[] arr){
        for (Year year : arr) {
            for (Week week : year.getWeeks()) {
                week.setPercent();
            }
        }
    }
    
    private static Year[] createFinal(Day[] arr) {
        Year[] output = getFourLastYears();
        for (Day day : arr) {
            int tempYear = day.getCalendar().get(Calendar.YEAR);
            int tempWeek = day.getCalendar().get(Calendar.WEEK_OF_YEAR) - 1;
            int tempDayOfWeek = day.getCalendar().get(Calendar.DAY_OF_WEEK) - 2;

            if (tempDayOfWeek < 0) {
                tempDayOfWeek = 6;
            }

            for (int i = 0; i < output.length; i++) {
                if (output[i].getYear() == tempYear) {
                    output[i].getWeeks()[tempWeek].getDays()[tempDayOfWeek] = day;
                }
            }
        }
        return output;
    }

    private static Year[] getUniqueYears() {
        TreeSet<Year> output = new TreeSet<>();
        for (Integer integer : uniqueYear) {
            output.add(new Year(integer));
        }
        return output.toArray(new Year[0]);
    }

    private static Year[] getFourLastYears(){
        int i = Calendar.getInstance().get(Calendar.YEAR);
        Year[] years = new Year[4];
        int x = 0;
        for (Year year : years) {
            years[x]=new Year(i-x);
            x++;
        }
        return years;
    }
    
    private static Day[] parseSql(ResultSet rset) throws Exception {
        ArrayList<Day> output = new ArrayList<>();
        Calendar calendar = new GregorianCalendar(0, 0, 0);
        ArrayList<Exer> list = new ArrayList<>();
        while (rset.next()) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.setFirstDayOfWeek(Calendar.MONDAY);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tempCal.setTime(sdf.parse(rset.getString("dato")));
            String type = rset.getString("excercise");
            uniqueExer.add(type);
            uniqueYear.add(tempCal.get(Calendar.YEAR));
            double kg = rset.getDouble("avg_weight");
            if (!tempCal.equals(calendar)) {
                output.add(new Day(calendar, list.toArray(new Exer[0])));
                list = new ArrayList<>();
                calendar = tempCal;
            }
            list.add(new Exer(type, kg));

        }
        output.remove(0);
        output.add(new Day(calendar, list.toArray(new Exer[0])));
        return output.toArray(new Day[1]);
    }

    private static void addWeight(Day[] arr) {
        for (String string : uniqueExer) {
            double max = 0;
            for (Day dag : arr) {
                for (Exer ovelse : dag.getExers()) {
                    if (ovelse.getType().equals(string)) {
                        double kg = ovelse.getKg();
                        double percent;
                        if (kg > max) {
                            max = kg;
                        }
                        if (max == 0) {
                            percent = 1;
                        } else {
                            percent = (kg / max);
                        }
                        ovelse.setPercent(percent);
                    }
                }
            }
        }
    }
}
