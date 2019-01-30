/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Tarald
 */
public class KostholdDatabase {

    static public Connection getDatabaseConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/kosthold", "kosthold", "");
        return connection;
    }

    static public ResultSet oneStringQuery(String query, String match) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, match);
        return ps.executeQuery();
    }

    static public ResultSet oneIntQuery(String query, int id) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    static public ResultSet databaseQuery(String query) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        ResultSet rset = c.createStatement().executeQuery(query);
        return rset;
    }
}
