/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.vekt;

import crypto.ValidSession;
import html.Div;
import html.Form;
import html.Input;
import html.Select;
import html.StandardHtml;
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
public class Logg extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Kondisjon Logg");
            Form form = getKondisjonLoggForm();
            Div div = new Div("", "vektLoggTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getVektLogg','vektLoggTabell','/vekt/logg/']";
            String deleteArr = "['deleteVekt','vektId','/vekt/logg/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",31);");
            String paramArray = "['kilo']";
            html.addBodyJS("insertRequest('vektLoggSubmit','insertVekt','/vekt/logg/'," + paramArray + "," + tableArr + "," + deleteArr + ",7);");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
    
      private Form getKondisjonLoggForm()  {
        Form form = new Form("kondisjonLoggForm", "div-form");
        form.addElement(new Input("kilo", "kilo", "number", "vektLoggInputKilo", "input"));
        form.addElement(new Div("submit", "vektLoggSubmit", "submit"));
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
            if (type.equals("getVektLogg")) {
                out.print(getVektLogg(brukerId, Integer.parseInt(request.getParameter("interval"))));
            } else if (type.equals("insertVekt")) {
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                out.print(insertVektLogg(kiloVekt, brukerId));
            } else if (type.equals("deleteVekt")) {
                int vektId = Integer.parseInt(request.getParameter("vektId"));
                out.print(deleteVekt(vektId, brukerId));
            } else if (type.equals("updateVekt")) {
                int vektId = Integer.parseInt(request.getParameter("rowId"));
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                String dato = request.getParameter("dato");
                out.print(updateVekt(vektId, brukerId, kiloVekt, dato));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateVekt(Integer vektId, int brukerId, Double kiloVekt, String dato) throws Exception {
        String updateQuery = "UPDATE vekt SET kilo = ?, dato = ? WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {kiloVekt, dato, vektId};
        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteVekt(int vektId, int brukerId) throws Exception {
        String deleteQuery = "DELETE FROM vekt WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {vektId};
        return Database.singleUpdateQuery(deleteQuery, vars, false);
    }

    private String getVektLogg(int brukerId, int interval) throws Exception {
        String query = "SELECT vektId,DATE_FORMAT(v.dato,'%d.%m.%y') as dato,kilo FROM vekt v "
                + " WHERE brukerId = " + brukerId + " AND v.dato <= curdate() AND v.dato > DATE_SUB(curdate(),INTERVAL ? DAY)"
                + " ORDER BY v.dato DESC;";
        return Database.multiQuery(query, new Object[]{interval}).getJSON();
    }

    private int insertVektLogg(Double kiloVekt, int brukerId) throws Exception {
        String insertQuery = "INSERT INTO vekt(dato,kilo,brukerId) VALUES (CURDATE(),?," + brukerId + ");";
        Object[] vars = {kiloVekt};
        return Database.singleUpdateQuery(insertQuery, vars, false);
    }
}
