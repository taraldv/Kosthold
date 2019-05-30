/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database;

import java.sql.PreparedStatement;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class FjernDenne {

    static final int DATABASENR = 1;

    static public int singleUpdateQuery(String query, Object[] var, boolean returnAutoIncrement) throws Exception {
        return Database.singleUpdateQuery(query, var, returnAutoIncrement, DATABASENR);
    }

    static public ResultSetContainer multiQuery(String query, Object[] vars) throws Exception {
        return Database.multiQuery(query, vars, DATABASENR);
    }

    static public int callProcedure(String query, Object[] vars) throws Exception{
        return Database.callProcedure(query, vars, DATABASENR);
    }
    
    static public ResultSetContainer normalQuery(String query) throws Exception {
        return Database.normalQuery(query, DATABASENR);
    }

    /* må gjøre det i flere steg pga hver rad har forskjellig WHERE clause */
    static public int innstillingerMultipleUpdateQueries(String query, String[][] arr, int offset) throws Exception {
        PreparedStatement ps = Database.getprepStatement(query, 0, DATABASENR);
        int output = 0;
        /* første array i 'arr' er 'type' og har lengde 1 */
        /* de 4 andre skal ha samme lengde */
        for (int i = 0; i < arr[offset].length; i++) {
            String øvre = arr[offset + 1][i];
            String nedre = arr[offset + 2][i];
            String id = arr[offset][i];
            ps.setBoolean(1, Boolean.parseBoolean(arr[offset + 3][i]));

            if (øvre == null || øvre.length() == 0) {
                ps.setInt(2, 0);
            } else {
                ps.setInt(2, Integer.parseInt(øvre));
            }

            if (nedre == null || nedre.length() == 0) {
                ps.setInt(3, 0);
            } else {
                ps.setInt(3, Integer.parseInt(nedre));
            }

            if (id == null || id.length() == 0) {
                ps.setInt(4, 0);
            } else {
                ps.setInt(4, Integer.parseInt(id));
            }

            output += ps.executeUpdate();
        }
        return output;
    }
}
