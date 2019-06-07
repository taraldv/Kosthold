/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.statistikk;

import crypto.ValidSession;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.http.Headers;

/**
 *
 * @author
 */
public class Styrke extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Statistikk Styrke");
        html.addStandard();
        html.addJS("../../js/statistikk.js");
        html.addJS("../../js/statistikkStyrke.js");
        resp.getWriter().print(html.toString());

    }
}
