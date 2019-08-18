/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kondisjon;

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
import util.HTML;
import util.http.Headers;
import util.sql.Database;

/**
 *
 * @author
 */
public class Logg extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Kondisjon Logg");
        html.addStandard();
        html.addJS("../../js/kondisjonLogg.js");
        resp.getWriter().print(html.toString());

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        try {
            int brukerId = (int) request.getSession().getAttribute("brukerId");
            if (type.equals("getKondisjonLogg")) {
                out.print(getKondisjonLogg(brukerId));
            } else if (type.equals("insertKondisjonLogg")) {
                out.print(insertKondisjonLogg(brukerId, request.getParameterMap()));
            } else if (type.equals("getKondisjonTur")) {
                out.print(getKondisjonTur(brukerId));
            } else if (type.equals("deleteKondisjonLogg")) {
                out.print(deleteKondisjonLogg(brukerId, Integer.parseInt(request.getParameter("kondisjonLoggId"))));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int deleteKondisjonLogg(int brukerId, int kondisjonLoggId) throws Exception {
        String query = "DELETE FROM kondisjonLogg WHERE brukerId = ? AND kondisjonLoggId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, kondisjonLoggId}, false);
    }

    private String getKondisjonTur(int brukerId) throws Exception {
        String query = "SELECT * FROM kondisjonLogg "
                + " WHERE s.brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    private String getKondisjonLogg(int brukerId) throws Exception {
        String query = "SELECT * FROM kondisjonLogg "
                + " WHERE s.brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    private String insertKondisjonLogg(int brukerId, Map<String, String[]> map) throws Exception {
        //inneholder: [[type][øvelseId,øvelseId...][kg,kg...][reps,reps...]]
        String[][] arr = map.values().toArray(new String[0][0]);

        return Arrays.deepToString(arr);

        /* String baseline = "INSERT INTO styrkeLogg(dato,styrkeId,vekt,reps,brukerId) VALUES ";
        String row = "";
        for (int i = 0; i < vars.length / 3; i++) {
            if (i != 0) {
                row += ",";
            }
            row += "(CURDATE(),?,?,?," + brukerId + ")";
        }
        //return baseline + row + " , " + Arrays.toString(vars) + " , " + Arrays.deepToString(arr);
        return Database.singleUpdateQuery(baseline + row, vars, false);*/
    }
}
