/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import ignore.GitHubIgnore;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import util.TooManyColumns;

/**
 *
 * @author Tarald
 */
public class Post extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", GitHubIgnore.URL);
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");
        HttpSession session = request.getSession();
        try {
            String bruker = request.getParameter("brukernavn");
            String passord = request.getParameter("passord");
            if (checkPassword(bruker, passord)) {
                session.setAttribute("bruker", bruker);
            }
        } catch (Exception e) {
            if (session.getAttribute("bruker") == null) {
                e.printStackTrace(out);
                return;
            }
        }
        try {
            if (type.equals("insertMatvaretabell")) {
                String matvareNavn = request.getParameter("navn");
                String[][] matvareIngrediensOgVerdier = mapToArrayInArray(request.getParameterMap(), 2);
                out.print(insertIntoMatvaretabellen(matvareNavn, matvareIngrediensOgVerdier));
            } else if (type.equals("insertMåltider")) {
                String[][] ingrediensOgVerdiArray = mapToArrayInArray(request.getParameterMap(), 2);
                String navn = request.getParameter("navn");
                int lastInsertedId = insertMåltidAndGetLastID(navn);
                out.print(insertSQL(ingrediensOgVerdiArray, lastInsertedId));
            } else if (type.equals("insertLogg")) {
                String[][] matvareOgMengdeArray = mapToArrayInArray(request.getParameterMap(), 1);
                out.print(insertIntoLogg(matvareOgMengdeArray));
            } else if (type.equals("getMatvaretabell")) {
                out.print(resultSetToJSON(databaseQuery("SELECT matvareId,matvare FROM matvaretabellen;")));
            } else if (type.equals("checkMåltider")) {
                String navn = request.getParameter("string");
                String navnQuery = "SELECT 1 FROM måltider WHERE navn LIKE ?;";
                out.print(resultSetToJSON(oneStringQuery(navnQuery, navn)));
            } else if (type.equals("autocomplete")) {
                String matchingParameter = request.getParameter("string");
                String whichTable = request.getParameter("table");
                String autocompleteQuery = "";
                if (whichTable.equals("matvaretabellen")) {
                    autocompleteQuery = "SELECT matvare,matvareId FROM matvaretabellen WHERE matvare LIKE ? LIMIT 15;";
                } else if (whichTable.equals("næringsinnhold")) {
                    autocompleteQuery = "SELECT næringsinnhold,benevning FROM benevninger WHERE næringsinnhold LIKE ? LIMIT 15;";
                }
                String completeJson = resultSetToJSON(oneStringQuery(autocompleteQuery, "%" + matchingParameter + "%"));
                if (completeJson.length() > 2) {
                    String jsonAddition = "\"search\":\"" + matchingParameter + "\",";
                    completeJson = new StringBuffer(completeJson).insert(1, jsonAddition).toString();
                }
                out.print(completeJson);
            } else {
                out.print(type);
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private boolean checkPassword(String brukernavn, String password) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/users", "kosthold", "");
        String query = "SELECT passord FROM users WHERE brukernavn LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, brukernavn);
        ResultSet rs = ps.executeQuery();
        rs.first();
        String hashedPassword = rs.getString(1);
        return BCrypt.checkpw(password, hashedPassword);
    }

    private int insertIntoLogg(String[][] arr) throws Exception {
        Connection c = getDatabaseConnection();
        String query = "INSERT INTO logg(dato,matvareId,mengde) VALUES ";
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                query += ",";
            }
            query += "(CURDATE(),?,?)";
        }
        query += ";";
        PreparedStatement ps = c.prepareStatement(query);
        for (int j = 0; j < arr.length; j++) {
            ps.setInt(1 + (2 * j), Integer.parseInt(arr[j][0]));
            ps.setDouble(2 + (2 * j), Double.parseDouble(arr[j][1]));
        }
        return ps.executeUpdate();
    }

    private int insertIntoMatvaretabellen(String navn, String[][] arr) throws Exception {
        TooManyColumns tmc = new TooManyColumns(arr);
        String query = tmc.getQuery();
        ArrayList<Double> list = tmc.getList();
        Connection c = getDatabaseConnection();
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, navn);
        for (int x = 0; x < list.size(); x++) {
            ps.setDouble(x + 2, list.get(x));
        }

        return ps.executeUpdate();
    }

    private int insertMåltidAndGetLastID(String navn) throws Exception {
        Connection c = getDatabaseConnection();
        String query = "INSERT INTO måltider(navn) VALUES (?);";
        PreparedStatement ps = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, navn);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        String lastId = rs.getString("GENERATED_KEY");
        return Integer.parseInt(lastId);
    }

    private int insertSQL(String[][] arr, int lastId) throws Exception {
        Connection c = getDatabaseConnection();
        String query = "INSERT INTO ingredienser (måltidId, matvareId, mengde) VALUES ";
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                query += ",";
            }
            query += "(" + lastId + ",?,?)";
        }
        query += ";";
        PreparedStatement ps = c.prepareStatement(query);
        for (int j = 0; j < arr.length; j++) {
            ps.setInt(1 + (2 * j), Integer.parseInt(arr[j][0]));
            ps.setDouble(2 + (2 * j), Double.parseDouble(arr[j][1]));

        }
        return ps.executeUpdate();
    }

    private String[][] mapToArrayInArray(Map<String, String[]> map, int offset) {

        String[][] out = new String[(map.size() - offset) / 2][2];
        String[][] temp = map.values().toArray(new String[0][0]);

        for (int i = offset; i < temp.length; i += 2) {
            out[(i - offset) / 2][0] = temp[i][0];
            out[(i - offset) / 2][1] = temp[i + 1][0];
        }

        return out;
    }

    private ResultSet oneStringQuery(String query, String match) throws Exception {
        Connection c = getDatabaseConnection();
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, match);
        return ps.executeQuery();
    }

    private Connection getDatabaseConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/kosthold", "kosthold", "");
        return connection;
    }

    private ResultSet databaseQuery(String query) throws Exception {
        Connection c = getDatabaseConnection();
        ResultSet rset = c.createStatement().executeQuery(query);
        return rset;
    }

    private String resultSetToJSON(ResultSet rset) throws Exception {
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
}
