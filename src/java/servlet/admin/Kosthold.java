/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.admin;

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

/**
 *
 * @author Tarald
 */
public class Kosthold extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Admin Kosthold");
            DivForm form = getBenevningForm();
            Div div = new Div("", "benevningTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            String tableArr = "['hentBenevning','benevningTabell','/admin/kosthold/']";
            String deleteArr = "['slettBenevning','benevningId','/admin/kosthold/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ");");
            String paramArray = "['benevningId','min','maks','aktiv']";
            html.addBodyJS("insertRequest('benevningSubmit','nyBenevning','/admin/kosthold/'," + paramArray + "," + tableArr + "," + deleteArr + ");");
            html.addBodyContent(containerDiv.toString());
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private DivForm getBenevningForm() throws Exception {
        DivForm form = new DivForm("benevningForm", "div-form");
        form.addElement(new Select("benevningId", "benevninger", "benevningSelect", "select"));
        form.addElement(new Input("Fra verdi", "minimum", "number", "benevningMax", "input"));
        form.addElement(new Input("Til verdi", "maksimum", "number", "benevningMin", "input"));
        form.addElement(new Div("Submit", "benevningSubmit", "submit"));
        return form;
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
            if (type.equals("nyBenevning")) {
                String maks = request.getParameter("maks");
                String min = request.getParameter("min");
                String id = request.getParameter(("benevningId"));
                out.print(nyBenevning(brukerId, Integer.parseInt(id), Integer.parseInt(maks), Integer.parseInt(min)));
            } else if (type.equals("endreBenevning")) {
                //String[][] arr = ParameterMapConverter.dynamiskConverter(request.getParameterMap(), 1, 4);

                //out.print(Arrays.deepToString(arr));
                //out.print(endreBenevningMål(request.getParameterMap(), brukerId));
            } else if (type.equals("slettBenevning")) {
                out.print(slettBenevning(brukerId, Integer.parseInt(request.getParameter("benevningId"))));
            } else if (type.equals("hentBenevning")) {
                out.print(hentBenevning(brukerId));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int slettBenevning(int brukerId, int benevningId) throws Exception {
        String query = "DELETE FROM brukerBenevningMål WHERE brukerId = " + brukerId + " AND benevningId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{benevningId}, false);
    }

    private int nyBenevning(int brukerId, int benevningId, int maks, int min) throws Exception {
        String query = "INSERT INTO brukerBenevningMål(benevningId,brukerId,aktiv,øvreMål,nedreMål)"
                + " VALUES(?," + brukerId + ",?,?,?);";
        return Database.singleUpdateQuery(query, new Object[]{benevningId, true, maks, min}, false);
    }

    private String hentBenevning(int brukerId) throws Exception {
        String query = "SELECT bm.benevningId,be.navn,bm.nedreMål as min,bm.øvreMål as maks, aktiv FROM brukerBenevningMål bm"
                + " LEFT JOIN benevninger be ON be.benevningId = bm.benevningId"
                + " WHERE bm.brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    private int endreBenevningMål(Map<String, String[]> map, int brukerId) throws Exception {
        String[][] arr = map.values().toArray(new String[0][0]);
        /* arr: [[type][id,id...][øvre,øvre...][nedre,nedre...][aktiv,aktiv...]]*/
        String query = "UPDATE brukerBenevningMål SET"
                + " aktiv = ?, øvreMål = ?, nedreMål = ?"
                + " WHERE brukerId =" + brukerId + " AND benevningId = ?" + ";";

        return Database.innstillingerMultipleUpdateQueries(query, arr, 1);
        //return Arrays.deepToString(arr);
    }

}
