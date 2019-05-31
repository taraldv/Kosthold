/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.styrke;

import crypto.SessionLogin;
import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.StandardResponse;
import util.sql.ResultSetContainer;

/**
 *
 * @author Your Name <your.name at your.org>
 */
public class Logg extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());
        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }
        int brukerId = vs.getId();
        try {
            if (type.equals("getStyrkeLogg")) {
                out.print(getLogg(brukerId, Integer.parseInt(request.getParameter("interval"))));
            } else if (type.equals("insertStyrkeLogg")) {
                out.print(insertStyrkeLogg(brukerId, request.getParameterMap()));
            } else if (type.equals("getØvelser")) {
                out.print(getØvelser(brukerId));
            } else if (type.equals("deleteStyrkeLogg")) {
                out.print(deleteStyrkeLogg(brukerId, Integer.parseInt(request.getParameter("styrkeLoggId"))));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int deleteStyrkeLogg(int brukerId, int styrkeLoggId) throws Exception {
        String query = "DELETE FROM styrkeLogg WHERE brukerId = ? AND styrkeLoggId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, styrkeLoggId}, false);
    }

    private String getLogg(int brukerId, int interval) throws Exception {
        String query = "SELECT styrkeLoggId,s.navn,vekt,reps,dato FROM styrkeLogg "
                + "LEFT JOIN styrkeØvelse s ON s.styrkeId = styrkeLogg.styrkeId"
                + " WHERE s.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL ? DAY)"
                + " ORDER BY dato DESC;";
        return Database.multiQuery(query, new Object[]{interval}).getJSON();
    }

    private String getØvelser(int brukerId) throws Exception {
        String query = "SELECT navn,styrkeId FROM styrkeØvelse WHERE brukerId = ?;";
        return Database.multiQuery(query, new Object[]{brukerId}).getJSON();
    }

    //kopiert fra forrige trening.java
    private Object[] merge(String[][] arr) {
        HashSet<String> hash = new HashSet<>();
        hash.addAll(Arrays.asList(arr[1]));
        Object[] vars = new Object[hash.size() * 3];
        int x = 0;
        for (String string : hash) {
            int reps = 0;
            double kg = 0;
            int id = Integer.parseInt(string);
            for (int i = 0; i < arr[1].length; i++) {
                if (id == Integer.parseInt(arr[1][i])) {
                    int tempReps = Integer.parseInt(arr[3][i]);
                    double tempKg = Double.parseDouble(arr[2][i]);
                    reps += tempReps;
                    kg += (tempKg * tempReps);
                }
            }
            vars[x * 3] = id;
            vars[(x * 3) + 1] = kg / reps;
            vars[(x * 3) + 2] = reps;
            x++;
        }
        return vars;
    }

    private int insertStyrkeLogg(int brukerId, Map<String, String[]> map) throws Exception {
        //inneholder: [[type][øvelseId,øvelseId...][kg,kg...][reps,reps...]]
        String[][] arr = map.values().toArray(new String[0][0]);
        Object[] vars = merge(arr);

        String baseline = "INSERT INTO styrkeLogg(dato,styrkeId,vekt,reps,brukerId) VALUES ";
        String row = "";
        for (int i = 0; i < vars.length / 3; i++) {
            if (i != 0) {
                row += ",";
            }
            row += "(CURDATE(),?,?,?," + brukerId + ")";
        }
        //return baseline + row + " , " + Arrays.toString(vars) + " , " + Arrays.deepToString(arr);
        return Database.singleUpdateQuery(baseline + row, vars, false);
    }
}
