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
public class ParameterMapConverter {

    public static String[][] twoParameterMap(Map<String, String[]> map, int offset) {

        String[][] out = new String[(map.size() - offset) / 2][2];
        String[][] temp = map.values().toArray(new String[0][0]);

        for (int i = offset; i < temp.length; i += 2) {
            out[(i - offset) / 2][0] = temp[i][0];
            out[(i - offset) / 2][1] = temp[i + 1][0];
        }

        return out;
    }

    /* POST fra innstillinger har samme 'name', så det blir annderledes
    TODO: endre alle post til samme 'name' ?*/
    public static String[][] dynamiskConverter(Map<String, String[]> map, int offset, int kol) {
        
        /* map.size() == temp.length */
        String[][] temp = map.values().toArray(new String[0][0]);
        

        /* temp[0] kommer til å ha feil antall parameter(1 stk), så flytter index med offset */
        String[][] out = new String[temp[0 + offset].length][kol];

        /* snur egentlig på array, og eksluderer offset */
        for (int i = offset; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                out[j][i-offset] = temp[i][j];
            }
        }

        return out;
    }
}
