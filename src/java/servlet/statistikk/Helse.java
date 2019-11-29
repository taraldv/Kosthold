/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.statistikk;

import crypto.ValidSession;
import html.Div;
import html.DivForm;
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
public class Helse extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Statistikk Helse");
            html.addBodyJS("<script type='text/javascript' src='../../js/Graf.js'></script>");
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.POST(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        String type = req.getParameter("type");
        try {
            int brukerId = (int) req.getSession().getAttribute("brukerId");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
