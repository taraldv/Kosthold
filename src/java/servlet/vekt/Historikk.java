/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.vekt;

import crypto.ValidSession;
import html.Div;
import html.Form;
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
            StandardHtml html = new StandardHtml("Kondisjon Logg");
            Div div = new Div("", "vektLoggTabell", "div-table");
            Div containerDiv = new Div(div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            String tableArr = "['getVektLogg','vektLoggTabell','/vekt/logg/']";
            String deleteArr = "['deleteVekt','vektId','/vekt/logg/']";
            html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",100);");
            //Form.get(brukerId));
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }
}
