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
import util.database.FjernDenne;
import util.http.StandardResponse;

/**
 *
 * @author Tarald
 */
public class Stats extends HttpServlet {

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
        String målQuery = "SELECT øvreMål,nedreMål,b.næringsinnhold,aktiv,b.benevning,b.benevningId FROM brukerBenevningMål "
                + "LEFT JOIN benevninger b ON b.benevningId = brukerBenevningMål.benevningId WHERE brukerId =" + brukerId + ";";
        return FjernDenne.normalQuery(målQuery).getJSON();
    }

    private String getGraf(int brukerId) throws Exception {
        String getLoggQuery = "SELECT DISTINCT dato,SUM(ROUND(m.Kilokalorier/100*mengde,2)) as kcal FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY) "
                + "GROUP BY dato ORDER BY dato;";
        return FjernDenne.normalQuery(getLoggQuery).getJSON();
    }

    private String getPieChart(int brukerId) throws Exception {
        String getLoggQuery = "SELECT DISTINCT m.matvare,SUM(ROUND(m.Kilokalorier/100*mengde,2)) as kcal FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY) "
                + "GROUP BY m.matvare ORDER BY kcal;";
        return FjernDenne.normalQuery(getLoggQuery).getJSON();
    }

    private String getLogg(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = FjernDenne.normalQuery(brukerDefinertQuery).getOneColumnToString("m.");
        //String additionalStuff = ",m.Kilokalorier";
        String getLoggQuery = "SELECT m.matvare,mengde,dato" + additionalStuff + " FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL 31 DAY);";
        return FjernDenne.normalQuery(getLoggQuery).getJSON();

    }

}
