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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.http.Headers;
import util.sql.Database;

/**
 *
 * @author
 */
public class Turer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Kondisjon Turer");
            Form form = getKondisjonTurForm();
            Div div = new Div("", "kondisjonTurTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getKondisjonTur','kondisjonTurTabell','/kondisjon/turer/']";
            String deleteArr = "['deleteKondisjonTur','kondisjonTurerId','/kondisjon/turer/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ");");
            String paramArray = "['navn','km','mohStart','mohSlutt']";
            html.addBodyJS("insertRequest('kondisjonTurSubmit','insertKondisjonTur','/kondisjon/turer/'," + paramArray + "," + tableArr + "," + deleteArr + ");");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private Form getKondisjonTurForm() {
        Form form = new Form("kondisjonTurForm", "div-form");
        // new Input(placeholder, label, inputType, elementId, elementClass)
        form.addElement(new Input("tur navn", "tur navn", "text", "kondisjonTurInputNavn", "input"));
        form.addElement(new Input("kilometer", "kilometer", "number", "kondisjonTurInputKm", "input"));
        form.addElement(new Input("moh start", "moh start", "number", "kondisjonTurInputMohStart", "input"));
        form.addElement(new Input("moh slutt", "moh slutt", "number", "kondisjonTurInputMohSlutt", "input"));
        form.addElement(new Div("submit", "kondisjonTurSubmit", "submit"));
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
            if (type.equals("getKondisjonTur")) {
                out.print(getKondisjonTur(brukerId));
            } else if (type.equals("insertKondisjonTur")) {
                out.print(insertKondisjonTur(brukerId,
                        request.getParameter("navn"),
                        Double.parseDouble(request.getParameter("km")),
                        Integer.parseInt(request.getParameter("mohStart")),
                        Integer.parseInt(request.getParameter("mohSlutt"))));
            } else if (type.equals("deleteKondisjonTur")) {
                out.print(deleteKondisjonTur(brukerId, Integer.parseInt(request.getParameter("kondisjonTurerId"))));
            } else if (type.equals("updateKondisjonTur")) {
                out.print(updateKondisjonTur(brukerId,
                        Integer.parseInt(request.getParameter("rowId")),
                        Double.parseDouble(request.getParameter("km")),
                        Integer.parseInt(request.getParameter("mohStart")),
                        Integer.parseInt(request.getParameter("mohSlutt")),
                        request.getParameter("navn")));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateKondisjonTur(int brukerId, int kondisjonTurerId, double km, int mohStart, int mohSlutt, String navn) throws Exception {
        String query = "UPDATE kondisjonTurer SET navn = ?, km = ?, mohStart = ?, mohSlutt = ? WHERE kondisjonTurerId = ? AND brukerId = " + brukerId + ";";
        return Database.singleUpdateQuery(query, new Object[]{navn, km, mohStart, mohSlutt, kondisjonTurerId}, false);
    }

    //brukerId brukt på annen måte her? må bli mer konsistens
    private int deleteKondisjonTur(int brukerId, int kondisjonTurerId) throws Exception {
        String query = "DELETE FROM kondisjonTurer WHERE brukerId = ? AND kondisjonTurerId = ?;";
        return Database.singleUpdateQuery(query, new Object[]{brukerId, kondisjonTurerId}, false);
    }

    private String getKondisjonTur(int brukerId) throws Exception {
        String query = "SELECT kondisjonTurerId,navn,km,mohStart,mohSlutt FROM kondisjonTurer "
                + " WHERE brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    private int insertKondisjonTur(int brukerId, String navn, double km, int mohStart, int mohSlutt) throws Exception {
        Object[] vars = {navn, km, mohStart, mohSlutt};
        String query = "INSERT INTO kondisjonTurer (brukerId,navn,km,mohStart,mohSlutt) "
                + "VALUES (" + brukerId + ",?,?,?,?);";
        return Database.singleUpdateQuery(query, vars, false);
    }
}
