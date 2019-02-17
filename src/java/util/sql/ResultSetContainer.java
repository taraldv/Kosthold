/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 *
 * @author Tarald
 */
public class ResultSetContainer {

    private String[] columnHeaders;
    private String[][] data;

    public ResultSetContainer(ResultSet rs) throws Exception {
        parseResultSet(rs);
    }

    private void parseResultSet(ResultSet rset) throws Exception {
        ResultSetMetaData rsmd = rset.getMetaData();
        int colCount = rsmd.getColumnCount();
        ArrayList<String[]> list = new ArrayList<>();
        for (int x = 0; rset.next(); x++) {
            if (x == 0) {
                setHeaders(rsmd, colCount);
            }
            String[] tempArr = new String[colCount];
            for (int y = 0; y < colCount; y++) {
                tempArr[y] = rset.getString(y + 1);
            }
            list.add(tempArr);
        }
        data = list.toArray(new String[0][0]);
    }

    private void setHeaders(ResultSetMetaData rsmd, int len) throws Exception {
        ArrayList<String> colHeaders = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            colHeaders.add(rsmd.getColumnLabel(i + 1));
        }
        columnHeaders = colHeaders.toArray(new String[0]);
    }

    public String[] getColumnHeaders() {
        return columnHeaders;
    }

    public String[][] getData() {
        return data;
    }

    /* x er rader, y er kolonner */
    public String getJSON() throws Exception {
        String json = "{";

        for (int x = 0; x < data.length; x++) {
            if (x != 0) {
                json += ",";
            }
            json += "\"" + x + "\":{";
            for (int y = 0; y < columnHeaders.length; y++) {
                if (y != 0) {
                    json += ",";
                }
                json += "\"" + columnHeaders[y] + "\":\"" + data[x][y] + "\"";
            }
            json += "}";
        }
        return json += "}";
    }

    public String getOneColumnToString(String prepend) throws Exception {
        String output = "";
        for (String[] dataArr : data) {
            output += "," + prepend + "`" + dataArr[0] + "`";
        }
        return output;
    }

}
