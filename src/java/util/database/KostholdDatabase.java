/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class KostholdDatabase {

    static private Connection getDatabaseConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        /* database , brukernavn, passord */
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/kosthold", "kosthold", "");
        return connection;
    }

    static private PreparedStatement getprepStatement(String query, int option) throws Exception {
        Connection c = getDatabaseConnection();
        if (option > 0) {
            return c.prepareStatement(query, option);
        } else {
            return c.prepareStatement(query);
        }
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

    /* TODO dynamisk int,double,string fra input */
    static public int multiInsertQuery(String[][] arr, String query) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0);
        for (int j = 0; j < arr.length; j++) {
            ps.setInt(1 + (2 * j), Integer.parseInt(arr[j][0]));
            ps.setDouble(2 + (2 * j), Double.parseDouble(arr[j][1]));

        }
        int result = ps.executeUpdate();
        ps.getConnection().close();
        return result;
    }

    static public ResultSetContainer normalQuery(String query) throws Exception {
        ResultSetContainer rsc;
        try (Connection c = getDatabaseConnection()) {
            rsc = new ResultSetContainer(c.createStatement().executeQuery(query));
        }
        return rsc;
    }

    static public int multiUpdateQuery(String query, String[][] arr) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0);
        int output = 0;
        for (int i = 0; i < arr.length; i++) {
            String øvre = arr[i][1];
            String nedre = arr[i][2];
            String id = arr[i][0];
            ps.setBoolean(1, Boolean.parseBoolean(arr[i][3]));

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
