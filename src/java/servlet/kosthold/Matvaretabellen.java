/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.TooManyColumns;
import util.database.KostholdDatabase;
import util.http.StandardResponse;
import util.insert.ParameterMapConverter;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class Matvaretabellen extends HttpServlet {

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
            if (type.equals("insertMatvaretabell")) {
                out.print(insertMatvaretabellen(request.getParameter("navn"), brukerId, ParameterMapConverter.twoParameterMap(request.getParameterMap(), 2)));
            } else if (type.equals("getBrukerMatvaretabell")) {
                out.print(getMatvaretabellTabell(brukerId));
            } else if (type.equals("deleteMatvare")) {
                out.print(deleteMatvare(brukerId, Integer.parseInt(request.getParameter("brukerId"))));
            } else if (type.equals("updateMatvare")) {

            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int deleteMatvare(int brukerId, int matvareId) throws Exception {
        String deleteQuery = "DELETE FROM matvaretabellen WHERE matvareId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {matvareId};
        return KostholdDatabase.singleUpdateQuery(deleteQuery, vars, false);
    }

    private String getMatvaretabellTabell(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = KostholdDatabase.normalQuery(brukerDefinertQuery).getOneColumnToString("");

        String getLoggQuery = "SELECT matvareId,matvare" + additionalStuff + " FROM matvaretabellen WHERE brukerId = " + brukerId + ";";
        return KostholdDatabase.normalQuery(getLoggQuery).getJSON();
    }


    /* TODO flytt denne dynamisk column insert */
    /* må bruke to queries, første henter kolonner fra benevningTabell */
    private int insertMatvaretabellen(String matvareNavn, int brukerId, String[][] arr) throws Exception {
        TooManyColumns tmc = new TooManyColumns(arr);
        String query = tmc.getQuery();
        ArrayList<Double> list = tmc.getList();

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/kosthold", "kosthold", "");

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, matvareNavn);
        ps.setInt(2, brukerId);
        for (int x = 0; x < list.size(); x++) {
            ps.setDouble(x + 3, list.get(x));
        }

        int result = ps.executeUpdate();
        connection.close();
        return result;
    }
}
