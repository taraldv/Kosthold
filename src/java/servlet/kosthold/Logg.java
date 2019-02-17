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
import util.database.KostholdDatabase;
import util.insert.ParameterMapConverter;
import util.http.StandardResponse;
import util.sql.MultiLineSqlQuery;

/**
 *
 * @author Tarald
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
            if (type.equals("getLoggTabell")) {
                out.print(getLoggTabell(brukerId));
            } else if (type.equals("getLogg")) {
                out.print(getLogg(brukerId));
            } else if (type.equals("insertLogg")) {
                out.print(insertLogg(ParameterMapConverter.twoParameterMap(request.getParameterMap(), 1), brukerId));
            } else if (type.equals("deleteLogg")) {
                out.print(deleteLogg(brukerId, Integer.parseInt(request.getParameter("loggId"))));
            } else if (type.equals("updateLogg")) {
                int loggId = Integer.parseInt(request.getParameter("rowId"));
                Double mengde = Double.parseDouble(request.getParameter("mengde"));
                String dato = request.getParameter("dato");
                out.print(updateLogg(brukerId, loggId, mengde, dato));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateLogg(int brukerId, int loggId, Double mengde, String dato) throws Exception {
        String updateQuery = "UPDATE logg SET mengde = ?, dato = ? WHERE loggId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {mengde, dato, loggId};
        return KostholdDatabase.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteLogg(int brukerId, int loggId) throws Exception {
        String deleteQuery = "DELETE FROM logg WHERE loggId = ? AND brukerId = " + brukerId + ";";
        return KostholdDatabase.singleUpdateQuery(deleteQuery, new Object[]{loggId}, false);
    }

    private String getLoggTabell(int brukerId) throws Exception {
        String query = "SELECT loggId,m.matvare,mengde,dato FROM logg "
                + "LEFT JOIN matvaretabellen m ON m.matvareId = logg.matvareId WHERE logg.brukerId = " + brukerId + " ORDER BY loggId DESC;";
        return KostholdDatabase.normalQuery(query).getJSON();
    }

    private int insertLogg(String[][] arr, int brukerId) throws Exception {
        String baseline = "INSERT INTO logg(dato,matvareId,mengde,brukerId) VALUES ";
        String row = "(CURDATE(),?,?," + brukerId + ")";
        String multiQuery = MultiLineSqlQuery.getStringFromArrayLength(arr.length, baseline, row);
        return KostholdDatabase.multiInsertQuery(arr, multiQuery);
    }

    /*TODO henter 31 distinct(dato) rader*/
    private String getLogg(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = KostholdDatabase.normalQuery(brukerDefinertQuery).getOneColumnToString("m.");

        String getLoggQuery = "SELECT m.matvare,mengde,dato" + additionalStuff + " FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + ";";
        return KostholdDatabase.normalQuery(getLoggQuery).getJSON();

    }

}
