/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.vekt;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
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
public class Logg extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Vekt Logg");
        html.addStandard();
        html.addJS("../../js/helseVekt.js");
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
            if (type.equals("getVektLogg")) {
                out.print(getVektLogg(brukerId));
            } else if (type.equals("insertVekt")) {
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                out.print(insertVektLogg(kiloVekt, brukerId));
            } else if (type.equals("deleteVekt")) {
                int vektId = Integer.parseInt(request.getParameter("vektId"));
                out.print(deleteVekt(vektId, brukerId));
            } else if (type.equals("updateVekt")) {
                int vektId = Integer.parseInt(request.getParameter("rowId"));
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                String dato = request.getParameter("dato");
                out.print(updateVekt(vektId, brukerId, kiloVekt, dato));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateVekt(Integer vektId, int brukerId, Double kiloVekt, String dato) throws Exception {
        String updateQuery = "UPDATE vekt SET kilo = ?, dato = ? WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {kiloVekt, dato, vektId};
        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteVekt(int vektId, int brukerId) throws Exception {
        String deleteQuery = "DELETE FROM vekt WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {vektId};
        return Database.singleUpdateQuery(deleteQuery, vars, false);
    }

    private String getVektLogg(int brukerId) throws Exception {
        String målQuery = "SELECT vektId,dato,kilo FROM vekt WHERE brukerId =" + brukerId + ";";
        return Database.normalQuery(målQuery).getJSON();
    }

    private int insertVektLogg(Double kiloVekt, int brukerId) throws Exception {
        String insertQuery = "INSERT INTO vekt(dato,kilo,brukerId) VALUES (CURDATE(),?," + brukerId + ");";
        Object[] vars = {kiloVekt};
        return Database.singleUpdateQuery(insertQuery, vars, false);
    }
}
