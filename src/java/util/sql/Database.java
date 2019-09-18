/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

/**
 *
 * @author Tarald
 */
public class Database {

    static private Connection getDatabaseConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        /* database , brukernavn, passord */
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/kosthold", "kosthold", "");
        return connection;
    }

    static public PreparedStatement getprepStatement(String query, int option) throws Exception {
        Connection c = getDatabaseConnection();
        if (option > 0) {
            return c.prepareStatement(query, option);
        } else {
            return c.prepareStatement(query);
        }
    }

    static public int callProcedure(String query, Object[] vars) throws Exception {
        Connection c = getDatabaseConnection();
        CallableStatement cstmt = c.prepareCall(query);

        cstmt.setInt(1, (int) vars[0]);
        cstmt.setInt(2, (int) vars[1]);

        cstmt.registerOutParameter(3, Types.INTEGER);
        cstmt.executeUpdate();
        int rader = cstmt.getInt(3);
        //ResultSetContainer rsc = new ResultSetContainer(c.createStatement().executeQuery("SELECT " + (String) vars[2]));
        // int rader = Integer.parseInt(rsc.getData()[0][0]);
        cstmt.close();
        c.close();
        return rader;
    }

    static public int singleUpdateQuery(String query, Object[] var, boolean returnAutoIncrement) throws Exception {
        int options = 0;
        if (returnAutoIncrement) {
            options = Statement.RETURN_GENERATED_KEYS;
        }
        PreparedStatement ps = getprepStatement(query, options);

        for (int i = 0; i < var.length; i++) {
            /* string,double eller int */
            if (var[i] instanceof Integer) {
                ps.setInt(i + 1, (int) var[i]);
            } else if (var[i] instanceof Double) {
                ps.setDouble(i + 1, (double) var[i]);
            } else if (var[i] instanceof String) {
                ps.setString(i + 1, (String) var[i]);
            } else if (var[i] instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) var[i]);
            }
        }

        int output = ps.executeUpdate();

        if (returnAutoIncrement) {
            ResultSet keySet = ps.getGeneratedKeys();
            keySet.next();
            String lastId = keySet.getString("GENERATED_KEY");
            ps.getConnection().close();
            return Integer.parseInt(lastId);
        }
        ps.getConnection().close();
        return output;
    }

    static private void setPreparedStatementVariables(PreparedStatement ps, Object[] vars) throws Exception {
        for (int i = 0; i < vars.length; i++) {
            /* string,double eller int */
            if (vars[i] instanceof Integer) {
                ps.setInt(i + 1, (int) vars[i]);
            } else if (vars[i] instanceof Double) {
                ps.setDouble(i + 1, (double) vars[i]);
            } else if (vars[i] instanceof String) {
                ps.setString(i + 1, (String) vars[i]);
            } else if (vars[i] instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) vars[i]);
            }
        }
    }

    static public ResultSetContainer multiQuery(String query, Object[] vars) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0);
        setPreparedStatementVariables(ps, vars);
        ResultSetContainer rsc = new ResultSetContainer(ps.executeQuery());
        ps.getConnection().close();
        return rsc;
    }

    static public ResultSetContainer normalQuery(String query) throws Exception {
        ResultSetContainer rsc;
        try (Connection c = getDatabaseConnection()) {
            rsc = new ResultSetContainer(c.createStatement().executeQuery(query));
        }
        return rsc;
    }

    static public int innstillingerMultipleUpdateQueries(String query, String[][] arr, int offset) throws Exception {
        PreparedStatement ps = Database.getprepStatement(query, 0);
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
