/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.sql.Database;
import util.http.Headers;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class Matvaretabellen extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Kosthold Logg");
        html.addStandard();
        html.addJS("../../js/kosthold.js");
        html.addJS("../../js/kostholdMatvaretabellen.js");
        resp.getWriter().print(html.toString());

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        ValidSession.isValid(request, response);
        PrintWriter out =response.getWriter();
        String type = request.getParameter("type");

        try {
            int brukerId = (int) request.getSession().getAttribute("brukerId");
            if (type.equals("insertMatvaretabell")) {
                out.print(matvaretabellInsert(request.getParameterMap(), brukerId, request.getParameter("navn")));
            } else if (type.equals("getBrukerMatvaretabell")) {
                out.print(getMatvaretabellTabell(brukerId));
            } else if (type.equals("deleteMatvare")) {
                out.print(deleteMatvare(brukerId, Integer.parseInt(request.getParameter("brukerId"))));
            } else if (type.equals("updateMatvare")) {
                Map<String, String[]> paras = request.getParameterMap();

                out.print(updateMatvare(paras, brukerId, Integer.parseInt(request.getParameter("rowId"))));
                //out.print(request.getParameter("rowData"));
            } else if (type.equals("autocomplete")) {
                out.print(autocomplete(brukerId, request.getParameter("string"), request.getParameter("table")));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String autocomplete(int brukerId, String matchingParameter, String whichTable) throws Exception {
        String autocompleteQuery = "";
        if (whichTable.equals("matvaretabellen")) {
            autocompleteQuery = "SELECT matvare,matvareId FROM matvaretabellen WHERE matvare LIKE ? LIMIT 30;";
        } else if (whichTable.equals("næringsinnhold")) {
            autocompleteQuery = "SELECT næringsinnhold,benevning FROM benevninger WHERE næringsinnhold LIKE ? LIMIT 15;";
        }
        ResultSetContainer rsc = Database.multiQuery(autocompleteQuery, new Object[]{"%" + matchingParameter + "%"});
        String completeJson = rsc.getJSON();
        if (completeJson.length() > 2) {
            String jsonAddition = "\"search\":\"" + matchingParameter + "\",";
            completeJson = new StringBuffer(completeJson).insert(1, jsonAddition).toString();
        }
        return completeJson;
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
        String[][] verifisertKolonneNavn = Database.multiQuery(verifyQuery, kolonneArray).getData();
        String[][] mapData = map.values().toArray(new String[0][0]);
        String updateQuery = "UPDATE matvaretabellen SET matvare=?";
        Object[] vars = new Object[verifisertKolonneNavn.length + 2];
        vars[0] = mapData[2][0];
        /* kan vel ikke være dynamisk? alt er jo strings, men skal være int */
        for (int i = 0; i < verifisertKolonneNavn.length; i++) {
            // if (i != 0) {
            updateQuery += ",";
            //}
            updateQuery += "`" + verifisertKolonneNavn[i][0] + "`=?";
            try {
                /* mapData offset pga type,matvareId og matvare */
                vars[i + 1] = Double.parseDouble(mapData[i + 3][0]);
            } catch (NumberFormatException e) {
                /* fjerner NULL verdier */
                vars[i + 1] = new Double(0);
            }
        }
        vars[vars.length - 1] = matvareId;
        updateQuery += " WHERE matvareId = ? AND brukerId=" + brukerId;

        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteMatvare(int brukerId, int matvareId) throws Exception {
        String deleteQuery = "DELETE FROM matvaretabellen WHERE matvareId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {matvareId};
        return Database.singleUpdateQuery(deleteQuery, vars, false);
    }

    private String getMatvaretabellTabell(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = Database.normalQuery(brukerDefinertQuery).getOneColumnToString("");

        String getLoggQuery = "SELECT matvareId,matvare" + additionalStuff + " FROM matvaretabellen WHERE brukerId = " + brukerId + ";";
        return Database.normalQuery(getLoggQuery).getJSON();
    }

    private int matvaretabellInsert(Map<String, String[]> map, int brukerId, String matvare) throws Exception {
        String verifyQuery = "SELECT næringsinnhold FROM benevninger WHERE";
        Object[] kolonneArray = map.get("innhold");
        for (int x = 0; x < kolonneArray.length; x++) {
            if (x != 0) {
                verifyQuery += " OR";
            }
            verifyQuery += " næringsinnhold LIKE ?";
        }
        /* en slags måte å bruke preparedStatement på kolonneNavn, men tar 2 steg */
        String[][] verifisertKolonneNavn = Database.multiQuery(verifyQuery, kolonneArray).getData();

        String[][] mapData = map.values().toArray(new String[0][0]);

        String columnString = "INSERT INTO matvaretabellen (matvare,brukerId";
        String valueString = "VALUES (?,?";
        Object[] vars = new Object[verifisertKolonneNavn.length + 2];
        vars[0] = matvare;
        vars[1] = brukerId;

        /* kan vel ikke være dynamisk? alt er jo strings, men skal være int */
        for (int i = 0; i < verifisertKolonneNavn.length; i++) {
            columnString += ",`" + verifisertKolonneNavn[i][0] + "`";
            valueString += ",?";

            /*mapData rader er type,navn,innhold og verdier(3)*/
            vars[i + 2] = Double.parseDouble(mapData[3][i]);

        }
        columnString += ") ";
        valueString += ");";
        //return columnString+valueString;
        return Database.singleUpdateQuery(columnString + valueString, vars, false);
    }
}
