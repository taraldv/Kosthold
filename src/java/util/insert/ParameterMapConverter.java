/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.insert;

import java.util.Map;

/**
 *
 * @author Tarald
 */
public class ParameterMap {

    public static String[][] convertMapToArray(Map<String, String[]> map, int offset) {

        String[][] out = new String[(map.size() - offset) / 2][2];
        String[][] temp = map.values().toArray(new String[0][0]);

        for (int i = offset; i < temp.length; i += 2) {
            out[(i - offset) / 2][0] = temp[i][0];
            out[(i - offset) / 2][1] = temp[i + 1][0];
        }

        return out;
    }
}
