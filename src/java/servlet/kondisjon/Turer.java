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
public class Turer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Kondisjon Turer");
        html.addStandard();
        html.addJS("../../js/kondisjonTurer.js");
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
            if (type.equals("getKondisjonTur")) {
                out.print(getKondisjonTur(brukerId));
            } else if (type.equals("insertKondisjonTur")) {
                out.print(insertKondisjonTur(brukerId,
                        request.getParameter("navn"),
                        Double.parseDouble(request.getParameter("km")),
                        Integer.parseInt(request.getParameter("mohStart")),
                        Integer.parseInt(request.getParameter("mohSlutt"))));
            } else if (type.equals("deleteKondisjonTur")) {
                out.print(deleteKondisjonTur(brukerId, Integer.parseInt(request.getParameter("kondisjonTurerId"))));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    //brukerId brukt på annen måte her? må bli mer konsistens
    private int deleteKondisjonTur(int brukerId, int kondisjonTurerId) throws Exception {
        String query = "DELETE FROM kondisjonTurer WHERE brukerId = ? AND kondisjonTurerId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, kondisjonTurerId}, false);
    }

    private String getKondisjonTur(int brukerId) throws Exception {
        String query = "SELECT * FROM kondisjonTurer "
                + " WHERE s.brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    private int insertKondisjonTur(int brukerId, String navn, double km, int mohStart, int mohSlutt) throws Exception {
        Object[] vars = {navn, km, mohStart, mohSlutt};
        String query = "INSERT INTO kondisjonTurer (brukerId,navn,km,mohStart,mohSlutt) "
                + "VALUES (" + brukerId + ",?,?,?,?);";
        return Database.singleUpdateQuery(query, vars, false);
    }
}
