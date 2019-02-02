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

    static public int oneStringInsert(String query, String inputString) throws Exception {
        PreparedStatement ps = getprepStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, inputString);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        String lastId = rs.getString("GENERATED_KEY");
        ps.getConnection().close();
        return Integer.parseInt(lastId);
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

    static public ResultSetContainer oneStringQuery(String query, String inputString) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0);
        ps.setString(1, inputString);
        ResultSetContainer rsc = new ResultSetContainer(ps.executeQuery());
        ps.getConnection().close();
        return rsc;
    }

    static public ResultSetContainer oneIntQuery(String query, int inputInt) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0);
        ps.setInt(1, inputInt);
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
}
