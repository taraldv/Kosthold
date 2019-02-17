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
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.TooManyColumns;
import util.database.KostholdDatabase;
import util.http.StandardResponse;
import util.insert.ParameterMapConverter;

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
                Map<String, String[]> paras = request.getParameterMap();

                out.print(updateMatvare(paras, brukerId, Integer.parseInt(request.getParameter("rowId"))));
                //out.print(request.getParameter("rowData"));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int updateMatvare(Map<String, String[]> map, int brukerId, int matvareId) throws Exception {
        String verifyQuery = "SELECT næringsinnhold FROM benevninger WHERE";
        Object[] kolonneArray = map.keySet().toArray();
        for (int x = 0; x < kolonneArray.length; x++) {
            if (x != 0) {
                verifyQuery += " OR";
            }
            verifyQuery += " næringsinnhold LIKE ?";
        }
        /* en slags måte å bruke preparedStatement på kolonneNavn, men tar 2 steg */
        String[][] verifisertKolonneNavn = KostholdDatabase.multiQuery(verifyQuery, kolonneArray).getData();
        String[][] mapData = map.values().toArray(new String[0][0]);
        String updateQuery = "UPDATE matvaretabellen SET ";
        Object[] vars = new Object[verifisertKolonneNavn.length + 1];

        /* kan vel ikke være dynamisk? alt er jo strings, men skal være int */
        for (int i = 0; i < verifisertKolonneNavn.length; i++) {
            if (i != 0) {
                updateQuery += ",";
            }
            updateQuery += "`"+verifisertKolonneNavn[i][0] + "`=?";
            try {
                /* mapData offset pga type,matvareId og matvare */
                vars[i] = Double.parseDouble(mapData[i + 3][0]);
            } catch (NumberFormatException e) {
                /* fjerner NULL verdier */
                vars[i] = new Double(0);
            }
        }
        vars[vars.length - 1] = matvareId;
        updateQuery += " WHERE matvareId = ? AND brukerId=" + brukerId;

        return KostholdDatabase.singleUpdateQuery(updateQuery, vars, false);
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
