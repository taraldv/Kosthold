/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.statistikk;

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
public class Kosthold extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Statistikk Kosthold");
        html.addStandard();
        html.addJS("../../js/statistikk.js");
        html.addJS("../../js/statistikkKosthold.js");
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
            if (type.equals("getStatsMål")) {
                out.print(getStatsMål(brukerId));
            } else if (type.equals("getLogg")) {
                out.print(getLogg(brukerId));
            } else if (type.equals("getGraf")) {
                out.print(getGraf(brukerId));
            } else if (type.equals("getPieChart")) {
                out.print(getPieChart(brukerId));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private String getStatsMål(int brukerId) throws Exception {
        String målQuery = "SELECT øvreMål,nedreMål,b.navn,aktiv,b.benevning,b.benevningId FROM brukerBenevningMål "
                + "LEFT JOIN benevninger b ON b.benevningId = brukerBenevningMål.benevningId WHERE brukerId =" + brukerId + ";";
        return Database.normalQuery(målQuery).getJSON();
    }

    private String getGraf(int brukerId) throws Exception {
        String getLoggQuery = "SELECT DISTINCT dato,SUM(ROUND(m.Kilokalorier/100*mengde,2)) as kcal FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY) "
                + "GROUP BY dato ORDER BY dato;";
        return Database.normalQuery(getLoggQuery).getJSON();
    }

    private String getPieChart(int brukerId) throws Exception {
        String getLoggQuery = "SELECT DISTINCT m.matvare,SUM(ROUND(m.Kilokalorier/100*mengde,2)) as kcal FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY) "
                + "GROUP BY m.matvare ORDER BY kcal;";
        return Database.normalQuery(getLoggQuery).getJSON();
    }

    private String getLogg(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.navn FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = Database.normalQuery(brukerDefinertQuery).getOneColumnToString("m.");
        //String additionalStuff = ",m.Kilokalorier";
        String getLoggQuery = "SELECT m.matvare,mengde,dato" + additionalStuff + " FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY);";
        return Database.normalQuery(getLoggQuery).getJSON();

    }

}
