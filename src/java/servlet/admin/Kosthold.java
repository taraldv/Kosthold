/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.admin;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.sql.Database;
import util.http.Headers;
/**
 *
 * @author Tarald
 */
public class Kosthold extends HttpServlet {

        @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Kosthold Logg");
        html.addStandard();
        resp.getWriter().print(html.toString());
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        ValidSession.isValid(request, response);
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");

        try {
            int brukerId = (int) request.getSession().getAttribute("brukerId");
            if (type.equals("endrePassord")) {

            } else if (type.equals("endreBenevningMål")) {
                //String[][] arr = ParameterMapConverter.dynamiskConverter(request.getParameterMap(), 1, 4);

                //out.print(Arrays.deepToString(arr));
                out.print(endreBenevningMål(request.getParameterMap(), brukerId));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int endreBenevningMål(Map<String, String[]> map, int brukerId) throws Exception {
        String[][] arr = map.values().toArray(new String[0][0]);
        /* arr: [[type][id,id...][øvre,øvre...][nedre,nedre...][aktiv,aktiv...]]*/
        String query = "UPDATE brukerBenevningMål SET"
                + " aktiv = ?, øvreMål = ?, nedreMål = ?"
                + " WHERE brukerId =" + brukerId + " AND benevningId = ?" + ";";

        return Database.innstillingerMultipleUpdateQueries(query, arr, 1);
        //return Arrays.deepToString(arr);
    }

}
