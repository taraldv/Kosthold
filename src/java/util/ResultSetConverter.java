/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 *
 * @author Tarald
 */
public class ResultSetConverter {

    public static String toJSON(ResultSet rset) throws Exception {
        String json = "{";
        ResultSetMetaData rsmd = rset.getMetaData();
        for (int x = 0; rset.next(); x++) {
            if (x != 0) {
                json += ",";
            }
            json += "\"" + x + "\":{";
            for (int y = 0; y < rsmd.getColumnCount(); y++) {
                if (y != 0) {
                    json += ",";
                }
                json += "\"" + rsmd.getColumnLabel(y + 1) + "\":\"" + rset.getString(y + 1) + "\"";
            }
            json += "}";
        }
        return json += "}";
    }

    public static String oneColumnToString(ResultSet rset) throws Exception {
        String output = "";
        while (rset.next()) {
            output += ",m.`" + rset.getString(1) + "`";
        }
        return output;
    }
}
