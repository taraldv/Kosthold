/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.trening;

import crypto.ValidSession;
import util.Kalender;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tarald
 */
public class Post extends HttpServlet {

    private final String recentHistory = "select dato,excercise,avg_weight from trening where dato >= (select distinct(dato) from trening order by id desc limit 5,1);";
    private final String calendar = "select dato,excercise,avg_weight from trening order by dato ASC";
    private final String names = "select distinct(excercise) from trening;";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "https://trening.tarves.no");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());

        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }

        try {
            if (type.equals("history")) {
                out.print(resultSetToJSON(db(recentHistory)));
            } else if (type.equals("names")) {
                out.print(resultSetToJSON(db(names)));
            } else if (type.equals("input")) {
                String[][] mergedParams = mapToArray(request.getParameterMap());
                int i = insertSQL(mergedParams);
                if (i > 0) {
                    response.sendRedirect("https://trening.tarves.no" + "?" + i);
                } else {
                    out.print(i);
                }
            } else if (type.equals("kalender")) {
                out.print(Kalender.getJSON(db(calendar)));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int insertSQL(String[][] arr) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/trening", "trening", "");
        String query = "INSERT INTO trening (dato, excercise, avg_weight, reps,time) VALUES ";
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                query += ",";
            }
            query += "(CURDATE(),?,?,?,CURRENT_TIMESTAMP())";
        }
        PreparedStatement ps = c.prepareStatement(query);
        for (int j = 0; j < arr.length; j++) {
            ps.setString(1 + (3 * j), arr[j][0]);
            ps.setDouble(2 + (3 * j), Double.parseDouble(arr[j][1]));
            ps.setInt(3 + (3 * j), Integer.parseInt(arr[j][2]));
        }
        //return query;
        return ps.executeUpdate();
    }

    private String[][] mapToArray(Map<String, String[]> map) {

        String[][] out = new String[map.size() / 3][3];
        String[][] temp = map.values().toArray(new String[0][0]);

        for (int i = 1; i < temp.length; i += 3) {
            out[(i - 1) / 3][0] = temp[i][0];
            out[(i - 1) / 3][1] = temp[i + 1][0];
            out[(i - 1) / 3][2] = temp[i + 2][0];
        }

        return merge(out);
    }

    private String[][] merge(String[][] arr) {
        HashSet<String> hash = new HashSet<>();
        for (String[] strings : arr) {
            hash.add(strings[0]);
        }
        if (arr.length == hash.size()) {
            return arr;
        } else {
            String[][] out = new String[hash.size()][3];
            int x = 0;
            for (String string : hash) {
                int reps = 0;
                double kg = 0;
                for (String[] strings : arr) {
                    if (string.equals(strings[0])) {
                        int tempReps = Integer.parseInt(strings[2]);
                        double tempKg = Double.parseDouble(strings[1]);
                        reps += tempReps;
                        kg += (tempKg * tempReps);
                    }
                }
                out[x][0] = string;
                out[x][1] = Double.toString(kg / reps);
                out[x][2] = Integer.toString(reps);
                x++;
            }
            return out;
        }
    }

    private ResultSet db(String query) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/trening", "trening", "");
        ResultSet rset = c.createStatement().executeQuery(query);
        //c.close();
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
