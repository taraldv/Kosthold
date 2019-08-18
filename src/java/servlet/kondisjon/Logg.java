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
                out.print(insertKondisjonLogg(brukerId,
                        Integer.parseInt(request.getParameter("tidSekunder")),
                        Integer.parseInt(request.getParameter("kondisjonTurerId"))));
            } else if (type.equals("deleteKondisjonLogg")) {
                out.print(deleteKondisjonLogg(brukerId, Integer.parseInt(request.getParameter("kondisjonLoggId"))));
            } else if (type.equals("updateKondisjonLogg")) {
                int loggId = Integer.parseInt(request.getParameter("rowId"));
                int tidSekunder = Integer.parseInt(request.getParameter("tidSekunder"));
                String dato = request.getParameter("dato");
                out.print(updateKondisjonLogg(brukerId, loggId, tidSekunder, dato));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateKondisjonLogg(int brukerId, int kondisjonLoggId, int tidSekunder, String dato) throws Exception {
        String updateQuery = "UPDATE kondisjonLogg SET tidSekunder = ?, dato = ? WHERE kondisjonLoggId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {tidSekunder, dato, kondisjonLoggId};
        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteKondisjonLogg(int brukerId, int kondisjonLoggId) throws Exception {
        String query = "DELETE FROM kondisjonLogg WHERE brukerId = ? AND kondisjonLoggId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, kondisjonLoggId}, false);
    }

    private String getKondisjonLogg(int brukerId) throws Exception {
        String query = "SELECT l.kondisjonLoggId,t.navn,l.dato,l.tidSekunder FROM kondisjonLogg l "
                + "LEFT JOIN kondisjonTurer t ON t.kondisjonTurerId = l.kondisjonTurerId"
                + " WHERE l.brukerId = " + brukerId
                + " ORDER BY l.dato DESC;";
        return Database.normalQuery(query).getJSON();
    }

    private int insertKondisjonLogg(int brukerId, int tidSekunder, int kondisjonTurerId) throws Exception {
        Object[] vars = {kondisjonTurerId, tidSekunder};
        String query = "INSERT INTO kondisjonLogg(dato,kondisjonTurerId,tidSekunder,brukerId) VALUES (CURDATE(),?,?," + brukerId + ")";
        return Database.singleUpdateQuery(query, vars, false);
    }
}
