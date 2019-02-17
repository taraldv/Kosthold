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
public class Exer {

    private final String type;
    private final double kg;
    private double percent;

    public Exer(String type, double kg) {
        this.type = type;
        this.kg = kg;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getType() {
        return type;
    }

    public double getKg() {
        return kg;
    }

  
}
