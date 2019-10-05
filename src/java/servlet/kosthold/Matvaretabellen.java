/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import html.Div;
import html.DivForm;
import html.Input;
import html.Select;
import html.StandardHtml;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Kosthold Matvaretabellen");
            DivForm form = getMatvaretabellenForm();
            Div div = new Div("", "brukerMatvaretabellTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getBrukerMatvaretabell','brukerMatvaretabellTabell','/kosthold/matvaretabellen/']";
            String deleteArr = "['deleteMatvare','matvareId','/kosthold/matvaretabellen/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",0);");
            String paramArray = "['matvareNavn']";
            html.addBodyJS("insertRequest('matvaretabellenSubmit','insertMatvaretabell','/kosthold/matvaretabellen/'," + paramArray + "," + tableArr + "," + deleteArr + ",0);");
            html.addBodyJS("attachServerRequestToButton('getDiv','ekstraInnhold','/kosthold/matvaretabellen/','matvaretabellForm')");
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private DivForm getMatvaretabellenForm() throws Exception {
        DivForm form = new DivForm("matvaretabellForm", "div-form");
        form.addElement(new Input("Navn", "Matvare navn", "text", "styrkeLoggInputKilo", "input"));
        form.addElement(new Div("Legg til innhold", "ekstraInnhold", "submit"));
        form.addElement(new Div("Submit", "matvaretabellenSubmit", "submit"));

        form.addElement(customDiv("Kilojoule", 3));
        form.addElement(customDiv("Kilokalorier", 4));
        form.addElement(customDiv("Fett", 5));
        form.addElement(customDiv("Mettet", 6));
        form.addElement(customDiv("Enumettet", 12));
        form.addElement(customDiv("Flerumettet", 15));
        form.addElement(customDiv("Karbohydrat", 28));
        form.addElement(customDiv("Sukker, tilsatt", 31));
        form.addElement(customDiv("Kostfiber", 32));
        form.addElement(customDiv("Protein", 33));
        form.addElement(customDiv("Salt", 34));

        return form;
    }

    private Div customDiv(String navn, int index) throws Exception {
        Select s = new Select("benevningId", "benevninger", "benevningSelect", "select", index, true);
        Input i = new Input(navn, "", "number", "", "input", "0.1");
        Div div = new Div(s.toString() + i.toString(), "inputSelectDiv");
        return div;
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
            if (type.equals("insertMatvaretabell")) {
                out.print(matvaretabellInsert(request.getParameterMap(), brukerId, request.getParameter("matvareNavn")));
            } else if (type.equals("getBrukerMatvaretabell")) {
                out.print(getMatvaretabellTabell(brukerId));
            } else if (type.equals("deleteMatvare")) {
                out.print(deleteMatvare(brukerId, Integer.parseInt(request.getParameter("matvareId"))));
            } else if (type.equals("updateMatvare")) {
                Map<String, String[]> paras = request.getParameterMap();

                out.print(updateMatvare(paras, brukerId, Integer.parseInt(request.getParameter("rowId"))));
                //out.print(request.getParameter("rowData"));
            } else if (type.equals("autocomplete")) {
                out.print(autocomplete(brukerId, request.getParameter("string"), request.getParameter("table")));
            } else if (type.equals("getDiv")) {
                Select s = new Select("benevningId", "benevninger", "benevningSelect", "select");
                Input i = new Input("", "", "number", "", "input", "0.1");
                Div div = new Div(s.toString() + i.toString(), "inputSelectDiv");
                out.print(div.toString());
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    /*private String insertMatvaretabell(Map<String, String[]> map, int brukerId, String matvare) throws Exception {
        String output = "";
        String[] keys = map.keySet().toArray(new String[0]);
        output += Arrays.toString(keys);
        String[] arr = map.get(map);
        return output;
    }*/
    private String autocomplete(int brukerId, String matchingParameter, String whichTable) throws Exception {
        String autocompleteQuery = "";
        if (whichTable.equals("matvaretabellen")) {
            autocompleteQuery = "SELECT matvare,matvareId FROM matvaretabellen WHERE matvare LIKE ? LIMIT 30;";
        } else if (whichTable.equals("næringsinnhold")) {
            autocompleteQuery = "SELECT navn,benevning FROM benevninger WHERE navn LIKE ? LIMIT 15;";
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
        String verifyQuery = "SELECT navn FROM benevninger WHERE";
        Object[] kolonneArray = map.keySet().toArray();
        for (int x = 0; x < kolonneArray.length; x++) {
            if (x != 0) {
                verifyQuery += " OR";
            }
            verifyQuery += " navn LIKE ?";
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
        String brukerDefinertQuery = "SELECT b.navn FROM benevninger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + " AND bm.aktiv = true;";
        String additionalStuff = Database.normalQuery(brukerDefinertQuery).getOneColumnToString("");

        String getLoggQuery = "SELECT matvareId,matvare" + additionalStuff + " FROM matvaretabellen WHERE brukerId = " + brukerId + ";";
        return Database.normalQuery(getLoggQuery).getJSON();
    }

    private int matvaretabellInsert(Map<String, String[]> map, int brukerId, String matvare) throws Exception {
        String verifyQuery = "SELECT navn FROM benevninger WHERE";
        Object[] kolonneArray = map.get("innhold");
        for (int x = 0; x < kolonneArray.length; x++) {
            if (x != 0) {
                verifyQuery += " OR";
            }
            verifyQuery += " benevningId = ?";
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
            String decimal = mapData[3][i];
            /* gjør empty string til 0 */
            if (decimal.length() > 0) {
                vars[i + 2] = Double.parseDouble(decimal);
            } else {
                vars[i + 2] = new Double(0);
            }

        }
        columnString += ") ";
        valueString += ");";
        //return columnString+valueString;
        return Database.singleUpdateQuery(columnString + valueString, vars, false);
    }
}
