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
public class Database {

    private static final String[] DATABASE = {"trening", "kosthold"};

    static private Connection getDatabaseConnection(int index) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        /* database , brukernavn, passord */
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE[index], DATABASE[index], "");
        return connection;
    }

    static public PreparedStatement getprepStatement(String query, int option, int databaseNr) throws Exception {
        Connection c = getDatabaseConnection(databaseNr);
        if (option > 0) {
            return c.prepareStatement(query, option);
        } else {
            return c.prepareStatement(query);
        }
    }

    static public int singleUpdateQuery(String query, Object[] var, boolean returnAutoIncrement, int databaseNr) throws Exception {
        int options = 0;
        if (returnAutoIncrement) {
            options = Statement.RETURN_GENERATED_KEYS;
        }
        PreparedStatement ps = getprepStatement(query, options, databaseNr);

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

    static public ResultSetContainer multiQuery(String query, Object[] vars, int databaseNr) throws Exception {
        PreparedStatement ps = getprepStatement(query, 0, databaseNr);
        setPreparedStatementVariables(ps, vars);
        ResultSetContainer rsc = new ResultSetContainer(ps.executeQuery());
        ps.getConnection().close();
        return rsc;
    }

    static public ResultSetContainer normalQuery(String query, int databaseNr) throws Exception {
        ResultSetContainer rsc;
        try (Connection c = getDatabaseConnection(databaseNr)) {
            rsc = new ResultSetContainer(c.createStatement().executeQuery(query));
        }
        return rsc;
    }
}
