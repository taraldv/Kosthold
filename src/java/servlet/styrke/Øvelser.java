/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.styrke;

import crypto.ValidSession;
import html.Div;
import html.DivForm;
import html.Input;
import html.StandardHtml;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.Headers;

public class Øvelser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Styrke Øvelser");
            DivForm form = getStyrkeØvelserForm();
            Div div = new Div("", "styrkeØvelserTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getØvelser','styrkeØvelserTabell','/styrke/øvelser/']";
            String deleteArr = "['deleteStyrkeØvelser','styrkeId','/styrke/øvelser/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ");");
            String paramArray = "['navn']";
            html.addBodyJS("insertRequest('styrkeØvelseSubmit','insertStyrkeØvelser','/styrke/øvelser/'," + paramArray + "," + tableArr + "," + deleteArr + ");");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private DivForm getStyrkeØvelserForm() {
        DivForm form = new DivForm("styrkeØvelseForm", "div-form");
        form.addElement(new Input("Øvelse navn", "øvelse navn", "text", "styrkeØvelseInputNavn", "input"));
        form.addElement(new Div("Submit", "styrkeØvelseSubmit", "submit"));
        return form;
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
            if (type.equals("insertStyrkeØvelser")) {
                out.print(insertStyrkeØvelser(brukerId, request.getParameter("navn")));
            } else if (type.equals("updateStyrkeØvelser")) {
                out.print(updateStyrkeØvelser(brukerId, Integer.parseInt(request.getParameter("rowId")), request.getParameter("navn")));
            } else if (type.equals("deleteStyrkeØvelser")) {
                out.print(deleteStyrkeØvelser(brukerId, Integer.parseInt(request.getParameter("styrkeId"))));
            } else if (type.equals("getØvelser")) {
                out.print(getØvelser(brukerId));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int deleteStyrkeØvelser(int brukerId, int styrkeId) throws Exception {
        String query = "DELETE FROM styrkeØvelse WHERE brukerId = ? AND styrkeId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, styrkeId}, false);
    }

    private int updateStyrkeØvelser(int brukerId, int styrkeId, String navn) throws Exception {
        String query = "UPDATE styrkeØvelse SET navn = ? WHERE styrkeId = ? AND brukerId = " + brukerId + ";";
        return Database.singleUpdateQuery(query, new Object[]{navn, styrkeId}, false);
    }

    private int insertStyrkeØvelser(int brukerId, String navn) throws Exception {
        String query = "INSERT INTO styrkeØvelse (brukerId,navn) VALUES (" + brukerId + ",?);";
        return Database.singleUpdateQuery(query, new Object[]{navn}, false);
    }

    private String getØvelser(int brukerId) throws Exception {
        String query = "SELECT styrkeId,navn FROM styrkeØvelse WHERE brukerId = " + brukerId + " ORDER BY navn;";
        return Database.normalQuery(query).getJSON();
    }
}
