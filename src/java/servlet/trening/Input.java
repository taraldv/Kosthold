/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.trening;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.Database;

/**
 *
 * @author Tarald
 */
public class Input extends HttpServlet {

    @Override
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
            if (type.equals("input")) {
                String[][] mergedParams = mapToArray(request.getParameterMap());
                int i = insertSQL(mergedParams);
                if (i > 0) {
                    response.sendRedirect("https://trening.tarves.no" + "?" + i);
                } else {
                    out.print(i);
                }
            } else if (type.equals("names")) {
                out.print(getExerNameSelect());
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String getExerNameSelect() throws Exception {
        String exerNameSelectQuery = "SELECT distinct(excercise) FROM trening;";
        return Database.normalQuery(exerNameSelectQuery, 0).getJSON();
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
}
