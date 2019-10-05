/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import html.Div;
import html.StandardHtml;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.Headers;

/**
 *
 * @author
 */
public class Historikk extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Kosthold Historikk");

            Div div = new Div("", "kostholdHistorikkTabell", "div-table");
            Div containerDiv = new Div(div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());

            String tableArr = "['getLoggTabell','kostholdHistorikkTabell','/kosthold/logg/']";
            String deleteArr = "['deleteLogg','loggId','/kosthold/logg/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + "," + 31 + ");");
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
