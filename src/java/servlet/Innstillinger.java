/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.KostholdDatabase;
import util.http.StandardResponse;
import util.insert.ParameterMap;
import util.sql.MultiLineSqlQuery;

/**
 *
 * @author Tarald
 */
public class Innstillinger extends HttpServlet {

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
            if (type.equals("endrePassord")) {

            } else if (type.equals("endreBenevningMål")) {
                String[][] arr = ParameterMap.convertMapToArray(request.getParameterMap(), 1);
                out.print(Arrays.deepToString(arr));
                //out.print(endreBenevningMål(, brukerId));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private String endreBenevningMål(String[][] arr, int brukerId) {
        String query = "UPDATE brukerBenevningMål SET"
                + " aktiv = ?, øvreMål = ?, nedreMål = ?"
                + " WHERE brukerId =" + brukerId + " AND benevningId = ?;";
        return query;
    }

}
