/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.Kosthold;
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
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private String getStatsMål(int brukerId) throws Exception {
        String målQuery = "SELECT øvreMål,nedreMål,b.næringsinnhold,aktiv,b.benevning,b.benevningId FROM brukerBenevningMål "
                + "LEFT JOIN benevninger b ON b.benevningId = brukerBenevningMål.benevningId WHERE brukerId =" + brukerId + ";";
        return Kosthold.normalQuery(målQuery).getJSON();
    }

    /*TODO henter 31 distinct(dato) rader*/
    private String getLogg(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = Kosthold.normalQuery(brukerDefinertQuery).getOneColumnToString("m.");

        String getLoggQuery = "SELECT m.matvare,mengde,dato" + additionalStuff + " FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + ";";
        return Kosthold.normalQuery(getLoggQuery).getJSON();

    }

}
