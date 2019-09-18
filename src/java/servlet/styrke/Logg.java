/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.styrke;

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
import util.sql.Database;
import util.http.Headers;

public class Logg extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            int brukerId = (int) req.getSession().getAttribute("brukerId");
            StandardHtml html = new StandardHtml("Styrke Logg");
            Form form = getKondisjonLoggForm(brukerId);
            Div div = new Div("", "styrkeLoggTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getStyrkeLogg','styrkeLoggTabell','/styrke/logg/']";
            String deleteArr = "['deleteStyrkeLogg','styrkeLoggId','/styrke/logg/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",2);");
            String paramArray = "['styrkeId','vekt','reps']";
            html.addBodyJS("insertRequest('styrkeLoggSubmit','insertStyrkeLogg','/styrke/logg/'," + paramArray + "," + tableArr + "," + deleteArr + ",7);");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private Form getKondisjonLoggForm(int brukerId) throws Exception {
        Form form = new Form("styrkeLoggForm", "div-form");
        form.addElement(new Select("styrkeId", "styrkeØvelse", brukerId, "styrkeLoggSelect", "select"));
        form.addElement(new Input("kilo", "kilo", "number", "styrkeLoggInputKilo", "input", "0.1"));
        form.addElement(new Input("reps", "reps", "number", "styrkeLoggInputReps", "input"));
        form.addElement(new Div("submit", "styrkeLoggSubmit", "submit"));
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
            if (type.equals("getStyrkeLogg")) {
                out.print(getLogg(brukerId, Integer.parseInt(request.getParameter("interval"))));
            } else if (type.equals("insertStyrkeLogg")) {
                out.print(insertStyrkeLogg(brukerId,
                        Integer.parseInt(request.getParameter("styrkeId")),
                        Double.parseDouble(request.getParameter("vekt")),
                        Integer.parseInt(request.getParameter("reps"))
                ));
            } else if (type.equals("deleteStyrkeLogg")) {
                out.print(deleteStyrkeLogg(brukerId, Integer.parseInt(request.getParameter("styrkeLoggId"))));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int deleteStyrkeLogg(int brukerId, int styrkeLoggId) throws Exception {
        String query = "DELETE FROM styrkeLogg WHERE brukerId = ? AND styrkeLoggId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, styrkeLoggId}, false);
    }

    private String getLogg(int brukerId, int interval) throws Exception {
        String query = "SELECT styrkeLoggId,s.navn,reps,vekt,DATE_FORMAT(dato,'%d.%m.%y') as dato FROM styrkeLogg "
                + "LEFT JOIN styrkeØvelse s ON s.styrkeId = styrkeLogg.styrkeId"
                + " WHERE s.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL ? DAY)"
                + " ORDER BY styrkeLoggId DESC;";
        return Database.multiQuery(query, new Object[]{interval}).getJSON();
    }

    private int insertStyrkeLogg(int brukerId, int styrkeId, double vekt, int reps) throws Exception {
        Object[] vars = {styrkeId, vekt, reps};
        String query = "INSERT INTO styrkeLogg(dato,styrkeId,vekt,reps,brukerId) VALUES (CURDATE(),?,?,?," + brukerId + ")";
        return Database.singleUpdateQuery(query, vars, false);
    }

}
