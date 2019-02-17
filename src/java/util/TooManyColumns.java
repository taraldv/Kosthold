/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author Tarald
 */
public class TooManyColumns {

    private String query;
    private final ArrayList<Double> list = new ArrayList<>();
    private final String[][] arr;
    private final String[] columns = {
        "Spiselig del",
        "Vann",
        "Kilojoule",
        "Kilokalorier",
        "Fett",
        "Mettet",
        "C12:0",
        "C14:0",
        "C16:0",
        "C18:0",
        "Trans",
        "Enumettet",
        "C16:1 sum",
        "C18:1 sum",
        "Flerumettet",
        "C18:2n-6",
        "C18:3n-3",
        "C20:3n-3",
        "C20:3n-6",
        "C20:4n-3",
        "C20:4n-6",
        "C20:5n-3 (EPA)",
        "C22:5n-3 (DPA)",
        "C22:6n-3 (DHA)",
        "Omega-3",
        "Omega-6",
        "Kolesterol",
        "Karbohydrat",
        "Stivelse",
        "Mono+disakk",
        "Sukker, tilsatt",
        "Kostfiber",
        "Protein",
        "Salt",
        "Alkohol",
        "Vitamin A",
        "Retinol",
        "Beta-karoten",
        "Vitamin D",
        "Vitamin E",
        "Tiamin",
        "Riboflavin",
        "Niacin",
        "Vitamin B6",
        "Folat",
        "Vitamin B12",
        "Vitamin C",
        "Kalsium",
        "Jern",
        "Natrium",
        "Kalium",
        "Magnesium",
        "Sink",
        "Selen",
        "Kopper",
        "Fosfor",
        "Jod"};

    public TooManyColumns(String[][] arr) {
        this.arr = arr;
        generateTableColumns();
    }

    private void generateTableColumns() {
        query = "INSERT INTO matvaretabellen(matvare,brukerId";
        String values = ") VALUES (?,?";
        for (int i = 0; i < columns.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                String matvareNavn = arr[j][0];
                String matvareMengde = arr[j][1];
                if (!matvareNavn.equals("0") && !matvareMengde.equals("0") && columns[i].equals(matvareNavn)) {
                    values += ",?";
                    query += ",`" + columns[i] + "`";
                    list.add(Double.parseDouble(matvareMengde));
                }
            }
        }
        query += values + ");";
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<Double> getList() {
        return list;
    }

}
