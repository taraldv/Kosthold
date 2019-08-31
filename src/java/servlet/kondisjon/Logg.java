/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kondisjon;

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
import util.http.Headers;
import util.sql.Database;

/**
 *
 * @author
 */
public class Logg extends HttpServlet {

    // private final String 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            int brukerId = (int) req.getSession().getAttribute("brukerId");
            StandardHtml html = new StandardHtml("Kondisjon Logg");
            Form form = getKondisjonLoggForm(brukerId);
            Div div = new Div("", "kondisjonLoggTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getKondisjonLogg','kondisjonLoggTabell','/kondisjon/logg/']";
            String deleteArr = "['deleteKondisjonLogg','kondisjonLoggId','/kondisjon/logg/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",7);");
            String paramArray = "['kondisjonTurerId','tidMinutter','tidSekunder']";
            html.addBodyJS("insertRequest('kondisjonLoggSubmit','insertKondisjonLogg','/kondisjon/logg/'," + paramArray + "," + tableArr + "," + deleteArr + ",7);");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private Form getKondisjonLoggForm(int brukerId) throws Exception {
        Form form = new Form("kondisjonLoggForm", "div-form");
        form.addElement(new Select("kondisjonTurerId", "kondisjonTurer", brukerId, "kondisjonLoggSelect", "select"));
        form.addElement(new Input("minutter", "minutter", "number", "kondisjonLoggInputMinutter", "input"));
        form.addElement(new Input("sekunder", "sekunder", "number", "kondisjonLoggInputSekunder", "input"));
        form.addElement(new Div("submit", "kondisjonLoggSubmit", "submit"));
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
            if (type.equals("getKondisjonLogg")) {
                out.print(getKondisjonLogg(brukerId, Integer.parseInt(request.getParameter("interval"))));
            } else if (type.equals("insertKondisjonLogg")) {
                int tidSekunder = Integer.parseInt(request.getParameter("tidSekunder"));
                int tidMinutter = Integer.parseInt(request.getParameter("tidMinutter"));
                out.print(insertKondisjonLogg(brukerId,
                        tidSekunder + (tidMinutter * 60),
                        Integer.parseInt(request.getParameter("kondisjonTurerId"))));
            } else if (type.equals("deleteKondisjonLogg")) {
                out.print(deleteKondisjonLogg(brukerId, Integer.parseInt(request.getParameter("kondisjonLoggId"))));
            } else if (type.equals("updateKondisjonLogg")) {
                int loggId = Integer.parseInt(request.getParameter("rowId"));
                int tidSekunder = Integer.parseInt(request.getParameter("tidSekunder"));
                int tidMinutter = Integer.parseInt(request.getParameter("tidMinutter"));
                String dato = request.getParameter("dato");
                out.print(updateKondisjonLogg(brukerId, loggId, tidSekunder + (tidMinutter * 60), dato));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateKondisjonLogg(int brukerId, int kondisjonLoggId, int tidSekunder, String dato) throws Exception {
        String updateQuery = "UPDATE kondisjonLogg SET tidSekunder = ?, dato = ? WHERE kondisjonLoggId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {tidSekunder, dato, kondisjonLoggId};
        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteKondisjonLogg(int brukerId, int kondisjonLoggId) throws Exception {
        String query = "DELETE FROM kondisjonLogg WHERE brukerId = ? AND kondisjonLoggId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, kondisjonLoggId}, false);
    }

    private String getKondisjonLogg(int brukerId, int interval) throws Exception {
        String query = "SELECT l.kondisjonLoggId,t.navn,DATE_FORMAT(l.dato,'%d.%m.%y') as dato,"
                + "l.tidSekunder DIV 60 as minutter,"
                + "l.tidSekunder%60 as sekunder "
                + "FROM kondisjonLogg l "
                + "LEFT JOIN kondisjonTurer t ON t.kondisjonTurerId = l.kondisjonTurerId"
                + " WHERE l.brukerId = " + brukerId + " AND l.dato <= curdate() AND l.dato > DATE_SUB(curdate(),INTERVAL ? DAY)"
                + " ORDER BY l.dato DESC;";
        return Database.multiQuery(query, new Object[]{interval}).getJSON();
    }

    private int insertKondisjonLogg(int brukerId, int tidSekunder, int kondisjonTurerId) throws Exception {
        Object[] vars = {kondisjonTurerId, tidSekunder};
        String query = "INSERT INTO kondisjonLogg(dato,kondisjonTurerId,tidSekunder,brukerId) VALUES (CURDATE(),?,?," + brukerId + ")";
        return Database.singleUpdateQuery(query, vars, false);
    }
}
